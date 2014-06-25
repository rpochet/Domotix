Config = require "config"
SerialModem = require "../swap/serialmodem" if not Config.serial.dummy
SerialModem = require "../swap/dummyserialmodem" if Config.serial.dummy
Publisher = require "../swap/pubsub"
UdpBridge = require "../swap/udpbridge"
swap = require "../../client/code/common/swap"
cradle = require "cradle"
ss = require "socketstream"
util = require "util"
eventEmitter = require("events").EventEmitter
logger = require("log4js").getLogger(__filename.split("/").pop(-1).split(".")[0])

dbPanstamp = new(cradle.Connection)(Config.couchDB.host, Config.couchDB.port).database("panstamp")
dbPanstampPackets = new(cradle.Connection)(Config.couchDB.host, Config.couchDB.port).database("panstamp_packets")

developers = []
devicesConfig = {}
devices = {}
swapPackets = []
swapEvents = []
publisher = undefined

initDevicesConfig = () ->
    developers = []
    devicesConfig = {}
    dbPanstamp.get "devices", (err, doc) ->
        return logger.error err if err?
        developers = doc.developers
        logger.info "Got #{developers.length} developers"
        for developer in developers
            do (developer) ->
                logger.info "Got #{developer.devices.length} devices for developer #{developer.name}"
                for device in developer.devices
                    do (device) ->
                        dbPanstamp.get developer.id + device.id, (err, doc) ->
                            return logger.error err if err?
                            device = doc
                            devicesConfig[device._id] = device
                            logger.info "Got device #{device.product} for developer #{developer.name}"
initDevicesConfig()

initDevices = () ->
    devices = {}
    dbPanstamp.view "devices/devices", { }, (err, doc) ->
        for docDevices in doc
            devices["DEV" + swap.num2byte(docDevices.value.address)] = docDevices.value
        ss.api.publish.all "devicesUpdated"
initDevices()

initSwapPackets = () ->
    swapPackets = []
    swapEvents = []
    options = 
        include_docs: true
        limit: 40
        descending: true
    
    dbPanstampPackets.all options, (err, res) ->
        logger.error err if err?
        return if err?
        for row in res.rows
            delete row.doc._id
            delete row.doc._rev
            swapPackets.splice 0, 0, row.doc
        ss.api.publish.all "swapPacket"
initSwapPackets()

addSwapEvent = (swapEvent) ->
    ss.api.publish.all "swapEvent", swapEvent
    swapEvents.splice 0, 0, swapEvent
    swapEvents.pop() if swapEvents.length > 80

addSwapPacket = (swapPacket) ->
    dbPanstampPackets.save swapPacket.time.time, swapPacket, (err, doc) ->
        logger.error err if err?
    ss.api.publish.all "swapPacket", swapPacket
    swapPackets.splice 0, 0, swapPacket
    swapPackets.pop() if swapPackets.length > 80

serial = new SerialModem Config.serial
serial.on "started", () ->
    publisher = new Publisher Config.broker
    udpBridge = new UdpBridge Config.udpBridge
    
    serial.on "swapPacket", (swapPacket) ->
        addSwapPacket swapPacket
        swapPacketReceived swapPacket
    
    udpBridge.on "swapPacket", (swapPacket) ->
        logger.info "Packet received from UDP Bridge"
        addSwapPacket swapPacket
        
        if swapPacket.func is swap.Functions.QUERY
            serial.send swapPacket
        else if swapPacket.func is swap.Functions.COMMAND
            serial.send swapPacket
        else # swap.Functions.STATUS
            logger.warn "Status Packet received from UDP Bridge, not allowed"
        
# Only for dummy serial modem
serial.start() if Config.serial.dummy

