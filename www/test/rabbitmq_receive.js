var amqp = require('amqp');

console.log(' [*] Starting...');

//var connection = amqp.createConnection();
//var connection = amqp.createConnection({host: '192.168.1.4', user: 'guest', password:'guest'});
var connection = amqp.createConnection({url: 'amqp://admin:admin@192.168.1.4:5672'});
connection.on('ready', function(){
    console.log(' [*] Connection ready')
    /*connection.exchange('', {type: 'fanout',
                                 autoDelete: false}, function(exchange){
        console.log(' [*] Exchange...')
        connection.queue('tmp-' + Math.random(), {exclusive: true},
                         function(queue){
            queue.bind('logs', '');
            console.log(' [*] Waiting for logs. To exit press CTRL+C')

            queue.subscribe(function(msg){
                console.log(" [x] %s", msg.data.toString('utf-8'));
            });
        })
    });*/
    connection.publish('swapPacket', 'test');
});
