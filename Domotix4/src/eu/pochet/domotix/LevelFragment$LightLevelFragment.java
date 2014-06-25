// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;
import eu.pochet.android.Util;
import eu.pochet.domotix.dao.LagartoResponse;
import eu.pochet.domotix.dao.LagartoResponseDao;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.dao.Light;
import eu.pochet.domotix.service.LightStatusUpdateService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package eu.pochet.domotix:
//            LevelFragment, LightLevelView, LightDialogFragment, MessageHelper

public static class lightStatusId extends LevelFragment
{

    private String lightStatusId;

    private void updateLightStatus(String s)
    {
        s.length();
        Iterator iterator = ((Level)levels.get(-1 + mCurrentLevelId)).getLights().iterator();
        do
        {
            if (!iterator.hasNext())
            {
                return;
            }
            Light _tmp = (Light)iterator.next();
        } while (true);
    }

    protected android.view.stener getGestureDetector()
    {
        return new android.view.GestureDetector.SimpleOnGestureListener() {

            private static final int OFFSET = 100;
            final LevelFragment.LightLevelFragment this$1;

            private Light getLightForEvent(MotionEvent motionevent)
            {
                Level level = getCurrentLevel();
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
                LightLevelView lightlevelview = (LightLevelView)getCurrentLevelView();
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
                    lightdialogfragment.show(getFragmentManager(), "light");
                }
                return true;
            }

            public void onLongPress(MotionEvent motionevent)
            {
                Light light = getLightForEvent(motionevent);
                if (light != null)
                {
                    Toast.makeText(getActivity(), (new StringBuilder("onDoubleTap light ")).append(light.getName()).toString(), 0).show();
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
                    MessageHelper.sendMessage(getActivity(), bundle);
                    Toast.makeText(getActivity(), (new StringBuilder("Toggle light ")).append(light.getName()).toString(), 0).show();
                }
                return true;
            }

            
            {
                this$1 = LevelFragment.LightLevelFragment.this;
                super();
            }
        };
    }

    protected int getLevelLayoutViewId()
    {
        return 0x7f030003;
    }

    protected boolean onBroadcastReceive(Context context, Intent intent)
    {
        String s = intent.getAction();
        if ("LightStatusUpdateService".equals(s))
        {
            updateLightStatus(intent.getStringExtra("status"));
            return Boolean.TRUE.booleanValue();
        }
        if ("UDPListenerService".equals(s))
        {
            updateLightStatus(Util.ByteArrayToHexString(intent.getByteArrayExtra("message")).substring(22, 2 * intent.getIntExtra("messageLength", 32)));
            return Boolean.TRUE.booleanValue();
        }
        if ("LagartoEventService".equals(s))
        {
            eu.pochet.domotix.dao.ctivity ctivity = LagartoResponseDao.getLagartoResponse(intent.getStringExtra("message")).getStatus(lightStatusId);
            if (ctivity != null)
            {
                updateLightStatus(ctivity.teLightStatus());
            }
            return Boolean.TRUE.booleanValue();
        } else
        {
            return Boolean.FALSE.booleanValue();
        }
    }

    protected void updateLevel()
    {
        Intent intent = new Intent(getActivity(), eu/pochet/domotix/service/LightStatusUpdateService);
        getActivity().startService(intent);
    }

    public r()
    {
        lightStatusId = "xxxx";
    }
}
