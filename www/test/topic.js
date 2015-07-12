var FifoArray = require('fifo-array');

function Topic(name) {
  	this.name = name;
  	this.queue = new FifoArray(10);
  	this.subscribers = [];
}

Topic.prototype.push = function(msg) {
		queue.push({
		    given: [],
			  msg: msg
		});
};

Topic.prototype.pop = function() {
	  this.subscribers.forEach(function(subscriber) {
				subscriber.packets.push(msg);
		});
		return queue.pop();
};

module.exports = Topic;
