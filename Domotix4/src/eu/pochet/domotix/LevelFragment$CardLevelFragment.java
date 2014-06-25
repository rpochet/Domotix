// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.view.MotionEvent;
import eu.pochet.domotix.dao.Card;
import eu.pochet.domotix.dao.Level;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package eu.pochet.domotix:
//            LevelFragment, CardLevelView

public static class er extends LevelFragment
{

    protected android.view.istener getGestureDetector()
    {
        return new android.view.GestureDetector.SimpleOnGestureListener() {

            private static final int OFFSET = 100;
            final LevelFragment.CardLevelFragment this$1;

            private Card getCardForEvent(MotionEvent motionevent)
            {
                RectF rectf = new RectF();
                Level level = getCurrentLevel();
                CardLevelView cardlevelview = (CardLevelView)getCurrentLevelView();
                float f = motionevent.getX() / cardlevelview.getRatioX();
                float f1 = motionevent.getY() / cardlevelview.getRatioY();
                Iterator iterator = level.getCards().iterator();
                Card card;
                do
                {
                    if (!iterator.hasNext())
                    {
                        return null;
                    }
                    card = (Card)iterator.next();
                    float f2 = cardlevelview.getCardX(card);
                    float f3 = cardlevelview.getCardY(card);
                    rectf.set(f2 - 100F, f3 - 100F, f2 + 100F, f3 + 100F);
                } while (!rectf.contains(f, f1));
                return card;
            }

            public boolean onDoubleTap(MotionEvent motionevent)
            {
                getCardForEvent(motionevent);
                return true;
            }

            
            {
                this$1 = LevelFragment.CardLevelFragment.this;
                super();
            }
        };
    }

    protected int getLevelLayoutViewId()
    {
        return 0x7f030002;
    }

    protected boolean onBroadcastReceive(Context context, Intent intent)
    {
        return Boolean.FALSE.booleanValue();
    }

    protected void updateLevel()
    {
    }

    public er()
    {
    }
}
