package com.agitive.usembassy.fragments.asyncTaskFragments;

import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.network.BannerDownloaderAsyncTask;
import com.agitive.usembassy.objects.Banner;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class BannerDownloaderFragment extends Fragment {

	public static final String TAG = "com.agitive.usembassy.fragments.asyncTaskFragment.BannerDownloaderFragment.tag";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
		BannerDownloaderAsyncTask bannerDownloaderAsyncTask = new BannerDownloaderAsyncTask(this);
		bannerDownloaderAsyncTask.execute();
	}
	
	public void showBanner(Banner banner) {
		if (getActivity() == null) {
			return;
		}
		
		((MainActivity)getActivity()).showBanner(banner);
		((MainActivity)getActivity()).removeBannerDownloaderFragment();
	}
}
