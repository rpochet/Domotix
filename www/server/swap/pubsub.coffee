zmq = require "zmq"
logger = require("log4js").getLogger(__filename.split("/").pop(-1).split(".")[0])

class Publisher
    constructor: (@config) ->
        @pub = zmq.socket "pub"
        url = "tcp://#{@config.host}:#{@config.port}"
        logger.info "Broker Publisher on #{url}"
        @pub.connect url
    
    publish: (data) ->
        logger.debug "Sending data..."
        @pub.send data

module.exports = Publisher
