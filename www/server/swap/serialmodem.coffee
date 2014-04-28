util = require 'util'
swap = require '../../client/code/common/swap'
serialport = require 'serialport'
events = require 'events'
logger = require('log4js').getLogger(__filename.split('/').pop(-1).split('.')[0])

###
Handles communication to and from serial port and relay the information
Emits:
  - started: once all info from serial device is obtained
  - data: new packet incoming from swap network
    - event: SwapPacket
###
class SerialModem extends events.EventEmitter
  constructor: (@config) ->
    @syncword = "qds"
    
    @serialPort = new serialport.SerialPort config.port,
      baudrate: config.baudrate || 38400,
      parser: serialport.parsers.readline '\r\n'
    
    self = this
    @serialPort.on "open", ->
      # this is now serialPort
      logger.info "Port " + @readStream.path + " opened"
      @on "data", (data) =>
        logger.debug "Received: #{data}"
        # TODO: D is not necessary since it already starts with '('
        if data[0] is '('
          packet = new swap.CCPacket data[0 .. data.length-1]  # remove \r
          if packet.data
            packet = new swap.SwapPacket packet
            self.emit 'data', packet
    
    @serialPort.on "close", ->
      process.kill(process.pid, 'SIGTERM')
    
  # To send a packet to the Swap network
  send: (packet) ->
    logger.debug "Sent: S#{packet}"
    @serialPort.write "S#{packet}\r"
  
  # To set value on modem config
  command: (str) ->
    logger.debug "Sent: #{str}"
    @serialPort.write str + "\r"
  
  # To check that the modem is still living
  ping: (callback) ->
    @write 'AT\r'
    @once 'data', (data) ->
      if data is not 'OK'
        logger.warn "Error while pinging: #{data}"
      else
        callback() if callback()

module.exports = SerialModem
