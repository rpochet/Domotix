// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import eu.pochet.domotix.dao.Level;

// Referenced classes of package eu.pochet.domotix:
//            LevelView

public class HouseLevelView extends LevelView
{

    public HouseLevelView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public HouseLevelView(Context context, Level level)
    {
        super(context, level);
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
    }
}
