var zmq = require('zmq')
var publisher = zmq.socket('pub')
var pending = 0

publisher.bind('tcp://*:8888', function(err) {
  if(err)
    console.log(err)
  else
    console.log('Listening on 8888...')
})

topic = 'TEST';
setTimeout(function() {
  console.log('Sending message to topic ' + topic);
  for(i = 0; i < 20; i++) {
    publisher.send([topic, "MSG" + i]);
  }
}, 10000);

process.on('SIGINT', function() {
  publisher.close()
})