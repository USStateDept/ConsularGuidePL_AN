package com.agitive.usembassy.fragments.asyncTaskFragments;

import com.agitive.usembassy.fragments.layoutFragments.MainScreenFragment;
import com.agitive.usembassy.fragments.layoutFragments.NewsFragment;
import com.agitive.usembassy.network.RSSAndTweetsDownloaderAsyncTask;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class RSSAndTweetsDownloaderFragment extends Fragment {

	public static final String TAG = "com.agitive.usembassy.fragments.asyncTaskFragments.RSSAndTweetsDownloaderFragment.tag";
	public static final String ARGUMENTS_PARENT_NAME_KEY = "com.agitive.usembassy.fragments.asyncTaskFragments.RSSAndTweetsDownloaderFragment.argumentsParentnameKey";
	public static final String ARGUMENTS_PARENT_NAME_MAIN_SCREEN = "com.agitive.usembassy.fragments.asyncTaskFragments.RSSAndTweetsDownloaderFragment.mainScreen";
	public static final String ARGUMENTS_PARENT_NAME_NEWS = "com.agitive.usembassy.fragments.asyncTaskFragments.RSSAndTweetsDownloaderFragment.news";
	
	private static RSSAndTweetsDownloaderFragment rssAndTweetsDownloaderFragment;
	private static final String SAVED_INSTANCE_STATE_IS_FIRST_RUN_KEY = "com.agitive.usembassy.fragments.asyncTaskFragments.RSSAndTweetsDownloaderFragment.savedInstanceStateIsFirstRunKey";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		RSSAndTweetsDownloaderFragment.rssAndTweetsDownloaderFragment = this;
		
		if (savedInstanceState != null) {
			return;
		}
		
		RSSAndTweetsDownloaderAsyncTask rssAndTweetsDownloaderAsyncTask = new RSSAndTweetsDownloaderAsyncTask(getActivity().getApplicationContext());
		rssAndTweetsDownloaderAsyncTask.execute();
	}
	
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        
        savedInstanceState.putBoolean(RSSAndTweetsDownloaderFragment.SAVED_INSTANCE_STATE_IS_FIRST_RUN_KEY, false);
    }
	
	public static void setUpdateStatus(boolean isSuccess) {
		if (RSSAndTweetsDownloaderFragment.rssAndTweetsDownloaderFragment.getParentFragment() == null ||
				RSSAndTweetsDownloaderFragment.rssAndTweetsDownloaderFragment.getActivity() == null) {
			
			return;
		}
		
		if (RSSAndTweetsDownloaderFragment.rssAndTweetsDownloaderFragment.getArguments().getString(RSSAndTweetsDownloaderFragment.ARGUMENTS_PARENT_NAME_KEY).equals(RSSAndTweetsDownloaderFragment.ARGUMENTS_PARENT_NAME_MAIN_SCREEN)) {
			setUpdateStatusInMainScreen(isSuccess);
		} else if (RSSAndTweetsDownloaderFragment.rssAndTweetsDownloaderFragment.getArguments().getString(RSSAndTweetsDownloaderFragment.ARGUMENTS_PARENT_NAME_KEY).equals(RSSAndTweetsDownloaderFragment.ARGUMENTS_PARENT_NAME_NEWS)) {
			setUpdateStatusInNews(isSuccess);
		}
	}
	
	private static void setUpdateStatusInMainScreen(boolean isSuccess) {
		((MainScreenFragment)RSSAndTweetsDownloaderFragment.rssAndTweetsDownloaderFragment.getParentFragment()).setRSSAndTweetsUpdateStatus(isSuccess);
		((MainScreenFragment)RSSAndTweetsDownloaderFragment.rssAndTweetsDownloaderFragment.getParentFragment()).removeRSSAndTweetsDownloaderFragment();
	}
	
	private static void setUpdateStatusInNews(boolean isSuccess) {
		((NewsFragment)RSSAndTweetsDownloaderFragment.rssAndTweetsDownloaderFragment.getParentFragment()).setRSSAndTweetsUpdateStatus(isSuccess);
		((NewsFragment)RSSAndTweetsDownloaderFragment.rssAndTweetsDownloaderFragment.getParentFragment()).removeRSSAndTweetsDownloaderFragment();
	}
}
