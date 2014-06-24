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
        
        # When a devicesUpdated event is received, update devices
        $scope.$on 'devicesUpdated', (e) ->
            ss.rpc 'swapserver.getDevices', (devices) ->
                console.log devices
                $scope.devices = devices
                
        $scope.sendMessage = () ->
            if $scope.message.functionCode == swap.Functions.COMMAND
                ss.rpc 'swapserver.sendCommand', $scope.message.address, $scope.message.register.id, swap.getValue $scope.message.register.value, $scope.message.register.length
            else
                ss.rpc 'swapserver.sendQuery', $scope.message.address, $scope.message.register.id
                
        $scope.checkNewDevices = () ->
            ss.rpc 'swapserver.sendQuery', swap.Address.BROADCAST, swap.Registers.productCode.id
    ]
    
    swapApp.controller 'DeviceListCtrl', ['$scope', 'rpc', 'pubsub', ($scope, rpc, pubsub) ->
        
        $scope.config = undefined
        $scope.devices = []
        
        $scope.selectedDevice = undefined
        $scope.editedDevice = undefined;
        
        $scope.update = () ->
            $scope.selectedDevice = angular.copy $scope.editedDevice
            ss.rpc 'swapserver.updateDevice', $scope.selectedDevice if $scope.selectedDevice
        
        $scope.reset = () ->
            $scope.editedDevice = angular.copy $scope.selectedDevice
        
        $scope.isUnchanged = () ->
            return angular.equals $scope.editedDevice, $scope.selectedDevice
        
        $scope.selectDevice = (device) ->
            $scope.selectedDevice = device
            $scope.reset()
        
        ss.server.on 'ready', () ->
            ss.rpc 'swapserver.getDevices', (devices) ->
                $scope.devices = devices
            ss.rpc 'swapserver.getConfig', (config) ->
                $scope.config = config
        
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
