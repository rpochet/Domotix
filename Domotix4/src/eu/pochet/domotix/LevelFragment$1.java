// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// Referenced classes of package eu.pochet.domotix:
//            LevelFragment, NotificationHelper

class <init> extends BroadcastReceiver
{

    final LevelFragment this$0;

    public void onReceive(Context context, Intent intent)
    {
        if (onBroadcastReceive(context, intent))
        {
            NotificationHelper.notify(context);
        }
    }

    er()
    {
        this$0 = LevelFragment.this;
        super();
    }
}