# Function to call when a packet is received
swapPacketReceived = (swapPacket) ->
    # Add device if not already seen
    packetDevice = undefined
    
    if ("DEV" + swap.num2byte(swapPacket.source.toString())) not of devices
        text =  "Packet received from unknown source: #{swapPacket.source}"
        logger.warn text
        addSwapEvent {name: "unknownSwapPacketDevice", text:text, type:"warning", time: new Date()}
    else
        packetDevice = devices["DEV" + swap.num2byte(swapPacket.source)]
        #logger.debug packetDevice
    
    # Handles STATUS packets
    if swapPacket.func is swap.Functions.STATUS
        value = swapPacket.value
        
        if swapPacket.regId is swap.Registers.productCode.id
            # First time this packetDevice talks
            return if packetDevice
            
            productCode = (swap.num2byte(v) for v in swapPacket.value).join("")
            
            if not devicesConfig[productCode]?
                text = "Unknown device or manufacturer Id detected: #{value}"
                logger.warn text
                addSwapEvent {name:"unknownDevice", text:text, type:"warning", time: new Date()}
                return
            
            packetDevice = devicesConfig[productCode]
            packetDevice.address = swapPacket.source
            packetDevice.networkId = Config.network.networkId
            packetDevice.frequencyChannel = Config.network.frequencyChannel
            packetDevice.productCode = productCode
            packetDevice.securityNonce = swapPacket.nonce
            packetDevice.lastStatusTime = swapPacket.time
            delete packetDevice._id
            delete packetDevice._rev
            
            dbPanstamp.save "DEV" + swap.num2byte(packetDevice.address), packetDevice, (err, doc) ->
                return logger.error err if err?
                devices[doc._id] = packetDevice
                text = "New packetDevice #{packetDevice.address} added: #{packetDevice.productCode} - #{devicesConfig[productCode].product} (#{devicesConfig[productCode].developer})"
                logger.info text
                addSwapEvent {name:"newSwapPacketDeviceDetected", text:text, packetDevice:packetDevice, time:new Date()}
        
        logger.warn "Packet received from unknown source: #{swapPacket.source} and not a productCode status packet" if not packetDevice 
        return if not packetDevice 
        
        # handles missing packets ??
        if not Math.abs(packetDevice.securityNonce - swapPacket.nonce) in [1,255]
            text = "(#{packetDevice.location}): Missing nonce: got #{swapPacket.nonce} - expected #{packetDevice.securityNonce}"
            logger.warn text
            addSwapEvent {name:"missingNonce", text:text, packetDevice:packetDevice, type:"warning", time: new Date()}
        
        packetDevice.securityNonce = swapPacket.nonce
        packetDevice.lastStatusTime = swapPacket.time
        
        if swapPacket.regId is swap.Registers.hardwareVersion.id
            packetDevice.version.hardware = (swap.num2byte(v) for v in value).join("")
            logger.info "(#{packetDevice.location}): Hardware version changed: #{packetDevice.version.hardware}"
        
        else if swapPacket.regId is swap.Registers.firmwareVersion.id
            packetDevice.version.firmware = (swap.num2byte(v) for v in value).join("")
            logger.info "(#{packetDevice.location}): Firmware version changed: #{packetDevice.version.firmware}"
        
        else if swapPacket.regId is swap.Registers.state.id
            packetDevice.systemState = swap.SwapStates.get value
            text = "(#{packetDevice.location}): State changed to #{packetDevice.systemState.str}"
            logger.info text
            addSwapEvent {name:"systemState", text:text, packetDevice: packetDevice, time: new Date() }
            packetDevice.systemState = packetDevice.systemState.level
        
        else if swapPacket.regId is swap.Registers.channel.id
            packetDevice.frequencyChannel = value[0]
            text = "(#{packetDevice.location}): Channel changed to #{packetDevice.frequencyChannel}"
            logger.warn text
            addSwapEvent {name:"frequencyChannel", text:text, packetDevice:packetDevice, time: new Date()}
        
        else if swapPacket.regId is swap.Registers.security.id
            packetDevice.securityOption = value[0]
            text = "(#{packetDevice.location}): Security changed to #{packetDevice.securityOption}"
            logger.info text
            addSwapEvent {name:"securityOption", text:text, packetDevice:packetDevice, time: new Date()}
        
        else if swapPacket.regId is swap.Registers.password.id
            packetDevice.securityPassword = (swap.num2byte(v) for v in value).join("")
            text = "(#{packetDevice.location}): Password changed"
            logger.info text
            addSwapEvent {name:"securityPassword", text:text, packetDevice:packetDevice, time: new Date()}
        
        else if swapPacket.regId is swap.Registers.nonce.id
            packetDevice.securityNonce = value[0]
            text = "(#{packetDevice.location}): Nonce received to #{packetDevice.securityNonce}"
            logger.debug text
            addSwapEvent {name:"securityNonce", text:text, packetDevice:packetDevice, time: new Date()}
        
        else if swapPacket.regId is swap.Registers.network.id
            value = 256 * value[0] + value[1]
            packetDevice.networkId = value
            text = "(#{packetDevice.location}): Network changed to #{value}"
            logger.warn text
            addSwapEvent {name:"network", text:text, packetDevice:packetDevice, time: new Date()}
        
        else if swapPacket.regId is swap.Registers.address.id
            newAddress = value[0]
            oldAddress = packetDevice.source
            
            dbPanstamp.save "DEV" + swap.num2byte(newAddress), packetDevice, (err, doc) ->
                return logger.error err if err?
                devices["DEV" + swap.num2byte(newAddress)] = packetDevice
                text = "(#{packetDevice.location}): Address changed from #{oldAddress} to #{newAddress}"
                logger.info text
                addSwapEvent {name:"address", text: text, packetDevice: packetDevice, oldAddress:oldAddress, time: new Date()}
                
                cid = "DEV" + swap.num2byte(oldAddress)
                dbPanstamp.remove cid, devices[cid]._rev, (err, res) ->
                    return logger.error err if err?
                    delete devices[cid]
                    return true
        
        else if swapPacket.regId is swap.Registers.txInterval.id
            value = 256 * value[0] + value[1]
            text = "(#{packetDevice.location}): Transmit interval changed to #{value}"
            logger.info text
            packetDevice.txInterval = value;
            addSwapEvent {name: "txInterval", text: text, packetDevice: packetDevice, time: new Date()}
        
        # Retrieve value from endpoints definition 
        else
            foundRegisters = (register for register in packetDevice.configRegisters when register.id == swapPacket.regId)
            if foundRegisters.length == 1
                foundRegister = foundRegisters[0]
                updateParamsValue foundRegister, swapPacket
                text = "(#{packetDevice.location}): Register #{foundRegister.name} changed to #{value}"
                logger.info text
                addSwapEvent {name: "REG-" + swapPacket.regId, text: text, packetDevice: packetDevice, time: new Date()}
            else
                foundRegisters = (register for register in packetDevice.regularRegisters when register.id == swapPacket.regId)
                if foundRegisters.length == 1
                    foundRegister = foundRegisters[0]
                    updateEndpointsValue foundRegister, swapPacket
                    text = "(#{packetDevice.location}): Register #{foundRegister.name} changed to #{value}"
                    logger.info text
                    addSwapEvent {name: "REG-" + swapPacket.regId, text: text, packetDevice: packetDevice, time: new Date()}
                else
                    text = "(#{packetDevice.location}): Unknown register #{swapPacket.regId}"
                    logger.info text
                    addSwapEvent {name: "REG-" + swapPacket.regId, text: text, packetDevice: packetDevice, time: new Date()}
                    return
        
        dbPanstamp.save "DEV" + swap.num2byte(packetDevice.address), packetDevice._rev, packetDevice, (err, res) ->
            return logger.error err if err?
            devices["DEV" + swap.num2byte(packetDevice.address)]._rev = res.rev
            ss.api.publish.all "devicesUpdated"
            
        publisher.publish JSON.stringify
            swapPacket : swapPacket
            packetDevice : packetDevice
        
    else if swapPacketfunc is swap.Functions.QUERY
        logger.info "Query request received from #{swapPacketsource} for packetDevice #{swapPacketdest} register #{swapPacket.regId}" 
    else if swapPacketfunc is swap.Functions.COMMAND
        logger.info "Command request received from #{swapPacketsource} for packetDevice #{swapPacket.dest} register #{swapPacket.regId} with value #{swapPacket.value}"
    else
        logger.error "Received packet does not contain a valid function: #{swapPacket.func}"

