package eu.pochet.domotix.service;

import android.content.Context;
import android.content.Intent;
import eu.pochet.domotix.dao.SwapPacket;

public class ActionBuilder {

	public static final String ACTION_IN = "ACTION_IN";
	public static final String ACTION_OUT = "ACTION_OUT";
	
	public static final int TYPE_LIGHT_SWITCH_ON = 0;
	public static final int TYPE_LIGHT_SWITCH_OFF = 1;
	public static final int TYPE_LIGHT_SWITCH_TOGGLE = 2;
	public static final int TYPE_LIGHT_SWITCH_ON_ALL = 3;
	public static final int TYPE_LIGHT_SWITCH_OFF_ALL = 4;
	public static final int TYPE_LIGHT_SWITCH = 5;
	public static final int TYPE_SWAP_PACKET = 6;

	public static final int LIGHT_STATUS_OFF = 0;
	public static final int LIGHT_STATUS_ON = 254;
	public static final int LIGHT_STATUS_TOGGLE = -1;
	
	public static final int FCT_SWAP_STATUS = 0;
	public static final int FCT_SWAP_QUERY = 1;
	public static final int FCT_SWAP_COMMAND = 2;

	private static final String ACTION_TYPE = "type";
	private static final String ACTION_LEVEL_ID = "levelId";
	private static final String ACTION_LIGHT_ID = "lightId";
	private static final String ACTION_LIGHT_STATUS = "lightStatus";
	private static final String ACTION_SWAP_PACKET = "swapPacket";
	
	private String action;
	
	private int type;
	
	private SwapPacket swapPacket; 
	
	private int levelId;
	
	private int lightId;
	
	private int lightStatus;

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
	
	public Intent toIntent() {
		Intent intent = new Intent(this.action);
		intent.putExtra(ACTION_TYPE, this.type);
        
		intent.putExtra(ACTION_SWAP_PACKET, this.swapPacket);
				
		intent.putExtra(ACTION_LEVEL_ID, this.levelId);
		intent.putExtra(ACTION_LIGHT_ID, this.lightId);
		intent.putExtra(ACTION_LIGHT_STATUS, this.lightStatus);

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

		context.startService(intent);
    }
    
    public ActionBuilder(Intent intent) {
    	this.action = intent.getAction();
    	this.type = intent.getIntExtra(ACTION_TYPE, -1);
    	
    	this.swapPacket = (SwapPacket) intent.getSerializableExtra(ACTION_SWAP_PACKET);
    	
    	this.levelId = intent.getIntExtra(ACTION_LEVEL_ID, -1);
    	this.lightId = intent.getIntExtra(ACTION_LIGHT_ID, -1);
    	this.lightStatus = intent.getIntExtra(ACTION_LIGHT_STATUS, -1);
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
    
    public int getLightStatus() {
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
}
