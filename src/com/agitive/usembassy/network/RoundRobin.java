package com.agitive.usembassy.network;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.privateKeys.AgitivePrivateKeys;

public class RoundRobin { // NO_UCD (use default)
	
	private static RoundRobin instance = null;
	private static String[] IPS = {AgitivePrivateKeys.SERVER_MASTER, AgitivePrivateKeys.SERVER_SLAVE};
	private static final String SHARED_PREFERENCES_LAST_TIME_RAND_KEY = "com.agitive.usembassy.network.RoundRobin.sharedPreferencesLastTimeRandKey";
	private static final String SHARED_PREFERENCES_IP_KEY = "com.agitive.usembassy.network.RoundRobin.ipKey";
	private static final int ONE_HOUR_MILISECONDS = 3600000;
	private static final String DATE_FORMAT = "dd-MM-yyy HH:mm:ss";
	
	public static RoundRobin getInstance() { // NO_UCD (use default)
		if (RoundRobin.instance == null) {
			RoundRobin.instance = new RoundRobin(); 
		}
		
		return RoundRobin.instance;
	}
	
	public String getMasterIP() {
		return RoundRobin.IPS[0];
	}
	
	public String getAnotherIP(Context context, String ip) { // NO_UCD (use default)
		for (String anotherIP: RoundRobin.IPS) {
			if (!anotherIP.contains(ip)) {
				saveIpToSharedPreferences(context, anotherIP);
				return anotherIP;
			}
		}
		
		return null;
	}
	
	public String getIP(Context context) { // NO_UCD (use default)
		if (isIPRanded(context)) {
			if (isIPUpToDate(context)) {
				return loadIPFromSharedPreferences(context);
			} else {
				randIPAndSaveToSharedPreferences(context);
				
				return loadIPFromSharedPreferences(context);
			}
		} else {
			randIPAndSaveToSharedPreferences(context);

			return loadIPFromSharedPreferences(context);
		}
	}
	
	private RoundRobin() {
		
	}
	
	private String loadIPFromSharedPreferences(Context context) {
		SharedPreferences settings = context.getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
		return settings.getString(RoundRobin.SHARED_PREFERENCES_IP_KEY, null);
	}
	
	private void randIPAndSaveToSharedPreferences(Context context) {
		SharedPreferences settings = context.getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putString(RoundRobin.SHARED_PREFERENCES_IP_KEY, randIP());
		editor.putString(RoundRobin.SHARED_PREFERENCES_LAST_TIME_RAND_KEY, getDateNowAsString());
		
		editor.commit();
	}
	
	private void saveIpToSharedPreferences(Context context, String ip) {
		SharedPreferences settings = context.getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putString(RoundRobin.SHARED_PREFERENCES_IP_KEY, ip);
		editor.putString(RoundRobin.SHARED_PREFERENCES_LAST_TIME_RAND_KEY, getDateNowAsString());
		
		editor.commit();
	}
	
	private String getDateNowAsString() {
		Date dateNow = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RoundRobin.DATE_FORMAT);
		
		return simpleDateFormat.format(dateNow);
	}
	
	private String randIP() {
		Random random = new Random();
		int ipIndex = random.nextInt(RoundRobin.IPS.length);
		
		return RoundRobin.IPS[ipIndex];
	}
	
	private boolean isIPRanded(Context context) {
		SharedPreferences settings = context.getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
		return settings.contains(RoundRobin.SHARED_PREFERENCES_LAST_TIME_RAND_KEY);
	}
	
	private boolean isIPUpToDate(Context context) {
		SharedPreferences settings = context.getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
		String dateString = settings.getString(RoundRobin.SHARED_PREFERENCES_LAST_TIME_RAND_KEY, null);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RoundRobin.DATE_FORMAT);
		Date date;
		try {
			date = simpleDateFormat.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Round Robin date parsing error");
			
			return false;
		}
		
		Date dateOneHourEarlier = new Date(System.currentTimeMillis() - RoundRobin.ONE_HOUR_MILISECONDS);
		
		return (!date.before(dateOneHourEarlier));
	}
}
