Config = require "config"
S = require "string"
zmq = require "zmq"
dgram = require "dgram"
client = dgram.createSocket "udp4"
swap = require "../../client/code/common/swap"
log4js = require "log4js"
log4js.configure 'config/log4js_configuration.json', {}
logger = log4js.getLogger(__filename.split("/").pop(-1).split(".")[0])

class UdpBridgeSubscriber
    constructor: (@config) ->
        
        logger.info "Starting UdpBridge Subscriber..."
        
        self = this
        
        @sub = zmq.socket "sub"
        @sub.subscribe ""
        @sub.on "message", (data) ->
            logger.info "UdpBridgeSubscriber Received message:" 
            client.send data, 0, data.length, self.config.outport, self.config.host, (err, bytes) ->
            client close
        
        url = "tcp://#{@config.broker.host}:#{@config.broker.port}"
        logger.info "Connection to broker on #{url}"
        @sub.bind url
        
        logger.info "UDP Bridge waiting for message..."
    
    destroy: () ->
        @sub.close
        logger.info "Connection to broker closed"

module.exports = UdpBridgeSubscriber
