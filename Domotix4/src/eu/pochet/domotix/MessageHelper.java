package eu.pochet.domotix;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import eu.pochet.domotix.service.MessageService;

public class MessageHelper
{

    public MessageHelper()
    {
    }

    public static void sendMessage(Context context, Bundle bundle)
    {
        Intent intent = new Intent(context, MessageService.class);
        intent.putExtras(bundle);
        context.startService(intent);
        NotificationHelper.notify(context);
    }
    
}
