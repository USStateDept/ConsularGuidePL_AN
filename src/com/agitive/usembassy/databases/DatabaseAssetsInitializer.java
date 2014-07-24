package com.agitive.usembassy.databases;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.agitive.usembassy.global.Global;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseAssetsInitializer extends SQLiteAssetHelper {

	private static final String DATABASE_NAME = "usembassydb.db"; 
	private static final int DATABASE_VERSION = 1;
	private static final String IMAGES_PATH_ROOT = "layout_images";
	
	private Context context;
	
	public DatabaseAssetsInitializer(Context context) {
		super(context, DatabaseAssetsInitializer.DATABASE_NAME, null, DatabaseAssetsInitializer.DATABASE_VERSION);
		
		this.context = context;
		
		this.getReadableDatabase();
		copyImages();
	}
	
	private InputStream getFileInputStream(String imageName) {
		AssetManager assetManager = this.context.getAssets();
		
		InputStream inputStream;
		try {
			inputStream = assetManager.open(DatabaseAssetsInitializer.IMAGES_PATH_ROOT + "/" + imageName);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "image from assets saving in filesystem error");
			
			return null;
		}
		
		return inputStream;
	}
	
	private FileOutputStream createFileOutputStream(String fileName) {
		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = this.context.openFileOutput(fileName, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "image from assets saving in filesystem error");
			
			return null;
		}
		
		return fileOutputStream;
	}
	
	private boolean isFileInInternalStorage(String imageName) {
		String[] fileList = this.context.fileList();
		for (String fileName: fileList) {
			if (fileName.equals(imageName)) {
				return true;
			}
		}
		
		return false;
	}
	
	private void copyImage(String imageName) {
		InputStream inputStream = getFileInputStream(imageName);
		if (inputStream == null) {
			return;
		}
		
		FileOutputStream fileOutputStream = createFileOutputStream(imageName);
		if (fileOutputStream == null) {
			return;
		}
		
		byte[] buffer = new byte[1024];
		int readedBytesCount;
		do {
			readedBytesCount = readBytesIntoBuffer(inputStream, buffer);
			if (readedBytesCount != -1) {
				writeBytesIntoFile(fileOutputStream, buffer, readedBytesCount);
			}
		} while (readedBytesCount != -1);
		
		closeInputFile(inputStream);
		closeFileOutputStream(fileOutputStream);
	}
	
	private void closeFileOutputStream(FileOutputStream fileOutputStream) {
		try {
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "image from assets saving in filesystem error");
			
			return;
		}
	}
	
	private void closeInputFile(InputStream inputStream) {
		try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "image from assets saving in filesystem error");
			
			return;
		}
	}
	
	private void writeBytesIntoFile(FileOutputStream fileOutputStream, byte[] buffer, int readedBytesCount) {
		try {
			fileOutputStream.write(buffer, 0, readedBytesCount);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "image from assets saving in filesystem error");
			
			return;
		}
	}
	
	private int readBytesIntoBuffer(InputStream inputStream, byte[] buffer) {
		int result;
		
		try {
			result = inputStream.read(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "image from assets saving in filesystem error");
			
			return -1;
		}
		
		return result;
	}
	
	private String[] getImagesFiles() {
		AssetManager assetManager = this.context.getAssets();
		String[] imagesFilesPaths;
		
		try {
			imagesFilesPaths = assetManager.list(DatabaseAssetsInitializer.IMAGES_PATH_ROOT);
		} catch (IOException e) {
			e.printStackTrace();
			
			return null;
		}
		
		return imagesFilesPaths;
	}
	
	private void copyImages() {
		String[] imagesNames = getImagesFiles();
		if (imagesNames == null) {
			return;
		}
		
		for (String imageName: imagesNames) {
			if (isFileInInternalStorage(imageName)) {
				continue;
			}
			
			copyImage(imageName);
		}
	}
}
