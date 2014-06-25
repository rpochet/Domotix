// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package eu.pochet.domotix;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import eu.pochet.domotix.dao.Light;

// Referenced classes of package eu.pochet.domotix:
//            MessageHelper

public class LightDialogFragment extends DialogFragment
{

    public static final String LIGHT_ARGUMENT = "light";

    public LightDialogFragment()
    {
    }

    public Dialog onCreateDialog(Bundle bundle)
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(0x7f030001, null);
        final Switch state = (Switch)view.findViewById(0x7f0c0001);
        final boolean initialState = state.isChecked();
        final SeekBar dimmer = (SeekBar)view.findViewById(0x7f0c0002);
        final int initialDimmer = dimmer.getProgress();
        final Spinner spinner = (Spinner)view.findViewById(0x7f0c0005);
        ArrayAdapter arrayadapter = ArrayAdapter.createFromResource(getActivity(), 0x7f090000, 0x1090008);
        arrayadapter.setDropDownViewResource(0x1090009);
        spinner.setAdapter(arrayadapter);
        builder.setView(view).setTitle(0x7f08000e).setPositiveButton(0x104000a, new android.content.DialogInterface.OnClickListener() {

            final LightDialogFragment this$0;
            private final SeekBar val$dimmer;
            private final int val$initialDimmer;
            private final boolean val$initialState;
            private final Spinner val$spinner;
            private final Switch val$state;

            public void onClick(DialogInterface dialoginterface, int i)
            {
                Bundle bundle1;
                bundle1 = new Bundle();
                bundle1.putString("Domotix.MessageService.ACTION", "light");
                bundle1.putInt("Domotix.MessageService.LIGHT", ((Light)getArguments().getSerializable("light")).getId());
                if (state.isChecked() == initialState) goto _L2; else goto _L1
_L1:
                bundle1.putBoolean("state", state.isChecked());
_L6:
                int j = Integer.parseInt((String)spinner.getSelectedItem());
                if (j > 0)
                {
                    bundle1.putInt("delayMinuts", j);
                }
                MessageHelper.sendMessage(getActivity(), bundle1);
_L4:
                return;
_L2:
                if (dimmer.getProgress() == initialDimmer) goto _L4; else goto _L3
_L3:
                bundle1.putInt("dimmer", dimmer.getProgress());
                if (true) goto _L6; else goto _L5
_L5:
            }

            
            {
                this$0 = LightDialogFragment.this;
                state = switch1;
                initialState = flag;
                dimmer = seekbar;
                initialDimmer = i;
                spinner = spinner1;
                super();
            }
        }).setNegativeButton(0x1040000, new android.content.DialogInterface.OnClickListener() {

            final LightDialogFragment this$0;

            public void onClick(DialogInterface dialoginterface, int i)
            {
                getDialog().cancel();
            }

            
            {
                this$0 = LightDialogFragment.this;
                super();
            }
        });
        return builder.create();
    }
}
