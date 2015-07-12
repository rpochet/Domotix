events = require "events"
AMQP = require "amqp-coffee"
dgram = require "dgram"
server = dgram.createSocket "udp4"
client = dgram.createSocket "udp4"
swap = require "../../client/code/common/swap"
logger = require("log4js").getLogger(__filename.split("/").pop(-1).split(".")[0])

class PubSub extends events.EventEmitter
    constructor: (@config) ->
        
        logger.info "Starting broker #{@config.impl}..."
        
        @latest = {}
        self = this
        
        if @config.impl is "amqp"
            url = "amqp://#{@config.amqp.host}:#{@config.amqp.port}#{@config.amqp.vhost}"
            logger.info "Broker AMQP Publisher on #{url}"
            
            connection = new AMQP @config.amqp, (error) ->
                return logger.error "Connection problem: #{error}" if error
                logger.info "Connection OK"
                connection.consume swap.MQ.Type._ALL, 
                    prefetchCount: 1, (message) ->
                        logger.debug "Consume message #{message.data} from #{message.exchange}..."
                        self.emit message.exchange, message.data
                        message.ack()
                    (error, consumer) ->
                        return logger.error "Consumer problem: #{error}" if error
            @pub = connection
        
        else if @config.impl is "udp"
            logger.info "Broker UDP Publisher on I:udp://#{@config.udp.inhost}:#{@config.udp.inport} / O:udp://#{@config.udp.outhost}:#{@config.udp.outport}"
            
            server.on "message", (data, rinfo) ->
                logger.debug "Udp Bridge got: " + data + " from " + rinfo.address + ":" + rinfo.port
                [type, length, message] = data.toString().split "|"
                self.emit type, message
            
            server.bind @config.udp.inport, @config.udp.inhost
            
            client.bind @config.port, ->
                client.setBroadcast true
    
    publish: (topic, data) ->
        logger.debug "Sending data to topic #{topic}..."
        @latest[topic] = data
        
        if @config.impl is "amqp"
            @pub.publish topic, "", data, 
                deliveryMode: 2,
                    (error, t) ->
                        logger.error "Problem to submit message to #{topic}" if error
        
        else if @config.impl is "udp"
            message = JSON.stringify data
            logger.debug message
            buffer = new Buffer topic + "|" + message.length.toString(16) + "|" + message
            client.send buffer, 0, buffer.length, @config.udp.outport, @config.udp.outhost, (error) ->
                logger.error "Sending UDP message failed: #{error}" if error
    
    getLatest: (topic) ->
        if topic
            return @latest[topic]
        else
            return @latest
    
    destroy: () ->
        @publish swap.MQ.Type.MANAGEMENT, "PubSub is closed"
        if @config.impl is "amqp"
            @pub.close
        else if @config.impl is "udp"
            server.close
            client.close
        
        logger.info "PubSub is closed"

module.exports = PubSub
