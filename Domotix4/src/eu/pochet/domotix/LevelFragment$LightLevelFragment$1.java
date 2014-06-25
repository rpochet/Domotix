// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.app.DialogFragment;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.dao.Light;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package eu.pochet.domotix:
//            LightLevelView, LightDialogFragment, MessageHelper, LevelFragment

class <init> extends android.view.ener
{

    private static final int OFFSET = 100;
    final tActivity this$1;

    private Light getLightForEvent(MotionEvent motionevent)
    {
        Level level = tCurrentLevel();
        List list = getLightForEvent(motionevent, level.getLights(), 100);
        if (list.size() == 1)
        {
            return (Light)list.get(0);
        }
        List list1 = getLightForEvent(motionevent, level.getLights(), 70);
        if (list1.size() == 1)
        {
            return (Light)list1.get(0);
        } else
        {
            return null;
        }
    }

    private List getLightForEvent(MotionEvent motionevent, List list, int i)
    {
        RectF rectf = new RectF();
        LightLevelView lightlevelview = (LightLevelView)tCurrentLevelView();
        float f = motionevent.getX() / lightlevelview.getRatioX();
        float f1 = motionevent.getY() / lightlevelview.getRatioY();
        ArrayList arraylist = new ArrayList();
        Iterator iterator = list.iterator();
        do
        {
            Light light;
            do
            {
                if (!iterator.hasNext())
                {
                    return arraylist;
                }
                light = (Light)iterator.next();
                float f2 = lightlevelview.getLightX(light);
                float f3 = lightlevelview.getLightY(light);
                rectf.set(f2 - (float)i, f3 - (float)i, f2 + (float)i, f3 + (float)i);
            } while (!rectf.contains(f, f1));
            arraylist.add(light);
        } while (true);
    }

    public boolean onDoubleTap(MotionEvent motionevent)
    {
        Light light = getLightForEvent(motionevent);
        if (light != null)
        {
            LightDialogFragment lightdialogfragment = new LightDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("light", light);
            lightdialogfragment.setArguments(bundle);
            lightdialogfragment.show(tFragmentManager(), "light");
        }
        return true;
    }

    public void onLongPress(MotionEvent motionevent)
    {
        Light light = getLightForEvent(motionevent);
        if (light != null)
        {
            Toast.makeText(tActivity(), (new StringBuilder("onDoubleTap light ")).append(light.getName()).toString(), 0).show();
        }
    }

    public boolean onSingleTapConfirmed(MotionEvent motionevent)
    {
        Light light = getLightForEvent(motionevent);
        if (light != null)
        {
            Bundle bundle = new Bundle();
            bundle.putString("Domotix.MessageService.ACTION", "toggle_light");
            bundle.putInt("Domotix.MessageService.LIGHT", light.getId());
            MessageHelper.sendMessage(tActivity(), bundle);
            Toast.makeText(tActivity(), (new StringBuilder("Toggle light ")).append(light.getName()).toString(), 0).show();
        }
        return true;
    }

    ()
    {
        this$1 = this._cls1.this;
        super();
    }
}
