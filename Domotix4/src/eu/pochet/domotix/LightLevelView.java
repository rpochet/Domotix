package eu.pochet.domotix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
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
				BuildConfig.DEBUG ? R.drawable.lightbulb
						: R.drawable.lightbulb_on, options);
		mLightOffBitmap = BitmapFactory.decodeResource(getResources(),
				BuildConfig.DEBUG ? R.drawable.lightbulb
						: R.drawable.lightbulb_off, options);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (Light light : level.getLights()) {
			float lightX = getLightX(light);
			float lightY = getLightY(light);
			if (light.getStatus() == 0) {
				canvas.drawBitmap(mLightOffBitmap, 
						Constants.LIGHT_OFFSET_X + lightX * getRatioX(), 
						Constants.LIGHT_OFFSET_Y + lightY * getRatioY(), 
						null);
			} else {
				canvas.drawBitmap(mLightOnBitmap, 
						Constants.LIGHT_OFFSET_X + lightX * getRatioX(), 
						Constants.LIGHT_OFFSET_Y + lightY * getRatioY(), 
						null);
			}
		}
	}

}
