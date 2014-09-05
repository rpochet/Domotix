package eu.pochet.domotix;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.wifi.WifiManager;
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
import android.widget.TextView;
import eu.pochet.domotix.LevelFragment.CardLevelFragment;
import eu.pochet.domotix.LevelFragment.LightLevelFragment;
import eu.pochet.domotix.dao.LevelDao;
import eu.pochet.domotix.service.LightStatusUpdateService;
import eu.pochet.domotix.service.UDPListenerService;
import eu.pochet.domotix.service.ZMQListenerService;

public class DomotixActivity extends Activity {
	
	private static final String TAG = DomotixActivity.class.getName();

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.activity_main);

		((WifiManager) getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(true);

		ActionBar actionbar = getActionBar();
		actionbar.setHomeButtonEnabled(true);
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionbar.addTab(actionbar
				.newTab()
				.setText(R.string.title_section1)
				.setTabListener(
						new TabListener(this, "section1",
								LightLevelFragment.class)));
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
								DummySectionFragment.class)));

		if (LevelDao.getLevels(getApplicationContext()) == null || LevelDao.getLevels(getApplicationContext()).size() == 0) {
			Configuration.updateConfig(this);
			try {
				Thread.sleep(15000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.domotix, menu);
		return super.onCreateOptionsMenu(menu);
	}

	protected void onDestroy() {
		Log.d(TAG, "Activity onDestroy");

		super.onDestroy();

		stopService(new Intent(this, LightStatusUpdateService.class));

		stopService(new Intent(this, UDPListenerService.class));

		stopService(new Intent(this, ZMQListenerService.class));
	}

	public boolean onOptionsItemSelected(MenuItem menuitem) {
		if (menuitem.getItemId() == R.id.itemOptions) {
			// Display the fragment as the main content.
	        /*getFragmentManager().beginTransaction()
	                .replace(android.R.id.content, new SettingsFragment())
	                .commit();*/
			startActivityForResult(new Intent(this, SettingsActivity.class), 1);
		} else if (menuitem.getItemId() == R.id.updateConfig) {
			Configuration.updateConfig(this);
			recreate();
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

	protected void onResume() {
		Log.d(TAG, "Activity onResume");

		super.onResume();
		
		startService(new Intent(this, LightStatusUpdateService.class));

		Intent intent = new Intent(this, UDPListenerService.class);
		intent.putExtra(Constants.DOMOTIX_BUS_OUTPUT_PORT, Constants.DOMOTIX_BUS_OUTPUT_PORT_DEFAULT);
		startService(intent);

		startService(new Intent(this, ZMQListenerService.class));
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
	
	public static class DummySectionFragment extends Fragment {
		
		public static final String ARG_SECTION_NUMBER = "section_number";

		public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
			TextView textview = new TextView(getActivity());
			textview.setText("text" + bundle.getInt(ARG_SECTION_NUMBER));
			return textview;
		}
	}

	public static class LevelsFragment extends Fragment {
		public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
			return layoutinflater.inflate(R.layout.light_level, viewgroup, false);
		}
	}

	public static class TabListener implements android.app.ActionBar.TabListener {
		
		private final Activity mActivity;

		private final Class mClass;

		private Fragment mFragment;

		private final String mTag;

		public TabListener(Activity activity, String s, Class class1) {
			mActivity = activity;
			mTag = s;
			mClass = class1;
		}

		public void onTabReselected(android.app.ActionBar.Tab tab, FragmentTransaction fragmenttransaction) {
		}

		public void onTabSelected(android.app.ActionBar.Tab tab, FragmentTransaction fragmenttransaction) {
			if (mFragment == null) {
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				fragmenttransaction.add(0x1020002, mFragment, mTag);
				return;
			} else {
				fragmenttransaction.attach(mFragment);
				return;
			}
		}

		public void onTabUnselected(android.app.ActionBar.Tab tab, FragmentTransaction fragmenttransaction) {
			if (mFragment != null) {
				fragmenttransaction.detach(mFragment);
			}
		}
	}

}
