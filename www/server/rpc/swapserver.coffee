#SerialModem = require '../swap/serialmodem'
SerialModem = require '../swap/dummyserialmodem'
Config = require 'config'
swap = require '../../client/code/common/swap'
cradle = require 'cradle'
ss = require 'socketstream'
util = require 'util'
eventEmitter = require('events').EventEmitter
logger = require('log4js').getLogger(__filename.split("/").pop(-1).split(".")[0])

db = new(cradle.Connection)().database('panstamp')

developers = []
devicesConfig = {}
devices = {}
packets = []

initDevicesConfig = () ->
    developers = []
    devicesConfig = {}
    db.get 'devices', (err, doc) ->
        return logger.error err if err?
        developers = doc.developers
        logger.info "Got #{developers.length} developers"
        for developer in developers
            do (developer) ->
                logger.info "Got #{developer.devices.length} devices for developer #{developer.name}"
                for device in developer.devices
                    do (device) ->
                        db.get developer.id + device.id, (err, doc) ->
                            return logger.error err if err?
                            device = doc
                            devicesConfig[device._id] = device
                            logger.info "Got device #{device.product} for developer #{developer.name}"
initDevicesConfig()

initDevices = () ->
    devices = {}
    db.view 'devices/devices', { }, (err, doc) ->
        for docDevices in doc
            devices["DEV" + swap.num2byte(docDevices.value.address)] = docDevices.value
        ss.api.publish.all 'devicesUpdated'
initDevices()

serialConfig = Config.serial
serial = new SerialModem serialConfig

serial.on 'started', () ->
    publisher = new ps.Publisher config
    serial.on 'data', (sp) ->
        ss.publish.all 'swapPacket', sp
        packets.splice(0, 0, sp)
        packets.pop() if packets.length > 40
        packetReceived packet

