package eu.pochet.domotix;

public interface Constants {

	String NOTIFICATION_DOMOTIX_GROUP = "Domotix";
			
	// Preferences
	String DOMOTIX_BUS_INPUT_HOST = "domotix.bus.input.host";
	String DOMOTIX_BUS_INPUT_HOST_DEFAULT = "192.168.1.4";

	String DOMOTIX_BUS_INPUT_PORT = "domotix.bus.input.port";
	int DOMOTIX_BUS_INPUT_PORT_DEFAULT = 5555;

	String DOMOTIX_BUS_OUTPUT_HOST = "domotix.bus.output.host";
	String DOMOTIX_BUS_OUTPUT_HOST_DEFAULT = "192.168.1.4";

	String DOMOTIX_BUS_OUTPUT_PORT = "domotix.bus.output.port";
	int DOMOTIX_BUS_OUTPUT_PORT_DEFAULT = 5556;
	
	String DOMOTIX_DATA_HOST = "domotix.data.host";
	String DOMOTIX_DATA_HOST_DEFAULT = "http://" + DOMOTIX_BUS_OUTPUT_HOST_DEFAULT + ":5984/panstamp/";
	
	// Messages
	String MESSAGE = "message";
	
	String MESSAGE_LENGTH = "messageLength";

	/*String MESSAGE_TYPE = "messageType";
	int MESSAGE_TYPE_SWAP = 0;
	int MESSAGE_TYPE_DEVICE = 1;
	int MESSAGE_TYPE_STATUS = 2;*/
	
	String MESSAGE_LIGHT_ID = "lightId";
	String MESSAGE_LIGHT_STATUS = "lightStatus";
	
	String LIGHT_CONTROLLER_PRODUCT_CODE = "0000006400000001";
	
	public static final int ADDR_BROADCAST = 0;
	
	int FCT_SWAP_STATUS = 0;
	int FCT_SWAP_QUERY = 1;
	int FCT_SWAP_COMMAND = 2;
	int FCT_SWAP_CUSTOM_1 = 3;
	int FCT_SWAP_CUSTOM_2 = FCT_SWAP_CUSTOM_1 + 1;
	int FCT_SWAP_CUSTOM_3 = FCT_SWAP_CUSTOM_2 + 1;
	int FCT_SWAP_CUSTOM_4 = FCT_SWAP_CUSTOM_3 + 1;
			
	int REGISTER_LIGHT_OUTPUT = 14;
	int REG_LIGHT_OUTPUT = 14;

	String SWAP_REGISTER_TEMPERATURE = "Temperature";
	
	// Design
    int BACKGROUNG_MAX_X = 1220;
    
    int BACKGROUNG_MAX_Y = 1220;
	
	int CARD_OFFSET_X = -16;
	int CARD_OFFSET_Y = -16;
	int CARD_TOUCH_OFFSET = 100;
	
	int LIGHT_SIZE = 16 * 2 * 5;
	int LIGHT_OFFSET_X = 0;//-16 * 2;
	int LIGHT_OFFSET_Y = 0;//-16 * 2;
	int LIGHT_TOUCH_OFFSET = 150;
	int LIGHT_TOUCH_OFFSET2 = 100;
	
}
