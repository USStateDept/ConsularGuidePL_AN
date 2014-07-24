package com.agitive.usembassy.fragments.layoutFragments;

import java.util.ArrayList;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.databases.ArticleLayout;
import com.agitive.usembassy.databases.DatabaseAdapter;
import com.agitive.usembassy.databases.DatabaseReader;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.interfaces.NewsRightColumnInterface;
import com.agitive.usembassy.layouts.CustomTextView;
import com.agitive.usembassy.objects.RSSItem;
import com.agitive.usembassy.objects.Tweet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class NewsRightColumnFragment extends Fragment implements NewsRightColumnInterface {
	
	public static final String NEWS_0_BACKGROUND_ID_KEY = "com.agitive.usembassy.fragments.layoutFragments.NewsRightColumnFragment.news0BackgroundId";
	public static final String NEWS_1_BACKGROUND_ID_KEY = "com.agitive.usembassy.fragments.layoutFragments.NewsRightColumnFragment.news1BackgroundId";
	
	private static final int RSS_ITEMS_FROM_DATABASE_LIMIT = 2;
	private static final int TWEETS_FROM_DATABASE_LIMIT = 1;
	private static final double NEWS_LAYOUT_HEIGHT_TO_ROOT_VIEW_HEIGHT = 1.0/3.0;
	private static final double NEWS_TITLE_HEIGHT_TO_NEWS_LAYOUT_WIDTH = 0.064;
	private static final double TWEET_TITLE_HEIGHT_TO_TWEET_LAYOUT_WIDTH = 0.064;
	
	private View rootView;
	private RSSItem news0;
	private RSSItem news1;
	private Tweet tweet;
	private int[] newsBackgroundsIds;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return null;
		}
		
		this.rootView = inflater.inflate(R.layout.news_right_column_layout, container, false);
		 
		setNewsAndTweetTitlesSize();
		setNewsBackgroundsIds();
		setNewsBackgrounds();
		setOnClickListenersForNewsAndTweet();
		setNewsAndTweet();
		 
		return this.rootView;
	}
	
	@Override
	public void updateNewsAndTweets() {
		setNewsAndTweet();
	}
	
	@Override
	public void changeLanguage() {
		setNewsAndTweet();
	}
	
	private void setNewsAndTweetTitlesSize() {
		setNews0TitleHeight();
		setNews1TitleHeight();
		setTweetTitleHeight();
	}
	
	private void setOnClickListenersForNewsAndTweet() {
		setOnClickListenerForNews0();
		setOnClickListenerForNews1();
		setOnClickListenerForTweet();
	}
	
	private void setOnClickListenerForTweet() {
		RelativeLayout tweetLayout = (RelativeLayout) this.rootView.findViewById(R.id.news_right_column_layout_tweet_layout);
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
	
	private boolean isOnline() {
	    ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	 
	    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
	    return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
	}
	
	private void showNoInternetForTweetToast() {
		Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.main_screen_no_internet_for_tweet, Toast.LENGTH_LONG);
        toast.show();
	}
	
	private void setOnClickListenerForNews0() {
		RelativeLayout news0Layout = (RelativeLayout) this.rootView.findViewById(R.id.news_right_column_layout_news_0_layout);
		if (news0Layout == null) {
			return;
		}
		
		news0Layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (news0 == null) {
					return;
				}
				
				((MainActivity)getActivity()).openArticleLayout(createArticleLayout(), createArgumentsForArticleLayout(0, getArguments().getInt(NewsRightColumnFragment.NEWS_0_BACKGROUND_ID_KEY), false));
			}
			
		});
	}
	
	private void setOnClickListenerForNews1() {
		RelativeLayout news1Layout = (RelativeLayout) this.rootView.findViewById(R.id.news_right_column_layout_news_1_layout);
		if (news1Layout == null) {
			return;
		}
		
		news1Layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(news1 == null) {
					return;
				}
				
				((MainActivity)getActivity()).openArticleLayout(createArticleLayout(), createArgumentsForArticleLayout(1, getArguments().getInt(NewsRightColumnFragment.NEWS_1_BACKGROUND_ID_KEY), false));
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
		arguments.putInt(ArticleFragment.ARTICLE_LANDSCAPE_HEIGHT_KEY, (int) (this.rootView.getHeight() * NewsRightColumnFragment.NEWS_LAYOUT_HEIGHT_TO_ROOT_VIEW_HEIGHT));
		arguments.putBoolean(ArticleFragment.ARTICLE_LAYOUT_BACKGROUND_ANIMATE_KEY, animateBackground);
		
		return arguments;
	}
	
	private String getNewsTitle(int newsNumber) {
		if (newsNumber == 0) {
			return this.news0.getTitle();
		} else if (newsNumber == 1) {
			return this.news1.getTitle();
		}
		
		return "";
	}
	
	private String getNewsDate(int newsNumber) {
		if (newsNumber == 0) {
			return this.news0.getSubtitle();
		} else if (newsNumber == 1) {
			return this.news1.getSubtitle();
		}
		
		return "";
	}
	
	private String getNewsText(int newsNumber) {
		if (newsNumber == 0) {
			return this.news0.getText();
		} else if (newsNumber == 1) {
			return this.news1.getText();
		}
		
		return "";
	}
	
	private void setNewsBackgroundsIds() {
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
	
	private void setNews0Background() {
		ImageView news0Background = (ImageView) this.rootView.findViewById(R.id.news_right_column_layout_news_0_background);
		if (news0Background == null) {
			return;
		}
		
		int backgroundNumber = getArguments().getInt(NewsRightColumnFragment.NEWS_0_BACKGROUND_ID_KEY);
		news0Background.setImageResource(this.newsBackgroundsIds[backgroundNumber]);
	}
	
	private void setNews1Background() {
		ImageView news1Background = (ImageView) this.rootView.findViewById(R.id.news_right_column_layout_news_1_background);
		if (news1Background == null) {
			return;
		}
		
		int backgroundNumber = getArguments().getInt(NewsRightColumnFragment.NEWS_1_BACKGROUND_ID_KEY);
		
		news1Background.setImageResource(this.newsBackgroundsIds[backgroundNumber]);
	}
	
	private void setNewsBackgrounds() {
		setNews0Background();
		setNews1Background();
	}
	
	private void setNewsAndTweet() {
		ArrayList<RSSItem> rssItems = getRSSItemsFromDatabase();
		ArrayList<Tweet> tweets = getTweetsFromDatabase();
		
		setNews0(rssItems);
		setNews1(rssItems);
		setTweet(tweets);
	}
	
	private void setTweet(ArrayList<Tweet> tweets) {
		if (tweets.size() < 1) {
			return;
		}
		
		this.tweet = tweets.get(0);
		CustomTextView tweetTitle = (CustomTextView) this.rootView.findViewById(R.id.news_right_column_layout_tweet_title);
		if (tweetTitle == null) {
			return;
		}
		
		if (this.tweet == null) {
			return;
		}
		
		tweetTitle.setText(this.tweet.getText());
	}
	
	private void setNews0(ArrayList<RSSItem> rssItems) {
		if (rssItems.size() < 1) {
			return;
		}
		
		this.news0 = rssItems.get(0);
		CustomTextView news0Title = (CustomTextView) this.rootView.findViewById(R.id.news_right_column_layout_news_0_title);
		if (news0Title == null) {
			return;
		}
		
		news0Title.setText(this.news0.getTitle());
	}
	
	private void setNews1(ArrayList<RSSItem> rssItems) {
		if (rssItems.size() < 2) {
			return;
		}
		
		this.news1 = rssItems.get(1);
		CustomTextView news1Title = (CustomTextView) this.rootView.findViewById(R.id.news_right_column_layout_news_1_title);
		
		if (news1Title == null) {
			return;
		}
		news1Title.setText(this.news1.getTitle());
	}
	
	private ArrayList<RSSItem> getRSSItemsFromDatabase() {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
		ArrayList<RSSItem> rssItems = databaseAdapter.getRSSItems(NewsRightColumnFragment.RSS_ITEMS_FROM_DATABASE_LIMIT, getLanguageRSSForDatabaseQuery());
		
		return rssItems;
	}
	
	private ArrayList<Tweet> getTweetsFromDatabase() {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
		ArrayList<Tweet> tweets = databaseAdapter.getTweets(NewsRightColumnFragment.TWEETS_FROM_DATABASE_LIMIT);
		
		return tweets;
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
	
	private void setNews0TitleHeight() {
		final RelativeLayout news0Layout = (RelativeLayout) this.rootView.findViewById(R.id.news_right_column_layout_news_0_layout);
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
				
				CustomTextView news0Title = (CustomTextView) rootView.findViewById(R.id.news_right_column_layout_news_0_title);
				if (news0Title == null) {
					return;
				}
				
				news0Title.setTextSize(MainActivity.pxToSp((int) (news0Layout.getWidth() * NewsRightColumnFragment.NEWS_TITLE_HEIGHT_TO_NEWS_LAYOUT_WIDTH), getActivity()));
			}
			
		});
	}
	
	private void setTweetTitleHeight() {
		final RelativeLayout tweetLayout = (RelativeLayout) this.rootView.findViewById(R.id.news_right_column_layout_tweet_layout);
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
				
				CustomTextView tweetTitle = (CustomTextView) rootView.findViewById(R.id.news_right_column_layout_tweet_title);
				if (tweetTitle == null) {
					return;
				}
				
				tweetTitle.setTextSize(MainActivity.pxToSp((int) (tweetLayout.getWidth() * NewsRightColumnFragment.TWEET_TITLE_HEIGHT_TO_TWEET_LAYOUT_WIDTH), getActivity()));
			}
			
		});
	}
	
	private void setNews1TitleHeight() {
		final RelativeLayout news1Layout = (RelativeLayout) this.rootView.findViewById(R.id.news_right_column_layout_news_1_layout);
		if (news1Layout == null) {
			return;
		}
		
		ViewTreeObserver news1LayoutObserver = news1Layout.getViewTreeObserver();
		news1LayoutObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			private boolean ready = false;
			
			@Override
			public void onGlobalLayout() {
				if (this.ready) {
					return;
				}
				this.ready = true;
				
				CustomTextView news1Title = (CustomTextView) rootView.findViewById(R.id.news_right_column_layout_news_1_title);
				if (news1Title == null) {
					return;
				}
				
				news1Title.setTextSize(MainActivity.pxToSp((int) (news1Layout.getWidth() * NewsRightColumnFragment.NEWS_TITLE_HEIGHT_TO_NEWS_LAYOUT_WIDTH), getActivity()));
			}
			
		});
	}
	
	private int getOrientation() {
		return getResources().getConfiguration().orientation;
	}
}
