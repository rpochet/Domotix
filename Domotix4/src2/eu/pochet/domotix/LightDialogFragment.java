package eu.pochet.domotix;

import eu.pochet.domotix.dao.Light;
import eu.pochet.domotix.service.MessageService;
import android.app.AlertDialog;
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


public class LightDialogFragment extends DialogFragment  {
	
	public static final String LIGHT_ARGUMENT = "light";
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	
	    final View dialogView = inflater.inflate(R.layout.dialog_light, null);

	    final Switch state = (Switch) dialogView.findViewById(R.id.state);
	    final boolean initialState = state.isChecked();
	    
	    final SeekBar dimmer = (SeekBar) dialogView.findViewById(R.id.dimmer);
	    final int initialDimmer = dimmer.getProgress();
		
	    // Setup delay spinner 
	    final Spinner spinner = (Spinner) dialogView.findViewById(R.id.delayMinuts);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.delay_minuts_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	    
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(dialogView)
	        .setTitle(R.string.dialog_light_title)
	    	// Add action buttons
	        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int id) {
	        	    Bundle parameters = new Bundle();
					parameters.putString(MessageService.ACTION, "light");
					parameters.putInt(MessageService.LIGHT_ID, ((Light) getArguments().getSerializable(LIGHT_ARGUMENT)).getId());
					if(state.isChecked() != initialState) {
						parameters.putBoolean("state", state.isChecked());
					} else if(dimmer.getProgress() != initialDimmer) {
						parameters.putInt("dimmer", dimmer.getProgress());
					} else {
						return;
					}
					int delayMinuts = Integer.parseInt((String) spinner.getSelectedItem());
					if(delayMinuts > 0) {
						parameters.putInt("delayMinuts", delayMinuts);
					}
					MessageHelper.sendMessage(getActivity(), parameters);
	            }
	        })
	        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int id) {
	            	LightDialogFragment.this.getDialog().cancel();
	            }
	        });
		
	    /*final View delayItems = dialogView.findViewById(R.id.delayItems);
	    View delayTitle = dialogView.findViewById(R.id.delayTitle);
	    delayTitle.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				boolean enabled = !delayItems.isShown();
				delayItems.setEnabled(enabled);
				delayItems.setVisibility(enabled ? View.VISIBLE : View.GONE);
			}
		});
	    delayItems.setEnabled(false);
	    delayItems.setVisibility(View.GONE);*/
	    return builder.create(); 
	}

}