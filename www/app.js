// My SocketStream 0.3 app

var http = require('http'),
    c = require('coffee-script'),
    ss = require('socketstream'),
    swap = require('./client/code/common/swap'),
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

//ss.ws.transport.use(require('ss-engine.io'));

// code & template formatters
ss.client.formatters.add(require('ss-coffee'));
ss.client.formatters.add(require('ss-jade'));
ss.client.formatters.add(require('ss-stylus'));
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
server.listen(serverConfig.port);
logger.info('Server listening on port %d in %s mode', serverConfig.port, ss.env);

// Start SocketStream 
ss.start(server);

setTimeout(function () {
    ss.api.publish.all(swap.MQ.Type.MANAGEMENT, "network", "SERVER_STARTED");
}, 3000);

process.on('SIGINT', function(){
    ss.api.publish.all(swap.MQ.Type.MANAGEMENT, "network", "SERVER_STOPPED");
    logger.info('Server is stopping...');
    logger.info(arguments);
    process.exit()
})

