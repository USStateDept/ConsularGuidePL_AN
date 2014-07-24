package com.agitive.usembassy.fragments.dialogFragments;

import com.agitive.usembassy.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class FileErrorDialogFragment extends DialogFragment {
	
	public static final String TAG = "com.agitive.usembassy.fragments.dialogFragments.FileErrorDialogFragment.tag";
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.server_error_dialog_title);
		builder.setMessage(R.string.file_manager_file_error);
		builder.setNeutralButton(R.string.server_error_dialog_neutral_button_text, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {	
			}
			
		});
		
		return builder.create();
	}
}
