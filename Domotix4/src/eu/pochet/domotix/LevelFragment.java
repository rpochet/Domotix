// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;
import eu.pochet.android.Util;
import eu.pochet.domotix.dao.Card;
import eu.pochet.domotix.dao.LagartoResponse;
import eu.pochet.domotix.dao.LagartoResponseDao;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.dao.LevelDao;
import eu.pochet.domotix.dao.Light;
import eu.pochet.domotix.service.LightStatusUpdateService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package eu.pochet.domotix:
//            LevelView, MessageHelper, CardLevelView, LightLevelView, 
//            LightDialogFragment, NotificationHelper

public abstract class LevelFragment extends Fragment
    implements android.gesture.GestureOverlayView.OnGesturePerformedListener
{
    public static class CardLevelFragment extends LevelFragment
    {

        protected android.view.GestureDetector.SimpleOnGestureListener getGestureDetector()
        {
            return new android.view.GestureDetector.SimpleOnGestureListener() {

                private static final int OFFSET = 100;
                final CardLevelFragment this$1;

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
                this$1 = CardLevelFragment.this;
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

        public CardLevelFragment()
        {
        }
    }

    public static class LightLevelFragment extends LevelFragment
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

        protected android.view.GestureDetector.SimpleOnGestureListener getGestureDetector()
        {
            return new android.view.GestureDetector.SimpleOnGestureListener() {

                private static final int OFFSET = 100;
                final LightLevelFragment this$1;

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
                this$1 = LightLevelFragment.this;
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
                eu.pochet.domotix.dao.LagartoResponse.Status status = LagartoResponseDao.getLagartoResponse(intent.getStringExtra("message")).getStatus(lightStatusId);
                if (status != null)
                {
                    updateLightStatus(status.getValue());
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

        public LightLevelFragment()
        {
            lightStatusId = "xxxx";
        }
    }


    protected List levels;
    protected int mCurrentLevelId;
    private GestureLibrary mLibrary;
    BroadcastReceiver myBroadcastReceiver;
    private ViewFlipper viewFlipper;

    public LevelFragment()
    {
        viewFlipper = null;
        mLibrary = null;
        levels = null;
        mCurrentLevelId = 0;
        myBroadcastReceiver = new BroadcastReceiver() {

            final LevelFragment this$0;

            public void onReceive(Context context, Intent intent)
            {
                if (onBroadcastReceive(context, intent))
                {
                    NotificationHelper.notify(context);
                }
            }

            
            {
                this$0 = LevelFragment.this;
                super();
            }
        };
    }

    protected Level getCurrentLevel()
    {
        return (Level)levels.get(-1 + mCurrentLevelId);
    }

    protected LevelView getCurrentLevelView()
    {
        return (LevelView)viewFlipper.getCurrentView();
    }

    protected abstract android.view.GestureDetector.SimpleOnGestureListener getGestureDetector();

    protected abstract int getLevelLayoutViewId();

    public void onActivityCreated(Bundle bundle)
    {
        super.onActivityCreated(bundle);
        getActivity().registerReceiver(myBroadcastReceiver, new IntentFilter("LightStatusUpdateService"));
        getActivity().registerReceiver(myBroadcastReceiver, new IntentFilter("UDPListenerService"));
    }

    protected abstract boolean onBroadcastReceive(Context context, Intent intent);

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        int i;
        View view = layoutinflater.inflate(getLevelLayoutViewId(), null);
        viewFlipper = (ViewFlipper)view.findViewById(0x7f0c0007);
        levels = LevelDao.getLevels(getActivity());
        if (bundle != null)
        {
            mCurrentLevelId = bundle.getInt("level", 2);
        } else
        {
            mCurrentLevelId = 2;
        }
        viewFlipper.setDisplayedChild(-1 + mCurrentLevelId);
        if (levels == null || levels.size() <= 0) goto _L2; else goto _L1
_L1:
        i = 0;
_L5:
        if (i < viewFlipper.getChildCount()) goto _L3; else goto _L2
_L2:
        view.setDrawingCacheEnabled(false);
        mLibrary = GestureLibraries.fromRawResource(getActivity(), 0x7f050000);
        mLibrary.load();
        ((GestureOverlayView)view).addOnGesturePerformedListener(this);
        view.setOnTouchListener(new android.view.View.OnTouchListener() {

            final LevelFragment this$0;
            private final GestureDetector val$gestureDetector;

            public boolean onTouch(View view1, MotionEvent motionevent)
            {
                return gestureDetector.onTouchEvent(motionevent);
            }

            
            {
                this$0 = LevelFragment.this;
                gestureDetector = gesturedetector;
                super();
            }
        });
        return view;
_L3:
        ((LevelView)viewFlipper.getChildAt(i)).setLevel((Level)levels.get(i));
        i++;
        if (true) goto _L5; else goto _L4
_L4:
    }

    public void onDestroy()
    {
        super.onDestroy();
        getActivity().unregisterReceiver(myBroadcastReceiver);
    }

    public void onGesturePerformed(GestureOverlayView gestureoverlayview, Gesture gesture)
    {
        ArrayList arraylist = mLibrary.recognize(gesture);
        if (arraylist.size() > 0 && ((Prediction)arraylist.get(0)).score > 1.0D)
        {
            String s = ((Prediction)arraylist.get(0)).name;
            if ("ltr".equals(s))
            {
                viewFlipper.showPrevious();
                mCurrentLevelId = 1 + viewFlipper.getDisplayedChild();
                updateLevel();
            } else
            {
                if ("rtl".equals(s))
                {
                    viewFlipper.showNext();
                    mCurrentLevelId = 1 + viewFlipper.getDisplayedChild();
                    updateLevel();
                    return;
                }
                if (s.startsWith("startall"))
                {
                    Bundle bundle = new Bundle();
                    bundle.putString("Domotix.MessageService.ACTION", "switch_on_all");
                    bundle.putInt("Domotix.MessageService.LEVEL", mCurrentLevelId);
                    MessageHelper.sendMessage(getActivity(), bundle);
                    return;
                }
                if (s.startsWith("stopall"))
                {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Domotix.MessageService.ACTION", "switch_off_all");
                    bundle1.putInt("Domotix.MessageService.LEVEL", mCurrentLevelId);
                    MessageHelper.sendMessage(getActivity(), bundle1);
                    return;
                }
            }
        }
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putInt("level", mCurrentLevelId);
    }

    protected abstract void updateLevel();
}
