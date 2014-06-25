// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import eu.pochet.domotix.service.MessageService;

// Referenced classes of package eu.pochet.domotix:
//            NotificationHelper

public class MessageHelper
{

    public MessageHelper()
    {
    }

    public static void sendMessage(Context context, Bundle bundle)
    {
        Intent intent = new Intent(context, eu/pochet/domotix/service/MessageService);
        intent.putExtras(bundle);
        context.startService(intent);
        NotificationHelper.notify(context);
    }
}
