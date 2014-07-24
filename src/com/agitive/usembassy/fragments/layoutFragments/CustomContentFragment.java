package com.agitive.usembassy.fragments.layoutFragments;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.databases.CustomContentLayout;
import com.agitive.usembassy.databases.DatabaseReader;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.interfaces.FragmentAboveInterface;
import com.agitive.usembassy.interfaces.LayoutFragmentInterface;
import com.agitive.usembassy.layouts.CustomTextView;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class CustomContentFragment extends Fragment implements LayoutFragmentInterface, FragmentAboveInterface {
	
	public static final String LAYOUT_ID_KEY = "com.agitive.usembassy.fragments.CustomContentFragment.layoutId";
	public static final String IS_IN_STEPS_KEY = "com.agitive.usembassy.fragments.CustomContentFragment.isInStepsKey";
	
	private View rootView;
	private CustomContentLayout customContentLayout;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.custom_content_layout, container, false);

    	setLayout();
        setMarginForEmblem();
        setLayoutName();
        setBackButton();
        setContent(savedInstanceState);
        
        return this.rootView;
	}
	
	@Override
	public void setContent() {
		setCustomContent();
    	hideMainProgressBar();
	}
	
	private void setContent(Bundle savedInstanceState) {
		if (savedInstanceState == null &&
				!getArguments().getBoolean(CustomContentFragment.IS_IN_STEPS_KEY, false)) {
			return;
		}
		
		setContent();
	}
	
	private void setCustomContent() {
		String content;
    	if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
    		content = this.customContentLayout.getContentEn();
    	} else {
    		content = this.customContentLayout.getContentPl();
    	}
    	
    	CustomContentParser parser = new CustomContentParser(getActivity(), getContentWidth(), this.customContentLayout.getId());
    	RelativeLayout customContent = parser.parseCustomContent(content);
    	
    	moveViewsToLocalCustomContent(customContent);
	}
	
	private void showMainProgressBar() {
		RelativeLayout mainProgressBarLayout = (RelativeLayout) this.rootView.findViewById(R.id.custom_content_layout_main_progress_bar_layout);
		if (mainProgressBarLayout == null) {
			return;
		}
		
		mainProgressBarLayout.setVisibility(RelativeLayout.VISIBLE);
	}
	
	private void hideMainProgressBar() {
		RelativeLayout mainProgressBarLayout = (RelativeLayout) this.rootView.findViewById(R.id.custom_content_layout_main_progress_bar_layout);
		if (mainProgressBarLayout == null) {
			return;
		}
		
		mainProgressBarLayout.setVisibility(RelativeLayout.INVISIBLE);
	}
	
	private void setLayout() {
		int id = getArguments().getInt(CustomContentFragment.LAYOUT_ID_KEY);
		
		DatabaseReader databaseReader = new DatabaseReader(getActivity());
		this.customContentLayout = (CustomContentLayout) databaseReader.getLayout(id);
	}
	
	private void setLayoutName() {
		CustomTextView layoutName = (CustomTextView) this.rootView.findViewById(R.id.custom_content_layout_name);
		if (layoutName == null) {
			return;
		}
		
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			layoutName.setText(this.customContentLayout.getTitleEn());
		} else {
			layoutName.setText(this.customContentLayout.getTitlePl());
		}
	}
	
	private void setBackButton() {
		ImageView backButton = (ImageView) this.rootView.findViewById(R.id.custom_content_back_button);
		if (backButton == null) {
			return;
		}
		
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getActivity().onBackPressed();	
			}
			
		});
	}
	
	private String getAppLanguage() {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		return sharedPreferences.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
	}
	
	@Override
	public void changeLanguage() {
		showMainProgressBar();
		
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				setLayoutName();
				
				RelativeLayout rootLayout = (RelativeLayout) rootView.findViewById(R.id.custom_content_root_layout);
				if (rootLayout == null) {
					return;
				}
				
				rootLayout.removeAllViews();
		        
				setCustomContent();
				
				hideMainProgressBar();
			}
			
		});
	}
	
	private void moveViewsToLocalCustomContent(RelativeLayout customContent) {
		RelativeLayout localCustomContent = (RelativeLayout) this.rootView.findViewById(R.id.custom_content_root_layout);
		if (localCustomContent == null) {
			return;
		}
		
    	while (customContent.getChildCount() > 0) {
    		View view = customContent.getChildAt(0);
    		customContent.removeViewAt(0); 
    		localCustomContent.addView(view);
    	}
	}
	
	private void setMarginForEmblem() {
		CustomTextView menuName = (CustomTextView) this.rootView.findViewById(R.id.custom_content_layout_name);
		if (menuName == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) menuName.getLayoutParams();
		params.topMargin = ((MainActivity)getActivity()).getEmblemPartOutsideSize();
		params.topMargin += getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_small);
		menuName.setLayoutParams(params);
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
	
	public boolean isPortraitOrientation() {
    	return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }
	
	private int getContentWidth() {
		int marginLeftRight = (int) getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_xlarge);
		int contentWidth;
    	if (isPortraitOrientation()) {
    		contentWidth = getDisplayWidth() - 2 * marginLeftRight;
    	} else {
    		contentWidth = (int) ((getDisplayWidth() * MainActivity.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH) - (2 * marginLeftRight));
    	}
    	
    	return contentWidth;
	}
}
