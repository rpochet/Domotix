// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.graphics.RectF;
import android.view.MotionEvent;
import eu.pochet.domotix.dao.Card;
import eu.pochet.domotix.dao.Level;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package eu.pochet.domotix:
//            CardLevelView, LevelFragment

class  extends android.view.tener
{

    private static final int OFFSET = 100;
    final getCardForEvent this$1;

    private Card getCardForEvent(MotionEvent motionevent)
    {
        RectF rectf = new RectF();
        Level level = tCurrentLevel();
        CardLevelView cardlevelview = (CardLevelView)tCurrentLevelView();
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

    ()
    {
        this$1 = this._cls1.this;
        super();
    }
}
