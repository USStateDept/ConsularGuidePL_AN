package com.agitive.usembassy.fragments.layoutFragments;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.layouts.CustomTextView;
import com.agitive.usembassy.objects.Banner;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

public class BannerFragment extends Fragment {
	
	public static final String BANNER_TYPE_KEY = "com.agitive.usembassy.fragments.BannerFragment.bannerTypeKey";
	public static final String BANNER_TITLE_EN_KEY = "com.agitive.usembassy.fragments.BannerFragment.bannerTitleEnKey";
	public static final String BANNER_TITLE_PL_KEY = "com.agitive.usembassy.fragments.BannerFragment.bannerTitlePlKey";
	public static final String BANNER_TEXT_EN_KEY = "com.agitive.usembassy.fragments.BannerFragment.bannerTextEnKey";
	public static final String BANNER_TEXT_PL_KEY = "com.agitive.usembassy.fragments.BannerFragment.bannerTextPlKey";
	
	private static final int BANNER_TITLE_BACKGROUND_MIN_LENGHT_TO_CUT = 5;
	private static final int BANNER_TITLE_BACKGROUND_CUT_LENGTH = 3;
	private static final double BANNER_TITLE_BACKGROUND_HEIGHT_TO_BANNER_LAYOUT_HEIGHT_PORTRAIT = 0.32;
	private static final double BANNER_TITLE_BACKGROUND_HEIGHT_TO_BANNER_LAYOUT_HEIGHT_LANDSCAPE = 0.7;
	private static final double BANNER_TYPE_HEIGHT_TO_BANNER_LAYOUT_HEIGHT_PORTRAIT = 0.05;
	private static final double BANNER_TYPE_HEIGHT_TO_BANNER_LAYOUT_HEIGHT_LADSCAPE = 0.07;
	private static final double BANNER_TITLE_HEIGHT_TO_BANNER_LAYOUT_HEIGHT_PORTRAIT = 0.15;
	private static final double BANNER_TITLE_HEIGHT_TO_BANNER_LAYOUT_HEIGHT_LANDSCAPE = 0.17;
	private static final double BANNER_TEXT_HEIGHT_TO_BANNER_LAYOUT_HEIGHT_PORTRAIT = 0.064;
	private static final double BANNER_TEXT_HEIGHT_TO_BANNER_LAYOUT_HEIGHT_LANDSCAPE = 0.1;
	
	private View rootView;
	private Banner banner;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(getlayoutId(), container, false);
        
        setBannerDimensions();
        
        createBannerObject();
        
        setBannerBackground();
        setBannerType();
        setBannerTitle();
        setBannerTitleBackground();
        setBannerText();
        
        setOnClickListenerForBanner();
        
