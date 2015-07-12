var AMQP = require('amqp-coffee');

console.log('Starting...');

var QUEUE_NAME = 'MANAGEMENT';
var connection = new AMQP({
    host: '192.168.1.4',
    port: 5672,
    login: 'domotix',
    password: 'domotix',
    vhost: '/domotix'
}, function(error) {
    if(error) {
        console.log('Connection problem: ', error);
        return;
    }
    console.log('Connection ready');
    connection.publish(QUEUE_NAME, QUEUE_NAME, ['RRRR2'], {
        deliveryMode: 2
      }, function(res, TBD) {
        if (res) {
          return console.log("Problem to submit message to " + QUEUE_NAME + '. Error ' + res);
        }
        console.log(TBD);
      });
});
