// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix.service;

import android.util.Log;

// Referenced classes of package eu.pochet.domotix.service:
//            UDPListenerService

class this._cls0
    implements Runnable
{

    final UDPListenerService this$0;

    public void run()
    {
        java.net.InetAddress inetaddress = UDPListenerService.access$0(UDPListenerService.this);
_L1:
        if (!UDPListenerService.access$1(UDPListenerService.this).booleanValue())
        {
            return;
        }
        try
        {
            UDPListenerService.access$3(UDPListenerService.this, inetaddress, Integer.valueOf(UDPListenerService.access$2(UDPListenerService.this)));
        }
        catch (Exception exception)
        {
            Log.e("UDPListenerService", (new StringBuilder("No longer listening for UDP broadcasts cause of error ")).append(exception.getMessage()).toString());
            return;
        }
          goto _L1
    }

    ()
    {
        this$0 = UDPListenerService.this;
        super();
    }
}
