package com.agitive.usembassy.fragments.dialogFragments;

import com.agitive.usembassy.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class PhoneCallDialogFragment extends DialogFragment {
	
	public static final String PHONE_NUMBER_KEY = "com.agitive.usembassy.fragments.dialogFragments.PhoneCallDialogFragment.phoneNumberKey";
	public static final String TAG = "com.agitive.usembassy.fragments.dialogFragments.PhoneCallDialogFragment.tag";
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.call_dialog_title);
		builder.setMessage(getMessage());
		builder.setPositiveButton(R.string.call_dialog_positive, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
        		callIntent.setData(Uri.parse("tel:" + getArguments().getString(PhoneCallDialogFragment.PHONE_NUMBER_KEY)));
        		startActivity(callIntent);
			}
		});
		builder.setNegativeButton(R.string.call_dialog_negative, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		return builder.create();
	}
	
	private String getMessage() {
		return getResources().getString(R.string.call_dialog_message) + " " + getArguments().getString(PhoneCallDialogFragment.PHONE_NUMBER_KEY);
	}
}
