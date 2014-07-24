package com.agitive.usembassy.fragments.dialogFragments;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ApplicationUpdateDialogFragment extends DialogFragment {
	
	public static final String TAG = "com.agitive.usembassy.fragments.ApplicationUpdateDialogFragment";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.update_question_updated_available);
		builder.setMessage(R.string.update_question_content);
		builder.setNegativeButton(R.string.update_question_next_time, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
    	});
		builder.setPositiveButton(R.string.update_question_update_now, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				((MainActivity)getActivity()).updateDatabaseAndGoToMainScreen();
			}
		});
		
		
		
		return builder.create();
	}	
}
