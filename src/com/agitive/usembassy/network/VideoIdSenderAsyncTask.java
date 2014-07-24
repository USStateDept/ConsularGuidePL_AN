package com.agitive.usembassy.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import com.agitive.usembassy.global.Global;

import android.os.AsyncTask;
import android.util.Log;

public class VideoIdSenderAsyncTask extends AsyncTask<Object, Void, Void> {

	private static final String METHOD_KEY = "method";
	private static final String METHOD_VALUE = "WatchVideo";
	private static final String VIDEO_ID_KEY = "video_id";
	private static final int SERVER_TIMEOUT = 3000;
	
	@Override
	protected Void doInBackground(Object... params) {
		int id =  (Integer) params[0];
		
		HttpPost httpPost = createHttpPost();
		UrlEncodedFormEntity httpEntity = createHttpPostEntity(id); 
		if (httpEntity == null) {
			return null;
		}
		httpPost.setEntity(httpEntity);
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		setTimeout(httpClient);
		executeHttpClient(httpClient, httpPost);
		
		return null;
	}
	
	private boolean executeHttpClient(DefaultHttpClient httpClient, HttpPost httpPost) {
		try {
			httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Video id sender sending id error");
			
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Video id sender sending id error");
			
			return false;
		}
		
		return true;
	}
	
	private HttpPost createHttpPost() {
		HttpPost httpPost = new HttpPost(RoundRobin.getInstance().getMasterIP() + Global.API_POST_ADDRESS);
		
		return httpPost;
	}
	
	private UrlEncodedFormEntity createHttpPostEntity(int id) {
		ArrayList<NameValuePair> postKeys = new ArrayList<NameValuePair>();
		postKeys.add(new BasicNameValuePair(VideoIdSenderAsyncTask.METHOD_KEY, VideoIdSenderAsyncTask.METHOD_VALUE));
		postKeys.add(new BasicNameValuePair(VideoIdSenderAsyncTask.VIDEO_ID_KEY, Integer.toString(id)));
		
		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(postKeys, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Video id sender http post error");
			
			return null;
		}
		
		return entity;
	}
	
	private void setTimeout(DefaultHttpClient httpClient) {
		HttpParams httpParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, VideoIdSenderAsyncTask.SERVER_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, VideoIdSenderAsyncTask.SERVER_TIMEOUT);
		httpClient.setParams(httpParams);
	}
}
