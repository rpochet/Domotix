package eu.pochet.domotix.service;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import eu.pochet.domotix.dao.Location;
import eu.pochet.domotix.dao.SwapPacket;

/**
 * Helper class to convert Intent to/from Dommotix message.
 * 
 * @author romuald
 *
 */
public class ActionBuilder {

	/**
	 * Action
	 */
	private String intentAction;
	public static final String INTENT_FROM_SWAP = "FROM_SWAP";
	public static final String INTENT_TO_SWAP = "TO_SWAP";
	

	/**
	 * Type of message
	 */
	private ActionType type;
	
	private static final String ACTION_TYPE = "type";

    public enum ActionType {
        MANAGEMENT,
        SWAP_PACKET,
        PAN_SWAP_PACKET,
        SWAP_DEVICE,
        LIGHT_STATUS,
        TEMPERATURE,
		TYPE_RFU_2,
		TYPE_RFU_3,
		TYPE_RFU_4,
		TYPE_RFU_5,
		TYPE_RFU_6,
		TYPE_RFU_7,
		TYPE_RFU_8,
		TYPE_RFU_9,
		TYPE_RFU_10,
		TYPE_RFU_11,
		TYPE_RFU_12,
		TYPE_RFU_13,
		TYPE_RFU_14,
		TYPE_RFU_15,
		TYPE_RFU_16,
		TYPE_RFU_17,
		TYPE_LIGHT_SWITCH_ON,
		TYPE_LIGHT_SWITCH_OFF,
		TYPE_LIGHT_SWITCH_TOGGLE,
		TYPE_LIGHT_SWITCH_ON_ALL,
		TYPE_LIGHT_SWITCH_OFF_ALL,
        TYPE_LIGHT_UPDATE
	}
	
	/**
	 * 
	 */
	public static final int LIGHT_STATUS_OFF = 0;
	public static final int LIGHT_STATUS_ON = 254;
	public static final int LIGHT_STATUS_TOGGLE = -1;

    private static final String ACTION_LOCATION = "location";
	private static final String ACTION_LEVEL_ID = "levelId";
	private static final String ACTION_LIGHT_ID = "lightId";
	private static final String ACTION_LIGHT_STATUS = "lightStatus";
	private static final String ACTION_LIGHTS_STATUS = "lightsStatus";
	private static final String ACTION_SWAP_PACKET = "swapPacket";
	private static final String ACTION_TEMPERATURE = "temperature";
    private static final String ACTION_MANAGEMENT = "management";
	
	
	private SwapPacket swapPacket; 

    private byte[] managementData;

	private int levelId;
	
	private int lightId;
	
	private float lightStatus;
	
	private ArrayList<Integer> lightsStatus;
	
	private float temperature;

    private Location location;

	public ActionBuilder() {
    	
    }
    
    public ActionBuilder setAction(String action) {
		this.intentAction = action;
		return this;
	}
	
	public ActionBuilder setType(ActionType type) {
		this.type = type;
		return this;
	}

	public ActionBuilder setSwapPacket(SwapPacket swapPacket) {
		this.swapPacket = swapPacket;
		return this;
	}

    public ActionBuilder setManagementData(byte[] managementData) {
        this.managementData = managementData;
        return this;
    }
	
	public ActionBuilder setLevelId(int levelId) {
		this.levelId = levelId;
		return this;
	}
	
	public ActionBuilder setLightId(int lightId) {
		this.lightId = lightId;
		return this;
	}

	public ActionBuilder setLightStatus(int lightStatus) {
		this.lightStatus = lightStatus;
		return this;
	}

	public ActionBuilder setLightsStatus(ArrayList<Integer> lightsStatus) {
		this.lightsStatus = lightsStatus;
		return this;
	}
	
	public ActionBuilder setTemperature(float temperature) {
		this.temperature = temperature;
		return this;
	}

    public ActionBuilder setLocation(Location location) {
        this.location = location;
        return this;
    }
	
	public Intent toIntent() {
		Intent intent = new Intent(this.intentAction);
		addExtra(intent);
    	return intent;
	}

    public void sendMessage(Context context) {
        Intent intent = new Intent(context, ActionService.class);
        intent.setAction(this.intentAction);
        addExtra(intent);
    	context.startService(intent);
    }
    
    private void addExtra(Intent intent) {
    	intent.putExtra(ACTION_TYPE, this.type.ordinal());

		intent.putExtra(ACTION_SWAP_PACKET, this.swapPacket);

        intent.putExtra(ACTION_LOCATION, this.location);
    	intent.putExtra(ACTION_LEVEL_ID, this.levelId);

        intent.putExtra(ACTION_LIGHT_ID, this.lightId);
        intent.putExtra(ACTION_LIGHT_STATUS, this.lightStatus);
        intent.putExtra(ACTION_LIGHTS_STATUS, this.lightsStatus);

		intent.putExtra(ACTION_TEMPERATURE, this.temperature);

        intent.putExtra(ACTION_MANAGEMENT, this.managementData);
    }
    
    public ActionBuilder(Intent intent) {
    	this.intentAction = intent.getAction();
    	this.type = ActionType.values()[intent.getIntExtra(ACTION_TYPE, -1)];
    	
    	this.swapPacket = (SwapPacket) intent.getSerializableExtra(ACTION_SWAP_PACKET);

        this.location = (Location) intent.getSerializableExtra(ACTION_LOCATION);
    	this.levelId = intent.getIntExtra(ACTION_LEVEL_ID, -1);

    	this.lightId = intent.getIntExtra(ACTION_LIGHT_ID, -1);
    	this.lightStatus = intent.getFloatExtra(ACTION_LIGHT_STATUS, -1);

    	this.temperature = intent.getFloatExtra(ACTION_TEMPERATURE, -1);

        this.managementData = intent.getByteArrayExtra(ACTION_MANAGEMENT);
    }
    
    public String getAction() {
		return intentAction;
	}
    
    public ActionType getType() {
		return type;
	}
    
    public int getLevelId() {
		return levelId;
	}
    
    public int getLightId() {
		return lightId;
	}
    
    public float getLightStatus() {
		return lightStatus;
	}

	public void setDelayMinuts(int j) {
	}
	
	public SwapPacket getSwapPacket() {
		return this.swapPacket;
	}
	
	public float getTemperature() {
		return temperature;
	}

    public byte[] getManagementData() {
        return managementData;
    }

    public Location getLocation() {
        return location;
    }
}
