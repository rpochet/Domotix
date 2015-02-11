package eu.pochet.domotix.service;

import java.net.URISyntaxException;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import eu.pochet.domotix.Constants;
import eu.pochet.domotix.R;
import eu.pochet.domotix.dao.DomotixDao;

public class UpdateDaoService extends IntentService {

	public static final String TAG = UpdateDaoService.class.getName();
	
	protected int updateNotificationId = 1;
	
	public UpdateDaoService() throws URISyntaxException {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
	
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(getApplicationContext())
		        .setSmallIcon(R.drawable.logo)
		        .setGroup(Constants.NOTIFICATION_DOMOTIX_GROUP)
		        .setContentTitle("Update configuration")
		        .setContentText("Please wait...");
		NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification mNotification = mBuilder.build();
		mNotificationManager.notify(updateNotificationId, mNotification);
	
		DomotixDao.update(this);
		
		mNotificationManager.cancel(updateNotificationId);
	}
	
	
}
