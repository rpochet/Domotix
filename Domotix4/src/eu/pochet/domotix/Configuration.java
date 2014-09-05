package eu.pochet.domotix;

import android.app.ProgressDialog;
import android.content.Context;
import android.preference.PreferenceManager;
import eu.pochet.domotix.dao.LevelDao;
import eu.pochet.domotix.dao.ScenarioDao;

public class Configuration
{
    public static void updateConfig(Context context)
    {
    	final ProgressDialog dialog1 = ProgressDialog.show(context, "Updating files", "");
    	final ProgressDialog dialog2 = ProgressDialog.show(context, "Updating files", "");
        String s = PreferenceManager.getDefaultSharedPreferences(context).getString("domotix.data.host", null);
        LevelDao.updateFiles(s, context, new Runnable() {
            public void run()
            {
                dialog1.dismiss();
            }
        });
        ScenarioDao.updateFiles(s, context, new Runnable() {
            public void run()
            {
                dialog2.dismiss();
            }
        });
    }
}
