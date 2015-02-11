amqp = require("amqp")
connection = amqp.createConnection()
message = "Hello World!"
connection.on "ready", ->
  connection.publish "swapPacket", message
  console.log " [x] Sent %s", message
  return
