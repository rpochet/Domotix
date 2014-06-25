// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

// Referenced classes of package eu.pochet.domotix:
//            DomotixActivity

public static class mClass
    implements android.app.tener
{

    private final Activity mActivity;
    private final Class mClass;
    private Fragment mFragment;
    private final String mTag;

    public void onTabReselected(android.app.tener tener, FragmentTransaction fragmenttransaction)
    {
    }

    public void onTabSelected(android.app.tener tener, FragmentTransaction fragmenttransaction)
    {
        if (mFragment == null)
        {
            mFragment = Fragment.instantiate(mActivity, mClass.getName());
            fragmenttransaction.add(0x1020002, mFragment, mTag);
            return;
        } else
        {
            fragmenttransaction.attach(mFragment);
            return;
        }
    }

    public void onTabUnselected(android.app.tener tener, FragmentTransaction fragmenttransaction)
    {
        if (mFragment != null)
        {
            fragmenttransaction.detach(mFragment);
        }
    }

    public (Activity activity, String s, Class class1)
    {
        mActivity = activity;
        mTag = s;
        mClass = class1;
    }
}
