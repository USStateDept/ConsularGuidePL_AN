package com.agitive.usembassy.activities;

import java.util.Locale;

import com.agitive.usembassy.R;
import com.agitive.usembassy.adapters.WelcomePagerAdapter;
import com.agitive.usembassy.databases.DatabaseAssetsInitializer;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.layouts.ViewPagerParallax;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.viewpagerindicator.CirclePageIndicator;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class WelcomeActivity extends FragmentActivity {

	private static final String PRIVACY_STATEMENT_KEY = "privacyStatementAccepted";
	private static final String PRIVACY_STATEMENT_APP_VERSION_KEY = "privacyStatementAppVersion";
	private static final double EMBLEM_HEIGHT_TO_LAYOUT_HEIGHT_PORTRAIT = 0.16;
	private static final double EMBLEM_HEIGHT_TO_LAYOUT_HEIGHT_LANDSCAPE = 0.22;
	private static final double SPACE_ABOVE_IMAGE_HEIGHT_TO_LAYOUT_HEIGHT_PORTRAIT = 0.14;
	private static final double SPACE_ABOVE_IMAGE_HEIGHT_TO_LAYOUT_HEIGHT_LANDSCAPE = 0.15;
	private static final double TOOLBAR_HEIGHT_TO_LAYOUT_HEIGHT_PORTRAIT = 0.096;
	private static final double TOOLBAR_HEIGHT_TO_LAYOUT_HEIGHT_LANDSCAPE = 0.072;
	private static final double EMBLEM_HEIGHT_TO_TOOLBAR_HEIGHT = 1.25;
	private static final double EMBLEM_MARGIN_TOP_TO_TOOLBAR_HEIGHT = 0.17;
	private static final long ANIMATION_DURATION = 500;
	private static final String SAVED_INSTANCE_STATE_IS_ACCEPT_BUTTON_ACTIVE_KEY = "com.agitive.usembassy.activities.savedInstanceStateIsAcceptButtonActiveKey";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initDatabaseFromAssets();
		
		if (isPrivacyStatementsAccepted()) {
			Intent homeActivityIntent = new Intent(this, MainActivity.class);
			startActivity(homeActivityIntent);
			
			finish();
			return;
		}
		
		setOrientation();
		setLanguage();
		
		setContentView(R.layout.welcome_layout);
		
		setViewPagerParallax();
		setPageIndicator();
		setEmblemDimensions();
	}
	
	@Override
	public void onBackPressed() {
		return;
	}
	
	@Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
    	super.onSaveInstanceState(savedInstanceState);
    	
    	savedInstanceState.putBoolean(WelcomeActivity.SAVED_INSTANCE_STATE_IS_ACCEPT_BUTTON_ACTIVE_KEY, getIsAcceptButtonActive());
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		setIsAcceptButtonActive(savedInstanceState.getBoolean(WelcomeActivity.SAVED_INSTANCE_STATE_IS_ACCEPT_BUTTON_ACTIVE_KEY));
	}
	
	private void initDatabaseFromAssets() {
		new DatabaseAssetsInitializer(this.getApplicationContext());
	}
	
	private boolean getIsAcceptButtonActive() {
		ViewPagerParallax viewPagerParallax = (ViewPagerParallax) findViewById(R.id.welcome_layout_view_pager_parallax);
		if (viewPagerParallax == null) {
			return false;
		}
		
		return ((WelcomePagerAdapter)viewPagerParallax.getAdapter()).getIsAcceptButtonActive();
	}
	
	private void setIsAcceptButtonActive(boolean isActive) {
		if (!isActive) {
			return;
		}
	
		ViewPagerParallax viewPagerParallax = (ViewPagerParallax) findViewById(R.id.welcome_layout_view_pager_parallax);
		if (viewPagerParallax == null) {
			return;
		}
		
		((WelcomePagerAdapter)viewPagerParallax.getAdapter()).setIsAcceptButtonActive();
	}
	
	private void setOrientation() {
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			if (!isAboveOrEqualLargeScreen()) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} 
		}
	}
	
	private void setLanguage() {
		String language;
		
		if (isLanguageSet()) {
			language = getAppLanguage();
		} else {
			language = getDeviceLanguage();
		}
		
		if (language.toUpperCase().equals(Global.SHARED_PREFERENCES_LANGUAGE_POLISH)) {
			setLanguageToConfiguration(Global.SHARED_PREFERENCES_LANGUAGE_POLISH);
			saveLanguageInSharedPreferences(Global.SHARED_PREFERENCES_LANGUAGE_POLISH);
		} else {
			setLanguageToConfiguration(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
			saveLanguageInSharedPreferences(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
		}
	}
	
	private String getDeviceLanguage() {
		return Locale.getDefault().getLanguage();
	}
	
	private void saveLanguageInSharedPreferences(String language) {
		SharedPreferences settings = getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
		
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, language);
    	
    	editor.commit();
	}
	
	private boolean isLanguageSet() {
		SharedPreferences settings = getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
		
		return settings.contains(Global.SHARED_PREFERENCES_LANGUAGE_KEY);
	}
	
	private String getAppLanguage() {
		SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		return sharedPreferences.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
	}
	
	private void setLanguageToConfiguration(String language) {
    	DisplayMetrics metrics = getResources().getDisplayMetrics();
    	Configuration configuration = getResources().getConfiguration();
    	if (language.equals(Global.SHARED_PREFERENCES_LANGUAGE_POLISH)) {
    		configuration.locale = new Locale("pl");
    	} else if (language.equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
    		configuration.locale = new Locale(Locale.US.toString());
    	}
    	getResources().updateConfiguration(configuration, metrics);
    }
	
	private int getOrientation() {
		return getResources().getConfiguration().orientation;
	}
	
	private int getEmblemTopMargin() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return (int) (getDisplayHeight() * getSpaceAboveImageHeightToLayoutHeight() - 2.0 / 3.0 * getDisplayHeight() * getEmblemHeightToLayoutHeight());
		} else {
			return (int) (getDisplayHeight() * getSpaceAboveImageHeightToLayoutHeight() - 1.0 / 2.0 * getDisplayHeight() * getEmblemHeightToLayoutHeight());
		}
    }
	
	private double getSpaceAboveImageHeightToLayoutHeight() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return WelcomeActivity.SPACE_ABOVE_IMAGE_HEIGHT_TO_LAYOUT_HEIGHT_PORTRAIT;
		} else {
			return WelcomeActivity.SPACE_ABOVE_IMAGE_HEIGHT_TO_LAYOUT_HEIGHT_LANDSCAPE;
		}
	}
	
	private void setEmblemDimensions() {
		ImageView emblem = (ImageView) findViewById(R.id.welcome_layout_emblem);
		if (emblem == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) emblem.getLayoutParams();
		params.height = (int) (getDisplayHeight() * getEmblemHeightToLayoutHeight());
		params.width = (int) (getDisplayHeight() * getEmblemHeightToLayoutHeight());
		params.topMargin = getEmblemTopMargin();
		emblem.setLayoutParams(params);
	}
	
	private double getEmblemHeightToLayoutHeight() {
		if(getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return WelcomeActivity.EMBLEM_HEIGHT_TO_LAYOUT_HEIGHT_PORTRAIT;
		} else {
			return WelcomeActivity.EMBLEM_HEIGHT_TO_LAYOUT_HEIGHT_LANDSCAPE;
		}
	}
	
	private void setPageIndicator() {
		CirclePageIndicator pageIndicator = (CirclePageIndicator) findViewById(R.id.welcome_layout_page_indicator);
		if (pageIndicator == null) {
			return;
		}
		
		ViewPagerParallax viewPagerParallax = (ViewPagerParallax) findViewById(R.id.welcome_layout_view_pager_parallax);
		if (viewPagerParallax == null) {
			return;
		}
		
		pageIndicator.setViewPager(viewPagerParallax);
	}
	
	private void setViewPagerParallax() {
		ViewPagerParallax viewPagerParallax = (ViewPagerParallax) findViewById(R.id.welcome_layout_view_pager_parallax);
		if (viewPagerParallax == null) {
			return;
		}
		
		WelcomePagerAdapter welcomePagerAdapter = new WelcomePagerAdapter(this);
		viewPagerParallax.setAdapter(welcomePagerAdapter);
		
		viewPagerParallax.set_max_pages(2);
		viewPagerParallax.setBackgroundAsset(getWelcomePhotoId());
	}
	
	public boolean isPortraitOrientation() {
    	return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }
	
	public void animateEmblem() {
		ImageView emblem = (ImageView) findViewById(R.id.welcome_layout_emblem);
		if (emblem == null) {
			return;
		}
		
		int emblemEndHeight = getEmblemEndHeight();
		float emblemEndScale = (float) (1.0 * emblemEndHeight / emblem.getHeight());
		int emblemTranslationY = getEmblemTranslationY(emblemEndHeight);
		
		ObjectAnimator emblemScaleXAnimator = ObjectAnimator.ofFloat(emblem, "scaleX", emblemEndScale);
		ObjectAnimator emblemScaleYAnimator = ObjectAnimator.ofFloat(emblem, "scaleY", emblemEndScale);
		ObjectAnimator emblemTranslationYAnimator = ObjectAnimator.ofFloat(emblem, "translationY", emblemTranslationY);
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(emblemScaleXAnimator, emblemScaleYAnimator, emblemTranslationYAnimator);
		animatorSet.setDuration(WelcomeActivity.ANIMATION_DURATION);
		setAnimatorListener(animatorSet);
		animatorSet.start();
	}
	
	public void savePrivacyStatementAccept() {
		SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		
		editor.putBoolean(WelcomeActivity.PRIVACY_STATEMENT_KEY, true);
		
		String appVersion;
		try {
			appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Accept privacy statement version error");
			
			return;
		}
		
		editor.putString(WelcomeActivity.PRIVACY_STATEMENT_APP_VERSION_KEY, appVersion);
		editor.commit();
	}
	
	private int getEmblemTranslationY(int endHeight) {
		int toolbarHeight = (int) (getDisplayHeight() * getToolbarHeightToLayoutHeight());
		
		ImageView emblem = (ImageView) findViewById(R.id.welcome_layout_emblem);
		if (emblem == null) {
			return 0;
		}
		
		int translation = emblem.getTop(); 
		translation -= (int) (toolbarHeight * WelcomeActivity.EMBLEM_MARGIN_TOP_TO_TOOLBAR_HEIGHT);
		translation += ((1.0 * emblem.getHeight() - endHeight) / 2);
		
		return -translation;
	}
	
	private double getToolbarHeightToLayoutHeight() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return WelcomeActivity.TOOLBAR_HEIGHT_TO_LAYOUT_HEIGHT_PORTRAIT;
		} else {
			return WelcomeActivity.TOOLBAR_HEIGHT_TO_LAYOUT_HEIGHT_LANDSCAPE;
		}
	}
	
	private int getEmblemEndHeight() {
		int toolbarHeight = (int) (getDisplayHeight() * getToolbarHeightToLayoutHeight());
		int height = (int) (toolbarHeight * WelcomeActivity.EMBLEM_HEIGHT_TO_TOOLBAR_HEIGHT);
		
		return height;
	}
	
	private void setAnimatorListener(AnimatorSet animatorSet) {
		animatorSet.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				Intent MainActivityIntent = new Intent(WelcomeActivity.this, MainActivity.class);
				startActivity(MainActivityIntent);
				overridePendingTransition(R.anim.welcome_to_main_screen_animation_enter, R.anim.welcome_to_main_screen_animation_exit);
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationStart(Animator animation) {
			}
			
		});
	}
	
	private boolean isAboveOrEqualLargeScreen() {
    	if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==
    			Configuration.SCREENLAYOUT_SIZE_SMALL) {
    		return false;
    	}
    	
    	if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==
    			Configuration.SCREENLAYOUT_SIZE_NORMAL) {
    		return false;
    	}
    	
    	if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==
    			Configuration.SCREENLAYOUT_SIZE_LARGE) {
    		return true;
    	}
    	
    	if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==
    			Configuration.SCREENLAYOUT_SIZE_XLARGE) {
    		return true;
    	}
    	
    	return true;
    }
	
	private boolean isPrivacyStatementsAccepted() {
		SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		boolean isPrivacyStatementAccepted = sharedPreferences.getBoolean(WelcomeActivity.PRIVACY_STATEMENT_KEY, false);
		if (!isPrivacyStatementAccepted) {
			return false;
		}
		
		String privacyStatementAppVersion = sharedPreferences.getString(WelcomeActivity.PRIVACY_STATEMENT_APP_VERSION_KEY, "0.0");
		try {
			if (!getPackageManager().getPackageInfo(getPackageName(), 0).versionName.equals(privacyStatementAppVersion)) {
				return false;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Accept privacy statement version error");
			
			return false;
		}
		
		return true;
	}
	
	private int getWelcomePhotoIdScreenNormal() {
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		switch (displayMetrics.densityDpi) {
			case DisplayMetrics.DENSITY_MEDIUM:
				return R.raw.welcome_photo_normal_mdpi_portrait;
			case DisplayMetrics.DENSITY_HIGH:
				return R.raw.welcome_photo_normal_hdpi_portrait;
			case DisplayMetrics.DENSITY_XHIGH:
				return R.raw.welcome_photo_normal_xhdpi_portrait;
			default:
				return R.raw.welcome_photo_normal_hdpi_portrait;
		}
	}
	
	private int getWelcomePhotoIdScreenLargeMDPI() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return R.raw.welcome_photo_large_mdpi_portrait;
		} else {
			return R.raw.welcome_photo_large_mdpi_landscape;
		}
	}
	
	private int getWelcomePhotoIdScreenLargeHDPI() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return R.raw.welcome_photo_large_hdpi_portrait;
		} else {
			return R.raw.welcome_photo_large_hdpi_landscape;
		}
	}
	
	private int getWelcomePhotoIdScreenLargeXHDPI() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return R.raw.welcome_photo_large_xhdpi_portrait;
		} else {
			return R.raw.welcome_photo_large_xhdpi_landscape;
		}
	}
	
	private int getWelcomePhotoIdScreenLarge() {
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		switch (displayMetrics.densityDpi) {
			case DisplayMetrics.DENSITY_MEDIUM:
				return getWelcomePhotoIdScreenLargeMDPI();
			case DisplayMetrics.DENSITY_HIGH:
				return getWelcomePhotoIdScreenLargeHDPI();
			case DisplayMetrics.DENSITY_XHIGH:
				return getWelcomePhotoIdScreenLargeXHDPI();
			default:
				return getWelcomePhotoIdScreenLargeHDPI();
		}
	}
	
	private int getWelcomePhotoIdScreenXlargeMDPI() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return R.raw.welcome_photo_xlarge_mdpi_portrait;
		} else {
			return R.raw.welcome_photo_xlarge_mdpi_landscape;
		}
	}
	
	private int getWelcomePhotoIdScreenXlargeHDPI() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return R.raw.welcome_photo_xlarge_hdpi_portrait;
		} else {
			return R.raw.welcome_photo_xlarge_hdpi_landscape;
		}
	}
	
	private int getWelcomePhotoIdScreenXlargeXHDPI() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return R.raw.welcome_photo_xlarge_xhdpi_portrait;
		} else {
			return R.raw.welcome_photo_xlarge_xhdpi_landscape;
		}
	}
	
	private int getWelcomePhotoIdScreenXlarge() {
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		switch (displayMetrics.densityDpi) {
			case DisplayMetrics.DENSITY_MEDIUM:
				return getWelcomePhotoIdScreenXlargeMDPI();
			case DisplayMetrics.DENSITY_HIGH:
				return getWelcomePhotoIdScreenXlargeHDPI();
			case DisplayMetrics.DENSITY_XHIGH:
				return getWelcomePhotoIdScreenXlargeXHDPI();
			default:
				return getWelcomePhotoIdScreenXlargeHDPI();
		}
	}
	
	private int getWelcomePhotoId() {
		if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
			return getWelcomePhotoIdScreenNormal();
    	}
    	
    	if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
    		return getWelcomePhotoIdScreenNormal();
    	}
    	
    	if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
    		return getWelcomePhotoIdScreenLarge();
    	}
    	
    	if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
    		return getWelcomePhotoIdScreenXlarge();
    	}
    	
    	return getWelcomePhotoIdScreenLarge();
	}
	
	@SuppressWarnings("deprecation")
	private int getDisplayHeight() {
		Display display = getWindowManager().getDefaultDisplay();
		
		if (Build.VERSION.SDK_INT < 13) {
			return display.getHeight();
		} else {
			Point size = new Point();
			display.getSize(size);
			
			return size.y;
		}
	}
}
