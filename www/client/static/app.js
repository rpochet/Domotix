var app = angular.module('app', ['ngTouch', 'ui.grid', 'ui.grid.expandable', 'ui.grid.selection', 'ui.grid.pinning']);

app.controller('MainCtrl', ['$scope', '$http', '$log', function ($scope, $http, $log) {
  
  $scope.gridOptions = {
    expandableRowTemplate: 'expandableRowTemplate.html',
    expandableRowHeight: function(row) {
      return row.entity.items ? row.entity.items.length * 20 : 0;
    },
    enableExpandableRowHeader: false
  }
  
  $scope.gridOptions.columnDefs = [
    { name: 'id' },
    { name: 'name'},
    { name: 'age'}
  ];
  
  $scope.gridOptions.data = [{
      id: 1,
      name: 'name1',
      age: 1
  },{
      id: 2,
      name: 'name1',
      age: 1,
      items: ['item1','item2','item3','item4','item5']
  },{
      id: 3,
      name: 'name1',
      age: 1,
      items: ['item1','item2','item3','item4','item5']
  }];
  
  $scope.gridOptions.onRegisterApi = function(gridApi){
    $scope.gridApi = gridApi;
  };
  
  $scope.toggleDoc = function() {
    $scope.gridApi.expandable.expandAllRows();
  }
  
}]);
