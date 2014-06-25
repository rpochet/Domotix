package eu.pochet.domotix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.dao.Light;

public class LightLevelView extends LevelView 
{
	private static final String TAG = LightLevelView.class.getName();
	
	private static final int OFFSET = -20;
	
	private Bitmap mLightOnBitmap;
	
	private Bitmap mLightOffBitmap;
	
	public LightLevelView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public LightLevelView(Context context, Level level)
	{
		super(context, level);
	}
	
    public void init() 
    {
        Options opts = new Options();
        opts.inDither = true;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        mLightOnBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lightbulb_on, opts); 
        mLightOffBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lightbulb_off, opts);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
    	super.onDraw(canvas);
    	float lightX = 0;
    	float lightY = 0;
		for (Light light : this.level.getLights()) 
		{
			lightX = getLightX(light);
			lightY = getLightY(light);
			if(light.getStatus().equals("0")) 
			{
				canvas.drawBitmap(mLightOffBitmap, (lightX * getRatioX()) + OFFSET, lightY * getRatioY(), null);
			} 
			else 
			{
				canvas.drawBitmap(mLightOnBitmap, (lightX * getRatioX()) + OFFSET, lightY * getRatioY(), null);
			}
		}
    }
    
    public float getLightX(Light light) 
    {
    	float roomX = light.getRoom().getX();
    	float lightX = this.levelX + roomX + light.getX();
    	return lightX;
    }
    
    public float getLightY(Light light)
    {
    	float roomY = light.getRoom().getY();
    	float lightY = this.levelY + roomY + light.getY(); 
    	return lightY;   	
    }
    
}
