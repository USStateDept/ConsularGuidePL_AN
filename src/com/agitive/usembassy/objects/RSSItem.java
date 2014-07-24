package com.agitive.usembassy.objects;

public class RSSItem {
	
	public static final String LANGUAGE_ENGLISH = "EN";
	public static final String LANGUAGE_POLISH = "PL";
	
	private String title;
	private String subtitle;
	private String language;
	private String text;
	private String description;
	
	public RSSItem() {
		this.title = "";
		this.subtitle = "";
		this.language = "EN";
		this.text = "";
		this.description = "";
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getSubtitle() {
		return this.subtitle;
	}
	
	public String getLanguage() {
		return this.language;
	}
	
	public String getText() {
		return this.text;
	}
	
	public String getDescription() {
		return this.description;
	}
}
