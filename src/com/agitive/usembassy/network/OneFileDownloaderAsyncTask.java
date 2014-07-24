package com.agitive.usembassy.network;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.agitive.usembassy.fragments.asyncTaskFragments.OneFileDownloaderFragment;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.objects.FileItem;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class OneFileDownloaderAsyncTask extends AsyncTask<Object, Void, Boolean>{

	private static final String LANGUAGE_EN_IN_FILE_NAME = "EN";
	private static final String LANGUAGE_PL_IN_FILE_NAME = "PL";
	
	private FileItem fileItem;
	private Context context;
	
	public OneFileDownloaderAsyncTask(Context context) {
		this.context = context;
	}
	
	@Override
	protected Boolean doInBackground(Object... args) {
		this.fileItem = (FileItem) args[0];
		
		
		boolean downloadResult = downloadFile(fileItem.getId(), fileItem.getUrlEn(), OneFileDownloaderAsyncTask.LANGUAGE_EN_IN_FILE_NAME, RoundRobin.getInstance().getIP(this.context.getApplicationContext()));
		if (downloadResult) {
			downloadResult = downloadFile(fileItem.getId(), fileItem.getUrlPl(), OneFileDownloaderAsyncTask.LANGUAGE_PL_IN_FILE_NAME, RoundRobin.getInstance().getIP(this.context.getApplicationContext()));
			if (downloadResult) {		
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			OneFileDownloaderFragment.setDownloadedFile(this.fileItem);
		} else {
			OneFileDownloaderFragment.setDownloadingError(this.fileItem);
		}
	}
	
	private URL createUrlFile(String serverUrl, String fileUrl) {
		try {
			return new URL(serverUrl + "/" + fileUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "url error");
			
			return null;
		}
	}
	
	private URLConnection createUrlConnection(URL url) {
		try {
			return url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "url connection error");
			
			return null;
		}
	}
	
	private byte [] downloadFileIntoByteArray(URLConnection urlConnection) {
		int fileLength = urlConnection.getContentLength();
		if (fileLength == -1) {
			Log.e(Global.TAG, "Downloading one file getting content length error");
			return null;
		}
		
		
		byte [] fileBytes = new byte [fileLength];
		
		DataInputStream inputStream;
		try {
			inputStream = new DataInputStream(urlConnection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Downloading one file getting input stream error");
			
			return null;
		}
		
		try {
			inputStream.readFully(fileBytes);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Downloading one file reading fully error");
			
			return null;
		}
		
		try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Downloading one file closing input stream error");
			
			return null;
		}
		
		return fileBytes;
	}
	
	private boolean saveFile(byte [] fileBytes, int id, String language) {
		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = this.context.openFileOutput(Integer.toString(id) + "_" + language + ".pdf", Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "One file downloader saving file error");
			
			return false;
		}
		try {
			fileOutputStream.write(fileBytes);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "One file downloader saving file error");
			
			return false;
		}
		try {
			fileOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "One file downloader saving file error");
			
			return false;
		}
		try {
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "One file downloader saving file error");
			
			return false;
		}
		
		return true;
	}
	
	private boolean downloadFile(int id, String fileUrl, String language, String serverUrl) {
		
		byte [] fileBytes;
		
		int serverErrors = 0;
		
		while (true) {
			if (serverErrors == 2) {
				return false;
			}
			
			URL url = createUrlFile(serverUrl, fileUrl);
			if (url == null) {
				++serverErrors;
				serverUrl = RoundRobin.getInstance().getAnotherIP(this.context.getApplicationContext(), serverUrl);
				continue;
			}
			
			URLConnection urlConnection = createUrlConnection(url);
			if (urlConnection == null) {
				++serverErrors;
				serverUrl = RoundRobin.getInstance().getAnotherIP(this.context.getApplicationContext(), serverUrl);
				continue;
			}
			
			fileBytes = downloadFileIntoByteArray(urlConnection);
			if (fileBytes == null) {
				++serverErrors;
				serverUrl = RoundRobin.getInstance().getAnotherIP(this.context.getApplicationContext(), serverUrl);
				continue;
			}
			
			break;
		}
		
		boolean result = saveFile(fileBytes, id, language);
		if (!result) {
			return false;
		}
		
		return true;
	}
}
