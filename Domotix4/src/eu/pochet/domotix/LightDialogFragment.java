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
import eu.pochet.domotix.service.ActionBuilder;

public class LightDialogFragment extends DialogFragment {
	public static final String LIGHT_ARGUMENT = "light";

	public Dialog onCreateDialog(Bundle bundle) {
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				getActivity());
		View view = getActivity().getLayoutInflater().inflate(
				R.layout.dialog_light, null);
		final Switch state = (Switch) view.findViewById(R.id.state);
		final boolean initialState = state.isChecked();
		final SeekBar dimmer = (SeekBar) view.findViewById(R.id.dimmer);
		final int initialDimmer = dimmer.getProgress();
		final Spinner spinner = (Spinner) view.findViewById(R.id.delayMinuts);
		ArrayAdapter arrayadapter = ArrayAdapter.createFromResource(
				getActivity(), R.array.delay_minuts_array, android.R.layout.simple_spinner_item);
		arrayadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spinner.setAdapter(arrayadapter);
		builder.setView(view)
				.setTitle(R.string.dialog_light_title)
				.setPositiveButton(android.R.string.ok,
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {
								Light light = ((Light) getArguments().getSerializable("light"));
								ActionBuilder actionBuilder = new ActionBuilder()
									.setType(ActionBuilder.TYPE_LIGHT_SWITCH)
									.setLightId(light.getId());
								if (state.isChecked() != initialState) {
									actionBuilder.setLightStatus(state.isChecked() ? 254 : 0);
								}
								int j = Integer.parseInt((String) spinner.getSelectedItem());
								if (j > 0) {
									actionBuilder.setDelayMinuts(j);
								}

								if (dimmer.getProgress() != initialDimmer) {
									actionBuilder.setLightStatus(dimmer.getProgress());
								}
								actionBuilder.sendMessage(getActivity());
								getDialog().cancel();
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {
								getDialog().cancel();
							}
						});
		return builder.create();
	}
}
