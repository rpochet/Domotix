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

public class LightLevelView extends LevelView {
	
	private static final String TAG = LightLevelView.class.getName();

	private Bitmap mLightOffBitmap;

	private Bitmap mLightOnBitmap;

	public LightLevelView(Context context, AttributeSet attributeset) {
		super(context, attributeset);
	}

	public LightLevelView(Context context, Level level) {
		super(context, level);
	}

	public float getLightX(Light light) {
		return light.getRoom().getX() + levelX + light.getX();
	}

	public float getLightY(Light light) {
		return light.getRoom().getY() + levelY + light.getY();
	}

	public void init() {
		android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
		options.inDither = true;
		options.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
		mLightOnBitmap = BitmapFactory.decodeResource(getResources(),
				DomotixActivity.DEBUG ? R.drawable.lightbulb
						: R.drawable.lightbulb_on, options);
		mLightOffBitmap = BitmapFactory.decodeResource(getResources(),
				DomotixActivity.DEBUG ? R.drawable.lightbulb
						: R.drawable.lightbulb_off, options);
	}
	
	//static final int GRID_SIZE = 5;
	static final int GRID_SIZE = 1;
	static final Paint paint1 = new Paint();
	static final Paint paint2 = new Paint();
	static final Paint paint3 = new Paint();
	static {
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
		float lightX;
		float lightY;
		for (Light light : level.getLights()) {
			
			lightX = (Constants.LIGHT_OFFSET_X + getLightX(light)) * getRatioX();
			lightY = (Constants.LIGHT_OFFSET_Y + getLightY(light)) * getRatioY();
			
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
				canvas.drawRect(getLightX(light) * getRatioX() + Constants.LIGHT_OFFSET_X * GRID_SIZE / 2, 
						getLightY(light) * getRatioY() + Constants.LIGHT_OFFSET_Y * GRID_SIZE / 2, 
						getLightX(light) * getRatioX() - Constants.LIGHT_OFFSET_X * GRID_SIZE / 2, 
						getLightY(light) * getRatioY() - Constants.LIGHT_OFFSET_Y * GRID_SIZE / 2, 
						paint3);
				canvas.drawRect(getLightX(light) * getRatioX() - 10, 
				        getLightY(light) * getRatioY() - 10, 
						getLightX(light) * getRatioX() + 10, 
						getLightY(light) * getRatioY() + 10,
						paint3);
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
