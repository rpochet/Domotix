zmq = require "zmq"
logger = require("log4js").getLogger(__filename.split("/").pop(-1).split(".")[0])

class Publisher
  constructor: (@config) ->
    @pub = zmq.socket "pub"
    url = "tcp://#{@config.host}:#{@config.port}"
    @pub.bind url, (err) ->
      if err
        logger.error err
      else
        logger.info "Broker Publisher on #{url}"
        
  publish: (data) ->
    logger.debug "Sending data..."
    @pub.send data

  destroy: () ->
    @pub.close
  
module.exports = Publisher
