package com.agitive.usembassy.adapters;

import java.util.ArrayList;
import java.util.Collections;
import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.databases.ArticleLayout;
import com.agitive.usembassy.databases.DatabaseReader;
import com.agitive.usembassy.fragments.layoutFragments.ArticleFragment;
import com.agitive.usembassy.layouts.CustomTextView;
import com.agitive.usembassy.layouts.NewsLayout;
import com.agitive.usembassy.layouts.TweetLayout;
import com.agitive.usembassy.objects.RSSItem;
import com.agitive.usembassy.objects.Tweet;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

public class NewsAndTweetsAdapter extends BaseAdapter {

	private static final int NEWS_IN_ROW_PORTRAIT = 2;
	private static final int NEWS_IN_ROW_LANDSCAPE = 3;
	private static final double NEWS_TITLE_HEIGHT_TO_NEWS_LAYOUT_WIDTH = 0.064;
	private static final int BACKGROUNDS_NUMBERS = 12;
	
	private ArrayList<RSSItem> rssItems;
	private ArrayList<Tweet> tweets;
	private Context context;
	private Activity activity;
	private ArrayList<Integer> availableNewsBackgroundsIds;
	private int[] randedNewsBackgroundsIds;
	
	public NewsAndTweetsAdapter(ArrayList<RSSItem> rssItems, ArrayList<Tweet> tweets, Context context, Activity activity) {
		this.rssItems = rssItems;
		this.tweets = tweets;
		this.context = context;
		this.activity = activity;
		this.randedNewsBackgroundsIds = new int[this.rssItems.size()];
		setNewsBackgroundsNames();
		randAllNewsBackgrounds();
	}
	
