package eu.pochet.domotix.service;

public enum Service {
	
	CARD_SERVICE(0), 
	BRIDGE_SERVICE(1), 
	LIGHT_CONTROLLER_SERVICE(2), 
	LIGHT_SWITCH_SERVICE(3), 
	LIGHT_STATUS_SERVICE(4), 
	SENSOR_SERVICE(5), 
	SENSOR_RECEIVER_SERVICE(6);

	private int id;

	private Service(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

}
