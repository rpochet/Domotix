package eu.pochet.domotix;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import eu.pochet.domotix.service.MessageService;

public class MessageHelper {
	
	public static void sendMessage(Context context, Bundle parameters) {
		Intent intent = new Intent(context, MessageService.class);
		intent.putExtras(parameters);
		context.startService(intent);
		NotificationHelper.notify(context);
	}
	
}
