Config = require "config"
S = require "string"
zmq = require "zmq"
swap = require "../../client/code/common/swap"
ThingSpeakClient = require "thingspeakclient"
log4js = require "log4js"
log4js.configure 'config/log4js_configuration.json', {}
logger = log4js.getLogger(__filename.split("/").pop(-1).split(".")[0])

class ThingspeakSubscriber
    constructor: (@config) ->
        
        logger.info "Starting Thingspeak Subscriber..."
        
        client = new ThingSpeakClient()
        for channelId, channel of config.thingspeak.channels
            client.attachChannel parseInt(channelId), { writeKey: channel.writeApiKey }, (err) ->
                return logger.error err if err?
        
        self = this
        
        @sub = zmq.socket "sub"
        @sub.subscribe ""
		
        @sub.on "message", (data) ->
            logger.info "Received message" 
            data = JSON.parse(data)
            swapPacket = data.swapPacket
            packetDevice = data.packetDevice
            
            res = ""
            if swapPacket.value isnt undefined
                channel = packetDevice.productCode + "/" + packetDevice.address + "/" + swapPacket.regId
                temp = if swapPacket.value.length is undefined then [swapPacket.value] else swapPacket.value
                
                foundRegisters = (register for register in packetDevice.configRegisters when register.id == swapPacket.regId)
                foundRegisters = (register for register in packetDevice.regularRegisters when register.id == swapPacket.regId) unless foundRegisters.length == 1
                if foundRegisters.length == 1
                    foundRegister = foundRegisters[0]
                    for endpoint, i in foundRegister.endpoints
                        
                        thingspeakChannel = self.config.thingspeak.fields[channel + ":" + i]
                        
                        if thingspeakChannel isnt undefined
                            
                            if thingspeakChannel.unit isnt undefined
                                value = (endpoint.units[thingspeakChannel.unit].factor or 1) * endpoint.value + (endpoint.units[thingspeakChannel.unit].offset or 0)
                            else
                                value = endpoint.value
                            
                            value = value.toFixed(2)                            
                            logger.debug "Sending field #{thingspeakChannel.fieldId} with value #{value} (raw value #{endpoint.value}) to channel #{thingspeakChannel.channelId}"
                            
                            data[thingspeakChannel.fieldId] = value
                            data["created_at"] = swapPacket.time
                            client.updateChannel parseInt(thingspeakChannel.channelId), data, (err, body) ->
                                return logger.error err if err?
        

        url = "tcp://#{@config.broker.host}:#{@config.broker.port}"
        logger.info "Connection to broker on #{url}"
        @sub.bind url
        
        logger.info "Thingspeak waiting for message..."
    
    destroy: () ->
        @sub.close
        logger.info "Connection to broker closed"

module.exports = ThingspeakSubscriber

thingspeakSubscriber = new ThingspeakSubscriber Config
