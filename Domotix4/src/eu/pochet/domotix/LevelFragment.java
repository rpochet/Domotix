package eu.pochet.domotix;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import eu.pochet.domotix.dao.DomotixDao;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.dao.Light;
import eu.pochet.domotix.dao.SwapDevice;
import eu.pochet.domotix.service.ActionBuilder;
import eu.pochet.domotix.service.LightStatusUpdateService;

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
					//NotificationHelper.notify(context);
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
		/*getActivity().registerReceiver(myBroadcastReceiver,
				new IntentFilter(LightStatusUpdateService.ACTION));*/
		/*getActivity().registerReceiver(myBroadcastReceiver,
				new IntentFilter(UDPListenerService.ACTION));*/
		getActivity().registerReceiver(myBroadcastReceiver,
				new IntentFilter(ActionBuilder.ACTION_IN));
	}

	protected abstract boolean onBroadcastReceive(Context context, Intent intent);

	protected abstract android.view.GestureDetector.SimpleOnGestureListener getGestureDetector();

	protected abstract int getLevelLayoutViewId();

	protected abstract int getViewLevelFlipperViewId();

	public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
		View view = layoutinflater.inflate(getLevelLayoutViewId(), null);
		view.setDrawingCacheEnabled(false);

		if(getViewLevelFlipperViewId() != -1) {
			
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
			this.levels = DomotixDao.getLevels(getActivity());
			if (bundle != null) {
				mCurrentLevelId = bundle.getInt("level", 2);
			} else {
				mCurrentLevelId = 2;
			}
			viewFlipper.setDisplayedChild(-1 + mCurrentLevelId);
	
			for (int i = 0; i < viewFlipper.getChildCount(); i++) {
				((LevelView) viewFlipper.getChildAt(i)).setLevel(levels.get(i));
			}
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
				new ActionBuilder().setType(ActionBuilder.TYPE_LIGHT_SWITCH_ON_ALL).setLevelId(mCurrentLevelId).sendMessage(getActivity());
			} else if ("stopall".startsWith(name)) {
				new ActionBuilder().setType(ActionBuilder.TYPE_LIGHT_SWITCH_OFF_ALL).setLevelId(mCurrentLevelId).sendMessage(getActivity());
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

				private SwapDevice getSwapDeviceForEvent(MotionEvent motionevent) {
					RectF rectf = new RectF();
					Level level = getCurrentLevel();
					SwapDeviceLevelView cardlevelview = (SwapDeviceLevelView) getCurrentLevelView();
					float f = motionevent.getX() / cardlevelview.getRatioX();
					float f1 = motionevent.getY() / cardlevelview.getRatioY();
					for (SwapDevice swapDevice : level.getSwapDevices()) {
						float f2 = cardlevelview.getSwapDeviceX(swapDevice);
						float f3 = cardlevelview.getSwapDeviceY(swapDevice);
						rectf.set(f2 - Constants.CARD_TOUCH_OFFSET, f3 - Constants.CARD_TOUCH_OFFSET, f2 + Constants.CARD_TOUCH_OFFSET, f3 + Constants.CARD_TOUCH_OFFSET);
						if (rectf.contains(f, f1)) {
							return swapDevice;
						}
					}
					return null;
				}

				public boolean onDoubleTap(MotionEvent motionevent) {
					getSwapDeviceForEvent(motionevent);
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
			return R.id.swapDeviceLevelViewFlipper;
		}

		protected boolean onBroadcastReceive(Context context, Intent intent) {
			return Boolean.FALSE.booleanValue();
		}

		protected void updateLevel() {
		}
	}

	public static class LightLevelFragment extends LevelFragment {

		protected android.view.GestureDetector.SimpleOnGestureListener getGestureDetector() {
			return new android.view.GestureDetector.SimpleOnGestureListener() {

				private Light getLightForEvent(MotionEvent motionevent) {
					Level level = getCurrentLevel();
					List<Light> lights = getLightForEvent(motionevent, level.getLights(), Constants.LIGHT_TOUCH_OFFSET);
					if (lights.size() == 1) {
						return lights.get(0);
					}
					lights = getLightForEvent(motionevent, level.getLights(), Constants.LIGHT_TOUCH_OFFSET2);
					if (lights.size() == 1) {
						return lights.get(0);
					} else {
						return null;
					}
				}

				/**
				 * Center is at getLightX(light) * getRatioX()
				 * 
				 * @param motionEvent
				 * @param lights
				 * @param i
				 * @return
				 */
				private List<Light> getLightForEvent(MotionEvent motionEvent, List<Light> lights, int i) {
					RectF rectf = new RectF();
					LightLevelView lightlevelview = (LightLevelView) getCurrentLevelView();
					float absoluteX = motionEvent.getX() / lightlevelview.getRatioX();
					float absoluteY = motionEvent.getY() / lightlevelview.getRatioY();
					List<Light> res = new ArrayList<Light>();
					for (Light light : lights) {
						float f2 = lightlevelview.getLightX(light);
						float f3 = lightlevelview.getLightY(light);
						rectf.set(f2 - i, f3 - i, f2 + i, f3 + i);
						if (rectf.contains(absoluteX, absoluteY)) {
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
						if(!DomotixActivity.DEBUG) {
							new ActionBuilder()
								.setAction(ActionBuilder.ACTION_OUT)
								.setType(ActionBuilder.TYPE_LIGHT_SWITCH_TOGGLE)
								.setLightId(light.getId())
								.sendMessage(getActivity());
						}
						NotificationHelper.notify(getActivity());
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

		@Override
		protected boolean onBroadcastReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ActionBuilder.ACTION_IN.equals(action)) {
				//updateLightStatus(intent.getIntExtra(Constants.MESSAGE_LIGHT_ID, -1), intent.getIntExtra(Constants.MESSAGE_LIGHT_STATUS, -1));
				// Refresh view from LevelDao...
				//getView().postInvalidate();
				getCurrentLevelView().postInvalidate();
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
		}

		@Override
		protected void updateLevel() {
			Intent intent = new Intent(getActivity(), LightStatusUpdateService.class);
			getActivity().startService(intent);
		}

	}
	
	public static class LightLevelListFragment extends LevelFragment {

		@Override
		protected boolean onBroadcastReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ActionBuilder.ACTION_IN.equals(action)) {
				((ListView) getView()).invalidateViews();
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
		}
		
		@Override
		public View onCreateView(LayoutInflater layoutinflater,
				ViewGroup viewgroup, Bundle bundle) {
			final List<Light> lights  = DomotixDao.getLights(getActivity());
			View view = super.onCreateView(layoutinflater, viewgroup, bundle);
			ListView listView = (ListView) view;
			listView.setAdapter(new BaseAdapter() {
				
				public View getView(int position, View convertView, ViewGroup parent) {
					View rowView = convertView;
				    // reuse views
				    if (rowView == null) {
				        LayoutInflater inflater = getActivity().getLayoutInflater();
				        rowView = inflater.inflate(R.layout.light_level_list_item, null);
				        // configure view holder
				        ViewHolder viewHolder = new ViewHolder();
				        viewHolder.text = (TextView) rowView.findViewById(R.id.label);
				        viewHolder.image = (ImageView) rowView.findViewById(R.id.icon);
				        rowView.setTag(viewHolder);
				    }
				    
				    Light light = lights.get(position);
				    
				    // fill data
				    ViewHolder holder = (ViewHolder) rowView.getTag();
				    holder.text.setText(light.getName());
				    if (light.getStatus() > 0) {
				        holder.image.setImageResource(R.drawable.lightbulb_on);
				    } else {
				        holder.image.setImageResource(R.drawable.lightbulb_off);
				    }

				    return rowView;
				}
				
				public long getItemId(int position) {
					return lights.get(position).getId();
				}
				
				public Object getItem(int position) {
					return lights.get(position);
				}
				
				public int getCount() {
					return lights.size();
				}
			});
			listView.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Light light = (Light) ((ListView) parent).getAdapter().getItem(position);
					if(!DomotixActivity.DEBUG) {
						new ActionBuilder()
							.setAction(ActionBuilder.ACTION_OUT)
							.setType(ActionBuilder.TYPE_LIGHT_SWITCH_TOGGLE)
							.setLightId(light.getId())
							.sendMessage(getActivity());
					}
					NotificationHelper.notify(getActivity());
					Toast.makeText(
							getActivity(),
							new StringBuilder("Toggle light ").append(light.getName()).toString(), 0)
						.show();
				}
				
			});
			return view;
		}

		@Override
		protected SimpleOnGestureListener getGestureDetector() {
			return null;
		}

		@Override
		protected int getLevelLayoutViewId() {
			return R.layout.light_level_list;
		}

		@Override
		protected int getViewLevelFlipperViewId() {
			return -1;
		}

		@Override
		protected void updateLevel() {
			// TODO Auto-generated method stub
			
		}
		
		static class ViewHolder {
			TextView text;
			ImageView image;
		}
		
	}

}
