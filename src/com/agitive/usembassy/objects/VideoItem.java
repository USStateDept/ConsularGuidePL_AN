package com.agitive.usembassy.objects;

public class VideoItem {
	private int id;
	private String titleEn;
	private String titlePl;
	private int day;
	private int month;
	private int year;
	private String[] urls;
	private String miniatureUrl;
	private boolean isLocalSource;
	
	public VideoItem() {
		this.id = 0;
		this.titleEn = "";
		this.titlePl = "";
		this.day = 0;
		this.month = 0;
		this.year = 0;
		this.urls = null;
		this.isLocalSource = false;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setTitleEn(String title) {
		this.titleEn = title;
	}
	
	public void setTitlePl(String title) {
		this.titlePl = title;
	}
	
	public void setDay(int day) {
		this.day = day;
	}
	
	public void setMonth(int month) {
		this.month = month;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public void setUrls(String[] urls) {
		this.urls = urls;
	}
	
	public void setIsLocalSource(boolean isLocalSource) {
		this.isLocalSource = isLocalSource;
	}
	
	public void setMiniatureUrl(String url) {
		this.miniatureUrl = url;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getTitleEn() {
		return this.titleEn;
	}
	
	public String getTitlePl() {
		return this.titlePl;
	}
	
	public int getDay() {
		return this.day;
	}
	
	public int getMonth() {
		return this.month;
	}
	
	public int getYear() {
		return this.year;
	}
	
	public String[] getUrls() {
		return this.urls;
	}
	
	public boolean getIsLocalSource() {
		return this.isLocalSource;
	}
	
	public String getMiniatureUrl() {
		return this.miniatureUrl;
	}
}
