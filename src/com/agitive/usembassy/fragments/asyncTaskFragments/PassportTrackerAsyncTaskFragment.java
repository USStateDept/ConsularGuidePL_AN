package com.agitive.usembassy.fragments.asyncTaskFragments;

import com.agitive.usembassy.fragments.layoutFragments.PassportTrackingFragment;
import com.agitive.usembassy.network.PassportTrackerAsyncTask;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class PassportTrackerAsyncTaskFragment extends Fragment {

	public static final String TAG = "com.agitive.usembassy.fragments.asyncTaskFragments.PassportTrackerAsyncTaskFragment.tag";
	public static final String ARGUMENTS_PASSPORT_NUMBER_KEY = "com.agitive.usembassy.fragments.asyncTaskFragments.PassportTrackerAsyncTaskFragment.argumentsPassportNumberKey";
	
	private static PassportTrackerAsyncTaskFragment passportTrackerAsyncTaskFragment;
	private static final String SAVED_INSTANCE_STATE_IS_FIRST_RUN_KEY = "com.agitive.usembassy.fragments.asyncTaskFragments.PassportTrackerAsyncTaskFragment.savedInstanceStateIsFirstRunKey";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PassportTrackerAsyncTaskFragment.passportTrackerAsyncTaskFragment = this;
		
		if (savedInstanceState != null) {
			return;
		}
		
		PassportTrackerAsyncTask passportTrackerAsyncTask = new PassportTrackerAsyncTask(getActivity().getApplicationContext());
		passportTrackerAsyncTask.execute(getArguments().getString(PassportTrackerAsyncTaskFragment.ARGUMENTS_PASSPORT_NUMBER_KEY));
	}
	
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        
        savedInstanceState.putBoolean(PassportTrackerAsyncTaskFragment.SAVED_INSTANCE_STATE_IS_FIRST_RUN_KEY, false);
    }
	
	public static void setPassportStatus(String status) {
		if (PassportTrackerAsyncTaskFragment.passportTrackerAsyncTaskFragment.getParentFragment() == null ||
				PassportTrackerAsyncTaskFragment.passportTrackerAsyncTaskFragment.getActivity() == null) {
		
			return;
		}
		((PassportTrackingFragment)PassportTrackerAsyncTaskFragment.passportTrackerAsyncTaskFragment.getParentFragment()).setPassportStatus(status);
		((PassportTrackingFragment)PassportTrackerAsyncTaskFragment.passportTrackerAsyncTaskFragment.getParentFragment()).removePassportTrackerAsyncTaskFragment();
	}
}
