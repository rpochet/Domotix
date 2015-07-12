/*! c3-angular - v0.1.0 - 2015-01-01
* https://github.com/jettro/c3-angular-sample
* Copyright (c) 2015 ; Licensed  */

angular.module("gridshore.c3js.chart",[]).controller("ChartController",["$scope",function(a){function b(b,c,d,e){void 0!==c&&(a.types[b]=c),void 0!==d&&(null===a.names&&(a.names={}),a.names[b]=d),void 0!==e&&(null===a.colors&&(a.colors={}),a.colors[b]=e)}function c(){a.jsonKeys={},a.jsonKeys.value=[],angular.forEach(a.chartColumns,function(c){a.jsonKeys.value.push(c.id),b(c.id,c.type,c.name,c.color)}),a.chartX&&(a.jsonKeys.x=a.chartX.id),a.names&&(a.config.data.names=a.names),a.colors&&(a.config.data.colors=a.colors),a.config.data.keys=a.jsonKeys,a.config.data.json=a.chartData,a.chart=c3.generate(a.config)}a.chart=null,a.columns=[],a.types={},a.axis={},a.axes={},a.xValues=null,a.xTick=null,a.names=null,a.colors=null,a.grid=null,a.legend=null,a.tooltip=null,a.chartSize=null,a.colors=null,a.jsonKeys=null,this.showGraph=function(){var b={};b.bindto="#"+a.bindto,b.data={},a.xValues&&(b.data.x=a.xValues),a.columns&&(b.data.columns=a.columns),b.data.types=a.types,b.data.axes=a.axes,a.names&&(b.data.names=a.names),a.colors&&(b.data.colors=a.colors),a.showLabels&&"true"===a.showLabels&&(b.data.labels=!0),a.showSubchart&&"true"===a.showSubchart&&(b.subchart={show:!0}),a.enableZoom&&"true"===a.enableZoom&&(b.zoom={enabled:!0}),b.axis=a.axis,a.xTick&&(b.axis.x.tick=a.xTick),null!=a.grid&&(b.grid=a.grid),null!=a.legend&&(b.legend=a.legend),null!=a.tooltip&&(b.tooltip=a.tooltip),null!=a.chartSize&&(b.size=a.chartSize),null!=a.colors&&(b.color={pattern:a.colors}),a.config=b,a.chartData&&a.chartColumns?a.$watchCollection("chartData",function(){c()}):a.chart=c3.generate(a.config)},this.addColumn=function(c,d,e,f){a.columns.push(c),b(c[0],d,e,f)},this.addYAxis=function(b){a.axes=b,a.axis.y2||(a.axis.y2={show:!0})},this.addXAxisValues=function(b){a.xValues=b},this.addAxisProperties=function(b,c){a.axis[b]=c},this.addXTick=function(b){a.xTick=b},this.rotateAxis=function(){a.axis.rotated=!0},this.addGrid=function(b){null==a.grid&&(a.grid={}),null==a.grid[b]&&(a.grid[b]={}),a.grid[b].show=!0},this.addGridLine=function(b,c,d){null==a.grid&&(a.grid={}),"x"===b?(void 0===a.grid.x&&(a.grid.x={}),void 0===a.grid.x.lines&&(a.grid.x.lines=[])):(void 0===a.grid.y&&(a.grid.y={}),void 0===a.grid.y.lines&&(a.grid.y.lines=[])),"y2"===b?a.grid.y.lines.push({value:c,text:d,axis:"y2"}):a.grid[b].lines.push({value:c,text:d})},this.addLegend=function(b){a.legend=b},this.addTooltip=function(b){a.tooltip=b},this.addSize=function(b){a.chartSize=b},this.addColors=function(b){a.colors=b}}]).directive("c3chart",["$timeout",function(a){var b=function(b,c,d,e){a(function(){e.showGraph()})};return{restrict:"E",controller:"ChartController",scope:{bindto:"@bindtoId",showLabels:"@showLabels",showSubchart:"@showSubchart",enableZoom:"@enableZoom",chartData:"=chartData",chartColumns:"=chartColumns",chartX:"=chartX"},template:"<div><div id='{{bindto}}'></div><div ng-transclude></div></div>",replace:!0,transclude:!0,link:b}}]).directive("chartColumn",function(){var a=function(a,b,c,d){var e=c.columnValues.split(",");e.unshift(c.columnId),d.addColumn(e,c.columnType,c.columnName,c.columnColor)};return{require:"^c3chart",restrict:"E",scope:{},replace:!0,link:a}}).directive("chartAxes",function(){var a=function(a,b,c,d){var e=c.valuesX;e&&d.addXAxisValues(e);var f=c.y,g=c.y2,h={};if(g){var i=g.split(",");for(var j in i)h[i[j]]="y2";if(f){var k=f.split(",");for(var l in k)h[k[l]]="y"}d.addYAxis(h)}};return{require:"^c3chart",restrict:"E",scope:{},replace:!0,link:a}}).directive("chartAxis",function(){var a=function(a,b,c,d){var e=c.axisRotate;e&&d.rotateAxis()};return{require:"^c3chart",restrict:"E",scope:{},transclude:!0,template:"<div ng-transclude></div>",replace:!0,link:a}}).directive("chartAxisX",function(){var a=function(a,b,c,d){var e=c.axisPosition,f=c.axisLabel,g={label:{text:f,position:e}},h=c.axisType;h&&(g.type=h);var i=c.axisHeight;i&&(g.height=i),d.addAxisProperties("x",g)};return{require:"^c3chart",restrict:"E",scope:{},transclude:!0,template:"<div ng-transclude></div>",replace:!0,link:a}}).directive("chartAxisY",function(){var a=function(a,b,c,d){var e=c.axisId,f=c.axisPosition,g=c.axisLabel,h={label:{text:g,position:f}};"y2"===e&&(h.show=!0);var i=c.paddingTop,j=c.paddingBottom;(i||j)&&(i=i?i:0,j=j?j:0,h.padding={top:parseInt(i),bottom:parseInt(j)});var k=c.rangeMax,l=c.rangeMin;k&&(h.max=parseInt(k)),l&&(h.min=parseInt(l)),d.addAxisProperties(e,h)};return{require:"^c3chart",restrict:"E",scope:{},replace:!0,link:a}}).directive("chartGrid",function(){var a=function(a,b,c,d){var e=c.showX;e&&"true"===e&&d.addGrid("x");var f=c.showY;f&&"true"===f&&d.addGrid("y");var g=c.showY2;g&&"true"===g&&d.addGrid("y2")};return{require:"^c3chart",restrict:"E",scope:{},replace:!0,link:a,transclude:!0,template:"<div ng-transclude></div>"}}).directive("chartGridOptional",function(){var a=function(a,b,c,d){var e=c.axisId,f=c.gridValue,g=c.gridText;d.addGridLine(e,f,g)};return{require:"^c3chart",restrict:"E",scope:{},replace:!0,link:a}}).directive("chartAxisXTick",function(){var a=function(a,b,c,d){var e={},f=c.tickCount;f&&(e.count=f);var g=c.tickFormat;g&&(e.format=g);var h=c.tickCulling;h&&(e.culling=h);var i=c.tickRotate;i&&(e.rotate=i);var j=c.tickFit;j&&(e.fit=j),d.addXTick(e)};return{require:"^c3chart",restrict:"E",scope:{},replace:!0,link:a}}).directive("chartLegend",function(){var a=function(a,b,c,d){var e=null,f=c.showLegend;if(f&&"false"===f)e={show:!1};else{var g=c.legendPosition;g&&(e={position:g})}null!=e&&d.addLegend(e)};return{require:"^c3chart",restrict:"E",scope:{},replace:!0,link:a}}).directive("chartTooltip",function(){var a=function(a,b,c,d){var e=null,f=c.showTooltip;if(f&&"false"===f)e={show:!1};else{var g=c.groupTooltip;g&&"false"===g&&(e={grouped:!1})}null!=e&&d.addTooltip(e)};return{require:"^c3chart",restrict:"E",scope:{},replace:!0,link:a}}).directive("chartSize",function(){var a=function(a,b,c,d){var e=null,f=c.chartWidth,g=c.chartHeight;(f||g)&&(e={},f&&(e.width=parseInt(f)),g&&(e.height=parseInt(g)),d.addSize(e))};return{require:"^c3chart",restrict:"E",scope:{},replace:!0,link:a}}).directive("chartColors",function(){var a=function(a,b,c,d){var e=c.colorPattern;e&&d.addColors(e.split(","))};return{require:"^c3chart",restrict:"E",scope:{},replace:!0,link:a}});
//# sourceMappingURL=c3-angular.min.js.map