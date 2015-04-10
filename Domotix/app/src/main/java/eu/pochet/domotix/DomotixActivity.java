package eu.pochet.domotix;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import eu.pochet.android.TabListener;
import eu.pochet.android.Util;
import eu.pochet.domotix.LevelFragment.CardLevelFragment;
import eu.pochet.domotix.LevelFragment.LightLevelFragment;
import eu.pochet.domotix.LevelFragment.LightLevelListFragment;
import eu.pochet.domotix.dao.DomotixDao;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.service.AMQPSubscriberService;
import eu.pochet.domotix.service.ActionBuilder;
import eu.pochet.domotix.service.LightStatusUpdateService;
import eu.pochet.domotix.service.UpdateDaoService;

public class DomotixActivity extends Activity {
	
	private static final String TAG = DomotixActivity.class.getName();
	
	public final static boolean DEBUG = false;
	
	protected int temperatureNotificationId = 0;
	
	private BroadcastReceiver myBroadcastReceiver = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Activity onCreate");
		super.onCreate(savedInstanceState);

        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.DOMOTIX_MQ_CIENT_ID, "0").equals("0")) {
            Intent firstLaunchIntent = new Intent(this, SettingsActivity.class);
            firstLaunchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(firstLaunchIntent);
            // set the bool to false in next activity !
            finish();
        }

		setContentView(R.layout.activity_main);

		((WifiManager) getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(true);

		ActionBar actionbar = getActionBar();
		actionbar.setHomeButtonEnabled(true);
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		if(Util.getSizeName(this).equals(Util.SCREEN_SIZE_SMALL)) { 
			actionbar.addTab(actionbar
				.newTab()
				.setText(R.string.title_section1)
				.setTabListener(
						new TabListener(this, "section1",
								LightLevelListFragment.class)));
		} else {
			actionbar.addTab(actionbar
				.newTab()
				.setText(R.string.title_section1)
				.setTabListener(
						new TabListener(this, "section1",
								LightLevelFragment.class)));
			
		}
		actionbar.addTab(actionbar
				.newTab()
				.setText(R.string.title_section2)
				.setTabListener(
						new TabListener(this, "section2",
								CardLevelFragment.class)));
		actionbar.addTab(actionbar
				.newTab()
				.setText(R.string.title_section3)
				.setTabListener(
						new TabListener(this, "section3",
								WebViewFragment.class, Util.newBundle(WebViewFragment.ARG_URL, getString(R.string.plugin_network_state_url)))));
		actionbar.addTab(actionbar
				.newTab()
				.setText(R.string.title_section4)
				.setTabListener(
						new TabListener(this, "section4",
								WebViewFragment.class, Util.newBundle(WebViewFragment.ARG_URL, getString(R.string.plugin_pressure_url)))));

		List<Level> levels = DomotixDao.getLevels(getApplicationContext());
		if (levels == null || levels.size() == 0) {
			new AsyncTask<Context, Void,  Void>() {
				@Override
				protected Void doInBackground(Context... params) {
					DomotixDao.update(params[0]);
					return null;
				}
			}.execute(this);
			try {
				Thread.sleep(15000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			levels = DomotixDao.getLevels(getApplicationContext());
		}

		myBroadcastReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				ActionBuilder action = new ActionBuilder(intent);
				if (ActionBuilder.INTENT_FROM_SWAP.equals(action.getAction()) && ActionBuilder.ActionType.TEMPERATURE == action.getType()) {
                    ActionBuilder builder = new ActionBuilder(intent);

                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, DomotixActivity.class), Intent.FLAG_ACTIVITY_NEW_TASK);
					NotificationCompat.Builder mBuilder =
					        new NotificationCompat.Builder(context)
					        .setSmallIcon(R.mipmap.ic_launcher)
					        .setGroup(Constants.NOTIFICATION_DOMOTIX_GROUP)
					        .setOngoing(true)
					        .setContentTitle("Temperature " + builder.getLocation().getRoom().getName())
					        .setContentText(builder.getTemperature() + "°. Mise à jour: " + new SimpleDateFormat("HH:mm").format(new Date()))
					        .setContentIntent(pendingIntent);
					
					NotificationManager mNotificationManager =
						    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					mNotificationManager.notify(temperatureNotificationId, mBuilder.build());
				}
			}
		};

        startServices();
	}

    private void startServices() {
        Log.d(TAG, "Activity startServices");

        //startService(new Intent(this, LightStatusUpdateService.class));

        //startService(new Intent(this, UDPListenerService.class));

        startService(new Intent(this, AMQPSubscriberService.class));

        //startService(new Intent(this, WebsocketClientUpdateService.class));

        registerReceiver(myBroadcastReceiver, new IntentFilter(ActionBuilder.INTENT_FROM_SWAP));
    }

    private void stopServices() {
        Log.d(TAG, "Activity stopServices");

        //stopService(new Intent(this, LightStatusUpdateService.class));

        //stopService(new Intent(this, UDPListenerService.class));

        stopService(new Intent(this, AMQPSubscriberService.class));

        //stopService(new Intent(this, WebsocketClientUpdateService.class));

        unregisterReceiver(myBroadcastReceiver);
    }

    @Override
	protected void onStart() {
		Log.d(TAG, "Activity onStart");
		super.onStart();
        DomotixDao.getLevels(getApplicationContext());
	}

    @Override
    protected void onRestart() {
        Log.d(TAG, "Activity onRestart");
        super.onRestart();
    }

    /**
	 * Screen rotation
	 * Back to app
	 * 
	 */
	@Override
	protected void onResume() {
		Log.d(TAG, "Activity onResume");
		super.onResume();
	}

    @Override
    protected void onPause() {
        Log.d(TAG, "Activity onPause");
        super.onPause();
    }
	
	@Override
	protected void onStop() {
		Log.d(TAG, "Activity onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "Activity onDestroy");
		super.onDestroy();

        stopServices();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.domotix, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuitem) {
		if (menuitem.getItemId() == R.id.itemOptions) {
			startActivityForResult(new Intent(this, SettingsActivity.class), 1);
		} else if (menuitem.getItemId() == R.id.updateConfig) {
			//ProgressDialog dialog1 = ProgressDialog.show(context, "Updating files", "");
	    	startService(new Intent(this, UpdateDaoService.class));
			//recreate();
		} else if (menuitem.getItemId() == R.id.updateLight) {
			startService(new Intent(this, LightStatusUpdateService.class));
		}
		return super.onOptionsItemSelected(menuitem);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1) {
			recreate();
		}
	}
	
	public static class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
		
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.preferences);
	        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	        initSummary(getPreferenceScreen());
	    }

		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	        updatePrefSummary(findPreference(key));
		}

	    private void initSummary(Preference p) {
	        if (p instanceof PreferenceGroup) {
	            PreferenceGroup pGrp = (PreferenceGroup) p;
	            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
	                initSummary(pGrp.getPreference(i));
	            }
	        } else {
	            updatePrefSummary(p);
	        }
	    }

	    private void updatePrefSummary(Preference p) {
	        if (p instanceof ListPreference) {
	            ListPreference listPref = (ListPreference) p;
	            p.setSummary(listPref.getEntry());
	        }
	        if (p instanceof EditTextPreference) {
	            EditTextPreference editTextPref = (EditTextPreference) p;
	            if (p.getTitle().toString().contains("assword")) {
	                p.setSummary("******");
	            } else {
	                p.setSummary(editTextPref.getText());
	            }
	        }
	        if (p instanceof MultiSelectListPreference) {
	            EditTextPreference editTextPref = (EditTextPreference) p;
	            p.setSummary(editTextPref.getText());
	        }
	    }
	}

	public static class SettingsFragment extends PreferenceFragment {
		 
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        // Load the preferences from an XML resource
	        addPreferencesFromResource(R.xml.preferences);
	    }
	    
	}
	
	public static class WebViewFragment extends Fragment {

		public static final String ARG_URL = "url";

		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.plugin_layout, container, false);
			WebView webView = (WebView) view.findViewById(R.id.webView1);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.loadUrl(getArguments().getString(ARG_URL));
			return view;
		}
	}

}
