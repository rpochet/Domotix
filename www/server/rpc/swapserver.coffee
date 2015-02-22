Config = require "config"
SerialModem = require "../swap/serialmodem" if not Config.serial.dummy
SerialModem = require "../swap/dummyserialmodem" if Config.serial.dummy
Publisher = require "../swap/pubsub"
UdpBridge = require "../swap/udpbridge"
swap = require "../../client/code/common/swap"
cradle = require "cradle"
ss = require "socketstream"
util = require "util"
sleep = require "sleep"
eventEmitter = require("events").EventEmitter
moment = require "moment"
logger = require("log4js").getLogger(__filename.split("/").pop(-1).split(".")[0])

dbPanstamp = new(cradle.Connection)(Config.couchDB.host, Config.couchDB.port).database("panstamp")
dbPanstampPackets = new(cradle.Connection)(Config.couchDB.host, Config.couchDB.port).database("panstamp_packets")
dbPanstampEvents = new(cradle.Connection)(Config.couchDB.host, Config.couchDB.port).database("panstamp_events")

developers = []
devicesConfig = {}
devices = {}
newDevice = 
    product:
        productCode: ""
        hardware: ""
        firmware: ""
levels = {}
lights = {}
historicLength = 40
swapPackets = []
swapEvents = []
publisher = undefined
udpBridge = undefined
pressure = undefined
temperature = undefined
lightStatus = undefined # Only one, may have multiple light controller

initLevels = () ->
    levels = {}
    dbPanstamp.get "levels", (err, doc) ->
        return logger.error err if err?
        levels = doc.levels
        logger.info "Got #{levels.length} levels"
        for level in levels
            do (level) ->
                room.lights = [] for room in level.rooms
        initLights()

initLights = () ->
    lights = {}
    dbPanstamp.get "lights", (err, doc) ->
        return logger.error err if err?
        lights = doc.lights
        logger.info "Got #{lights.length} lights"
        for light in lights
            do (light) ->
                for level in levels
                    do (level) ->
                        for room in level.rooms
                            do (room) ->
                                room.lights.push light if room.id == light.location.room_id

        initDevicesConfig()

initDevicesConfig = () ->
    developers = []
    devicesConfig = {}
    logger.info "Init devices configuration..."
    dbPanstamp.get "devices", (err, doc) ->
        return logger.error err if err?
        developers = doc.developers
        logger.debug "Got #{developers.length} developers"
        for developer in developers
            do (developer) ->
                logger.debug "Got #{developer.devices.length} devices for developer #{developer.name}"
                for device in developer.devices
                    do (device) ->
                        logger.debug "Init device configuration #{developer.id}/#{device.id} - #{device.version.hardware}/#{device.version.firmware} for developer #{developer.name}"
                        dbPanstamp.get developer.id + device.id + device.version.hardware + device.version.firmware, (err, doc) ->
                            return logger.error err if err?
                            device = doc
                            devicesConfig[device._id] = device
                            logger.info "Got device configuration #{device._id} for developer #{developer.name}"
        initDevices()

initDevices = () ->
    devices = {}
    dbPanstamp.view "domotix/devices", { }, (err, doc) ->
        for docDevices in doc
            devices["DEV" + swap.num2byte(docDevices.value.address)] = docDevices.value
            for level in levels
                for room in level.rooms
                    devices["DEV" + swap.num2byte(docDevices.value.address)].location.room = room if room.id == docDevices.value.location.room_id
        ss.api.publish.all "devicesUpdated"

initLevels()

initSwapPackets = () ->
    swapPackets = []
    swapEvents = []
    options =
        include_docs: true
        limit: historicLength
        descending: true

    dbPanstampPackets.all options, (err, res) ->
        logger.error err if err?
        return if err?
        for row in res.rows
            delete row.doc._id
            delete row.doc._rev
            swapPackets.splice 0, 0, row.doc
        ss.api.publish.all "swapPacket"
    
    dbPanstampEvents.all options, (err, res) ->
        logger.error err if err?
        return if err?
        for row in res.rows
            delete row.doc._id
            delete row.doc._rev
            swapEvents.splice 0, 0, row.doc
        ss.api.publish.all "swapEvent"
        
