package eu.pochet.domotix.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import eu.pochet.android.Util;
import eu.pochet.domotix.Constants;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.dao.DomotixDao;
import eu.pochet.domotix.dao.Light;

public class ActionService extends IntentService {

	private static final String TAG = ActionService.class.getName();
	
	private String domotixBusInputHost = Constants.DOMOTIX_BUS_INPUT_HOST_DEFAULT;

	private int domotixBusInputPort = Constants.DOMOTIX_BUS_INPUT_PORT_DEFAULT;

	public ActionService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();
		if(ActionBuilder.ACTION_OUT.equals(action)){
			handleOutputIntent(intent);
		} else if (ActionBuilder.ACTION_IN.equals(action)) {
			handleInputIntent(intent);
		} else {
			// TODO
		}
	}
	
	private void handleInputIntent(Intent intent) {
		ActionBuilder action = new ActionBuilder(intent);
		switch(action.getType()) {
			case ActionBuilder.TYPE_SWAP_PACKET:
				int regAddress = action.getSwapPacket().getRegAddress();
				int regId = action.getSwapPacket().getRegId();
				if(DomotixDao.getSwapDevice(getBaseContext(), regAddress).getProductCode().equals(Constants.LIGHT_CONTROLLER_PRODUCT_CODE) 
						&& regId >= Constants.LIGHT_OUTPUT_OFFSET) {
					int outputNb = regId - Constants.LIGHT_OUTPUT_OFFSET;
					Light light = DomotixDao.getLight(getApplicationContext(), regAddress, outputNb);
					int lightStatus = action.getSwapPacket().getRegValueAsInt();
					if(lightStatus == ActionBuilder.LIGHT_STATUS_TOGGLE) {
						light.setStatus(light.getStatus() == ActionBuilder.LIGHT_STATUS_OFF ? ActionBuilder.LIGHT_STATUS_ON : ActionBuilder.LIGHT_STATUS_OFF);
					} else {
						light.setStatus(lightStatus);
					}
					sendBroadcast(new ActionBuilder()
						.setAction(ActionBuilder.ACTION_IN)
						.setType(ActionBuilder.TYPE_LIGHT_SWITCH)
						.setLightId(light.getId())
						.toIntent());
				} else {
					
				}
			break;
		}
	}
	
	private void handleOutputIntent(Intent intent) {
		this.domotixBusInputHost = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.DOMOTIX_BUS_INPUT_HOST, Constants.DOMOTIX_BUS_INPUT_HOST_DEFAULT);
		this.domotixBusInputPort = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(Constants.DOMOTIX_BUS_INPUT_PORT, Constants.DOMOTIX_BUS_INPUT_PORT_DEFAULT);
		
		List<Integer> message = new ArrayList<Integer>();
		Level level = null;
		Light light = null;
		ActionBuilder action = new ActionBuilder(intent);
		switch(action.getType()) {
			case ActionBuilder.TYPE_LIGHT_SWITCH_OFF:
				light = DomotixDao.getLight(getApplicationContext(), action.getLightId());
				message.add(light.getSwapDeviceAddress());
				message.add(0);
				message.add(0);
				message.add(0);
				message.add(ActionBuilder.FCT_SWAP_COMMAND);
				message.add(light.getSwapDeviceAddress());
				message.add(light.getOutputNb());
				message.add(ActionBuilder.LIGHT_STATUS_OFF);
				break;
			case ActionBuilder.TYPE_LIGHT_SWITCH_ON:
				light = DomotixDao.getLight(getApplicationContext(), action.getLightId());
				message.add(light.getSwapDeviceAddress());
				message.add(0);
				message.add(0);
				message.add(0);
				message.add(ActionBuilder.FCT_SWAP_COMMAND);
				message.add(light.getSwapDeviceAddress());
				message.add(light.getOutputNb());
				message.add(ActionBuilder.LIGHT_STATUS_ON);
				break;
			case ActionBuilder.TYPE_LIGHT_SWITCH_TOGGLE:
				light = DomotixDao.getLight(getApplicationContext(), action.getLightId());
				message.add(light.getSwapDeviceAddress());
				message.add(0);
				message.add(0);
				message.add(0);
				message.add(ActionBuilder.FCT_SWAP_COMMAND);
				message.add(light.getSwapDeviceAddress());
				message.add(Constants.LIGHT_OUTPUT_OFFSET + light.getOutputNb());
				message.add(ActionBuilder.LIGHT_STATUS_TOGGLE);
				break;
			case ActionBuilder.TYPE_LIGHT_SWITCH_OFF_ALL:
				{
					List<Light> lights = DomotixDao.getLights(getApplicationContext());
				}
				break;
			case ActionBuilder.TYPE_LIGHT_SWITCH_ON_ALL:
				{
					List<Light> lights = DomotixDao.getLights(getApplicationContext());
				}
				break;
			default:
				break;
		}
		
		if(message.size() > 0) {
			int i = 0;
			byte[] tmp = new byte[message.size()];
			for (int d : message) {
				tmp[i++] = (byte) d;
			}
			sendMessage(Util.ByteArrayToHexString(tmp));
		}
	}

	private void sendMessage(String message) {
		try {
			InetAddress serverAddr = InetAddress.getByName(this.domotixBusInputHost);
			DatagramPacket packetMessage = new DatagramPacket(message.getBytes(), message.length(), serverAddr, this.domotixBusInputPort);

			DatagramSocket socket = new DatagramSocket();
			socket.send(packetMessage);
			Log.i(TAG, "Message " + message + " sent to Domotix bus");
		} catch (Exception e) {
			Log.e(TAG, "Failed to send message to Domotix bus", e);
		}
	}

}
