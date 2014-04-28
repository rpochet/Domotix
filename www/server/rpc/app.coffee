#SerialModem = require '../swap/serialmodem'
SerialModem = require '../swap/dummyserialmodem'
Config = require 'config'
swap = require '../../client/code/common/swap'

serialConfig = Config.serial;
serial = new SerialModem serialConfig

packets = []

serial.on 'started', () ->
    publisher = new ps.Publisher config
    serial.on 'data', (sp) -> 
        ss.api.publish.all 'swapPacket', sp
        packets.splice(0, 0, sp)
        packets.pop() if packets.length > 40

exports.actions = (req, res, ss) ->

    # Easily debug incoming requests here
    console.log(req);

    # Square a number and return the result
    square: (num) ->
        res num * num
    
    # Gets the value of a specific register
    sendQuery: (address, regId) ->
        sp = new swap.SwapPacket()
        sp.source = Config.network.address
        sp.dest = address
        sp.func = swap.Functions.QUERY
        sp.regAddress = address
        sp.regId = regId
        serial.send(sp)

    # Sets the value of a specific register
    sendCommand: (address, regId, value) ->
        sp = new swap.SwapPacket()
        sp.source = Config.network.address
        sp.dest = address
        sp.func = swap.Functions.COMMAND
        sp.regAddress = address
        sp.regId = regId
        sp.value = value
        serial.send(sp)

    sendAlert: () ->
        ss.publish.all 'systemAlert', 'The server is about to be shut down'

