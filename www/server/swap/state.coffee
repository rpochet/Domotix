events = require "events"
cradle = require "cradle"
swap = require "../../client/code/common/swap"
logger = require("log4js").getLogger(__filename.split("/").pop(-1).split(".")[0])
moment = require "moment"
Q = require "q"

###############################################################
#
#  
#
###############################################################

class State extends events.EventEmitter
    
    constructor: (@config, @pubSub) ->
        
        logger.info "Starting state manager..."
        
        @state = {}
        
        @dbPanstamp = new(cradle.Connection)(@config.couchDB.host, @config.couchDB.port).database("panstamp")
        
        @init(@pubSub)
    
    init: (@pubSub) ->
        
        @dbPanstamp.get "state", (err, doc) =>
            return logger.error "Get state failed: #{JSON.stringify(err)}" if err?
            @state = doc
            logger.info "State initialized"
            logger.debug @state
            
            if @config.state.clientCheck
                @clientcheck = setInterval () =>
                    #logger.debug "Client check #{JSON.stringify(@state["clients"])}"
                    
                    for clientId, clientState of @state["clients"]
                        clientState.lastCheck = moment().unix()
                        clientState.time = moment().unix() if clientState.time is undefined
                        
                        #logger.debug "#{clientId} : #{JSON.stringify(clientState)}"
                        
                        if clientState.value != "disconnect"
                            if (clientState.lastCheck - clientState.time) > @config.state.clientTimeOut
                                clientState.value = "disconnect"
                                clientState.time = moment().unix()
                                @saveState()
                        #else
                        #    logger.debug "Is already diconnected"
                    
                , @config.state.clientCheckInterval
    
    saveState: () ->
        logger.debug "Saving state: #{JSON.stringify(@state)}" 
        
        @dbPanstamp.save "state", @state, (err, status) =>
            logger.debug err if err?
            return logger.error "Save state failed: #{JSON.stringify(err)}" if err?
            @state._rev = status.rev
            logger.debug "State saved: #{JSON.stringify(@state)}"
    
    updateState: (name, key, value, data) ->
        logger.debug "Update state #{name} - #{key} - #{value}" if value != "ping"
        
        @state[name] = {} if not @state[name]
        @state[name][key] = {} if not @state[name][key]
        @state[name][key].time = moment().unix()
        
        if value is "ping"
            #if @state[name][key].nonce > 
            #    @pubSub.publish topic,
            #        swapPacket: swapPacket
            #        swapDevice: swapDevice
            #        swapRegister: swapRegister
            return
        
        logger.info "Update state #{name}/#{key} with value #{value}"
        @state[name][key].value = value
        @state[name][key].data = data if data?
        @state[name][key].nonce = @state[name][key].nonce++
        
        @saveState()
        
        @state[name][key].nonce
    
    getState: (name) ->
        return @state.name if name
        return @state if name?
    
    destroy: () ->
        logger.info "Stopping state manager..."
        logger.info "State manager stopped"

module.exports = State