	@Override
	public int getCount() {
		return this.rssItems.size() / getNewsInRow() * 2;
		
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position % 2 == 0) {
			return getNewsView(position);
		} else {
			return getTweetView(position);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void randAllNewsBackgrounds() {
		if (this.rssItems.size() <= NewsAndTweetsAdapter.BACKGROUNDS_NUMBERS) {
			ArrayList<Integer> shuffledBackgroundsInd = (ArrayList<Integer>) this.availableNewsBackgroundsIds.clone();
			Collections.shuffle(shuffledBackgroundsInd);
			
			for (int index = 0; index < this.rssItems.size(); ++index) {
				this.randedNewsBackgroundsIds[index] = shuffledBackgroundsInd.get(index); 
			}
		} else {
			ArrayList<Integer> shuffledBackgroundsInd = (ArrayList<Integer>) this.availableNewsBackgroundsIds.clone();
			Collections.shuffle(shuffledBackgroundsInd);
			
			for (int newsIndex = 0; newsIndex < this.rssItems.size(); newsIndex += NewsAndTweetsAdapter.BACKGROUNDS_NUMBERS) {
				for (int backgroundIndex = 0; backgroundIndex < NewsAndTweetsAdapter.BACKGROUNDS_NUMBERS; ++backgroundIndex) {
					if (newsIndex + backgroundIndex == this.rssItems.size()) {
						return;
					}
					this.randedNewsBackgroundsIds[newsIndex + backgroundIndex] = shuffledBackgroundsInd.get(backgroundIndex);
				}
				
				shuffledBackgroundsInd = (ArrayList<Integer>) this.availableNewsBackgroundsIds.clone();
				Collections.shuffle(shuffledBackgroundsInd);
			}
		}
	}
	
	private View getNewsView(int rowPosition) {
		View row;
		LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		row = layoutInflater.inflate(R.layout.news_row, null);
		
		setNews0Background(row, getNews0IndexByRowPosition(rowPosition));
		setNews0Title(row, getNews0IndexByRowPosition(rowPosition));
		setNews0TitleHeight(row);
		
		setNews1Background(row, getNews1IndexByRowPosition(rowPosition));
		setNews1Title(row, getNews1IndexByRowPosition(rowPosition));
		setNews1TitleHeight(row);
		
		setNews2Background(row, getNews2IndexByRowPosition(rowPosition));
		setNews2Title(row, getNews2IndexByRowPosition(rowPosition));
		setNews2TitleHeight(row);
		
		setOnClickListenerForNews0(row, getNews0IndexByRowPosition(rowPosition));
		setOnClickListenerForNews1(row, getNews1IndexByRowPosition(rowPosition));
		setOnClickListenerForNews2(row, getNews2IndexByRowPosition(rowPosition));
		
		return row;
	}
	
	private int getNews0IndexByRowPosition(int rowPosition) {
		return getNewsInRow() * (rowPosition / 2);
	}
	
	private int getNews1IndexByRowPosition(int rowPosition) {
		return getNewsInRow() * (rowPosition / 2) + 1;
	}
	
	private int getNews2IndexByRowPosition(int rowPosition) {
		return getNewsInRow() * (rowPosition / 2) + 2;
	}
	
	private void setOnClickListenerForNews0(View row, final int newsIndex) {
		final NewsLayout news0Layout = (NewsLayout) row.findViewById(R.id.news_row_layout_news_0_layout);
		if (news0Layout == null) {
			return;
		}
		
		news0Layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DatabaseReader databaseReader = new DatabaseReader(activity);
				
				ArticleLayout layout = new ArticleLayout(databaseReader.getNewsId());
				Bundle arguments = createArgumentsForArticleFragment(newsIndex, news0Layout);
				((MainActivity)activity).openArticleLayout(layout, arguments);
			}
			
		});
	}
	
	private Bundle createArgumentsForArticleFragment(int newsIndex, NewsLayout newsLayout) {
		Bundle arguments = new Bundle();
		arguments.putString(ArticleFragment.ARTICLE_TITLE_KEY, rssItems.get(newsIndex).getTitle());
		arguments.putString(ArticleFragment.ARTICLE_DATE_KEY, rssItems.get(newsIndex).getSubtitle());
		arguments.putString(ArticleFragment.ARTICLE_TEXT_KEY, rssItems.get(newsIndex).getText());
		arguments.putInt(ArticleFragment.ARTICLE_LANDSCAPE_ID_KEY, getBackgroundIdForArticleLayout(randedNewsBackgroundsIds[newsIndex]));
		arguments.putInt(ArticleFragment.ARTICLE_LANDSCAPE_HEIGHT_KEY, newsLayout.getHeight());
		arguments.putBoolean(ArticleFragment.ARTICLE_LAYOUT_BACKGROUND_ANIMATE_KEY, false);
		
		return arguments;
	}
	
	private void setOnClickListenerForNews1(View row, final int newsIndex) {
		final NewsLayout news1Layout = (NewsLayout) row.findViewById(R.id.news_row_layout_news_1_layout);
		if (news1Layout == null) {
			return;
		}
		
		news1Layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DatabaseReader databaseReader = new DatabaseReader(activity);
				
				ArticleLayout layout = new ArticleLayout(databaseReader.getNewsId());
				Bundle arguments = createArgumentsForArticleFragment(newsIndex, news1Layout);
				((MainActivity)activity).openArticleLayout(layout, arguments);
			}
			
		});
	}
	
	private void setOnClickListenerForNews2(View row, final int newsIndex) {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return;
		}
		
		final NewsLayout news2Layout = (NewsLayout) row.findViewById(R.id.news_row_layout_news_2_layout);
		if (news2Layout == null) {
			return;
		}
		
		news2Layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DatabaseReader databaseReader = new DatabaseReader(activity);
				
				ArticleLayout layout = new ArticleLayout(databaseReader.getNewsId());
				Bundle arguments = createArgumentsForArticleFragment(newsIndex, news2Layout);
				((MainActivity)activity).openArticleLayout(layout, arguments);
			}
			
		});
	}
	
	private int getBackgroundIdForArticleLayout(int resourceId) {
		int index = 0;
		for (Integer id: this.availableNewsBackgroundsIds) {
			if (id == resourceId) {
				return index; 
			}
			
			++index;
		}
		
		return -1;
	}
	
	private void setNews0Background(View row, int newsIndex) {
		ImageView landscape0 = (ImageView) row.findViewById(R.id.news_row_layout_landscape_0);
		if (landscape0 == null) {
			return;
		}
		
		Picasso.with(this.context).load(this.randedNewsBackgroundsIds[newsIndex]).placeholder(R.drawable.news_placeholder).into(landscape0);
	}
	
	private void setNews1Background(View row, int newsIndex) {
		ImageView landscape1 = (ImageView) row.findViewById(R.id.news_row_layout_landscape_1);
		if (landscape1 == null) {
			return;
		}
		
		Picasso.with(this.context).load(this.randedNewsBackgroundsIds[newsIndex]).placeholder(R.drawable.news_placeholder).into(landscape1);
	}
	
	private void setNews2Background(View row, int newsIndex) {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return;
		}
		
		ImageView landscape2 = (ImageView) row.findViewById(R.id.news_row_layout_landscape_2);
		if (landscape2 == null) {
			return;
		}
		
		Picasso.with(this.context).load(this.randedNewsBackgroundsIds[newsIndex]).placeholder(R.drawable.news_placeholder).into(landscape2);
	}
	
	private void setNewsBackgroundsNames() {
		this.availableNewsBackgroundsIds = new ArrayList<Integer>();
		
		
		this.availableNewsBackgroundsIds.add(R.drawable.news_background_0);
		this.availableNewsBackgroundsIds.add(R.drawable.news_background_1);
		this.availableNewsBackgroundsIds.add(R.drawable.news_background_2);
		this.availableNewsBackgroundsIds.add(R.drawable.news_background_3);
		this.availableNewsBackgroundsIds.add(R.drawable.news_background_4);
		this.availableNewsBackgroundsIds.add(R.drawable.news_background_5);
		this.availableNewsBackgroundsIds.add(R.drawable.news_background_6);
		this.availableNewsBackgroundsIds.add(R.drawable.news_background_7);
		this.availableNewsBackgroundsIds.add(R.drawable.news_background_8);
		this.availableNewsBackgroundsIds.add(R.drawable.news_background_9);
		this.availableNewsBackgroundsIds.add(R.drawable.news_background_10);
		this.availableNewsBackgroundsIds.add(R.drawable.news_background_11);
	}
	
	private void setNews0TitleHeight(View row) {
		CustomTextView news0Title = (CustomTextView) row.findViewById(R.id.news_row_layout_news_0_title);
		if (news0Title == null) {
			return;
		}
		
		news0Title.setTextSize(MainActivity.pxToSp(getTitleHeight(), this.context.getApplicationContext()));
	}
	
	private void setNews1TitleHeight(View row) {
		CustomTextView news1Title = (CustomTextView) row.findViewById(R.id.news_row_layout_news_1_title);
		if (news1Title == null) {
			return;
		}
		
		news1Title.setTextSize(MainActivity.pxToSp(getTitleHeight(), this.context.getApplicationContext()));
	}
	
	private void setNews2TitleHeight(View row) {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return;
		}
		
		CustomTextView news2Title = (CustomTextView) row.findViewById(R.id.news_row_layout_news_2_title);
		if (news2Title == null) {
			return;
		}
		
		news2Title.setTextSize(MainActivity.pxToSp(getTitleHeight(), this.context.getApplicationContext()));
	}
	
	private int getTitleHeight() {
		return (int) (1.0 * getDisplayWidth() / getNewsInRow() * NewsAndTweetsAdapter.NEWS_TITLE_HEIGHT_TO_NEWS_LAYOUT_WIDTH);
	}
	
	@SuppressWarnings("deprecation")
	private int getDisplayWidth() {
		Display display = this.activity.getWindowManager().getDefaultDisplay();
		
		if (Build.VERSION.SDK_INT < 13) {
			return display.getWidth();
		} else {
			Point size = new Point();
			display.getSize(size);
			
			return size.x;
		}
	}
	
	private int getNewsInRow() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return NewsAndTweetsAdapter.NEWS_IN_ROW_PORTRAIT;
		} else {
			return NewsAndTweetsAdapter.NEWS_IN_ROW_LANDSCAPE;
		}
	}
	
	private void setNews0Title(View row, int newsIndex) {
		CustomTextView news0Title = (CustomTextView) row.findViewById(R.id.news_row_layout_news_0_title);
		if (news0Title == null) {
			return;
		}
		if (newsIndex >= this.rssItems.size()) {
			return;
		}
		
		news0Title.setText(this.rssItems.get(newsIndex).getTitle());
	}
	
	private void setNews1Title(View row, int newsIndex) {
		CustomTextView news1Title = (CustomTextView) row.findViewById(R.id.news_row_layout_news_1_title);
		if (news1Title == null) {
			return;
		}
		if (newsIndex >= this.rssItems.size()) {
			return;
		}
		
		news1Title.setText(this.rssItems.get(newsIndex).getTitle());
	}
	
	private void setNews2Title(View row, int newsIndex) {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return;
		}
		
		CustomTextView news2Title = (CustomTextView) row.findViewById(R.id.news_row_layout_news_2_title);
		if (news2Title == null) {
			return;
		}
		if (newsIndex >= this.rssItems.size()) {
			return;
		}
		
		news2Title.setText(this.rssItems.get(newsIndex).getTitle());
	}
	
	private View getTweetView(int rowPosition) {
		View row;
		LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		row = layoutInflater.inflate(R.layout.tweet_row, null);
		
		setTweetTitle(row, rowPosition / 2);
		setTweetTitleHeight(row);
		setTweetOnClickListener(row, rowPosition / 2);
		
		return row;
	}
	
	private void setTweetOnClickListener(View row, final int tweetIndex) {
		TweetLayout tweetLayout = (TweetLayout) row.findViewById(R.id.tweet_row_layout_tweet_layout);
		if (tweetLayout == null) {
			return;
		}
		
		tweetLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isOnline()) {
					showNoInternetForTweetToast();
					return;
				}
				
				Intent twitterAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://status?status_id=" + tweets.get(tweetIndex).getId()));
		    	try {
		    		context.startActivity(twitterAppIntent);
		    	} catch (Exception e) {
		    		Intent webBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/USEmbassyWarsaw/status/" + tweets.get(tweetIndex).getId()));
					context.startActivity(webBrowserIntent);
		    	}
			}			
		});
	}
	
	private void showNoInternetForTweetToast() {
		Toast toast = Toast.makeText(this.context.getApplicationContext(), R.string.main_screen_no_internet_for_tweet, Toast.LENGTH_LONG);
        toast.show();
	}
	
	private boolean isOnline() {
	    ConnectivityManager connectivityManager = (ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
	 
	    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
	    return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
	}
	
	private void setTweetTitle(View row, int tweetIndex) {
		CustomTextView title = (CustomTextView) row.findViewById(R.id.tweet_row_layout_title);
		if (title == null) {
			return;
		}
		if (tweetIndex >= this.tweets.size()) {
			return;
		}
		
		title.setText(this.tweets.get(tweetIndex).getText());
	}
	
	private void setTweetTitleHeight(View row) {
		CustomTextView title = (CustomTextView) row.findViewById(R.id.tweet_row_layout_title);
		if (title == null) {
			return;
		}
		
		title.setTextSize(MainActivity.pxToSp(getTitleHeight(), this.context.getApplicationContext()));
	}
	
	private int getOrientation() {
		return this.context.getApplicationContext().getResources().getConfiguration().orientation;
	}
}
