package eu.pochet.domotix;

import android.app.ProgressDialog;
import android.content.Context;
import android.preference.PreferenceManager;
import eu.pochet.domotix.dao.LevelDao;
import eu.pochet.domotix.dao.ScenarioDao;

public class Configuration {
	
	public static void updateConfig(Context context)
	{
		String dataHost = PreferenceManager.getDefaultSharedPreferences(context).getString(SettingsActivity.DATA_HOST_KEY, null);
		
		final ProgressDialog dialog1 = ProgressDialog.show(context, "", "Loading levels. Please wait...", true);
		LevelDao.updateFiles(dataHost, context, new Runnable() 
		{
            public void run()
            {
				dialog1.dismiss();
            }
			
		});
		
		final ProgressDialog dialog2 = ProgressDialog.show(context, "", "Loading scenarios. Please wait...", true);
		ScenarioDao.updateFiles(dataHost, context, new Runnable() 
		{
            public void run()
            {
				dialog2.dismiss();
            }
			
		});
	}
	
}
