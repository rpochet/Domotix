// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.dao.Light;
import eu.pochet.domotix.dao.Room;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package eu.pochet.domotix:
//            LevelView

public class LightLevelView extends LevelView
{

    private static final int OFFSET = -20;
    private static final String TAG = eu/pochet/domotix/LightLevelView.getName();
    private Bitmap mLightOffBitmap;
    private Bitmap mLightOnBitmap;

    public LightLevelView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public LightLevelView(Context context, Level level)
    {
        super(context, level);
    }

    public float getLightX(Light light)
    {
        return (float)light.getRoom().getX() + (float)levelX + (float)light.getX();
    }

    public float getLightY(Light light)
    {
        return (float)light.getRoom().getY() + (float)levelY + (float)light.getY();
    }

    public void init()
    {
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inDither = true;
        options.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
        mLightOnBitmap = BitmapFactory.decodeResource(getResources(), 0x7f02000e, options);
        mLightOffBitmap = BitmapFactory.decodeResource(getResources(), 0x7f02000d, options);
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        Iterator iterator = level.getLights().iterator();
        do
        {
            if (!iterator.hasNext())
            {
                return;
            }
            Light light = (Light)iterator.next();
            float f = getLightX(light);
            float f1 = getLightY(light);
            if (light.getStatus().equals("0"))
            {
                canvas.drawBitmap(mLightOffBitmap, -20F + f * getRatioX(), f1 * getRatioY(), null);
            } else
            {
                canvas.drawBitmap(mLightOnBitmap, -20F + f * getRatioX(), f1 * getRatioY(), null);
            }
        } while (true);
    }

}
