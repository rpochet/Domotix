package eu.pochet.domotix.service;

import android.app.IntentService;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import eu.pochet.android.Util;
import eu.pochet.domotix.Constants;
import eu.pochet.domotix.dao.DomotixDao;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.dao.Light;
import eu.pochet.domotix.dao.SwapDevice;
import eu.pochet.domotix.dao.SwapPacket;
import eu.pochet.domotix.dao.SwapRegister;
import eu.pochet.domotix.dao.SwapRegisterEndpoint;

/**
 * Handle INCOMING/OUTGOING intent that contains Domotix message:
 * - intent.getAction() == ActionBuilder.ACTION_IN: 
 *     Handle message received from DOMOTIX PAN network. Update model and broadcast a more specific message with action = ActionBuilder.ACTION_IN, if required 
 * - intent.getAction() == ActionBuilder.ACTION_OUT: 
 *     Send a message to DOMOTIX PAN network
 * 
 * @author romuald
 *
 */
public class ActionService extends IntentService {

	private static final String TAG = ActionService.class.getName();
	
	private String domotixBusInputHost = Constants.DOMOTIX_BUS_INPUT_HOST_DEFAULT;

	private int domotixBusInputPort = Constants.DOMOTIX_BUS_INPUT_PORT_DEFAULT;

    private String domotixMqUri = Constants.DOMOTIX_MQ_URI_DEFAULT;

    private ConnectionFactory connectionFactory = null;

	public ActionService() {
        super(TAG);
	}

    @Override
    public void onCreate() {
        super.onCreate();

        this.domotixBusInputHost = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(Constants.DOMOTIX_BUS_INPUT_HOST, Constants.DOMOTIX_BUS_INPUT_HOST_DEFAULT);
		/*this.domotixBusInputPort = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
				.getInt(Constants.DOMOTIX_BUS_INPUT_PORT, Constants.DOMOTIX_BUS_INPUT_PORT_DEFAULT);*/
        this.domotixBusInputPort = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(Constants.DOMOTIX_BUS_INPUT_PORT, Integer.toString(Constants.DOMOTIX_BUS_INPUT_PORT_DEFAULT)));

