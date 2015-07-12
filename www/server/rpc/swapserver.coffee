Config = require "config"
SerialModem = require "../swap/serialmodem" if not Config.serial.dummy
SerialModem = require "../swap/dummyserialmodem" if Config.serial.dummy
PubSub = require "../swap/pubsub"
State = require "../swap/state"
swap = require "../../client/code/common/swap"
cradle = require "cradle"
ss = require "socketstream"
util = require "util"
sleep = require "sleep"
eventEmitter = require("events").EventEmitter
moment = require "moment"
logger = require("log4js").getLogger(__filename.split("/").pop(-1).split(".")[0])

dbPanstamp = new(cradle.Connection)(Config.couchDB.host, Config.couchDB.port).database("panstamp")
dbEvents = new(cradle.Connection)(Config.couchDB.host, Config.couchDB.port).database("events")
dbPanstampPackets = new(cradle.Connection)(Config.couchDB.host, Config.couchDB.port).database("panstamp_packets")
dbPanstampEvents = new(cradle.Connection)(Config.couchDB.host, Config.couchDB.port).database("panstamp_events")

state = undefined
pubSub = undefined
historicLength = 40


####################################################################################
####################################################################################
# APPLICATION DATA
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
swapPackets = []
swapEvents = []


####################################################################################
#
####################################################################################
initLevels = () ->
    levels = {}
    dbPanstamp.get "levels", (err, doc) ->
        return logger.error "Get levels failed: #{JSON.stringify(err)}" if err?
        levels = doc.levels
        logger.info "Got #{levels.length} levels"
        for level in levels
            do (level) ->
                room.lights = [] for room in level.rooms
        initLights()

####################################################################################
#
####################################################################################
initLights = () ->
    lights = {}
    dbPanstamp.get "lights", (err, doc) ->
        return logger.error "Get lights failed: #{JSON.stringify(err)}" if err?
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

####################################################################################
#
####################################################################################
initDevicesConfig = () ->
    developers = []
    devicesConfig = {}
    logger.info "Init devices configuration..."
    dbPanstamp.get "devices", (err, doc) ->
        return logger.error "Get devices failed: #{JSON.stringify(err)}" if err?
        developers = doc.developers
        logger.debug "Got #{developers.length} developers"
        for developer in developers
            do (developer) ->
                logger.debug "Got #{developer.devices.length} devices for developer #{developer.name}"
                for device in developer.devices
                    do (device) ->
                        logger.debug "Init device configuration #{developer.id}/#{device.id} - #{device.version.hardware}/#{device.version.firmware} for developer #{developer.name}"
                        dbPanstamp.get developer.id + device.id + device.version.hardware + device.version.firmware, (err, doc) ->
                            return logger.error "Get device #{developer.id + device.id + device.version.hardware + device.version.firmware} failed: #{JSON.stringify(err)}" if err?
                            device = doc
                            devicesConfig[device._id] = device
                            logger.info "Got device configuration #{device._id} for developer #{developer.name}"
        initDevices()

####################################################################################
#
####################################################################################
initDevices = () ->
    devices = {}
    dbPanstamp.view "domotix/devices", { }, (err, doc) ->
        for docDevices in doc
            devices["DEV" + swap.num2byte(docDevices.value.address)] = docDevices.value
            for level in levels
                for room in level.rooms
                    devices["DEV" + swap.num2byte(docDevices.value.address)].location.room = room if room.id == docDevices.value.location.room_id
        ss.api.publish.all "devicesUpdated"

####################################################################################
#
####################################################################################
initSwapPacketsEvents = () ->
    swapPackets = []
    swapEvents = []
    options =
        include_docs: true
        limit: historicLength
        descending: true
    
    dbPanstampPackets.view "domotix/lastPackets", 
        limit: historicLength
        descending: true
        , (err, res) ->
            return logger.error "Get view domotix/lastPackets failed: #{JSON.stringify(err)}" if err?
            for row in res.rows
                swapPackets.push row.value
            ss.api.publish.all "swapPacket"
    
    dbPanstampEvents.view "domotix/lastEvents", 
        limit: historicLength
        descending: true
        , (err, res) ->
            return logger.error "Get view domotix/lastEvents failed: #{JSON.stringify(err)}" if err?
            for row in res.rows
                swapEvents.push row.value
            ss.api.publish.all "swapEvent"

