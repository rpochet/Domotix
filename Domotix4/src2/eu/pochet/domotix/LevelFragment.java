package eu.pochet.domotix;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;
import eu.pochet.android.Util;
import eu.pochet.domotix.dao.LagartoResponse;
import eu.pochet.domotix.dao.LagartoResponseDao;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.dao.LevelDao;
import eu.pochet.domotix.dao.Light;
import eu.pochet.domotix.dao.Card;
import eu.pochet.domotix.service.LagartoEventService;
import eu.pochet.domotix.service.LightStatusUpdateService;
import eu.pochet.domotix.service.MessageService;
import eu.pochet.domotix.service.UDPListenerService;

public abstract class LevelFragment extends Fragment implements GestureOverlayView.OnGesturePerformedListener 
{	
	private ViewFlipper viewFlipper = null;

	private GestureLibrary mLibrary = null;

	protected List<Level> levels = null;

	protected int mCurrentLevelId = 0;

	BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			boolean res = onBroadcastReceive(context, intent);

			if(res)
			{
				NotificationHelper.notify(context);
			}
		}
	};

	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		getActivity().registerReceiver(myBroadcastReceiver, new IntentFilter(LightStatusUpdateService.BROADCAST_ACTION));
		getActivity().registerReceiver(myBroadcastReceiver, new IntentFilter(UDPListenerService.BROADCAST_ACTION));
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

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View mContentView = inflater.inflate(getLevelLayoutViewId(), null);
		this.viewFlipper = ((ViewFlipper) mContentView.findViewById(R.id.viewLevelFlipper));
		this.levels = LevelDao.getLevels(getActivity());

		if (savedInstanceState != null) 
		{
			this.mCurrentLevelId = savedInstanceState.getInt("level", 2);
		} 
		else 
		{
			this.mCurrentLevelId = 2;
		}
		this.viewFlipper.setDisplayedChild(this.mCurrentLevelId - 1);

		if(this.levels != null && this.levels.size() > 0) 
		{
			LevelView levelView = null;
			for (int i = 0; i < this.viewFlipper.getChildCount(); i++) 
			{
				levelView = (LevelView) this.viewFlipper.getChildAt(i);
				levelView.setLevel(this.levels.get(i));
			}
		}

		mContentView.setDrawingCacheEnabled(false);

		this.mLibrary = GestureLibraries.fromRawResource(getActivity(), R.raw.gestures);
		this.mLibrary.load();

		GestureOverlayView gestures = (GestureOverlayView) mContentView;
		gestures.addOnGesturePerformedListener(this);

		final GestureDetector gestureDetector = new GestureDetector(getActivity(), getGestureDetector());
		mContentView.setOnTouchListener(new View.OnTouchListener() 
		{
			public boolean onTouch(View v, MotionEvent event) 
			{
				return gestureDetector.onTouchEvent(event);
			}
		});

		return mContentView;
	}

	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) 
	{
		List<Prediction> predictions = this.mLibrary.recognize(gesture);
		if (predictions.size() > 0 && predictions.get(0).score > 1.0D) 
		{
			String action = predictions.get(0).name;
			if ("ltr".equals(action)) 
			{
				this.viewFlipper.showPrevious();
				this.mCurrentLevelId = this.viewFlipper.getDisplayedChild() + 1;
				updateLevel();
			} 
			else if ("rtl".equals(action)) 
			{
				this.viewFlipper.showNext();
				this.mCurrentLevelId = this.viewFlipper.getDisplayedChild() + 1;
				updateLevel();
			} 
			else if (action.startsWith("startall")) 
			{
				Bundle parameters = new Bundle();
				parameters.putString(MessageService.ACTION, "switch_on_all");
				parameters.putInt(MessageService.LEVEL_ID, this.mCurrentLevelId);
				MessageHelper.sendMessage(getActivity(), parameters);
			} 
			else if (action.startsWith("stopall")) 
			{
				Bundle parameters = new Bundle();
				parameters.putString(MessageService.ACTION, "switch_off_all");
				parameters.putInt(MessageService.LEVEL_ID, this.mCurrentLevelId);
				MessageHelper.sendMessage(getActivity(), parameters);
			}
		}
	}

	protected abstract int getLevelLayoutViewId();

	protected abstract SimpleOnGestureListener getGestureDetector();

	protected abstract void updateLevel();

	protected abstract boolean onBroadcastReceive(Context context, Intent intent);

	protected Level getCurrentLevel() 
	{
		return this.levels.get(this.mCurrentLevelId - 1);
	}

	protected LevelView getCurrentLevelView() 
	{
		return (LevelView) this.viewFlipper.getCurrentView();
	}

	public static class LightLevelFragment extends LevelFragment 
	{
		private String lightStatusId = "xxxx";

		@Override
		protected SimpleOnGestureListener getGestureDetector()
		{
			return new SimpleOnGestureListener() {
				
				private static final int OFFSET = 100;

				public boolean onDoubleTap(MotionEvent e)
				{
					Light light = getLightForEvent(e);
					if (light != null) 
					{
						DialogFragment newFragment = new LightDialogFragment();
						Bundle parameters = new Bundle();
						parameters.putSerializable(LightDialogFragment.LIGHT_ARGUMENT, (Serializable) light);
						newFragment.setArguments(parameters);
						newFragment.show(getFragmentManager(), "light");
					}
					return true;
				}

				public void onLongPress(MotionEvent e) 
				{
					Light light = getLightForEvent(e);
					if(light != null) 
					{
						Toast.makeText(getActivity(), "onDoubleTap light " + light.getName(), Toast.LENGTH_SHORT).show();
					}
				}

				public boolean onSingleTapConfirmed(MotionEvent e) 
				{
					Light light = getLightForEvent(e);
					if (light != null) 
					{
						Bundle parameters = new Bundle();
						parameters.putString(MessageService.ACTION, "toggle_light");
						parameters.putInt(MessageService.LIGHT_ID, light.getId());
						MessageHelper.sendMessage(getActivity(), parameters);
						Toast.makeText(getActivity(), "Toggle light " + light.getName(), Toast.LENGTH_SHORT).show();
					}
					return true;
				}

				private Light getLightForEvent(MotionEvent e) 
				{
					Level level = getCurrentLevel();
					List<Light> candidateLights = getLightForEvent(e, level.getLights(), OFFSET);
					if(candidateLights.size() == 1)
					{
						return candidateLights.get(0);
					}
					else 
					{
						candidateLights = getLightForEvent(e, level.getLights(), (int) (OFFSET * 0.7));
						if(candidateLights.size() == 1)
						{
							return candidateLights.get(0);
						}
					}
					return null;
				}

				private List<Light> getLightForEvent(MotionEvent e, List<Light> lights, int precision) 
				{
					float lightX = 0.0F;
					float lightY = 0.0F;
					RectF rectF = new RectF();
					LightLevelView mCurrentLevel = (LightLevelView) getCurrentLevelView();
					float x = e.getX() / mCurrentLevel.getRatioX();
					float y = e.getY() / mCurrentLevel.getRatioY();
					
					List<Light> candidateLights = new ArrayList<Light>();
					for (Light light : lights) 
					{
						lightX = mCurrentLevel.getLightX(light);
						lightY = mCurrentLevel.getLightY(light);
						// Log.d(LevelFragment.TAG, "Light " + light.getName() +
						// " (" + lightX + ", " + lightY + "). X: " + x + ", " +
						// y);
						rectF.set(lightX - precision, lightY - precision, lightX + precision, lightY + precision);
						if (rectF.contains(x, y)) {
							candidateLights.add(light);
						}
					}
					return candidateLights;
				}
			};
		};

		@Override
		protected boolean onBroadcastReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (LightStatusUpdateService.BROADCAST_ACTION.equals(action)) {
				//String cardNb = intent.getStringExtra("cardNb");
				String status = intent.getStringExtra("status");
				updateLightStatus(status);
				return Boolean.TRUE;
			} else if (UDPListenerService.BROADCAST_ACTION.equals(action)) {
				String message = Util.ByteArrayToHexString(intent.getByteArrayExtra("message"));
				//String cardAddress = message.substring(4, 20);
				String status = message.substring(22, intent.getIntExtra("messageLength", 32) * 2);
				updateLightStatus(status);
				return Boolean.TRUE;
			} else if (LagartoEventService.ACTION.equals(action)) {
				String message = intent.getStringExtra("message");
				LagartoResponse lagartoResponse = LagartoResponseDao.getLagartoResponse(message);
				LagartoResponse.Status lightStatus = lagartoResponse.getStatus(lightStatusId);
				if (lightStatus != null) {
					updateLightStatus(lightStatus.getValue());
				}
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
		}

		@Override
		protected int getLevelLayoutViewId() {
			return R.layout.light_level;
		}

		@Override
		protected void updateLevel() {
			Intent intent = new Intent(getActivity(), LightStatusUpdateService.class);
			getActivity().startService(intent);
		}

		private void updateLightStatus(String status) {
			int statusLength = status.length();
			String lightStatus = null;
			Level level = (Level) this.levels.get(this.mCurrentLevelId - 1);
			for (Light light : level.getLights()) {
				/*if (statusLength > ((light.getOutputNb() + 1) * 2)) {
					lightStatus = status.substring(light.getOutputNb() * 2, (light.getOutputNb() + 1) * 2);
					light.setStatus(Integer.parseInt(lightStatus, 16));
					LevelView mCurrentLevel = getCurrentLevelView();
					mCurrentLevel.invalidate();
				}*/
			}
		}

	}

	public static class CardLevelFragment extends LevelFragment {
		
		@Override
		protected SimpleOnGestureListener getGestureDetector() {
			return new SimpleOnGestureListener() {
				
				private static final int OFFSET = 100;
				
				@Override
				public boolean onDoubleTap(MotionEvent e) {
					Card card = getCardForEvent(e);
					if (card != null) {
						
					}
					return true;
				}

				private Card getCardForEvent(MotionEvent e) {
					float cardX = 0.0F;
					float cardY = 0.0F;
					RectF rectF = new RectF();
					Level level = getCurrentLevel();
					CardLevelView mCurrentLevel = (CardLevelView) getCurrentLevelView();
					float x = e.getX() / mCurrentLevel.getRatioX();
					float y = e.getY() / mCurrentLevel.getRatioY();
					for (Card card : level.getCards()) {
						cardX = mCurrentLevel.getCardX(card);
						cardY = mCurrentLevel.getCardY(card);
						rectF.set(cardX - OFFSET, cardY - OFFSET, cardX + OFFSET, cardY + OFFSET);
						if (rectF.contains(x, y)) {
							return card;
						}
					}
					return null;
				}
			};
		}

		@Override
		protected boolean onBroadcastReceive(Context context, Intent intent) {
			return Boolean.FALSE;
		}

		@Override
		protected int getLevelLayoutViewId() {
			return R.layout.house_level;
		}

		@Override
		protected void updateLevel() {
		}
	}

}