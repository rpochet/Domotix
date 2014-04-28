// My SocketStream 0.3 app

var http = require('http'),
    c = require('coffee-script'),
    ss = require('socketstream'),
//    express = require('express'),
    log4js = require('log4js'),
    serverConfig = require('config').server;
log4js.configure('config/log4js_configuration.json', {});
var logger = log4js.getLogger(__filename.split('/').pop(-1).split('.')[0]);

// ???? c = require('coffee-script'),

//var consoleServer = require('ss-console')(ss);
//consoleServer.listen(5000);

//app = express();

// Define a single-page client called 'main'
ss.client.define('desktop', {
  view: 'desktop.jade',
  css:  ['libs', 'desktop.styl'],
  code: ['libs', 'desktop'],
  tmpl: ['common', 'desktop']
});
ss.client.define('mobile', {
  view: 'mobile.jade',
  css:  ['libs', 'mobile.styl'],
  code: ['libs', 'mobile'],
  tmpl: ['common', 'mobile']
});

// Serve this client on the root URL
ss.http.router.on('/', function(req, res) {
  if (req.headers['user-agent'].match(/iPhone/))
    res.serveClient('mobile');
  else
    res.serveClient('desktop');
});

// Extend SocketStream with Models and more
//ss.responders.add(require('ss-backbone'));

//ss.session.store.use('redis');
//ss.publish.transport.use('redis');

// Code Formatters
ss.client.formatters.add(require('ss-coffee'));
ss.client.formatters.add(require('ss-jade'));
ss.client.formatters.add(require('ss-stylus'));

// Use server-side compiled Hogan (Mustache) templates. Others engines available
//ss.client.templateEngine.use(require('ss-hogan'));
ss.client.templateEngine.use('ember');

// Minimize and pack assets if you type: SS_ENV=production node app.js
if (ss.env === 'production') {
    ss.client.packAssets();
    process.on('uncaughtException', function (err) {
        console.log('ERR (uncaught) ', err);
    });
}
    
// Start web server
//var server = app.listen(serverConfig.port);
var server = http.Server(ss.http.middleware);
server.listen(serverConfig.port);
logger.info('Server started on port %d', serverConfig.port);

// Start SocketStream
ss.start(server);

// Append SocketStream middleware to the stack
//app.stack = ss.http.middleware.stack.concat(app.stack);

process.on('SIGINT', function(){
  console.log(arguments);
  process.exit()
})
