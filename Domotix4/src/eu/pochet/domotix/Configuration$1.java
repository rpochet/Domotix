// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.app.ProgressDialog;

// Referenced classes of package eu.pochet.domotix:
//            Configuration

class val.dialog1
    implements Runnable
{

    private final ProgressDialog val$dialog1;

    public void run()
    {
        val$dialog1.dismiss();
    }

    ()
    {
        val$dialog1 = progressdialog;
        super();
    }
}
