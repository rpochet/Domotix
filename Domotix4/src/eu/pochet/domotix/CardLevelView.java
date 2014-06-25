// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import eu.pochet.domotix.dao.Card;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.dao.Room;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package eu.pochet.domotix:
//            LevelView

public class CardLevelView extends LevelView
{

    private static final int OFFSET = -20;
    private Bitmap mCardBitmap;
    private Bitmap mTempBitmap;

    public CardLevelView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public CardLevelView(Context context, Level level)
    {
        super(context, level);
    }

    public float getCardX(Card card)
    {
        return (float)card.getRoom().getX() + (float)levelX + (float)card.getX();
    }

    public float getCardY(Card card)
    {
        return (float)card.getRoom().getY() + (float)levelY + (float)card.getY();
    }

    public void init()
    {
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inDither = true;
        options.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
        mCardBitmap = BitmapFactory.decodeResource(getResources(), 0x7f020001, options);
        mTempBitmap = BitmapFactory.decodeResource(getResources(), 0x7f020002, options);
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        Iterator iterator = level.getCards().iterator();
        do
        {
            if (!iterator.hasNext())
            {
                return;
            }
            Card card = (Card)iterator.next();
            float f = getCardX(card);
            float f1 = getCardY(card);
            if (card.getStatus() == 0)
            {
                canvas.drawBitmap(mCardBitmap, -20F + f * getRatioX(), f1 * getRatioY(), null);
            } else
            {
                canvas.drawBitmap(mTempBitmap, -20F + f * getRatioX(), f1 * getRatioY(), null);
            }
        } while (true);
    }
}
