zmq = require "zmq"
faye = require "faye"
amqp = require "amqp"
dgram = require "dgram"
logger = require("log4js").getLogger(__filename.split("/").pop(-1).split(".")[0])

class Publisher
  constructor: (@config) ->
    if @config.type == "zmq"
      @pub = zmq.socket "pub"
      url = "tcp://#{@config.host}:#{@config.port}"
      @pub.bind url, (err) ->
        if err
          logger.error err
        else
          logger.info "Broker Zmq Publisher on #{url}"
    else if @config.type == "faye"
      url = "http://#{@config.host}:#{@config.port}/#{@config.path}"
      @pub = new faye.Client url
      logger.info "Broker Faye Publisher on #{url}"
    else if @config.type == "udp"
      client = dgram.createSocket "udp4"
      client.bind @config.port, ->
        client.setBroadcast true
      url = "udp://*:#{@config.port}"
      @pub = client
      logger.info "Broker UDP Publisher on #{url}"
    else if @config.type == "rabbitmq"
      @pub = amqp.createConnection
        host: @config.host
        port: @config.port
      @pub.on "ready", () ->
        logger.info "Broker RabbitMQ Publisher on #{@config.host}:#{@config.port}" 

  publish: (topic, data) ->
    logger.debug "Sending data to topic #{topic}..."
    if @config.type == "zmq"
      @pub.send [
        topic, 
        JSON.stringify data
      ]
    else if @config.type == "faye"
      publication = @pub.publish "/" + topic, data
    else if @config.type == "rabbitmq"
      @pub.publish topic, JSON.stringify data
    else if @config.type == "udp"
      data = JSON.stringify data
      type = 0
      logger.debug "Data #{type}|#{data.length.toString(16)}|#{data.substr(0, 10)}..."
      buffer = new Buffer type + "|" + data.length.toString(16) + "|" + data
      self = @pub 
      self.send buffer, 0, buffer.length, @config.port, @config.host, (err, bytes) ->
        self.close
      
  destroy: () ->
    if @config.type == "zmq"
      @pub.close
    if @config.type == "udp"
      @pub.close
  
module.exports = Publisher