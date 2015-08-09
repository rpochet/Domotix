
class CCPacket
    constructor: (strPacket) ->
        unless (strPacket.length) % 2 is 0
            throw new error "Packet length must be even: " + strPacket.length
            return
        @RSSI = parseInt(strPacket.slice(1, 3), 16)
        @LQI = parseInt(strPacket.slice(3, 5), 16)
        @data = []
        i = 6
        while i < strPacket.length
            @data.push parseInt(strPacket.slice(i, i + 2), 16)
            i += 2
        @time = new Date()

class SwapPacket
    constructor: (ccPacket) ->
        # http://www.ti.com/lit/an/swra114d/swra114d.pdf
        @RSSI = if ccPacket?.RSSI >= 128 then ((ccPacket?.RSSI - 256) / 2 - 74) else (ccPacket?.RSSI / 2 - 74)
        @LQI = ccPacket?.LQI
        @dest = ccPacket?.data[0]
        @source = ccPacket?.data[1]
        @hop = ccPacket?.data[2] >> 4 and 0x0F
        @security = ccPacket?.data[2] or 0 and 0x0F
        @nonce = ccPacket?.data[3] or 0
        @func = ccPacket?.data[4]
        @regAddress = ccPacket?.data[5]
        @regId = ccPacket?.data[6]
        @value = ccPacket?.data.slice(7, ccPacket?.data.length)
        @time = ccPacket?.time

    toString: () ->
        res = (num2byte(i) for i in [@dest, @source]).join('')
        res += @hop.toString(16) + @security.toString(16)   
        res += (num2byte(i) for i in [@nonce, @func, @regAddress, @regId]).join('')
        if @value isnt undefined
            temp = if @value.length is undefined then [@value] else @value
            res += (num2byte(i) for i in temp).join('')
        return res

# Utility function
# Convert an int into a hexadecimal byte as a string
num2byte = (number) ->
    ('00' + number?.toString(16)).slice(-2)

bytePad = (byte, number) ->
    (('00' for a in [0..number+1]).join('') + byte).slice(-(2*number))

getValue = (value, length) ->
    #TODO: transform this to implement to parsing of a value in byte array
    #TODO: handle string values here, and others
    if (typeof value is 'string' || value instanceof String)
        results = []
        for i in [0..value.length / 2 - 1]
            do(i) ->
                b = value.substr i * 2, 2
                results.push parseInt b, 16
                return
        #parseInt value.substr i * 2, 2 for i in [0..length/2]
        return results
    else
        (value >> 8 * i) & 255 for i in [length-1..0]

getTemperature = (swapPacket) ->
    regiser = swapPacket.value
    return register.value.value[0] * 256 + register.value.value[1]

getPressure = (swapPacket) ->
    regiser = swapPacket.value
    return "N.A" if register.value.value.length != 6
    return register.value.value[2] * 256 * 256 * 256 + register.value.value[3] * 256 * 256 + register.value.value[4] * 256 + register.value.value[5]

Address =
    BROADCAST: 255

Functions =
    STATUS: 0
    QUERY: 1
    COMMAND: 2
    CUSTOM_1: 3
    CUSTOM_2: 4
    CUSTOM_3: 5

Registers =
    productCode: {id: 0, length: 8}
    hardwareVersion: {id: 1, length: 4}
    firmwareVersion: {id: 2, length: 4}
    state: {id: 3, length: 1}
    channel: {id: 4, length: 1} 
    security: {id: 5, length: 1}
    password: {id: 6, length: undefined}
    nonce: {id: 7, length: 1}
    network: {id: 8, length: 2}
    address: {id: 9, length: 1}
    txInterval: {id: 10, length: -1}
    CUSTOM_1: {id: 11, length: -1}
    CUSTOM_2: {id: 12, length: -1}
    CUSTOM_3: {id: 13, length: -1}
    CUSTOM_4: {id: 14, length: -1}
    CUSTOM_5: {id: 15, length: -1}

SwapStates =
    RESTART:
        level: 0
        str: "Restart"

    RXON:
        level: 1
        str: "Radio On"

    RXOFF:
        level: 2
        str: "Radio Off"

    SYNC:
        level: 3
        str: "Sync mode"

    LOWBAT:
        level: 4
        str: "Low battery"

    get: (val) ->
        [@RESTART, @RXON, @RXOFF, @SYNC, @LOWBAT][val]

LightController = 
    productCode: "0000006400000001"
    Functions:
        Light: Functions.CUSTOM_1
        Reset: Functions.CUSTOM_2
    Registers:
        PressureTemperature: Registers.CUSTOM_2
        Outputs: Registers.CUSTOM_4
    Values:
        On: 254
        Off: 0
        Toggle: 255

LightSwitch = 
    productCode: "0000006400000002"
    Registers:
        Voltage: Registers.CUSTOM_1
        Temperature: Registers.CUSTOM_3
        
MQ =
    Type:
        MANAGEMENT: "MANAGEMENT"
        SWAP_PACKET: "SWAP_PACKET"
        SWAP_DEVICE: "SWAP_DEVICE"
        LIGHT_STATUS: "LIGHT_STATUS"
        TEMPERATURE: "TEMPERATURE"
        PRESSURE: "PRESSURE"
        _ALL: "_ALL"
        
module.exports =
    CCPacket: CCPacket
    SwapPacket: SwapPacket
    Address: Address
    Functions: Functions
    Registers: Registers
    SwapStates: SwapStates
    LightController: LightController
    LightSwitch: LightSwitch
    MQ: MQ
    bytePad : bytePad
    num2byte : num2byte
    getValue: getValue
