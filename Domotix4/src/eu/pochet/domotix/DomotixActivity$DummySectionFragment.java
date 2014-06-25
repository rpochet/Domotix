// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

// Referenced classes of package eu.pochet.domotix:
//            DomotixActivity

public static class  extends Fragment
{

    public static final String ARG_SECTION_NUMBER = "section_number";

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        TextView textview = new TextView(getActivity());
        textview.setText("text");
        return textview;
    }

    public ()
    {
    }
}
