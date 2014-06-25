package eu.pochet.domotix;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import eu.pochet.domotix.dao.Level;

public class HouseLevelView extends LevelView {
	
	public HouseLevelView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public HouseLevelView(Context context, Level level)
	{
		super(context, level);
	}

    @Override
    protected void onDraw(Canvas canvas)
    {
    	super.onDraw(canvas);
    }
    
}
