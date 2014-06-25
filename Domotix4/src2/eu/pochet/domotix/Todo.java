package eu.pochet.domotix;



public class Todo {


	
	/*
	

    void showDialog(String text) 
    {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        //DialogFragment newFragment = LightFragment.newInstance(text);

        // Show the dialog.
        //newFragment.show(ft, "dialog");
    }

    void showNotification(String message) 
    {
        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(true)
                .setOnlyAlertOnce(false)
                .setTicker(message)
                .setContentIntent(getDialogPendingIntent("Tapped the notification entry."))
        		.setContentTitle(getString(R.string.app_name))
                .setContentText(message);
        notificationManager.notify(NOTIFICATION_DEFAULT, builder.getNotification());
    }

    PendingIntent getDialogPendingIntent(String dialogText) 
    {
        return PendingIntent.getActivity(
                this,
                dialogText.hashCode(), // Otherwise previous PendingIntents with the same requestCode may be overwritten.
                new Intent(ACTION_DIALOG)
                        .putExtra(Intent.EXTRA_TEXT, dialogText)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                0);
    }
    
    
    
	 
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
	    String text = getArguments().getString("text");
	
	    return new AlertDialog.Builder(getActivity())
	      	.setTitle("A Dialog of Awesome")
	      	.setMessage(text)
	      	.setPositiveButton(R.id.notification_button, 
	      			new DialogInterface.OnClickListener()
	      			{
				      	public void onClick(DialogInterface dialog, int whichButton)
				      	{
				      	}
	      			}
	      	).create();
	}
	
	
	
	 
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		getActivity().unregisterReceiver(myBroadcastReceiver);
	}

	public void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
		outState.putInt("level", this.mCurrentLevelId);
	}
	
	
	
	private void updateLightStatus(String status) 
	{
		int statusLength = status.length();
		String lightStatus = null;
		Level level = (Level) this.levels.get(LevelFragment.this.mCurrentLevelId - 1);
		for (Light light : level.getLights())
		{
			if(statusLength > ((light.getOutputNb() + 1) * 2))
			{
				lightStatus = status.substring(light.getOutputNb() * 2, (light.getOutputNb() + 1) * 2);
				light.setStatus(Integer.parseInt(lightStatus, 16));
				LevelView mCurrentLevel = (LevelView) LevelFragment.this.viewFlipper.getCurrentView();
				mCurrentLevel.invalidate();
			}
		}
	}
	
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	
	if(savedInstanceState != null) 
	{
		this.mCurrentLevelId = savedInstanceState.getInt("level", 2);
	} 
	else 
	{
		this.mCurrentLevelId = 2;
	}
	
	*/
	
}
