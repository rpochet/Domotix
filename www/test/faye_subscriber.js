var faye = require('faye');

faye.Logging.logLevel = 'debug'; 
faye.logger = function(msg) { if (console) console.log(msg) }; 

var client = new faye.Client('http://192.168.1.4:4000/domotix');
var publication = client.subscribe('/swapPacket', function(msg) {
  console.log(msg);
});
