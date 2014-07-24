package com.agitive.usembassy.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;

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

import com.agitive.usembassy.fragments.layoutFragments.VideosFragment;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.objects.DayMonthYearDate;
import com.agitive.usembassy.objects.MostViewedVideosAndRecentVideosPair;
import com.agitive.usembassy.objects.VideoItem;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;

public class VideosDownloaderAsyncTask extends AsyncTask<Void, Void, MostViewedVideosAndRecentVideosPair> {

	private static final String METHOD_KEY = "method";
	private static final String METHOD_VALUE = "GetVideos";
	private static final String RESPONSE_ID_KEY = "id";
	private static final String RESPONSE_TITLE_EN_KEY = "title_en";
	private static final String RESPONSE_TITLE_PL_KEY = "title_pl";
	private static final String RESPONSE_DATE_KEY = "date";
	private static final String RESPONSE_DATE_SEPARATOR = "-";
	private static final int WIDTH_0 = 320;
	private static final int WIDTH_1 = 480;
	private static final int WIDTH_2 = 640;
	private static final int WIDTH_3 = 720;
	private static final int WIDTH_4 = 768;
	private static final int WIDTH_5 = 960;
	private static final int WIDTH_6 = 1152;
	private static final int WIDTH_7 = 1536;
	private static final String RESPONSE_RESULT_KEY = "result";
	private static final String RESPONSE_VIDEOS_KEY = "videos";
	private static final String RESPONSE_ERROR_KEY = "error";
	private static final String RESPONSE_MOST_VIEWED_KEY = "most_viewed";
	private static final String RESPONSE_TYPE_KEY = "type";
	private static final String RESPONSE_LOCAL_KEY = "LC";
	private static final String RESPONSE_ANDROID_URLS_KEY = "android_urls";
	private static final String RESPONSE_YOUTUBE_URL_KEY = "yt_url";
	private static final String RESPONSE_POSTER_KEY = "poster";
	private static final String RESPONSE_POSTER_PREFIX_VALUE = "poster_";
	private static final String YOUTUBE_URL_PREFIX = "http://img.youtube.com/vi/";
	private static final int SERVER_TIMEOUT = 3000;
	
	private Fragment fragment;
	
	public VideosDownloaderAsyncTask(Fragment fragment) {
		this.fragment = fragment;
	}
	
	@Override
	protected MostViewedVideosAndRecentVideosPair doInBackground(Void... arg0) {
		UrlEncodedFormEntity entity = createHttpPostEntity();
		HttpPost httpPost = createHttpPost(entity);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		setTimeout(httpClient);
		HttpResponse response = getResponse(httpClient, httpPost);
		if (response == null) {
			return null;
		}
		
		JSONObject responseJSON = getResponseJSON(response);
		if (responseJSON == null) {
			return null;
		}
		
		JSONObject resultJSON = getJSONObjectFromJSON(responseJSON, VideosDownloaderAsyncTask.RESPONSE_RESULT_KEY);
		if (resultJSON == null) {
			return null;
		}
		
		VideoItem mostViewed = getMostViewed(resultJSON);
		ArrayList<VideoItem> recentVideos = getRecentVideos(resultJSON);
		
		MostViewedVideosAndRecentVideosPair mostViewedVideosAndRecentVideosPair = new MostViewedVideosAndRecentVideosPair(mostViewed, recentVideos);
		
		return mostViewedVideosAndRecentVideosPair;
	}
	
	@Override
	protected void onPostExecute(MostViewedVideosAndRecentVideosPair mostViewedVideosAndRecentVideosPair) {
		if (mostViewedVideosAndRecentVideosPair == null) {
			((VideosFragment)this.fragment).notifyServerError();
			return;
		}
		
		((VideosFragment)this.fragment).setMostViewedVideo(mostViewedVideosAndRecentVideosPair.getMostViewedVideo());
		((VideosFragment)this.fragment).setRecentVideos(mostViewedVideosAndRecentVideosPair.getRecentVideos());
	}
	
	private ArrayList<VideoItem> getRecentVideos(JSONObject resultJSON) {
		ArrayList<VideoItem> recentVideos = new ArrayList<VideoItem>();
		JSONArray recentVideosObject = getJSONArrayFromJSON(resultJSON, VideosDownloaderAsyncTask.RESPONSE_VIDEOS_KEY);
		
		for (int index = 0; index < recentVideosObject.length(); ++index) {
			recentVideos.add(jsonToVideoItem(getJSONObjectFromJSONArray(recentVideosObject, index)));
		}
		
		return recentVideos;
	}
	
