package com.agitive.usembassy.fragments.layoutFragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.databases.ArticleLayout;
import com.agitive.usembassy.databases.DatabaseAdapter;
import com.agitive.usembassy.databases.DatabaseReader;
import com.agitive.usembassy.fragments.asyncTaskFragments.RSSAndTweetsDownloaderFragment;
import com.agitive.usembassy.fragments.dialogFragments.ServerErrorDialogFragment;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.interfaces.LayoutFragmentInterface;
import com.agitive.usembassy.layouts.CustomTextView;
import com.agitive.usembassy.objects.Banner;
import com.agitive.usembassy.objects.RSSItem;
import com.agitive.usembassy.objects.Tweet;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainScreenFragment extends Fragment implements LayoutFragmentInterface {

	
	public static final String NEWS_0_BACKGROUND_ID_KEY = "com.agitive.usembassy.fragments.MainScreenFragments.news0BackgroundId";
	public static final String NEWS_1_BACKGROUND_ID_KEY = "com.agitive.usembassy.fragments.MainScreenFragments.news1BackgroundId";
	public static final String NEWS_2_BACKGROUND_ID_KEY = "com.agitive.usembassy.fragments.MainScreenFragments.news2BackgroundId";
	public static final String APP_START_KEY = "com.agitive.usembassy.fragments.MainScreenFragments.appStart";
	
	private static final double FLAG_LAYOUT_TO_ROOT_VIEW = 0.4;
	private static final double NEWS_0_LAYOUT_TO_ROOT_VIEW_PORTRAIT = 0.2;
	private static final double NEWS_0_LAYOUT_TO_ROOT_VIEW_LANDSCAPE = 1.0/3.0;
	private static final double NEWS_1_LAYOUT_TO_ROOT_VIEW = 0.2;
	private static final int MINUTES_50 = 3000000;
	private static final int ANIMATION_DURATION = 1000;
	private static final double BANNER_LAYOUT_HEIGHT_TO_ROOT_VIEW_HEIGHT_PORTRAIT = 0.6;
	private static final double BANNER_LAYOUT_HEIGHT_TO_ROOT_VIEW_HEIGHT_LANDSCAPE = 1.0/3.0;
	private static final int RSS_ITEMS_FROM_DATABASE_LIMIT = 3;
	private static final int TWEETS_FROM_DATABASE_LIMIT = 2;
	private static final double NEWS_TITLE_HEIGHT_TO_NEWS_LAYOUT_WIDTH_LANDSCAPE = 0.064;
	private static final double TWEET_TITLE_HEIGHT_TO_TWEET_LAYOUT_WIDTH_LANDSCAPE = 0.064;
	private static final double NEWS_TITLE_HEIGHT_TO_NEWS_LAYOUT_HEIGHT_PORTRAIT = 0.14;
	private static final double TWEET_TITLE_HEIGHT_TO_TWEET_LAYOUT_HEIGHT_PORTRAIT = 0.14;
	private static final double WELCOME_TEXT_HEIGHT_TO_FLAG_LAYOUT_HEIGHT_LANDSCAPE = 0.12;
	private static final double WELCOME_TEXT_HEIGHT_TO_FLAG_LAYOUT_HEIGHT_PORTRAIT = 0.15;
	private static final double US_MISSION_POLAND_TEXT_HEIGHT_TO_FLAG_LAYOUT_HEIGHT_LANDSCAPE = 0.083;
	private static final double US_MISSION_POLAND_TEXT_HEIGHT_TO_FLAG_LAYOUT_HEIGHT_PORTRAIT = 0.1;
	private static final String CONTAINER_BANNER_TAG = "com.agitive.usembassy.fragments.MainScreenFragment.containerBannerTag";
	private static final String SAVED_INSTANCE_STATE_BANNER_OPEN = "com.agitive.usembassy.fragments.MainScreenFragment.SavedInstanceStateBannerOpen";
	
	private View rootView;
	private int[] newsBackgroundsIds;
	private RSSItem news0;
	private RSSItem news1;
	private Tweet tweet;
	private AnimatorSet animatorSet;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    
        this.rootView = inflater.inflate(R.layout.main_screen_layout, container, false);
        this.newsBackgroundsIds = null;
        this.news0 = null;
        this.news1 = null;
        
        setFlagLayoutDimensions();
        setNews0LayoutDimensions();
        setNews1LayoutDimensions();
        setTweetLayoutDimensions();
        setBannerLayoutDimensions();
        setNewsBackgroundsNames();
        setOnClickListenersForNewsAndTweet();
        setRSSAndTweets();
        restoreState(savedInstanceState);
        
        return this.rootView;
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setNewsBackgrounds();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		((MainActivity)getActivity()).runHandler(MainActivity.HANDLER_MESSAGE_NORMAL_RUN);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		
		if (this.animatorSet != null) {
			this.animatorSet.cancel();
		}
	}
	
	@Override
	public void changeLanguage() {
		changeLanguageInNews();
		changeLanguageInBanner();
		changeLanguageInFlagLayout();
	}
	
	@Override
	public void onSaveInstanceState (Bundle savedInstanceState) {
		savedInstanceState.putBoolean(MainScreenFragment.SAVED_INSTANCE_STATE_BANNER_OPEN, isBannerVisible());
	}
	
	public void setRSSAndTweetsUpdateStatus(boolean isSuccess) {
		if (!isSuccess) {
			removeServerErrorDialogFragment();
			showServerError();
		}
		
		setRSSTweetsAndShowThem();
		((MainActivity)getActivity()).updateRSSAndTweetInNewsRightColumn();
	}
	
	public void changeNewsBackgrounds(int news0BackroundId, int news1BackroundId, int news2BackroundId) {
		getArguments().putInt(MainScreenFragment.NEWS_0_BACKGROUND_ID_KEY, news0BackroundId);
		getArguments().putInt(MainScreenFragment.NEWS_1_BACKGROUND_ID_KEY, news1BackroundId);
		getArguments().putInt(MainScreenFragment.NEWS_2_BACKGROUND_ID_KEY, news2BackroundId);
	}
	
	public void showBanner(final Banner banner) {
		if (banner == null) {
			return;
		}
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						removeBannerFragment();
						
						FrameLayout bannerFragmentContainer = (FrameLayout) rootView.findViewById(R.id.main_screen_layout_banner_fragment_container);
						if (bannerFragmentContainer == null) {
							return;
						}
						
						bannerFragmentContainer.setVisibility(FrameLayout.VISIBLE);

						addBannerFragment(banner);
					}
					
				});
			}
			
		}).start();
	}
	
	public void removeRSSAndTweetsDownloaderFragment() {
		FragmentManager fragmentManager = getChildFragmentManager();
		Fragment fragment = fragmentManager.findFragmentByTag(RSSAndTweetsDownloaderFragment.TAG);
		if (fragment == null) {
			return;
		}
		
		try {
			fragmentManager.beginTransaction().remove(fragment).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	public void closeBanner() { // NO_UCD (use default)
		removeBannerFragment();
		((MainActivity)getActivity()).eraseBanner();
	}
	
	private void removeBannerFragment() {
		FragmentManager fragmentManager = getChildFragmentManager();
		Fragment fragment = fragmentManager.findFragmentByTag(MainScreenFragment.CONTAINER_BANNER_TAG);
		if (fragment != null) {
			try {
				fragmentManager.beginTransaction().remove(fragment).commit();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
		
		FrameLayout bannerFragmentContainer = (FrameLayout) this.rootView.findViewById(R.id.main_screen_layout_banner_fragment_container);
		if (bannerFragmentContainer == null) {
			return;
		}
		
		bannerFragmentContainer.setVisibility(FrameLayout.INVISIBLE);
	}
	
	private void changeLanguageInFlagLayout() {
		CustomTextView usMissionPolandText = (CustomTextView) this.rootView.findViewById(R.id.main_screen_layout_us_mission_poland_text);
		if (usMissionPolandText == null) {
			return;
		}
		
		usMissionPolandText.setText(getResources().getString(R.string.main_screen_us_mission_poland));
		
		CustomTextView welcomeText = (CustomTextView) this.rootView.findViewById(R.id.main_screen_layout_welcome_text);
		if (welcomeText == null) {
			return;
		}
		
		welcomeText.setText(getResources().getString(R.string.main_screen_welcome));
	}
	
	private void addBannerFragment(Banner banner) {
		FragmentManager fragmentManager = getChildFragmentManager();
		
		BannerFragment bannerFragment = new BannerFragment();
		bannerFragment.setArguments(createArgumentsForBanner(banner));
		
		try {
			fragmentManager.beginTransaction().add(R.id.main_screen_layout_banner_fragment_container, bannerFragment, MainScreenFragment.CONTAINER_BANNER_TAG).commit();
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
	
	private void changeLanguageInNews() {
		setRSSAndTweets();
	}
	
	private void restoreState(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		}
		
		setBannerFragmentContainerVisibility(savedInstanceState.getBoolean(MainScreenFragment.SAVED_INSTANCE_STATE_BANNER_OPEN));
	}
	
	private void setBannerFragmentContainerVisibility(boolean isVisible) {
		FrameLayout bannerFragmentContainer = (FrameLayout) this.rootView.findViewById(R.id.main_screen_layout_banner_fragment_container);
		if (bannerFragmentContainer == null) {
			return;
		}
		
		if (isVisible) {
			bannerFragmentContainer.setVisibility(FrameLayout.VISIBLE);
		} else {
			bannerFragmentContainer.setVisibility(FrameLayout.INVISIBLE);
		}
	}
	
	private boolean isBannerVisible() {
		FragmentManager fragmentManager = getChildFragmentManager();
		Fragment fragment = fragmentManager.findFragmentByTag(MainScreenFragment.CONTAINER_BANNER_TAG);
		
		return (fragment != null);
	}
	
	private void changeLanguageInBanner() {
		FragmentManager fragmentManager = getChildFragmentManager();
		BannerFragment bannerFragment = (BannerFragment) fragmentManager.findFragmentByTag(MainScreenFragment.CONTAINER_BANNER_TAG);
		if (bannerFragment == null) {
			return;
		}
		
		bannerFragment.changeLanguage();
	}
	
	private String getAppLanguage() {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		return sharedPreferences.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
	}
	
	private void setBannerLayoutDimensions() {
		ViewTreeObserver rootViewObserver = this.rootView.getViewTreeObserver();
		rootViewObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			private boolean isReady = false;
			
			@Override
			public void onGlobalLayout() {
				if (this.isReady) {
					return;
				}
				this.isReady = true;
				
				int height = (int) (rootView.getHeight() * getBannerLayoutHeightToRootViewHeight() + 1);
				setBannerHeight(height);
			}
			
		});
	}
	
	private double getBannerLayoutHeightToRootViewHeight() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return MainScreenFragment.BANNER_LAYOUT_HEIGHT_TO_ROOT_VIEW_HEIGHT_PORTRAIT;
		} else {
			return MainScreenFragment.BANNER_LAYOUT_HEIGHT_TO_ROOT_VIEW_HEIGHT_LANDSCAPE;
		}
	}
	
	private void setBannerHeight(int height) {
		FrameLayout bannerFragmentContainer = (FrameLayout) this.rootView.findViewById(R.id.main_screen_layout_banner_fragment_container);
		if (bannerFragmentContainer == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bannerFragmentContainer.getLayoutParams();
		params.height = height;
		bannerFragmentContainer.setLayoutParams(params);
	}
	
	private void setOnClickListenerForNews0Portrait() {
		final RelativeLayout news0Layout = (RelativeLayout) this.rootView.findViewById(R.id.main_screen_layout_news_0_layout);
		if (news0Layout == null) {
			return;
		}
		
		news0Layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (news0 == null) {
					return;
				}
				
				news0Layout.setClickable(false);
				
				RelativeLayout flagLayout = (RelativeLayout) rootView.findViewById(R.id.main_screen_layout_flag_layout);
				if (flagLayout == null) {
					return;
				}
				
				RelativeLayout news1Layout = (RelativeLayout) rootView.findViewById(R.id.main_screen_layout_news_1_layout);
				if (news1Layout == null) {
					return;
				}
				
				RelativeLayout tweetLayout = (RelativeLayout) rootView.findViewById(R.id.main_screen_layout_tweet_layout);
				if (tweetLayout == null) {
					return;
				}
				
				int flagTranslationY = -flagLayout.getHeight();
				ObjectAnimator flagLayoutAnimator = ObjectAnimator.ofFloat(flagLayout, "translationY", flagTranslationY);
				
				int news0TranslationY = -flagLayout.getHeight() + ((MainActivity)getActivity()).getToolbarOpenHeight();
				ObjectAnimator news0LayoutAnimator = ObjectAnimator.ofFloat(news0Layout, "translationY", news0TranslationY);
				
				int news1TranslationY = news1Layout.getHeight() + tweetLayout.getHeight();
				ObjectAnimator news1LayoutAnimator = ObjectAnimator.ofFloat(news1Layout, "translationY", news1TranslationY);
				
				int tweetTranslationY = tweetLayout.getHeight();
				ObjectAnimator tweetLayoutAnimator = ObjectAnimator.ofFloat(tweetLayout, "translationY", tweetTranslationY);
				
				AnimatorSet animatorSet = new AnimatorSet();
				MainScreenFragment.this.animatorSet = animatorSet;
				animatorSet.playTogether(flagLayoutAnimator, news0LayoutAnimator, news1LayoutAnimator, tweetLayoutAnimator);
				setAnimatorListener(animatorSet, getArguments().getInt(MainScreenFragment.NEWS_0_BACKGROUND_ID_KEY), 0);
				animatorSet.setDuration(MainScreenFragment.ANIMATION_DURATION);
				
				
				animatorSet.start();
			}
		});
	}
	
	private void setAnimatorListener(AnimatorSet animatorSet, final int backgroundId, final int newsNumber) {
		animatorSet.addListener(new AnimatorListener() {
			
			private boolean isCanceled = false;

			@Override
			public void onAnimationCancel(Animator animation) {
				this.isCanceled = true;
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (this.isCanceled) {
					return;
				}
				
				((MainActivity)getActivity()).openArticleLayout(createArticleLayout(), createArgumentsForArticleLayout(newsNumber, backgroundId, true));
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationStart(Animator animation) {
			}
			
		});
	}
	
	private ArticleLayout createArticleLayout() {
		DatabaseReader databaseReader = new DatabaseReader(getActivity());
		ArticleLayout articleLayout = new ArticleLayout(databaseReader.getNewsId());
		
		return articleLayout;
	}
	
	private Bundle createArgumentsForArticleLayout(int newsNumber, int backgroundId, boolean animateBackground) {
		Bundle arguments = new Bundle();
		arguments.putString(ArticleFragment.ARTICLE_TITLE_KEY, getNewsTitle(newsNumber));
		arguments.putString(ArticleFragment.ARTICLE_DATE_KEY, getNewsDate(newsNumber));
		arguments.putString(ArticleFragment.ARTICLE_TEXT_KEY, getNewsText(newsNumber));
		arguments.putInt(ArticleFragment.ARTICLE_LANDSCAPE_ID_KEY, backgroundId);
		arguments.putInt(ArticleFragment.ARTICLE_LANDSCAPE_HEIGHT_KEY, (int) (this.rootView.getHeight() * getNews0LayoutHeightToRootViewHeight()));
		arguments.putBoolean(ArticleFragment.ARTICLE_LAYOUT_BACKGROUND_ANIMATE_KEY, animateBackground); 
		
		return arguments;
	}
	
	private double getNews0LayoutHeightToRootViewHeight() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return MainScreenFragment.NEWS_0_LAYOUT_TO_ROOT_VIEW_PORTRAIT;
		} else {
			return MainScreenFragment.NEWS_0_LAYOUT_TO_ROOT_VIEW_LANDSCAPE;
		}
	}
	
	private String getNewsText(int newsNumber) {
		
		switch (newsNumber) {
			case 0:
				return this.news0.getText();
			case 1:
				return this.news1.getText();
		}
	
		return null;
	}
	
	private String getNewsDate(int newsNumber) {
		switch (newsNumber) {
			case 0:
				return this.news0.getSubtitle();
			case 1:
				return this.news1.getSubtitle();
		}
	
		return null;
	}
	
	private String getNewsTitle(int newsNumber) {
		switch (newsNumber) {
			case 0:
				return this.news0.getTitle();
			case 1:
				return this.news1.getTitle();
		}
		
		return null;
	}
	
	private void setOnClickListenerForNews1() {
		final RelativeLayout news1Layout = (RelativeLayout) this.rootView.findViewById(R.id.main_screen_layout_news_1_layout);
		if (news1Layout == null) {
			return;
		}
		
		news1Layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (news1 == null) {
					return;
				}
				
				news1Layout.setClickable(false);
				
				RelativeLayout flagLayout = (RelativeLayout) rootView.findViewById(R.id.main_screen_layout_flag_layout);
				if (flagLayout == null) {
					return;
				}
				
				RelativeLayout news0Layout = (RelativeLayout) rootView.findViewById(R.id.main_screen_layout_news_0_layout);
				if (news0Layout == null) {
					return;
				}
				
				RelativeLayout tweetLayout = (RelativeLayout) rootView.findViewById(R.id.main_screen_layout_tweet_layout);
				if (tweetLayout == null) {
					return;
				}
				
				int flagTranslationY = -flagLayout.getHeight();
				ObjectAnimator flagLayoutAnimator = ObjectAnimator.ofFloat(flagLayout, "translationY", flagTranslationY);
				
				int news0TranslationY = -flagLayout.getHeight() - news0Layout.getHeight();
				ObjectAnimator news0LayoutAnimator = ObjectAnimator.ofFloat(news0Layout, "translationY", news0TranslationY);
				
				int news1TranslationY = -flagLayout.getHeight() - news0Layout.getHeight() + ((MainActivity)getActivity()).getToolbarOpenHeight();
				ObjectAnimator news1LayoutAnimator = ObjectAnimator.ofFloat(news1Layout, "translationY", news1TranslationY);
				
				int tweetTranslationY = tweetLayout.getHeight();
				ObjectAnimator tweetLayoutAnimator = ObjectAnimator.ofFloat(tweetLayout, "translationY", tweetTranslationY);
				
				AnimatorSet animatorSet = new AnimatorSet();
				MainScreenFragment.this.animatorSet = animatorSet;
				animatorSet.playTogether(flagLayoutAnimator, news0LayoutAnimator, news1LayoutAnimator, tweetLayoutAnimator);
				animatorSet.setDuration(MainScreenFragment.ANIMATION_DURATION);
				setAnimatorListener(animatorSet, getArguments().getInt(MainScreenFragment.NEWS_1_BACKGROUND_ID_KEY), 1);
				animatorSet.start();
			}
		});
	}
	
	private void setOnClickListenerForTweet() {
		final RelativeLayout tweetLayout = (RelativeLayout) this.rootView.findViewById(R.id.main_screen_layout_tweet_layout);
		if (tweetLayout == null) {
			return;
		}
		
		tweetLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {	
				if (tweet == null) {
					return;
				}
				
				if (!isOnline()) {
					showNoInternetForTweetToast();
					return;
				}
				
		    	Intent twitterAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://status?status_id=" + tweet.getId()));
		    	try {
		    		startActivity(twitterAppIntent);
		    	} catch (Exception e) {
		    		Intent webBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/USEmbassyWarsaw/status/" + tweet.getId()));
					startActivity(webBrowserIntent);
		    	}
			}
		});
	}
	
	private void showNoInternetForTweetToast() {
		Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.main_screen_no_internet_for_tweet, Toast.LENGTH_LONG);
        toast.show();
	}
	
	private void setOnClickListenersForNewsAndTweetPortrait() {
		setOnClickListenerForNews0Portrait();
		setOnClickListenerForNews1();
		setOnClickListenerForTweet();
	}
	
	private void setOnClickListenersForNewsAndTweetLandscape() {
		setOnClickListenerForNews0Landscape();
		setOnClickListenerForTweet();
	}
	
	private void setOnClickListenerForNews0Landscape() {
		RelativeLayout news0Layout = (RelativeLayout) this.rootView.findViewById(R.id.main_screen_layout_news_0_layout);
		if (news0Layout == null) {
			return;
		}
		
		news0Layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (news0 == null) {
					return;
				}
				
				((MainActivity)getActivity()).openArticleLayout(createArticleLayout(), createArgumentsForArticleLayout(0, getArguments().getInt(MainScreenFragment.NEWS_2_BACKGROUND_ID_KEY), false));
			}
			
		});
	}
	
	private void setOnClickListenersForNewsAndTweet() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			setOnClickListenersForNewsAndTweetPortrait();
		} else {
			setOnClickListenersForNewsAndTweetLandscape();
		}
	}
	
	private void setFlagLayoutDimensionsPortrait() {
		ViewTreeObserver rootViewObserver = this.rootView.getViewTreeObserver();
		rootViewObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			private boolean isReady = false;
			
			@Override
			public void onGlobalLayout() {
				if (this.isReady) {
					return;
				}
				this.isReady = true;
				
				RelativeLayout flagLayout = (RelativeLayout) rootView.findViewById(R.id.main_screen_layout_flag_layout);
				if (flagLayout == null) {
					return;
				}
				
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) flagLayout.getLayoutParams();
				int height = (int) (rootView.getHeight() * FLAG_LAYOUT_TO_ROOT_VIEW);
				params.height = height;
				flagLayout.setLayoutParams(params);
			
				setWelcomeTextHeight((int) (height * MainScreenFragment.WELCOME_TEXT_HEIGHT_TO_FLAG_LAYOUT_HEIGHT_PORTRAIT));
				setUSMissionPolandTextHeight((int) (height * MainScreenFragment.US_MISSION_POLAND_TEXT_HEIGHT_TO_FLAG_LAYOUT_HEIGHT_PORTRAIT));
			}
			
		});
	}
	
	private void setFlagLayoutDimensionsLandscape() {
		final RelativeLayout flagLayout = (RelativeLayout) this.rootView.findViewById(R.id.main_screen_layout_flag_layout);
		if (flagLayout == null) {
			return;
		}
		
		ViewTreeObserver flagLayoutObserver = flagLayout.getViewTreeObserver();
		flagLayoutObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			private boolean ready = false;
			
			@Override
			public void onGlobalLayout() {
				if (this.ready) {
					return;
				}
				this.ready = true;
				
				setWelcomeTextHeight((int) (flagLayout.getHeight() * MainScreenFragment.WELCOME_TEXT_HEIGHT_TO_FLAG_LAYOUT_HEIGHT_LANDSCAPE));
				setUSMissionPolandTextHeight((int) (flagLayout.getHeight() * MainScreenFragment.US_MISSION_POLAND_TEXT_HEIGHT_TO_FLAG_LAYOUT_HEIGHT_LANDSCAPE));
			}
			
		});
	}
	
	private void setWelcomeTextHeight(int height) {
		CustomTextView welcomeText = (CustomTextView) this.rootView.findViewById(R.id.main_screen_layout_welcome_text);
		if (welcomeText == null) {
			return;
		}
		
		welcomeText.setTextSize(MainActivity.pxToSp(height, getActivity()));
	}
	
	private void setUSMissionPolandTextHeight(int height) {
		CustomTextView welcomeText = (CustomTextView) this.rootView.findViewById(R.id.main_screen_layout_us_mission_poland_text);
		if (welcomeText == null) {
			return;
		}
		
		welcomeText.setTextSize(MainActivity.pxToSp(height, getActivity()));
	}
	
	private void setFlagLayoutDimensions() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			setFlagLayoutDimensionsPortrait();
		} else {
			setFlagLayoutDimensionsLandscape();
		}
	}
	
	private void setNews0LayoutDimensionsPortrait() {
		ViewTreeObserver rootViewObserver = this.rootView.getViewTreeObserver();
		rootViewObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			private boolean isReady = false;
			
			@Override
			public void onGlobalLayout() {
				if (this.isReady) {
					return;
				}
				this.isReady = true;
				
				RelativeLayout news0Layout = (RelativeLayout) rootView.findViewById(R.id.main_screen_layout_news_0_layout);
				if (news0Layout == null) {
					return;
				}
				
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) news0Layout.getLayoutParams();
				int height = (int) (rootView.getHeight() * NEWS_0_LAYOUT_TO_ROOT_VIEW_PORTRAIT);
				params.height = height;
				params.topMargin = (int) (rootView.getHeight() * FLAG_LAYOUT_TO_ROOT_VIEW);
				news0Layout.setLayoutParams(params);
				
				setNews0TitleHeight((int) (height * MainScreenFragment.NEWS_TITLE_HEIGHT_TO_NEWS_LAYOUT_HEIGHT_PORTRAIT));
			}
			
		});
	}
	
	private void setNews0LayoutDimensionsLandscape() {
		final RelativeLayout news0Layout = (RelativeLayout) this.rootView.findViewById(R.id.main_screen_layout_news_0_layout);
		if (news0Layout == null) {
			return;
		}
		
		ViewTreeObserver news0LayoutObserver = news0Layout.getViewTreeObserver();
		news0LayoutObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			private boolean ready = false;

			@Override
			public void onGlobalLayout() {
				if (this.ready) {
					return;
				}
				this.ready = true;
				
				setNews0TitleHeight((int) (news0Layout.getWidth() * MainScreenFragment.NEWS_TITLE_HEIGHT_TO_NEWS_LAYOUT_WIDTH_LANDSCAPE));
			}
			
		});
	}
	
	private void setNews0LayoutDimensions() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			setNews0LayoutDimensionsPortrait();
		} else {
			setNews0LayoutDimensionsLandscape();
		}
	}
	
	private void setNews0TitleHeight(int height) {
		CustomTextView news0Title = (CustomTextView) this.rootView.findViewById(R.id.main_screen_layout_news_0_title);
		if (news0Title == null) {
			return;
		}
		
		news0Title.setTextSize(MainActivity.pxToSp(height, getActivity()));
	}
	
	private void setNews1TitleHeight(int height) {
		CustomTextView news1Title = (CustomTextView) this.rootView.findViewById(R.id.main_screen_layout_news_1_title);
		if (news1Title == null) {
			return;
		}
		
		news1Title.setTextSize(MainActivity.pxToSp(height, getActivity()));
	}
	
	private void setTweetTitleHeight(int height) {
		CustomTextView tweetTitle = (CustomTextView) this.rootView.findViewById(R.id.main_screen_layout_tweet_title);
		if (tweetTitle == null) {
			return;
		}
		
		tweetTitle.setTextSize(MainActivity.pxToSp(height, getActivity()));
	}
	
	private void setNews1LayoutDimensions() {
		if (getOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
			return;
		}
		
		ViewTreeObserver rootViewObserver = this.rootView.getViewTreeObserver();
		rootViewObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			private boolean isReady = false;
			
			@Override
			public void onGlobalLayout() {
				if (this.isReady) {
					return;
				}
				this.isReady = true;
				
				RelativeLayout news1Layout = (RelativeLayout) rootView.findViewById(R.id.main_screen_layout_news_1_layout);
				if (news1Layout == null) {
					return;
				}
				
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) news1Layout.getLayoutParams();
				int height = (int) (rootView.getHeight() * NEWS_1_LAYOUT_TO_ROOT_VIEW);
				params.height = height;
				params.topMargin = ((int) (rootView.getHeight() * FLAG_LAYOUT_TO_ROOT_VIEW) +
						(int) (rootView.getHeight() * NEWS_0_LAYOUT_TO_ROOT_VIEW_PORTRAIT));
				news1Layout.setLayoutParams(params);
				
				setNews1TitleHeight((int) (height * MainScreenFragment.NEWS_TITLE_HEIGHT_TO_NEWS_LAYOUT_HEIGHT_PORTRAIT));
			}
			
		});
	}
	
	private void setTweetLayoutDimensionsPortrait() {
		ViewTreeObserver rootViewObserver = this.rootView.getViewTreeObserver();
		rootViewObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			private boolean isReady = false;
			
			@Override
			public void onGlobalLayout() {
				if (this.isReady) {
					return;
				}
				this.isReady = true;
				
				RelativeLayout tweetLayout = (RelativeLayout) rootView.findViewById(R.id.main_screen_layout_tweet_layout);
				if (tweetLayout == null) {
					return;
				}
				
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tweetLayout.getLayoutParams();
				int previousLayoutsHeightSum = ((int) (rootView.getHeight() * FLAG_LAYOUT_TO_ROOT_VIEW)) +
						((int) (rootView.getHeight() * NEWS_0_LAYOUT_TO_ROOT_VIEW_PORTRAIT)) +
						((int) (rootView.getHeight() * NEWS_1_LAYOUT_TO_ROOT_VIEW));
				int height = rootView.getHeight() - previousLayoutsHeightSum; 
				params.height = height;
				params.topMargin = previousLayoutsHeightSum;
				tweetLayout.setLayoutParams(params);
				
				setTweetTitleHeight((int) (height * MainScreenFragment.TWEET_TITLE_HEIGHT_TO_TWEET_LAYOUT_HEIGHT_PORTRAIT));
			}
			
		});
	}
	
	private void setTweetLayoutDimensionsLandscape() {
		final RelativeLayout tweetLayout = (RelativeLayout) rootView.findViewById(R.id.main_screen_layout_tweet_layout);
		if (tweetLayout == null) {
			return;
		}
		
		ViewTreeObserver tweetLayoutObserver = tweetLayout.getViewTreeObserver();
		tweetLayoutObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			private boolean ready = false;

			@Override
			public void onGlobalLayout() {
				if (this.ready) {
					return;
				}
				this.ready = true;
				
				setTweetTitleHeight((int) (tweetLayout.getWidth() * MainScreenFragment.TWEET_TITLE_HEIGHT_TO_TWEET_LAYOUT_WIDTH_LANDSCAPE));
			}
			
		});
	}
	
	private void setTweetLayoutDimensions() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			setTweetLayoutDimensionsPortrait();
		} else {
			setTweetLayoutDimensionsLandscape();
		}
	}
	
	private void setNewsBackgroundsPortrait() {
		setNews0Background();
		setNews1Background();
	}
	
	private void setNewsBackgroundsLandscape() {
		setNews0Background();
	}
	
	private void setNewsBackgrounds() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			setNewsBackgroundsPortrait();
		} else {
			setNewsBackgroundsLandscape();
		}
	}
	
	private void setNews0Background() {
		ImageView news0Background = (ImageView) this.rootView.findViewById(R.id.main_screen_layout_news_0_background);
		if (news0Background == null) {
			return;
		}
		
		int backgroundNumber;
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			backgroundNumber = getArguments().getInt(MainScreenFragment.NEWS_0_BACKGROUND_ID_KEY);
		} else {
			backgroundNumber = getArguments().getInt(MainScreenFragment.NEWS_2_BACKGROUND_ID_KEY);
		}
		
		news0Background.setImageResource(this.newsBackgroundsIds[backgroundNumber]);
	}
	
	private void setNews1Background() {
		ImageView news1Background = (ImageView) this.rootView.findViewById(R.id.main_screen_layout_news_1_background);
		if (news1Background == null) {
			return;
		}
		
		int backgroundNumber = getArguments().getInt(MainScreenFragment.NEWS_1_BACKGROUND_ID_KEY);
		news1Background.setImageResource(this.newsBackgroundsIds[backgroundNumber]);
	}
	
	private void setNewsBackgroundsNames() {
		this.newsBackgroundsIds = new int[12];
		
		this.newsBackgroundsIds[0] = R.drawable.news_background_0;
		this.newsBackgroundsIds[1] = R.drawable.news_background_1;
		this.newsBackgroundsIds[2] = R.drawable.news_background_2;
		this.newsBackgroundsIds[3] = R.drawable.news_background_3;
		this.newsBackgroundsIds[4] = R.drawable.news_background_4;
		this.newsBackgroundsIds[5] = R.drawable.news_background_5;
		this.newsBackgroundsIds[6] = R.drawable.news_background_6;
		this.newsBackgroundsIds[7] = R.drawable.news_background_7;
		this.newsBackgroundsIds[8] = R.drawable.news_background_8;
		this.newsBackgroundsIds[9] = R.drawable.news_background_9;
		this.newsBackgroundsIds[10] = R.drawable.news_background_10;
		this.newsBackgroundsIds[11] = R.drawable.news_background_11;
	}
	
	private boolean areRSSAndTweetsUpToDate() {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		if (!sharedPreferences.contains(Global.RSS_AND_TWEETS_DOWNLOADED_DATE_KEY)) {
			return false;
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		Date downloadedDate;
		try {
			downloadedDate = dateFormat.parse(sharedPreferences.getString(Global.RSS_AND_TWEETS_DOWNLOADED_DATE_KEY, "01.01.1970 00:00"));
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Date parsing error in reading rss and tweets");
			
			return false;
		}
		
		Date date50MinutesAgo = new Date(System.currentTimeMillis() - MainScreenFragment.MINUTES_50);
		if (downloadedDate.before(date50MinutesAgo)) {
			return false;
		}
		
		return true;
	}
	
	private boolean isOnline() {
	    ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	 
	    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
	    return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
	}
	
	private void showNoInternetToast() {
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.main_screen_no_internet, Toast.LENGTH_LONG);
        toast.show();
    }
	
	private void addRSSAndTweetsDownloaderFragment() {
		FragmentManager fragmentManager = getChildFragmentManager();
		RSSAndTweetsDownloaderFragment rssAndTweetsDownloaderFragment = new RSSAndTweetsDownloaderFragment();
		rssAndTweetsDownloaderFragment.setArguments(createArgumentsForRSSAndTweetsDownloaderFragment());
		
		try {
			fragmentManager.beginTransaction().add(rssAndTweetsDownloaderFragment, RSSAndTweetsDownloaderFragment.TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private void downloadRSSAndTweetsAndShowThem() {
		addRSSAndTweetsDownloaderFragment();
	}
	
	private Bundle createArgumentsForRSSAndTweetsDownloaderFragment() {
		Bundle arguments = new Bundle();
		arguments.putString(RSSAndTweetsDownloaderFragment.ARGUMENTS_PARENT_NAME_KEY, RSSAndTweetsDownloaderFragment.ARGUMENTS_PARENT_NAME_MAIN_SCREEN);
		
		return arguments;
	}
	
	private void setRSSAndTweets() {
		if (!areRSSAndTweetsUpToDate()) {
			if (!isOnline()) {
				showNoInternetToast();
				setRSSTweetsAndShowThem();
				return;
			}
			setRSSTweetsAndShowThem();
			downloadRSSAndTweetsAndShowThem();
		} else {
			setRSSTweetsAndShowThem();
		}
	}
	
	private void removeServerErrorDialogFragment() {
		FragmentManager fragmentManager = getChildFragmentManager();
		DialogFragment serverErrorDialogFragment = (DialogFragment) fragmentManager.findFragmentByTag(ServerErrorDialogFragment.TAG);
		if (serverErrorDialogFragment != null) {
			try {
				fragmentManager.beginTransaction().remove(serverErrorDialogFragment).commit();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void showServerError() {
		ServerErrorDialogFragment serverErrorDialogFragment = new ServerErrorDialogFragment();
		serverErrorDialogFragment.setArguments(createArgumentsForServerErrorDialogFragment());
		serverErrorDialogFragment.setCancelable(false);
		FragmentManager fragmentManager = getChildFragmentManager();
		
		try {
			fragmentManager.beginTransaction().add(serverErrorDialogFragment, ServerErrorDialogFragment.TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private Bundle createArgumentsForServerErrorDialogFragment() {
		Bundle arguments = new Bundle();
		arguments.putString(ServerErrorDialogFragment.MESSAGE_KEY, getResources().getString(R.string.main_screen_server_error_message));
		
		return arguments;
	}
	
	private void setRSSTweetsAndShowThem() {
		ArrayList<RSSItem> rssItems;
		ArrayList<Tweet> tweets;
		
		rssItems = getRSSFromDatabase();
		tweets = getTweetsFromDatabase();
		
		setNews0(rssItems);
		setNews1(rssItems);
		setTweet(tweets);
	}
	
	private int getRSSItemIndexForNews0() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return 0;
		} else {
			return 2;
		}
	}
	
	private void setNews0(ArrayList<RSSItem> rssItems) {
		if (rssItems.size() < getRSSItemIndexForNews0() + 1) {
			return;
		}
		
		CustomTextView news0Title = (CustomTextView) this.rootView.findViewById(R.id.main_screen_layout_news_0_title);
		if (news0Title == null) {
			return;
		}
		
		if (rssItems.get(getRSSItemIndexForNews0()) == null) {
			return;
		}
		
		news0Title.setText(rssItems.get(getRSSItemIndexForNews0()).getTitle());
		this.news0 = rssItems.get(getRSSItemIndexForNews0());
	}
	
	private void setNews1(ArrayList<RSSItem> rssItems) {
		if (getOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
			return;
		}
		
		if (rssItems.size() < 2) {
			return;
		}
		
		CustomTextView news1Title = (CustomTextView) this.rootView.findViewById(R.id.main_screen_layout_news_1_title);
		if (news1Title == null) {
			return;
		}
		
		if (rssItems.get(1) == null) {
			return;
		}
		
		news1Title.setText(rssItems.get(1).getTitle());
		this.news1 = rssItems.get(1);
	}
	
	private int getTweetIndex() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return 0;
		} else {
			return 1;
		}
	}
	
	private void setTweet(ArrayList<Tweet> tweets) {
		if (tweets.size() < getTweetIndex() + 1) {
			return;
		}
		
		
		CustomTextView tweetTitle = (CustomTextView) this.rootView.findViewById(R.id.main_screen_layout_tweet_title);
		if (tweetTitle == null) {
			return;
		}
		
		if (tweets.get(getTweetIndex()) == null) {
			return;
		}
		
		tweetTitle.setText(tweets.get(getTweetIndex()).getText());
		this.tweet = tweets.get(getTweetIndex());
	}
	
	private String getLanguageRSSForDatabaseQuery() {
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			return RSSItem.LANGUAGE_ENGLISH;
		} else {
			return RSSItem.LANGUAGE_POLISH;
		}
	}
	
	private ArrayList<RSSItem> getRSSFromDatabase() {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
		ArrayList<RSSItem> rssItems = databaseAdapter.getRSSItems(MainScreenFragment.RSS_ITEMS_FROM_DATABASE_LIMIT, getLanguageRSSForDatabaseQuery());
		
		return rssItems;
	}
	
	private ArrayList<Tweet> getTweetsFromDatabase() {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
		ArrayList<Tweet> tweets = databaseAdapter.getTweets(MainScreenFragment.TWEETS_FROM_DATABASE_LIMIT);
		
		return tweets;
	}
	
	private int getOrientation() {
		return getResources().getConfiguration().orientation;
	}
}
