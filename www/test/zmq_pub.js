var zmq = require('zmq');
var publisher = zmq.socket("pub");

publisher.bind("tcp://192.168.1.4:8131", function(err) {
  if (err) throw err;
});
console.log("Connected");

var count = 0;

setInterval(function() {
    console.log("Sending a " + count);
    publisher.send("a " + count++);
}, 1000);