# Function to call when a packet is received
packetReceived = (packet) ->
    # Add device if not already seen
    packetDevice = undefined
    
    logger.debug packet
    
    if ("DEV" + swap.num2byte(packet.source.toString())) not of devices
        text =  "Packet received from unknown source: #{packet.source}"
        logger.warn text
        #eventEmitter.emit 'swapEvent', {name: 'unknownpacketDevice', text:text, type:'warning', time: new Date()}
    else
        packetDevice = devices["DEV" + swap.num2byte(packet.source)]
        logger.debug packetDevice
    
    # Handles STATUS packets
    if packet.func is swap.Functions.STATUS
        value = packet.value
        
        if packet.regId is swap.Registers.productCode.id
            # First time this packetDevice talks
            return if packetDevice
            
            productCode = (swap.num2byte(v) for v in packet.value).join('')
            
            if not devicesConfig[productCode]?
                text = "Unknown device or manufacturer Id detected: #{value}"
                logger.warn text
                #eventEmitter.emit 'swapEvent', {name:'unknownDevice', text:text, type:'warning', time: new Date()}
                return
            
            packetDevice = devicesConfig[productCode]
            packetDevice.address = packet.source
            packetDevice.networkId = Config.network.networkId
            packetDevice.frequencyChannel = Config.network.frequencyChannel
            packetDevice.nonce = packet.nonce
            packetDevice.productCode = productCode
            packetDevice.lastStatusTime = packet.time
            delete packetDevice._id
            delete packetDevice._rev
            
            db.save "DEV" + swap.num2byte(packetDevice.address), packetDevice, (err, doc) ->
                return logger.error err if err?
                devices[doc._id] = packetDevice
                text = "New packetDevice #{packetDevice.address} added: #{packetDevice.productCode} - #{devicesConfig[productCode].product} (#{devicesConfig[productCode].developer})"
                logger.info text
                #@emit 'swapEvent', {name:'newpacketDeviceDetected', text:text, packetDevice:packetDevice, time: new Date()}
        
        logger.warn "Packet received from unknown source: #{packet.source} and not a productCode status packet" if not packetDevice 
        return if not packetDevice 
        
        # handles missing packets ??
        if Math.abs(packetDevice.securityNonce - packet.nonce) in [1,255]
            text = "(#{packetDevice.location}): Missing nonce: #{packet.nonce} - #{packetDevice.securityNonce}"
            logger.warn text
            # device = @repo[packetDevice.manufacturerId].devices[packetDevice.deviceId]
            #@emit 'swapEvent', {name:'missingNonce', text:text, packetDevice:packetDevice, type:'warning', time: new Date()}
        
        packetDevice.securityNonce = packet.nonce
        packetDevice.lastStatusTime = packet.time
        
        if packet.regId is swap.Registers.hardwareVersion.id
            packetDevice.version.hardware = (swap.num2byte(v) for v in value).join('')
            logger.info "(#{packetDevice.location}): Hardware version changed: #{packetDevice.version.hardware}"
        
        else if packet.regId is swap.Registers.firmwareVersion.id
            packetDevice.version.firmware = (swap.num2byte(v) for v in value).join('')
            logger.info "(#{packetDevice.location}): Firmware version changed: #{packetDevice.version.firmware}"
        
        else if packet.regId is swap.Registers.state.id
            packetDevice.systemState = swap.SwapStates.get value
            text = "(#{packetDevice.location}): State changed to #{packetDevice.systemState.str}"
            logger.info text
            #@emit 'swapEvent', {name:'systemState', text:text, packetDevice: packetDevice, time: new Date() }
            packetDevice.systemState = packetDevice.systemState.level
        
        else if packet.regId is swap.Registers.channel.id
            packetDevice.frequencyChannel = value[0]
            text = "(#{packetDevice.location}): Channel changed to #{packetDevice.frequencyChannel}"
            logger.warn text
            #@emit 'swapEvent', {name:'frequencyChannel', text:text, packetDevice:packetDevice, time: new Date()}
        
        else if packet.regId is swap.Registers.security.id
            packetDevice.securityOption = value[0]
            text = "(#{packetDevice.location}): Security changed to #{packetDevice.securityOption}"
            logger.info text
            #@emit 'swapEvent', {name:'securityOption', text:text, packetDevice:packetDevice, time: new Date()}
        
        else if packet.regId is swap.Registers.password.id
            packetDevice.securityPassword = (swap.num2byte(v) for v in value).join('')
            text = "(#{packetDevice.location}): Password changed"
            logger.info text
            #@emit 'swapEvent', {name:'securityPassword', text:text, packetDevice:packetDevice, time: new Date()}
        
        else if packet.regId is swap.Registers.nonce.id
            packetDevice.securityNonce = value[0]
            text = "(#{packetDevice.location}): Nonce received to #{packetDevice.securityNonce}"
            logger.debug text
            #@emit 'swapEvent', {name:'securityNonce', text:text, packetDevice:packetDevice, time: new Date()}
        
        else if packet.regId is swap.Registers.network.id
            value = 256 * value[0] + value[1]
            packetDevice.networkId = value
            text = "(#{packetDevice.location}): Network changed to #{value}"
            logger.warn text
            #@emit 'swapEvent', {name:'network', text:text, packetDevice:packetDevice, time: new Date()}
        
        else if packet.regId is swap.Registers.address.id
            newAddress = value[0]
            oldAddress = packetDevice.source
            
            db.save "DEV" + swap.num2byte(newAddress), packetDevice, (err, doc) ->
                return logger.error err if err?
                devices["DEV" + swap.num2byte(newAddress)] = packetDevice
                text = "(#{packetDevice.location}): Address changed from #{oldAddress} to #{newAddress}"
                logger.info text
                #@emit 'swapEvent', {name:'address', text: text, packetDevice: packetDevice, oldAddress:oldAddress, time: new Date()}
                
                cid = "DEV" + swap.num2byte(oldAddress)
                db.remove cid, devices[cid]._rev, (err, res) ->
                    return logger.error err if err?
                    delete devices[cid]
                    return true
        
        else if packet.regId is swap.Registers.txInterval.id
            value = 256 * value[0] + value[1]
            text = "(#{packetDevice.location}): Transmit interval changed to #{value} s"
            logger.info text
            packetDevice.txInterval = value;
            #@emit 'swapEvent', {name: 'txInterval', text: text, packetDevice: packetDevice, time: new Date()}
        
        # Retrieve value from endpoints definition 
        else if packet.regId > 10
            if not (packetDevice.manufacturerId of @repo)
                text = "(#{packetDevice.location}): Status received from unknown device manufacturer: #{packetDevice.manufacturerId}"
                logger.warn text
                #@emit 'swapEvent', {name: 'unknownDevice', type:"warning", text:text, packetDevice:packetDevice, time: new Date()}
            else                
                device = @repo[packetDevice.manufacturerId].devices[packetDevice.deviceId]
                @handleStatus packet, device
    else if packet.function is swap.Functions.QUERY
        logger.info "Query request received from #{packet.source} for packetDevice #{packet.dest} register #{packet.regId}" 
    else if packet.function is swap.Functions.COMMAND
        logger.info "Command request received from #{packet.source} for packetDevice #{packet.dest} register #{packet.regId} with value #{packet.value}"
    else
        logger.error "Received packet does not contain a valid function: #{packet.func}"

exports.actions = (req, res, ss) ->

    # Easily debug incoming requests here
    #console.log(req)

    getConfig: ->
        res Config
    
    getDevices: ->
        logger.debug devices
        res devices
    
    getPackets: () ->
        res packets
    
    refreshDevices: ->
        initDevicesConfig()
        initDevices()
    
    updateDevice: (device) ->
        # handle address update
        cid = "DEV" + swap.num2byte device.address
        db.save cid, devices[cid]._rev, device, (err, res) ->
            return logger.error err if err?
            device._rev = res.rev
            devices[cid] = device
            return true
        res true
    
    deleteDevice: (address) ->
        cid = "DEV" + swap.num2byte address
        db.remove cid, devices[cid]._rev, (err, res) ->
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
        sp.time = new Date()
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
        sp.time = new Date()
        serial.send(sp)
    
    checkNewDevices: () ->
        sendQuery address, swap.Registers.productCode.id
    
    onSerial: (str) ->
        data = "(0000)" + str
        packet = new swap.CCPacket data
        packet = new swap.SwapPacket packet
        
        packetReceived packet
        
        ss.publish.all 'swapPacket', packet
        packets.splice(0, 0, packet)
        packets.pop() if packets.length > 40

