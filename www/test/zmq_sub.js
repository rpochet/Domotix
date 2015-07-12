var zmq = require('zmq');
var subscriber = zmq.socket('sub');

subscriber.connect("tcp://192.168.1.4:8131");
console.log("Connected");

subscriber.subscribe('a');
subscriber.on('message', function(msg) {
    console.log(msg.toString());
});