initSwapPackets()

addSwapEvent = (swapEvent, swapDevice) ->
    swapEvent.time = moment().format()
    
    dbPanstampEvents.save swapEvent.time, swapEvent, (err, doc) ->
        logger.error err if err?
    
    logger.warn "#{swapEvent.topic} - #{swapEvent.text} @#{swapEvent.time}" if swapEvent.type is "warn"
    logger.info "#{swapEvent.topic} - #{swapEvent.text} @#{swapEvent.time}" if swapEvent.type is "info"
    logger.error "#{swapEvent.topic} - #{swapEvent.text} @#{swapEvent.time}" if swapEvent.type is "error"
    
    swapEvents.splice 0, 0, swapEvent
    swapEvents.pop() if swapEvents.length > historicLength
    ss.api.publish.all "swapEvent", swapEvent
    
addSwapPacket = (swapPacket, packetDevice, foundRegister) ->
    send = false
    if packetDevice isnt undefined && swapPacket.func == swap.Functions.STATUS
        if packetDevice.product.indexOf swap.LightController.productCode == 0
            if swapPacket.regId == swap.LightController.Registers.Outputs.id
                lightStatus = swapPacket
                
                for light in lights
                    do(light) ->
                        if light.swapDeviceAddress == packetDevice.address 
                            light.status = swapPacket.value[light.outputNb]
                
                # do not need to store updated lights
                #dbPanstamp.save "lights", lights._rev, lights, (err, res) ->
                #    return logger.error err if err?

                publisher.publish swap.MQ.Type.LIGHT_STATUS, 
                    swapPacket: swapPacket
                
                ss.api.publish.all "lightStatusUpdated", lightStatus
                send = true
            else if swapPacket.regId == swap.LightController.Registers.PressureTemperature.id
                # issue-#
                pressure = swapPacket
                publisher.publish swap.MQ.Type.PRESSURE, 
                    swapPacket: swapPacket
                
                sleep.sleep 1
                
                temperature = swapPacket
                publisher.publish swap.MQ.Type.TEMPERATURE, 
                    swapPacket: swapPacket
                
                ss.api.publish.all "pressureUpdated", pressure
                ss.api.publish.all "temperatureUpdated", temperature
                send = true
        else if packetDevice.product.indexOf swap.LightSwitch.productCode == 0
            if swapPacket.regId == swap.LightSwitch.Registers.Temperature.id
                temperature = swapPacket
                publisher.publish swap.MQ.Type.TEMPERATURE, 
                    swapPacket: swapPacket
                
                ss.api.publish.all "temperatureUpdated", temperature
                send = true
                
        dbPanstampPackets.save swapPacket.time.time, swapPacket, (err, doc) ->
        logger.error err if err?
    
        swapPackets.splice 0, 0, swapPacket
        swapPackets.pop() if swapPackets.length > historicLength
        ss.api.publish.all "swapPacket", swapPacket
        
        if !send
            publisher.publish swap.MQ.Type.SWAP_PACKET, 
                swapPacket: swapPacket
    
serial = new SerialModem Config.serial
serial.on "started", () ->
    publisher = new Publisher Config.broker
    udpBridge = new UdpBridge Config.udpBridge
    
    serial.on "swapPacket", (swapPacket) ->
        swapPacketReceived swapPacket

    udpBridge.on "message", (message) ->
        [type, length, data] = message.split "|"
        if parseInt(type) is swap.MQ.Type.SWAP_PACKET
            packet = new swap.CCPacket ("(FFFF)" + data)
            if packet.data
                swapPacket = new swap.SwapPacket packet                
                if swapPacket.func is swap.Functions.STATUS
                    logger.warn "Status Packet received from UDP Bridge, not allowed"
                else
                    addSwapPacket swapPacket
                    serial.send swapPacket
        else if parseInt(type) is swap.MQ.Type.PRESSURE
            # Request pressure
            publisher.publish swap.MQ.Type.PRESSURE, pressure
        else if parseInt(type) is swap.MQ.Type.TEMPERATURE
            # Request temperature
            publisher.publish swap.MQ.Type.TEMPERATURE, temperature
        else if parseInt(type) is swap.MQ.Type.LIGHT_STATUS
            # Request light status
            publisher.publish swap.MQ.Type.LIGHT_STATUS, lightStatus
        else
            logger.error "Unknown message type received #{type}"

