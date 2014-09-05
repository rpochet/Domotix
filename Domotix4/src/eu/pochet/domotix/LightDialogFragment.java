package eu.pochet.domotix;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import eu.pochet.domotix.dao.Light;

public class LightDialogFragment extends DialogFragment
{
    public static final String LIGHT_ARGUMENT = "light";

    public Dialog onCreateDialog(Bundle bundle)
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_light, null);
        final Switch state = (Switch) view.findViewById(R.id.state);
        final boolean initialState = state.isChecked();
        final SeekBar dimmer = (SeekBar) view.findViewById(R.id.dimmer);
        final int initialDimmer = dimmer.getProgress();
        final Spinner spinner = (Spinner) view.findViewById(R.id.delayMinuts);
        ArrayAdapter arrayadapter = ArrayAdapter.createFromResource(getActivity(), R.array.delay_minuts_array, 0x1090008);
        arrayadapter.setDropDownViewResource(0x1090009);
        spinner.setAdapter(arrayadapter);
        builder.setView(view).setTitle(R.string.dialog_light_title).setPositiveButton(0x104000a, new android.content.DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialoginterface, int i)
            {
                Bundle bundle1 = new Bundle();
                bundle1.putString("Domotix.MessageService.ACTION", "light");
                bundle1.putInt("Domotix.MessageService.LIGHT", ((Light)getArguments().getSerializable("light")).getId());
                if (state.isChecked() != initialState)
                {                
                	bundle1.putBoolean("state", state.isChecked());
				}
                int j = Integer.parseInt((String) spinner.getSelectedItem());
                if (j > 0)
                {
                    bundle1.putInt("delayMinuts", j);
                }

                if(dimmer.getProgress() != initialDimmer)
                {
                	bundle1.putInt("dimmer", dimmer.getProgress());
                }
                MessageHelper.sendMessage(getActivity(), bundle1);
                getDialog().cancel();
            }
        }).setNegativeButton(0x1040000, new android.content.DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i)
            {
                getDialog().cancel();
            }
        });
        return builder.create();
    }
}
