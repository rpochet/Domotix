import zmq
context = zmq.Context()
socket = context.socket(zmq.PUB)
socket.bind("tcp://*:5556")

while True:
	req = socket.recv()
	socket.send("Hello " + req) 