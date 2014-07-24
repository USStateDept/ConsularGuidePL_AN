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

import com.agitive.usembassy.fragments.layoutFragments.FileManagerFragment;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.objects.FileItem;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

public class FileDownloaderAsyncTask extends AsyncTask<Void, Void, ArrayList<FileItem>> {
	
	private static final int SERVER_TIMEOUT = 3000;
	private Fragment fragment;
	
	public FileDownloaderAsyncTask(Fragment fragment) {
		this.fragment = fragment;
	}
	
	@Override
	protected ArrayList<FileItem> doInBackground(Void... params) {
		ArrayList<NameValuePair> postKeys = new ArrayList<NameValuePair>();
		postKeys.add(new BasicNameValuePair("method", "GetPdfs"));
		
		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(postKeys, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		
		HttpPost httpPost = new HttpPost(RoundRobin.getInstance().getIP(this.fragment.getActivity().getApplicationContext()) + Global.API_POST_ADDRESS);
		httpPost.setEntity(entity);
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		setTimeout(httpClient);
		HttpResponse response = null;
		
		int timeouts = 0;
		while (true) {
			try {
				response = httpClient.execute(httpPost);
				
				break;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				Log.e(Global.TAG, "response error");
				
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(Global.TAG, "response error");
				
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
		
		JSONObject json = null;
		
		try {
			json = new JSONObject(EntityUtils.toString(response.getEntity()));
		} catch (ParseException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "json error");
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "json error");
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "json error");
			return null;
		}
		
		
		ArrayList<FileItem> files = new ArrayList<FileItem>();
		try {
			JSONObject result = json.getJSONObject("result");
			JSONArray pdfs = result.getJSONArray("pdfs");
			
			for (int pdfNumber = 0; pdfNumber < pdfs.length(); ++pdfNumber) {
				JSONObject pdf = pdfs.getJSONObject(pdfNumber);
				
				int id = pdf.getInt("id");
				String nameEn = pdf.getString("name_en");
				String namePl = pdf.getString("name_pl");
				String updated = pdf.getString("updated");
				String [] updatedParts = updated.split("-");
				int updatedDay = Integer.parseInt(updatedParts[2]);
				int updatedMonth = Integer.parseInt(updatedParts[1]);
				int updatedYear = Integer.parseInt(updatedParts[0]);
				int version = pdf.getInt("version");
				double size = pdf.getDouble("size");
				String urlEn = pdf.getString("url_en");
				String urlPl = pdf.getString("url_pl");
				
				files.add(new FileItem(id, nameEn, namePl, updatedDay, updatedMonth, updatedYear, version, size, urlPl, urlEn, false));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "json error");
			return null;
		}
		
		return files;
	}
	
	private void changeServer(HttpPost httpPost, String sufix) {
		String ip = RoundRobin.getInstance().getAnotherIP(this.fragment.getActivity().getApplicationContext(), httpPost.getURI().getHost());
		httpPost.setURI(URI.create(ip + sufix));
	}
	
	@Override
	protected void onPostExecute(ArrayList<FileItem> files) {		
		((FileManagerFragment)this.fragment).setFiles(files);
	}
	
	private void setTimeout(DefaultHttpClient httpClient) {
		HttpParams httpParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, FileDownloaderAsyncTask.SERVER_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, FileDownloaderAsyncTask.SERVER_TIMEOUT);
		httpClient.setParams(httpParams);
	}
}
