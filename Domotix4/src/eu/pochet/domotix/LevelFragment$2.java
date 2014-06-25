// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

// Referenced classes of package eu.pochet.domotix:
//            LevelFragment

class val.gestureDetector
    implements android.view.ener
{

    final LevelFragment this$0;
    private final GestureDetector val$gestureDetector;

    public boolean onTouch(View view, MotionEvent motionevent)
    {
        return val$gestureDetector.onTouchEvent(motionevent);
    }

    ()
    {
        this$0 = final_levelfragment;
        val$gestureDetector = GestureDetector.this;
        super();
    }
}
