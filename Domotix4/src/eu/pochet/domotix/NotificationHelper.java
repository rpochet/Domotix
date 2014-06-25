// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;

public class NotificationHelper
{

    public NotificationHelper()
    {
    }

    public static void notify(Context context)
    {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedpreferences.getBoolean("notification", false))
        {
            Ringtone ringtone = RingtoneManager.getRingtone(context, Uri.parse(sharedpreferences.getString("notification.uri", null)));
            ringtone.play();
            SystemClock.sleep(500L);
            ringtone.stop();
        }
        if (sharedpreferences.getBoolean("notification.vibrate", false))
        {
            ((Vibrator)context.getSystemService("vibrator")).vibrate(300L);
        }
    }
}
