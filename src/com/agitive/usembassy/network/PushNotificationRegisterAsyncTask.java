package com.agitive.usembassy.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.privateKeys.AgitivePrivateKeys;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class PushNotificationRegisterAsyncTask extends AsyncTask<Void, Void, Void> {

	private static final String SHARED_PREFERENCES_GCM_ID_KEY = "com.agitive.usembassy.network.PushNotificationRegisterAsyncTask.sharedPreferencesGCMId";
	private static final String METHOD_KEY = "method";
	private static final String METHOD_SEND_GCM_ID_VALUE = "AddAndroidDevice";
	private static final String GCM_ID_KEY = "new_registration_id";
	private static final String METHOD_CHANGE_GCM_ID_VALUE = "ChangeAndroidDevice";
	private static final String OLD_GCM_ID_KEY = "old_registration_id";
	private static final String NEW_GCM_ID_KEY = "new_registration_id";
	private static final int SERVER_TIMEOUT = 3000;
	
	private Context context;
	
	public PushNotificationRegisterAsyncTask(Context context) {
		this.context = context;
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		String gcmId = getGCMId();
		if (gcmId == null) {
			return null;
		}
		
		String gcmIdOld = loadGCMIdFromSharedPreferences();
		boolean result = false;
		if (gcmIdOld == null) {
			result = sendGCMIdToServerAndGetResult(gcmId);
		} else if (!gcmIdOld.equals(gcmId)) {
			result = changeGCMIdInServerAndGetResult(gcmIdOld, gcmId);
		}
		
		if (result) {
			saveGCMIdInSharedPreferences(gcmId);
		}
		
		
		return null;
	}
	
	private void setTimeout(DefaultHttpClient httpClient) {
		HttpParams httpParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, PushNotificationRegisterAsyncTask.SERVER_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, PushNotificationRegisterAsyncTask.SERVER_TIMEOUT);
		httpClient.setParams(httpParams);
	}
	
	private boolean changeGCMIdInServerAndGetResult(String gcmIdOld, String gcmIdNew) {
    	HttpPost httpPost = new HttpPost(RoundRobin.getInstance().getMasterIP() + Global.API_POST_ADDRESS);
    	httpPost.setEntity(createEntiryForChangeId(gcmIdOld, gcmIdNew));
    	DefaultHttpClient httpClient = new DefaultHttpClient();
    	setTimeout(httpClient);
    	return excuteHttpClientAndGetResult(httpClient, httpPost);
    }
	
	private boolean excuteHttpClientAndGetResult(HttpClient httpClient, HttpPost httpPost) {
		HttpResponse response;
		
		try {
			response = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Google Cloud Messaging changing id in server error");
			
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Google Cloud Messaging changing id in server error");
			
			return false;
		}
		
		if (response == null ||
				response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			return false;
		}
		
		return true;
	}
	
	private UrlEncodedFormEntity createEntiryForChangeId(String gcmIdOld, String gcmIdNew) {
		ArrayList<BasicNameValuePair> httpContent = new ArrayList<BasicNameValuePair>();
    	httpContent.add(new BasicNameValuePair(PushNotificationRegisterAsyncTask.METHOD_KEY, PushNotificationRegisterAsyncTask.METHOD_CHANGE_GCM_ID_VALUE));
    	httpContent.add(new BasicNameValuePair(PushNotificationRegisterAsyncTask.OLD_GCM_ID_KEY, gcmIdOld));
    	httpContent.add(new BasicNameValuePair(PushNotificationRegisterAsyncTask.NEW_GCM_ID_KEY, gcmIdNew));
		
		UrlEncodedFormEntity entity;
		try {
			entity = new UrlEncodedFormEntity(httpContent, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			
			return null;
		}
		
		return entity;
	}
	
	private boolean sendGCMIdToServerAndGetResult(String gcmId) {
		HttpPost httpPost = new HttpPost(RoundRobin.getInstance().getMasterIP() + Global.API_POST_ADDRESS);
    	httpPost.setEntity(createEntityForNewId(gcmId));
    	DefaultHttpClient httpClient = new DefaultHttpClient();
    	setTimeout(httpClient);
    	return executeHttpClientAndGetResult(httpClient, httpPost);
    }
	
	private boolean executeHttpClientAndGetResult(HttpClient httpClient, HttpPost httpPost) {
		HttpResponse response;
		
		try {
			response = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Google Cloud Messaging sending id to server error");
			
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Google Cloud Messaging sending id to server error");
			
			return false;
		}
		
		if (response == null ||
				response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			return false;
		}
		
		return true;
	}
	
	private UrlEncodedFormEntity createEntityForNewId(String gcmId) {
		ArrayList<BasicNameValuePair> httpContent = new ArrayList<BasicNameValuePair>();
    	httpContent.add(new BasicNameValuePair(PushNotificationRegisterAsyncTask.METHOD_KEY, PushNotificationRegisterAsyncTask.METHOD_SEND_GCM_ID_VALUE));
    	httpContent.add(new BasicNameValuePair(PushNotificationRegisterAsyncTask.GCM_ID_KEY, gcmId));
    	
    	UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(httpContent, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			
			return null;
		}
		
		return entity;
	}
	
	private void saveGCMIdInSharedPreferences(String gcmId) {
    	SharedPreferences sharedPreferences = this.context.getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = sharedPreferences.edit();
    	editor.putString(PushNotificationRegisterAsyncTask.SHARED_PREFERENCES_GCM_ID_KEY, gcmId);
    	editor.commit();
    }
	
	private String loadGCMIdFromSharedPreferences() {
    	SharedPreferences sharedPreferences = this.context.getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    	
    	return sharedPreferences.getString(PushNotificationRegisterAsyncTask.SHARED_PREFERENCES_GCM_ID_KEY, null);
    }

	private String getGCMId() {
		GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this.context);
		
		String gcmId;
		try {
			gcmId = googleCloudMessaging.register(AgitivePrivateKeys.GOOGLE_CLOUD_MESSAGING_PROJECT_ID);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Google Cloud Messaging registration error");
			
			return null;
		}
		
		return gcmId;
	}
}
