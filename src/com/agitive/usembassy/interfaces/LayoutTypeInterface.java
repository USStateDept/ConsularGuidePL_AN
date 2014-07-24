package com.agitive.usembassy.interfaces;

public interface LayoutTypeInterface {

	static public final int MENU_LAYOUT = 0;
	static public final int LIST_LAYOUT = 1;
	static public final int TEXT_LAYOUT = 2;
	static public final int STEPS_LAYOUT = 3;
	static public final int NEWS_LAYOUT = 4;
	static public final int ARTICLE_LAYOUT = 5;
	static public final int VIDEOS_LAYOUT = 6;
	static public final int FILE_MANAGER_LAYOUT = 7;
	static public final int CONTACT_LAYOUT = 8;
	static public final int FAQ_LAYOUT = 9;
	static public final int FACEBOOK_LAYOUT = 10;
	static public final int MAIN_SCREEN_LAYOUT = 11;
	static public final int PASSPORT_TRACKING_LAYOUT = 12;
	
	public int getLayoutType();
	public int getParentId();
	public int getId();
	public boolean hasAdditionalContent();
	public String getTitleEn();
	public String getTitlePl();
}
