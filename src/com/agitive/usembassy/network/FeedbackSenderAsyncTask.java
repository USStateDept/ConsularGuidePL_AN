package com.agitive.usembassy.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import com.agitive.usembassy.fragments.asyncTaskFragments.FeedbackSenderFragment;
import com.agitive.usembassy.global.Global;

import android.os.AsyncTask;
import android.util.Log;

public class FeedbackSenderAsyncTask extends AsyncTask<Object, Void, Boolean>{

	private static final String METHOD_KEY = "method";
	private static final String METHOD_NAME = "AddFeedback";
	private static final String FEEDBACK_KEY = "feedback";
	private static final String EMAIL_KEY = "email";
	private static final String PAGE_KEY = "from_page";
	private static final int SERVER_TIMEOUT = 3000;
	
	@Override
	protected Boolean doInBackground(Object... params) {
		String feedbackContent = (String) params[0];
		String email = (String) params[1];
		int layoutId = (Integer) params[2];
		
		UrlEncodedFormEntity httpEntity = createHttpEntity(feedbackContent, email, layoutId);
		HttpPost httpPost = createHttpPost(httpEntity);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		setTimeout(httpClient);
		HttpResponse response = sendFeedbackAndGetResponse(httpClient, httpPost);
		if (response == null ||
				response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean isFeedbackSent) {
		FeedbackSenderFragment.setFeedbackSenderResult(isFeedbackSent);
	}
	
	private void setTimeout(DefaultHttpClient httpClient) {
		HttpParams httpParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, FeedbackSenderAsyncTask.SERVER_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, FeedbackSenderAsyncTask.SERVER_TIMEOUT);
		httpClient.setParams(httpParams);
	}
	
	private UrlEncodedFormEntity createHttpEntity(String feedbackContent, String email, int layoutId) {
		ArrayList<NameValuePair> postKeys = new ArrayList<NameValuePair>();
		postKeys.add(new BasicNameValuePair(FeedbackSenderAsyncTask.METHOD_KEY, FeedbackSenderAsyncTask.METHOD_NAME));
		postKeys.add(new BasicNameValuePair(FeedbackSenderAsyncTask.FEEDBACK_KEY, feedbackContent));
		postKeys.add(new BasicNameValuePair(FeedbackSenderAsyncTask.EMAIL_KEY, email));
		postKeys.add(new BasicNameValuePair(FeedbackSenderAsyncTask.PAGE_KEY, Integer.toString(layoutId)));
		
		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(postKeys, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			
			return null;
		}
		
		return entity;
	}
	
	private HttpPost createHttpPost(UrlEncodedFormEntity entity) {
		HttpPost httpPost = new HttpPost(RoundRobin.getInstance().getMasterIP() + Global.API_POST_ADDRESS);
		httpPost.setEntity(entity);
		
		return httpPost;
	}
	
	private HttpResponse sendFeedbackAndGetResponse(DefaultHttpClient httpClient, HttpPost httpPost) {
		HttpResponse response;
		
		try {
			response = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Feedback response error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Feedback response error");
			
			return null;
		}
		if (response == null ||
				response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			return null;
		}
		
		return response;
	}
}
