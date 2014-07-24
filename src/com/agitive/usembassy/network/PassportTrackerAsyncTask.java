package com.agitive.usembassy.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.fragments.asyncTaskFragments.PassportTrackerAsyncTaskFragment;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.privateKeys.AgitivePrivateKeys;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class PassportTrackerAsyncTask extends AsyncTask<Object, Void, String>{
	
	private static final String CGI_FEDERAL_URL_HTML_TAG = "instantIFrame";
	private static final String CGI_FEDERAL_URL_ATTRIBUTE = "src";
	private static final char START_TAG_CHAR = '<';
	private static final char END_TAG_CHAR = '>';
	private static final String STATUS_ERROR = "ERROR";
	private static final String HTTP_POST_AJAX_KEY = "AJAXREQUEST";
	private static final String HTTP_POST_PASSPORT_TRACKER_PAGE_KEY = "passportTrackerPage:psptTrackerForm";
	private static final String HTTP_POST_PASSPORT_TRACKER_PAGE_PASSPORT_NUMBER_KEY = "passportTrackerPage:psptTrackerForm:j_id34:j_id35:passportNo";
	private static final String HTTP_POST_VIEW_STATE_KEY = "com.salesforce.visualforce.ViewState";
	private static final String HTTP_POST_VIEW_STATE_VERSION_KEY = "com.salesforce.visualforce.ViewStateVersion";
	private static final String HTTP_POST_VIEW_STATE_MAC_KEY = "com.salesforce.visualforce.ViewStateMAC";
	private static final String HTTP_POST_TRACK_BUTTON_KEY = "passportTrackerPage:psptTrackerForm:trackButton";
	private static final String HTTP_POST_AJAX_VALUE = "_viewRoot";
	private static final String HTTP_POST_PASSPORT_TRACKER_PAGE_VALUE = "passportTrackerPage:psptTrackerForm";
	private static final String HTTP_POST_VIEW_STATE_KEY_FOR_VALUE = "com.salesforce.visualforce.ViewState";
	private static final String HTTP_POST_VIEW_STATE_ATTRIBUTE_FOR_VALUE = "value";
	private static final String HTTP_POST_VIEW_STATE_VERSION_KEY_FOR_VALUE = "com.salesforce.visualforce.ViewStateVersion";
	private static final String HTTP_POST_VIEW_STATE_VERSION_ATTRIBUTE_FOR_VALUE = "value";
	private static final String HTTP_POST_VIEW_STATE_MAC_KEY_FOR_VALUE = "com.salesforce.visualforce.ViewStateMAC";
	private static final String HTTP_POST_VIEW_STATE_MAC_ATTRIBUTE_FOR_VALUE = "value";
	private static final String HTTP_POST_TRACK_BUTTON_VALUE = "passportTrackerPage:psptTrackerForm:trackButton";
	private static final String PASSPORT_STATUS_HTML_KEY = "result";
	private static final int SERVER_TIMEOUT = 5000;
	private static final String HTTP_POST_HEADER_REFERER_KEY = "Referer";
	private static final String HTTP_POST_HEADER_ACCEPT_LANGUAGE_KEY = "Accept-Language";
	private static final String HTTP_POST_HEADER_ACCEPT_LANGUAGE_PL_VALUE = "pl,en-us,en";
	private static final String HTTP_POST_HEADER_ACCEPT_LANGUAGE_EN_VALUE = "en-us,en";
	
	private Context context;
	
	public PassportTrackerAsyncTask(Context context) {
		this.context = context;
	}
	
	@Override
	protected String doInBackground(Object... params) {
		String passportNumber = (String) params[0];
		
		HttpGet httpGetForUSTravelDocs = createHttpGetForUSTravelDocs();
		DefaultHttpClient httpClientForUSTravelDocs = new DefaultHttpClient();
		setTimeout(httpClientForUSTravelDocs);
		HttpResponse usTravelDocsResponse = executeHttpClient(httpGetForUSTravelDocs, httpClientForUSTravelDocs);
		if (usTravelDocsResponse == null) {
			return null;
		}
		
		String usTravelDocsResponseString = getResponseAsString(usTravelDocsResponse);
		if (usTravelDocsResponseString == null) {
			return null;
		}
		
		String cgiFederalUrl = getValueOfTagFromHtml(usTravelDocsResponseString, PassportTrackerAsyncTask.CGI_FEDERAL_URL_HTML_TAG, PassportTrackerAsyncTask.CGI_FEDERAL_URL_ATTRIBUTE);
		
		HttpGet httpGetForCGIFederal = createHttpGetForCGIFederal(cgiFederalUrl);
		DefaultHttpClient httpClientForCGIFederal = new DefaultHttpClient();
		setTimeout(httpClientForCGIFederal);
		HttpResponse cgiFederalResponse = executeHttpClient(httpGetForCGIFederal, httpClientForCGIFederal);
		if (cgiFederalResponse == null) {
			return null;
		}
		
		String cgiFederalResponseString = getResponseAsString(cgiFederalResponse);
		if (cgiFederalResponseString == null) {
			return null;
		}
		
		HttpPost httpPostForCGIFederal = createHttpPostForCGIFederal(cgiFederalUrl);
		UrlEncodedFormEntity httpPostEntity = createHttpPostEntity(passportNumber, cgiFederalResponseString);
		httpPostForCGIFederal.setEntity(httpPostEntity);
		DefaultHttpClient httpClientForPostCGIFederal = new DefaultHttpClient();
		setTimeout(httpClientForPostCGIFederal);
		HttpResponse postCGIResponse = executeHttpClient(httpPostForCGIFederal, httpClientForPostCGIFederal);
		if (postCGIResponse == null) {
			return null;
		}
		
		String postCGIFederalResponseString = getResponseAsString(postCGIResponse);
		if (postCGIFederalResponseString == null) {
			return null;
		}
		
		return getStatusFromResponse(postCGIFederalResponseString);
	}
	
	@Override
	protected void onPostExecute(String status) {
		PassportTrackerAsyncTaskFragment.setPassportStatus(status);
	}
	
	private void setTimeout(DefaultHttpClient httpClient) {
		HttpParams httpParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, PassportTrackerAsyncTask.SERVER_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, PassportTrackerAsyncTask.SERVER_TIMEOUT);
		httpClient.setParams(httpParams);
	}
	
	private String getStatusFromResponse(String response) {
		if (!response.contains(PassportTrackerAsyncTask.PASSPORT_STATUS_HTML_KEY)) {
			return PassportTrackerAsyncTask.STATUS_ERROR;
		}
		
		int resultKeyPosition = response.indexOf(PassportTrackerAsyncTask.PASSPORT_STATUS_HTML_KEY);
		
		return getStatusValueFromAttribute(response, resultKeyPosition);
	}
	
	private String getStatusValueFromAttribute(String html, int position) {
		while (html.charAt(position) != PassportTrackerAsyncTask.END_TAG_CHAR) {
			++position;
		}
		++position;
		String result = "";
		while (html.charAt(position) != PassportTrackerAsyncTask.START_TAG_CHAR) {
			result += html.charAt(position);
			++position;
		}
		
		return result;
	}
	
	private HttpResponse executeHttpClient(HttpPost httpPost, DefaultHttpClient httpClient) {
		HttpResponse response;
		
		try {
			response = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Passport Tracking HttpPost exrcuting error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Passport Tracking HttpPost exrcuting error");
			
			return null;
		}
		
		return response;
	}
	
	private UrlEncodedFormEntity createHttpPostEntity(String passportNumber, String cgiFederalResponseString) {
		ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();
		arguments.add(new BasicNameValuePair(PassportTrackerAsyncTask.HTTP_POST_AJAX_KEY, PassportTrackerAsyncTask.HTTP_POST_AJAX_VALUE));
		arguments.add(new BasicNameValuePair(PassportTrackerAsyncTask.HTTP_POST_PASSPORT_TRACKER_PAGE_KEY, PassportTrackerAsyncTask.HTTP_POST_PASSPORT_TRACKER_PAGE_VALUE));
		arguments.add(new BasicNameValuePair(PassportTrackerAsyncTask.HTTP_POST_PASSPORT_TRACKER_PAGE_PASSPORT_NUMBER_KEY, passportNumber));
		arguments.add(new BasicNameValuePair(PassportTrackerAsyncTask.HTTP_POST_VIEW_STATE_KEY, getValueOfTagFromHtml(cgiFederalResponseString, PassportTrackerAsyncTask.HTTP_POST_VIEW_STATE_KEY_FOR_VALUE, PassportTrackerAsyncTask.HTTP_POST_VIEW_STATE_ATTRIBUTE_FOR_VALUE)));
		arguments.add(new BasicNameValuePair(PassportTrackerAsyncTask.HTTP_POST_VIEW_STATE_VERSION_KEY, getValueOfTagFromHtml(cgiFederalResponseString, PassportTrackerAsyncTask.HTTP_POST_VIEW_STATE_VERSION_KEY_FOR_VALUE, PassportTrackerAsyncTask.HTTP_POST_VIEW_STATE_VERSION_ATTRIBUTE_FOR_VALUE)));
		arguments.add(new BasicNameValuePair(PassportTrackerAsyncTask.HTTP_POST_VIEW_STATE_MAC_KEY, getValueOfTagFromHtml(cgiFederalResponseString, PassportTrackerAsyncTask.HTTP_POST_VIEW_STATE_MAC_KEY_FOR_VALUE, PassportTrackerAsyncTask.HTTP_POST_VIEW_STATE_MAC_ATTRIBUTE_FOR_VALUE)));
		arguments.add(new BasicNameValuePair(PassportTrackerAsyncTask.HTTP_POST_TRACK_BUTTON_KEY, PassportTrackerAsyncTask.HTTP_POST_TRACK_BUTTON_VALUE));	
		
		UrlEncodedFormEntity entity;
		try {
			entity = new UrlEncodedFormEntity(arguments);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "CGI Federal HttpPost arguments error");
			
			return null;
		}
		
		return entity;
	}
	
	private HttpPost createHttpPostForCGIFederal(String cgiFederalUrl) {
		HttpPost httpPost = new HttpPost(AgitivePrivateKeys.CGI_FEDERAL_POST_URL);
		httpPost.addHeader(PassportTrackerAsyncTask.HTTP_POST_HEADER_REFERER_KEY, cgiFederalUrl);
		httpPost.addHeader(PassportTrackerAsyncTask.HTTP_POST_HEADER_ACCEPT_LANGUAGE_KEY, getHttpPostHeaderAcceptLanguageValue());
		
		return httpPost;
	}
	
	private String getAppLanguage() {
		SharedPreferences sharedPreferences = this.context.getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		return sharedPreferences.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
	}
	
	private String getHttpPostHeaderAcceptLanguageValue() {
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			return PassportTrackerAsyncTask.HTTP_POST_HEADER_ACCEPT_LANGUAGE_EN_VALUE;
		} else {
			return PassportTrackerAsyncTask.HTTP_POST_HEADER_ACCEPT_LANGUAGE_PL_VALUE;
		}
	}
	
	private HttpGet createHttpGetForCGIFederal(String url) {
		HttpGet httpGet = new HttpGet(url);
		
		return httpGet;
	}
	
	private String getResponseAsString(HttpResponse response) {
		try {
			return EntityUtils.toString(response.getEntity());
		} catch (ParseException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "USTravel Docs response error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "USTravel Docs response error");
			
			return null;
		}
	}
	
	private String getValueOfTagFromHtml(String html, String tag, String attribute) {
		int tagPosition = html.indexOf(tag);
		int startStagPosition = getStartTagPosition(html, tagPosition);
		int valuePosition = html.indexOf(attribute, startStagPosition);
		valuePosition += (attribute.length() + 2);
		
		return getAttributeValue(html, valuePosition);
	}
	
	private String getAttributeValue(String html, int attributePosition) {
		String url = "";
		
		while (html.charAt(attributePosition) != '"') {
			url += html.charAt(attributePosition);
			++attributePosition;
		}
		
		return url;
	}
	
	private int getStartTagPosition(String html, int position) {
		while (html.charAt(position) != PassportTrackerAsyncTask.START_TAG_CHAR) {
			--position;
		}
		
		return position;
	}
	
	private HttpGet createHttpGetForUSTravelDocs() {
		HttpGet httpGet = new HttpGet(AgitivePrivateKeys.USTRAVELDOCS_URL);
		
		return httpGet;
	}
	
	private HttpResponse executeHttpClient(HttpGet httpGet, DefaultHttpClient httpClient) {
		HttpResponse response;
		
		try {
			response = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "USTravelDocs server error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "USTravelDocs server error");
			
			return null;
		}
		
		return response;
	}
}
