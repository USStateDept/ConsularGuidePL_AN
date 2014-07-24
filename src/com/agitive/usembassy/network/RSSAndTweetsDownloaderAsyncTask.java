package com.agitive.usembassy.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.databases.DatabaseAdapter;
import com.agitive.usembassy.fragments.asyncTaskFragments.RSSAndTweetsDownloaderFragment;
import com.agitive.usembassy.fragments.layoutFragments.NewsFragment;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.objects.RSSItem;
import com.agitive.usembassy.objects.Tweet;
import com.agitive.usembassy.privateKeys.AgitivePrivateKeys;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class RSSAndTweetsDownloaderAsyncTask extends AsyncTask<Void, Void, Boolean> {
	
	private static final String METHOD_KEY = "method";
	private static final String METHOD_VALUE = "GetRssNews";
	private static final int SERVER_TIMEOUT = 3000;
	private static final String TWITTER_USER_NAME = "USEmbassyWarsaw";
	
	private Context context;
	public RSSAndTweetsDownloaderAsyncTask (Context context) {
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		
		HttpPost httpPost = createHttpPost();
		DefaultHttpClient httpClient = new DefaultHttpClient();
		setTimeout(httpClient);
		HttpResponse response = createResponse(httpClient, httpPost);
		if (response == null) {
			return false;
		}
		
		ArrayList<RSSItem> rssItems = parseRSS(response);
		
		ArrayList<Tweet> tweets = getTweets(rssItems.size());
		
		saveRSSInDatabase(rssItems);
		saveTweetsInDatabase(tweets);
		
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean isSuccess) {
		RSSAndTweetsDownloaderFragment.setUpdateStatus(isSuccess);
	}
	
	private void saveTweetsInDatabase(ArrayList<Tweet> tweets) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.context.getApplicationContext());
		
		databaseAdapter.deleteAllTweets();
		
		int index = 0;
		for (Tweet tweet: tweets) {
			databaseAdapter.insertTweet(tweet, index);
			
			++index;
		}
		
		SharedPreferences rssAndTweets = this.context.getApplicationContext().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = rssAndTweets.edit();
		
		Date dateNow = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		editor.putString(NewsFragment.DOWNLOADED_DATE_KEY, dateFormat.format(dateNow));
		
		editor.commit();
	}
	
	private void saveRSSInDatabase(ArrayList<RSSItem> rssItems) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.context.getApplicationContext());
		
		databaseAdapter.deleteAllRSSItems();
		
		int index = 0;
		for (RSSItem rssItem: rssItems) {
			databaseAdapter.insertRSS(rssItem, index);
			
			++index;
		}
		
		SharedPreferences rssAndTweets = this.context.getApplicationContext().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = rssAndTweets.edit();
		
		Date dateNow = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		editor.putString(NewsFragment.DOWNLOADED_DATE_KEY, dateFormat.format(dateNow));
		
		editor.commit();
	}
	
	private ArrayList<Tweet> getTweets(int count) {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setDebugEnabled(true);
		configurationBuilder.setOAuthConsumerKey(AgitivePrivateKeys.TWITTER_O_AUTH_CONSUMER_KEY);
		configurationBuilder.setOAuthConsumerSecret(AgitivePrivateKeys.TWITTER_O_AUTH_CONSUMER_SECRET);
		configurationBuilder.setOAuthAccessToken(AgitivePrivateKeys.TWITTER_O_AUTH_ACCESS_TOKEN);
		configurationBuilder.setOAuthAccessTokenSecret(AgitivePrivateKeys.TWITTER_O_AUTH_ACCESS_TOKEN_SECRET);
		
        TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
        Twitter twitter = twitterFactory.getInstance();
        
        ArrayList<Tweet> tweetsAll = new ArrayList<Tweet>();
        Paging page = new Paging(1);
        
        while (tweetsAll.size() < count) {
        	ResponseList<twitter4j.Status> tweetsPart = null;
        	
        	try {
        		tweetsPart = twitter.getUserTimeline(RSSAndTweetsDownloaderAsyncTask.TWITTER_USER_NAME, page);
    		} catch (TwitterException e) {
    			e.printStackTrace();
    			
    			Log.e(Global.TAG, "twitter error");
    			return new ArrayList<Tweet>();
    		}
            
            for (twitter4j.Status status : tweetsPart) {
                if (status.isRetweet()) {
                	continue;
                }
                
                Tweet tweet = new Tweet(status.getId());
                tweet.setText(status.getText());
                
                tweetsAll.add(tweet);
                
                if (tweetsAll.size() == count) {
                	break;
                }
            }
            
            page.setPage(page.getPage() + 1);
        }
        
        return tweetsAll;
	}
	
	private ArrayList<RSSItem> parseRSS(HttpResponse response) {
		ArrayList<RSSItem> parseResult = new ArrayList<RSSItem>();
		try {
			JSONObject responseObject = new JSONObject(EntityUtils.toString(response.getEntity()));
			
			if (!responseObject.isNull("error")) {
				Log.e(Global.TAG, "RSS news response error key not null");
				
				return parseResult;
			}
			
			JSONArray resultArray = responseObject.getJSONArray("result");
			for (int index = 0; index < resultArray.length(); ++index) {
				JSONObject rssObject = resultArray.getJSONObject(index);
				
				RSSItem rssItem = new RSSItem();
				if (rssObject.has("title")) {
					rssItem.setTitle(rssObject.getString("title"));
				}
				
				if (rssObject.has("subtitle")) {
					if (rssObject.isNull("subtitle")) {
						rssItem.setSubtitle(null);
					} else {
						rssItem.setSubtitle(rssObject.getString("subtitle"));
					}
				}
				
				if (rssObject.has("language")) {
					rssItem.setLanguage(rssObject.getString("language"));
				}
				
				if (rssObject.has("text")) {
					rssItem.setText(rssObject.getString("text"));
				}
				
				if (rssObject.has("description")) {
					rssItem.setDescription(rssObject.getString("description"));
				}
				
				parseResult.add(rssItem);
			}
		} catch (ParseException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "RSS error in parsing");
			
			return parseResult;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "RSS error in parsing");
			
			return parseResult;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "RSS error in parsing");
			
			return parseResult;
		}
		
		return parseResult;
	}
	
	private HttpPost createHttpPost() {
		ArrayList<NameValuePair> postKeys = new ArrayList<NameValuePair>();
		postKeys.add(new BasicNameValuePair(RSSAndTweetsDownloaderAsyncTask.METHOD_KEY, RSSAndTweetsDownloaderAsyncTask.METHOD_VALUE));
		
		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(postKeys, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			
			return null;
		}
		
		HttpPost httpPost = new HttpPost(RoundRobin.getInstance().getIP(this.context.getApplicationContext()) + Global.API_POST_ADDRESS);
		httpPost.setEntity(entity);
		
		return httpPost;
	}
	
	private void changeServer(HttpPost httpPost, String sufix) {
		String ip = RoundRobin.getInstance().getAnotherIP(this.context.getApplicationContext(), httpPost.getURI().getHost());
		httpPost.setURI(URI.create(ip + sufix));
	}
	
	private void setTimeout(DefaultHttpClient httpClient) {
		HttpParams httpParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, RSSAndTweetsDownloaderAsyncTask.SERVER_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, RSSAndTweetsDownloaderAsyncTask.SERVER_TIMEOUT);
		httpClient.setParams(httpParams);
	}
	
	private HttpResponse createResponse(DefaultHttpClient httpClient, HttpPost httpPost) {
		HttpResponse response = null;
		int timeouts = 0;
		while (true) {
			try {
				response = httpClient.execute(httpPost);
				
				break;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				
				Log.e(Global.TAG, "RSS response error");
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(Global.TAG, "RSS response error");
				
				++timeouts;
				if (timeouts == 2) {
					return null;
				}
				
				changeServer(httpPost, Global.API_POST_ADDRESS);
				
				continue;
			}
		}
		
		if (response == null ||
				response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			return null;
		}
		
		return response;
	}

}
