package eu.pochet.domotix;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.debug.hv.ViewServer;

import eu.pochet.domotix.dao.LevelDao;
import eu.pochet.domotix.service.LightStatusUpdateService;
import eu.pochet.domotix.service.UDPListenerService;

public class DomotixActivity extends Activity {
	
	private static final String DOMOCAN_TAG = "DomotixActivity";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		((WifiManager) getSystemService(WIFI_SERVICE)).setWifiEnabled(true);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();

		// Specify that the Home/Up button should not be enabled, since there is
		// no hierarchical
		// parent.
		actionBar.setHomeButtonEnabled(true);

		// Specify that we will be displaying tabs in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		actionBar.addTab(actionBar
				.newTab()
				.setText(R.string.title_section1)
				.setTabListener(
						new TabListener<LevelFragment.LightLevelFragment>(this,
								"section1",
								LevelFragment.LightLevelFragment.class)));

		actionBar.addTab(actionBar
				.newTab()
				.setText(R.string.title_section2)
				.setTabListener(
						new TabListener<LevelFragment.CardLevelFragment>(this,
								"section2",
								LevelFragment.CardLevelFragment.class)));

		actionBar.addTab(actionBar
				.newTab()
				.setText(R.string.title_section3)
				.setTabListener(
						new TabListener<DummySectionFragment>(this, "section3",
								DummySectionFragment.class)));

		PreferenceManager.setDefaultValues(this, R.xml.settings_domotix, false);
		PreferenceManager.setDefaultValues(this, R.xml.settings_notification, false);

		if (LevelDao.getLevels(getApplicationContext()) == null || LevelDao.getLevels(getApplicationContext()).size() == 0) {
			Configuration.updateConfig(this);
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		ViewServer.get(this).addWindow(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(DOMOCAN_TAG, "Activity onResume");

		Intent intent1 = new Intent(this, UDPListenerService.class);
		intent1.putExtra(UDPListenerService.OUTGOING_BUS_PORT_KEY, UDPListenerService.DEFAULT_OUTGOING_BUS_PORT);
		startService(intent1);

		Intent intent2 = new Intent(this, LightStatusUpdateService.class);
		startService(intent2);

		/*Intent intent3 = new Intent(this, LagartoEventService.class);
		startService(intent3);*/
		
		ViewServer.get(this).setFocusedWindow(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(DOMOCAN_TAG, "Activity onDestroy");

		Intent intent2 = new Intent(this, UDPListenerService.class);
		stopService(intent2);
		
		ViewServer.get(this).removeWindow(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.domotix, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itemOptions) {
			startActivityForResult(new Intent(this, SettingsActivity.class), 0);
		} else if (item.getItemId() == R.id.updateConfig) {
			Configuration.updateConfig(this);
		} else if (item.getItemId() == R.id.updateLight) {
			
		}
		return super.onOptionsItemSelected(item);
	}

	public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
		
		private Fragment mFragment;
		
		private final Activity mActivity;
		
		private final String mTag;
		
		private final Class<T> mClass;

		/**
		 * Constructor used each time a new tab is created.
		 * 
		 * @param activity
		 *            The host Activity, used to instantiate the fragment
		 * @param tag
		 *            The identifier tag for the fragment
		 * @param clz
		 *            The fragment's Class, used to instantiate the fragment
		 */
		public TabListener(Activity activity, String tag, Class<T> clz) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
		}

		/* The following are each of the ActionBar.TabListener callbacks */

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// Check if the fragment is already initialized
			if (mFragment == null) {
				// If not, instantiate and add it to the activity
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				// If it exists, simply attach it in order to show it
				ft.attach(mFragment);
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				// Detach the fragment, because another one is being attached
				ft.detach(mFragment);
			}
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// User selected the already selected tab. Usually do nothing.
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the primary sections of the app.
	 */
	/*
	 * public static class AppSectionsPagerAdapter extends FragmentPagerAdapter
	 * {
	 * 
	 * public AppSectionsPagerAdapter(FragmentManager fm) { super(fm); }
	 * 
	 * @Override public Fragment getItem(int i) { switch (i) { case 0: // The
	 * first section of the app is the most interesting -- it offers // a
	 * launchpad into the other demonstrations in this example application.
	 * return new LevelsFragment();
	 * 
	 * default: // The other sections of the app are dummy placeholders.
	 * Fragment fragment = new DummySectionFragment(); Bundle args = new
	 * Bundle(); args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
	 * fragment.setArguments(args); return fragment; } }
	 * 
	 * @Override public int getCount() { return 3; }
	 * 
	 * @Override public CharSequence getPageTitle(int position) { return
	 * "Section " + (position + 1); } }
	 */

	/**
	 * A fragment that launches other parts of the demo application.
	 */
	public static class LevelsFragment extends Fragment {

		/**
		 * The {@link android.support.v4.view.PagerAdapter} that will provide
		 * fragments for each of the three primary sections of the app. We use a
		 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
		 * will keep every loaded fragment in memory. If this becomes too memory
		 * intensive, it may be best to switch to a
		 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
		 */
		// AppSectionsPagerAdapter mAppSectionsPagerAdapter;

		/**
		 * The {@link ViewPager} that will display the three primary sections of
		 * the app, one at a time.
		 */
		// ViewPager mViewPager;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.light_level, container, false);

			// Create the adapter that will return a fragment for each of the
			// three primary sections
			// of the app.
			// mAppSectionsPagerAdapter = new
			// AppSectionsPagerAdapter(getFragmentManager());

			// Set up the ViewPager, attaching the adapter and setting up a
			// listener for when the
			// user swipes between sections.
			/*
			 * mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
			 * mViewPager.setAdapter(mAppSectionsPagerAdapter);
			 * mViewPager.setOnPageChangeListener(new
			 * ViewPager.SimpleOnPageChangeListener() {
			 * 
			 * @Override public void onPageSelected(int position) { } });
			 */

			// Demonstration of a collection-browsing activity.
			/*
			 * rootView.findViewById(R.id.demo_collection_button)
			 * .setOnClickListener(new View.OnClickListener() {
			 * 
			 * @Override public void onClick(View view) { Intent intent = new
			 * Intent(getActivity(), CollectionDemoActivity.class);
			 * startActivity(intent); } });
			 */

			// Demonstration of navigating to external activities.
			/*
			 * rootView.findViewById(R.id.demo_external_activity)
			 * .setOnClickListener(new View.OnClickListener() {
			 * 
			 * @Override public void onClick(View view) { // Create an intent
			 * that asks the user to pick a photo, but using //
			 * FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET, ensures that relaunching //
			 * the application from the device home screen does not return // to
			 * the external activity. Intent externalActivityIntent = new
			 * Intent(Intent.ACTION_PICK);
			 * externalActivityIntent.setType("image/*");
			 * externalActivityIntent.addFlags(
			 * Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			 * startActivity(externalActivityIntent); } });
			 */

			return rootView;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {

		public static final String ARG_SECTION_NUMBER = "section_number";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			TextView rootView = new TextView(getActivity());
			rootView.setText("text");
			return rootView;
		}
	}

}