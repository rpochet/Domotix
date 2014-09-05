package eu.pochet.domotix;

public interface Constants {

	// Preferences
	String DOMOTIX_BUS_INPUT_HOST = "domotix.bus.input.host";
	String DOMOTIX_BUS_INPUT_HOST_DEFAULT = "192.168.1.4";

	String DOMOTIX_BUS_INPUT_PORT = "domotix.bus.input.port";
	int DOMOTIX_BUS_INPUT_PORT_DEFAULT = 5555;

	String DOMOTIX_BUS_OUTPUT_HOST = "domotix.bus.output.host";
	String DOMOTIX_BUS_OUTPUT_HOST_DEFAULT = "192.168.1.4";

	String DOMOTIX_BUS_OUTPUT_PORT = "domotix.bus.output.port";
	int DOMOTIX_BUS_OUTPUT_PORT_DEFAULT = 10000;
	
	// Messages
	String MESSAGE = "message";
	
	String MESSAGE_LENGTH = "messageLength";

	String MESSAGE_TYPE = "messageType";
	int MESSAGE_TYPE_SWAP = 1;
	int MESSAGE_TYPE_STATUS = 2;
	
	String MESSAGE_LIGHT_ID = "lightId";
	String MESSAGE_LIGHT_STATUS = "lightStatus";
	

	// Design
    int BACKGROUNG_MAX_X = 1220;
    
    int BACKGROUNG_MAX_Y = 1220;
	
	int CARD_OFFSET_X = -16;
	int CARD_OFFSET_Y = -16;
	int CARD_TOUCH_OFFSET = -20;
	
	int LIGHT_OFFSET_X = -16;
	int LIGHT_OFFSET_Y = -16;
	int LIGHT_TOUCH_OFFSET = -20;

}
