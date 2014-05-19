S = require 'string'
http = require 'http'
zmq = require 'zmq'
logger = require('log4js').getLogger(__filename.split('/').pop(-1).split('.')[0])

class ThingspeakSubscriber
    constructor: (@config) ->

        logger.info 'Starting Thingspeak Subscriber...'
        
        @sub = zmq.socket 'sub'

        self = this
        
        @sub.on 'message', (type, data) ->
            logger.info 'Received message: ' + type + ' with data ' + data
            #logger.debug JSON.parse(data)
            
            thingspeakChannel = self.config.thingspeak.channels[type]
            
            if thingspeakChannel
                options =
                    hostname: self.config.thingspeak.host
                    port: 80
                    path: '/update'
                    method: 'POST'
                    headers:
                        'X-THINGSPEAKAPIKEY': thingspeakChannel.writeApiKey
                
                req = http.request options, (res) ->
                    res.setEncoding 'utf8'
                    res.on 'data', (chunk) ->
                        logger.error 'Problem with request: ' if chunk == 0
                
                req.on 'error', (e) ->
                    logger.error 'Problem with request: ' + e.message
                
                req.write(thingspeakChannel.fieldId)
                req.write('=')
                req.write(JSON.parse(data).value+'')
                req.end()
               
        url = "tcp://#{@config.broker.host}:#{@config.broker.port}"
        logger.info "Connection to broker on #{url}"
        @sub.connect url
        @sub.subscribe ''

        logger.info 'Thingspeak waiting for message...'

    destroy: () ->
        @sub.close
        logger.info 'Connection to broker closed'

module.exports = ThingspeakSubscriber