        this.domotixMqUri = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(Constants.DOMOTIX_MQ_URI, Constants.DOMOTIX_MQ_URI_DEFAULT);
        try {
            this.connectionFactory = new ConnectionFactory();
            this.connectionFactory.setUri(this.domotixMqUri);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Unable to create MQ connection factory", e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Unable to create MQ connection factory", e);
        } catch (KeyManagementException e) {
            Log.e(TAG, "Unable to create MQ connection factory", e);
        }
    }

    @Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();
		if(ActionBuilder.INTENT_TO_SWAP.equals(action)){
			handleOutputIntent(intent);
		} else if (ActionBuilder.INTENT_FROM_SWAP.equals(action)) {
			handleInputIntent(intent);
		} else {
			// TODO
		}
	}
	
	/**
	 * Send a message to DOMOTIX PAN network
	 * 
	 * @param intent
	 */
	private void handleOutputIntent(Intent intent) {
		
		SwapPacket swapPacket = new SwapPacket();
		boolean toSend = true;
		
		Level level = null;
		Light light = null;
		ActionBuilder action = new ActionBuilder(intent);
		switch(action.getType()) {
			case TYPE_LIGHT_SWITCH_OFF:
				light = DomotixDao.getLight(getApplicationContext(), action.getLightId());
				
				swapPacket.setDest(light.getSwapDeviceAddress());
				swapPacket.setFunc(Constants.FCT_SWAP_CUSTOM_1);
				swapPacket.setDest(light.getSwapDeviceAddress());
				swapPacket.setRegId(Constants.REGISTER_LIGHT_OUTPUT);
				swapPacket.setRegValue(new byte[]{
					(byte) light.getOutputNb(),
					ActionBuilder.LIGHT_STATUS_OFF
				});
				
				break;
			case TYPE_LIGHT_SWITCH_ON:
				light = DomotixDao.getLight(getApplicationContext(), action.getLightId());
				
				swapPacket.setDest(light.getSwapDeviceAddress());
				swapPacket.setFunc(Constants.FCT_SWAP_CUSTOM_1);
				swapPacket.setDest(light.getSwapDeviceAddress());
				swapPacket.setRegId(Constants.REGISTER_LIGHT_OUTPUT);
				swapPacket.setRegValue(new byte[]{
					(byte) light.getOutputNb(),
					(byte) ActionBuilder.LIGHT_STATUS_ON
				});
				
				break;
			case TYPE_LIGHT_SWITCH_TOGGLE:
				light = DomotixDao.getLight(getApplicationContext(), action.getLightId());
				
				swapPacket.setDest(light.getSwapDeviceAddress());
				swapPacket.setFunc(Constants.FCT_SWAP_CUSTOM_1);
				swapPacket.setDest(light.getSwapDeviceAddress());
				swapPacket.setRegId(Constants.REGISTER_LIGHT_OUTPUT);
				swapPacket.setRegValue(new byte[]{
					(byte) light.getOutputNb(),
					(byte) ActionBuilder.LIGHT_STATUS_TOGGLE
				});
				
				break;
			case LIGHT_STATUS:
				
				swapPacket.setDest(Constants.ADDR_BROADCAST);
				swapPacket.setFunc(Constants.FCT_SWAP_QUERY);
				swapPacket.setRegId(Constants.REG_LIGHT_OUTPUT);
				
				break;
			case TYPE_LIGHT_SWITCH_OFF_ALL:
				{
					List<Light> lights = DomotixDao.getLights(getApplicationContext());
				}
				break;
			case TYPE_LIGHT_SWITCH_ON_ALL:
				{
					List<Light> lights = DomotixDao.getLights(getApplicationContext());
				}
				break;
			default:
				toSend = false;
				break;
		}
		
		if(toSend) {
			sendMessage(swapPacket);
		}
	}

    private void sendMessage(SwapPacket swapPacket) {
        Connection connection = null;
        Channel channel = null;
        String data = Util.byteArrayToHexString(swapPacket.toByteArray());
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.basicPublish(ActionBuilder.ActionType.SWAP_PACKET.name(), ActionBuilder.ActionType.SWAP_PACKET.name(), new AMQP.BasicProperties(), swapPacket.toByteArray());
            Log.i(TAG, "SwapPacket " + data + " sent to Domotix bus");
        } catch (Exception e) {
            Log.e(TAG, "Failed to send message to Domotix bus", e);
        }
    }
	
	/**
	 * Handle message received from DOMOTIX PAN network
	 * 
	 * @param intent
	 */
	private void handleInputIntent(Intent intent) {
		ActionBuilder action = new ActionBuilder(intent);
		int regAddress = action.getSwapPacket().getRegAddress();
		int regId = action.getSwapPacket().getRegId();
		byte[] regValue = action.getSwapPacket().getRegValue();
		SwapDevice swapDevice = null;
		SwapRegister swapRegister = null;
		switch(action.getType()) {
			case LIGHT_STATUS:
				swapDevice = DomotixDao.getSwapDevice(getBaseContext(), regAddress);
                if(swapDevice != null) {
                    if (swapDevice.getProduct().startsWith(Constants.LIGHT_CONTROLLER_PRODUCT_CODE) && regId == Constants.REGISTER_LIGHT_OUTPUT) {
                        int i = 0;
                        List<Light> lights = DomotixDao.getLights(getApplicationContext());
                        for (Light light : lights) {
                            int lightStatus = (byte) regValue[i++];
                            if (lightStatus < 0) {
                                lightStatus += 0xff;
                            }
                            light.setStatus(lightStatus);
                        }
                        sendBroadcast(new ActionBuilder()
                                .setAction(ActionBuilder.INTENT_FROM_SWAP)
                                .setType(ActionBuilder.ActionType.TYPE_LIGHT_UPDATE)
                                .toIntent());
                    }
                } else {
                    // Should send MANAGEMENT message
                    Log.w(TAG, "No swap device found for packet " + action.getSwapPacket().toString());
                }
				break;
			case TEMPERATURE:
				swapDevice = DomotixDao.getSwapDevice(getBaseContext(), regAddress);
				swapRegister = swapDevice.getSwapRegisterById(action.getSwapPacket().getRegId());
				if(swapRegister != null) {
					swapRegister.setValue(action.getSwapPacket().getRegValue());
					if(swapRegister.getName().contains(Constants.SWAP_REGISTER_TEMPERATURE)) {
						for(SwapRegisterEndpoint swapRegisterEndpoint : swapRegister.getSwapRegisterEndpoints()) {
							if(swapRegisterEndpoint.getName().equals(Constants.SWAP_REGISTER_TEMPERATURE)) {
								float temperature = swapRegisterEndpoint.getValueAsInt() / 100f;
								sendBroadcast(new ActionBuilder()
									.setAction(ActionBuilder.INTENT_FROM_SWAP)
									.setType(ActionBuilder.ActionType.TEMPERATURE)
									.setTemperature(temperature)
									.toIntent()
								);
							}
						}
					}
				}
				break;
		}
	}

}
