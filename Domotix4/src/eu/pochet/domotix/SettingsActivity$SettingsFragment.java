// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.os.Bundle;
import android.preference.PreferenceFragment;

// Referenced classes of package eu.pochet.domotix:
//            SettingsActivity

public static class _cls9 extends PreferenceFragment
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

    public _cls9()
    {
    }
}
