package eu.pochet.domotix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.dao.Light;
import eu.pochet.domotix.dao.Room;
import eu.pochet.domotix.dao.SwapDevice;
import eu.pochet.domotix.dao.SwapRegister;
import eu.pochet.domotix.dao.SwapRegisterEndpoint;

public class LightLevelView extends LevelView {

	private static final String TAG = LightLevelView.class.getName();

	private Bitmap mLightOffBitmap;

	private Bitmap mLightOnBitmap;

	private int offsetX;

	private int offsetY;

	public LightLevelView(Context context, AttributeSet attributeset) {
		super(context, attributeset);
	}

	public LightLevelView(Context context, Level level) {
		super(context, level);
	}

	public void init() {
		android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
		options.inDither = true;
		options.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
		
		mLightOnBitmap = BitmapFactory.decodeResource(getResources(),
				DomotixActivity.DEBUG ? R.drawable.lightbulb
						: R.drawable.light_on, options);
		mLightOffBitmap = BitmapFactory.decodeResource(getResources(),
				DomotixActivity.DEBUG ? R.drawable.lightbulb
						: R.drawable.light_off, options);
		
		offsetX = mLightOnBitmap.getWidth() / -2;
		offsetY = mLightOnBitmap.getHeight() / -2;
		
		/*mLightOnBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(),
				DomotixActivity.DEBUG ? R.drawable.lightbulb
						: R.drawable.light_on, options), Constants.LIGHT_SIZE, Constants.LIGHT_SIZE);
		mLightOffBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(),
				DomotixActivity.DEBUG ? R.drawable.lightbulb
						: R.drawable.light_off, options), Constants.LIGHT_SIZE, Constants.LIGHT_SIZE);*/
	}
	
	//static final int GRID_SIZE = 5;
	static final int GRID_SIZE = 1;
	static final Paint paintText = new Paint();
	static final Paint paint1 = new Paint();
	static final Paint paint2 = new Paint();
	static final Paint paint3 = new Paint();
	static {
		
		paintText.setColor(Color.RED);
		paintText.setTextSize(48);
		
		paint1.setColor(Color.RED);
	    paint1.setStrokeWidth(GRID_SIZE);	
	    paint1.setStyle(Paint.Style.STROKE);
	    paint1.setColor(0x99000000);

		paint2.setColor(Color.RED);
	    paint2.setStrokeWidth(GRID_SIZE);	
	    paint2.setStyle(Paint.Style.STROKE);

		paint3.setColor(Color.GREEN);
	    paint3.setStrokeWidth(GRID_SIZE);	
	    paint3.setStyle(Paint.Style.STROKE);
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		//Log.i(TAG, canvas.getDensity() + "-" + mLightOnBitmap.getDensity() + "-" + mLightOffBitmap.getDensity());
		//Log.i(TAG, canvas.getWidth() + "-" + mLightOnBitmap.getWidth() + "-" + mLightOffBitmap.getWidth());
		//Log.i(TAG, canvas.getHeight() + "-" + mLightOnBitmap.getHeight() + "-" + mLightOffBitmap.getHeight());
		
		float lightX;
		float lightY;
		for(Room room : level.getRooms()) {
			for (Light light : room.getLights()) {
				lightX = (offsetX + light.getLocation().getAbsoluteX()) * getRatioX();
				lightY = (offsetY + light.getLocation().getAbsoluteY()) * getRatioY();
				
				if (light.getStatus() == 0) {
					canvas.drawBitmap(mLightOffBitmap, 
						lightX, 
						lightY, 
						null);
				} else {
					canvas.drawBitmap(mLightOnBitmap, 
						lightX, 
						lightY, 
						null);
				}
				
				if(DomotixActivity.DEBUG) {
					canvas.drawRect(lightX - 10, 
							lightY - 10, 
							lightX + 10, 
							lightY + 10, 
							paint2);
					canvas.drawRect(light.getLocation().getAbsoluteX() * getRatioX() + Constants.LIGHT_OFFSET_X * GRID_SIZE / 2, 
							light.getLocation().getAbsoluteY() * getRatioY() + Constants.LIGHT_OFFSET_Y * GRID_SIZE / 2, 
							light.getLocation().getAbsoluteX() * getRatioX() - Constants.LIGHT_OFFSET_X * GRID_SIZE / 2, 
							light.getLocation().getAbsoluteY() * getRatioY() - Constants.LIGHT_OFFSET_Y * GRID_SIZE / 2, 
							paint3);
					canvas.drawRect(light.getLocation().getAbsoluteX() * getRatioX() - 10, 
					        light.getLocation().getAbsoluteY() * getRatioY() - 10, 
							light.getLocation().getAbsoluteX() * getRatioX() + 10, 
							light.getLocation().getAbsoluteY() * getRatioY() + 10,
							paint3);
				}
			}

			for (SwapDevice swapDevice : room.getSwapDevices()) {
				for (SwapRegister swapRegister : swapDevice.getSwapRegisters()) {
					for (SwapRegisterEndpoint swapRegisterEndpoint : swapRegister.getSwapRegisterEndpoints()) {
						if(swapRegisterEndpoint.getName().equals(Constants.SWAP_REGISTER_TEMPERATURE)) {
							canvas.drawText(swapRegisterEndpoint.getValueAsInt() / 100f + " °", 
									(swapDevice.getLocation().getAbsoluteX()) * getRatioX() - 20, 
									(swapDevice.getLocation().getAbsoluteY()) * getRatioY() - 10,
							        paintText);
						}
					}
				}
			}
		}
		
		if(DomotixActivity.DEBUG) {
			for(int i = 0; i < canvas.getWidth(); i = i + (10 * GRID_SIZE)) {
				for(int j = 0; j < canvas.getHeight(); j = j + (10 * GRID_SIZE)) {
					canvas.drawPoint(i, j, paint1);
				}	
			}
		}
			
	}

}