# Only for dummy serial modem
serial.start() if Config.serial.dummy

# Function to call when a packet is received
swapPacketReceived = (swapPacket) ->
    # Add device if not already seen
    packetDevice = undefined

    if ("DEV" + swap.num2byte(swapPacket.source)) not of devices
        text =  "Packet received from unknown source: #{swapPacket.source}"
        addSwapEvent
            type: "warn"
            topic: "unknownSwapPacketDevice"
            text: text

        if swapPacket.func is swap.Functions.STATUS
            value = swapPacket.value
            if swapPacket.regId is swap.Registers.productCode.id
                newDevice.product.productCode = (swap.num2byte(v) for v in value).join("")
            else if swapPacket.regId is swap.Registers.hardwareVersion.id
                newDevice.product.hardware = (swap.num2byte(v) for v in value).join("")
            else if swapPacket.regId is swap.Registers.firmwareVersion.id
                newDevice.product.firmware = (swap.num2byte(v) for v in value).join("")

        if newDevice.product.productCode != "" && newDevice.product.hardware != "" && newDevice.product.firmware != ""
            productCode = newDevice.product.productCode + newDevice.product.hardware + newDevice.product.firmware
            if not devicesConfig[productCode]?
                addSwapEvent
                    type: "warn"
                    topic: "unknownDevice"
                    text: "Unknown device or manufacturer Id detected: #{productCode}"
            else
                packetDevice = devicesConfig[productCode]
                packetDevice.address = swapPacket.source
                packetDevice.networkId = Config.network.networkId
                packetDevice.frequencyChannel = Config.network.frequencyChannel
                packetDevice.product = productCode
                packetDevice.securityNonce = swapPacket.nonce
                packetDevice.lastStatusTime = swapPacket.time
                packetDevice.location =
                    room_id: -1
                    x: 0
                    dx: 0
                    y: 0
                    dy: 0
                    z: 0
                    dz: 0
                delete packetDevice._id
                delete packetDevice._rev

                # Add to the list before save in DB because packets may be received during saving
                devices["DEV" + swap.num2byte(packetDevice.address)] = packetDevice
                ss.api.publish.all "devicesUpdated"

                addSwapEvent
                    type: "warn"
                    name: "newDevice"
                    text: "New device detected: #{packetDevice.productCode}, #{packetDevice.address}"

                dbPanstamp.save "DEV" + swap.num2byte(packetDevice.address), packetDevice, (err, doc) ->
                    return logger.error err if err?
                    packetDevice._id = doc._id
                    packetDevice._rev = doc._rev
                    #devices[doc._id] = packetDevice
                    addSwapEvent
                        type: "info"
                        name: "newSwapPacketDeviceDetected"
                        text: "New packetDevice #{packetDevice.address} added: #{packetDevice.product.productCode} - #{devicesConfig[productCode].product} (#{devicesConfig[productCode].developer})"
                        packetDevice: packetDevice

                sleep.sleep 1
        return
    else
        packetDevice = devices["DEV" + swap.num2byte(swapPacket.source)]

    # Handles STATUS packets
    if swapPacket.func is swap.Functions.STATUS
        value = swapPacket.value

        # handles missing packets ??
        if not Math.abs(packetDevice.securityNonce - swapPacket.nonce) in [1,255]
            addSwapEvent
                type: "warn"
                name: "missingNonce"
                text: "(#{packetDevice._id}): Missing nonce: got #{swapPacket.nonce} - expected #{packetDevice.securityNonce}"
                packetDevice: packetDevice

        packetDevice.securityNonce = swapPacket.nonce
        packetDevice.lastStatusTime = swapPacket.time

        if swapPacket.regId is swap.Registers.productCode.id
            # Nothing special to do 
        else if swapPacket.regId is swap.Registers.state.id
            packetDevice.systemState = swap.SwapStates.get value
            addSwapEvent
                type: "info"
                topic: "systemState"
                text: "(#{packetDevice._id}): State changed to #{packetDevice.systemState.str}"
                packetDevice: packetDevice

            packetDevice.systemState = packetDevice.systemState.level

        else if swapPacket.regId is swap.Registers.channel.id
            packetDevice.frequencyChannel = value[0]
            addSwapEvent
                type: "warn"
                topic: "frequencyChannel"
                text: "(#{packetDevice._id}): Channel changed to #{packetDevice.frequencyChannel}"
                packetDevice: packetDevice

        else if swapPacket.regId is swap.Registers.security.id
            packetDevice.securityOption = value[0]
            addSwapEvent
                type: "info"
                topic: "securityOption"
                text: "(#{packetDevice._id}): Security changed to #{packetDevice.securityOption}"
                packetDevice: packetDevice

        else if swapPacket.regId is swap.Registers.password.id
            packetDevice.securityPassword = (swap.num2byte(v) for v in value).join("")
            addSwapEvent
                type: "info"
                topic: "securityPassword"
                text: "(#{packetDevice._id}): Password changed"
                packetDevice: packetDevice

        else if swapPacket.regId is swap.Registers.nonce.id
            packetDevice.securityNonce = value[0]
            addSwapEvent
                type: "info"
                topic: "securityNonce"
                text: "(#{packetDevice._id}): Nonce received to #{packetDevice.securityNonce}"
                packetDevice:packetDevice

        else if swapPacket.regId is swap.Registers.network.id
            value = 256 * value[0] + value[1]
            packetDevice.networkId = value
            addSwapEvent
                type: "info"
                topic: "network"
                text: "(#{packetDevice._id}): Network changed to #{value}"
                packetDevice: packetDevice

        else if swapPacket.regId is swap.Registers.address.id
            newAddress = value[0]
            oldAddress = packetDevice.address
            if oldAddress != newAddress  # may be due a QUERY request
                dbPanstamp.save "DEV" + swap.num2byte(newAddress), packetDevice, (err, doc) ->
                    return logger.error err if err?
                    devices["DEV" + swap.num2byte(newAddress)] = packetDevice
                    addSwapEvent
                        type: "info"
                        topic: "address"
                        text: "(#{packetDevice._id}): Address changed from #{oldAddress} to #{newAddress}"
                        packetDevice: packetDevice
                        oldAddress: oldAddress

                    cid = "DEV" + swap.num2byte(oldAddress)
                    dbPanstamp.remove cid, devices[cid]._rev, (err, res) ->
                        return logger.error err if err?
                        delete devices[cid]
                        return true

        else if swapPacket.regId is swap.Registers.txInterval.id
            value = 256 * value[0] + value[1]
            packetDevice.txInterval = value
            addSwapEvent
                type: "info"
                topic: "txInterval"
                text: "(#{packetDevice._id}): Transmit interval changed to #{value}"
                packetDevice: packetDevice

        # Retrieve value from endpoints definition
        else
            foundRegisters = (register for register in packetDevice.configRegisters when register.id == swapPacket.regId)
            if foundRegisters.length == 1
                foundRegister = foundRegisters[0]
                updateParamsValue foundRegister, swapPacket
                addSwapEvent
                    type: "info"
                    topic: "register"
                    text: "(#{packetDevice._id}): Register #{foundRegister.name} changed to #{value}"
                    packetDevice: packetDevice
            else
                foundRegisters = (register for register in packetDevice.regularRegisters when register.id == swapPacket.regId)
                if foundRegisters.length == 1
                    foundRegister = foundRegisters[0]
                    updateEndpointsValue foundRegister, swapPacket
                    addSwapEvent
                        type: "info"
                        topic: "register"
                        text: "(#{packetDevice._id}): Register #{foundRegister.name} changed to #{value}"
                        packetDevice: packetDevice
                else
                    addSwapEvent
                        type: "warn"
                        topic: "register"
                        text: "(#{packetDevice._id}): Unknown register #{swapPacket.regId}"
                        packetDevice: packetDevice
                    return

        dbPanstamp.save "DEV" + swap.num2byte(packetDevice.address), packetDevice._rev, packetDevice, (err, res) ->
            return logger.error err if err?
            devices["DEV" + swap.num2byte(packetDevice.address)]._rev = res.rev
            ss.api.publish.all "devicesUpdated"

    else if swapPacket.func is swap.Functions.QUERY
        logger.info "Query request received from #{swapPacketsource} for packetDevice #{swapPacketdest} register #{swapPacket.regId}"
    else if swapPacket.func is swap.Functions.COMMAND
        logger.info "Command request received from #{swapPacketsource} for packetDevice #{swapPacket.dest} register #{swapPacket.regId} with value #{swapPacket.value}"
    
    addSwapPacket swapPacket, packetDevice, foundRegister
    
