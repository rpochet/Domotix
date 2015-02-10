package eu.pochet.domotix.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import eu.pochet.android.Stream;
import eu.pochet.android.Stream.StreamDelegate;
import eu.pochet.domotix.Constants;

public class WebsocketClientUpdateService extends Service {

	public static final String ACTION = WebsocketClientUpdateService.class.getName();

	private String domotixBusOutputHost = Constants.DOMOTIX_BUS_OUTPUT_HOST_DEFAULT;

	private int domotixBusOutputPort = Constants.DOMOTIX_BUS_OUTPUT_PORT_DEFAULT;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(ACTION, "Service onStartCommand");
		
		this.domotixBusOutputHost = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.DOMOTIX_BUS_OUTPUT_HOST, Constants.DOMOTIX_BUS_OUTPUT_HOST_DEFAULT);

		this.domotixBusOutputPort = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.DOMOTIX_BUS_OUTPUT_PORT, Integer.toString(Constants.DOMOTIX_BUS_OUTPUT_PORT_DEFAULT)));
		
		startWebSocket();
		
		return START_REDELIVER_INTENT;
	}

    private void startWebSocket() {
    	Stream stream = new Stream(getApplicationContext(), this.domotixBusOutputHost, this.domotixBusOutputPort, false, "/engine.io/default", "polling");
    	stream.delegate = new StreamDelegate() {
			
			public void streamDidReconnect(Stream stream) {
				Log.i(ACTION, "Stream reconnected");
			}
			
			public void streamDidDisconnect(Stream stream) {
				Log.i(ACTION, "Stream disconnected");
			}
			
			public void streamDidConnect(Stream stream) {
				Log.i(ACTION, "Stream connected");
			}
			
			public String didSetSessionId(Stream stream, String sid) {
				Log.i(ACTION, String.format("Session ID %s", sid));
				return null;
			}
		};
		stream.connectToServer();
	}

}
