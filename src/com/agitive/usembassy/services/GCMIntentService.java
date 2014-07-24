package com.agitive.usembassy.services;

import java.util.List;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.broadcastReceivers.GCMBroadcastReceiver;
import com.agitive.usembassy.global.Global;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GCMIntentService extends IntentService {
	
	public static final String IS_ALERT_NOTIFICATION = "com.agitive.usembassy.GCMIntentServce.isAlertNotification";
	public static final String BANNER_TITLE_EN = "com.agitive.usembassy.GCMIntentServce.bannerTitleEn";
	public static final String BANNER_TITLE_PL = "com.agitive.usembassy.GCMIntentServce.bannerTitlePl";
	public static final String BANNER_CONTENT_EN = "com.agitive.usembassy.GCMIntentServce.bannerContentEn";
	public static final String BANNER_CONTENT_PL = "com.agitive.usembassy.GCMIntentServce.bannerContentPl";
	public static final String BANNER_TYPE = "com.agitive.usembassy.GCMIntentServce.bannerType";
	public static final String IS_CONTENT_UPDATE = "com.agitive.usembassy.GCMIntentServce.contentUpdate";
	
	private static final String GCM_NOTIFICATION_TITLE_EN_KEY = "title_en";
	private static final String GCM_NOTIFICATION_TITLE_PL_KEY = "title_pl";
	private static final String GCM_NOTIFICATION_TEXT_EN_KEY = "message_en";
	private static final String GCM_NOTIFICATION_TEXT_PL_KEY = "message_pl";
	private static final String GCM_NOTIFICATION_TYPE_KEY = "type";
	
	private static final int NOTIFICATION_TYPE_BASIC = 0;
	private static final int NOTIFICATION_TYPE_ALERT = 1;
	private static final int NOTIFICATION_TYPE_UPDATE = 2;

	public GCMIntentService() {
		super("GCMIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		
		GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
		String gcmMessageType = googleCloudMessaging.getMessageType(intent);
		
		if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(gcmMessageType)) {
			switch (Integer.parseInt(extras.getString(GCMIntentService.GCM_NOTIFICATION_TYPE_KEY))) {
				case GCMIntentService.NOTIFICATION_TYPE_BASIC:
					showBasicNotification(extras);
					return;
				case GCMIntentService.NOTIFICATION_TYPE_ALERT:
					showAlert(extras);
					return;
				case GCMIntentService.NOTIFICATION_TYPE_UPDATE:
					showUpdate(extras);
					return;
			}
		}
		
		GCMBroadcastReceiver.completeWakefulIntent(intent);
	}
	
	private Notification createNotification(Bundle extras, PendingIntent contentIntent) {
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
		notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
		notificationBuilder.setContentTitle(getTitle(extras));
    	notificationBuilder.setContentText(getText(extras));
    	notificationBuilder.setAutoCancel(true);
    	notificationBuilder.setContentIntent(contentIntent);
    	
    	return notificationBuilder.build();
	}
	
	private void showBasicNotification(Bundle extras) {
		int type = Integer.parseInt(extras.getString(GCMIntentService.GCM_NOTIFICATION_TYPE_KEY));
		
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		PendingIntent contentIntent = PendingIntent.getActivity(this, type, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification notification = createNotification(extras, contentIntent);
		
    	NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
	}
	
	private String getTitle(Bundle extras) {
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			return extras.getString(GCMIntentService.GCM_NOTIFICATION_TITLE_EN_KEY);
		} else {
			return extras.getString(GCMIntentService.GCM_NOTIFICATION_TITLE_PL_KEY);
		}
	}
	
	private String getText(Bundle extras) {
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			return extras.getString(GCMIntentService.GCM_NOTIFICATION_TEXT_EN_KEY);
		} else {
			return extras.getString(GCMIntentService.GCM_NOTIFICATION_TEXT_PL_KEY);
		}
	}
	
	private String getAppLanguage() {
		SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		return sharedPreferences.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
	}
	
	private Intent createAlertIntent(Bundle extras) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(GCMIntentService.BANNER_TITLE_EN, extras.getString(GCMIntentService.GCM_NOTIFICATION_TITLE_EN_KEY));
		intent.putExtra(GCMIntentService.BANNER_TITLE_PL, extras.getString(GCMIntentService.GCM_NOTIFICATION_TITLE_PL_KEY));
		intent.putExtra(GCMIntentService.BANNER_CONTENT_EN, extras.getString(GCMIntentService.GCM_NOTIFICATION_TEXT_EN_KEY));
		intent.putExtra(GCMIntentService.BANNER_CONTENT_PL, extras.getString(GCMIntentService.GCM_NOTIFICATION_TEXT_PL_KEY));
		intent.putExtra(GCMIntentService.BANNER_TYPE, "emergency");
		intent.putExtra(GCMIntentService.IS_ALERT_NOTIFICATION, true);
		
		return intent;
	}
	
	private void showAlert(Bundle extras) {
		int type = Integer.parseInt(extras.getString(GCMIntentService.GCM_NOTIFICATION_TYPE_KEY));
		Intent intent = createAlertIntent(extras);
		PendingIntent contentIntent = PendingIntent.getActivity(this, type, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		try {
			contentIntent.send();
		} catch (CanceledException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Alert banner error");
		}
	}
	
	private Intent createUpdateIntent(Bundle extras) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(GCMIntentService.IS_CONTENT_UPDATE, true);
		
		return intent;
	}
	
	private boolean isAppInForeground(int importance) {
		return (importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND);
	}
	
	private boolean isAppRunning() {
		ActivityManager activityManager = (ActivityManager) this.getSystemService(IntentService.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningApps = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess: runningApps) {
			if (appProcess.processName.equals(getApplicationContext().getPackageName()) &&
					(isAppInForeground(appProcess.importance))) {
				return true;
			}
		}
		
		return false;
	}
	
	private void showUpdate(Bundle extras) {
		if (!isAppRunning()) {
			return;
		}
		
		int type = Integer.parseInt(extras.getString(GCMIntentService.GCM_NOTIFICATION_TYPE_KEY));
		Intent intent = createUpdateIntent(extras);
		PendingIntent contentIntent = PendingIntent.getActivity(this, type, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		try {
			contentIntent.send();
		} catch (CanceledException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "Content update push error");
		}
	}
}
