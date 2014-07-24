package com.agitive.usembassy.fragments.dialogFragments;

import com.agitive.usembassy.R;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressDialogFragment extends DialogFragment {
	
	public static final String TAG = "com.agitive.usembassy.fragments.dialogFragments.ProgressDialogFragment";
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog progressDialog = new ProgressDialog(this.getActivity());
		progressDialog.setMessage(getActivity().getResources().getString(R.string.progress_dialog_message));
		progressDialog.setCancelable(false);
		
		return progressDialog;
	}
}
