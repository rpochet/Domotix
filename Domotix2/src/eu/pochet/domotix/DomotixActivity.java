package eu.pochet.domotix;

import java.util.Iterator;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.dao.LevelDao;
import eu.pochet.domotix.dao.Light;
import eu.pochet.domotix.service.LightStatusUpdateService;
import eu.pochet.domotix.service.MessageService;

public class DomotixActivity extends Activity implements GestureOverlayView.OnGesturePerformedListener, OnSharedPreferenceChangeListener
{
	private static final String DOMOCAN_TAG = "DomotixActivity";
	
	private ViewFlipper viewFlipper;
	private GestureLibrary mLibrary;
	private int mCurrentLevelId = 1;

	private String dataHost = null;

	private boolean shouldNotify = false;
	private Uri notification = null;

	private MulticastLock lock = null;
	
	BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(LightStatusUpdateService.BROADCAST_ACTION.equals(action))
			{
				String cardNb = intent.getStringExtra("cardNb");
				String status = intent.getStringExtra("status");
				updateLightStatus(cardNb, status);
			}
			/*else if(UDPListenerService.BROADCAST_ACTION.equals(action))
			{
				String message =  Util.ByteArrayToHexString(intent.getByteArrayExtra("message"));
				String cardAddress =   message.substring(6, 22);
				String status = message.substring(24, intent.getIntExtra("messageLength", 24) * 2);
				updateLightStatus(cardAddress, status);
			}*/
			
			if(notification != null &&  shouldNotify)
			{
				Ringtone r = RingtoneManager.getRingtone(context, notification);
				r.play();
				SystemClock.sleep(1000);
				r.stop();
				shouldNotify = false;
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		WifiManager wifiManager = ((WifiManager) getSystemService(WIFI_SERVICE));
		wifiManager.setWifiEnabled(true);
		lock = wifiManager.createMulticastLock(DOMOCAN_TAG);
		lock.acquire();
		
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
		updatePreferences();

		this.viewFlipper = (ViewFlipper) this.findViewById(R.id.viewLevelFlipper);

		List<Level> levels = LevelDao.getLevels(this);
		if(levels.size() == viewFlipper.getChildCount())
		{
			int i = 0;
			ListView lv = null;
			Level level = null;
			LevelAdapter levelAdapter = null;
			for (Iterator<Level> iterator = levels.iterator(); iterator.hasNext();)
			{
				level = iterator.next();
				levelAdapter = new LevelAdapter(this, level);
				lv = (ListView) viewFlipper.getChildAt(i++);
				lv.setAdapter(levelAdapter);
			}
		} 
		else
		{
			updateConfig();
		}

		if(savedInstanceState != null)
		{
			this.mCurrentLevelId = savedInstanceState.getInt("level", 2);
		}
		else
		{
			this.mCurrentLevelId = 2;
		}
		this.viewFlipper.setDisplayedChild(this.mCurrentLevelId - 1);

		this.mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		this.mLibrary.load();
		
		GestureOverlayView gestures = (GestureOverlayView) this.findViewById(R.id.gestures);
		gestures.addOnGesturePerformedListener(this);
		
		updateLevelName();
		
		registerReceiver(myBroadcastReceiver, new IntentFilter(LightStatusUpdateService.BROADCAST_ACTION));
		//registerReceiver(myBroadcastReceiver, new IntentFilter(UDPListenerService.BROADCAST_ACTION));
	}

