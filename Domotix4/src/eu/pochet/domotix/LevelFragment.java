package eu.pochet.domotix;

import java.util.ArrayList;
import java.util.List;

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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;
import eu.pochet.domotix.dao.Card;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.dao.LevelDao;
import eu.pochet.domotix.dao.Light;
import eu.pochet.domotix.service.LightStatusUpdateService;
import eu.pochet.domotix.service.UDPListenerService;
import eu.pochet.domotix.service.ZMQListenerService;

public abstract class LevelFragment extends Fragment implements android.gesture.GestureOverlayView.OnGesturePerformedListener {
	
	protected List<Level> levels = null;

	protected int mCurrentLevelId = 0;

	private GestureLibrary mLibrary = null;

	private GestureDetector gestureDetector = null;
			
	private BroadcastReceiver myBroadcastReceiver = null;

	private ViewFlipper viewFlipper = null;

	public LevelFragment() {
		myBroadcastReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				if (onBroadcastReceive(context, intent)) {
					NotificationHelper.notify(context);
				}
			}
		};
	}

	protected Level getCurrentLevel() {
		return (Level) levels.get(-1 + mCurrentLevelId);
	}

	protected LevelView getCurrentLevelView() {
		return (LevelView) viewFlipper.getCurrentView();
	}

	public void onActivityCreated(Bundle bundle) {
		super.onActivityCreated(bundle);
		getActivity().registerReceiver(myBroadcastReceiver,
				new IntentFilter(LightStatusUpdateService.ACTION));
		getActivity().registerReceiver(myBroadcastReceiver,
				new IntentFilter(UDPListenerService.ACTION));
		getActivity().registerReceiver(myBroadcastReceiver,
				new IntentFilter(ZMQListenerService.ACTION));
	}

	protected abstract boolean onBroadcastReceive(Context context, Intent intent);

	protected abstract android.view.GestureDetector.SimpleOnGestureListener getGestureDetector();

	protected abstract int getLevelLayoutViewId();

	protected abstract int getViewLevelFlipperViewId();

	public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
		View view = layoutinflater.inflate(getLevelLayoutViewId(), null);
		view.setDrawingCacheEnabled(false);

		mLibrary = GestureLibraries.fromRawResource(getActivity(), R.raw.gestures);
		mLibrary.load();

		this.gestureDetector = new GestureDetector(getActivity(), getGestureDetector());
		((GestureOverlayView) view).addOnGesturePerformedListener(this);
		view.setOnTouchListener(new android.view.View.OnTouchListener() {
			public boolean onTouch(View view, MotionEvent motionEvent) {
				return LevelFragment.this.gestureDetector.onTouchEvent(motionEvent);
			}
		});

		viewFlipper = (ViewFlipper) view.findViewById(getViewLevelFlipperViewId());
		this.levels = LevelDao.getLevels(getActivity());
		if (bundle != null) {
			mCurrentLevelId = bundle.getInt("level", 2);
		} else {
			mCurrentLevelId = 2;
		}
		viewFlipper.setDisplayedChild(-1 + mCurrentLevelId);

		for (int i = 0; i < viewFlipper.getChildCount(); i++) {
			((LevelView) viewFlipper.getChildAt(i)).setLevel(levels.get(i));
		}

		return view;
	}

	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(myBroadcastReceiver);
	}

	public void onGesturePerformed(GestureOverlayView gestureoverlayview, Gesture gesture) {
		List<Prediction> predictions = mLibrary.recognize(gesture);
		if (predictions.size() > 0 && ((Prediction) predictions.get(0)).score > 1.0D) {
			String name = predictions.get(0).name;
			if ("ltr".equals(name)) {
				viewFlipper.showPrevious();
				mCurrentLevelId = 1 + viewFlipper.getDisplayedChild();
				updateLevel();
			} else if ("rtl".equals(name)) {
				viewFlipper.showNext();
				mCurrentLevelId = 1 + viewFlipper.getDisplayedChild();
				updateLevel();
			} else if ("startall".startsWith(name)) {
				Bundle bundle = new Bundle();
				bundle.putString("Domotix.MessageService.ACTION", "switch_on_all");
				bundle.putInt("Domotix.MessageService.LEVEL", mCurrentLevelId);
				MessageHelper.sendMessage(getActivity(), bundle);
			} else if ("stopall".startsWith(name)) {
				Bundle bundle1 = new Bundle();
				bundle1.putString("Domotix.MessageService.ACTION", "switch_off_all");
				bundle1.putInt("Domotix.MessageService.LEVEL", mCurrentLevelId);
				MessageHelper.sendMessage(getActivity(), bundle1);
			}
		}
	}

	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putInt("level", mCurrentLevelId);
	}

	protected abstract void updateLevel();

	public static class CardLevelFragment extends LevelFragment {
		protected android.view.GestureDetector.SimpleOnGestureListener getGestureDetector() {
			return new android.view.GestureDetector.SimpleOnGestureListener() {

				private static final int OFFSET = 100;

				private Card getCardForEvent(MotionEvent motionevent) {
					RectF rectf = new RectF();
					Level level = getCurrentLevel();
					CardLevelView cardlevelview = (CardLevelView) getCurrentLevelView();
					float f = motionevent.getX() / cardlevelview.getRatioX();
					float f1 = motionevent.getY() / cardlevelview.getRatioY();
					for (Card card : level.getCards()) {
						float f2 = cardlevelview.getCardX(card);
						float f3 = cardlevelview.getCardY(card);
						rectf.set(f2 - OFFSET, f3 - OFFSET, f2 + OFFSET, f3 + OFFSET);
						if (rectf.contains(f, f1)) {
							return card;
						}
					}
					return null;
				}

				public boolean onDoubleTap(MotionEvent motionevent) {
					getCardForEvent(motionevent);
					return true;
				}
			};
		}

		@Override
		protected int getLevelLayoutViewId() {
			return R.layout.house_level;
		}
		
		@Override
		protected int getViewLevelFlipperViewId() {
			return R.id.cardLevelViewFlipper;
		}

		protected boolean onBroadcastReceive(Context context, Intent intent) {
			return Boolean.FALSE.booleanValue();
		}

		protected void updateLevel() {
		}
	}

	public static class LightLevelFragment extends LevelFragment {

		private void updateLightStatus(int lightId, int lightStatus) {
			for (Light light : levels.get(-1 + mCurrentLevelId).getLights()) {
				if(light.getId() == lightId) {
					light.setStatus(lightStatus);
				}
			}
		}

		protected android.view.GestureDetector.SimpleOnGestureListener getGestureDetector() {
			return new android.view.GestureDetector.SimpleOnGestureListener() {

				private static final int OFFSET_LONG = 100;
				private static final int OFFSET_SHORT = 70;

				private Light getLightForEvent(MotionEvent motionevent) {
					Level level = getCurrentLevel();
					List<Light> lights = getLightForEvent(motionevent, level.getLights(), OFFSET_LONG);
					if (lights.size() == 1) {
						return lights.get(0);
					}
					lights = getLightForEvent(motionevent, level.getLights(), OFFSET_SHORT);
					if (lights.size() == 1) {
						return lights.get(0);
					} else {
						return null;
					}
				}

				private List<Light> getLightForEvent(MotionEvent motionevent, List<Light> lights, int i) {
					RectF rectf = new RectF();
					LightLevelView lightlevelview = (LightLevelView) getCurrentLevelView();
					float f = motionevent.getX() / lightlevelview.getRatioX();
					float f1 = motionevent.getY() / lightlevelview.getRatioY();

					List<Light> res = new ArrayList<Light>();
					for (Light light : lights) {
						float f2 = lightlevelview.getLightX(light);
						float f3 = lightlevelview.getLightY(light);
						rectf.set(f2 - (float) i, f3 - (float) i, f2 + (float) i, f3 + (float) i);
						if (rectf.contains(f, f1)) {
							res.add(light);
						}
					}
					return res;
				}

				@Override
				public boolean onDoubleTap(MotionEvent motionevent) {
					Light light = getLightForEvent(motionevent);
					if (light != null) {
						LightDialogFragment lightdialogfragment = new LightDialogFragment();
						Bundle bundle = new Bundle();
						bundle.putSerializable("light", light);
						lightdialogfragment.setArguments(bundle);
						lightdialogfragment.show(getFragmentManager(), "light");
					}
					return true;
				}

				public void onLongPress(MotionEvent motionevent) {
					Light light = getLightForEvent(motionevent);
					if (light != null) {
						Toast.makeText(
								getActivity(),
								new StringBuilder("onLongPress light ").append(light.getName()).toString(), 0)
							.show();
					}
				}

				public boolean onSingleTapConfirmed(MotionEvent motionevent) {
					Light light = getLightForEvent(motionevent);
					if (light != null) {
						Bundle bundle = new Bundle();
						bundle.putString("Domotix.MessageService.ACTION", "toggle_light");
						bundle.putInt("Domotix.MessageService.LIGHT", light.getId());
						MessageHelper.sendMessage(getActivity(), bundle);
						Toast.makeText(
								getActivity(),
								new StringBuilder("Toggle light ").append(light.getName()).toString(), 0)
							.show();
					}
					return true;
				}
			};
		}

		@Override
		protected int getLevelLayoutViewId() {
			return R.layout.light_level;
		}
		
		@Override
		protected int getViewLevelFlipperViewId() {
			return R.id.lightLevelViewFlipper;
		}

		protected boolean onBroadcastReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (LightStatusUpdateService.ACTION.equals(action) || UDPListenerService.ACTION.equals(action) || ZMQListenerService.ACTION.equals(action)) {
				updateLightStatus(intent.getIntExtra(Constants.MESSAGE_LIGHT_ID, -1), intent.getIntExtra(Constants.MESSAGE_LIGHT_STATUS, -1));
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
		}

		protected void updateLevel() {
			Intent intent = new Intent(getActivity(), LightStatusUpdateService.class);
			getActivity().startService(intent);
		}

		public LightLevelFragment() {
		}
	}

}
