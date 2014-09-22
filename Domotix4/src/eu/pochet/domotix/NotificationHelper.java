package eu.pochet.domotix;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;

public class NotificationHelper {

	public static void notify(Context context) {
		SharedPreferences sharedpreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (sharedpreferences.getBoolean("notification", false)) {
			
			if (sharedpreferences.getString("notification.sound", null) != null) {
				Ringtone ringtone = RingtoneManager.getRingtone(context, Uri
					.parse(sharedpreferences
							.getString("notification.sound", null)));
				ringtone.play();
				SystemClock.sleep(500L);
				ringtone.stop();
			}
			
			if (sharedpreferences.getBoolean("notification.vibrate", false)) {
				((Vibrator) context.getSystemService("vibrator")).vibrate(300L);
			}
		}
	}
}