updateEndpointsValue = (register, swapPacket) ->
    register.time = swapPacket.time
    register.value = if swapPacket.value.length is undefined then [swapPacket.value] else swapPacket.value

updateParamsValue = (register, swapPacket) ->
    register.time = swapPacket.time
    register.value = if swapPacket.value.length is undefined then [swapPacket.value] else swapPacket.value

# Return TRUE if device must be saved in DB
# Return FALSE in case no update is required or update is handled by STATUS packet
onUpdateDevice = (oldDevice, newDevice) ->
    if oldDevice.address = 255 and newDevice.address != 255
        sendSwapCommand oldDevice.address, swap.Registers.address, newDevice.address
        return false
    else if oldDevice.location != newDevice.location
        return true
    else
        return false

# Gets the value of a specific register
sendSwapQuery = (address, registerId) ->
    sp = new swap.SwapPacket()
    sp.source = Config.network.address
    sp.dest = address
    sp.func = swap.Functions.QUERY
    sp.regAddress = address
    sp.regId = registerId
    serial.send(sp)

# Sets the value of a specific register
sendSwapCommand = (address, registerId, value) ->
    sp = new swap.SwapPacket()
    sp.source = Config.network.address
    sp.dest = address
    sp.func = swap.Functions.COMMAND
    sp.regAddress = address
    sp.regId = registerId
    sp.value = value
    serial.send(sp)

