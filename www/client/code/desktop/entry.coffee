# This file automatically gets called first by SocketStream and must always exist

# Make 'ss' available to all modules and the browser console
window.ss = require 'socketstream'

require '/services'
require '/directives'
require '/components'

# angular application
app = angular.module('swapApp', ['swap.filters', 'swap.services', 'swap.directives', 'ui.bootstrap', 'ngRoute', 'xeditable', 'components', 'ngToast', 'angular-loading-bar', 'nvd3ChartDirectives', 'gridshore.c3js.chart'], 
    ($dialogProvider) ->
        $dialogProvider.options {
            backdrop: true
            backdropFade: true
            modalFade: true
            keyboard: true
        }
)

app.run (editableOptions) ->
    editableOptions.theme = 'bs2' # bootstrap3 theme. Can be also 'bs2', 'default'

# Cors usage example. 
# @author Georgi Naumov gonaumov@gmail.com for contacts and suggestions. 
app.config ($httpProvider) ->
    # Enable cross domain calls
    $httpProvider.defaults.useXDomain = true
    delete $httpProvider.defaults.headers.common['X-Requested-With']

app.config (ngToastProvider) ->
  ngToastProvider.configure
    animation: 'slide'
    horizontalPosition: 'right'
    verticalPosition: 'top'
    maxNumber: 0

# configure angular routing
require('/routers')(app)

# setup angular controllers
require('/controllers')(app)

require('/filters')(app)

ss.server.on 'disconnect', ->
    $('#warning').modal 'show'
    console.log('Connection down :-(')

ss.server.on 'reconnect', ->
    $('#warning').modal 'hide'
    console.log('Connection back up :-)')

ss.server.on 'ready', ->

  # Wait for the DOM to finish loading
  jQuery ->
    # no-op, load is done before
    
    # Load app
    #require('/app')
