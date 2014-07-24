package com.agitive.usembassy.fragments.layoutFragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.adapters.NewsAndTweetsAdapter;
import com.agitive.usembassy.databases.AllNewsLayout;
import com.agitive.usembassy.databases.DatabaseAdapter;
import com.agitive.usembassy.databases.DatabaseReader;
import com.agitive.usembassy.fragments.asyncTaskFragments.RSSAndTweetsDownloaderFragment;
import com.agitive.usembassy.fragments.dialogFragments.ServerErrorDialogFragment;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.interfaces.FragmentAboveInterface;
import com.agitive.usembassy.interfaces.LayoutFragmentInterface;
import com.agitive.usembassy.layouts.CustomTextView;
import com.agitive.usembassy.objects.RSSItem;
import com.agitive.usembassy.objects.Tweet;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class NewsFragment extends Fragment implements LayoutFragmentInterface, FragmentAboveInterface {

	public static final String DOWNLOADED_DATE_KEY = "RSSANdTweetsDownloadedDate";
	public static final String LAYOUT_ID_KEY = "com.agitive.usembassy.fragments.NewsFragment.layoutIdKey";
	
	private static final int MINUTES_50 = 3000000;
	private static final String SAVED_INSTANCE_STATE_SYNC_TEXT_VISIBILITY_KEY = "com.agitive.usembassy.fragments.layoutFragments.NewsFragment.savedInstanceStateSyncTextVisibilityKey";
	
	private View rootView;
	private int[] newsBackgroundsIds;
	private AllNewsLayout allNewsLayout;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        this.rootView = inflater.inflate(R.layout.news_layout, container, false);
        setLayout();
		setMarginForEmblem();
        setLayoutName();
        setBackButton();
        
        setNewsBackgroundsNames();
        setContent(savedInstanceState);
        restoreInstanceState(savedInstanceState);
        
        return this.rootView;
	}
	
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(NewsFragment.SAVED_INSTANCE_STATE_SYNC_TEXT_VISIBILITY_KEY, getSyncTextVisibility());
    }

	
	@Override
	public void changeLanguage() {
		setSyncTextLanguage();
		showMainProgressBar();
		
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				setLayoutName();
				setContent();
			}
			
		});
    }
	
	@Override
	public void setContent() {
		if (isRSSAndTweetsDownloaderRunning()) {
			return;
		}
		
		if (!areRSSAndTweetsUpToDate()) {
			if (!isOnline()) {
				showNoInternetToast();
				hideMainProgressBar();
				setRSSTweetsAndShowThem();
				return;
			}
			setSyncTextVisibility(View.VISIBLE);
			downloadRSSAndTweetsAndShowThem();
		} else {
			hideMainProgressBar();
			setRSSTweetsAndShowThem();
		}
	}
	
	public View getRootView() {
		return this.rootView;
	}
	
	private void setSyncTextLanguage() {
		CustomTextView syncText = (CustomTextView) this.rootView.findViewById(R.id.news_layout_sync_text);
		syncText.setText(R.string.news_fragment_sync);
	}
	
	private boolean isRSSAndTweetsDownloaderRunning() {
		FragmentManager fragmentManager = getChildFragmentManager();
		Fragment fragment = fragmentManager.findFragmentByTag(RSSAndTweetsDownloaderFragment.TAG);
		
		return (fragment != null);
	}
	
	private void restoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		}
		
		setSyncTextVisibility(savedInstanceState.getInt(NewsFragment.SAVED_INSTANCE_STATE_SYNC_TEXT_VISIBILITY_KEY));
	}
	
	private int getSyncTextVisibility() {
		CustomTextView syncText = (CustomTextView) this.rootView.findViewById(R.id.news_layout_sync_text);
		
		return syncText.getVisibility();
	}
	
	private void setSyncTextVisibility(int visibility) {
		CustomTextView syncText = (CustomTextView) this.rootView.findViewById(R.id.news_layout_sync_text);
		syncText.setVisibility(visibility);
	}
	
	private void setContent(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		}
		
		setContent();
	}
	
	private void hideMainProgressBar() {
		RelativeLayout mainProgressBarLayout = (RelativeLayout) this.rootView.findViewById(R.id.news_layout_main_progress_bar_layout);
		if (mainProgressBarLayout == null) {
			return;
		}
		
		mainProgressBarLayout.setVisibility(RelativeLayout.INVISIBLE);
	}
	
	private void showMainProgressBar() {
		RelativeLayout mainProgressBarLayout = (RelativeLayout) this.rootView.findViewById(R.id.news_layout_main_progress_bar_layout);
		if (mainProgressBarLayout == null) {
			return;
		}
		
		mainProgressBarLayout.setVisibility(RelativeLayout.VISIBLE);
	}
	
	public void setRSSAndTweetsUpdateStatus(boolean isSuccess) {
		if (!isSuccess) {
			removeServerErrorDialogFragment();
			showServerError();
		}
		
		hideMainProgressBar();
		setRSSTweetsAndShowThem();
		((MainActivity)getActivity()).updateRSSAndTweetInNewsRightColumn();
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
	
	private void setRSSTweetsAndShowThem() {
		ArrayList<RSSItem> rssItems = getRSSItemsFromDatabase();
		ArrayList<Tweet> tweets = getTweetsFromDatabase();
		NewsAndTweetsAdapter newsAndTweetsAdapter = new NewsAndTweetsAdapter(rssItems, tweets, getActivity(), getActivity());
		
		ListView newsList = (ListView) this.rootView.findViewById(R.id.news_layout_news_list);
		if (newsList == null) {
			return;
		}
		
		newsList.setAdapter(newsAndTweetsAdapter);
	}
	
	private void setLayout() {
		DatabaseReader databaseReader = new DatabaseReader(getActivity());
		int id = getArguments().getInt(NewsFragment.LAYOUT_ID_KEY);
		this.allNewsLayout = (AllNewsLayout) databaseReader.getLayout(id);
	}
	
	private void setMarginForEmblem() {
		CustomTextView menuName = (CustomTextView) this.rootView.findViewById(R.id.news_layout_layout_name);
		if (menuName == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) menuName.getLayoutParams();
		params.topMargin = ((MainActivity)getActivity()).getEmblemPartOutsideSize();
		params.topMargin += getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_small);
		menuName.setLayoutParams(params);
	}
	
	private void setLayoutName() {
		CustomTextView layoutName = (CustomTextView) this.rootView.findViewById(R.id.news_layout_layout_name);
		if (layoutName == null) {
			return;
		}
		
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			layoutName.setText(this.allNewsLayout.getTitleEn());
		} else {
			layoutName.setText(this.allNewsLayout.getTitlePl());
		}
	}
	
	private void setBackButton() {
		ImageView backButton = (ImageView) this.rootView.findViewById(R.id.news_layout_back_button);
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
		
		Date date50MinutesAgo = new Date(System.currentTimeMillis() - NewsFragment.MINUTES_50);
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
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.news_fragment_no_internet, Toast.LENGTH_LONG);
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
	
	private Bundle createArgumentsForRSSAndTweetsDownloaderFragment() {
		Bundle arguments = new Bundle();
		arguments.putString(RSSAndTweetsDownloaderFragment.ARGUMENTS_PARENT_NAME_KEY, RSSAndTweetsDownloaderFragment.ARGUMENTS_PARENT_NAME_NEWS);
		
		return arguments;
	}
	
	private void downloadRSSAndTweetsAndShowThem() {
		addRSSAndTweetsDownloaderFragment();
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
	
	private ArrayList<RSSItem> getRSSItemsFromDatabase() {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
		ArrayList<RSSItem> rssItems = databaseAdapter.getAllRSSItems(getLanguageRSSForDatabaseQuery());
		
		return rssItems;
	}
	
	private String getLanguageRSSForDatabaseQuery() {
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			return RSSItem.LANGUAGE_ENGLISH;
		} else {
			return RSSItem.LANGUAGE_POLISH;
		}
	}
	
	private String getAppLanguage() {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		return sharedPreferences.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
	}
	
	private ArrayList<Tweet> getTweetsFromDatabase() {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
		ArrayList<Tweet> tweets = databaseAdapter.getAllTweets();
		
		return tweets;
	}
}
