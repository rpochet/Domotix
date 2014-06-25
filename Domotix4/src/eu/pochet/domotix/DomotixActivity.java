// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.debug.hv.ViewServer;
import eu.pochet.domotix.dao.LevelDao;
import eu.pochet.domotix.service.LightStatusUpdateService;
import eu.pochet.domotix.service.UDPListenerService;
import java.util.List;

// Referenced classes of package eu.pochet.domotix:
//            Configuration, SettingsActivity

public class DomotixActivity extends Activity
{
    public static class DummySectionFragment extends Fragment
    {

        public static final String ARG_SECTION_NUMBER = "section_number";

        public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
        {
            TextView textview = new TextView(getActivity());
            textview.setText("text");
            return textview;
        }

        public DummySectionFragment()
        {
        }
    }

    public static class LevelsFragment extends Fragment
    {

        public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
        {
            return layoutinflater.inflate(0x7f030003, viewgroup, false);
        }

        public LevelsFragment()
        {
        }
    }

    public static class TabListener
        implements android.app.ActionBar.TabListener
    {

        private final Activity mActivity;
        private final Class mClass;
        private Fragment mFragment;
        private final String mTag;

        public void onTabReselected(android.app.ActionBar.Tab tab, FragmentTransaction fragmenttransaction)
        {
        }

        public void onTabSelected(android.app.ActionBar.Tab tab, FragmentTransaction fragmenttransaction)
        {
            if (mFragment == null)
            {
                mFragment = Fragment.instantiate(mActivity, mClass.getName());
                fragmenttransaction.add(0x1020002, mFragment, mTag);
                return;
            } else
            {
                fragmenttransaction.attach(mFragment);
                return;
            }
        }

        public void onTabUnselected(android.app.ActionBar.Tab tab, FragmentTransaction fragmenttransaction)
        {
            if (mFragment != null)
            {
                fragmenttransaction.detach(mFragment);
            }
        }

        public TabListener(Activity activity, String s, Class class1)
        {
            mActivity = activity;
            mTag = s;
            mClass = class1;
        }
    }


    private static final String DOMOCAN_TAG = "DomotixActivity";

    public DomotixActivity()
    {
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(0x7f030000);
        ((WifiManager)getSystemService("wifi")).setWifiEnabled(true);
        ActionBar actionbar = getActionBar();
        actionbar.setHomeButtonEnabled(true);
        actionbar.setNavigationMode(2);
        actionbar.addTab(actionbar.newTab().setText(0x7f080008).setTabListener(new TabListener(this, "section1", eu/pochet/domotix/LevelFragment$LightLevelFragment)));
        actionbar.addTab(actionbar.newTab().setText(0x7f080009).setTabListener(new TabListener(this, "section2", eu/pochet/domotix/LevelFragment$CardLevelFragment)));
        actionbar.addTab(actionbar.newTab().setText(0x7f08000a).setTabListener(new TabListener(this, "section3", eu/pochet/domotix/DomotixActivity$DummySectionFragment)));
        PreferenceManager.setDefaultValues(this, 0x7f040001, false);
        PreferenceManager.setDefaultValues(this, 0x7f040002, false);
        if (LevelDao.getLevels(getApplicationContext()) == null || LevelDao.getLevels(getApplicationContext()).size() == 0)
        {
            Configuration.updateConfig(this);
            try
            {
                Thread.sleep(15000L);
            }
            catch (InterruptedException interruptedexception)
            {
                interruptedexception.printStackTrace();
            }
        }
        ViewServer.get(this).addWindow(this);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(0x7f0b0000, menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected void onDestroy()
    {
        super.onDestroy();
        Log.d("DomotixActivity", "Activity onDestroy");
        stopService(new Intent(this, eu/pochet/domotix/service/UDPListenerService));
        ViewServer.get(this).removeWindow(this);
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        if (menuitem.getItemId() == 0x7f0c0012)
        {
            startActivityForResult(new Intent(this, eu/pochet/domotix/SettingsActivity), 0);
        } else
        if (menuitem.getItemId() == 0x7f0c0011)
        {
            Configuration.updateConfig(this);
        } else
        {
            menuitem.getItemId();
        }
        return super.onOptionsItemSelected(menuitem);
    }

    protected void onResume()
    {
        super.onResume();
        Log.d("DomotixActivity", "Activity onResume");
        Intent intent = new Intent(this, eu/pochet/domotix/service/UDPListenerService);
        intent.putExtra("message.out.bus.port", 54128);
        startService(intent);
        startService(new Intent(this, eu/pochet/domotix/service/LightStatusUpdateService));
        ViewServer.get(this).setFocusedWindow(this);
    }
}
