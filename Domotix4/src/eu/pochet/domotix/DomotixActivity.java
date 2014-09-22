package eu.pochet.domotix;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import eu.pochet.android.Util;
import eu.pochet.domotix.LevelFragment.CardLevelFragment;
import eu.pochet.domotix.LevelFragment.LightLevelFragment;
import eu.pochet.domotix.LevelFragment.LightLevelListFragment;
import eu.pochet.domotix.dao.DomotixDao;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.service.LightStatusUpdateService;
import eu.pochet.domotix.service.UpdateDaoService;
import eu.pochet.domotix.service.ZMQListenerService;

public class DomotixActivity extends Activity {
	
	private static final String TAG = DomotixActivity.class.getName();
	
	public final static boolean DEBUG = false;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.activity_main);

		((WifiManager) getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(true);

		ActionBar actionbar = getActionBar();
		actionbar.setHomeButtonEnabled(true);
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		if(getSizeName(this).equals("small")) { 
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
		}
	}
	
	private static String getSizeName(Context context) {
	    int screenLayout = context.getResources().getConfiguration().screenLayout;
	    screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

	    switch (screenLayout) {
		    case Configuration.SCREENLAYOUT_SIZE_SMALL:
		        return "small";
		    case Configuration.SCREENLAYOUT_SIZE_NORMAL:
		        return "normal";
		    case Configuration.SCREENLAYOUT_SIZE_LARGE:
		        return "large";
		    case 4: // Configuration.SCREENLAYOUT_SIZE_XLARGE is API >= 9
		        return "xlarge";
		    default:
		        return "undefined";
	    }
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
		
		startService(new Intent(this, LightStatusUpdateService.class));

		/*Intent intent = new Intent(this, UDPListenerService.class);
		intent.putExtra(Constants.DOMOTIX_BUS_OUTPUT_PORT, Constants.DOMOTIX_BUS_OUTPUT_PORT_DEFAULT);
		startService(intent);*/

		startService(new Intent(this, ZMQListenerService.class));
	}
	
	@Override
	protected void onStart() {
		Log.d(TAG, "Activity onStart");
		super.onStart();
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

		stopService(new Intent(this, LightStatusUpdateService.class));
		
		//stopService(new Intent(this, UDPListenerService.class));
		
		stopService(new Intent(this, ZMQListenerService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.domotix, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuitem) {
		if (menuitem.getItemId() == R.id.itemOptions) {
			// Display the fragment as the main content.
	        /*getFragmentManager().beginTransaction()
	                .replace(android.R.id.content, new SettingsFragment())
	                .commit();*/
			startActivityForResult(new Intent(this, SettingsActivity.class), 1);
		} else if (menuitem.getItemId() == R.id.updateConfig) {
			//ProgressDialog dialog1 = ProgressDialog.show(context, "Updating files", "");
	    	startService(new Intent(this, UpdateDaoService.class));
			//recreate();
		} else if (menuitem.getItemId() == R.id.updateLight) {
			// todo update light status
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
	
	/*public static class WebViewFragment extends android.webkit.WebViewFragment {

		public static final String ARG_URL = "url";

		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = super.onCreateView(inflater, container, savedInstanceState);
			getWebView().getSettings().setJavaScriptEnabled(true);
			getWebView().loadUrl(getArguments().getString(ARG_URL));
			return view;
		}
	}*/
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
	
	public static class TabListener implements android.app.ActionBar.TabListener {
		
		private final Activity mActivity;

		private final Class mClass;

		private Fragment mFragment;

		private final String mTag;

		private Bundle mArguments = null;

		public TabListener(Activity activity, String s, Class class1) {
			mActivity = activity;
			mTag = s;
			mClass = class1;
		}

		public TabListener(Activity activity, String s, Class class1, Bundle arguments) {
			this(activity, s, class1);
			mArguments = arguments;
		}

		public void onTabReselected(android.app.ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		}

		public void onTabSelected(android.app.ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
			if (mFragment == null) {
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				if(mArguments != null) {
					mFragment.setArguments(mArguments);
				}
				fragmentTransaction.add(0x1020002, mFragment, mTag);
				return;
			} else {
				fragmentTransaction.attach(mFragment);
				return;
			}
		}

		public void onTabUnselected(android.app.ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
			if (mFragment != null) {
				fragmentTransaction.detach(mFragment);
			}
		}
	}

}
