events = require 'events'
logger = require('log4js').getLogger(__filename.split('/').pop(-1).split('.')[0])

###
Handles communication to and from serial port and relay the information
Emits:
  - started: once all info from serial device is obtained
###
class SerialModem extends events.EventEmitter
    
    constructor: (@config) ->
        logger.info "Starting serial modem"
    
    # To send a packet to the Swap network
    send: (packet) ->
        logger.debug packet
        logger.debug "Sent: S#{packet}"
    
    # To set value on modem config
    command: (str) ->
        logger.debug "Sent: #{str}"
    
    # To check that the modem is still living
    ping: (callback) ->
        @write 'AT\r'
        @once 'data', (data) ->
            if data is not 'OK'
                logger.warn "Error while pinging: #{data}"
            else
                callback() if callback()
    
    start: ->
        @emit "started"
    
module.exports = SerialModem
