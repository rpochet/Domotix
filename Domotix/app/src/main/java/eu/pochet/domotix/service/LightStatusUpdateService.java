package eu.pochet.domotix.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import eu.pochet.domotix.Constants;
import eu.pochet.domotix.dao.DomotixDao;

public class LightStatusUpdateService extends Service {
	
	public static final String ACTION = LightStatusUpdateService.class.getName();

	private int period = 5000;

	private long lastTimestamp = -1;

	private Timer timer = null;

	private String domotixBusOutputHost = Constants.DOMOTIX_BUS_OUTPUT_HOST_DEFAULT;

	private int domotixBusOutputPort = Constants.DOMOTIX_BUS_OUTPUT_PORT_DEFAULT;

	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onCreate() {
		Log.d(ACTION, "Service onCreate");
	}

	public void onDestroy() {
		Log.d(ACTION, "Service onDestroy");
		if (timer != null) {
			timer.cancel();
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(ACTION, "Service onStartCommand");
		
		this.domotixBusOutputHost = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.DOMOTIX_BUS_OUTPUT_HOST, Constants.DOMOTIX_BUS_OUTPUT_HOST_DEFAULT);

		this.domotixBusOutputPort = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.DOMOTIX_BUS_OUTPUT_PORT, Integer.toString(Constants.DOMOTIX_BUS_OUTPUT_PORT_DEFAULT)));
		
		lastTimestamp = -1L;
		//if (intent != null && intent.getBooleanExtra("pool", false)) {
			startPollingLightStatusUpdate();
		//} else {
		//	startLightStatusUpdateOnce();
		//}
	}

	/*private void startLightStatusUpdateOnce() {
		new Thread(new Runnable() {
			public void run() {
				updateLightStatus();
			}
		}).start();
	}*/

	private void startPollingLightStatusUpdate() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				List<Integer> outputs = null;
				URL url = null;
				InputStream is = null;
				try {
					url = new URL("http", domotixBusOutputHost, domotixBusOutputPort, "panstamp/_design/devices/_view/controller_output");
					is = url.openStream();
					outputs = DomotixDao.readOutputs(IOUtils.toByteArray(is));
					updateLightStatus((ArrayList<Integer>) outputs);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 0L, period);
	}

	private void updateLightStatus(ArrayList<Integer> outputs) {
		new ActionBuilder()
			.setAction(ActionBuilder.INTENT_FROM_SWAP)
			.setType(ActionBuilder.ActionType.LIGHT_STATUS)
			.setLightsStatus(outputs)
			.sendMessage(getBaseContext());
	}

}