updateEndpointsValue = (register, swapPacket) ->
    for endpoint in register.endpoints
        do (endpoint) ->
            if swapPacket.value isnt undefined
                temp = if swapPacket.value.length is undefined then [swapPacket.value] else swapPacket.value
                endpoint.value = temp.slice(parseInt(endpoint.position), (parseInt(endpoint.position) + parseInt(endpoint.size)))
                if endpoint.type is "number"
                    value = 0
                    value += v * Math.pow(256, endpoint.value.length - 1 - i) for v, i in endpoint.value
                    endpoint.value = value

updateParamsValue = (register, swapPacket) ->
    for param in register.params
        do (param) ->
            if swapPacket.value isnt undefined
                temp = if swapPacket.value.length is undefined then [swapPacket.value] else swapPacket.value
                param.value = temp.slice(parseInt(param.position), (parseInt(param.position) + parseInt(param.size)))
                if param.type is "number"
                    value = 0
                    value += v * Math.pow(256, param.value.length - 1 - i) for v, i in param.value
                    param.value = value

onUpdateDevice = (newDevice) ->
    cid = "DEV" + swap.num2byte newDevice.address
    oldDevice = devices[cid]

exports.actions = (req, res, ss) ->

    # Easily debug incoming requests here
    #console.log(req)

    getConfig: ->
        res Config
    
    updateConfig: (Config) ->
        @Config = Config
        res Config
    
    getDevices: ->
        res devices
    
    getSwapPackets: () ->
        res swapPackets
    
    getSwapEvents: () ->
        res swapEvents
    
    refreshDevices: ->
        initDevicesConfig()
        initDevices()
        res true
    
    refreshSwapPackets: ->
        initSwapPackets()
        res true
    
    updateDevice: (device) ->
        # TODO: handle device update...
        onUpdateDevice device
        cid = "DEV" + swap.num2byte device.address
        dbPanstamp.save cid, devices[cid]._rev, device, (err, res) ->
            return logger.error err if err?
            device._rev = res.rev
            devices[cid] = device
            return true
        res true
    
    deleteDevice: (address) ->
        cid = "DEV" + swap.num2byte address
        dbPanstamp.remove cid, devices[cid]._rev, (err, res) ->
            return logger.error err if err?
            delete devices[cid]
            return true
        res true
    
    # Gets the value of a specific register
    sendQuery: (address, registerId) ->
        sp = new swap.SwapPacket()
        sp.source = Config.network.address
        sp.dest = address
        sp.func = swap.Functions.QUERY
        sp.regAddress = address
        sp.regId = registerId
        serial.send(sp)

    # Sets the value of a specific register
    sendCommand: (address, registerId, value) ->
        sp = new swap.SwapPacket()
        sp.source = Config.network.address
        sp.dest = address
        sp.func = swap.Functions.COMMAND
        sp.regAddress = address
        sp.regId = registerId
        sp.value = value
        serial.send(sp)
    
    checkNewDevices: () ->
        sendQuery address, swap.Registers.productCode.id
    
    onSerial: (str) ->
        data = "(0000)" + str
        swapPacket = new swap.CCPacket data
        swapPacket = new swap.SwapPacket swapPacket
        
        logger.info "OnSerial:"
        logger.info swapPacket
        
        swapPacketReceived swapPacket
        
        ss.publish.all "swapPacket", swapPacket
        swapPackets.splice(0, 0, swapPacket)
        swapPackets.pop() if swapPackets.length > 40
