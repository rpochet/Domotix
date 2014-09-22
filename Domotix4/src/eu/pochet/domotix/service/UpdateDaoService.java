package eu.pochet.domotix.service;

import java.net.URISyntaxException;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;
import eu.pochet.domotix.dao.DomotixDao;

public class UpdateDaoService extends IntentService {

	public static final String TAG = UpdateDaoService.class.getName();
	
	public UpdateDaoService() throws URISyntaxException {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		DomotixDao.update(this);
		
		Toast.makeText(
				getBaseContext(),
				"Configuration updated", 0)
			.show();
	}
	
	
}
