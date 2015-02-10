// My SocketStream 0.3 app

var http = require('http'),
    faye = require('faye'),
    amqp = require('amqp'),
    c = require('coffee-script'),
    ss = require('socketstream'),
    log4js = require('log4js'),
    moment = require('moment'),
    brokerConfig = require('config').broker,
    serverConfig = require('config').server;
log4js.configure('config/log4js_configuration.json', {});
var logger = log4js.getLogger(__filename.split('/').pop(-1).split('.')[0]);

//process.env.DEBUG = "engine*"

//var consoleServer = require('ss-console')(ss);
//consoleServer.listen(5000);

//ss.session.store.use('redis');
//ss.publish.transport.use('redis');

ss.ws.transport.use(require('ss-engine.io'));

// code & template formatters
ss.client.formatters.add(require('ss-coffee'));
ss.client.formatters.add(require('ss-jade'));
ss.client.formatters.add(require('ss-stylus'));
// user server-side compiled Hogan (Mustache) templates
//ss.client.templateEngine.use(require('ss-hogan'));
//ss.client.templateEngine.use('ember');
ss.client.templateEngine.use('angular');

// Define a single-page client called 'main'
ss.client.define('desktop', {
    view: 'desktop.jade',
    css:  ['libs', 'desktop.styl'],
    code: ['libs', 'common', 'desktop'],
    tmpl: ['common', 'desktop']
});
ss.client.define('mobile', {
    view: 'mobile.jade',
    css:  ['libs', 'mobile.styl'],
    code: ['libs', 'common', 'mobile'],
    tmpl: ['common', 'mobile']
});

// pack / minify if product env
if (ss.env === 'production') {
    ss.client.packAssets();
}

// Serve this client on the root URL 
ss.http.route('/', function(req, res){
    if (req.headers['user-agent'].match(/iPhone/))
        res.serveClient('mobile');
    else
        res.serveClient('desktop');
})

// Start web server 
var server = http.Server(ss.http.middleware); 
if(brokerConfig.type == 'faye') {
    var bayeux = new faye.NodeAdapter({
        mount: '/domotix', timeout: 45
    });
    bayeux.on('handshake', function(clientId) {
        logger.debug('Handshake %s', clientId);
    });
    bayeux.on('disconnect', function(clientId) {
        logger.debug('Disconnect %s', clientId);
    });
    bayeux.on('subscribe', function(clientId, channel) {
        logger.debug('Subscribe %s for %s', clientId, channel);
    });
    bayeux.on('unsubscribe', function(clientId, channel) {
        logger.debug('Unsubscribe %s for %s', clientId, channel);
    });
    bayeux.on('publish', function(clientId, channel, data) {
        logger.debug('Message publish %s for %s:', clientId, channel);
        logger.debug(data);
    });
    bayeux.attach(server);
}

server.listen(serverConfig.port);
logger.info('Server listening on port %d in %s mode', serverConfig.port, ss.env);

// Start SocketStream 
ss.start(server);

setTimeout(function () {
    if(brokerConfig.type == 'faye') { 
        bayeux.getClient().publish('/network', { 
            type: "SERVER_STARTED",
            port: serverConfig.port,
            time: moment().format()
        });
    } else if(brokerConfig.type == 'rabbitmq') {
        var connection = amqp.createConnection();
        connection.on('ready', function(){
            connection.publish('network', JSON.stringify({
                "type": "SERVER_STARTED",
                "time": moment().format()
            }));
        });
    } else {
        ss.api.publish.all("sendSwapEvent", "network", "SERVER_STARTED"); 
    }
}, 3000);

process.on('SIGINT', function(){
    if(brokerConfig.type == 'faye') { 
        bayeux.getClient().publish('/network', { 
            "type": "SERVER_STOPPED",
            "time": moment().format()
        }); 
    } else if(brokerConfig.type == 'rabbitmq') {
        var connection = amqp.createConnection();
        connection.on('ready', function(){
            connection.publish('network', JSON.stringify({
                "type": "SERVER_STOPPED",
                "time": moment().format()
            }));
        });
    } else {
        ss.api.publish.all("sendSwapEvent", "network", "SERVER_STOPPED"); 
    }

    logger.info('Server is stopping...');
    logger.info(arguments);
    process.exit()
})