# Send a generic swap packet
sendSwapPacket = (functionCode, address, registerId, value) ->
    sp = new swap.SwapPacket()
    sp.source = Config.network.address
    sp.dest = address
    sp.func = functionCode
    sp.regAddress = address
    sp.regId = registerId
    sp.value = value
    serial.send(sp)
    
destroy = () ->
    publisher.destroy
    udpBridge.destroy

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

    getLevels: ->
        res levels

    getLights: ->
        res lights

    getSwapPackets: ->
        res swapPackets

    getSwapEvents: ->
        res swapEvents

    getLastLightStatus: ->
        res lightStatus
    
    refreshDevices: ->
        initLevels()
        res true

    refreshSwapPackets: ->
        initSwapPackets()
        res true

    updateDevice: (oldDevice, newDevice) ->
        # TODO: handle device update...
        if onUpdateDevice oldDevice, newDevice
          cid = "DEV" + swap.num2byte newDevice.address
          dbPanstamp.save cid, devices[cid]._rev, newDevice, (err, res) ->
              return logger.error err if err?
              newDevice._rev = res.rev
              devices[cid] = newDevice
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
    sendSwapQuery: (address, registerId) ->
        sendSwapQuery address, registerId

    # Sets the value of a specific register
    sendSwapCommand: (address, registerId, value) ->
        sendSwapCommand address, registerId, value

    # Send a generic swap packet
    sendSwapPacket: (functionCode, address, registerId, value) ->
        sendSwapPacket functionCode, address, registerId, value

    sendSwapEvent: (topic, data) ->
        addSwapEvent
            "topic": topic,
            "text": data

    checkNewDevices: () ->
        sendSwapQuery address, swap.Registers.productCode.id

    # Test serial reception
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
    
    # Delete document returned by a view    
    cleanByView: (view) ->
        logger.debug "Cleaning view #{view}..."
        dbPanstampPackets.view "domotix/" + view, { }, (err, res) ->
            return logger.error err if err?
            logger.debug "Removing document from view #{view}..."
            res.forEach (key, swapEvent, id) ->
                logger.debug "Removing document #{swapEvent._id} from view #{view}..."
                dbPanstampPackets.remove swapEvent._id, swapEvent._rev, (err, res) ->
                    return logger.error err if err?
                    logger.info "#{swapEvent._id} deleted"

