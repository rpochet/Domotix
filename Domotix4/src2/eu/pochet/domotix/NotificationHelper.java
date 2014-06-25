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
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (preferences.getBoolean(SettingsActivity.NOTIFICATION_KEY, false)) {
			Ringtone r = RingtoneManager.getRingtone(context, Uri.parse(preferences.getString(SettingsActivity.NOTIFICATION_URI_KEY, null)));
			r.play();
			SystemClock.sleep(500);
			r.stop();
		}
		if (preferences.getBoolean(SettingsActivity.NOTIFICATION_VIBRATE_KEY, false)) {
			Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(300);
		}
	}
}
