var amqp = require('amqp');

var connection = amqp.createConnection({
   host: '192.168.1.4',
   port: 5672,
   login: 'domotix',
   password: 'domotix',
   vhost: '/domotix',
});

console.log('Starting...');

connection.on('ready', function () {
		console.log('Connected!');
    
    var queue = connection.exchange('SWAP_PACK', function(queue) {
        queue.subscribe(function (message) {
						console.log("Got a message (Consumer 1): ");
						// Message Properties:
						console.log("--- Message (" + message._deliveryTag + ", '" + message._routingKey + "') ---");
						console.log("--- contentType: " + message.contentType);
						// Get original message string:
						console.log('--- toString(): ' + message.data.toString('ascii', 0, message.data.length));
				});
    });
    
    /*var exchange = connection.exchange('SWAP_PACKET', {type: 'fanout', durable: true, autoDelete: false}, function(exchange) {
				console.log('Exchange OK');
    		var _1st_queue = connection.queue('swapPacket-one');
				_1st_queue.bind(exchange, '');
				_1st_queue.subscribe(function (message) {
						console.log("Got a message (Consumer 1): ");
						// Message Properties:
						console.log("--- Message (" + message._deliveryTag + ", '" + message._routingKey + "') ---");
						console.log("--- contentType: " + message.contentType);
						// Get original message string:
						console.log('--- toString(): ' + message.data.toString('ascii', 0, message.data.length));
				});
    		var _2nd_queue = connection.queue('swapPacket-two');
				_2nd_queue.bind(exchange, '');
				_2nd_queue.subscribe(function (message) {
						console.log("Got a message (Consumer 2): ");
						// Message Properties:
						console.log("--- Message (" + message._deliveryTag + ", '" + message._routingKey + "') ---");
						console.log("--- contentType: " + message.contentType);
						// Get original message string:
						console.log('--- toString(): ' + message.data.toString('ascii', 0, message.data.length));
				});
    });*/
});

connection.on('error', function (e) {
    console.log(e);
})
 
connection.on('close', function (e) {
    console.log('connection closed.');
});




/*var http = require('http');
var io = require('socket.io');
var moment = require('moment');
var FifoArray = require('fifo-array');
 
server = http.createServer(function(req, res){
});
server.listen(8080);

var topics = [{
		name: 'swapPacket',
		done: new Array(),
		subscribers: new Array(),
		msg: new FifoArray(10)
}];

var subscribers = {};

var handleSwapPacket = function(swapPacket) {
		
}


// socket.io 
var socket = io.listen(server);
 
socket.on('connection', function(client) {

		console.log("Client connected");
		console.log(client.id);
		client.emit('message', {message: 'Vous êtes bien connecté !'});
		
		client.on('swapPacket', function(swapPacket) {
				handleSwapPacket(swapPacket);
		});
		
		client.on('swapPacket', function(swapPacket) {
				handleSwapPacket(swapPacket);
		});

		client.on('subscribe', function (subscriptionName) {
				console.log('Client subscribe: ' + client.id);
				if(subscribers[subscriptionName] == undefined) {				
						subscribers[subscriptionName] = {
								socket: client,
								subscriptionName: subscriptionName,
								subscriptionDate: moment().format(),
								swapPacket: new FifoArray(10)
						};
				}
				subscribers[subscriptionName].clientId = client.id;
				subscribers[subscriptionName].connected = true;
		});

		client.on('unsubscribe', function (subscriptionName) {
				console.log('Client unsubscribe: ' + client.id);
				delete subscribers[subscriptionName];
		});

		client.on('disconnect', function () {
				console.log('Client disconnected: ' + client.id);
				console.log(subscribers);
				subscribers.forEach(function(subscriber) {
						console.log('subscriber.clientId: ' + subscriber.clientId);
						console.log('client.id: ' + client.id);
						console.log('Same: ' + (subscriber.clientId == client.id));
						if(subscriber.clientId == client.id) {
								subscriber.connected = false;
						}
				});
		});
	
});

var poller = setInterval(function() {

		topics.forEach(function(topic) {
				if(topic.msg.length == 0) {
						return;
				}
				for(subscriberName in subscribers) {
				    if(topic.done.contains(subscriberName)) {
				    		return;
				    }
				    subscriber = subscribers[subscriberName];
				    if(subscriber.connected) {
				        subscriber.socket.emit(topic.name, topic.msg.pop());
				        topic.done.push(subscriberName);
				        if(topic.done.length == topic.subscribers.length) {
				        		topic.msg.pop();
				        }
				    }
				}
		});
		
}, 1000);

console.log("Listening...");*/
