package com.agitive.usembassy.network;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.agitive.usembassy.databases.DatabaseAdapter;
import com.agitive.usembassy.databases.LayoutDatabase;
import com.agitive.usembassy.fragments.asyncTaskFragments.ApplicationContentDownloaderFragment;
import com.agitive.usembassy.fragments.dialogFragments.ProgressDialogFragment;
import com.agitive.usembassy.global.Global;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.Pair;

public class ApplicationContentDownloaderAsyncTask extends AsyncTask<Void, Void, HashMap<String, Object>> {

	private final static String IMAGE_TAG_NAME = "img";
	private final static String IMAGE_SOURCE_TAG_NAME = "src";
	private static final int SERVER_TIMEOUT = 3000;
	
	private static final String METHOD_KEY = "method";
	private static final String METHOD_VALUE = "UpdatePages";
	private static final String PAGES_KEY = "pages";
	private static final String JSON_RESULT_KEY = "result";
	private static final String JSON_UPDATED_PAGES_KEY = "updated";
	private static final String JSON_REMOVED_PAGES_KEY = "removed";
	private static final String JSON_NEW_PAGES_KEY = "new";
	private static final String HTTP_POST_HEADER_ACCEPT_KEY = "Accept";
	private static final String HTTP_POST_HEADER_ACCEPT_VALUE = "application/json";
	private static final String HTTP_POST_HEADER_CONTENT_TYPE_KEY = "Content-Type";
	private static final String HTTP_POST_HEADER_CONTENT_TYPE_VALUE = "application/json";
	private static final String LAYOUT_ID_KEY = "id";
	private static final String LAYOUT_PARENT_ID_KEY = "parent_id";
	private static final String LAYOUT_INDEX_KEY = "index";
	private static final String LAYOUT_TITLE_EN_KEY = "title_en";
	private static final String LAYOUT_TITLE_PL_KEY = "title_pl";
	private static final String LAYOUT_VERSION_KEY = "version";
	private static final String LAYOUT_TYPE_KEY = "type";
	private static final String LAYOUT_CONTENT_EN_KEY = "content_en";
	private static final String LAYOUT_FAQ_EN_KEY = "faq_en";
	private static final String LAYOUT_CONTENT_PL_KEY = "content_pl";
	private static final String LAYOUT_FAQ_PL_KEY = "faq_pl";
	private static final String LAYOUT_ADDITIONAL_EN_KEY = "additional_en";
	private static final String LAYOUT_ADDITIONAL_PL_KEY = "additional_pl";
	private static final String LAYOUT_LATITUDE_KEY = "latitude";
	private static final String LAYOUT_LONGITUDE_KEY = "longitude";
	private static final String LAYOUT_ZOOM_KEY = "zoom";
	private static final String LAYOUT_ERROR_KEY = "error";
	private static final String REQUEST_VERSION_KEY = "version";
	private static final String LAYOUT_TYPE_TEXT = "text";
	private static final String LAYOUT_TYPE_LIST = "list";
	private static final String LAYOUT_TYPE_STEPS = "stps";
	private static final String LAYOUT_TYPE_CONTACT = "cont";
	
	private Fragment fragment;
	
	public ApplicationContentDownloaderAsyncTask(Fragment fragment) {
		this.fragment = fragment;
	}
	