	private UrlEncodedFormEntity createHttpPostEntity() {
		ArrayList<NameValuePair> postKeys = new ArrayList<NameValuePair>();
		postKeys.add(new BasicNameValuePair(VideosDownloaderAsyncTask.METHOD_KEY, VideosDownloaderAsyncTask.METHOD_VALUE));
		
		UrlEncodedFormEntity entity;
		try {
			entity = new UrlEncodedFormEntity(postKeys, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			
			return null;
		}
		
		return entity;
	}
	
	private HttpPost createHttpPost(UrlEncodedFormEntity entity) {
		HttpPost httpPost = new HttpPost(RoundRobin.getInstance().getIP(this.fragment.getActivity().getApplicationContext()) + Global.API_POST_ADDRESS);
		httpPost.setEntity(entity);
		
		return httpPost;
	}
	
	private HttpResponse getResponse(DefaultHttpClient httpClient, HttpPost httpPost) {
		HttpResponse response;
		
		int timeouts = 0;
		while (true) {
			try {
				response = httpClient.execute(httpPost);
				
				break;
			} catch (ClientProtocolException e2) {
				e2.printStackTrace();
				Log.e(Global.TAG, "Videos response error");
				
				return null;
			} catch (IOException e2) {
				e2.printStackTrace();
				Log.e(Global.TAG, "Videos response error");
				
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
	
	private JSONObject getResponseJSON(HttpResponse response) {
		JSONObject responseObject;
		try {
			responseObject = new JSONObject(EntityUtils.toString(response.getEntity()));
			if (!responseObject.isNull(VideosDownloaderAsyncTask.RESPONSE_ERROR_KEY)) {
				Log.e(Global.TAG, "Videos JSON response error");
				
				return null;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Videos JSON response format error");
			
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Videos JSON response format error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Videos JSON response format error");
			
			return null;
		}
		
		return responseObject;
	}
	
	private JSONObject getJSONObjectFromJSON(JSONObject json, String key) {
		try {
			return json.getJSONObject(key);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Videos JSON getting JSON Object error");
			
			return null;
		}
	}
	
	private Integer getIntegerFromJSON(JSONObject json, String key) {
		try {
			return json.getInt(key);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Videos JSON getting int error");
			
			return null;
		}
	}
	
	private String getStringFromJSON(JSONObject json, String key) {
		try {
			return json.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Videos JSON getting String error");
			
			return null;
		}
	}
	
	private JSONArray getJSONArrayFromJSON(JSONObject json, String key) {
		try {
			return json.getJSONArray(key);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Videos JSON getting JSON Array error");
			
			return null;
		}
	}
	
	private String getStringFromJSONArray(JSONArray json, int index) {
		try {
			return json.getString(index);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Videos JSON getting String from Array error");
			
			return null;
		}
	}
	
	private JSONObject getJSONObjectFromJSONArray(JSONArray json, int index) {
		try {
			return json.getJSONObject(index);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Videos JSON getting JSON Object from Array error");
			
			return null;
		}
	}
	
	private VideoItem getMostViewed(JSONObject result) {
		return jsonToVideoItem(getJSONObjectFromJSON(result, VideosDownloaderAsyncTask.RESPONSE_MOST_VIEWED_KEY));
	}
	
	private DayMonthYearDate getDayMonthYearDate(String date) {
		String[] dateParts = date.split(VideosDownloaderAsyncTask.RESPONSE_DATE_SEPARATOR);
		int year = Integer.parseInt(dateParts[0]);
		int month = Integer.parseInt(dateParts[1]);
		int day = Integer.parseInt(dateParts[2]);
		
		DayMonthYearDate dayMonthYearDate = new DayMonthYearDate(day, month, year);
		
		return dayMonthYearDate;
	}
	
	private Boolean isLocal(JSONObject videoObject) {
		return getStringFromJSON(videoObject, VideosDownloaderAsyncTask.RESPONSE_TYPE_KEY).equals(VideosDownloaderAsyncTask.RESPONSE_LOCAL_KEY);
	}
	
	private String[] getUrlsForLocalVideo(JSONObject videoObject) {
		String[] urls = new String[3];
		JSONArray urlsObject = getJSONArrayFromJSON(videoObject, VideosDownloaderAsyncTask.RESPONSE_ANDROID_URLS_KEY);
		for (int index = 0; index < urlsObject.length(); ++index) {
			urls[index] = RoundRobin.getInstance().getIP(this.fragment.getActivity().getApplicationContext()) + "/" + getStringFromJSONArray(urlsObject, index);
		}
		
		return urls;
	}
	
	private String[] getUrlsForNotLocalVideo(JSONObject videoObject) {
		String[] urls = new String[1];
		urls[0] = getStringFromJSON(videoObject, VideosDownloaderAsyncTask.RESPONSE_YOUTUBE_URL_KEY);
		
		return urls;
	}
	
	private String[] getUrls(JSONObject videoObject) {
		if (isLocal(videoObject)) {
			return getUrlsForLocalVideo(videoObject);
		} else {
			return getUrlsForNotLocalVideo(videoObject);
		}
	}
	
	private String getMiniatureUrlForLocalVideo(JSONObject videoObject) {
		String miniatureUrl;
		
		JSONObject posters = getJSONObjectFromJSON(videoObject, VideosDownloaderAsyncTask.RESPONSE_POSTER_KEY);
		String key = VideosDownloaderAsyncTask.RESPONSE_POSTER_PREFIX_VALUE + Integer.toString(getWidthGreaterOrEqual());
		miniatureUrl = RoundRobin.getInstance().getIP(this.fragment.getActivity().getApplicationContext()) + "/" + getStringFromJSON(posters, key);
		
		return miniatureUrl;
	}
	
	private String getMiniatureUrlForNotLocalVideo(JSONObject videoObject) {
		String url;
		if (!videoObject.isNull(VideosDownloaderAsyncTask.RESPONSE_POSTER_KEY)) {
			JSONObject posters = getJSONObjectFromJSON(videoObject, VideosDownloaderAsyncTask.RESPONSE_POSTER_KEY);
			String key = VideosDownloaderAsyncTask.RESPONSE_POSTER_PREFIX_VALUE + Integer.toString(getWidthGreaterOrEqual());
			
			url = RoundRobin.getInstance().getIP(this.fragment.getActivity().getApplicationContext()) + "/" + getStringFromJSON(posters, key);
		} else {
			url = VideosDownloaderAsyncTask.YOUTUBE_URL_PREFIX + getYouTubeVideoId(getStringFromJSON(videoObject, VideosDownloaderAsyncTask.RESPONSE_YOUTUBE_URL_KEY)) + "/0.jpg";
		}
		
		return url;
	}
	
	private String getMiniatureUrl(JSONObject videoObject) {	
		if (isLocal(videoObject)) {
			return getMiniatureUrlForLocalVideo(videoObject);
		} else {
			return getMiniatureUrlForNotLocalVideo(videoObject);
		}
	}
	
	private VideoItem jsonToVideoItem(JSONObject videoObject) {
		VideoItem result = new VideoItem();
		
		result.setId(getIntegerFromJSON(videoObject, VideosDownloaderAsyncTask.RESPONSE_ID_KEY));
		result.setTitleEn(getStringFromJSON(videoObject, VideosDownloaderAsyncTask.RESPONSE_TITLE_EN_KEY));
		result.setTitlePl(getStringFromJSON(videoObject, VideosDownloaderAsyncTask.RESPONSE_TITLE_PL_KEY));
		String date = getStringFromJSON(videoObject, VideosDownloaderAsyncTask.RESPONSE_DATE_KEY);
		DayMonthYearDate dayMonthYearDate = getDayMonthYearDate(date);
		result.setDay(dayMonthYearDate.getDay());
		result.setMonth(dayMonthYearDate.getMonth());
		result.setYear(dayMonthYearDate.getYear());
		result.setUrls(getUrls(videoObject));
		result.setIsLocalSource(isLocal(videoObject));
		result.setMiniatureUrl(getMiniatureUrl(videoObject));
			
		return result;
	}
	
	private int getOrientation() {
		return this.fragment.getActivity().getApplicationContext().getResources().getConfiguration().orientation;
	}
	
	private void changeServer(HttpPost httpPost, String sufix) {
		String ip = RoundRobin.getInstance().getAnotherIP(this.fragment.getActivity().getApplicationContext(), httpPost.getURI().getHost());
		httpPost.setURI(URI.create(ip + sufix));
	}
	
	private void setTimeout(DefaultHttpClient httpClient) {
		HttpParams httpParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, VideosDownloaderAsyncTask.SERVER_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, VideosDownloaderAsyncTask.SERVER_TIMEOUT);
		httpClient.setParams(httpParams);
	}
	
	private int getWidthGreaterOrEqual() {
		int width;
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			width = getDisplayWidth();
		} else {
			width = (int) (getDisplayWidth() * Global.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH);
		}
		
		if (VideosDownloaderAsyncTask.WIDTH_0 >= width) {
			return VideosDownloaderAsyncTask.WIDTH_0;
		}
		
		if (VideosDownloaderAsyncTask.WIDTH_1 >= width) {
			return VideosDownloaderAsyncTask.WIDTH_1;
		}
		
		if (VideosDownloaderAsyncTask.WIDTH_2 >= width) {
			return VideosDownloaderAsyncTask.WIDTH_2;
		}
		
		if (VideosDownloaderAsyncTask.WIDTH_3 >= width) {
			return VideosDownloaderAsyncTask.WIDTH_3;
		}
		
		if (VideosDownloaderAsyncTask.WIDTH_4 >= width) {
			return VideosDownloaderAsyncTask.WIDTH_4;
		}
		
		if (VideosDownloaderAsyncTask.WIDTH_5 >= width) {
			return VideosDownloaderAsyncTask.WIDTH_5;
		}
		
		if (VideosDownloaderAsyncTask.WIDTH_6 >= width) {
			return VideosDownloaderAsyncTask.WIDTH_6;
		}
		
		return VideosDownloaderAsyncTask.WIDTH_7;
	}
	
	private String getYouTubeVideoId(String url) {
		return Uri.parse(url).getQueryParameter("v");
	}
	
	@SuppressWarnings("deprecation")
	private int getDisplayWidth() {
		Display display = this.fragment.getActivity().getWindowManager().getDefaultDisplay();
		
		if (Build.VERSION.SDK_INT < 13) {
			return display.getWidth();
		} else {
			Point size = new Point();
			display.getSize(size);
			
			return size.x;
		}
	}
}
