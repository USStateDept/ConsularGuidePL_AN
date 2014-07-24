package com.agitive.usembassy.fragments.asyncTaskFragments;

import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.network.ApplicationContentDownloaderAsyncTask;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ApplicationContentDownloaderFragment extends Fragment {

	public static final String TAG = "com.agitive.usembassy.fragments.asyncTaskFragments.ApplicationContentDownloaderFragment.tag";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
		
		ApplicationContentDownloaderAsyncTask applicationContentDownloaderAsyncTask = new ApplicationContentDownloaderAsyncTask(this);
		applicationContentDownloaderAsyncTask.execute();
	}
	
	public void removeFragment() {
		if (getActivity() == null) {
			return;
		}
		
		((MainActivity)getActivity()).removeApplicationContentDownloaderFragment();
	}
	
	public void saveDownloadingTime() {
		if (getActivity() == null) {
			return;
		}
		
		((MainActivity)getActivity()).saveContentDownloadingTime();
	}
}
