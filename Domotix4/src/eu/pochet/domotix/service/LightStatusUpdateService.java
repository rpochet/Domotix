package eu.pochet.domotix.service;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.app.Service;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.IBinder;
import android.util.Log;

public class LightStatusUpdateService extends Service {
	
	public static final String ACTION = LightStatusUpdateService.class.getName();

	private AndroidHttpClient client = null;;

	private int delay = 5000;

	private long lastTimestamp = -1;

	private String lightStatusUrl = "http://192.168.1.4:3000/data/lights/status";

	private Timer timer = null;

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
		// client.close();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(ACTION, "Service onStartCommand");
		lastTimestamp = -1L;
		// client = AndroidHttpClient.newInstance("Domotix");
		BasicHttpParams basichttpparams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(basichttpparams, 4000);
		HttpConnectionParams.setSoTimeout(basichttpparams, 4000);
		if (intent != null && intent.getBooleanExtra("pool", false)) {
			startPollingLightStatusUpdate();
		} else {
			startLightStatusUpdateOnce();
		}
	}

	private void broadcastIntent(String s, String s1) {
		Intent intent = new Intent("LightStatusUpdateService");
		intent.putExtra("cardNb", s);
		intent.putExtra("status", s1);
		sendBroadcast(intent);
	}

	private void startLightStatusUpdateOnce() {
		new Thread(new Runnable() {
			public void run() {
				updateLightStatus();
			}
		}).start();
	}

	private void startPollingLightStatusUpdate() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				updateLightStatus();
			}
		}, 0L, delay);
	}

	private void updateLightStatus() {
		/*
		 * JsonReader jsonreader; String s; String s1; long l; jsonreader =
		 * null; s = null; s1 = null; l = 0L; JsonReader jsonreader1; HttpGet
		 * httpget = new HttpGet(lightStatusUrl); jsonreader1 = new
		 * JsonReader(new
		 * InputStreamReader(client.execute(httpget).getEntity().getContent()));
		 * jsonreader1.setLenient(true); jsonreader1.beginObject(); _L7: if
		 * (jsonreader1.hasNext()) goto _L2; else goto _L1 _L1:
		 * jsonreader1.endObject(); if (l <= lastTimestamp) goto _L4; else goto
		 * _L3 _L3: lastTimestamp = l; Log.i("LightStatusUpdateService",
		 * "New light status"); broadcastIntent(s, s1); _L13: if (jsonreader1 ==
		 * null) goto _L6; else goto _L5 _L5: jsonreader1.close(); _L11: return;
		 * _L2: String s2; label0: { s2 = jsonreader1.nextName(); if
		 * (!s2.equals("cardNb")) { break label0; } s =
		 * jsonreader1.nextString(); } goto _L7 label1: { if
		 * (!s2.equals("status")) { break label1; } s1 =
		 * jsonreader1.nextString(); } goto _L7 if (!s2.equals("date")) goto
		 * _L9; else goto _L8 _L8: String s3 = jsonreader1.nextString(); long l1
		 * = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
		 * Locale.FRENCH)).parse(s3).getTime(); l = l1; goto _L7 ParseException
		 * parseexception; parseexception; Log.d("LightStatusUpdateService",
		 * (new
		 * StringBuilder("Wrong date format received: ")).append(s3).toString
		 * ()); goto _L7 IllegalStateException illegalstateexception;
		 * illegalstateexception; jsonreader = jsonreader1; _L17:
		 * illegalstateexception.printStackTrace(); if (jsonreader == null) goto
		 * _L11; else goto _L10 _L10: try { jsonreader.close(); return; } catch
		 * (IOException ioexception3) { ioexception3.printStackTrace(); }
		 * return; _L9: jsonreader1.skipValue(); goto _L7 IOException
		 * ioexception1; ioexception1; jsonreader = jsonreader1; _L16:
		 * ioexception1.printStackTrace(); if (jsonreader == null) goto _L11;
		 * else goto _L12 _L12: try { jsonreader.close(); return; } catch
		 * (IOException ioexception2) { ioexception2.printStackTrace(); }
		 * return; _L4: Log.d("LightStatusUpdateService",
		 * "No new light status"); goto _L13 Exception exception; exception;
		 * jsonreader = jsonreader1; _L15: if (jsonreader != null) { try {
		 * jsonreader.close(); } catch (IOException ioexception) {
		 * ioexception.printStackTrace(); } } throw exception; IOException
		 * ioexception4; ioexception4; ioexception4.printStackTrace(); _L6:
		 * return; exception; if (true) goto _L15; else goto _L14 _L14:
		 * ioexception1; jsonreader = null; goto _L16 illegalstateexception;
		 * jsonreader = null; goto _L17
		 */
	}

}
