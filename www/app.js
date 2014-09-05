// My SocketStream 0.3 app

var http = require('http'),
    c = require('coffee-script'),
    ss = require('socketstream'),
    log4js = require('log4js'),
    serverConfig = require('config').server;
log4js.configure('config/log4js_configuration.json', {});
var logger = log4js.getLogger(__filename.split('/').pop(-1).split('.')[0]);

//var consoleServer = require('ss-console')(ss);
//consoleServer.listen(5000);

//ss.session.store.use('redis');
//ss.publish.transport.use('redis');

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
server.listen(serverConfig.port);
console.log('Server listening on port %d in %s mode', serverConfig.port, ss.env);

// Start SocketStream 
ss.start(server);

// FIXME: remove this code; just demo of pubsub
/*setInterval(function () {
    ss.api.publish.all('foo:bar', new Date());
}, 3000);*/

process.on('SIGINT', function(){
    console.log(arguments);
    process.exit()
})

