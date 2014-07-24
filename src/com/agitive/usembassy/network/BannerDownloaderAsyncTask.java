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
import org.json.JSONException;
import org.json.JSONObject;

import com.agitive.usembassy.fragments.asyncTaskFragments.BannerDownloaderFragment;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.objects.Banner;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

public class BannerDownloaderAsyncTask extends AsyncTask<Void, Void, Banner>{

	private static String methodKey = "method";
	private static String methodValue = "GetBanner";
	private static final int SERVER_TIMEOUT = 3000;
	
	private Fragment fragment;
	
	public BannerDownloaderAsyncTask(Fragment fragment) {
		this.fragment = fragment;
	}
	
	@Override
	protected Banner doInBackground(Void... params) {		
		UrlEncodedFormEntity entity = createEntity();
		HttpPost httpPost = createHttpPost(entity);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		setTimeout(httpClient);
		HttpResponse response = executeHttpClient(httpClient, httpPost, Global.API_POST_ADDRESS);
		if (response == null ||
				response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			return null;
		}
		
		JSONObject responseObject = createResponseObject(response);
		if (responseObject == null) {
			Log.e(Global.TAG, "Banner response error");
			return null;
		}
		
		if (!responseObject.isNull("error")) {
			Log.e(Global.TAG, "response error not null");
			
			return null;
		}
		
		if (!isBannerEnabled(responseObject)) {
			return null;
		}
		
		return createBanner(responseObject);
	}

	@Override
	protected void onPostExecute(Banner banner) {
		if (banner == null) {
			return;
		} 
		
		((BannerDownloaderFragment)this.fragment).showBanner(banner);
	}
	
	private UrlEncodedFormEntity createEntity() {
		ArrayList<NameValuePair> postKeys = new ArrayList<NameValuePair>();
		postKeys.add(new BasicNameValuePair(BannerDownloaderAsyncTask.methodKey, BannerDownloaderAsyncTask.methodValue));
		
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
		HttpPost httpPost = new HttpPost(RoundRobin.getInstance().getIP(this.fragment.getActivity().getApplicationContext()) + Global.API_POST_ADDRESS);
		httpPost.setEntity(entity);
		
		return httpPost;
	}
	
	private void setTimeout(DefaultHttpClient httpClient) {
		HttpParams httpParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, BannerDownloaderAsyncTask.SERVER_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, BannerDownloaderAsyncTask.SERVER_TIMEOUT);
		httpClient.setParams(httpParams);
	}
	
	private HttpResponse executeHttpClient(DefaultHttpClient httpClient, HttpPost httpPost, String urlSufix) {
		HttpResponse response = null;
		int timeouts = 0;
		while (true) {
			try {
				response = httpClient.execute(httpPost);
				
				break;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				Log.e(Global.TAG, "Banner server response error");
				
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(Global.TAG, "Banner server response error");
				++timeouts;
				if (timeouts == 2) {
					return null;
				}
				
				changeServer(httpPost, urlSufix);
				
				continue;
			}
		}
		
		return response;
	}
	
	private void changeServer(HttpPost httpPost, String sufix) {
		String ip = RoundRobin.getInstance().getAnotherIP(this.fragment.getActivity().getApplicationContext(), httpPost.getURI().getHost());
		httpPost.setURI(URI.create(ip + sufix));
	}
	
	private JSONObject createResponseObject(HttpResponse response) {
		JSONObject responseObject;
		
		try {
			responseObject = new JSONObject(EntityUtils.toString(response.getEntity()));
		} catch (ParseException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Banner response to JSON error");
			
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Banner response to JSON error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Banner response to JSON error");
			
			return null;
		}
		
		return responseObject;
	}
	
	private boolean isBannerEnabled(JSONObject responseObject) {
		JSONObject result;
		try {
			result = responseObject.getJSONObject("result");
			
			return result.getBoolean("enabled");
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Banner response JSON format error");
			
			return false;
		}
	}
	
	private Banner createBanner(JSONObject responseObject) {
		Banner banner = new Banner(this.fragment.getActivity());
		try {
			JSONObject result = responseObject.getJSONObject("result");
			
			banner.setTitleEn(result.getString("title_en"));
			banner.setTitlePl(result.getString("title_pl"));
			banner.setContentEn(result.getString("description_en"));
			banner.setContentPl(result.getString("description_pl"));
			banner.setType(result.getString("type"));
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Banner response JSON format error");
			
			return null;
		}
		
		return banner;
	}
}
