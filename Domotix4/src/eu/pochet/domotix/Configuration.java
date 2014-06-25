// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import eu.pochet.domotix.dao.LevelDao;
import eu.pochet.domotix.dao.ScenarioDao;

public class Configuration
{

    public Configuration()
    {
    }

    public static void updateConfig(Context context)
    {
        String s = PreferenceManager.getDefaultSharedPreferences(context).getString("domotix.data.host", null);
        LevelDao.updateFiles(s, context, new Runnable() {

            private final ProgressDialog val$dialog1;

            public void run()
            {
                dialog1.dismiss();
            }

            
            {
                dialog1 = progressdialog;
                super();
            }
        });
        ScenarioDao.updateFiles(s, context, new Runnable() {

            private final ProgressDialog val$dialog2;

            public void run()
            {
                dialog2.dismiss();
            }

            
            {
                dialog2 = progressdialog;
                super();
            }
        });
    }
}
