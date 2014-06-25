// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import java.util.List;

public class SettingsActivity extends PreferenceActivity
{
    public static class SettingsFragment extends PreferenceFragment
    {

        public void onCreate(Bundle bundle)
        {
            super.onCreate(bundle);
            String s = getArguments().getString("settings");
            if ("domotix".equals(s))
            {
                addPreferencesFromResource(0x7f040001);
            } else
            if ("notification".equals(s))
            {
                addPreferencesFromResource(0x7f040002);
                return;
            }
        }

        public SettingsFragment()
        {
        }
    }


    public static final String DATA_HOST_KEY = "domotix.data.host";
    public static final String INCOMING_BUS_HOST_KEY = "domotix.in.bus.host";
    public static final String INCOMING_BUS_PORT_KEY = "domotix.in.bus.port";
    public static final String NOTIFICATION_KEY = "notification";
    public static final String NOTIFICATION_URI_KEY = "notification.uri";
    public static final String NOTIFICATION_VIBRATE_KEY = "notification.vibrate";
    public static final String OUTGOING_BUS_PORT_KEY = "message.out.bus.port";

    public SettingsActivity()
    {
    }

    public void onBuildHeaders(List list)
    {
        loadHeadersFromResource(0x7f040000, list);
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        ActionBar actionbar = getActionBar();
        actionbar.setHomeButtonEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);
    }
}
