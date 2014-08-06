events = require "events"
dgram = require "dgram"
server = dgram.createSocket "udp4"
swap = require "../../client/code/common/swap"

logger = require("log4js").getLogger(__filename.split("/").pop(-1).split(".")[0])

###
Handles communication from local network to serial port
Emits:
  - swapPacket: new packet incoming from local network
    - event: SwapPacket
###
class UdpBridge extends events.EventEmitter
    constructor: (@config) ->
        
        logger.info "Starting Udp Bridge..."
        
        self = this
        server.on "message", (data, rinfo) ->
            logger.info "Udp Bridge got: " + data + " from " + rinfo.address + ":" + rinfo.port 
            packet = new swap.CCPacket ("(FFFF)" + data)
            if packet.data
                packet = new swap.SwapPacket packet
                self.emit "swapPacket", packet
        
        server.bind @config.port, @config.host
        
        logger.info "Listening on UDP port #{config.port}"
        
        logger.info "Udp Bridge waiting for message..."
    
    destroy: () ->
        @sub.close
        console.log "Udp Bridge closed"

module.exports = UdpBridge
