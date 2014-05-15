# AngularJS controllers

module.exports = (swapApp) ->
    'use strict';
    
    swapApp.controller 'MessageCtrl', ['$scope', 'rpc', ($scope, rpc) ->
        $scope.registers = []
        $scope.functions = []
        $scope.message =
            valueLength: 1
        
        ss.server.on 'ready', () ->
            ss.rpc 'swapserver.getDefine', (define) ->
                for code, value of define.registers
                    $scope.registers.push
                        "name": code
                        "id": value.id
                        "length": value.length
                for code, value of define.functions
                    $scope.functions.push
                        "name": code
                        "value": value
                $scope.message.functionCode = $scope.functions[0]
                $scope.message.registerId = 11
        
        # When a serial packet is received
        $scope.$on 'swapPacket', (e, sp) ->
            $scope.packets.splice(0, 0, sp)
            $scope.packets.pop() if $scope.packets.length > 40
        
        # When a devicesUpdated event is received, update devices
        $scope.$on 'devicesUpdated', (e) ->
            ss.rpc 'swapserver.getDevices', (devices) ->
                console.log devices
                $scope.devices = devices
                
        $scope.sendMessage = () ->
            ss.rpc 'swapserver.sendMessage', $scope.message
        
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
        
        $scope.refreshDevices = () ->
            ss.rpc 'swapserver.refreshDevices'
        
        $scope.deleteSelectedDevice = () ->
            ss.rpc 'swapserver.deleteDevice', $scope.selectedDevice.address if $scope.selectedDevice
        
        $scope.noSee = (device) ->
            moment().diff(moment(device.lastStatusTime)) / 1000 > 2 * device.txInterval
        
    ]
    
    swapApp.controller 'ConfigCtrl', ['$scope', 'rpc', '$dialog', ($scope, rpc, $dialog) ->
        $scope.config = {}
        
        ss.server.on 'ready', () ->
            ss.rpc 'swapserver.getConfig', (config) ->
                $scope.config = config
        
        $scope.refreshDevices = () ->
            ss.rpc 'swapserver.refreshDevices'
        
        $scope.openConfig = () ->
            $dialog.dialog().open('config.html', 'ConfigCtrl')
        
        $scope.close = (res) ->
            if not res
                $dialog.dialog().close()
            else
                console.log "Saving config"
                ss.rpc 'swapserver.saveConfig', $scope.config, (res) ->
                    dialog.close() if not res
                    #         rpc.exec('swapserver.saveConfig', $scope.config).then (err) ->
                    #             console.log err if err
    ]

