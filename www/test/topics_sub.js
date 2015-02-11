var zmq = require('zmq')
var subscriber = zmq.socket('sub')

subscriber.connect('tcp://192.168.1.4:8888', function(err) {
  if(err)
    console.log(err)
  else
    console.log('Connected to 8888…')
});

subscriber.subscribe('TEST');

subscriber.on('message', function(message) {
  console.log(message);
});

process.on('SIGINT', function() {
  subscriber.close()
})