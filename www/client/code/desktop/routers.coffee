# AngularJS client-side routes

module.exports = (swapApp) ->
    
    "use strict"
    
    swapApp.config ["$routeProvider", "$locationProvider", ($routeProvider, $locationProvider) ->
        
        # setup routing
        $routeProvider.when("/",
            action: "devices.view"
        ).when("/foo",
            action: "foo.view"
        ).when("/bar",
            action: "bar.view"
        ).when("/tests",
            action: "tests.view"
        ).otherwise
            redirectTo: "/"
        
        # use html5 push state
        $locationProvider.html5Mode true
    ]
