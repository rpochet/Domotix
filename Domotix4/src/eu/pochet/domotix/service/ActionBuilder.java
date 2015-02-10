package eu.pochet.domotix.service;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import eu.pochet.domotix.dao.SwapPacket;

/**
 * Helper class to convert Intent to/from Dommotix message.
 * 
 * @author romuald
 *
 */
public class ActionBuilder {

	/**
	 * Action type
	 */
	private static final String ACTION_TYPE = "type";
	
	/**
	 * Action type from SWAP network
	 */
	public static final String TYPE_FROM_SWAP = "TYPE_FROM_SWAP";
	
	/**
	 * Action type to SWAP network
	 */
	public static final String TYPE_TO_SWAP = "TYPE_TO_SWAP";
	
	public static final int TYPE_SWAP_PACKET = 0;
	public static final int TYPE_SWAP_DEVICE = 1;
	public static final int TYPE_LIGHT_SWITCH_ON = 2;
	public static final int TYPE_LIGHT_SWITCH_OFF = 3;
	public static final int TYPE_LIGHT_SWITCH_TOGGLE = 4;
	public static final int TYPE_LIGHT_SWITCH_ON_ALL = 5;
	public static final int TYPE_LIGHT_SWITCH_OFF_ALL = 6;
	public static final int TYPE_LIGHT_UPDATE = 7;
	public static final int TYPE_TEMPERATURE = 8;
	public static final int TYPE_LIGHT_STATUS = 9;
	
	public static final int LIGHT_STATUS_OFF = 0;
	public static final int LIGHT_STATUS_ON = 254;
	public static final int LIGHT_STATUS_TOGGLE = -1;

	private static final String ACTION_LEVEL_ID = "levelId";
	private static final String ACTION_LIGHT_ID = "lightId";
	private static final String ACTION_LIGHT_STATUS = "lightStatus";
	private static final String ACTION_LIGHTS_STATUS = "lightsStatus";
	private static final String ACTION_SWAP_PACKET = "swapPacket";
	private static final String ACTION_TEMPERATURE = "temperature";
	
	private String action;
	
	private int type;
	
	private SwapPacket swapPacket; 
	
	private int levelId;
	
	private int lightId;
	
	private float lightStatus;
	
	private ArrayList<Integer> lightsStatus;
	
	private float temperature;

	public ActionBuilder() {
    	
    }
    
    public ActionBuilder setAction(String action) {
		this.action = action;
		return this;
	}
	
	public ActionBuilder setType(int type) {
		this.type = type;
		return this;
	}

	public ActionBuilder setSwapPacket(SwapPacket swapPacket) {
		this.swapPacket = swapPacket;
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
	
	public Intent toIntent() {
		Intent intent = new Intent(this.action);
		intent.putExtra(ACTION_TYPE, this.type);
        
		intent.putExtra(ACTION_SWAP_PACKET, this.swapPacket);
				
		intent.putExtra(ACTION_LEVEL_ID, this.levelId);
		intent.putExtra(ACTION_LIGHT_ID, this.lightId);
		intent.putExtra(ACTION_LIGHT_STATUS, this.lightStatus);
		intent.putIntegerArrayListExtra(ACTION_LIGHTS_STATUS, this.lightsStatus);
		
		intent.putExtra(ACTION_TEMPERATURE, this.temperature);

		return intent;
	}

    public void sendMessage(Context context) {
        Intent intent = new Intent(context, ActionService.class);
        intent.setAction(this.action);
    	intent.putExtra(ACTION_TYPE, this.type);

		intent.putExtra(ACTION_SWAP_PACKET, this.swapPacket);
		
    	intent.putExtra(ACTION_LEVEL_ID, this.levelId);
        intent.putExtra(ACTION_LIGHT_ID, this.lightId);
        intent.putExtra(ACTION_LIGHT_STATUS, this.lightStatus);
        intent.putExtra(ACTION_LIGHTS_STATUS, this.lightsStatus);

		intent.putExtra(ACTION_TEMPERATURE, this.temperature);
		
		context.startService(intent);
    }
    
    public ActionBuilder(Intent intent) {
    	this.action = intent.getAction();
    	this.type = intent.getIntExtra(ACTION_TYPE, -1);
    	
    	this.swapPacket = (SwapPacket) intent.getSerializableExtra(ACTION_SWAP_PACKET);
    	
    	this.levelId = intent.getIntExtra(ACTION_LEVEL_ID, -1);
    	this.lightId = intent.getIntExtra(ACTION_LIGHT_ID, -1);
    	this.lightStatus = intent.getFloatExtra(ACTION_LIGHT_STATUS, -1);

    	this.temperature = intent.getFloatExtra(ACTION_TEMPERATURE, -1);
    }
    
    public String getAction() {
		return action;
	}
    
    public int getType() {
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
		/*SwapPacket res = new SwapPacket();
		res.setDest(this.regAddress);
		res.setRegAddress(this.regAddress);
		res.setRegId(this.regId);
		res.setRegValue(this.regValue);
		return res;*/
		return this.swapPacket;
	}
	
	public float getTemperature() {
		return temperature;
	}
	
}
