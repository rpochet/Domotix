// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import eu.pochet.domotix.dao.Light;

// Referenced classes of package eu.pochet.domotix:
//            LightDialogFragment, MessageHelper

class val.spinner
    implements android.content.kListener
{

    final LightDialogFragment this$0;
    private final SeekBar val$dimmer;
    private final int val$initialDimmer;
    private final boolean val$initialState;
    private final Spinner val$spinner;
    private final Switch val$state;

    public void onClick(DialogInterface dialoginterface, int i)
    {
        Bundle bundle;
        bundle = new Bundle();
        bundle.putString("Domotix.MessageService.ACTION", "light");
        bundle.putInt("Domotix.MessageService.LIGHT", ((Light)getArguments().getSerializable("light")).getId());
        if (val$state.isChecked() == val$initialState) goto _L2; else goto _L1
_L1:
        bundle.putBoolean("state", val$state.isChecked());
_L6:
        int j = Integer.parseInt((String)val$spinner.getSelectedItem());
        if (j > 0)
        {
            bundle.putInt("delayMinuts", j);
        }
        MessageHelper.sendMessage(getActivity(), bundle);
_L4:
        return;
_L2:
        if (val$dimmer.getProgress() == val$initialDimmer) goto _L4; else goto _L3
_L3:
        bundle.putInt("dimmer", val$dimmer.getProgress());
        if (true) goto _L6; else goto _L5
_L5:
    }

    istener()
    {
        this$0 = final_lightdialogfragment;
        val$state = switch1;
        val$initialState = flag;
        val$dimmer = seekbar;
        val$initialDimmer = i;
        val$spinner = Spinner.this;
        super();
    }
}
