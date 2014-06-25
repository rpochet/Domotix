package eu.pochet.domotix;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.dao.LevelDao;

public class LevelView extends ImageView 
{
	private static final String TAG = LevelView.class.getName();
	
	protected Level level;
	
	protected int levelX = 0;
	protected int levelY = 0;
	
	private float ratioX = 0;
	private float ratioY = 0;
	
	private int backgroundMaxX = 1220;
	private int backgroundMaxY = 1220;
	
    public LevelView(Context context, AttributeSet attrs)
    {
		super(context, attrs);
		int levelId = attrs.getAttributeIntValue(null, "levelId", -1);
		if(levelId != -1)
		{
			this.level = LevelDao.getLevel(context, levelId);	
		}
		init();
	}
	
    public LevelView(Context context, Level level)
    {
		super(context);
		this.level = level;
		init();
	}
    
    public void setLevel(Level level) 
    {
		this.level = level;
	}
	
    protected void init() 
    {
    }
    
    @Override
    protected void onDraw(Canvas canvas) 
    {
    	Log.d(TAG, "W=" + canvas.getWidth() + ", H=" + canvas.getHeight());
    	
    	ratioX = (float) (canvas.getWidth() * 1.0f / backgroundMaxX);
    	ratioY = (float) (canvas.getHeight() * 1.0f / backgroundMaxY);
    }
    
    protected float getRatioX() 
    {
		return ratioX;
	}
    
    protected float getRatioY() 
    {
		return ratioY;
	}    
    
}