####################################################################################
#
####################################################################################
addSwapEvent = (swapEvent, swapDevice) ->
    swapEvent.time = moment().format()
    
    dbPanstampEvents.save swapEvent.time, swapEvent, (err, doc) ->
        logger.error "Save SWAP event #{swapEvent.time} failed: #{JSON.stringify(err)}" if err?
    
    logger.warn "EVENT: #{swapEvent.topic} - #{swapEvent.text} @#{swapEvent.time}" if swapEvent.type is "warn"
    logger.info "EVENT: #{swapEvent.topic} - #{swapEvent.text} @#{swapEvent.time}" if swapEvent.type is "info"
    logger.error "EVENT: #{swapEvent.topic} - #{swapEvent.text} @#{swapEvent.time}" if swapEvent.type is "error"
    
    swapEvents.splice 0, 0, swapEvent
    swapEvents.pop() if swapEvents.length > historicLength
    ss.api.publish.all "swapEvent", swapEvent

####################################################################################
#
####################################################################################
addSwapPacket = (swapPacket, packetDevice, foundRegister) ->
    send = false
    
    return if packetDevice is undefined
    
    if swapPacket.func == swap.Functions.STATUS
        
        if (packetDevice.product.indexOf swap.LightController.productCode) == 0
            
            if swapPacket.regId == swap.LightController.Registers.Outputs.id
                
                # TODO Add support for multiple lights controller
                sendToClient swap.MQ.Type.LIGHT_STATUS, swapPacket, packetDevice, foundRegister
                
                for light in lights
                    do(light) ->
                        if light.swapDeviceAddress == packetDevice.address 
                            light.status = swapPacket.value[light.outputNb]
                
                ss.api.publish.all "lightStatusUpdated", state.lightStatus
                send = true
            else if swapPacket.regId == swap.LightController.Registers.PressureTemperature.id
                
                sendToClient [swap.MQ.Type.PRESSURE, swap.MQ.Type.TEMPERATURE], swapPacket, packetDevice, foundRegister
                
                ss.api.publish.all "temperatureUpdated", state.temperature
                send = true
            
        else if (packetDevice.product.indexOf swap.LightSwitch.productCode) == 0
            
            if swapPacket.regId == swap.LightSwitch.Registers.Temperature.id
                
                sendToClient swap.MQ.Type.TEMPERATURE, swapPacket, packetDevice, foundRegister
                
                ss.api.publish.all "temperatureUpdated", state.temperature
                send = true
            
        if !send
            sendToClient swap.MQ.Type.SWAP_PACKET, swapPacket, packetDevice, foundRegister
    
    dbPanstampPackets.save swapPacket.time.time, swapPacket, (err, doc) ->
        logger.error "Save SWAP packet #{swapPacket.time.time} failed: #{JSON.stringify(err)}" if err?
    
    swapPackets.splice 0, 0, swapPacket
    swapPackets.pop() if swapPackets.length > historicLength
    ss.api.publish.all "swapPacket", swapPacket

####################################################################################
####################################################################################
# SETUP
####################################################################################
initLevels()

initSwapPacketsEvents()

