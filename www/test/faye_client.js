var faye = require('faye');
var client = new faye.Client('http://192.168.1.4:4000/domotix');
var publication = client.publish('/network', {text: 'Hi there'});

publication.then(function() {
  console.log('Message received by server!');
}, function(error) {
  console.log('There was a problem: ' + error.message);
});

