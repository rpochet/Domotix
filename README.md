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
Incoming messsage: @config.broker.udp.host on port @config.broker.udp.inport
Output message: broadcast address on port @config.broker.udp.outport 

####AMQP
AMQP subscriber is listening on message published on a topic. The topic defines the message type. 

#####Configuration
RabbitMQ server: @config.broker.amqp.host on port @config.broker.amqp.inport with virtual host @config.broker.amqp.vhost
Authentification: @config.broker.amqp.login with password @config.broker.amqp.password
        
##DomotixLightController

TODO

##DomotixLightSwitch

TODO

##www

TODO

### Raspberry PI

Cron: crontab -e  
