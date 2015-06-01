#Domotix

##Description

###Message Type
MANAGEMENT: 
SWAP_PACKET: 
SWAP_DEVICE: 
LIGHT_STATUS: 
TEMPERATURE: 
_ALL: 

###PubSub

####Configuration
@config.broker.type: 'amqp' or 'udp'

####UDP
A UDP datagram listener is listening on incoming message. 

#####Message format
&lt;type&gt; | &lt;message length&gt; | &lt;message&gt;
* type is the position of the message type in the list of available types
* message length is a 2-byte value
* message depends on the message type

#####Configuration
@config.udpBridge.host on port @config.udpBridge.port

####AMQP
AMQP subscriber is listening on message published on a topic. The topic defines the message type. 

##DomotixLightController

TODO

##DomotixLightSwitch

TODO

##www

TODO

### Raspberry PI

Cron: crontab -e  
