var AMQP = require('amqp-coffee');

console.log('Starting...');

var QUEUE_NAME = 'SWAP_PACKET';
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
    connection.queue({
        queue: QUEUE_NAME + '-1',
        autoDelete: false,
        durable: true
    }, function(err, queue) {
        queue.declare(function(error, queueOptions) {
            if(error) {
                console.log('Declare problem: ', error);
                return;
            }
            console.log('Declare OK');
            queue.bind(QUEUE_NAME, queue.queueOptions.queue, function(err, TBD1) {
                if(error) {
                    console.log('Binding problem: ', error);
                    return;
                }
                console.log(TBD1);
                console.log('Binding OK');
                connection.consume(queue.queueOptions.queue, {prefetchCount: 1}, function(message) {
                    console.log("Message Data", message.data);
                    message.ack();
                }, function(error, consumer) {
                    if(error) {
                        console.log('Consumer problem: ', error);
                        return;
                    }
                    console.log("Consumer OK", consumer);
                });
            });
        });
    });
});
