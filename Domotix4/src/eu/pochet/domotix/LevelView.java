// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

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

    private static final String TAG = eu/pochet/domotix/LevelView.getName();
    private int backgroundMaxX;
    private int backgroundMaxY;
    protected Level level;
    protected int levelX;
    protected int levelY;
    private float ratioX;
    private float ratioY;

    public LevelView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        levelX = 0;
        levelY = 0;
        ratioX = 0.0F;
        ratioY = 0.0F;
        backgroundMaxX = 1220;
        backgroundMaxY = 1220;
        int i = attributeset.getAttributeIntValue(null, "levelId", -1);
        if (i != -1)
        {
            level = LevelDao.getLevel(context, i);
        }
        init();
    }

    public LevelView(Context context, Level level1)
    {
        super(context);
        levelX = 0;
        levelY = 0;
        ratioX = 0.0F;
        ratioY = 0.0F;
        backgroundMaxX = 1220;
        backgroundMaxY = 1220;
        level = level1;
        init();
    }

    protected float getRatioX()
    {
        return ratioX;
    }

    protected float getRatioY()
    {
        return ratioY;
    }

    protected void init()
    {
    }

    protected void onDraw(Canvas canvas)
    {
        Log.d(TAG, (new StringBuilder("W=")).append(canvas.getWidth()).append(", H=").append(canvas.getHeight()).toString());
        ratioX = (1.0F * (float)canvas.getWidth()) / (float)backgroundMaxX;
        ratioY = (1.0F * (float)canvas.getHeight()) / (float)backgroundMaxY;
    }

    public void setLevel(Level level1)
    {
        level = level1;
    }

}