        return this.rootView;
	}
	
	public void changeLanguage() {
        setBannerType();
        setBannerTitle();
        setBannerTitleBackground();
        setBannerText();
	}
	
	private void createBannerObject() {
		Banner banner = new Banner(this.getActivity());
		banner.setContentEn(getArguments().getString(BannerFragment.BANNER_TEXT_EN_KEY));
		banner.setContentPl(getArguments().getString(BannerFragment.BANNER_TEXT_PL_KEY));
		banner.setTitleEn(getArguments().getString(BannerFragment.BANNER_TITLE_EN_KEY));
		banner.setTitlePl(getArguments().getString(BannerFragment.BANNER_TITLE_PL_KEY));
		banner.setType(getArguments().getInt(BannerFragment.BANNER_TYPE_KEY));
		
		this.banner = banner;
	}
	
	private void setBannerDimensions() {
		final ViewTreeObserver rootViewObserver = this.rootView.getViewTreeObserver();
		rootViewObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			private boolean ready = false;
			
			@Override
			public void onGlobalLayout() {
				if (this.ready) {
					return;
				}
				if (rootView.getHeight() > 10) {
					this.ready = true;
				}
				setBannerTitleBackgroundHeight((int) (rootView.getHeight() * getBannerTitleBackgroundHeightToBannerLayoutHeight()));
		        setBannerTypeHeight((int) (rootView.getHeight() * getBannerTypeHeightToBannerLayoutHeight()));
		        setBannerTitleHeight((int) (rootView.getHeight() * getBannerTitleHeightToBannerLayoutHeight()));
		        setBannerTextHeight((int) (rootView.getHeight() * getBannerTextHeightToBannerLayoutHeight()));
			}
			
		});
	}
	
	private void setOnClickListenerForBanner() {
		ImageView bannerClose = (ImageView) this.rootView.findViewById(R.id.banner_layout_banner_close);
		if (bannerClose == null) {
			return;
		}
		
		bannerClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (getParentFragment() != null) {
					((MainScreenFragment)getParentFragment()).closeBanner();
				} else {
					((MainActivity)getActivity()).closeBanner();
				}
			}
			
		});
	}
	
	private int getlayoutId() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return R.layout.banner_layout;
		} else {
			return R.layout.banner_right_column_layout;
		}
	}

	private int getOrientation() {
		return getResources().getConfiguration().orientation;
	}
	
	private void setBannerBackground() {
		ImageView bannerBackground = (ImageView) this.rootView.findViewById(R.id.banner_layout_banner_background);
		if (bannerBackground == null) {
			return;
		}
		
		switch (this.banner.getType()) {
			case Banner.TYPE_CALENDAR_UPDATE:
				bannerBackground.setImageResource(R.drawable.banner_background);
				break;
			case Banner.TYPE_EMERGENCY:
				bannerBackground.setImageResource(R.drawable.alert_background);
				break;
			case Banner.TYPE_GENERAL_UPDATE:
				bannerBackground.setImageResource(R.drawable.banner_background);
				break;
			case Banner.TYPE_NEW_MEDIA_RELEASE:
				bannerBackground.setImageResource(R.drawable.banner_background);
				break;
			case Banner.TYPE_SECURITY_ADVISORY:
				bannerBackground.setImageResource(R.drawable.banner_background);
				break;
		}
	}
	
	private void setBannerType() {
		CustomTextView typeText = (CustomTextView) this.rootView.findViewById(R.id.banner_layout_banner_type_text);
		if (typeText == null) {
			return;
		}
		
		typeText.setText(this.banner.getTypeText());
	}
	
	private void setBannerTitle() {
		CustomTextView bannerTitle = (CustomTextView) this.rootView.findViewById(R.id.banner_layout_banner_title);
		if (bannerTitle == null) {
			return;
		}
		
		bannerTitle.setText(getBannerTitle());
	}
	
	private String getBannerTitle() {
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			return this.banner.getTitleEn();
		} else {
			return this.banner.getTitlePl();
		}
	}
	
	private String getAppLanguage() {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		return sharedPreferences.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
	}
	
	private String getBannerText() {
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			return this.banner.getContentEn();
		} else {
			return this.banner.getContentPl();
		}
	}
	
	private void setBannerText() {
		CustomTextView bannerTitle = (CustomTextView) this.rootView.findViewById(R.id.banner_layout_banner_content);
		if (bannerTitle == null) {
			return;
		}
		
		bannerTitle.setText(getBannerText());
	}
	
	private void setBannerTitleBackground() {
		CustomTextView bannerTitle = (CustomTextView) this.rootView.findViewById(R.id.banner_layout_banner_title_background);
		if (bannerTitle == null) {
			return;
		}
		
		String title = getBannerTitle();
		
		if (title.length() > BannerFragment.BANNER_TITLE_BACKGROUND_MIN_LENGHT_TO_CUT) {
			title = title.substring(BannerFragment.BANNER_TITLE_BACKGROUND_CUT_LENGTH);
		}
		bannerTitle.setText(getBannerTitle());
	}
	
	private void setBannerTitleBackgroundHeight(int height) {
		CustomTextView bannerTitleBackground = (CustomTextView) rootView.findViewById(R.id.banner_layout_banner_title_background);
		if (bannerTitleBackground == null){
			return;
		}
		
		bannerTitleBackground.setTextSize(MainActivity.pxToSp(height, getActivity()));
	}
	
	private double getBannerTitleBackgroundHeightToBannerLayoutHeight() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return BannerFragment.BANNER_TITLE_BACKGROUND_HEIGHT_TO_BANNER_LAYOUT_HEIGHT_PORTRAIT;
		} else {
			return BannerFragment.BANNER_TITLE_BACKGROUND_HEIGHT_TO_BANNER_LAYOUT_HEIGHT_LANDSCAPE;
		}
	}
	
	private double getBannerTypeHeightToBannerLayoutHeight() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return BannerFragment.BANNER_TYPE_HEIGHT_TO_BANNER_LAYOUT_HEIGHT_PORTRAIT;
		} else {
			return BannerFragment.BANNER_TYPE_HEIGHT_TO_BANNER_LAYOUT_HEIGHT_LADSCAPE;
		}
	}
	
	private double getBannerTitleHeightToBannerLayoutHeight() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return BannerFragment.BANNER_TITLE_HEIGHT_TO_BANNER_LAYOUT_HEIGHT_PORTRAIT;
		} else {
			return BannerFragment.BANNER_TITLE_HEIGHT_TO_BANNER_LAYOUT_HEIGHT_LANDSCAPE;
		}
	}
	
	private void setBannerTypeHeight(int height) {
		CustomTextView bannerTypeText = (CustomTextView) rootView.findViewById(R.id.banner_layout_banner_type_text);
		if (bannerTypeText == null) {
			return;
		}
		
		bannerTypeText.setTextSize(MainActivity.pxToSp(height, getActivity()));
	}
	
	private void setBannerTitleHeight(int height) {
		CustomTextView bannerTitle = (CustomTextView) rootView.findViewById(R.id.banner_layout_banner_title);
		if (bannerTitle == null) {
			return;
		}
		
		bannerTitle.setTextSize(MainActivity.pxToSp(height, getActivity()));
	}
	
	private void setBannerTextHeight(int height) {
		CustomTextView bannerContent = (CustomTextView) rootView.findViewById(R.id.banner_layout_banner_content);
		if (bannerContent == null) {
			return;
		}
		
		bannerContent.setTextSize(MainActivity.pxToSp(height, getActivity()));
	}
	
	private double getBannerTextHeightToBannerLayoutHeight() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return BannerFragment.BANNER_TEXT_HEIGHT_TO_BANNER_LAYOUT_HEIGHT_PORTRAIT;
		} else {
			return BannerFragment.BANNER_TEXT_HEIGHT_TO_BANNER_LAYOUT_HEIGHT_LANDSCAPE;
		}
	}
}