	public void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
		outState.putInt("level", this.mCurrentLevelId);
	}
    
    @Override
    protected void onResume() 
    {
        super.onResume();
		Log.d(DOMOCAN_TAG, "Activity onResume");
        
		/*Intent intent1 = new Intent(this, UDPListenerService.class);
		startService(intent1);*/
		
		Intent intent2 = new Intent(this, LightStatusUpdateService.class);
		startService(intent2);
    }
    
    @Override
    protected void onDestroy() 
    {
        super.onDestroy();
		Log.d(DOMOCAN_TAG, "Activity onDestroy");

		/*Intent intent2 = new Intent(this, UDPListenerService.class);
    	stopService(intent2);*/

		unregisterReceiver(myBroadcastReceiver);
    }
	
	@Override
	public void finish()
	{
		lock.release();
	    super.finish();
	}

	private void updateLevelName()
	{
		Level level = LevelDao.getLevel(this, this.mCurrentLevelId);
		setTitle(level.getName());
	}
	
	@Override 
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) 
	{ 
		List<Prediction> predictions = this.mLibrary.recognize(gesture);
		if(predictions.size() > 0 && predictions.get(0).score > 1.0D) 
		{ 
			String action = predictions.get(0).name; 
			if("ltr".equals(action)) 
			{ 
				this.viewFlipper.showPrevious(); 
				this.mCurrentLevelId = this.viewFlipper.getDisplayedChild() + 1; 
				updateLevelName(); 
			} 
			else if("rtl".equals(action))
			{ 
				this.viewFlipper.showNext();
				this.mCurrentLevelId = this.viewFlipper.getDisplayedChild() + 1;
				updateLevelName(); 
			} 
			else if(action.startsWith("startall")) 
			{ 
				Intent intent = new Intent(this, MessageService.class);
				intent.putExtra(MessageService.LEVEL_ID, this.mCurrentLevelId);
				intent.putExtra(MessageService.ACTION, "switch_on_all");
				this.startService(intent); 
			} 
			else if(action.startsWith("stopall")) 
			{ 
				Intent intent = new Intent(this, MessageService.class); 
				intent.putExtra(MessageService.LEVEL_ID, this.mCurrentLevelId); 
				intent.putExtra(MessageService.ACTION, "switch_off_all"); 
				this.startService(intent);
			} 
		} 
	}

	public class LevelAdapter extends BaseAdapter implements OnClickListener
	{
		private Context mContext;
		private Level level;
		private LayoutInflater mInflater;

		public LevelAdapter(Context context, Level level)
		{
			this.mContext = context;
			this.level = level;
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount()
		{
			return level.getLights().size();
		}

		@Override
		public Object getItem(int position)
		{
			return level.getLights().get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder;
			Light light = this.level.getLights().get(position);
			if (convertView == null)
			{
				holder = new ViewHolder();
				convertView = (LinearLayout) mInflater.inflate(R.layout.light_item, parent, false);
				holder.status = (ImageView) convertView.findViewById(R.id.status);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			} 
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}

			ImageView i = (ImageView) convertView.findViewById(R.id.status);
			i.setClickable(true);
			i.setFocusable(true);
			i.setTag(holder);
			i.setOnClickListener(this);
			
			holder.light = light;
			holder.status.setImageResource(light.getStatus().equals("00") ? R.drawable.lightbulb_off : R.drawable.lightbulb_on);
			holder.name.setText(light.getName());
			return convertView;
		}

		@Override
		public void onClick(View v)
		{
			Light light = ((ViewHolder) v.getTag()).light;
			Intent intent = new Intent(DomotixActivity.this, MessageService.class);
			intent.putExtra(MessageService.ACTION, "toggle_light");
			intent.putExtra(MessageService.LIGHT_ID, light.getId());
			DomotixActivity.this.startService(intent);
			
			Toast.makeText(this.mContext, "Toogle " + light.getName(), Toast.LENGTH_LONG).show();
			
			SystemClock.sleep(2000);
			
			shouldNotify = true;
			
			updateLight();
		}

		public class ViewHolder
		{
			Light light;

			ImageView status;

			TextView name;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.domotix, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private static final int CODE_RETOUR = 1;

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == R.id.itemOptions)
		{
			startActivityForResult(new Intent(this, DomotixPreferences.class), CODE_RETOUR);
		}
		else if(item.getItemId() == R.id.updateConfig)
		{
			updateConfig();
		}
		else if(item.getItemId() == R.id.updateLight)
		{
			updateLight();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == CODE_RETOUR)
		{
			updatePreferences();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void updateConfig()
	{
		final ProgressDialog dialog = ProgressDialog.show(this, "", "Loading. Please wait...", true);
		LevelDao.updateFiles(this.dataHost, this, new Runnable() 
		{
			@Override
            public void run()
            {
				dialog.dismiss();
            }
			
		});
	}
	
	private void updateLight()
	{
		Intent intent = new Intent(this, LightStatusUpdateService.class);
		startService(intent);
	}

	private void updateLightStatus(String cardAddress, String status) 
	{
		int statusLength = status.length();
		String lightStatus = null;
		Level level = null;
		ListView levelView = null;
		for (int i = 1; i <= viewFlipper.getChildCount(); i++) 
		{
			level = LevelDao.getLevel(this, i);
			/*for (Light light : level.getLights())
			{
				if(statusLength > ((light.getOutputNb() + 1) * 2))
				{
					lightStatus = status.substring(light.getOutputNb() * 2, (light.getOutputNb() + 1) * 2);
					light.nextString(Integer.parseInt(lightStatus, 16));
				}
			}*/
			levelView = (ListView) viewFlipper.getChildAt(i - 1);
			((LevelAdapter) levelView.getAdapter()).notifyDataSetChanged();
			levelView.invalidate();
		}
	}
	
	private void updatePreferences()
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		this.dataHost = preferences.getString(DomotixPreferences.DATA_HOST_KEY, DomotixPreferences.DEFAULT_DATA_HOST);
		
		String notificationStr = preferences.getString(DomotixPreferences.NOTIFICATION_KEY, DomotixPreferences.DEFAULT_NOTIFICATION);
		if(notificationStr != null && notificationStr.length() > 0) 
		{
			this.notification = Uri.parse(notificationStr);
		}
		else 
		{
			this.notification = null;
		}
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		updatePreferences();
	}

}