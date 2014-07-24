package com.agitive.usembassy.fragments.layoutFragments;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.interfaces.FragmentAboveInterface;
import com.agitive.usembassy.interfaces.LayoutFragmentInterface;
import com.agitive.usembassy.parsers.CustomContentParser;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class AdditionalContentFragment extends Fragment implements LayoutFragmentInterface, FragmentAboveInterface {
	
	public static final String CONTENT_EN_KEY = "com.agitive.usembassy.fragments.layoutFragments.AdditionalContentFragment.contentEn";
	public static final String CONTENT_PL_KEY = "com.agitive.usembassy.fragments.layoutFragments.AdditionalContentFragment.contentPl";
	public static final String LAYOUT_ID_KEY = "com.agitive.usembassy.fragments.layoutFragments.AdditionalContentFragment.layoutId";
	public static final String IS_RESTORED_KEY = "com.agitive.usembassy.fragments.layoutFragments.AdditionalContentFragment.isResoredKey";
	
	private View rootView;
	private int layoutId;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return null;
		}
		
        this.rootView = inflater.inflate(R.layout.additional_content_layout, container, false);
        
        this.layoutId = getArguments().getInt(AdditionalContentFragment.LAYOUT_ID_KEY);
        setContent(savedInstanceState);
        
        return this.rootView;
	}
	
	@Override
	public void changeLanguage() {
		showMainProgressBar();
		
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				setCustomContent();
				
				hideMainProgressBar();
			}
			
		});
	}
	
	@Override
	public void setContent() {
		setCustomContent();
		
		hideMainProgressBar();
	}
	
	public void setContent(Bundle savedInstanceState) {
		if (savedInstanceState == null &&
				!getArguments().getBoolean(AdditionalContentFragment.IS_RESTORED_KEY, false)) {
			return;
		}
		
		setContent();
	}
	
	private void moveViewsToLocalCustomContent(RelativeLayout customContent) {
		RelativeLayout localCustomContent = (RelativeLayout) this.rootView.findViewById(R.id.additional_content_layout_content_layout);
		if (localCustomContent == null) {
			return;
		}
		
		localCustomContent.removeAllViews();
    	while (customContent.getChildCount() > 0) {
    		View view = customContent.getChildAt(0);
    		customContent.removeViewAt(0); 
    		localCustomContent.addView(view);
    	}
	}
	
	private void setCustomContent() {
		CustomContentParser customContentParser = new CustomContentParser(getActivity(), getContentWidth(), this.layoutId);
		RelativeLayout contentLayout = customContentParser.parseCustomContent(getContent());
		moveViewsToLocalCustomContent(contentLayout);
	}
	
	private String getAppLanguage() {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		return sharedPreferences.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
	}
	
	private String getContent() {
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			return getArguments().getString(AdditionalContentFragment.CONTENT_EN_KEY);
		} else {
			return getArguments().getString(AdditionalContentFragment.CONTENT_PL_KEY);
		}
	}
	
	private int getContentWidth() {
		int marginLeftRight = (int) getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_xlarge);
		return (int) (getDisplayWidth() - getDisplayWidth() * MainActivity.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH - 2 * marginLeftRight);
	}
	
	private void hideMainProgressBar() {
		RelativeLayout mainProgressBar = (RelativeLayout) this.rootView.findViewById(R.id.additional_content_layout_main_progress_bar_layout);
		if (mainProgressBar == null) {
			return;
		}
		
		mainProgressBar.setVisibility(RelativeLayout.INVISIBLE);
	}
	
	private void showMainProgressBar() {
		RelativeLayout mainProgressBar = (RelativeLayout) this.rootView.findViewById(R.id.additional_content_layout_main_progress_bar_layout);
		if (mainProgressBar == null) {
			return;
		}
		
		mainProgressBar.setVisibility(RelativeLayout.VISIBLE);
	}
	
	@SuppressWarnings("deprecation")
	private int getDisplayWidth() {
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		
		if (Build.VERSION.SDK_INT < 13) {
			return display.getWidth();
		} else {
			Point size = new Point();
			display.getSize(size);
			
			return size.x;
		}
	}
	
	private int getOrientation() {
		return getResources().getConfiguration().orientation;
	}
}
