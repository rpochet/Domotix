// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.app.ProgressDialog;

// Referenced classes of package eu.pochet.domotix:
//            Configuration

class val.dialog2
    implements Runnable
{

    private final ProgressDialog val$dialog2;

    public void run()
    {
        val$dialog2.dismiss();
    }

    ()
    {
        val$dialog2 = progressdialog;
        super();
    }
}
