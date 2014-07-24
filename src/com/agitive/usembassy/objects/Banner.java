package com.agitive.usembassy.objects;

import com.agitive.usembassy.R;

import android.content.Context;

public class Banner {
	
	public static final int TYPE_EMERGENCY = 0;
	public static final int TYPE_SECURITY_ADVISORY = 1;
	public static final int TYPE_CALENDAR_UPDATE = 2;
	public static final int TYPE_NEW_MEDIA_RELEASE = 3;
	public static final int TYPE_GENERAL_UPDATE = 4;
	
	private String titleEn;
	private String titlePl;
	private String contentEn;
	private String contentPl;
	private String type;
	private Context context;
	
	public Banner(Context context) {
		this.titleEn = "";
		this.titlePl = "";
		this.contentEn = "";
		this.contentPl = "";
		this.context = context;
	}
	
	public void setTitleEn(String title) {
		this.titleEn = title;
	}
	
	public void setTitlePl(String title) {
		this.titlePl = title;
	}
	
	public void setContentEn(String content) {
		this.contentEn = content;
	}
	
	public void setContentPl(String content) {
		this.contentPl = content;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setType(int type) {
		switch (type) {
			case Banner.TYPE_EMERGENCY:
				this.type = "emergency";
				break;
			case Banner.TYPE_SECURITY_ADVISORY:
				this.type = "sec_adv";
				break;
			case Banner.TYPE_CALENDAR_UPDATE:
				this.type = "calendar";
				break;
			case Banner.TYPE_NEW_MEDIA_RELEASE:
				this.type = "media";
				break;
			case Banner.TYPE_GENERAL_UPDATE:
				this.type = "general";
				break;
		}
	}
	
	public String getTitleEn() {
		return this.titleEn;
	}
	
	public String getTitlePl() {
		return this.titlePl;
	}
	
	public String getContentEn() {
		return this.contentEn;
	}
	
	public String getContentPl() {
		return this.contentPl;
	}
	
	public int getType() {
		if (this.type.equals("emergency")) {
			return Banner.TYPE_EMERGENCY;
		} else if (this.type.equals("sec_adv")) {
			return Banner.TYPE_SECURITY_ADVISORY;
		} else if (this.type.equals("calendar")) {
			return Banner.TYPE_CALENDAR_UPDATE;
		} else if (this.type.equals("media")) {
			return Banner.TYPE_NEW_MEDIA_RELEASE;
		} else if (this.type.equals("general")) {
			return Banner.TYPE_GENERAL_UPDATE;
		}
		
		return -1;
	}
	
	public String getTypeText() {
		if (this.type.equals("emergency")) {
			return this.context.getResources().getString(R.string.banner_type_emergency);
		} else if (this.type.equals("sec_adv")) {
			return this.context.getResources().getString(R.string.banner_type_security_advisory);
		} else if (this.type.equals("calendar")) {
			return this.context.getResources().getString(R.string.banner_type_calendar_update);
		} else if (this.type.equals("media")) {
			return this.context.getResources().getString(R.string.banner_type_new_media_release);
		} else if (this.type.equals("general")) {
			return this.context.getResources().getString(R.string.banner_type_general_update);
		}
		
		return "";
	}
}