serial = new SerialModem Config.serial
serial.on "started", () ->
    pubSub = new PubSub Config.broker
    state = new State Config, pubSub
    
    # Raw panstamp message (xxxx)yyyyyy
    serial.on swap.MQ.Type.SWAP_PACKET, (rawSwapPacket) ->
        if rawSwapPacket[0] is "("
            ccPacket = new swap.CCPacket rawSwapPacket[0 .. rawSwapPacket.length-1]  # remove \r
            if ccPacket.data
                swapPacket = new swap.SwapPacket ccPacket
                swapPacketReceived swapPacket
            else
                logger.warn "Unknown data received from Serial Bridge: #{rawSwapPacket} but must be a CCPacket"
        else
            logger.warn "Unknown data received from Serial Bridge: #{rawSwapPacket} but must be like '(xxxx)yyyyyy'"
    
    pubSub.on swap.MQ.Type.MANAGEMENT, (message) ->
        logger.info "Management message: #{JSON.stringify(message)}"
        [client, type, data] = message.split ":"
        state.updateState "clients", client, type, data
    
    pubSub.on swap.MQ.Type.SWAP_PACKET, (rawSwapPacket) ->
        ccPacket = new swap.CCPacket "(FFFF)" + rawSwapPacket.toString 'hex'
        if ccPacket.data
            swapPacket = new swap.SwapPacket ccPacket
            if swapPacket.func is swap.Functions.STATUS
                logger.warn "Status Packet received from MQ Bridge, not allowed"
            else
                addSwapPacket swapPacket, undefined
                serial.send swapPacket
    
# Only for dummy serial modem
serial.start() if Config.serial.dummy


####################################################################################
# Function to call when a packet is received
####################################################################################
sendToClient = (topics, swapPacket, swapDevice, swapRegister) ->
    
    topics = [topics] if not Array.isArray topics
    
    for topic in topics
        
        nonce = state.updateState topic, swapPacket.source, swapPacket
        
        pubSub.publish topic, 
            nonce: nonce
            swapPacket: swapPacket
            swapDevice: swapDevice
            swapRegister: swapRegister


####################################################################################
# Function to call when a packet is received
####################################################################################
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
                    return logger.error "Save new device DEV#{swap.num2byte(packetDevice.address)} failed: #{JSON.stringify(err)}" if err?
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
                    return logger.error "Save device DEV#{swap.num2byte(newAddress)} failed: #{JSON.stringify(err)}" if err?
                    devices["DEV" + swap.num2byte(newAddress)] = packetDevice
                    addSwapEvent
                        type: "info"
                        topic: "address"
                        text: "(#{packetDevice._id}): Address changed from #{oldAddress} to #{newAddress}"
                        packetDevice: packetDevice
                        oldAddress: oldAddress
                    
                    cid = "DEV" + swap.num2byte(oldAddress)
                    dbPanstamp.remove cid, devices[cid]._rev, (err, res) ->
                        return logger.error "#{JSON.stringify(err)}" if err?
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
            return logger.error "Save device DEV#{swap.num2byte(packetDevice.address)}/#{packetDevice._rev} failed: #{JSON.stringify(err)}" if err?
            devices["DEV" + swap.num2byte(packetDevice.address)]._rev = res.rev
            ss.api.publish.all "devicesUpdated"
    
    else if swapPacket.func is swap.Functions.QUERY
        logger.info "Query request received from #{swapPacketsource} for packetDevice #{swapPacketdest} register #{swapPacket.regId}"
    else if swapPacket.func is swap.Functions.COMMAND
        logger.info "Command request received from #{swapPacketsource} for packetDevice #{swapPacket.dest} register #{swapPacket.regId} with value #{swapPacket.value}"
    
    addSwapPacket swapPacket, packetDevice, foundRegister
    
####################################################################################
#
####################################################################################
updateEndpointsValue = (register, swapPacket) ->
    register.time = swapPacket.time
    register.value = if swapPacket.value.length is undefined then [swapPacket.value] else swapPacket.value

####################################################################################
#
####################################################################################
updateParamsValue = (register, swapPacket) ->
    register.time = swapPacket.time
    register.value = if swapPacket.value.length is undefined then [swapPacket.value] else swapPacket.value

