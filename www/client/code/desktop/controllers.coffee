# AngularJS controllers

swap = require '/swap'

module.exports = (swapApp) ->
  'use strict';
  
  swapApp.controller 'EventsCtrl', ['$scope', 'rpc', 'ngToast', ($scope, rpc, ngToast) ->
    $scope.eventTypes = [
      {
        name: 'EAU'
        description: 'Valeur du compteur en m3'
        label: 'Eau'
        unit: 'm3'
      }
      {
        name: 'ELEC'
        description: 'Choisir un sous type'
        label: 'Electricite'
        subTypes: [
          {
            name: 'HP'
            description: 'Valeur du compteur heure pleine. Arrondir à l\'unité'
            label: 'Heure pleine'
            unit: 'kWh'
          }
          {
            name: 'HC'
            description: 'Valeur du compteur heure creuse. Arrondir à l\'unité'
            label: 'Heure creuse'
            unit: 'kWh'
          }
        ]
      }
    ]

    $scope.$watch 'eventType', (newValue, oldValue) ->
      $scope.eventSubType = undefined
    
    $scope.createEvent = () ->
      eventData = 
        type : $scope.eventType.name
        detail : $scope.eventDetail
        value : $scope.eventValue
        unit : $scope.eventType.unit
      eventData.subtype = $scope.eventSubType.name if $scope.eventSubType
      eventData.unit = $scope.eventSubType.unit if $scope.eventSubType
      
      ss.rpc 'swapserver.createEvent', eventData, (err, res) ->
        if err
          ngToast.create 
            content: 'Event not created'
            className: 'danger'
        else
          ngToast.create 'Event created' if not err
  ]
  
  swapApp.controller 'AdminCtrl', ['$scope', 'rpc', ($scope, rpc) ->
    $scope.couchDbView = 'packet_event'
    $scope.cleanByView = () ->
      ss.rpc 'swapserver.cleanByView', $scope.couchDbView

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
    
    $scope.refreshSwapPacketsEvents = () ->
      ss.rpc 'swapserver.refreshSwapPackets'
    
    $scope.refreshState = () ->
      ss.rpc 'swapserver.refreshState'
    
    ss.event.on 'devicesUpdated', () ->
      ss.rpc 'swapserver.getDevices', (devices) ->
        $scope.$apply () ->
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
    
    $scope.decodeMessage = (rawMessage) ->
      data = if rawMessage.charAt(0) == '(' then rawMessage else "(0000)" + rawMessage
      ccPacket = new swap.CCPacket data
      swapPacket = new swap.SwapPacket ccPacket
      
      $scope.decodedMessage = 
        rssi : swapPacket.RSSI 
        lqi : swapPacket.LQI
        dest : swapPacket.dest
        source : swapPacket.source
        nonce : swapPacket.nonce
        regAddress: swapPacket.regAddress
        regId : swapPacket.regId
        value : swapPacket.value
      for key, value of swap.Functions
        $scope.decodedMessage.func = key if value == swapPacket.func
  ]
  
  swapApp.controller 'DevicesCtrl', ['$scope', 'rpc', ($scope, rpc) ->
    
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
    
    ss.event.on 'devicesUpdated', () ->
      ss.rpc 'swapserver.getDevices', (devices) ->
        $scope.$apply () ->
          $scope.devices = devices
    
    $scope.delete = () ->
      ss.rpc 'swapserver.deleteDevice', $scope.selectedDevice.address if $scope.selectedDevice
    
    $scope.noSee = (device) ->
      moment().diff(moment(device.lastStatusTime)) / 1000 > 2 * device.txInterval
  ]
  
  swapApp.controller 'NetworkCtrl', ['$scope', '$http', 'rpc', ($scope, $http, rpc) ->
    
    minSource = 2
    maxSource = 4
    
    formatDate = d3.time.format "%Y-%m-%d %H:%M:%S"
    formatUpdateDate = d3.time.format "%Y-%m-%d %H:%M:%S"
    
    ######################################
    # QUALITY RSSI/LQI
    
    defaultMinRSSI = -120
    defaultMaxRSSI = 120
    defaultMinLQI = 0
    defaultMaxLQI = 120
    
    $scope.quality =
      zoomRSSI: false
      zoomLQI: false
      offsetData: 0
      nbData: 100
      rowsHeader: []
      nbGraph: maxSource - minSource + 1
      options:
        bindto: '#chart-quality'
        data:
          xs: {}
          columns: []
          axes: {}
        axis:
          x:
            type: 'timeseries'
            tick:
              format: (x) ->
                return d3.time.format('%m-%d %H:%M')(x)
          y:
            show: true
            label:
              text: 'RSSI'
              position: 'outer-middle'
            min: defaultMinRSSI
            max: defaultMaxRSSI
          y2:
            show: true
            label:
              text: 'LQI'
              position: 'outer-middle'
            min: defaultMinLQI
            max: defaultMaxLQI
        
    s = minSource
    while s <= maxSource
      $scope.quality.options.data.xs['RSSI-' + s] = 'x-' + s
      $scope.quality.options.data.xs['LQI-' + s] = 'x-' + s
      
      # data.columns
      $scope.quality.options.data.columns[0 + $scope.quality.nbGraph * (s - minSource)] = ['x-' + s]
      $scope.quality.options.data.columns[1 + $scope.quality.nbGraph * (s - minSource)] = ['RSSI-' + s]
      $scope.quality.options.data.columns[2 + $scope.quality.nbGraph * (s - minSource)] = ['LQI-' + s]
      
      $scope.quality.options.data.axes['LQI-' + s] = 'y2'
      s++
    
    $scope.createQualityGraph = () ->
      $scope.quality.chart = c3.generate $scope.quality.options
    
    loadQuality = () ->
      $http.get 'http://192.168.1.2:5984/panstamp_packets/_design/domotix/_view/network_status',
        params:
          skip: $scope.quality.offsetData
          limit: $scope.quality.nbData
          descending: true
      .success (data) ->
        # Remove previous data but header
        angular.forEach $scope.quality.options.data.columns, (column) ->
          column.splice 1
          
        angular.forEach data.rows, (row, idx) ->
          value = row.value
          source = parseInt value.source
          if(source > 1 && source != 255) # do not handle modem packets and new device
            $scope.quality.options.data.columns[0 + $scope.quality.nbGraph * (source - minSource)].push d3.time.format.iso.parse value.time
            $scope.quality.options.data.columns[1 + $scope.quality.nbGraph * (source - minSource)].push value.RSSI
            $scope.quality.options.data.columns[2 + $scope.quality.nbGraph * (source - minSource)].push value.LQI
        
        #$scope.quality.minRSSI = d3.min $scope.quality.options.data.columns[1], (d, idx) ->
        #  if idx > 0 then d else $scope.quality.defaultMaxRSSI
        # 
        #$scope.quality.maxRSSI = d3.max $scope.quality.options.data.columns[1], (d, idx) ->
        #  if idx > 0 then d else $scope.quality.defaultMinRSSI
        #
        #$scope.quality.minLQI = d3.min $scope.quality.options.data.columns[2], (d, idx) ->
        #  if idx > 0 then d else $scope.quality.defaultMaxLQI
        #
        #$scope.quality.maxLQI = d3.max $scope.quality.options.data.columns[2], (d, idx) ->
        #  if idx > 0 then d else $scope.quality.defaultMinLQI
        #
        $scope.quality.chart.load
          columns: $scope.quality.options.data.columns
    
    $scope.refreshQuality = () ->
      loadQuality()
    
    $scope.previousQuality = () ->
      $scope.quality.offsetData = $scope.quality.offsetData + $scope.quality.nbData
      $scope.refreshQuality()
      
    $scope.nextQuality = () ->
      return if $scope.quality.offsetData <= 0
      $scope.quality.offsetData = $scope.quality.offsetData - $scope.quality.nbData
      $scope.refreshQuality()
      
    
    ######################################
    # QUALITY NONCE
    
    $scope.nonce =
      offsetData: 0
      nbData: 100
      rowsHeader: []
      nbGraph: 2
      options:
        bindto: '#chart-nonce'
        data:
          xs: {}
          columns: []
          axes: {}
        axis:
          x:
            type: 'timeseries'
            tick:
              format: (x) ->
                return d3.time.format('%m-%d %H:%M')(x)
          y:
            show: true
            label:
              text: 'Nonce'
              position: 'outer-middle'
        
    s = minSource
    while s <= maxSource
      $scope.nonce.options.data.xs['N-' + s] = 'x-' + s
      
      # data.columns
      $scope.nonce.options.data.columns[0 + $scope.nonce.nbGraph * (s - minSource)] = ['x-' + s]
      $scope.nonce.options.data.columns[1 + $scope.nonce.nbGraph * (s - minSource)] = ['N-' + s]
      
      s++
      
    $scope.createNonceGraph = () ->
      $scope.nonce.chart = c3.generate $scope.nonce.options
      
    loadNonce = (addr) ->
      $http.get 'http://192.168.1.2:5984/panstamp_packets/_design/domotix/_view/nonce_' + addr,
        params:
          skip: $scope.nonce.offsetData
          limit: $scope.nonce.nbData
          descending: true
      .success (data) ->
        # Remove previous data but header
        angular.forEach $scope.nonce.options.data.columns, (column) ->
          column.splice 1
          
        angular.forEach data.rows, (row, idx) ->
          console.log d3.time.format.iso.parse row.key
          $scope.nonce.options.data.columns[0 + $scope.nonce.nbGraph * (addr - minSource)].push d3.time.format.iso.parse row.key
          $scope.nonce.options.data.columns[1 + $scope.nonce.nbGraph * (addr - minSource)].push row.value
        $scope.nonce.chart.load
          columns: $scope.nonce.options.data.columns
    
    $scope.refreshNonce = () ->
      loadNonce addr for addr in [minSource..maxSource]
    
    $scope.previousNonce = () ->
      $scope.nonce.offsetData = $scope.nonce.offsetData + $scope.nonce.nbData
      $scope.refreshNonce()
      
    $scope.nextNonce = () ->
      return if $scope.nonce.offsetData <= 0
      $scope.nonce.offsetData = $scope.nonce.offsetData - $scope.nonce.nbData
      $scope.refreshNonce()
    
    
    ######################################
    # SWAP PACKETS/EVENTS
    
    $scope.swapPackets = []
    $scope.swapEvents = []
    
    ss.server.on 'ready', () ->
      ss.rpc 'swapserver.getSwapPackets', (swapPackets) ->
        $scope.$apply () ->
          $scope.swapPackets = swapPackets
      ss.rpc 'swapserver.getSwapEvents', (swapEvents) ->
        $scope.$apply () ->
          $scope.swapEvents = swapEvents
      $scope.createQualityGraph()
      #$scope.refreshQuality()
      $scope.createNonceGraph()
      #$scope.refreshNonce()
    
    # When a serial packet is received
    ss.event.on 'swapPacket', (sp) ->
      $scope.$apply () ->
        $scope.swapPackets.splice(0, 0, sp)
        $scope.swapPackets.pop() if $scope.swapPackets.length > 40
    
    # When a swap event is received
    ss.event.on 'swapEvent', (se) ->
      $scope.$apply () ->
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
              if ((x - pos[0]) * (x - pos[0]) + (y - pos[1]) * (y - pos[1])) < 10000
                # Click on light in room
                ss.rpc 'swapserver.sendSwapPacket', swap.LightController.Functions.Light, light.swapDeviceAddress, swap.LightController.Registers.Outputs.id, [light.outputNb, swap.LightController.Values.Toggle]
            
    $scope.lightPosition = (room, light) ->
      #dx and dy should be used for custom position in handeld device in order to avoid cross light tapping
      #return [room.location.x + light.location.x + (light.location.dx || 0), room.location.y + light.location.y + (light.location.dy || 0)];
      return [room.location.x + light.location.x, room.location.y + light.location.y];
    
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
    
    ss.event.on 'temperatureUpdated', (temperature) ->
      $scope.$apply () ->
          $scope.temperature = swap.getTemperature(temperature)
          $scope.pressure = swap.getTemperature(temperature)
    
    ss.event.on 'pressureUpdated', (pressure) ->
      $scope.$apply () ->
          $scope.temperature = swap.getTemperature(pressure)
          $scope.pressure = swap.getTemperature(pressure)
    
    ss.event.on 'lightStatusUpdated', (lightStatus) ->
      $scope.$apply () ->
        for level in $scope.levels
          do (level) ->
            for room in level.rooms
              do (room) ->
                for light in room.lights
                  do (light) ->
                    if lightStatus.regAddress == light.swapDeviceAddress
                          light.status = lightStatus.value[light.outputNb]
    
    ss.server.on 'ready', () ->
      ss.rpc 'swapserver.getLevels', (levels) ->
        $scope.$apply () ->
          $scope.levels = levels
      ss.rpc 'swapserver.getTemperature', (temperature) ->
        $scope.$apply () ->
          $scope.temperature = swap.getTemperature(temperature)
          $scope.pressure = swap.getTemperature(temperature)
      ss.rpc 'swapserver.getPressure', (pressure) ->
        $scope.$apply () ->
          $scope.temperature = swap.getTemperature(pressure)
          $scope.pressure = swap.getTemperature(pressure)
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
        $scope.$apply () ->
          $scope.config = config
          $scope.reset()
  ]
  
  swapApp.controller 'TemperatureCtrl', ['$scope', '$http', 'rpc', ($scope, $http, rpc) ->
    
    $scope.chartType = 'spline'
    $scope.chartNbPoints = 100
    $scope.chart = null
    
    $scope.config =
      bindto: '#chart-temperature'
      size:
        width: 1200
        height: 600
      data:
        keys: 
          x: 'x'
          value: []
        json: []
      axis:
        x: 
          'type': 'timeseries'
          'tick':
            'format': '%x %X'
        y: 
          'label':
            'text': 'Temp. (°C)'
            'position': 'uter-middle'
    
    $scope.$watch 'devices', (newValue, oldValue) ->
      if newValue
        $scope.config.data.types = {}
        $scope.config.data.keys.value = []
        for id, device of newValue
          do (device) ->
            dataId = 'data' + device.address
            $scope.config.data.types[dataId] = $scope.chartType
            $scope.config.data.keys.value.push dataId
        $scope.createGraph()
    
    $scope.createGraph = () ->
      $scope.chart = c3.generate $scope.config

    $scope.reload = () ->
      $scope.loadNewData()

    $scope.loadNewData = () ->
      $http.get('http://192.168.1.2:5984/panstamp_packets/_design/domotix/_view/temperature?descending=true&limit=' + $scope.chartNbPoints).success (data, status, headers, config) ->
        newdata =
          keys: $scope.config.data.keys
          json: []
        angular.forEach data.rows, (row, i) ->
          item = 
            x: moment(row.value.time).toDate()
          item['data' + row.value.regAddress] = (row.value.value[0] * 256 + row.value.value[1]) / 100
          newdata.json.push item
          return
        $scope.chart.load newdata
    
    ss.server.on 'ready', () ->
      ss.rpc 'swapserver.getDevices', (devices) ->
        $scope.$apply () ->
          $scope.devices = devices
      
    ss.event.on 'devicesUpdated', () ->
      ss.rpc 'swapserver.getDevices', (devices) ->
        $scope.$apply () ->
          $scope.devices = devices
      
  ]
  
  swapApp.controller 'ConsommationCtrl', ['$scope', '$http', 'rpc', ($scope, $http, rpc) ->
    
    $scope.chartNbPoints = 100
    $scope.chart = null
    
    $scope.config =
      bindto: '#chart-consommation'
      size:
        width: 1200
        height: 600
      data:
        keys: 
          x: 'x'
          value: ['EAU', 'ELEC']
        types:
          'EAU': 'spline'
          'ELEC': 'bar'
        json: []
      bar:
        width:
            ratio: 1
      axis:
        x: 
          'type': 'timeseries'
          'tick':
            'format': '%x %X'
        y1: 
          'label':
            'text': 'm3'
            'position': 'uter-middle'
        y2: 
          'label':
            'text': 'kWh'
            'position': 'uter-middle'
    
    $scope.createGraph = () ->
      $scope.chart = c3.generate $scope.config

    $scope.reload = () ->
      $scope.loadNewData()

    $scope.loadNewData = () ->
      $http.get('http://192.168.1.2:5984/events/_design/domotix/_view/consommation?descending=true&limit=' + $scope.chartNbPoints).success (data, status, headers, config) ->
        newdata =
          keys: $scope.config.data.keys
          json: []
        angular.forEach data.rows, (row, i) ->
          item = 
            x: moment(row.value.dateTime).toDate()
          item[row.value.type] = row.value.value
          newdata.json.push item
          return
        $scope.chart.load newdata
    
    $scope.createGraph()
  ]