	@Override
	protected void onPreExecute() {
		ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
		progressDialogFragment.setCancelable(false);
		FragmentManager fragmentManager = this.fragment.getFragmentManager();
		
		try {
			fragmentManager.beginTransaction().add(progressDialogFragment, ProgressDialogFragment.TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected HashMap<String, Object> doInBackground(Void... params) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		HttpPost httpPost = createHttpPost();
		JSONObject postEntity = createPostEntity();
		if (postEntity == null) {
			return new HashMap<String, Object>();
		}
	
		if (!setHttpEntity(httpPost, postEntity)) {
			return new HashMap<String, Object>();
		}
	
		DefaultHttpClient httpClient = new DefaultHttpClient();
		setTimeout(httpClient);
		HttpResponse response = executeHttpCleint(httpPost, httpClient);
		if (response == null) {
			return null;
		}
		
		JSONObject responseJson = getResponseAsJSON(response);
		if (responseJson == null) {
			return null;
		}
		
		if (!responseJson.isNull(ApplicationContentDownloaderAsyncTask.LAYOUT_ERROR_KEY)) {
			Log.e(Global.TAG, "Application content downloader response error not null");
			return null;
		}
		
		JSONObject resultJSONObject = getJSONObjectFromJSONObject(responseJson, ApplicationContentDownloaderAsyncTask.JSON_RESULT_KEY);
		if (resultJSONObject == null) {
			return null;
		}
		
		JSONArray updatedObject = getJSONArrayFromJSONObject(resultJSONObject, ApplicationContentDownloaderAsyncTask.JSON_UPDATED_PAGES_KEY);
		if (updatedObject == null) {
			return null;
		}
		
		JSONArray removedObject = getJSONArrayFromJSONObject(resultJSONObject, ApplicationContentDownloaderAsyncTask.JSON_REMOVED_PAGES_KEY);
		if (removedObject == null) {
			return null;
		}
		
		JSONArray newObject = getJSONArrayFromJSONObject(resultJSONObject, ApplicationContentDownloaderAsyncTask.JSON_NEW_PAGES_KEY);
		if (newObject == null) {
			return null;
		}
		
		ArrayList<LayoutDatabase> newLayouts = parseLayouts(newObject);
		ArrayList<LayoutDatabase> updatedLayouts = parseLayouts(updatedObject);
		ArrayList<Integer> removedLayouts = parseLayoutsIds(removedObject);
		
		manageImages(newLayouts, updatedLayouts, removedLayouts);
		
		result.put(ApplicationContentDownloaderAsyncTask.JSON_UPDATED_PAGES_KEY, updatedLayouts);
		result.put(ApplicationContentDownloaderAsyncTask.JSON_REMOVED_PAGES_KEY, removedLayouts);
		result.put(ApplicationContentDownloaderAsyncTask.JSON_NEW_PAGES_KEY, newLayouts);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onPostExecute(HashMap<String, Object> result) {
		if (result == null) {
			removeProgressDialogFragment();
			((ApplicationContentDownloaderFragment)this.fragment).removeFragment();
			
			return;
		}
		
		updateLayouts((ArrayList<LayoutDatabase>)result.get(ApplicationContentDownloaderAsyncTask.JSON_UPDATED_PAGES_KEY));
		removeLayouts((ArrayList<Integer>)result.get(ApplicationContentDownloaderAsyncTask.JSON_REMOVED_PAGES_KEY));
		insertLayouts((ArrayList<LayoutDatabase>)result.get(ApplicationContentDownloaderAsyncTask.JSON_NEW_PAGES_KEY));
		
		removeProgressDialogFragment();
		((ApplicationContentDownloaderFragment)this.fragment).removeFragment();
		((ApplicationContentDownloaderFragment)this.fragment).saveDownloadingTime();
	}
	
	private void removeProgressDialogFragment() {
		FragmentManager fragmentManager = this.fragment.getFragmentManager();
		ProgressDialogFragment progressDialogFragment = (ProgressDialogFragment) fragmentManager.findFragmentByTag(ProgressDialogFragment.TAG);
		if (progressDialogFragment != null) {
			try {
				fragmentManager.beginTransaction().remove(progressDialogFragment).commit();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}
	
	private JSONArray getJSONArrayFromJSONObject(JSONObject jsonObject, String key) {
		try {
			return jsonObject.getJSONArray(key);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Application content downloader JSON error");
			
			return null;
		}
	}
	
	private JSONObject getJSONObjectFromJSONObject(JSONObject jsonObject, String key) {
		try {
			return jsonObject.getJSONObject(key);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Application content downloader JSON error");
			
			return null;
		}
	}
	
	private JSONObject getResponseAsJSON(HttpResponse response) {
		JSONObject responseJson;
		
		try {
			responseJson = new JSONObject(EntityUtils.toString(response.getEntity()));
		} catch (ParseException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Application content downloader response to JSON error");
			
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Application content downloader response to JSON error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Application content downloader response to JSON error");
			
			return null;
		}
		
		return responseJson;
	}
	
	private HttpResponse executeHttpCleint(HttpPost httpPost, DefaultHttpClient httpClient) {
		HttpResponse response;
		int timeouts = 0;
		while (true) {
			try {
				response = httpClient.execute(httpPost);
				
				break;
			} catch (ClientProtocolException e1) {
				e1.printStackTrace();
				
				Log.e(Global.TAG, "response error");
				return null;
			} catch (IOException e1) {
				e1.printStackTrace();
				Log.e(Global.TAG, "response error");
				
				++timeouts;
				if (timeouts == 2) {
					return null;
				}
				
				changeServer(httpPost, Global.API_JSON_ADDRESS);
				
				continue;
			}
		}
		
		return response;
	}
	
	private void changeServer(HttpPost httpPost, String sufix) {
		String ip = RoundRobin.getInstance().getAnotherIP(this.fragment.getActivity().getApplicationContext(), httpPost.getURI().getHost());
		httpPost.setURI(URI.create(ip + sufix));
	}
	
	private boolean setHttpEntity(HttpPost httpPost, JSONObject postEntity) {
		try {
			httpPost.setEntity(new StringEntity(postEntity.toString()));
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
			Log.e(Global.TAG, "Application update http entity error");
			
			return false;
		} catch (ParseException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Application update http entity error");
			
			return false;
		}
		
		return true;
	}
	
	private HttpPost createHttpPost() {
		HttpPost httpPost = new HttpPost(RoundRobin.getInstance().getIP(this.fragment.getActivity().getApplicationContext()) + Global.API_JSON_ADDRESS);
		httpPost.addHeader(ApplicationContentDownloaderAsyncTask.HTTP_POST_HEADER_ACCEPT_KEY, ApplicationContentDownloaderAsyncTask.HTTP_POST_HEADER_ACCEPT_VALUE);
		httpPost.addHeader(ApplicationContentDownloaderAsyncTask.HTTP_POST_HEADER_CONTENT_TYPE_KEY, ApplicationContentDownloaderAsyncTask.HTTP_POST_HEADER_CONTENT_TYPE_VALUE);
		
		return httpPost;
	}
	
	private JSONObject createPostEntity() {
		JSONObject postEntity = new JSONObject();
		try {
			postEntity.put(ApplicationContentDownloaderAsyncTask.METHOD_KEY, ApplicationContentDownloaderAsyncTask.METHOD_VALUE);
		} catch (JSONException e3) {
			e3.printStackTrace();
			Log.e(Global.TAG, "Application content json error");
			
			return null;
		}
		
		ArrayList<Pair<Integer, Integer>> allLayoutVersions = getAllLayoutsVersions();
		JSONObject jsonVersions = createJSONObjectWithVersions(allLayoutVersions);
		try {
			postEntity.put(ApplicationContentDownloaderAsyncTask.PAGES_KEY, jsonVersions);
		} catch (JSONException e3) {
			e3.printStackTrace();
			Log.e(Global.TAG, "Application content json error");
			
			return null;
		}
		
		return postEntity;
	}
	
	private void setTimeout(DefaultHttpClient httpClient) {
		HttpParams httpParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, ApplicationContentDownloaderAsyncTask.SERVER_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, ApplicationContentDownloaderAsyncTask.SERVER_TIMEOUT);
		httpClient.setParams(httpParams);
	}
	
	private int getIntFromJSONObject(JSONObject jsonObject, String key) {
		try {
			return jsonObject.getInt(key);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Application content downloader int from json error");
			
			return -1;
		}
	}
	
	private String getStringFromJSONObject(JSONObject jsonObject, String key) {
		try {
			return jsonObject.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Application content downloader String from json error");
			
			return null;
		}
	}
	
	private double getDoubleFromJSONObject(JSONObject jsonObject, String key) {
		try {
			return jsonObject.getDouble(key);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Application content downloader String from json error");
			
			return -1;
		}
	}
	
	private LayoutDatabase jsonObjectToLayoutDatabase(JSONObject json) {
		LayoutDatabase layoutDatabase = new LayoutDatabase();
		
		if (json.has(ApplicationContentDownloaderAsyncTask.LAYOUT_ID_KEY)) {
			layoutDatabase.setId(getIntFromJSONObject(json, ApplicationContentDownloaderAsyncTask.LAYOUT_ID_KEY));
		}
			
		if (json.has(ApplicationContentDownloaderAsyncTask.LAYOUT_PARENT_ID_KEY)) {
			layoutDatabase.setParentId(getIntFromJSONObject(json, ApplicationContentDownloaderAsyncTask.LAYOUT_PARENT_ID_KEY));
		}
			
		if (json.has(ApplicationContentDownloaderAsyncTask.LAYOUT_INDEX_KEY)) {
			layoutDatabase.setIndex(getIntFromJSONObject(json, ApplicationContentDownloaderAsyncTask.LAYOUT_INDEX_KEY));
		}
			
		if (json.has(ApplicationContentDownloaderAsyncTask.LAYOUT_TITLE_EN_KEY)) {
			layoutDatabase.setTitleEn(getStringFromJSONObject(json, ApplicationContentDownloaderAsyncTask.LAYOUT_TITLE_EN_KEY));
		}
			
		if (json.has(ApplicationContentDownloaderAsyncTask.LAYOUT_TITLE_PL_KEY)) {
			layoutDatabase.setTitlePl(getStringFromJSONObject(json, ApplicationContentDownloaderAsyncTask.LAYOUT_TITLE_PL_KEY));
		}
			
		if (json.has(ApplicationContentDownloaderAsyncTask.LAYOUT_VERSION_KEY)) {
			layoutDatabase.setVersion(getIntFromJSONObject(json, ApplicationContentDownloaderAsyncTask.LAYOUT_VERSION_KEY));
		}
			
		if (json.has(ApplicationContentDownloaderAsyncTask.LAYOUT_TYPE_KEY)) {
			layoutDatabase.setType(getStringFromJSONObject(json, ApplicationContentDownloaderAsyncTask.LAYOUT_TYPE_KEY));
		}
			
		if (json.has(ApplicationContentDownloaderAsyncTask.LAYOUT_CONTENT_EN_KEY)) {
			layoutDatabase.setContentEn(getStringFromJSONObject(json, ApplicationContentDownloaderAsyncTask.LAYOUT_CONTENT_EN_KEY));
		}
		
		if (json.has(ApplicationContentDownloaderAsyncTask.LAYOUT_FAQ_EN_KEY)) {
			layoutDatabase.setContentEn(getStringFromJSONObject(json, ApplicationContentDownloaderAsyncTask.LAYOUT_FAQ_EN_KEY));
		}
			
		if (json.has(ApplicationContentDownloaderAsyncTask.LAYOUT_CONTENT_PL_KEY)) {
			layoutDatabase.setContentPl(getStringFromJSONObject(json, ApplicationContentDownloaderAsyncTask.LAYOUT_CONTENT_PL_KEY));
		}
			
		if (json.has(ApplicationContentDownloaderAsyncTask.LAYOUT_FAQ_PL_KEY)) {
			layoutDatabase.setContentPl(getStringFromJSONObject(json, ApplicationContentDownloaderAsyncTask.LAYOUT_FAQ_PL_KEY));
		}
			
		if (json.has(ApplicationContentDownloaderAsyncTask.LAYOUT_ADDITIONAL_EN_KEY)) {
			layoutDatabase.setAdditionalEn(getStringFromJSONObject(json, ApplicationContentDownloaderAsyncTask.LAYOUT_ADDITIONAL_EN_KEY));
		}
			
		if (json.has(ApplicationContentDownloaderAsyncTask.LAYOUT_ADDITIONAL_PL_KEY)) {
			layoutDatabase.setAdditionalPl(getStringFromJSONObject(json, ApplicationContentDownloaderAsyncTask.LAYOUT_ADDITIONAL_PL_KEY));
		}
			
		if (json.has(ApplicationContentDownloaderAsyncTask.LAYOUT_LATITUDE_KEY)) {
			layoutDatabase.setLatitude((float) getDoubleFromJSONObject(json, ApplicationContentDownloaderAsyncTask.LAYOUT_LATITUDE_KEY));
		}
			
		if (json.has(ApplicationContentDownloaderAsyncTask.LAYOUT_LONGITUDE_KEY)) {
			layoutDatabase.setLongitude((float) getDoubleFromJSONObject(json, ApplicationContentDownloaderAsyncTask.LAYOUT_LONGITUDE_KEY));
		}
			
		if (json.has(ApplicationContentDownloaderAsyncTask.LAYOUT_ZOOM_KEY)) {
			layoutDatabase.setZoom(getIntFromJSONObject(json, ApplicationContentDownloaderAsyncTask.LAYOUT_ZOOM_KEY));
		}
	
		return layoutDatabase;
	}
	
	private ArrayList<Pair<Integer, Integer>> getAllLayoutsVersions() {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.fragment.getActivity());		
		ArrayList<Pair<Integer, Integer>> allLayoutsVersions = databaseAdapter.getAllLayoutsVersions();

		return allLayoutsVersions;
	}

	private JSONObject createJSONObjectWithVersions(ArrayList<Pair<Integer, Integer>> versions){
		JSONObject result = new JSONObject();
		for (Pair<Integer, Integer> entry: versions) {
			try {
				JSONObject version = new JSONObject();
				version.put(ApplicationContentDownloaderAsyncTask.REQUEST_VERSION_KEY, entry.second);
				
				result.put(Integer.toString(entry.first), version);
			} catch (JSONException e) {
				e.printStackTrace();
				
				Log.e(Global.TAG, "database layouts versions error");
			}
		}
		
		return result;
	}
	
	private ArrayList<LayoutDatabase> parseLayouts(JSONArray newObject) {
		ArrayList<LayoutDatabase> newLayouts = new ArrayList<LayoutDatabase>();
		for (int index = 0; index < newObject.length(); ++index) {
			JSONObject layout;
			try {
				layout = newObject.getJSONObject(index);
			} catch (JSONException e) {
				e.printStackTrace();
				
				return new ArrayList<LayoutDatabase>();
			}
			
			newLayouts.add(jsonObjectToLayoutDatabase(layout));
		}
		
		return newLayouts;
	}
	
	private void updateLayouts(ArrayList<LayoutDatabase> layouts) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.fragment.getActivity());
		
		for (LayoutDatabase layout: layouts) {
			databaseAdapter.updateLayout(layout);
		}
	}
	
	private void removeLayouts(ArrayList<Integer> ids) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.fragment.getActivity());
		
