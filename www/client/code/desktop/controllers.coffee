# AngularJS controllers

swap = require '/swap'

module.exports = (swapApp) ->
  'use strict';
  
  swapApp.controller 'UtilitiesCtrl', ['$scope', 'rpc', ($scope, rpc) ->
    $scope.registers = swap.Registers
    $scope.functions = swap.Functions
    $scope.message =
      address: 2
      functionCode: swap.Functions.QUERY
      register: 
        id: swap.Registers.txInterval.id + 1
        length: 1
    
    $scope.refreshDevices = () ->
      ss.rpc 'swapserver.refreshDevices'
    
    $scope.refreshSwapPackets = () ->
      ss.rpc 'swapserver.refreshSwapPackets'
    
    # When a devicesUpdated event is received, update devices
    $scope.$on 'devicesUpdated', (e) ->
      ss.rpc 'swapserver.getDevices', (devices) ->
        $scope.devices = devices
    
    $scope.sendMessage = () ->
      if $scope.message.functionCode == swap.Functions.COMMAND
        ss.rpc 'swapserver.sendSwapCommand', $scope.message.address, $scope.message.register.id, swap.getValue $scope.message.register.value || $scope.message.register.valueStr, $scope.message.register.length
      else if $scope.message.functionCode == swap.Functions.QUERY
        ss.rpc 'swapserver.sendSwapQuery', $scope.message.address, $scope.message.register.id
      else
        ss.rpc 'swapserver.sendSwapPacket', $scope.message.functionCode, $scope.message.address, $scope.message.register.id, swap.getValue $scope.message.register.value || $scope.message.register.valueStr, $scope.message.register.length
      
    $scope.checkNewDevices = () ->
      ss.rpc 'swapserver.sendSwapQuery', swap.Address.BROADCAST, swap.Registers.productCode.id
    
    $scope.rawMessage = ""
    $scope.$watch 'rawMessage', (newValue, oldValue) ->
      data = if newValue.charAt(0) == '(' then newValue else "(0000)" + newValue
      ccPacket = new swap.CCPacket data
      swapPacket = new swap.SwapPacket ccPacket
        
      $scope.decodedMessage = 
        rssi : swapPacket.RSSI 
        lqi : swapPacket.LQI
        dest : swapPacket.dest
        source : swapPacket.source
        nonce : swapPacket.nonce
        regId : swapPacket.regId
        value : swapPacket.value
        
      for key, value of swap.Functions
        $scope.decodedMessage.func = key if value == swapPacket.func
  ]
  
  swapApp.controller 'DeviceListCtrl', ['$scope', 'rpc', ($scope, rpc) ->
    
    $scope.config = undefined
    $scope.devices = []
    
    $scope.selectedDevice = undefined
    $scope.editedDevice = undefined
    
    $scope.update = () ->
      $scope.selectedDevice = angular.copy $scope.editedDevice
      ss.rpc 'swapserver.updateDevice', $scope.editedDevice, $scope.selectedDevice if $scope.selectedDevice
    
    $scope.reset = () ->
      $scope.editedDevice = angular.copy $scope.selectedDevice
    
    $scope.isUnchanged = () ->
      return angular.equals $scope.editedDevice, $scope.selectedDevice
    
    $scope.selectDevice = (device) ->
      $scope.selectedDevice = device
      $scope.reset()
    
    $scope.registerPartValue = (registerPart, registerValue) ->
      return registerValue.slice(parseInt(registerPart.position), (parseInt(registerPart.position) + parseInt(registerPart.size)))
    
    ss.server.on 'ready', () ->
      ss.rpc 'swapserver.getDevices', (devices) ->
        $scope.devices = devices
      ss.rpc 'swapserver.getConfig', (config) ->
        $scope.config = config
      ss.rpc 'swapserver.getLevels', (levels) ->
        $scope.levels = levels
    
    # When a devicesUpdated event is received, update devices
    $scope.$on 'devicesUpdated', (e) ->
      ss.rpc 'swapserver.getDevices', (devices) ->
        $scope.devices = devices
    
    $scope.delete = () ->
      ss.rpc 'swapserver.deleteDevice', $scope.selectedDevice.address if $scope.selectedDevice
    
    $scope.noSee = (device) ->
      moment().diff(moment(device.lastStatusTime)) / 1000 > 2 * device.txInterval
  ]
  
  swapApp.controller 'NetworkCtrl', ['$scope', 'rpc', ($scope, rpc) ->
    
    $scope.swapPackets = []
    $scope.swapEvents = []
    
    ss.server.on 'ready', () ->
      ss.rpc 'swapserver.getSwapPackets', (swapPackets) ->
        $scope.swapPackets = swapPackets
      ss.rpc 'swapserver.getSwapEvents', (swapEvents) ->
        $scope.swapEvents = swapEvents
    
    # When a serial packet is received
    $scope.$on 'swapPacket', (e, sp) ->
      $scope.swapPackets.splice(0, 0, sp)
      $scope.swapPackets.pop() if $scope.swapPackets.length > 40
    
    # When a swap event is received
    $scope.$on 'swapEvent', (e, se) ->
      $scope.swapEvents.splice(0, 0, se)
      $scope.swapEvents.pop() if $scope.swapEvents.length > 40
  ]
  
  swapApp.controller 'DomotixCtrl', ['$scope', 'rpc', ($scope, rpc) ->

    $scope.levels = undefined
    
    $scope.handleSvgClick = ($event, level) ->
      x = $event.offsetX;
      y = $event.offsetY;
      console.log 'Click on ' + x + ', ' + y;
      for room in level.rooms
        do (room) ->
          for light in room.lights
            do (light) ->
              pos = $scope.lightPosition room, light
              if (x - pos[0]) * (x - pos[0]) + (y - pos[1]) * (y - pos[1]) < 2500
                # Click on light in room
                ss.rpc 'swapserver.sendSwapPacket', swap.Light.Functions.Light, light.swapDeviceAddress, swap.Light.Registers.Outputs.id, [light.outputNb, swap.Light.Values.Toggle]
            
    $scope.lightPosition = (room, light) ->
      #dx and dy should be used for custom position in handeld device in order to avoid cross light tapping
      #return [room.x + light.location.x + (light.location.dx || 0), room.y + light.location.y + (light.location.dy || 0)];
      return [room.x + light.location.x, room.y + light.location.y];
    
    $scope.lightDef = (room, light) ->
      if light.status == 0
        return '#r1';
      else 
        return '#r2';
    
    $scope.lightFill = (room, light) ->
      if light.status == 0
        return 'url(#g1)';
      else 
        return 'url(#g2)';
    
    ss.event.on 'lightStatusUpdated2', (lightStatus) ->
      ss.rpc 'swapserver.getLevels', (levels) ->
        $scope.levels = levels
    
    ss.event.on 'lightStatusUpdated', (lightStatus) ->
      for level in $scope.levels
        do (level) ->
          for room in level.rooms
            do (room) ->
              for light in room.lights
                do (light) ->
                  for lightnew in lightStatus.lights
                    do (lightnew) ->
                      if lightnew.swapDeviceAddress == light.swapDeviceAddress && lightnew.outputNb == light.outputNb 
                        light.status = lightnew.status
    
    ss.server.on 'ready', () ->
      ss.rpc 'swapserver.getLevels', (levels) ->
        $scope.levels = levels
  ]
  
  swapApp.controller 'ConfigCtrl', ['$scope', 'rpc', ($scope, rpc) ->
    $scope.config = undefined
    $scope.editedConfig = undefined
    
    $scope.update = () ->
      $scope.config = angular.copy $scope.editedConfig
      ss.rpc 'swapserver.updateConfig', $scope.config if $scope.config
    
    $scope.reset = () ->
      $scope.editedConfig = angular.copy $scope.config
    
    $scope.isUnchanged = () ->
      return angular.equals $scope.editedConfig, $scope.config
    
    ss.server.on 'ready', () ->
      ss.rpc 'swapserver.getConfig', (config) ->
        console.log config
        $scope.config = config
        $scope.reset()
  ]
  
