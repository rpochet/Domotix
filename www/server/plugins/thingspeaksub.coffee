Config = require "config"
S = require "string"
http = require "http"
zmq = require "zmq"
swap = require "../../client/code/common/swap"
logger = require("log4js").getLogger(__filename.split("/").pop(-1).split(".")[0])

class ThingspeakSubscriber
    constructor: (@config) ->
        
        logger.info "Starting Thingspeak Subscriber..."
        
        @sub = zmq.socket "sub"
        
        self = this
        
        @sub.subscribe ""
        @sub.on "message", (data) ->
            logger.info "Received message:" 
            logger.debug data
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
                                logger.debug endpoint.units[thingspeakChannel.unit]
                                value = (endpoint.units[thingspeakChannel.unit].factor || 1) * endpoint.value + (endpoint.units[thingspeakChannel.unit].offset || 0)
                            else
                                value = endpoint.value
                            
                            value = value.toFixed(2)                            
                            logger.debug "Sending field #{thingspeakChannel.fieldId} with value #{value} (raw value #{endpoint.value})"
                            
                            options =
                                hostname: self.config.thingspeak.host
                                port: self.config.thingspeak.port
                                path: "/update"
                                method: "POST"
                                headers:
                                    "X-THINGSPEAKAPIKEY": self.config.thingspeak.channels[thingspeakChannel.channelId].writeApiKey
                            
                            req = http.request options, (res) ->
                                res.setEncoding "utf8"
                                res.on "data", (chunk) ->
                                    logger.error "Problem with request!" if chunk == 0
                            
                            req.on "error", (e) ->
                                logger.error "Problem with request: error #{e.message}"
                            
                            req.write thingspeakChannel.fieldId
                            req.write "=" + value
                            req.end()
        
        url = "tcp://#{@config.broker.host}:#{@config.broker.port}"
        logger.info "Connection to broker on #{url}"
        @sub.bind url
        
        logger.info "Thingspeak waiting for message..."
    
    destroy: () ->
        @sub.close
        logger.info "Connection to broker closed"

module.exports = ThingspeakSubscriber

thingspeakSubscriber = new ThingspeakSubscriber Config