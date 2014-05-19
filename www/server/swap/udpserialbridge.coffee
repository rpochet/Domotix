events = require 'events'
dgram = require 'dgram'
server = dgram.createSocket 'udp4'
swap = require '../../client/code/common/swap'

logger = require('log4js').getLogger(__filename.split('/').pop(-1).split('.')[0])

class UdpSerialBridge extends events.EventEmitter
    constructor: (@config) ->

        logger.info 'Starting UdpSerial Bridge...'
        
        self = this
        server.on 'message', (data, rinfo) ->
            logger.info 'UdpSerial Bridge got: ' + data + ' from ' + rinfo.address + ':' + rinfo.port 
            packet = new swap.CCPacket ('(FFFF)' + data)
            if packet.data
                packet = new swap.SwapPacket packet
                self.emit 'swapMessage',
                    packet: packet,
                    time: new Date()

        server.bind @config.port, @config.host

        logger.info 'Listening on UDP port #{config.port}'

        logger.info 'UdpSerial Bridge waiting for message...'

    destroy: () ->
        @sub.close
        console.log 'UdpSerial Bridge closed'

module.exports = UdpSerialBridge
