dgram = require "dgram"
logger = require("log4js").getLogger(__filename.split("/").pop(-1).split(".")[0])

class Publisher
  constructor: (@config) ->
    client = dgram.createSocket "udp4"
    client.bind @config.port, ->
      client.setBroadcast true
    url = "udp://*:#{@config.port}"
    @pub = client
    logger.info "Broker UDP Publisher on #{url}"
    
  publish: (topic, data) ->
    logger.debug "Sending data to topic #{topic}..."
    data = JSON.stringify data
    logger.debug "Data #{topic}|#{data.length.toString(16)}|#{data.substr(0, 10)}..."
    buffer = new Buffer topic + "|" + data.length.toString(16) + "|" + data
    self = @pub 
    self.send buffer, 0, buffer.length, @config.port, @config.host, (err, bytes) ->
      self.close
      
  destroy: () ->
    @pub.close
  
module.exports = Publisher