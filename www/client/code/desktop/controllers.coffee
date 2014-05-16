# AngularJS controllers

swap = require '/swap'

module.exports = (swapApp) ->
    'use strict';
    
    swapApp.controller 'UtilitiesCtrl', ['$scope', 'rpc', ($scope, rpc) ->
        $scope.registers = swap.Registers
        $scope.functions = swap.Functions
        $scope.message =
            valueLength: 1
            address: 2
            functionCode: swap.Functions.QUERY
            register: 
                id: swap.Registers.txInterval.id + 1
        
        # When a serial packet is received
        $scope.$on 'swapPacket', (e, sp) ->
            $scope.packets.splice(0, 0, sp)
            $scope.packets.pop() if $scope.packets.length > 40
        
        $scope.refreshDevices = () ->
            ss.rpc 'swapserver.refreshDevices'
        
        # When a devicesUpdated event is received, update devices
        $scope.$on 'devicesUpdated', (e) ->
            ss.rpc 'swapserver.getDevices', (devices) ->
                console.log devices
                $scope.devices = devices
                
        $scope.sendMessage = () ->
            if $scope.message.functionCode == swap.Functions.COMMAND
                ss.rpc 'swapserver.sendCommand', $scope.message.address, $scope.message.register.id, swap.getValue $scope.message.value, $scope.message.valueLength
            else
                ss.rpc 'swapserver.sendQuery', $scope.message.address, $scope.message.register.id
                
        $scope.checkNewDevices = () ->
            ss.rpc 'swapserver.sendQuery', swap.Address.BROADCAST, swap.Registers.productCode.id
    ]
    
    swapApp.controller 'DeviceListCtrl', ['$scope', 'rpc', 'pubsub', '$dialog', ($scope, rpc, pubsub, $dialog) ->
        $scope.packets = []
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
            
            ss.rpc 'swapserver.getPackets', (packets) ->
                $scope.packets = packets
        
        # When a serial packet is received
        $scope.$on 'swapPacket', (e, sp) ->
            $scope.packets.splice(0, 0, sp)
            $scope.packets.pop() if $scope.packets.length > 40
        
        # When a devicesUpdated event is received, update devices
        $scope.$on 'devicesUpdated', (e) ->
            ss.rpc 'swapserver.getDevices', (devices) ->
                $scope.devices = devices
        
        $scope.deleteSelectedDevice = () ->
            ss.rpc 'swapserver.deleteDevice', $scope.selectedDevice.address if $scope.selectedDevice
        
        $scope.noSee = (device) ->
            moment().diff(moment(device.lastStatusTime)) / 1000 > 2 * device.txInterval
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