####################################################################################
# Return TRUE if device must be saved in DB
# Return FALSE in case no update is required or update is handled by STATUS packet
####################################################################################
onUpdateDevice = (oldDevice, newDevice) ->
    if oldDevice.address = 255 and newDevice.address != 255
        sendSwapCommand oldDevice.address, swap.Registers.address, newDevice.address
        return false
    else if oldDevice.location != newDevice.location
        return true
    else
        return false

####################################################################################
# Gets the value of a specific register
####################################################################################
sendSwapQuery = (address, registerId) ->
    swapPacket = new swap.SwapPacket()
    swapPacket.source = Config.network.address
    swapPacket.dest = address
    swapPacket.func = swap.Functions.QUERY
    swapPacket.regAddress = address
    swapPacket.regId = registerId
    serial.send swapPacket
    addSwapPacket swapPacket

####################################################################################
# Sets the value of a specific register
####################################################################################
sendSwapCommand = (address, registerId, value) ->
    swapPacket = new swap.SwapPacket()
    swapPacket.source = Config.network.address
    swapPacket.dest = address
    swapPacket.func = swap.Functions.COMMAND
    swapPacket.regAddress = address
    swapPacket.regId = registerId
    serial.send swapPacket
    addSwapPacket swapPacket

####################################################################################
# Send a generic swap packet
####################################################################################
sendSwapPacket = (functionCode, address, registerId, value) ->
    swapPacket = new swap.SwapPacket()
    swapPacket.source = Config.network.address
    swapPacket.dest = address
    swapPacket.func = functionCode
    swapPacket.regAddress = address
    swapPacket.regId = registerId
    swapPacket.value = value
    serial.send swapPacket
    addSwapPacket swapPacket

####################################################################################
#
####################################################################################
destroy = () ->
    pubSub.destroy

####################################################################################
# CLIENT SERVICES
####################################################################################
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
    
    getState: ->
        res state.getState()
    
    getLightStatus: ->
        res state.getState("lightStatus")
    
    getPressure: ->
        res state.getState("pressure")
    
    getTemperature: ->
        res state.getState("temperature")
    
    refreshDevices: ->
        initLevels()
        res true
    
    refreshState: ->
        logger.info "State before #{JSON.stringify(state.getState())}"
        state.init()
        logger.info "State after #{JSON.stringify(state.getState())}"
        res true
        
    refreshSwapPacketsEvents: ->
        initSwapPacketsEvents()
        res true
    
    updateDevice: (oldDevice, newDevice) ->
        # TODO: handle device update...
        if onUpdateDevice oldDevice, newDevice
          cid = "DEV" + swap.num2byte newDevice.address
          dbPanstamp.save cid, devices[cid]._rev, newDevice, (err, res) ->
              return logger.error "Save device #{cid}/#{devices[cid]._rev} failed: #{JSON.stringify(err)}" if err?
              newDevice._rev = res.rev
              devices[cid] = newDevice
              return true
        res true
    
    deleteDevice: (address) ->
        cid = "DEV" + swap.num2byte address
        dbPanstamp.remove cid, devices[cid]._rev, (err, res) ->
            return logger.error "Remove device #{cid}/#{devices[cid]._rev} failed: #{JSON.stringify(err)}" if err?
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
            return logger.error "#{JSON.stringify(err)}" if err?
            logger.debug "Removing document from view #{view}..."
            res.forEach (key, swapEvent, id) ->
                logger.debug "Removing document #{swapEvent._id} from view #{view}..."
                dbPanstampPackets.remove swapEvent._id, swapEvent._rev, (err, res) ->
                    return logger.error "Remove SWAP event #{swapEvent._id}/#{swapEvent._rev} failed: #{JSON.stringify(err)}" if err?
                    logger.info "#{swapEvent._id} deleted"
    
    createEvent: (event) ->
        event.dateTime = moment().format()
        dbEvents.save event, (err, doc) ->
            logger.error "Save event failed: #{JSON.stringify(err)}" if err?
        logger.info "#{event.type} - #{event.subtype} @#{event.time}"

