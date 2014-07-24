package com.agitive.usembassy.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import com.agitive.usembassy.R;
import com.agitive.usembassy.adapters.ExpandableListAdapter;
import com.agitive.usembassy.databases.AllNewsLayout;
import com.agitive.usembassy.databases.ArticleLayout;
import com.agitive.usembassy.databases.CustomContentLayout;
import com.agitive.usembassy.databases.DatabaseReader;
import com.agitive.usembassy.databases.FAQLayout;
import com.agitive.usembassy.databases.FacebookLayout;
import com.agitive.usembassy.databases.FileManagerLayout;
import com.agitive.usembassy.databases.ListLayout;
import com.agitive.usembassy.databases.MainScreenLayout;
import com.agitive.usembassy.databases.MapLayout;
import com.agitive.usembassy.databases.MenuLayout;
import com.agitive.usembassy.databases.MenuLayoutItem;
import com.agitive.usembassy.databases.PassportTrackingLayout;
import com.agitive.usembassy.databases.StepsLayout;
import com.agitive.usembassy.databases.VideosLayout;
import com.agitive.usembassy.fragments.asyncTaskFragments.ApplicationContentDownloaderFragment;
import com.agitive.usembassy.fragments.asyncTaskFragments.BannerDownloaderFragment;
import com.agitive.usembassy.fragments.dialogFragments.ApplicationUpdateDialogFragment;
import com.agitive.usembassy.fragments.layoutFragments.AdditionalContentFragment;
import com.agitive.usembassy.fragments.layoutFragments.ArticleFragment;
import com.agitive.usembassy.fragments.layoutFragments.BannerFragment;
import com.agitive.usembassy.fragments.layoutFragments.CustomContentFragment;
import com.agitive.usembassy.fragments.layoutFragments.FAQFragment;
import com.agitive.usembassy.fragments.layoutFragments.FeedbackFragment;
import com.agitive.usembassy.fragments.layoutFragments.FileManagerFragment;
import com.agitive.usembassy.fragments.layoutFragments.ListFragment;
import com.agitive.usembassy.fragments.layoutFragments.MainScreenFragment;
import com.agitive.usembassy.fragments.layoutFragments.MapFragment;
import com.agitive.usembassy.fragments.layoutFragments.MenuFragment;
import com.agitive.usembassy.fragments.layoutFragments.NewsFragment;
import com.agitive.usembassy.fragments.layoutFragments.NewsRightColumnFragment;
import com.agitive.usembassy.fragments.layoutFragments.PassportTrackingFragment;
import com.agitive.usembassy.fragments.layoutFragments.StepsFragment;
import com.agitive.usembassy.fragments.layoutFragments.StepsViewPagerFragment;
import com.agitive.usembassy.fragments.layoutFragments.VideosFragment;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.interfaces.AdditionalContentInterface;
import com.agitive.usembassy.interfaces.FragmentAboveInterface;
import com.agitive.usembassy.interfaces.LayoutFragmentInterface;
import com.agitive.usembassy.interfaces.LayoutTypeInterface;
import com.agitive.usembassy.interfaces.NewsRightColumnInterface;
import com.agitive.usembassy.layouts.CustomTextView;
import com.agitive.usembassy.layouts.SquareLayout;
import com.agitive.usembassy.network.PushNotificationRegisterAsyncTask;
import com.agitive.usembassy.objects.Banner;
import com.agitive.usembassy.services.GCMIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	public static final String SHARED_PREFERENCES_NAME = "com.agitive.usembassy";
	public static final int HANDLER_MESSAGE_NORMAL_RUN = 0;
	public static final int HANDLER_MESSAGE_NORMAL_RUN_FOR_STEP_0 = 1;
	public static final double LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH = 2.0 / 3.0;
	
	private static final long MENU_ANIMATION_DURATION = 300;
	private static final int CONTAINER_BELOW_NUMBER = 0;
	private static final int CONTAINER_ABOVE_NUMBER = 1;
	private static final int CONTAINER_RIGHT_COLUMN_NUMBER = 2;
	private static final double MENU_SHADOW_TO_DISPLAY_WIDTH = 0.042;
	private static final String CONTAINER_BELOW_TAG = "com.agitive.usembassy.activities.MainActivity.containerBelow";
	private static final String CONTAINER_ABOVE_TAG = "com.agitive.usembassy.activities.MainActivity.containerAbove";
	private static final double TOOLBAR_HEIGHT_TO_WINDOW_HEIGHT_PORTRAIT = 0.096;
	private static final double TOOLBAR_HEIGHT_TO_WINDOW_HEIGHT_LANDSCAPE = 0.072;
	private static final double EMBLEM_MARGIN_TOP_TO_TOOLBAR_HEIGHT = 0.17;
	private static final double EMBLEM_HEIGHT_TO_TOOLBAR_HEIGHT = 1.25;
	private static final double EMBLEM_PART_OUTSIDE_TO_EMBLEM_HEIGHT = 0.35;
	private static final double MENU_WIDTH_TO_WINDOW_WIDTH_PORTRAIT = 0.75;
	private static final double MENU_WIDTH_TO_WINDOW_WIDTH_LANDSCAPE = 1 - (2.0 / 3.0);
	private static final String CONTAINER_NEWS_RIGHT_COLUMN_TAG = "com.agitive.usembassy.activities.MainActivity.containerRightColumn";
	private static final String SAVED_INSTANCE_STATE_CONTAINER_BELOW_VISIBILITY_KEY = "com.agitive.usembassy.activities.MainActivity.containerBelowVisibility";
	private static final String SAVED_INSTANCE_STATE_CONTAINER_ABOVE_VISIBILITY_KEY = "com.agitive.usembassy.activities.MainActivity.containerAboveVisibility";
	private static final String SAVED_INSTANCE_STATE_CURRENT_LAYOUT_ID_KEY = "com.agitive.usembassy.activities.MainActivity.currentLayoutId";
	private static final String SAVED_INSTANCE_STATE_CURRENT_LAYOUT_IN_VISIBLE_PANEL_ID_KEY = "com.agitive.usembassy.activities.MainActivity.currentLayoutInVisiblePanelId";
	private static final String SAVED_INSTANCE_STATE_SLIDING_MENU_STATE_KEY = "com.agitive.usembassy.activities.MainActivity.slidingMenuState";
	private static final String SAVED_INSTANCE_STATE_TOOLBAR_OPEN_STATE_KEY = "com.agitive.usembassy.activities.MainActivity.toolbarOpenState";
	private static final String SAVED_INSTANCE_STATE_TOOLBAR_OPEN_LANGUAGE_BAR_STATE_KEY = "com.agitive.usembassy.activities.MainActivity.openToolbarLanguageBarState";
	private static final String CONTAINER_FEEDBACK_TAG = "com.agitive.usembassy.activities.MainActivity.containerFeedback";
	private static final String SAVED_INSTANCE_STATE_CONTAINER_FEEDBACK_VISIBILITY_KEY = "com.agitive.usembassy.activities.MainActivity.containerFeedbackVisibility";
	private static final String SAVED_INSTANCE_STATE_FEEDBACK_BAR_VISIBILITY_KEY = "com.agitive.usembassy.activities.MainActivity.containerFeedbackBarVisibility";
	private static final String CONTAINER_ADDITIONAL_CONTENT_TAG = "com.agitive.usembassy.activities.MainActivity.containerAdditionalContent";
	private static final int CONTAINER_FEEDBACK_NUMBER = 3;
	private static final String CONTAINER_BANNER_TAG = "com.agitive.usembassy.activities.MainActivity.containerBannerTag";
	private static final int CONTAINER_BANNER_NUMBER = 4;
	private static final String SAVED_INSTANCE_STATE_BANNER_TITLE_EN_KEY = "com.agitive.usembassy.activities.MainActivity.savedInstanceStateBannerTitleEnKey";
	private static final String SAVED_INSTANCE_STATE_BANNER_TITLE_PL_KEY = "com.agitive.usembassy.activities.MainActivity.savedInstanceStateBannerTitlePlKey";
	private static final String SAVED_INSTANCE_STATE_BANNER_TEXT_EN_KEY = "com.agitive.usembassy.activities.MainActivity.savedInstanceStateBannerTextEnKey";
	private static final String SAVED_INSTANCE_STATE_BANNER_TEXT_PL_KEY = "com.agitive.usembassy.activities.MainActivity.savedInstanceStateBannerTextPLKey";
	private static final String SAVED_INSTANCE_STATE_BANNER_TYPE_KEY = "com.agitive.usembassy.activities.MainActivity.savedInstanceStateBannerTypeKey";
	private static final double BANNER_LAYOUT_HEIGHT_TO_CONTENT_HEIGHT = 1.0 / 3.0;
	private static final int MENU_GENERAL_INFORMATION_POSITION = 1;
	private static final String SHARED_PREFERENCES_LAST_CONTENT_UPDATE_TIME_KEY = "com.agitive.usembassy.activities.MainActivity.sharedPreferencesLastContentUpdateTimeKey";
	private static final String SHARED_PREFERENCES_LAST_CONTENT_UPDATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";
	private static final int HOURS_24_IN_MILISECONDS = 86400000;
	private static final String FACEBOOK_URL = "https://pl-pl.facebook.com/USEmbassyWarsaw";
	private static final String FIRST_RUN_KEY = "com.agitive.usembassy.activities.MainActivity.firstRun";
	private static final int FIRST_RUN_OPEN_MENU_POST_DELAY = 600;
	
	private LayoutTypeInterface currentLayout;
	private LayoutTypeInterface currentLayoutInVisiblePanel;
	private Banner banner;
	private SlidingMenu slidingMenu;
	private Handler waitingHandler;
	private int emblemPartOutsideSize;
	private int news0BackgroundId;
	private int news1BackgroundId;
	private int news2BackgroundId;
	
	public static float dpToPixel(int dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = 1.0f * dp * (metrics.densityDpi / 160f);
		
		return px;
	}
	
	public static float pxToSp(int px, Context context) {
	    float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
	    float sp = px/scaledDensity;
	    
	    return sp;
	}
	
	public void showBanner(Banner banner) {
		this.banner = banner;
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		if (this.currentLayout.getLayoutType() == LayoutTypeInterface.MAIN_SCREEN_LAYOUT) {
			MainScreenFragment mainScreenFragment = (MainScreenFragment) fragmentManager.findFragmentByTag(MainActivity.CONTAINER_BELOW_TAG);
			mainScreenFragment.showBanner(banner);
		} else if (getOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
			removeFragment(MainActivity.CONTAINER_BANNER_NUMBER);
			addBannerFragmentInRightColumn(banner);
		}
	}
	
	public void closeBanner() {
		removeFragment(MainActivity.CONTAINER_BANNER_NUMBER);
		eraseBanner();
	}
	
	public void openLayout(int id, int fromLayoutId) {
		DatabaseReader databaseReader = new DatabaseReader(this);
		LayoutTypeInterface layout = databaseReader.getLayout(id);
		if (layout == null) {
			return;
		}
		
		if (isFeedbackVisible()) {
			closeFeedbackLayout();
		}
		
		switch (layout.getLayoutType()) {
			case LayoutTypeInterface.MENU_LAYOUT:
				openMenuLayout((MenuLayout) layout, fromLayoutId);
				break;
			case LayoutTypeInterface.NEWS_LAYOUT:
				openNewsLayout((AllNewsLayout) layout, fromLayoutId);
				break;
			case LayoutTypeInterface.VIDEOS_LAYOUT:
				openVideosLayout((VideosLayout) layout, fromLayoutId);
				break;
			case LayoutTypeInterface.FILE_MANAGER_LAYOUT:
				openFileManagerLayout((FileManagerLayout) layout, fromLayoutId);
				break;
			case LayoutTypeInterface.TEXT_LAYOUT:
				openCustomContentLayout((CustomContentLayout) layout, fromLayoutId);
				break;
			case LayoutTypeInterface.LIST_LAYOUT:
				openListLayout((ListLayout) layout, fromLayoutId);
				break;
			case LayoutTypeInterface.CONTACT_LAYOUT:
				openMapLayout((MapLayout) layout, fromLayoutId);
				break;
			case LayoutTypeInterface.STEPS_LAYOUT:
				openStepsLayout((StepsLayout) layout, fromLayoutId);
				break;
			case LayoutTypeInterface.FAQ_LAYOUT:
				openFAQLayout((FAQLayout) layout, fromLayoutId);
				break;
			case LayoutTypeInterface.FACEBOOK_LAYOUT:
				openFacebookLayout((FacebookLayout) layout);
				break;
			case LayoutTypeInterface.MAIN_SCREEN_LAYOUT:
				openMainScreenLayout((MainScreenLayout) layout);
				break;
			case LayoutTypeInterface.PASSPORT_TRACKING_LAYOUT:
				openPassportTrackingLayout((PassportTrackingLayout) layout, fromLayoutId);
				break;
		}
	}
	
	public void removeApplicationContentDownloaderFragment() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment applicationContentDownloaderFragment = fragmentManager.findFragmentByTag(ApplicationContentDownloaderFragment.TAG);
		if (applicationContentDownloaderFragment != null) {
			try {
				fragmentManager.beginTransaction().remove(applicationContentDownloaderFragment).commit();
			} catch(IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void removeBannerDownloaderFragment() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment bannerDownloaderFragment = fragmentManager.findFragmentByTag(BannerDownloaderFragment.TAG);
		if (bannerDownloaderFragment != null) {
			try {
				fragmentManager.beginTransaction().remove(bannerDownloaderFragment).commit();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void saveContentDownloadingTime() {
		SharedPreferences settings = getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		
		Date dateNow = new Date();
		SimpleDateFormat simpleFormatDate = new SimpleDateFormat(MainActivity.SHARED_PREFERENCES_LAST_CONTENT_UPDATE_TIME_FORMAT);
		editor.putString(MainActivity.SHARED_PREFERENCES_LAST_CONTENT_UPDATE_TIME_KEY, simpleFormatDate.format(dateNow));
		
		editor.commit();
	}
	
	private void openPassportTrackingLayout(PassportTrackingLayout layout, int fromLayoutId) {
		this.currentLayout = layout;
		this.currentLayoutInVisiblePanel = layout;
		hideToolbarOpen();
		setToolbarOpenLanguageBarVisibility(View.VISIBLE);
		setFeedbackBarVisibilityInToolbarOpen(View.VISIBLE);
		setLeftColumnDimensionForNewLayout(layout);
		setRightColumnVisibilityForNewLayout(layout);
		changeBannerPosition(layout.getId());
		addFragmentForPassportTracking(layout);
		createHandlerUIFlushForPassportTrackingFragment(layout, fromLayoutId);
	}
	
	private void createHandlerUIFlushForPassportTrackingFragment(final PassportTrackingLayout layout, final int fromLayoutId) {
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				ObjectAnimator animation = createAnimationForPassportTrackingLayout(layout, fromLayoutId);
				animation.start();	
			}
			
		});
	}
	
	private ObjectAnimator createAnimationForPassportTrackingLayout(final PassportTrackingLayout layout, int fromLayoutId) {
		float startPosition = getStartPositionForAboveContainer();
		float endPosition = 0;
		long duration = getAnimationDurationForLayoutByFromLayoutId(fromLayoutId);
			
		FrameLayout fragmentAboveContainer = (FrameLayout) findViewById(R.id.main_layout_fragment_above_container);
		if (fragmentAboveContainer == null) {
			return null;
		}
			
		ObjectAnimator fragmentAnimator = ObjectAnimator.ofFloat(fragmentAboveContainer, "translationX", startPosition, endPosition);
		fragmentAnimator.setDuration(duration);
		fragmentAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(Animator animation) {
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				
				if (canToggleMenu(layout.getId())) {
					slidingMenu.setOnClosedListener(new OnClosedListener() {
						@Override
						public void onClosed() {
							setContentInFragmentAbove();
							slidingMenu.setOnClosedListener(null);
						}
									
					});
					slidingMenu.toggle();
				} else {
					setContentInFragmentAbove();
				}
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationStart(Animator animation) {
			}
				
		});
			
		return fragmentAnimator;
	}
	
	private void setContentInFragmentAbove() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentAboveInterface fragment = (FragmentAboveInterface) fragmentManager.findFragmentByTag(MainActivity.CONTAINER_ABOVE_TAG);
		if (fragment == null) {
			return;
		}
		fragment.setContent();
	}
	
	private void addFragmentForPassportTracking(PassportTrackingLayout layout) {
		removeFragment(MainActivity.CONTAINER_ABOVE_NUMBER);
		removeFragment(MainActivity.CONTAINER_RIGHT_COLUMN_NUMBER);

		showContainer(MainActivity.CONTAINER_ABOVE_NUMBER);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		PassportTrackingFragment passportTrackingFragment = new PassportTrackingFragment();
		passportTrackingFragment.setArguments(createArgumentsForPassportTrackingFragment(layout));
		
		try {
			fragmentManager.beginTransaction().add(R.id.main_layout_fragment_above_container, passportTrackingFragment, MainActivity.CONTAINER_ABOVE_TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private Bundle createArgumentsForPassportTrackingFragment(PassportTrackingLayout layout) {
		Bundle arguments = new Bundle();
		arguments.putInt(PassportTrackingFragment.LAYOUT_ID_KEY, layout.getId());
		
		return arguments;
	}
	
	private int getStartPositionForAboveContainer() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return getDisplayWidth();
		} else {
			return (int) (getDisplayWidth() * MainActivity.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH); 
		}
	}
	
	private ObjectAnimator createAnimationForCustomContentLayout(final CustomContentLayout layout, int fromLayoutId) {
		float startPosition = getStartPositionForAboveContainer();
		float endPosition = 0;
		long duration = getAnimationDurationForLayoutByFromLayoutId(fromLayoutId);
		
		FrameLayout fragmentAboveContainer = (FrameLayout) findViewById(R.id.main_layout_fragment_above_container);
		if (fragmentAboveContainer == null) {
			return null;
		}
		
		ObjectAnimator fragmentAnimator = ObjectAnimator.ofFloat(fragmentAboveContainer, "translationX", startPosition, endPosition);
		fragmentAnimator.setDuration(duration);
		fragmentAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (canToggleMenu(layout.getId())) {
					slidingMenu.setOnClosedListener(new OnClosedListener() {

						@Override
						public void onClosed() {
							setContentInFragmentAbove();
							setContentInFragmentAdditional();
							slidingMenu.setOnClosedListener(null);
						}
								
					});
					slidingMenu.toggle();
				} else {
					setContentInFragmentAbove();
					setContentInFragmentAdditional();
				}
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationStart(Animator animation) {
			}
			
		});
		
		return fragmentAnimator;
	}
	
	public void eraseBanner() {
		this.banner = null;
	}
	
	private void hideContainer(int containerNumber) {
		if ((getOrientation() == Configuration.ORIENTATION_PORTRAIT) && 
				(containerNumber == MainActivity.CONTAINER_RIGHT_COLUMN_NUMBER)) {
			return;
		}
		
		FrameLayout fragmentLayout = null;
		
		if (containerNumber == MainActivity.CONTAINER_ABOVE_NUMBER) {
			fragmentLayout = (FrameLayout) findViewById(R.id.main_layout_fragment_above_container);
		} else if (containerNumber == MainActivity.CONTAINER_BELOW_NUMBER) {
			fragmentLayout = (FrameLayout) findViewById(R.id.main_layout_fragment_below_container);
		} else if (containerNumber == MainActivity.CONTAINER_RIGHT_COLUMN_NUMBER) {
			fragmentLayout = (FrameLayout) findViewById(R.id.main_layout_right_column_fragment_above_layout);
		}
		
		if (fragmentLayout == null) {
			return;
		}
		fragmentLayout.setVisibility(FrameLayout.INVISIBLE);
	}
	
	private ObjectAnimator createAdditionalContentAnimation(AdditionalContentInterface layout, int fromLayoutId) {
		if (!hasCustomContentLayoutAdditionalContent(layout)) {
			removeFragment(MainActivity.CONTAINER_RIGHT_COLUMN_NUMBER);
			
			return null;
		}
		
		RelativeLayout rightColumnAboveLayout = (RelativeLayout) findViewById(R.id.main_layout_right_column_above_layout);
		if (rightColumnAboveLayout == null) {
			return null;
		}
		
		int startPosition = getDisplayWidth() - ((int) (getDisplayWidth() * MainActivity.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH));
		int endPosition = 0;
		long duration = getAnimationDurationForLayoutByFromLayoutId(fromLayoutId);
		
		addAdditionalContentFragment(layout, false);
		
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rightColumnAboveLayout, "translationX", startPosition, endPosition);
		objectAnimator.setDuration(duration);
		
		return objectAnimator;
	}
	
	private AnimatorSet createAnimatorSet(ObjectAnimator animation1, ObjectAnimator animation2) {
		AnimatorSet animatorSet = new AnimatorSet();
		
		if (animation1 != null &&
				animation2 != null) {
			animatorSet.playTogether(animation1, animation2);
		} else {
			if (animation1 != null) {
				animatorSet.play(animation1);
			}
			
			if (animation2 != null) {
				animatorSet.play(animation2);
			}
		}
		
		return animatorSet;
	}
	
	private void addBannerFragmentInRightColumn(Banner banner) {
		if (banner == null) {
			return;
		}
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		BannerFragment bannerFragment = new BannerFragment();
		bannerFragment.setArguments(createArgumentsForBanner(banner));
		
		try {
			fragmentManager.beginTransaction().add(R.id.main_layout_banner_fragment_container, bannerFragment, MainActivity.CONTAINER_BANNER_TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		
	}
	
	private Bundle createArgumentsForBanner(Banner banner) {
		Bundle arguments = new Bundle();
		arguments.putInt(BannerFragment.BANNER_TYPE_KEY, banner.getType());
		arguments.putString(BannerFragment.BANNER_TITLE_EN_KEY, banner.getTitleEn());
		arguments.putString(BannerFragment.BANNER_TITLE_PL_KEY, banner.getTitlePl());
		arguments.putString(BannerFragment.BANNER_TEXT_EN_KEY, banner.getContentEn());
		arguments.putString(BannerFragment.BANNER_TEXT_PL_KEY, banner.getContentPl());
		
		return arguments;
	}
	
	private void addAdditionalContentFragment(AdditionalContentInterface layout, boolean isRestored) {
		removeFragment(MainActivity.CONTAINER_RIGHT_COLUMN_NUMBER);
		
		AdditionalContentFragment additionalContentFragment = new AdditionalContentFragment();
		additionalContentFragment.setArguments(createArgumentsForAdditionalContent(layout, isRestored));
		
		showContainer(MainActivity.CONTAINER_RIGHT_COLUMN_NUMBER);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		try {
			fragmentManager.beginTransaction().add(R.id.main_layout_right_column_fragment_above_layout, additionalContentFragment, MainActivity.CONTAINER_ADDITIONAL_CONTENT_TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private void setContentInFragmentAdditional() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentAboveInterface fragment = (FragmentAboveInterface) fragmentManager.findFragmentByTag(MainActivity.CONTAINER_ADDITIONAL_CONTENT_TAG);
		if (fragment == null) {
			return;
		}
		fragment.setContent();
	}
	
	private Bundle createArgumentsForAdditionalContent(AdditionalContentInterface layout, boolean isRestored) {
		Bundle arguments = new Bundle();
		arguments.putString(AdditionalContentFragment.CONTENT_EN_KEY, layout.getAdditionalEn());
		arguments.putString(AdditionalContentFragment.CONTENT_PL_KEY, layout.getAdditionalPl());
		arguments.putInt(AdditionalContentFragment.LAYOUT_ID_KEY, layout.getId());
		arguments.putBoolean(AdditionalContentFragment.IS_RESTORED_KEY, isRestored);
		
		return arguments;
	}
	
	private void setToolbarOpenLanguageBarVisibility(int visibility) {
		RelativeLayout languageBar = (RelativeLayout) findViewById(R.id.main_layout_toolbar_open_language_bar);
		if (languageBar == null) {
			return;
		}
		
		languageBar.setVisibility(visibility);
	}
	
	private void openCustomContentLayout(CustomContentLayout layout, int fromLayoutId) {		
		this.currentLayout = layout;
		this.currentLayoutInVisiblePanel = layout;
		hideToolbarOpen();
		setToolbarOpenLanguageBarVisibility(View.VISIBLE);
		setFeedbackBarVisibilityInToolbarOpen(View.VISIBLE);
		setLeftColumnDimensionForNewLayout(layout);
		setRightColumnVisibilityForNewLayout(layout);
		changeBannerPosition(layout.getId());
		addFragmentForCustomContent(layout);
		createHandlerUIFlushForCustomContentFragment(layout, fromLayoutId);
	}
	
	private void createHandlerUIFlushForCustomContentFragment(final CustomContentLayout layout, final int fromLayoutId) {
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				ObjectAnimator animation = createAnimationForCustomContentLayout(layout, fromLayoutId);
				ObjectAnimator additionalContentAnimation = createAdditionalContentAnimation(layout, fromLayoutId);
				AnimatorSet animatorSet;
				animatorSet = createAnimatorSet(animation, additionalContentAnimation);		
				animatorSet.start();	
			}
			
		});
	}
	
	public void updateDatabaseAndGoToMainScreen() {
		if (!isOnline()) {
			showNoInternetToast();
			return;
		}
		
		openLayout(0, 0);
		
		try {
			this.getSupportFragmentManager().beginTransaction().add(new ApplicationContentDownloaderFragment(), ApplicationContentDownloaderFragment.TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		
	}
	
	public void openArticleLayout(final ArticleLayout layout, final Bundle arguments) {
		this.currentLayout = layout;
		this.currentLayoutInVisiblePanel = layout;
		hideToolbarOpen();
		setToolbarOpenLanguageBarVisibility(View.INVISIBLE);
		setFeedbackBarVisibilityInToolbarOpen(View.VISIBLE);
		setLeftColumnDimensionForNewLayout(layout);
		setRightColumnVisibilityForNewLayout(layout);
		setFragmentAbovePositionForArticle();
		changeBannerPosition(layout.getId());
		addFragmentForArticle(arguments);
	}
	
	private void setFragmentAbovePositionForArticle() {
		FrameLayout fragmentAboveContainer = (FrameLayout) findViewById(R.id.main_layout_fragment_above_container);
		if (fragmentAboveContainer == null) {
			return;
		}
		
		int endPosition = 0;
		
		ObjectAnimator fragmentAnimator = ObjectAnimator.ofFloat(fragmentAboveContainer, "translationX", endPosition);
		fragmentAnimator.setDuration(0);
		fragmentAnimator.start();
	}
	
	private void addFragmentForArticle(Bundle arguments) {
		removeFragment(MainActivity.CONTAINER_ABOVE_NUMBER);
		removeFragment(MainActivity.CONTAINER_RIGHT_COLUMN_NUMBER);
		
		ArticleFragment articleFragment = new ArticleFragment();
		articleFragment.setArguments(arguments);
		
		showContainer(MainActivity.CONTAINER_ABOVE_NUMBER);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		try {
			fragmentManager.beginTransaction().add(R.id.main_layout_fragment_above_container, articleFragment, MainActivity.CONTAINER_ABOVE_TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private void updateDatabaseAndBannerAfter24Hours() {
		if (!isOnline()) {
			return;
		}
		
		if (isContentUpToDate()) {
			return;
		}
		
		try {
			this.getSupportFragmentManager().beginTransaction().add(new ApplicationContentDownloaderFragment(), ApplicationContentDownloaderFragment.TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		downloadAndSetBannerAfter24Hours();
	}
	
	private boolean isContentUpToDate() {
		SharedPreferences settings = getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
		if (!settings.contains(MainActivity.SHARED_PREFERENCES_LAST_CONTENT_UPDATE_TIME_KEY)) {
			return false;
		}
		
		Date lastContentUpdateTime = getLastContentUpdateTimeFromSharedPreferences();
		if (lastContentUpdateTime == null) {
			return false;
		}
		
		Date hours24Ago = new Date(System.currentTimeMillis() - MainActivity.HOURS_24_IN_MILISECONDS);
		
		return (!lastContentUpdateTime.before(hours24Ago));
	}
	
	private Date getLastContentUpdateTimeFromSharedPreferences() {
		SharedPreferences settings = getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
		String dateString = settings.getString(MainActivity.SHARED_PREFERENCES_LAST_CONTENT_UPDATE_TIME_KEY, "");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SHARED_PREFERENCES_LAST_CONTENT_UPDATE_TIME_FORMAT);
		try {
			return simpleDateFormat.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Content last time update format error");
			
			return null;
		}
	}
	
	private boolean isOnline() {
	    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	 
	    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
	    return (networkInfo != null && networkInfo.isConnectedOrConnecting());
	}
	
	private void showNoInternetToast() {
		Toast toast = Toast.makeText(getApplicationContext(), R.string.main_activity_no_internet, Toast.LENGTH_LONG);
		toast.show();
	}
	
	private boolean hasCustomContentLayoutAdditionalContent(AdditionalContentInterface layout) {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return false;
		}
		
		if ((layout.getAdditionalEn() == null || layout.getAdditionalEn().equals("")) &&
				(layout.getAdditionalPl() == null || layout.getAdditionalPl().equals(""))) {
			
			return false;
		}
		
		return true;
	}
	
	public void updateRSSAndTweetInNewsRightColumn() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		NewsRightColumnFragment newsRightColumnFragment = (NewsRightColumnFragment) fragmentManager.findFragmentByTag(MainActivity.CONTAINER_NEWS_RIGHT_COLUMN_TAG);
		if (newsRightColumnFragment != null) {
			newsRightColumnFragment.updateNewsAndTweets();
		}
	}
	
	public int getToolbarOpenHeight() {
		RelativeLayout toolbarOpen = (RelativeLayout) findViewById(R.id.main_layout_toolbar_open);
		if (toolbarOpen == null) {
			return 0;
		}
		
		return toolbarOpen.getHeight();
	}
	
	public void closeFeedbackLayout() {
		setFeedbackContainerVisibility(FrameLayout.INVISIBLE);
		hideToolbarOpen();
		setToolbarOpenLanguageBarVisibilityByLayoutType(this.currentLayout);
		setFeedbackBarVisibilityInToolbarOpen(View.VISIBLE);
		removeFragment(MainActivity.CONTAINER_FEEDBACK_NUMBER);
	}
	
	public void runHandler(int messageType) {
		if (this.waitingHandler == null) {
			return;
		}
		
		this.waitingHandler.sendEmptyMessage(messageType);
		this.waitingHandler = null;
	}
	
	public int getEmblemPartOutsideSize() {
		return this.emblemPartOutsideSize;
	}
	
	public void openAddtionalContent(int id) {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return;
		}
		
		DatabaseReader databaseReader = new DatabaseReader(this);
		AdditionalContentInterface layout = (AdditionalContentInterface) databaseReader.getLayout(id);
		
		if (layout == null) {
			return;
		}
		
		if (!hasCustomContentLayoutAdditionalContent(layout)) {
			return;
		}
		
		addAdditionalContentFragment(layout, false);
	}
	
	@Override
	public void onBackPressed() {
		if (isMenuVisibleAsShortcut()) {
			this.slidingMenu.toggle();
			
			return;
		}
		
		if (isFeedbackVisible()) {
			closeFeedbackLayout();
			return;
		}
		
		switch (this.currentLayout.getLayoutType()) {
			case LayoutTypeInterface.MENU_LAYOUT:
				backFromMenuLayout();
				break;
			case LayoutTypeInterface.NEWS_LAYOUT:
				backFromNewsLayout();
				break;
			case LayoutTypeInterface.VIDEOS_LAYOUT:
				backFromVideosLayout();
				break;
			case LayoutTypeInterface.FILE_MANAGER_LAYOUT:
				backFromFileManagerLayout();
				break;
			case LayoutTypeInterface.TEXT_LAYOUT:
				backFromCustomContentLayout();
				break;
			case LayoutTypeInterface.LIST_LAYOUT:
				backFromListLayout();
				break;
			case LayoutTypeInterface.CONTACT_LAYOUT:
				backFromMapLayout();
				break;
			case LayoutTypeInterface.STEPS_LAYOUT:
				backFromStepsLayout();
				break;
			case LayoutTypeInterface.ARTICLE_LAYOUT:
				backFromArticleLayout();
				break;
			case LayoutTypeInterface.FAQ_LAYOUT:
				backFromFAQLayout();
				break;
			case LayoutTypeInterface.FACEBOOK_LAYOUT:
				break;
			case LayoutTypeInterface.MAIN_SCREEN_LAYOUT:
				backFromMainScreenLayout();
				break;
			case LayoutTypeInterface.PASSPORT_TRACKING_LAYOUT:
				backFromPassportTrackingLayout();
				break;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setLanguageToConfiguration(getAppLanguage());
		setOrientation();
		
        setContentView(R.layout.main_layout);
        
        checkGooglePlayServices();
        removeRightColumnFragment();
        setLeftColumnDimensions();
        setRightColumnDimensions();
        setSlidingMenu();
        setEmblemDimensions();
        randNewsBackground();
        setToolbar();
        setToolbarOpen();
        setMainScreenAndRightColumn(savedInstanceState);
        setMenu();
        updateDatabase(savedInstanceState);
        downloadAndSetBanner(savedInstanceState);
        registerInGoogleCloudMessaging(savedInstanceState);
        openMenuFirstRunWithListener();
	}
	
	@Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
    	super.onSaveInstanceState(savedInstanceState);
    	
    	savedInstanceState.putInt(MainActivity.SAVED_INSTANCE_STATE_CONTAINER_BELOW_VISIBILITY_KEY, getContainerBelowVisibility());
    	savedInstanceState.putInt(MainActivity.SAVED_INSTANCE_STATE_CONTAINER_ABOVE_VISIBILITY_KEY, getContainerAboveVisibility());
    	savedInstanceState.putInt(MainActivity.SAVED_INSTANCE_STATE_CURRENT_LAYOUT_ID_KEY, this.currentLayout.getId());
    	savedInstanceState.putInt(MainActivity.SAVED_INSTANCE_STATE_CURRENT_LAYOUT_IN_VISIBLE_PANEL_ID_KEY, this.currentLayoutInVisiblePanel.getId());
    	savedInstanceState.putBoolean(MainActivity.SAVED_INSTANCE_STATE_SLIDING_MENU_STATE_KEY, getSlidingMenuOpen());
    	savedInstanceState.putBoolean(MainActivity.SAVED_INSTANCE_STATE_TOOLBAR_OPEN_STATE_KEY, getToolbarOpenState());
    	savedInstanceState.putBoolean(MainActivity.SAVED_INSTANCE_STATE_TOOLBAR_OPEN_LANGUAGE_BAR_STATE_KEY, getToolbarOpenLanguageBarState());
    	savedInstanceState.putBoolean(MainActivity.SAVED_INSTANCE_STATE_CONTAINER_FEEDBACK_VISIBILITY_KEY, isFeedbackVisible());
    	savedInstanceState.putInt(MainActivity.SAVED_INSTANCE_STATE_FEEDBACK_BAR_VISIBILITY_KEY, getFeedbackBarVisibilityInToolbarOpen());
    	saveBannerInSavedInstanceState(savedInstanceState);
    }
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		setContainerBelowVisibility(savedInstanceState.getInt(MainActivity.SAVED_INSTANCE_STATE_CONTAINER_BELOW_VISIBILITY_KEY));
		setContainerAboveVisibility(savedInstanceState.getInt(MainActivity.SAVED_INSTANCE_STATE_CONTAINER_ABOVE_VISIBILITY_KEY));
		createCurrentLayout(savedInstanceState.getInt(SAVED_INSTANCE_STATE_CURRENT_LAYOUT_ID_KEY));
		createCurrentInVisiblePanelLayout(savedInstanceState.getInt(SAVED_INSTANCE_STATE_CURRENT_LAYOUT_IN_VISIBLE_PANEL_ID_KEY));
		setSlidingMenu(savedInstanceState.getBoolean(MainActivity.SAVED_INSTANCE_STATE_SLIDING_MENU_STATE_KEY));
		setToolbarOpenState(savedInstanceState.getBoolean(MainActivity.SAVED_INSTANCE_STATE_TOOLBAR_OPEN_STATE_KEY));
		setToolbarOpenLanguageBarState(savedInstanceState.getBoolean(MainActivity.SAVED_INSTANCE_STATE_TOOLBAR_OPEN_LANGUAGE_BAR_STATE_KEY));
		setLeftColumnDimensionForNewLayout(savedInstanceState.getInt(SAVED_INSTANCE_STATE_CURRENT_LAYOUT_IN_VISIBLE_PANEL_ID_KEY));
		setRightColumnDimensionForNewLayout(savedInstanceState.getInt(SAVED_INSTANCE_STATE_CURRENT_LAYOUT_IN_VISIBLE_PANEL_ID_KEY));
		setContainerFeedbackVisibility(savedInstanceState.getBoolean(MainActivity.SAVED_INSTANCE_STATE_CONTAINER_FEEDBACK_VISIBILITY_KEY));
		setFeedbackBarVisibilityInToolbarOpen(savedInstanceState.getInt(MainActivity.SAVED_INSTANCE_STATE_FEEDBACK_BAR_VISIBILITY_KEY));
		restoreRightColumn(savedInstanceState.getInt(SAVED_INSTANCE_STATE_CURRENT_LAYOUT_ID_KEY));
		restoreBanner(savedInstanceState);
		restoreBannerInRightColumn(savedInstanceState);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		if (intent.hasExtra(GCMIntentService.IS_ALERT_NOTIFICATION)) {
    		Banner banner = createBannerFronIntent(intent);
    		showBanner(banner);
    	}
		
		if (intent.hasExtra(GCMIntentService.IS_CONTENT_UPDATE)) {
			showApplicationUpdateDialog();
    	}
	}
	
	private void showContainer(int containerNumber) {
		if ((getOrientation() == Configuration.ORIENTATION_PORTRAIT) && 
				(containerNumber == MainActivity.CONTAINER_RIGHT_COLUMN_NUMBER)) {
			return;
		}
		
		FrameLayout fragmentLayout = null;
		
		if (containerNumber == MainActivity.CONTAINER_ABOVE_NUMBER) {
			fragmentLayout = (FrameLayout) findViewById(R.id.main_layout_fragment_above_container);
		} else if (containerNumber == MainActivity.CONTAINER_BELOW_NUMBER) {
			fragmentLayout = (FrameLayout) findViewById(R.id.main_layout_fragment_below_container);
		} else if (containerNumber == MainActivity.CONTAINER_RIGHT_COLUMN_NUMBER) {
			fragmentLayout = (FrameLayout) findViewById(R.id.main_layout_right_column_fragment_above_layout);
		}
		
		if (fragmentLayout == null) {
			return;
		}
		
		fragmentLayout.setVisibility(FrameLayout.VISIBLE);
	}
	
	private Banner createBannerFronIntent(Intent intent) {
		Banner banner = new Banner(this);
		banner.setTitleEn(intent.getStringExtra(GCMIntentService.BANNER_TITLE_EN));
		banner.setTitlePl(intent.getStringExtra(GCMIntentService.BANNER_TITLE_PL));
		banner.setContentEn(intent.getStringExtra(GCMIntentService.BANNER_CONTENT_EN));
		banner.setContentPl(intent.getStringExtra(GCMIntentService.BANNER_CONTENT_PL));
		banner.setType(intent.getStringExtra(GCMIntentService.BANNER_TYPE));
		
		return banner;
	}
	
	private void backFromPassportTrackingLayout() {
		openLayout(1, this.currentLayout.getId());
	}
	
	private void restoreBannerInRightColumn(Bundle savedInstanceState) {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return;
		}
		
		if (savedInstanceState == null) {
			return;
		}
		
		if (this.currentLayout.getLayoutType() == LayoutTypeInterface.MAIN_SCREEN_LAYOUT) {
			return;
		}
		addBannerFragmentInRightColumn(banner);
	}
	
	private void checkGooglePlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode == ConnectionResult.SUCCESS) {
			return;
		}
		GooglePlayServicesUtil.getErrorDialog(resultCode, this, 9000).show();
	}
	
	private void restoreBanner(Bundle savedInstanceState) {
		if (!savedInstanceState.containsKey(MainActivity.SAVED_INSTANCE_STATE_BANNER_TEXT_EN_KEY)) {
			this.banner = null;
			return;
		}
		
		Banner banner = new Banner(this);
		banner.setContentEn(savedInstanceState.getString(MainActivity.SAVED_INSTANCE_STATE_BANNER_TEXT_EN_KEY));
		banner.setContentPl(savedInstanceState.getString(MainActivity.SAVED_INSTANCE_STATE_BANNER_TEXT_PL_KEY));
		banner.setTitleEn(savedInstanceState.getString(MainActivity.SAVED_INSTANCE_STATE_BANNER_TITLE_EN_KEY));
		banner.setTitlePl(savedInstanceState.getString(MainActivity.SAVED_INSTANCE_STATE_BANNER_TITLE_PL_KEY));
		banner.setType(savedInstanceState.getInt(MainActivity.SAVED_INSTANCE_STATE_BANNER_TYPE_KEY));
		this.banner = banner;
	}
	
	private void saveBannerInSavedInstanceState(Bundle savedInstanceState) {
		if (this.banner == null) {
			savedInstanceState.remove(MainActivity.SAVED_INSTANCE_STATE_BANNER_TEXT_EN_KEY);
	    	savedInstanceState.remove(MainActivity.SAVED_INSTANCE_STATE_BANNER_TEXT_PL_KEY);
	    	savedInstanceState.remove(MainActivity.SAVED_INSTANCE_STATE_BANNER_TITLE_EN_KEY);
	    	savedInstanceState.remove(MainActivity.SAVED_INSTANCE_STATE_BANNER_TITLE_PL_KEY);
	    	savedInstanceState.remove(MainActivity.SAVED_INSTANCE_STATE_BANNER_TYPE_KEY);
		} else {
			savedInstanceState.putString(MainActivity.SAVED_INSTANCE_STATE_BANNER_TEXT_EN_KEY, this.banner.getContentEn());
	    	savedInstanceState.putString(MainActivity.SAVED_INSTANCE_STATE_BANNER_TEXT_PL_KEY, this.banner.getContentPl());
	    	savedInstanceState.putString(MainActivity.SAVED_INSTANCE_STATE_BANNER_TITLE_EN_KEY, this.banner.getTitleEn());
	    	savedInstanceState.putString(MainActivity.SAVED_INSTANCE_STATE_BANNER_TITLE_PL_KEY, this.banner.getTitlePl());
	    	savedInstanceState.putInt(MainActivity.SAVED_INSTANCE_STATE_BANNER_TYPE_KEY, this.banner.getType());
		}
	}
	
	private void restoreRightColumn(int id) {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return;
		}
		
		DatabaseReader databaseReader = new DatabaseReader(this);
		LayoutTypeInterface layout = databaseReader.getLayout(id);
		
		if (layout == null) {
			return;
		}
		
		if (!layout.hasAdditionalContent()) {
			return;
		}
		
		if (layout.getLayoutType() == LayoutTypeInterface.STEPS_LAYOUT) {
			return;
		}
		
		removeFragment(MainActivity.CONTAINER_RIGHT_COLUMN_NUMBER);
		addAdditionalContentFragment((AdditionalContentInterface) layout, true);
	}
	
	private void setContainerFeedbackVisibility(boolean isOpen) {
		if (isOpen) {
			setFeedbackContainerVisibility(View.VISIBLE);
		} else {
			setFeedbackContainerVisibility(View.INVISIBLE);
		}
	}
	
	private void setLeftColumnDimensionForNewLayout(int id) {
		DatabaseReader databaseReader = new DatabaseReader(this);
		LayoutTypeInterface layout = databaseReader.getLayout(id);
		if (layout == null) {
			return;
		}
		
		setLeftColumnDimensionForNewLayout(layout);
	}
	
	private void setRightColumnDimensionForNewLayout(int id) {
		DatabaseReader databaseReader = new DatabaseReader(this);
		LayoutTypeInterface layout = databaseReader.getLayout(id);
		if (layout == null) {
			return;
		}
		
		setRightColumnVisibilityForNewLayout(layout);
	}
	
	private void changeBannerPositionPortrait(int layoutId) {
		if (layoutId != DatabaseReader.MAIN_SCREEN_LAYOUT_ID) {
			return;
		}
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		MainScreenFragment mainScreenFragment = (MainScreenFragment) fragmentManager.findFragmentByTag(MainActivity.CONTAINER_BELOW_TAG);
		if (mainScreenFragment == null) {
			return;
		}
		mainScreenFragment.showBanner(this.banner);
	}
	
	private void changeBannerPositionLandscape(int layoutId) {
		if (layoutId == DatabaseReader.MAIN_SCREEN_LAYOUT_ID) {
			removeFragment(MainActivity.CONTAINER_BANNER_NUMBER);
			
			FragmentManager fragmentManager = getSupportFragmentManager();
			MainScreenFragment mainScreenFragment = (MainScreenFragment) fragmentManager.findFragmentByTag(MainActivity.CONTAINER_BELOW_TAG);
			if (mainScreenFragment == null) {
				return;
			}
			mainScreenFragment.showBanner(this.banner);
		} else {
			if (isBannerInRightColumnVisible()) {
				return;
			}
			
			addBannerFragmentInRightColumn(this.banner);
		}
	}
	
	private void changeBannerPosition(int layoutId) {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			changeBannerPositionPortrait(layoutId);
		} else {
			changeBannerPositionLandscape(layoutId);
		}
	}
	
	private boolean isBannerInRightColumnVisible() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentByTag(MainActivity.CONTAINER_BANNER_TAG);
		
		return (fragment != null);
	}
	
	private void setToolbarOpenLanguageBarState(boolean open) {
		RelativeLayout toolbarOpenLanguageBar = (RelativeLayout) findViewById(R.id.main_layout_toolbar_open_language_bar);
		if (toolbarOpenLanguageBar == null) {
			return;
		}
		
		if (open) {
			toolbarOpenLanguageBar.setVisibility(RelativeLayout.VISIBLE);
		} else {
			toolbarOpenLanguageBar.setVisibility(RelativeLayout.INVISIBLE);
		}
	}
	
	private void openMenu() {
		this.slidingMenu.showMenu();
	}
	
	private void openMenuFirstRunWithListener() {
		if (isFirstRun()) {
			RelativeLayout rootView = (RelativeLayout) findViewById(R.id.main_layout_root_view);
			ViewTreeObserver rootViewObserver = rootView.getViewTreeObserver();
			rootViewObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

				private boolean ready = false;
				
				@Override
				public void onGlobalLayout() {
					if (this.ready) {
						return;
					}
					this.ready = true;
					
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							saveFirstRunInSharedPreferences();
							expandGeneralInformation();
							openMenu();
						}
						
					}, MainActivity.FIRST_RUN_OPEN_MENU_POST_DELAY);	
				}
				
			});
		}
	}
	
	private boolean isFirstRun() {
		SharedPreferences settings = getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
		return (!settings.contains(MainActivity.FIRST_RUN_KEY));
	}
	
	private void saveFirstRunInSharedPreferences() {
		SharedPreferences settings = getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(MainActivity.FIRST_RUN_KEY, false);
		
		editor.commit();
	}
	
	private boolean getToolbarOpenLanguageBarState() {
		RelativeLayout toolbarOpenLanguageBar = (RelativeLayout) findViewById(R.id.main_layout_toolbar_open_language_bar);
		if (toolbarOpenLanguageBar == null) {
			return false;
		}
		
		return (toolbarOpenLanguageBar.getVisibility() == RelativeLayout.VISIBLE);
	}
	
	private void setToolbarOpenState(boolean open) {
		RelativeLayout toolbarOpen = (RelativeLayout) findViewById(R.id.main_layout_toolbar_open);
		if (toolbarOpen == null) {
			return;
		}
		
		ImageView toolbarIcon = (ImageView) findViewById(R.id.main_layout_toolbar_icon);
		if (toolbarIcon == null) {
			return;
		}
		
		if (open) {
			toolbarOpen.setVisibility(RelativeLayout.VISIBLE);
			toolbarIcon.setBackgroundResource(R.color.toolbar_open);
		} else {
			toolbarOpen.setVisibility(RelativeLayout.INVISIBLE);
			toolbarIcon.setBackgroundResource(R.color.main_theme);
		}
	}
	
	private boolean getToolbarOpenState() {
		RelativeLayout toolbarOpen = (RelativeLayout) findViewById(R.id.main_layout_toolbar_open);
		if (toolbarOpen == null) {
			return false;
		}
		
		return (toolbarOpen.getVisibility() == RelativeLayout.VISIBLE);
	}
	
	private void removeRightColumnFragment() {
		if (getOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
			return;
		}
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		Fragment fragment = fragmentManager.findFragmentByTag(MainActivity.CONTAINER_NEWS_RIGHT_COLUMN_TAG);
		if (fragment != null) {
			try {
				fragmentManager.beginTransaction().remove(fragment).commit();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
		
		fragment = fragmentManager.findFragmentByTag(MainActivity.CONTAINER_ADDITIONAL_CONTENT_TAG);
		if (fragment != null) {
			try {
				fragmentManager.beginTransaction().remove(fragment).commit();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
		
		fragment = fragmentManager.findFragmentByTag(MainActivity.CONTAINER_BANNER_TAG);
		if (fragment != null) {
			try {
				fragmentManager.beginTransaction().remove(fragment).commit();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setMenuDimensions() {
		final CustomTextView toolbarOpenTextHeightMeter = (CustomTextView) findViewById(R.id.main_layout_toolbar_open_text_height_meter);
		if (toolbarOpenTextHeightMeter == null) {
			return;
		}
		
		ViewTreeObserver toolbarOpenTextHeightMeterObserver = toolbarOpenTextHeightMeter.getViewTreeObserver();
		toolbarOpenTextHeightMeterObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			private boolean ready = false;
			
			@Override
			public void onGlobalLayout() {
				if (this.ready) {
					return;
				}
				this.ready = true;
				
				RelativeLayout menuLanguageBar = (RelativeLayout) findViewById(R.id.menu_layout_language_bar);
				if (menuLanguageBar == null) {
					return;
				}
				
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) menuLanguageBar.getLayoutParams();
				params.height = (int) (getEmblemPartOutsideSize() +
						getApplicationContext().getResources().getDimension(R.dimen.margin_small) +
						getApplicationContext().getResources().getDimension(R.dimen.margin_medium) + 
						toolbarOpenTextHeightMeter.getHeight());
				menuLanguageBar.setLayoutParams(params);
			}
			
		});
	}
	
	private void createCurrentInVisiblePanelLayout(int id) {
		DatabaseReader databaseReader = new DatabaseReader(this);
		LayoutTypeInterface layout = databaseReader.getLayout(id);
		if (layout == null) {
			return;
		}
		
		this.currentLayoutInVisiblePanel = layout;
	}
	
	private void setSlidingMenu(boolean open) {
		if (open) {
			this.slidingMenu.showMenu();	
		} else {
			this.slidingMenu.showContent();
		}
	}
	
	private boolean getSlidingMenuOpen() {
		return this.slidingMenu.isMenuShowing();
	}
	
	private void createCurrentLayout(int id) {
		DatabaseReader databaseReader = new DatabaseReader(this);
		LayoutTypeInterface layout = databaseReader.getLayout(id);
		if (layout == null) {
			return;
		}
		
		this.currentLayout = layout;
	}
	
	private void setContainerAboveVisibility(int visibility) {
		FrameLayout fragmentAboveLayout = (FrameLayout) findViewById(R.id.main_layout_fragment_above_container);
		if (fragmentAboveLayout == null) {
			return;
		}
		
		fragmentAboveLayout.setVisibility(visibility);
	}
	
	private int getContainerAboveVisibility() {
		FrameLayout fragmentAboveLayout = (FrameLayout) findViewById(R.id.main_layout_fragment_above_container);
		if (fragmentAboveLayout == null) {
			return FrameLayout.INVISIBLE;
		}
		
		return fragmentAboveLayout.getVisibility();
	}
	
	private void setContainerBelowVisibility(int visibility) {
		FrameLayout fragmentBelowLayout = (FrameLayout) findViewById(R.id.main_layout_fragment_below_container);
		if (fragmentBelowLayout == null) {
			return;
		}
		
		fragmentBelowLayout.setVisibility(visibility);
	}
	
	private int getContainerBelowVisibility() {
		FrameLayout fragmentBelowLayout = (FrameLayout) findViewById(R.id.main_layout_fragment_below_container);
		if (fragmentBelowLayout == null) {
			return FrameLayout.INVISIBLE;
		}
		
		return fragmentBelowLayout.getVisibility();
	}
	
	private int getNewsBackgroundIdExcept(int news0BackgroundId, int news1BackgroundId) {
		Random random = new Random();
		
		int newsBackgroundId = random.nextInt(Global.BACKGROUNDS_NUMBERS);
		while (newsBackgroundId == news0BackgroundId ||
				newsBackgroundId == news1BackgroundId) {
			newsBackgroundId = random.nextInt(Global.BACKGROUNDS_NUMBERS);
		}
		
		return newsBackgroundId;
	}
	
	private void downloadAndSetBanner(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			return;
		}
		
		if (!isOnline()) {
			return;
		}
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		BannerDownloaderFragment bannerDownloaderFragment = new BannerDownloaderFragment();
		
		try {
			fragmentManager.beginTransaction().add(bannerDownloaderFragment, BannerDownloaderFragment.TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private void downloadAndSetBannerAfter24Hours() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		BannerDownloaderFragment bannerDownloaderFragment = new BannerDownloaderFragment();
		
		try {
			fragmentManager.beginTransaction().add(bannerDownloaderFragment, BannerDownloaderFragment.TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private void setMainScreenAndRightColumn(Bundle savedInstanceState) {
		setMainScreenFragment(this.news0BackgroundId, this.news1BackgroundId, this.news2BackgroundId, savedInstanceState);
        setRightColumnFragment(this.news0BackgroundId, this.news1BackgroundId);
	}
	
	private void setRightColumnFragment(int news0BackgroundId, int news1BackgroundId) {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return;
		}
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		NewsRightColumnFragment rightColumnFragment = new NewsRightColumnFragment();
		rightColumnFragment.setArguments(createArgumentsForRightColumnFragment(news0BackgroundId, news1BackgroundId));
		
		try {
			fragmentManager.beginTransaction().add(R.id.main_layout_right_column_fragment_below_layout, rightColumnFragment, MainActivity.CONTAINER_NEWS_RIGHT_COLUMN_TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private void registerInGoogleCloudMessaging(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			return;
		}
		
		if (!isOnline()) {
			return;
		}
		
		PushNotificationRegisterAsyncTask pushNotificationRegisterAsyncTask = new PushNotificationRegisterAsyncTask(getApplicationContext());
		pushNotificationRegisterAsyncTask.execute();
	}
	
	private Bundle createArgumentsForRightColumnFragment(int news0BackgroundId, int news1BackgroundId) {
		Bundle arguments = new Bundle();
		arguments.putInt(NewsRightColumnFragment.NEWS_0_BACKGROUND_ID_KEY, news0BackgroundId);
		arguments.putInt(NewsRightColumnFragment.NEWS_1_BACKGROUND_ID_KEY, news1BackgroundId);
		
		return arguments;
	}
	
	private void expandGeneralInformation() {
		ExpandableListView menuList = (ExpandableListView) findViewById(R.id.menu_layout_menu_list);
		if (menuList == null) {
			return;
		}
		
		menuList.expandGroup(MainActivity.MENU_GENERAL_INFORMATION_POSITION);
	}
	
	private void setMenu() {
		setMenuDimensions();
		setMenuItems();
		
		setOnGroupExpandListenerInMenu();
		setOnChildClickListenerInMenu();
		setOnCloseListener();
		
		setLanguageButtons();
	}
	
	private void setRightColumnDimensions() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return;
		}
		
		setRightColumnBelowLayoutDimension();
		setRightColumnAboveLayoutDimension();
		setRightColumnBannerFragmentContainerDimensions();
	}
	
	private void setRightColumnBannerFragmentContainerDimensions() {
		final RelativeLayout contentLayout = (RelativeLayout) findViewById(R.id.main_layout_content_layout);
		if (contentLayout == null) {
			return;
		}
		
		ViewTreeObserver contentLayoutObserver = contentLayout.getViewTreeObserver();
		contentLayoutObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			private boolean ready = false;
			
			@Override
			public void onGlobalLayout() {
				
				if (this.ready) {
					return;
				}
				this.ready = true;
				
				FrameLayout bannerFragmentContainer = (FrameLayout) findViewById(R.id.main_layout_banner_fragment_container);
				if (bannerFragmentContainer == null) {
					return;
				}
				
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bannerFragmentContainer.getLayoutParams();
				params.width = getDisplayWidth() - (int) (getDisplayWidth() * MainActivity.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH);
				params.height = (int) (contentLayout.getHeight() * MainActivity.BANNER_LAYOUT_HEIGHT_TO_CONTENT_HEIGHT);
				bannerFragmentContainer.setLayoutParams(params);	
			}
			
		});
	}
	
	private void setRightColumnBelowLayoutDimension() {
		FrameLayout rightColumnFragmentLayout = (FrameLayout) findViewById(R.id.main_layout_right_column_fragment_below_layout);
		if (rightColumnFragmentLayout == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rightColumnFragmentLayout.getLayoutParams(); 
		params.width = getDisplayWidth() - (int) (getDisplayWidth() * MainActivity.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH);
		rightColumnFragmentLayout.setLayoutParams(params);
	}
	
	private void setRightColumnAboveLayoutDimension() {
		RelativeLayout rightColumnAboveLayout = (RelativeLayout) findViewById(R.id.main_layout_right_column_above_layout);
		if (rightColumnAboveLayout == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rightColumnAboveLayout.getLayoutParams(); 
		params.width = getDisplayWidth() - (int) (getDisplayWidth() * MainActivity.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH);
		rightColumnAboveLayout.setLayoutParams(params);
	}
	
	private void setLeftColumnDimensions() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return;
		}
		
		setFragmentBelowContainerDimension();
		setFragmentAboveContainerDimension();
	}
	
	private boolean isFeedbackVisible() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentByTag(MainActivity.CONTAINER_FEEDBACK_TAG);
		
		return (fragment != null);
	}
	
	private void setFragmentBelowContainerDimension() {
		FrameLayout fragmentBelowLayout = (FrameLayout) findViewById(R.id.main_layout_fragment_below_container);
		if (fragmentBelowLayout == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fragmentBelowLayout.getLayoutParams(); 
		params.width = (int) (getDisplayWidth() * MainActivity.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH);
		fragmentBelowLayout.setLayoutParams(params);
	}
	
	private void setFragmentBelowContainerDimensionForNewLayout(LayoutTypeInterface layoutType) {
		FrameLayout fragmentBelowLayout = (FrameLayout) findViewById(R.id.main_layout_fragment_below_container);
		if (fragmentBelowLayout == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fragmentBelowLayout.getLayoutParams();
		
		if (layoutType.getLayoutType() == LayoutTypeInterface.NEWS_LAYOUT) {
			params.width = getDisplayWidth();
		} else {
			params.width = (int) (getDisplayWidth() * MainActivity.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH);
		}
		
		fragmentBelowLayout.setLayoutParams(params);
	}
	
	private void setFragmentAboveContainerDimension() {
		FrameLayout fragmentAboveContainer = (FrameLayout) findViewById(R.id.main_layout_fragment_above_container);
		if (fragmentAboveContainer == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fragmentAboveContainer.getLayoutParams(); 
		params.width = (int) (getDisplayWidth() * MainActivity.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH);
		fragmentAboveContainer.setLayoutParams(params);
	}
	
	private void setFragmentAboveContainerDimensionForNewLayout(LayoutTypeInterface layoutType) {
		FrameLayout fragmentAboveContainer = (FrameLayout) findViewById(R.id.main_layout_fragment_above_container);
		if (fragmentAboveContainer == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fragmentAboveContainer.getLayoutParams();
		
		if (layoutType.getLayoutType() == LayoutTypeInterface.NEWS_LAYOUT) {
			params.width = getDisplayWidth();
		} else {
			params.width = (int) (getDisplayWidth() * MainActivity.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH);
		}

		fragmentAboveContainer.setLayoutParams(params);
	}
	
	private void setToolbar() {
		setIconMenu();
		setToolbarIcon();
	}
	
	private void setToolbarIcon() {
		final ImageView toolbarIcon = (ImageView) findViewById(R.id.main_layout_toolbar_icon);
		if (toolbarIcon == null) {
			return;
		}
		
		toolbarIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RelativeLayout toolbarOpen = (RelativeLayout) findViewById(R.id.main_layout_toolbar_open);
				if (toolbarOpen == null) {
					return;
				}
				
				if (toolbarOpen.getVisibility() == RelativeLayout.VISIBLE) {
					toolbarOpen.setVisibility(RelativeLayout.INVISIBLE);
					toolbarIcon.setBackgroundResource(R.color.main_theme);
				} else {
					toolbarOpen.setVisibility(RelativeLayout.VISIBLE);
					toolbarIcon.setBackgroundResource(R.color.toolbar_open);
				}
			}
			
		});
	}
	
	private void showApplicationUpdateDialog() {
		ApplicationUpdateDialogFragment applicationUpdateDialogFragment = new ApplicationUpdateDialogFragment();
		applicationUpdateDialogFragment.setCancelable(false);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		try {
			fragmentManager.beginTransaction().add(applicationUpdateDialogFragment, ApplicationUpdateDialogFragment.TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private void setToolbarOpenHeight() {
		final CustomTextView toolbarOpenTextHeightMeter = (CustomTextView) findViewById(R.id.main_layout_toolbar_open_text_height_meter);
		if (toolbarOpenTextHeightMeter == null) {
			return;
		}
		
		ViewTreeObserver toolbarOpenTextHeightMeterObserver = toolbarOpenTextHeightMeter.getViewTreeObserver();
		toolbarOpenTextHeightMeterObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			private boolean ready = false;
			
			@Override
			public void onGlobalLayout() {
				if (this.ready) {
					return;
				}
				this.ready = true;
				
				RelativeLayout toolbarOpen = (RelativeLayout) findViewById(R.id.main_layout_toolbar_open);
				if (toolbarOpen == null) {
					return;
				}
				
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toolbarOpen.getLayoutParams();
				params.height = (int) (getEmblemPartOutsideSize() +
						getApplicationContext().getResources().getDimension(R.dimen.margin_small) +
						getApplicationContext().getResources().getDimension(R.dimen.margin_medium) + 
						toolbarOpenTextHeightMeter.getHeight());
				toolbarOpen.setLayoutParams(params);
			}
			
		});
	}
	
	private void setToolbarOpenLanguageButtonsOnClicksListeners() {
		SquareLayout languageEnLayout = (SquareLayout) findViewById(R.id.main_layout_language_en_layout);
		if (languageEnLayout == null) {
			return;
		}
		
		languageEnLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideToolbarOpen();
				changeLanguage(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
			}
			
		});
		
		SquareLayout languagePlLayout = (SquareLayout) findViewById(R.id.main_layout_language_pl_layout);
		if (languagePlLayout == null) {
			return;
		}
		
		languagePlLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideToolbarOpen();
				changeLanguage(Global.SHARED_PREFERENCES_LANGUAGE_POLISH);
			}
			
		});
	}
	
	private void setToolbarOpenLanguageButtonsColors() {
		CustomTextView languageEnText = (CustomTextView) findViewById(R.id.main_layout_language_en_text);
		if (languageEnText == null) {
			return;
		}
		
		CustomTextView languagePlText = (CustomTextView) findViewById(R.id.main_layout_language_pl_text);
		if (languagePlText == null) {
			return;
		}
		
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			languageEnText.setTextColor(getApplicationContext().getResources().getColor(R.color.language_active_text));
			languagePlText.setTextColor(getApplicationContext().getResources().getColor(R.color.language_not_active_text));
		} else {
			languageEnText.setTextColor(getApplicationContext().getResources().getColor(R.color.language_not_active_text));
			languagePlText.setTextColor(getApplicationContext().getResources().getColor(R.color.language_active_text));
		}
	}
	
	private void setToolbarOpenLanguageButtons() {
		setToolbarOpenLanguageButtonsColors();
		setToolbarOpenLanguageButtonsOnClicksListeners();
	}
	
	private void hideToolbarOpen() {
		RelativeLayout toolbarOpen = (RelativeLayout) findViewById(R.id.main_layout_toolbar_open);
		if (toolbarOpen == null) {
			return;
		}
		
		toolbarOpen.setVisibility(RelativeLayout.INVISIBLE);
		
		ImageView toolbarIcon = (ImageView) findViewById(R.id.main_layout_toolbar_icon);
		if (toolbarIcon == null) {
			return;
		}
		
		toolbarIcon.setBackgroundResource(R.color.main_theme);
	}
	
	private void setToolbarOpen() {
		setToolbarOpenHeight();
		setToolbarOpenLanguageButtons();
		setFeedbackTextOnClickListener();
		setFeedbackIconOnClickListener();
	}
	
	private void setFeedbackTextOnClickListener() {
		CustomTextView feedbackText = (CustomTextView) findViewById(R.id.main_layout_feedback_text);
		if (feedbackText == null) {
			return;
		}
		
		feedbackText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openFeedbackLayout();
			}
			
		});
	}
	
	private void setFeedbackIconOnClickListener() {
		ImageView feedbackIcon = (ImageView) findViewById(R.id.main_layout_feedback_icon);
		if (feedbackIcon == null) {
			return;
		}
		
		feedbackIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openFeedbackLayout();
			}
			
		});
	}
	
	private void openFeedbackLayout() {
		hideToolbarOpen();
		setToolbarOpenLanguageBarVisibility(View.VISIBLE);
		setFeedbackBarVisibilityInToolbarOpen(View.INVISIBLE);
		addFeedbackFragment();
		setFeedbackContainerVisibility(FrameLayout.VISIBLE);
	}
	
	private void setFeedbackBarVisibilityInToolbarOpen(int visibility) {
		CustomTextView feedbackText = (CustomTextView) findViewById(R.id.main_layout_feedback_text);
		if (feedbackText == null) {
			return;
		}
		
		ImageView feedbackIcon = (ImageView) findViewById(R.id.main_layout_feedback_icon);
		if (feedbackIcon == null) {
			return;
		}
		
		feedbackText.setVisibility(visibility);
		feedbackIcon.setVisibility(visibility);
	}
	
	private int getFeedbackBarVisibilityInToolbarOpen() {
		ImageView feedbackIcon = (ImageView) findViewById(R.id.main_layout_feedback_icon);
		if (feedbackIcon == null) {
			return ImageView.INVISIBLE;
		}
		
		return feedbackIcon.getVisibility();
	}
	
	private void addFeedbackFragment() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		FeedbackFragment feedbackFragment = new FeedbackFragment();
		feedbackFragment.setArguments(createArgumentsForFeedbackFragment());
		
		try {
			fragmentTransaction.add(R.id.main_layout_fragment_feedback_container, feedbackFragment, MainActivity.CONTAINER_FEEDBACK_TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private Bundle createArgumentsForFeedbackFragment() {
		Bundle arguments = new Bundle();
		arguments.putInt(FeedbackFragment.LAYOUT_ID_KEY, this.currentLayout.getId());
		
		return arguments;
	}
	
	private void setFeedbackContainerVisibility(int visibility) {
		FrameLayout fragmentFeedbackLayout = (FrameLayout) findViewById(R.id.main_layout_fragment_feedback_container);
		if (fragmentFeedbackLayout == null) {
			return;
		}
		
		fragmentFeedbackLayout.setVisibility(visibility);
	}
	
	private double getToolbarHeightToWindowHeight() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return MainActivity.TOOLBAR_HEIGHT_TO_WINDOW_HEIGHT_PORTRAIT;
		} else {
			return MainActivity.TOOLBAR_HEIGHT_TO_WINDOW_HEIGHT_LANDSCAPE;
		}
	}
	
	private void setEmblemDimensions() {
		int toolbarHeight = (int) (getDisplayHeight() * getToolbarHeightToWindowHeight());
		ImageView emblem = (ImageView) findViewById(R.id.main_layout_emblem);
		if (emblem == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) emblem.getLayoutParams();
		params.width = (int) (toolbarHeight * MainActivity.EMBLEM_HEIGHT_TO_TOOLBAR_HEIGHT);
		params.height = (int) (toolbarHeight * MainActivity.EMBLEM_HEIGHT_TO_TOOLBAR_HEIGHT);
		params.topMargin = (int) (toolbarHeight * MainActivity.EMBLEM_MARGIN_TOP_TO_TOOLBAR_HEIGHT);
		
		this.emblemPartOutsideSize = (int) (params.height * MainActivity.EMBLEM_PART_OUTSIDE_TO_EMBLEM_HEIGHT); 
		
		emblem.setLayoutParams(params);
	}
	
	private boolean isMenuVisibleAsShortcut() {
		return ((this.currentLayout.getId() == DatabaseReader.MAIN_SCREEN_LAYOUT_ID ||
				this.currentLayout.getId() > 5 ||
				this.currentLayout.getId() == DatabaseReader.ARTICLE_LAYOUT_ID ||
				this.currentLayout.getId() < -5) &&
				this.slidingMenu.isMenuShowing());
	}
	
	private void backFromMainScreenLayout() {
		Intent homeIntent = new Intent();
    	homeIntent.setAction(Intent.ACTION_MAIN);
    	homeIntent.addCategory(Intent.CATEGORY_HOME);
    	startActivity(homeIntent);
	}
	
	private void backFromFAQLayout() {
		openLayout(this.currentLayout.getParentId(), this.currentLayout.getId());
	}
	
	private void backFromArticleLayout() {
		openLayout(this.currentLayout.getParentId(), this.currentLayout.getId());
	}
	
	private void backFromStepsLayout() {
		openLayout(this.currentLayout.getParentId(), this.currentLayout.getId());
	}
	
	private void backFromMapLayout() {
		openLayout(this.currentLayout.getParentId(), this.currentLayout.getId());
	}
	
	private void backFromListLayout() {
		openLayout(this.currentLayout.getParentId(), this.currentLayout.getId());
	}
	
	private void backFromNewsLayout() {
		openLayout(this.currentLayout.getParentId(), this.currentLayout.getId());
	}
	
	private void backFromVideosLayout() {
		openLayout(this.currentLayout.getParentId(), this.currentLayout.getId());
	}
	
	private void backFromFileManagerLayout() {
		openLayout(this.currentLayout.getParentId(), this.currentLayout.getId());
	}
	
	private void backFromCustomContentLayout() {	
		openLayout(this.currentLayout.getParentId(), this.currentLayout.getId());
	}
	
	private void saveLanguageInSharedPreferences(String language) {
		SharedPreferences settings = getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
		if (settings.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH).equals(language)) {
    		return;
    	}
		
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, language);
    	
    	editor.commit();
	}
	
	private int getOrientation() {
		return getResources().getConfiguration().orientation;
	}
	
	private void changeLanguage(String language) {
		saveLanguageInSharedPreferences(language);
		setLanguageToConfiguration(language);
    	changeLanguageInMainActivity();
    	
    	FragmentManager fragmentManager = getSupportFragmentManager();
		
		LayoutFragmentInterface currentFragment = null;
		currentFragment = (LayoutFragmentInterface) fragmentManager.findFragmentByTag(MainActivity.CONTAINER_BELOW_TAG);
		if (currentFragment != null) {
			currentFragment.changeLanguage();
		}
	
		if (this.currentLayout.getLayoutType() != LayoutTypeInterface.ARTICLE_LAYOUT) {
			currentFragment = (LayoutFragmentInterface) fragmentManager.findFragmentByTag(MainActivity.CONTAINER_ABOVE_TAG);
			if (currentFragment != null) {
				currentFragment.changeLanguage();
			}
		}
		
		NewsRightColumnInterface rightColumnFragment = (NewsRightColumnInterface) fragmentManager.findFragmentByTag(MainActivity.CONTAINER_NEWS_RIGHT_COLUMN_TAG);
		if (rightColumnFragment != null) {
			rightColumnFragment.changeLanguage();
		}
		
		FeedbackFragment feedbackFragment = (FeedbackFragment) fragmentManager.findFragmentByTag(MainActivity.CONTAINER_FEEDBACK_TAG);
		if (feedbackFragment != null) {
			feedbackFragment.changeLanguage();
		}
		
		AdditionalContentFragment additionalContentFragment = (AdditionalContentFragment) fragmentManager.findFragmentByTag(MainActivity.CONTAINER_ADDITIONAL_CONTENT_TAG);
		if (additionalContentFragment != null) {
			additionalContentFragment.changeLanguage();
		}
		
		BannerFragment bannerFragment = (BannerFragment) fragmentManager.findFragmentByTag(MainActivity.CONTAINER_BANNER_TAG);
		if (bannerFragment != null) {
			bannerFragment.changeLanguage();
		}
    	
    	changeLanguageButtonsColor();
	}
	
	private void changeLanguageButtonsColorInToolbarOpen() {
		CustomTextView languageEnText = (CustomTextView) findViewById(R.id.main_layout_language_en_text);
		if (languageEnText == null) {
			return;
		}
		
		CustomTextView languagePlText = (CustomTextView) findViewById(R.id.main_layout_language_pl_text);
		if (languagePlText == null) {
			return;
		}
		
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			languageEnText.setTextColor(getApplicationContext().getResources().getColor(R.color.language_active_text));
			languagePlText.setTextColor(getApplicationContext().getResources().getColor(R.color.language_not_active_text));
		} else {
			languageEnText.setTextColor(getApplicationContext().getResources().getColor(R.color.language_not_active_text));
			languagePlText.setTextColor(getApplicationContext().getResources().getColor(R.color.language_active_text));
		}
	}
	
	private void changeLanguageButtonsColorInMenu() {
		CustomTextView menuLanguageEnglishText = (CustomTextView) findViewById(R.id.menu_layout_language_english_text);
		if (menuLanguageEnglishText == null) {
			return;
		}
		
		CustomTextView menuLanguagePolishText = (CustomTextView) findViewById(R.id.menu_layout_language_polish_text);
		if (menuLanguagePolishText == null) {
			return;
		}
		
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			menuLanguageEnglishText.setTextColor(getApplicationContext().getResources().getColor(R.color.language_active_text));
			menuLanguagePolishText.setTextColor(getApplicationContext().getResources().getColor(R.color.language_not_active_text));
		} else {
			menuLanguageEnglishText.setTextColor(getApplicationContext().getResources().getColor(R.color.language_not_active_text));
			menuLanguagePolishText.setTextColor(getApplicationContext().getResources().getColor(R.color.language_active_text));
		}
	}
	
	private void changeLanguageButtonsColor() {
		changeLanguageButtonsColorInToolbarOpen();
		changeLanguageButtonsColorInMenu();
	}
	
	private void changeLanguageInMainActivity() {
		int expandedGroupPosition = getExpandedGroupPosition();
		
		setMenuItems();
		
		if (expandedGroupPosition != -1) {
			expandPosition(expandedGroupPosition);
		}
		
		CustomTextView feedbackText = (CustomTextView) findViewById(R.id.main_layout_feedback_text);
		if (feedbackText == null) {
			return;
		}
		
		feedbackText.setText(R.string.feedback);
		
		CustomTextView languageText = (CustomTextView) findViewById(R.id.menu_layout_language_text);
		if (languageText == null) {
			return;
		}
		
		languageText.setText(R.string.language);
	}
	
	private void expandPosition(int position) {
		ExpandableListView menuList = (ExpandableListView) findViewById(R.id.menu_layout_menu_list);
		if (menuList == null) {
			return;
		}
		menuList.expandGroup(position);
	}

	private int getExpandedGroupPosition() {
		ExpandableListView menuList = (ExpandableListView) findViewById(R.id.menu_layout_menu_list);
		if (menuList == null) {
			return -1;
		}
		
		for (int position = 0; position < menuList.getExpandableListAdapter().getGroupCount(); ++position) {
			if (menuList.isGroupExpanded(position)) {
				return position;
			}
		}
		
		return -1;
	}
	
	private void collapseAllHeadersInMenu() {
		final ExpandableListView menuList = (ExpandableListView) findViewById(R.id.menu_layout_menu_list);
		if (menuList == null) {
			return;
		}
		
		int groupCount = menuList.getExpandableListAdapter().getGroupCount();
		for (int groupId = 0; groupId < groupCount; ++groupId) {
			menuList.collapseGroup(groupId);
		}
	}
	
	private void openMainScreenLayout(MainScreenLayout layout) {
		this.currentLayout = layout;
		this.currentLayoutInVisiblePanel = layout;
		updateDatabaseAndBannerAfter24Hours();
		hideToolbarOpen();
		setToolbarOpenLanguageBarVisibility(View.VISIBLE);
		setFeedbackBarVisibilityInToolbarOpen(View.VISIBLE);
		setLeftColumnDimensionForNewLayout(layout);
		setRightColumnVisibilityForNewLayout(layout);	
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				removeFragment(MainActivity.CONTAINER_ABOVE_NUMBER);
				removeFragment(MainActivity.CONTAINER_RIGHT_COLUMN_NUMBER);

				changeBannerPosition(DatabaseReader.MAIN_SCREEN_LAYOUT_ID);
				collapseAllHeadersInMenu();
				slidingMenu.showContent();
			}
		};
		
		this.waitingHandler = handler;
		
		createHandlerUIFlushForMainScreenLayout();
	}
	
	private void createHandlerUIFlushForMainScreenLayout() {
		Handler handlerFlush = new Handler();
		handlerFlush.post(new Runnable() {

			@Override
			public void run() {
				removeFragment(MainActivity.CONTAINER_BELOW_NUMBER);
				removeFragment(MainActivity.CONTAINER_RIGHT_COLUMN_NUMBER);
				
				showContainer(MainActivity.CONTAINER_BELOW_NUMBER);
				
				FragmentManager fragmentManager = getSupportFragmentManager();
				MainScreenFragment mainScreenFragment = new MainScreenFragment();
				mainScreenFragment.setArguments(createArgumentsForMainScreenFragment(news0BackgroundId, news1BackgroundId, news2BackgroundId, false));
				
				try {
					fragmentManager.beginTransaction().add(R.id.main_layout_fragment_below_container, mainScreenFragment, MainActivity.CONTAINER_BELOW_TAG).commit();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
			}
			
		});
	}
	
	private void setToolbarOpenLanguageBarVisibilityByLayoutType(LayoutTypeInterface layout) {
		if (layout.getLayoutType() == LayoutTypeInterface.ARTICLE_LAYOUT) {
			setToolbarOpenLanguageBarVisibility(View.INVISIBLE);
		} else {
			setToolbarOpenLanguageBarVisibility(View.VISIBLE);
		}
	}
	
	private void randNewsBackground() {
		this.news0BackgroundId = getNewsBackgroundIdExcept(-1, -1);
		this.news1BackgroundId = getNewsBackgroundIdExcept(this.news0BackgroundId, -1);
		this.news2BackgroundId = getNewsBackgroundIdExcept(this.news0BackgroundId, this.news1BackgroundId);
	}
	
	private void addFragmentForCustomContent(CustomContentLayout layout) {
		removeFragment(MainActivity.CONTAINER_ABOVE_NUMBER);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		showContainer(MainActivity.CONTAINER_ABOVE_NUMBER);
		
		Fragment fragment;
		if (isInSteps(layout)) {
			fragment = new StepsViewPagerFragment();
			fragment.setArguments(createArgumentsForStepsViewPagerFragment(layout));
		} else {
			fragment = new CustomContentFragment();
			fragment.setArguments(createArgumentsForCustomContentFragment(layout));
		}
		
		try {
			fragmentManager.beginTransaction().add(R.id.main_layout_fragment_above_container, fragment, MainActivity.CONTAINER_ABOVE_TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private Bundle createArgumentsForCustomContentFragment(CustomContentLayout layout) {
		Bundle arguments = new Bundle();
		arguments.putInt(CustomContentFragment.LAYOUT_ID_KEY, layout.getId());
		arguments.putBoolean(CustomContentFragment.IS_IN_STEPS_KEY, false);
		
		return arguments;
	}
	
	private Bundle createArgumentsForStepsViewPagerFragment(LayoutTypeInterface layout) {
		Bundle arguments = new Bundle();
		arguments.putInt(StepsViewPagerFragment.LAYOUT_ID_KEY, layout.getId());
		
		return arguments;
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
	
	private boolean isInSteps(LayoutTypeInterface layout) {
		DatabaseReader databaseReader = new DatabaseReader(this);
		LayoutTypeInterface parent = databaseReader.getLayout(layout.getParentId());
		if (parent == null) {
			return false;
		}
		
		return (parent.getLayoutType() == LayoutTypeInterface.STEPS_LAYOUT);
	}
	
	private long getAnimationDurationForLayoutByFromLayoutId(int fromLayoutId) {
		DatabaseReader databaseReader = new DatabaseReader(this);
		LayoutTypeInterface layout = databaseReader.getLayout(fromLayoutId);
		if (layout == null) {
			return 0;
		}
		
		if ((layout.getLayoutType() == LayoutTypeInterface.MENU_LAYOUT) ||
				(layout.getLayoutType() == LayoutTypeInterface.STEPS_LAYOUT)) {
			if (fromLayoutId >=1 && fromLayoutId <= 5) {
				return 0;
			} else {
				return MainActivity.MENU_ANIMATION_DURATION;
			}
		} else {
			return 0;
		}
	}
	
	private long getAnimationDurationForLayoutWhenEnteringMenu(int id) {
		DatabaseReader databaseReader = new DatabaseReader(this);
		LayoutTypeInterface layout = databaseReader.getLayout(id);
		if (layout == null) {
			return 0;
		}
		
		if ((layout.getLayoutType() != LayoutTypeInterface.MENU_LAYOUT) &&
				(layout.getLayoutType() != LayoutTypeInterface.STEPS_LAYOUT)) {
				return MainActivity.MENU_ANIMATION_DURATION;
		} else {
			return 0;
		}
	}
	
	private long getAnimationDurationForLayoutWhenEnteringSteps(int id) {
		DatabaseReader databaseReader = new DatabaseReader(this);
		LayoutTypeInterface layout = databaseReader.getLayout(id);
		if (layout == null) {
			return 0;
		}
		
		if ((layout.getLayoutType() != LayoutTypeInterface.MENU_LAYOUT) &&
				(layout.getLayoutType() != LayoutTypeInterface.STEPS_LAYOUT)) {
				return MainActivity.MENU_ANIMATION_DURATION;
		} else {
			return 0;
		}
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
	
	private void setSlidingMenu() {
		this.slidingMenu = new SlidingMenu(this);
		this.slidingMenu.setMode(SlidingMenu.LEFT);
		this.slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		this.slidingMenu.setShadowWidth((int) (getDisplayWidth() * MainActivity.MENU_SHADOW_TO_DISPLAY_WIDTH));
		this.slidingMenu.setShadowDrawable(R.drawable.sliding_menu_gradient);
        int menuWidth = (int) (getDisplayWidth() * getMenuWidthToWindowWidth());
        this.slidingMenu.setBehindWidth(menuWidth);
        
        this.slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        this.slidingMenu.setMenu(R.layout.menu_layout);
	}
	
	private double getMenuWidthToWindowWidth() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return MainActivity.MENU_WIDTH_TO_WINDOW_WIDTH_PORTRAIT;
		} else {
			return MainActivity.MENU_WIDTH_TO_WINDOW_WIDTH_LANDSCAPE;
		}
	}
	
	private void setIconMenu() {
		ImageView menuIcon = (ImageView) findViewById(R.id.main_layout_menu_icon);
		if (menuIcon == null) {
			return;
		}
		
		menuIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				slidingMenu.toggle();
			}
			
		});
	}
	
	private void changeBackgroundsInMainScreen(int news0BackgroundId, int news1BackgroundId, int news2BackgroundId, Bundle savedInstanceState) {
		if (savedInstanceState.getInt(MainActivity.SAVED_INSTANCE_STATE_CURRENT_LAYOUT_IN_VISIBLE_PANEL_ID_KEY) != 0) {
			return;
		}
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		MainScreenFragment mainScreenFragment = (MainScreenFragment) fragmentManager.findFragmentByTag(MainActivity.CONTAINER_BELOW_TAG);
		mainScreenFragment.changeNewsBackgrounds(this.news0BackgroundId, this.news1BackgroundId, this.news2BackgroundId);
	}
	
	private void setMainScreenFragment(int news0BackgroundId, int news1BackgroundId, int news2BackgroundId, Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			changeBackgroundsInMainScreen(news0BackgroundId, news1BackgroundId, news2BackgroundId, savedInstanceState);
			return;
		}
		
		this.currentLayout = new MainScreenLayout();
		this.currentLayoutInVisiblePanel = this.currentLayout; 
		
		removeFragment(MainActivity.CONTAINER_BELOW_NUMBER);
		removeFragment(MainActivity.CONTAINER_ABOVE_NUMBER);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		showContainer(MainActivity.CONTAINER_BELOW_NUMBER);		
		
		MainScreenFragment mainScreenFragment = new MainScreenFragment();
		mainScreenFragment.setArguments(createArgumentsForMainScreenFragment(news0BackgroundId, news1BackgroundId, news2BackgroundId, true));
		
		try {
			fragmentManager.beginTransaction().add(R.id.main_layout_fragment_below_container, mainScreenFragment, MainActivity.CONTAINER_BELOW_TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private Bundle createArgumentsForMainScreenFragment(int news0BackgroundId, int news1BackgroundId, int news2BackgroundId, boolean appStart) {
		Bundle arguments = new Bundle();
		arguments.putInt(MainScreenFragment.NEWS_0_BACKGROUND_ID_KEY, news0BackgroundId);
		arguments.putInt(MainScreenFragment.NEWS_1_BACKGROUND_ID_KEY, news1BackgroundId);
		arguments.putInt(MainScreenFragment.NEWS_2_BACKGROUND_ID_KEY, news2BackgroundId);
		arguments.putBoolean(MainScreenFragment.APP_START_KEY, appStart);
		
		return arguments;
	}
	
	private String getItemTitleToMainMenu(int id) {
		DatabaseReader databaseReader = new DatabaseReader(this);
		LayoutTypeInterface layout = databaseReader.getLayout(id);
		if (layout == null) {
			return null;
		}
		
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			return layout.getTitleEn();
		} else {
			return layout.getTitlePl();
		}	
	}
	
	private void setMenuItems() {
		ArrayList<String> headers = new ArrayList<String>();
		headers.add(getString(R.string.home));
		headers.add(getItemTitleToMainMenu(DatabaseReader.GENERAL_INFORMATION_LAYOUT_ID));
		headers.add(getItemTitleToMainMenu(DatabaseReader.VISAS_LAYOUT_ID));
		headers.add(getItemTitleToMainMenu(DatabaseReader.SERVICES_FOR_US_CITIZENS_LAYOUT_ID));
		headers.add(getItemTitleToMainMenu(DatabaseReader.NEWS_LAYOUT_ID));
		//headers.add(getString(R.string.visa_status)); TODO
		headers.add(getItemTitleToMainMenu(DatabaseReader.PASSPORT_TRACKING_LAYOUT_ID));
		
		ArrayList<MenuLayoutItem> homeMenu = new ArrayList<MenuLayoutItem>();
		ArrayList<MenuLayoutItem> generalInformationMenu = getNextMenuLevel(1);
		ArrayList<MenuLayoutItem> visaMenu = getNextMenuLevel(2);
		ArrayList<MenuLayoutItem> servicesMenu = getNextMenuLevel(3);
		ArrayList<MenuLayoutItem> newsMenu = getNextMenuLevel(4);
		//ArrayList<MenuLayoutItem> visaStatusMenu = new ArrayList<MenuLayoutItem>(); TODO
		ArrayList<MenuLayoutItem> passportTrackingMenu = new ArrayList<MenuLayoutItem>();
		
		HashMap<String, ArrayList<MenuLayoutItem>> menuStructure = new HashMap<String, ArrayList<MenuLayoutItem>>();
		menuStructure.put(headers.get(0), homeMenu);
		menuStructure.put(headers.get(1), generalInformationMenu);
		menuStructure.put(headers.get(2), visaMenu);
		menuStructure.put(headers.get(3), servicesMenu);
		menuStructure.put(headers.get(4), newsMenu);
		//menuStructure.put(headers.get(5), visaStatusMenu); TODO
		menuStructure.put(headers.get(5), passportTrackingMenu);
		
		ExpandableListView menuList = (ExpandableListView) findViewById(R.id.menu_layout_menu_list);
		if (menuList == null) {
			return;
		}
		
		ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(headers, menuStructure, this);
		menuList.setAdapter(expandableListAdapter);
	}
	
	private void setLanguageButtonsOnClicksListeners() {
		SquareLayout languagePolishLayout = (SquareLayout) findViewById(R.id.menu_layout_language_polish_layout);
		if (languagePolishLayout == null) {
			return;
		}
		
		languagePolishLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				changeLanguage(Global.SHARED_PREFERENCES_LANGUAGE_POLISH);
			}
			
		});
		
		SquareLayout languageEnglishLayout = (SquareLayout) findViewById(R.id.menu_layout_language_english_layout);
		if (languageEnglishLayout == null) {
			return;
		}
		
		languageEnglishLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				changeLanguage(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
			}
			
		});
	}
	
	private void setLanguageButtonsColorsAndBorders() {
		CustomTextView languageEnglishText = (CustomTextView) findViewById(R.id.menu_layout_language_english_text);
		if (languageEnglishText == null) {
			return;
		}
		
		CustomTextView languagePolishText = (CustomTextView) findViewById(R.id.menu_layout_language_polish_text);
		if (languagePolishText == null) {
			return;
		}
		
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			languageEnglishText.setTextColor(getApplicationContext().getResources().getColor(R.color.language_active_text));
			languagePolishText.setTextColor(getApplicationContext().getResources().getColor(R.color.language_not_active_text));
		} else {
			languageEnglishText.setTextColor(getApplicationContext().getResources().getColor(R.color.language_not_active_text));
			languagePolishText.setTextColor(getApplicationContext().getResources().getColor(R.color.language_active_text));
		}
	}
	
	private void setLanguageButtons() {
		setLanguageButtonsColorsAndBorders();
		setLanguageButtonsOnClicksListeners();
	}
	
	private void setOnCloseListener() {
		slidingMenu.setOnCloseListener(new OnCloseListener() {

			@Override
			public void onClose() {
				currentLayout = currentLayoutInVisiblePanel;
				if (currentLayout.getId() == 0) {
					collapseAllHeadersInMenu();
				}
			}
			
		});
	}
	
	private boolean isHomeButtonClicked(int position) {
		return (position == ExpandableListAdapter.HOME_POSITION);
	}
	
	private boolean isPassportTrackingClicked(int position) {
		return (position == ExpandableListAdapter.PASSPORT_TRACKING_POSITION);
	}
	
	private void setOnGroupExpandListenerInMenu() {
		final ExpandableListView menuList = (ExpandableListView) findViewById(R.id.menu_layout_menu_list);
		if (menuList == null) {
			return;
		}
		
		menuList.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {			
				if (isHomeButtonClicked(groupPosition)) {
					openLayout(DatabaseReader.MAIN_SCREEN_LAYOUT_ID, DatabaseReader.MAIN_SCREEN_LAYOUT_ID);
					menuList.collapseGroup(groupPosition);
				}
				
				if  (isPassportTrackingClicked(groupPosition)) {
					openLayout(DatabaseReader.PASSPORT_TRACKING_LAYOUT_ID, DatabaseReader.MAIN_SCREEN_LAYOUT_ID);
					menuList.collapseGroup(groupPosition);
				}
				
				int groupCount = menuList.getExpandableListAdapter().getGroupCount();
				
				for (int groupId = 0; groupId < groupCount; ++groupId) {
					if (groupId != groupPosition) {
						menuList.collapseGroup(groupId);
					}
				}
				
			}
			
		});
	}
	
	private void setOnChildClickListenerInMenu() {
		ExpandableListView menuList = (ExpandableListView) findViewById(R.id.menu_layout_menu_list);
		if (menuList == null) {
			return;
		}
		
		menuList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {

				MenuLayoutItem child = (MenuLayoutItem) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
				
				String parentName = (String) parent.getExpandableListAdapter().getGroup(groupPosition);
				int parentId = 0;
				if (parentName.equals(getItemTitleToMainMenu(DatabaseReader.GENERAL_INFORMATION_LAYOUT_ID))) {
					parentId = DatabaseReader.GENERAL_INFORMATION_LAYOUT_ID;
				} else if (parentName.equals(getItemTitleToMainMenu(DatabaseReader.VISAS_LAYOUT_ID))) {
					parentId = DatabaseReader.VISAS_LAYOUT_ID;
				} else if (parentName.equals(getItemTitleToMainMenu(DatabaseReader.SERVICES_FOR_US_CITIZENS_LAYOUT_ID))) {
					parentId = DatabaseReader.SERVICES_FOR_US_CITIZENS_LAYOUT_ID;
				} else if (parentName.equals(getItemTitleToMainMenu(DatabaseReader.NEWS_LAYOUT_ID))) {
					parentId = DatabaseReader.NEWS_LAYOUT_ID;
				} else if (parentName.equals(getItemTitleToMainMenu(DatabaseReader.VISA_APPLICATION_STATUS_LAYOUT_ID))) {
					parentId = DatabaseReader.VISA_APPLICATION_STATUS_LAYOUT_ID;
				} else if (parentName.equals(getItemTitleToMainMenu(DatabaseReader.PASSPORT_TRACKING_LAYOUT_ID))) {
					parentId = DatabaseReader.PASSPORT_TRACKING_LAYOUT_ID;
				}
				
				openLayout(child.getPathToId(), parentId);
				
				return false;
			}
			
		});
	}
	
	private ArrayList<MenuLayoutItem> getNextMenuLevel(int id) {
		DatabaseReader databaseReader = new DatabaseReader(this);
		MenuLayout menuLayout = (MenuLayout) databaseReader.getLayout(id);
		if (menuLayout == null) {
			return new ArrayList<MenuLayoutItem>();
		}
		
		if (getAppLanguage().equals("EN")) {
			return menuLayout.getMenuItemsEn();
		} else {
			return menuLayout.getMenuItemsPl();
		}
	}
	
	private void updateDatabase(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			return;
		}
		
		if (!isOnline()) {
			showNoInternetToast();
			return;
		}
		
		try {
			this.getSupportFragmentManager().beginTransaction().add(new ApplicationContentDownloaderFragment(), ApplicationContentDownloaderFragment.TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
    }
	
	private boolean isMenuLayoutInMenuPanel(int id) {
		return (id >= 1 && id <= 5);
	}
	
	private void openMenuLayoutInMenuPanel(MenuLayout layout, int fromLayout) {
		slidingMenu.toggle();
	}
	
	private int getEndPositionForAboveContainer() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return getDisplayWidth();
		} else {
			return (int) (getDisplayWidth() * MainActivity.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH);
		}
	}
	
	private void setWaitingHandlerForMenuLayoutWhenCanToggleMenu() {
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				removeFragment(MainActivity.CONTAINER_ABOVE_NUMBER);
				slidingMenu.toggle();
			}
		};
		
		this.waitingHandler = handler;
	}
	
	private void setWaitingHandlerForMenuLayoutWhenHaveLoadNewMenu() {
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				removeFragment(MainActivity.CONTAINER_ABOVE_NUMBER);
			}
		};
		
		this.waitingHandler = handler;
	}
	
	private void setWaitingHandlerForMenuLayoutWhenHaveNotLoadNewMenu(final ObjectAnimator fragmentAnimator) {
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				ObjectAnimator additionContentAnimation = createAdditionContentAnimationClose();
				AnimatorSet animatorSet;
				animatorSet = createAnimatorSet(fragmentAnimator, additionContentAnimation);
				createAnimatorListenerForMenuLayout(animatorSet);

				animatorSet.start();
			}
		};
		
		this.waitingHandler = handler;
	}
	
	private void openMenuLayoutInLayoutPanel(final MenuLayout layout, int fromLayoutId) {
		this.currentLayout = layout;
		this.currentLayoutInVisiblePanel = layout;
		
		setToolbarOpenLanguageBarVisibility(View.VISIBLE);
		setFeedbackBarVisibilityInToolbarOpen(View.VISIBLE);
		setLeftColumnDimensionForNewLayout(layout);
		setRightColumnVisibilityForNewLayout(layout);
		changeBannerPosition(layout.getId());
		
		FrameLayout fragmentAboveContainer = (FrameLayout) findViewById(R.id.main_layout_fragment_above_container);
		if (fragmentAboveContainer == null) {
			return;
		}
		
		float startPosition = 0;
		float endPosition = getEndPositionForAboveContainer();
		long duration = getAnimationDurationForLayoutWhenEnteringMenu(fromLayoutId);
		final boolean haveLoadNewMenu = duration == 0;
		
		ObjectAnimator fragmentAnimator = ObjectAnimator.ofFloat(fragmentAboveContainer, "translationX", startPosition, endPosition);
		fragmentAnimator.setDuration(duration);
		setAnimatorListenerForMenuAnimation(fragmentAnimator);
		
		if (canToggleMenu(layout.getId())) {
			setWaitingHandlerForMenuLayoutWhenCanToggleMenu();
			addFragmentForMenu(layout);
		} else {
			if (haveLoadNewMenu) {
				setWaitingHandlerForMenuLayoutWhenHaveLoadNewMenu();
				addFragmentForMenu(layout);
			} else {
				setWaitingHandlerForMenuLayoutWhenHaveNotLoadNewMenu(fragmentAnimator);
				addFragmentForMenu(layout);
			}
		}
	}
	
	private void createAnimatorListenerForMenuLayout(AnimatorSet animatorSet) {
		animatorSet.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				removeFragment(MainActivity.CONTAINER_ABOVE_NUMBER);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}
		});
	}
	
	private void setAnimatorListenerForMenuAnimation(ObjectAnimator fragmentAnimator) {
		fragmentAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				removeFragment(MainActivity.CONTAINER_ABOVE_NUMBER);
				removeFragment(MainActivity.CONTAINER_RIGHT_COLUMN_NUMBER);
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationStart(Animator animation) {
			}
			
		});
	}
	
	private void openMenuLayout(final MenuLayout layout, int fromLayoutId) {
		this.currentLayout = layout;
		hideToolbarOpen();
		
		if (isMenuLayoutInMenuPanel(layout.getId())) {
			openMenuLayoutInMenuPanel(layout, fromLayoutId);
		} else {
			openMenuLayoutInLayoutPanel(layout, fromLayoutId);
		}
    }
	
	private void addFragmentForMenu(MenuLayout layout) {
		removeFragment(MainActivity.CONTAINER_BELOW_NUMBER);
		removeFragment(MainActivity.CONTAINER_RIGHT_COLUMN_NUMBER);
		
		showContainer(MainActivity.CONTAINER_BELOW_NUMBER);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		MenuFragment menuFragment = new MenuFragment();
		menuFragment.setArguments(createArgumentsForMenuFragment(layout));
		
		try {
			fragmentManager.beginTransaction().add(R.id.main_layout_fragment_below_container, menuFragment, MainActivity.CONTAINER_BELOW_TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private Bundle createArgumentsForMenuFragment(MenuLayout layout) {
		Bundle arguments = new Bundle();
		arguments.putInt(MenuFragment.LAYOUT_ID_KEY, layout.getId());
		
		return arguments;
	}
	
	private void backFromMenuLayout() {
		openLayout(this.currentLayout.getParentId(), this.currentLayout.getId());
	}
	
	private boolean canToggleMenu(int id) {
		if (id >= 1 && id <= 4) {
			if (this.slidingMenu.isMenuShowing()) {
				return false;
			} else {
				return true;
			}
		} else {
			if (this.slidingMenu.isMenuShowing()) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	private void removeFragment(int containerNumber) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		Fragment currentFragment = null;
		if (containerNumber == MainActivity.CONTAINER_BELOW_NUMBER) {
			currentFragment = fragmentManager.findFragmentByTag(MainActivity.CONTAINER_BELOW_TAG);
			hideContainer(containerNumber);
		} else if (containerNumber == MainActivity.CONTAINER_ABOVE_NUMBER) {
			currentFragment = fragmentManager.findFragmentByTag(MainActivity.CONTAINER_ABOVE_TAG);
			hideContainer(containerNumber);
		} else if (containerNumber == MainActivity.CONTAINER_RIGHT_COLUMN_NUMBER) {
			currentFragment = fragmentManager.findFragmentByTag(MainActivity.CONTAINER_ADDITIONAL_CONTENT_TAG);
			hideContainer(containerNumber);
		} else if (containerNumber == MainActivity.CONTAINER_FEEDBACK_NUMBER) {
			currentFragment = fragmentManager.findFragmentByTag(MainActivity.CONTAINER_FEEDBACK_TAG);
			hideContainer(containerNumber);
		} else if (containerNumber == MainActivity.CONTAINER_BANNER_NUMBER) {
			currentFragment = fragmentManager.findFragmentByTag(MainActivity.CONTAINER_BANNER_TAG);
			hideContainer(containerNumber);
		}
		
		if (currentFragment != null) {
			try {
				fragmentManager.beginTransaction().remove(currentFragment).commit();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setLeftColumnDimensionForNewLayout(LayoutTypeInterface layoutType) {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return;
		}
		
		setFragmentBelowContainerDimensionForNewLayout(layoutType);
		setFragmentAboveContainerDimensionForNewLayout(layoutType);
	}
	
	private void setRightColumnVisibilityForNewLayout(LayoutTypeInterface layoutType) {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return;
		}
		
		FrameLayout rightColumnFragmentLayout = (FrameLayout) findViewById(R.id.main_layout_right_column_fragment_below_layout);
		if (rightColumnFragmentLayout == null) {
			return;
		}
		
		if (layoutType.getLayoutType() == LayoutTypeInterface.NEWS_LAYOUT) {
			rightColumnFragmentLayout.setVisibility(View.INVISIBLE);
		} else {
			rightColumnFragmentLayout.setVisibility(View.VISIBLE);
		}
	}
	
	private ObjectAnimator createAnimationForNewsLayout(final AllNewsLayout layout, int fromLayoutId) {
		float startPosition = getStartPositionForAboveContainer();
		float endPosition = 0;
		long duration = getAnimationDurationForLayoutByFromLayoutId(fromLayoutId);
		
		FrameLayout fragmentAboveContainer = (FrameLayout) findViewById(R.id.main_layout_fragment_above_container);
		if (fragmentAboveContainer == null) {
			return null;
		}
		
		ObjectAnimator fragmentAnimator = ObjectAnimator.ofFloat(fragmentAboveContainer, "translationX", startPosition, endPosition);
		fragmentAnimator.setDuration(duration);
		fragmentAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (canToggleMenu(layout.getId())) {
					slidingMenu.setOnClosedListener(new OnClosedListener() {

						@Override
						public void onClosed() {
							setContentInFragmentAbove();
							slidingMenu.setOnClosedListener(null);
						}
								
					});
					slidingMenu.toggle();
				} else {
					setContentInFragmentAbove();
				}
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationStart(Animator animation) {
			}
			
		});
		
		return fragmentAnimator;
	}
	
	private void openNewsLayout(AllNewsLayout layout, int fromLayoutId) {
		this.currentLayout = layout;
		this.currentLayoutInVisiblePanel = layout;
		hideToolbarOpen();
		setToolbarOpenLanguageBarVisibility(View.VISIBLE);
		setFeedbackBarVisibilityInToolbarOpen(View.VISIBLE);
		setLeftColumnDimensionForNewLayout(layout);
		setRightColumnVisibilityForNewLayout(layout);
		changeBannerPosition(layout.getId());
		addFragmentForNews(layout);
		createHandlerUIFlushForNewsFragment(layout, fromLayoutId);
	}
	
	private void createHandlerUIFlushForNewsFragment(final AllNewsLayout layout, final int fromLayoutId) {
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				ObjectAnimator animation = createAnimationForNewsLayout(layout, fromLayoutId);
				animation.start();	
			}
			
		});
	}
	
	private ObjectAnimator createAdditionContentAnimationClose() {
		if (!isAdditionalContentVisible()) {
			return null;
		}
		
		RelativeLayout rightColumnAboveLayout = (RelativeLayout) findViewById(R.id.main_layout_right_column_above_layout);
		if (rightColumnAboveLayout == null) {
			return null;
		}
		
		int startPosition = 0;
		int endPosition = getDisplayWidth() - ((int) (getDisplayWidth() * MainActivity.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH));
		
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rightColumnAboveLayout, "translationX", startPosition, endPosition);
		objectAnimator.setDuration((long) (MainActivity.MENU_ANIMATION_DURATION));
		
		return objectAnimator;
	}
	
	private boolean isAdditionalContentVisible() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentByTag(MainActivity.CONTAINER_ADDITIONAL_CONTENT_TAG);
		
		return (fragment != null);
	}
	
	private void addFragmentForNews(AllNewsLayout layout) {
		removeFragment(MainActivity.CONTAINER_ABOVE_NUMBER);
		removeFragment(MainActivity.CONTAINER_RIGHT_COLUMN_NUMBER);
		
		showContainer(MainActivity.CONTAINER_ABOVE_NUMBER);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		NewsFragment newsFragment = new NewsFragment();
		newsFragment.setArguments(createArgumentsForNewsragment(layout));
		
		try {
			fragmentManager.beginTransaction().add(R.id.main_layout_fragment_above_container, newsFragment, MainActivity.CONTAINER_ABOVE_TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private Bundle createArgumentsForNewsragment(AllNewsLayout layout) {
		Bundle arguments = new Bundle();
		arguments.putInt(NewsFragment.LAYOUT_ID_KEY, layout.getId());
		
		return arguments;
	}
	
	private ObjectAnimator createAnimationForVideosLayout(final VideosLayout layout, int fromLayoutId) {		
		float startPosition = getStartPositionForAboveContainer();
		float endPosition = 0;
		long duration = getAnimationDurationForLayoutByFromLayoutId(fromLayoutId);
		
		FrameLayout fragmentAboveContainer = (FrameLayout) findViewById(R.id.main_layout_fragment_above_container);
		if (fragmentAboveContainer == null) {
			return null;
		}
		
		ObjectAnimator fragmentAnimator = ObjectAnimator.ofFloat(fragmentAboveContainer, "translationX", startPosition, endPosition);
		fragmentAnimator.setDuration(duration);
		fragmentAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (canToggleMenu(layout.getId())) {
					slidingMenu.setOnClosedListener(new OnClosedListener() {

						@Override
						public void onClosed() {
							setContentInFragmentAbove();
							slidingMenu.setOnClosedListener(null);
						}
								
					});
					slidingMenu.toggle();
				} else {
					setContentInFragmentAbove();
				}
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationStart(Animator animation) {
			}
			
		});
		
		return fragmentAnimator;
	}
	
	private void openVideosLayout(VideosLayout layout, int fromLayoutId) {
		this.currentLayout = layout;
		this.currentLayoutInVisiblePanel = layout;
		hideToolbarOpen();
		setToolbarOpenLanguageBarVisibility(View.VISIBLE);
		setFeedbackBarVisibilityInToolbarOpen(View.VISIBLE);
		setLeftColumnDimensionForNewLayout(layout);
		setRightColumnVisibilityForNewLayout(layout);
		changeBannerPosition(layout.getId());
		addFragmentForVideos(layout);
		createHandlerUIFlushForVideosFragment(layout, fromLayoutId);
	}
	
	private void createHandlerUIFlushForVideosFragment(final VideosLayout layout, final int fromLayoutId) {
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				ObjectAnimator animation = createAnimationForVideosLayout(layout, fromLayoutId);
				animation.start();
			}
			
		});
	}
	
	private void addFragmentForVideos(VideosLayout layout) {
		removeFragment(MainActivity.CONTAINER_ABOVE_NUMBER);
		removeFragment(MainActivity.CONTAINER_RIGHT_COLUMN_NUMBER);
		
		showContainer(MainActivity.CONTAINER_ABOVE_NUMBER);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		VideosFragment videosFragment = new VideosFragment();
		videosFragment.setArguments(createArgumentsForVideoFragment(layout));
		
		try {
			fragmentManager.beginTransaction().add(R.id.main_layout_fragment_above_container, videosFragment, MainActivity.CONTAINER_ABOVE_TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private Bundle createArgumentsForVideoFragment(VideosLayout layout) {
		Bundle arguments = new Bundle();
		arguments.putInt(VideosFragment.LAYOUT_ID_KEY, layout.getId());
		
		return arguments;
	}
	
	private ObjectAnimator createAnimationForFileManagerLayout(final FileManagerLayout layout, int fromLayoutId) {
		float startPosition = getStartPositionForAboveContainer();
		float endPosition = 0;
		long duration = getAnimationDurationForLayoutByFromLayoutId(fromLayoutId);
		
		FrameLayout fragmentAboveContainer = (FrameLayout) findViewById(R.id.main_layout_fragment_above_container);
		if (fragmentAboveContainer == null) {
			return null;
		}
		
		ObjectAnimator fragmentAnimator = ObjectAnimator.ofFloat(fragmentAboveContainer, "translationX", startPosition, endPosition);
		fragmentAnimator.setDuration(duration);
		fragmentAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (canToggleMenu(layout.getId())) {
					slidingMenu.setOnClosedListener(new OnClosedListener() {

						@Override
						public void onClosed() {
							setContentInFragmentAbove();
							slidingMenu.setOnClosedListener(null);
						}
								
					});
					slidingMenu.toggle();
				} else {
					setContentInFragmentAbove();
				}
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationStart(Animator animation) {
			}
			
		});
		
		return fragmentAnimator;
	}
	
	private void openFileManagerLayout(FileManagerLayout layout, int fromLayoutId) {
		this.currentLayout = layout;
		this.currentLayoutInVisiblePanel = layout;
		hideToolbarOpen();
		setToolbarOpenLanguageBarVisibility(View.VISIBLE);
		setFeedbackBarVisibilityInToolbarOpen(View.VISIBLE);
		setLeftColumnDimensionForNewLayout(layout);
		setRightColumnVisibilityForNewLayout(layout);
		changeBannerPosition(layout.getId());
		addFragmentForFileManager(layout);
		createHandlerUIFlushForFileManagerFragment(layout, fromLayoutId);
	}
	
	private void createHandlerUIFlushForFileManagerFragment(final FileManagerLayout layout, final int fromLayoutId) {
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				ObjectAnimator animation = createAnimationForFileManagerLayout(layout, fromLayoutId);
				animation.start();	
			}
			
		});
	}
	
	private void addFragmentForFileManager(FileManagerLayout layout) {
		removeFragment(MainActivity.CONTAINER_ABOVE_NUMBER);
		removeFragment(MainActivity.CONTAINER_RIGHT_COLUMN_NUMBER);
		
		showContainer(MainActivity.CONTAINER_ABOVE_NUMBER);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		FileManagerFragment fileManagerFragment = new FileManagerFragment();
		fileManagerFragment.setArguments(createArgumentsForFileManagerFragment(layout));
		
		try {
			fragmentManager.beginTransaction().add(R.id.main_layout_fragment_above_container, fileManagerFragment, MainActivity.CONTAINER_ABOVE_TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private Bundle createArgumentsForFileManagerFragment(FileManagerLayout layout) {
		Bundle arguments = new Bundle();
		arguments.putInt(FileManagerFragment.LAYOUT_ID_KEY, layout.getId());
		
		return arguments;
	}
	
	private ObjectAnimator createAnimationForListLayout(final ListLayout layout, int fromLayoutId) {
		float startPosition = getStartPositionForAboveContainer();
		float endPosition = 0;
		long duration = getAnimationDurationForLayoutByFromLayoutId(fromLayoutId);
		
		FrameLayout fragmentAboveContainer = (FrameLayout) findViewById(R.id.main_layout_fragment_above_container);
		if (fragmentAboveContainer == null) {
			return null;
		}
		
		ObjectAnimator fragmentAnimator = ObjectAnimator.ofFloat(fragmentAboveContainer, "translationX", startPosition, endPosition);
		fragmentAnimator.setDuration(duration);
		fragmentAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (canToggleMenu(layout.getId())) {
					slidingMenu.setOnClosedListener(new OnClosedListener() {

						@Override
						public void onClosed() {
							setContentInFragmentAbove();
							setContentInFragmentAdditional();
							slidingMenu.setOnClosedListener(null);
						}
								
					});
					slidingMenu.toggle();
				} else {
					setContentInFragmentAbove();
					setContentInFragmentAdditional();
				}
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationStart(Animator animation) {
			}
			
		});
		
		return fragmentAnimator;
	}
	
	private void openListLayout(ListLayout layout, int fromLayoutId) {
		this.currentLayout = layout;
		this.currentLayoutInVisiblePanel = layout;
		hideToolbarOpen();
		setToolbarOpenLanguageBarVisibility(View.VISIBLE);
		setFeedbackBarVisibilityInToolbarOpen(View.VISIBLE);
		setLeftColumnDimensionForNewLayout(layout);
		setRightColumnVisibilityForNewLayout(layout);
		changeBannerPosition(layout.getId());
		addFragmentForList(layout);
		createHandlerUIFlushForListFragment(layout, fromLayoutId);
	}
	
	private void createHandlerUIFlushForListFragment(final ListLayout layout, final int fromLayoutId) {
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				ObjectAnimator animation = createAnimationForListLayout(layout, fromLayoutId);
				ObjectAnimator additionalContentAnimation = createAdditionalContentAnimation(layout, fromLayoutId);
				AnimatorSet animatorSet;
				animatorSet = createAnimatorSet(animation, additionalContentAnimation);		
				animatorSet.start();
			}
			
		});
	}
	
	private void addFragmentForList(ListLayout layout) {
		removeFragment(MainActivity.CONTAINER_ABOVE_NUMBER);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		showContainer(MainActivity.CONTAINER_ABOVE_NUMBER);
		
		Fragment fragment;
		if (isInSteps(layout)) {
			fragment = new StepsViewPagerFragment();
			fragment.setArguments(createArgumentsForStepsViewPagerFragment(layout));
		} else {
			fragment = new ListFragment();
			fragment.setArguments(createArgumentsForListFragment(layout));
		}
	
		try {
			fragmentManager.beginTransaction().add(R.id.main_layout_fragment_above_container, fragment, MainActivity.CONTAINER_ABOVE_TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private Bundle createArgumentsForListFragment(ListLayout layout) {
		Bundle arguments = new Bundle();
		arguments.putInt(ListFragment.LAYOUT_ID_KEY, layout.getId());
		arguments.putBoolean(ListFragment.IS_IN_STEPS_KEY, false);
		
		return arguments;
	}
	
	private void setWaitingHandlerForStepsLayotuWhenCanToggleMenu() {
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				removeFragment(MainActivity.CONTAINER_ABOVE_NUMBER);
				slidingMenu.toggle();
			}
		};
		
		this.waitingHandler = handler;
	}
	
	private void setWaitingHandlerForStepsLayotuWhenHaveLoadNewMenu() {
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				removeFragment(MainActivity.CONTAINER_ABOVE_NUMBER);
			}
		};
		
		this.waitingHandler = handler;
	}
	
	private void setWaitingHandlerForStepsLayotuWhenHaveNotLoadNewMenu(final ObjectAnimator fragmentAnimator) {
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				ObjectAnimator additionContentAnimation = createAdditionContentAnimationClose();
				AnimatorSet animatorSet;
				animatorSet = createAnimatorSet(fragmentAnimator, additionContentAnimation);
				createAnimatorListenerForStepsLayout(animatorSet);
				
				animatorSet.start();
			}
		};
		this.waitingHandler = handler;
	}
	
	private void openStepsAsMenu(StepsLayout layout, int fromLayoutId) {
		this.currentLayout = layout;
		
		setToolbarOpenLanguageBarVisibility(View.VISIBLE);
		setFeedbackBarVisibilityInToolbarOpen(View.VISIBLE);
		setLeftColumnDimensionForNewLayout(layout);
		setRightColumnVisibilityForNewLayout(layout);
		changeBannerPosition(layout.getId());
		
		FrameLayout fragmentAboveContainer = (FrameLayout) findViewById(R.id.main_layout_fragment_above_container);
		if (fragmentAboveContainer == null) {
			return;
		}
		
		float startPosition = 0;
		float endPosition = getEndPositionForAboveContainer();
		long duration = getAnimationDurationForLayoutWhenEnteringSteps(fromLayoutId);
		final boolean haveLoadNewSteps = duration == 0;
		
		ObjectAnimator fragmentAnimator = ObjectAnimator.ofFloat(fragmentAboveContainer, "translationX", startPosition, endPosition);
		fragmentAnimator.setDuration(duration);
		setAnimatorListenerForMenuAnimation(fragmentAnimator);
		
		if (canToggleMenu(layout.getId())) {
			setWaitingHandlerForStepsLayotuWhenCanToggleMenu();
			addFragmentForSteps(layout);
		} else {
			if (haveLoadNewSteps) {
				setWaitingHandlerForStepsLayotuWhenHaveLoadNewMenu();
				addFragmentForSteps(layout);
			} else {	
				setWaitingHandlerForStepsLayotuWhenHaveNotLoadNewMenu(fragmentAnimator);
				addFragmentForSteps(layout);
			}
		}
	}
	
	private void createAnimatorListenerForStepsLayout(AnimatorSet animatorSet) {
		animatorSet.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				removeFragment(MainActivity.CONTAINER_ABOVE_NUMBER);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {	
			}
		});
	}
	
	private void openStepsLayout(final StepsLayout layout, final int fromLayoutId) {
		hideToolbarOpen();
		openStepsAsMenu(layout, fromLayoutId);
	}
	
	private void addFragmentForSteps(StepsLayout layout) {
		removeFragment(MainActivity.CONTAINER_BELOW_NUMBER);
		removeFragment(MainActivity.CONTAINER_RIGHT_COLUMN_NUMBER);
		
		showContainer(MainActivity.CONTAINER_BELOW_NUMBER);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		StepsFragment stepsFragment = new StepsFragment();
		stepsFragment.setArguments(createArgumentsForStepsFragment(layout));
		
		try {
			fragmentManager.beginTransaction().add(R.id.main_layout_fragment_below_container, stepsFragment, MainActivity.CONTAINER_BELOW_TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private Bundle createArgumentsForStepsFragment(StepsLayout layout) {
		Bundle arguments = new Bundle();
		arguments.putInt(StepsFragment.LAYOUT_ID_KEY, layout.getId());
		
		return arguments;
	}
	
	private ObjectAnimator createAnimationForMapLayout(final MapLayout layout, int fromLayoutId) {
		float startPosition = getStartPositionForAboveContainer();
		float endPosition = 0;
		long duration = getAnimationDurationForLayoutByFromLayoutId(fromLayoutId);
		
		FrameLayout fragmentAboveContainer = (FrameLayout) findViewById(R.id.main_layout_fragment_above_container);
		if (fragmentAboveContainer == null) {
			return null;
		}
		
		ObjectAnimator fragmentAnimator = ObjectAnimator.ofFloat(fragmentAboveContainer, "translationX", startPosition, endPosition);
		fragmentAnimator.setDuration(duration);
		fragmentAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (canToggleMenu(layout.getId())) {
					slidingMenu.setOnClosedListener(new OnClosedListener() {

						@Override
						public void onClosed() {
							setContentInFragmentAbove();
							setContentInFragmentAdditional();
							slidingMenu.setOnClosedListener(null);
						}
								
					});
					slidingMenu.toggle();
				} else {
					setContentInFragmentAbove();
					setContentInFragmentAdditional();
				}
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationStart(Animator animation) {
			}
			
		});
		
		return fragmentAnimator;
	}
	
	private void openMapLayout(MapLayout layout, int fromLayoutId) {
		this.currentLayout = layout;
		this.currentLayoutInVisiblePanel = layout;
		hideToolbarOpen();
		setToolbarOpenLanguageBarVisibility(View.VISIBLE);
		setFeedbackBarVisibilityInToolbarOpen(View.VISIBLE);
		setLeftColumnDimensionForNewLayout(layout);
		setRightColumnVisibilityForNewLayout(layout);
		changeBannerPosition(layout.getId());
		addFragmentForMap(layout);
		createHandlerUIFlushForMapFragment(layout, fromLayoutId);
	}
	
	private void createHandlerUIFlushForMapFragment(final MapLayout layout, final int fromLayoutId) {
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				ObjectAnimator animation = createAnimationForMapLayout(layout, fromLayoutId);
				ObjectAnimator additionalContentAnimation = createAdditionalContentAnimation(layout, fromLayoutId);
				AnimatorSet animatorSet;
				animatorSet = createAnimatorSet(animation, additionalContentAnimation);		
				animatorSet.start();		
			}
			
		});
	}
	
	private void addFragmentForMap(MapLayout layout) {
		removeFragment(MainActivity.CONTAINER_ABOVE_NUMBER);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		showContainer(MainActivity.CONTAINER_ABOVE_NUMBER);
		
		Fragment fragment;
		if (isInSteps(layout)) {
			fragment = new StepsViewPagerFragment();
			fragment.setArguments(createArgumentsForStepsViewPagerFragment(layout));
		} else {
			fragment = new MapFragment();
			fragment.setArguments(createArgumentsForMapFragment(layout));
		}
		
		try {
			fragmentManager.beginTransaction().add(R.id.main_layout_fragment_above_container, fragment, MainActivity.CONTAINER_ABOVE_TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private Bundle createArgumentsForMapFragment(MapLayout layout) {
		Bundle arguments = new Bundle();
		arguments.putInt(MapFragment.LAYOUT_ID_KEY, layout.getId());
		arguments.putBoolean(MapFragment.IS_IN_STEPS_KEY, false);
		
		return arguments;
	}
	
	private ObjectAnimator createAnimationForFAQLayout(final FAQLayout layout, int fromLayoutId) {
		float startPosition = getStartPositionForAboveContainer();
		float endPosition = 0;
		long duration = getAnimationDurationForLayoutByFromLayoutId(fromLayoutId);
		
		FrameLayout fragmentAboveContainer = (FrameLayout) findViewById(R.id.main_layout_fragment_above_container);
		if (fragmentAboveContainer == null) {
			return null;
		}
		
		ObjectAnimator fragmentAnimator = ObjectAnimator.ofFloat(fragmentAboveContainer, "translationX", startPosition, endPosition);
		fragmentAnimator.setDuration(duration);
		fragmentAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (canToggleMenu(layout.getId())) {
					slidingMenu.setOnClosedListener(new OnClosedListener() {

						@Override
						public void onClosed() {
							setContentInFragmentAbove();
							slidingMenu.setOnClosedListener(null);
						}
								
					});
					slidingMenu.toggle();
				} else {
					setContentInFragmentAbove();
				}
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationStart(Animator animation) {
			}
			
		});
		
		return fragmentAnimator;
	}
	
	private void openFAQLayout(FAQLayout layout, int fromLayoutId) {
		this.currentLayout = layout;
		this.currentLayoutInVisiblePanel = layout;
		hideToolbarOpen();
		setToolbarOpenLanguageBarVisibility(View.VISIBLE);
		setFeedbackBarVisibilityInToolbarOpen(View.VISIBLE);
		setLeftColumnDimensionForNewLayout(layout);
		setRightColumnVisibilityForNewLayout(layout);
		changeBannerPosition(layout.getId());
		addFragmentForFAQ(layout);
		createHandlerUIFlushForFAQFragment(layout, fromLayoutId);
	}
	
	private void createHandlerUIFlushForFAQFragment(final FAQLayout layout, final int fromLayoutId) {
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				ObjectAnimator animation = createAnimationForFAQLayout(layout, fromLayoutId);
				animation.start();	
			}
			
		});
	}
	
	private void addFragmentForFAQ(FAQLayout layout) {
		removeFragment(MainActivity.CONTAINER_ABOVE_NUMBER);
		removeFragment(MainActivity.CONTAINER_RIGHT_COLUMN_NUMBER);
		
		showContainer(MainActivity.CONTAINER_ABOVE_NUMBER);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		FAQFragment faqFragment = new FAQFragment();
		faqFragment.setArguments(createArgumentsForFAQFragment(layout));
		
		try {
			fragmentManager.beginTransaction().add(R.id.main_layout_fragment_above_container, faqFragment, MainActivity.CONTAINER_ABOVE_TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private Bundle createArgumentsForFAQFragment(FAQLayout layout) {
		Bundle arguments = new Bundle();
		arguments.putInt(FAQFragment.LAYOUT_ID_KEY, layout.getId());
		
		return arguments;
	}
	
	private void openFacebookLayout(FacebookLayout layout) {
		Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MainActivity.FACEBOOK_URL));
    	startActivity(facebookIntent);
	}
	
	@SuppressWarnings("deprecation")
	private int getDisplayWidth() {
		Display display = getWindowManager().getDefaultDisplay();
		
		if (Build.VERSION.SDK_INT < 13) {
			return display.getWidth();
		} else {
			Point size = new Point();
			display.getSize(size);
			
			return size.x;
		}
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
	
	private String getAppLanguage() {
		SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		return sharedPreferences.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
	} 
}
