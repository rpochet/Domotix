# This file automatically gets called first by SocketStream and must always exist

# Make 'ss' available to all modules and the browser console
window.ss = require 'socketstream'

require '/services'
require '/directives'

# angular application
app = angular.module('swapApp', ['swap.filters', 'swap.services', 'swap.directives', 'ui.bootstrap', 'ngRoute', 'xeditable'], 
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
