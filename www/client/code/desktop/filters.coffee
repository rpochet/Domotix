
module.exports = (swapApp) ->
    'use strict';
    
    systemStates =
        0: "Restart"
        1: "Radio On"
        2: "Radio Off"
        3: "Sync mode"
        4: "Low battery"
    
    angular.module('swap.filters', [])
        .filter 'fromNow', () ->
            (date) -> moment(date).fromNow()
        .filter 'systemState', () ->
            (systemState) -> systemStates[systemState]
        .filter 'objectToArray', () ->
            (object) -> (ele for id, ele of object)
