var http = require('http'),
    faye = require('faye');
var port = 8000;
var server = http.createServer(),
    bayeux = new faye.NodeAdapter({mount: '/domotix', timeout: 45});

bayeux.on('handshake', function(clientId) {
  console.log('handshake', clientId);
});
bayeux.on('disconnect', function(clientId) {
  console.log('disconnect %s', clientId);
});
bayeux.on('subscribe', function(clientId, channel) {
  console.log('subscribe %s from %s', clientId, channel);
});
bayeux.on('unsubscribe', function(clientId, channel) {
  console.log('unsubscribe %s from %s', clientId, channel);
});
bayeux.on('publish', function(clientId, channel, data) {
  console.log('publish %s from %s', clientId, channel);
  console.log(data);
});

bayeux.attach(server);
server.listen(port);
console.log('Listening on port %s', port);

setTimeout(function() {
  bayeux.getClient().publish('/network', { 
    text: 'Server started on port ' + port,
    timestamp: new Date()
  });
}, 1000);

