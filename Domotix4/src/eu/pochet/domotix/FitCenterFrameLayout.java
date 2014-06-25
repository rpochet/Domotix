// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class FitCenterFrameLayout extends ViewGroup
{

    public FitCenterFrameLayout(Context context)
    {
        super(context);
    }

    public FitCenterFrameLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        int i1;
        int j1;
        int k1;
        int l1;
        int i2;
        int j2;
        int k2;
        int l2;
        i1 = getChildCount();
        j1 = getPaddingLeft();
        k1 = getPaddingTop();
        l1 = k - i - getPaddingRight();
        i2 = l - j - getPaddingBottom();
        j2 = l1 - j1;
        k2 = i2 - k1;
        l2 = 0;
_L2:
        View view;
        if (l2 >= i1)
        {
            return;
        }
        view = getChildAt(l2);
        if (view.getVisibility() != 8)
        {
            break; /* Loop/switch isn't completed */
        }
_L3:
        l2++;
        if (true) goto _L2; else goto _L1
_L1:
        int i3 = view.getPaddingLeft();
        int j3 = view.getPaddingTop();
        int k3 = view.getPaddingRight();
        int l3 = view.getPaddingBottom();
        int i4 = view.getMeasuredWidth() - i3 - k3;
        int j4 = view.getMeasuredHeight() - j3 - l3;
        int k4 = j2 - i3 - k3;
        int l4 = k2 - j3 - l3;
        if (k4 * j4 > l4 * i4)
        {
            int j5 = k3 + (i3 + (i4 * l4) / j4);
            view.layout(j1 + (j2 - j5) / 2, k1, l1 - (j2 - j5) / 2, i2);
        } else
        {
            int i5 = l3 + (j3 + (j4 * k4) / i4);
            view.layout(j1, k1 + (k2 - i5) / 2, l1, k1 + (k2 + i5) / 2);
        }
          goto _L3
        if (true) goto _L2; else goto _L4
_L4:
    }

    protected void onMeasure(int i, int j)
    {
        int k = resolveSize(getSuggestedMinimumWidth(), i);
        int l = resolveSize(getSuggestedMinimumHeight(), j);
        setMeasuredDimension(k, l);
        int i1 = android.view.View.MeasureSpec.makeMeasureSpec(k, 0);
        int j1 = android.view.View.MeasureSpec.makeMeasureSpec(l, 0);
        int k1 = getChildCount();
        int l1 = 0;
        do
        {
            if (l1 >= k1)
            {
                return;
            }
            getChildAt(l1).measure(i1, j1);
            l1++;
        } while (true);
    }
}
