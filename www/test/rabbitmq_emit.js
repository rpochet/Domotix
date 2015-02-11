var amqp       = require('amqp');
//var amqp_hacks = require('./amqp-hacks');

var connection = amqp.createConnection();

var message = process.argv.slice(2).join(' ') || 'Hello World!';

connection.on('ready', function(){
    /*connection.exchange('', {type: 'fanout',
                                 autoDelete: false}, function(exchange){
        exchange.publish('swpPacket', message);
        console.log(" [x] Sent %s", message);

        //amqp_hacks.safeEndConnection(connection);
    });*/
    connection.publish('', message);
    console.log(" [x] Sent %s", message);
});
