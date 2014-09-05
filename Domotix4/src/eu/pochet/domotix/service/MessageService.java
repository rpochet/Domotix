package eu.pochet.domotix.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import eu.pochet.domotix.Constants;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.dao.LevelDao;
import eu.pochet.domotix.dao.Light;

public class MessageService extends IntentService {

	private static final String TAG = MessageService.class.getName();

	public static final String ACTION = "Domotix.MessageService.ACTION";

	public static final String LEVEL_ID = "Domotix.MessageService.LEVEL";

	public static final String LIGHT_ID = "Domotix.MessageService.LIGHT";
	
	public MessageService() {
		super(ACTION);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		String domotixIncomingBusHost = (String) intent.getCharSequenceExtra(Constants.DOMOTIX_BUS_INPUT_HOST);
		if(domotixIncomingBusHost == null) {
			domotixIncomingBusHost = Constants.DOMOTIX_BUS_INPUT_HOST_DEFAULT;
		}
		int domotixIncomingBusPort = intent.getIntExtra(Constants.DOMOTIX_BUS_INPUT_PORT, Constants.DOMOTIX_BUS_INPUT_PORT_DEFAULT);
		
		Message message = null;

		String action = intent.getStringExtra(MessageService.ACTION);
		if ("switch_off_all".equals(action)) {
			int levelId = intent.getIntExtra(MessageService.LEVEL_ID, 0);
			Level level = LevelDao.getLevel(getApplicationContext(), levelId);
			for (Light light : level.getLights()) {
				message = new Message(light.getCardAddress(), Message.COMMAND);
				//message.setRegisterNb(light.getOutputNb());
				message.setRegisterValue("00");
			}
		} else if ("switch_on_all".equals(action)) {
			int levelId = intent.getIntExtra(MessageService.LEVEL_ID, 0);
			Level level = LevelDao.getLevel(getApplicationContext(), levelId);
			for (Light light : level.getLights()) {
				message = new Message(light.getCardAddress(), Message.COMMAND);
				//message.setRegisterNb(light.getOutputNb());
				message.setRegisterValue("FF");
			}
		} else if ("switch_on_light".equals(action)) {
			int lightId = intent.getIntExtra(MessageService.LIGHT_ID, 0);
			Light light = LevelDao.getLight(getApplicationContext(), lightId);
			message = new Message(light.getCardAddress(), Message.COMMAND);
			//message.setRegisterNb(light.getOutputNb());
			message.setRegisterValue("FF");
		} else if ("switch_off_light".equals(action)) {
			int lightId = intent.getIntExtra(MessageService.LIGHT_ID, 0);
			Light light = LevelDao.getLight(getApplicationContext(), lightId);
			message = new Message(light.getCardAddress(), Message.COMMAND);
			//message.setRegisterNb(light.getOutputNb());
			message.setRegisterValue("00");
		} else if ("toggle_light".equals(action)) {
			int lightId = intent.getIntExtra(MessageService.LIGHT_ID, 0);
			Light light = LevelDao.getLight(getApplicationContext(), lightId);
			message = new Message(light.getCardAddress(), Message.COMMAND);
			//message.setRegisterNb(light.getOutputNb());
			message.setRegisterValue("00");
		} else {
			return;
		}

		sendMessage(message, domotixIncomingBusHost, domotixIncomingBusPort);
	}

	private void sendMessage(Message message, String domotixIncomingBusHost, int domotixIncomingBusPort) {
		try {
			String data = message.getData();
			InetAddress serverAddr = InetAddress.getByName(domotixIncomingBusHost);
			DatagramPacket packetMessage = new DatagramPacket(data.getBytes(), data.length(), serverAddr, domotixIncomingBusPort);

			DatagramSocket socket = new DatagramSocket();
			socket.send(packetMessage);
			Log.i(TAG, "Message " + data + " sent to Domotix bus");
		} catch (Exception e) {
			Log.e(TAG, "Failed to send message to Domotix bus", e);
		}
	}

}