		for (Integer id: ids) {
			databaseAdapter.deleteLayout(id);
		}
	}
	
	private void insertLayouts(ArrayList<LayoutDatabase> layouts) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.fragment.getActivity());
		
		for (LayoutDatabase layout: layouts) {
			databaseAdapter.insertLayout(layout);
		}
	}
	
	private ArrayList<Integer> parseLayoutsIds(JSONArray removed) {
		ArrayList<Integer> result = new ArrayList<Integer>();
	
		for (int index = 0; index < removed.length(); ++index) {
			try {
				result.add(removed.getInt(index));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	private void findAndSaveImages(LayoutDatabase layout) {
		if (isTypeWithContent(layout.getType()) && !layout.getContentEn().isEmpty()) {
			findAndSaveImagesFromContent(layout.getContentEn());
		}
		
		if (isTypeWithContent(layout.getType()) && !layout.getContentPl().isEmpty()) {
			findAndSaveImagesFromContent(layout.getContentPl());
		}
		
		if (isTypeWithContent(layout.getType()) && !layout.getAdditionalEn().isEmpty()) {
			findAndSaveImagesFromContent(layout.getAdditionalEn());
		}
		
		if (isTypeWithContent(layout.getType()) && !layout.getAdditionalPl().isEmpty()) {
			findAndSaveImagesFromContent(layout.getAdditionalPl());
		}
	}
	
	private void findAndSaveImagesFromContent(String content) {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlParser = factory.newPullParser();
			xmlParser.setInput(new StringReader(content));
			
			while (xmlParser.getEventType() != XmlPullParser.END_DOCUMENT) {
				if (xmlParser.getEventType() == XmlPullParser.START_TAG &&
						xmlParser.getName().equals(ApplicationContentDownloaderAsyncTask.IMAGE_TAG_NAME)) {
					saveImageInDatabase(xmlParser);
				}
				
				
				xmlParser.next();
			}
			
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "xml error");
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "xml error");
		}
	}
	
	private void saveImageInDatabase(XmlPullParser xmlParser) {
		String url = xmlParser.getAttributeValue(null, ApplicationContentDownloaderAsyncTask.IMAGE_SOURCE_TAG_NAME);
		
		Bitmap bitmap;
		try {
			bitmap = BitmapFactory.decodeStream(new URL(RoundRobin.getInstance().getIP(this.fragment.getActivity().getApplicationContext()) + "/" + url).openStream());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			Log.e(Global.TAG, "image source error");
			
			return;
		} catch (IOException e1) {
			e1.printStackTrace();
			Log.e(Global.TAG, "image source error");
			
			return;
		}
		
		String[] urlParts = url.split("/");
		
		FileOutputStream fileStream;
		try {
			fileStream = this.fragment.getActivity().getApplicationContext().openFileOutput(urlParts[urlParts.length - 1], Context.MODE_PRIVATE);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileStream);
			fileStream.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			Log.e(Global.TAG, "image saving in filesystem error");
			
			return;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "image saving in filesystem error");
			
			return;
		}
		
		try {
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				xmlParser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "xml error");
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "xml error");
		}
	}
	
	private boolean isTypeWithContent(String type) {
		return (type.equals(ApplicationContentDownloaderAsyncTask.LAYOUT_TYPE_TEXT) ||
				type.equals(ApplicationContentDownloaderAsyncTask.LAYOUT_TYPE_LIST) ||
				type.equals(ApplicationContentDownloaderAsyncTask.LAYOUT_TYPE_STEPS) ||
				type.equals(ApplicationContentDownloaderAsyncTask.LAYOUT_TYPE_CONTACT));
	}
	
	private void findAndRemoveImages(int layoutId) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.fragment.getActivity());
		
		LayoutDatabase layout = databaseAdapter.getLayoutDatabase(layoutId);
		
		if (isTypeWithContent(layout.getType()) && !layout.getContentEn().isEmpty()) {
			findAndRemoveImagesFromContent(layout.getContentEn());
		}
		
		if (isTypeWithContent(layout.getType()) && !layout.getContentPl().isEmpty()) {
			findAndRemoveImagesFromContent(layout.getContentPl());
		}
		
		if (isTypeWithContent(layout.getType()) && !layout.getAdditionalEn().isEmpty()) {
			findAndRemoveImagesFromContent(layout.getAdditionalEn());
		}
		
		if (isTypeWithContent(layout.getType()) && !layout.getAdditionalPl().isEmpty()) {
			findAndRemoveImagesFromContent(layout.getAdditionalPl());
		}
	}
	
	private void findAndRemoveImagesFromContent(String content) {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlParser = factory.newPullParser();
			xmlParser.setInput(new StringReader(content));
			
			while (xmlParser.getEventType() != XmlPullParser.END_DOCUMENT) {
				if (xmlParser.getEventType() == XmlPullParser.START_TAG &&
						xmlParser.getName().equals(ApplicationContentDownloaderAsyncTask.IMAGE_TAG_NAME)) {
					removeImageInDatabase(xmlParser);
				}
				
				
				xmlParser.next();
			}
			
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "xml error");
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "xml error");
		}
	}
	
	private void removeImageInDatabase(XmlPullParser xmlParser) {
		String url = xmlParser.getAttributeValue(null, ApplicationContentDownloaderAsyncTask.IMAGE_SOURCE_TAG_NAME);
		String[] urlParts = url.split("/");
		
		this.fragment.getActivity().getApplicationContext().deleteFile(urlParts[urlParts.length - 1]);
		
		try {
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				xmlParser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "xml error");
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "xml error");
		}
	}
	
	private void manageImages(ArrayList<LayoutDatabase> newLayouts, ArrayList<LayoutDatabase> updatedLayouts, ArrayList<Integer> removedLayouts) {
		for (LayoutDatabase layout: newLayouts) {
			findAndSaveImages(layout);
		}
		
		for (LayoutDatabase layout: updatedLayouts) {
			findAndRemoveImages(layout.getId());
			findAndSaveImages(layout);
		}
		
		for (int layoutId: removedLayouts) {
			findAndRemoveImages(layoutId);
		}
	}
}
