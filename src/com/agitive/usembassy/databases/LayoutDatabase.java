package com.agitive.usembassy.databases;

public class LayoutDatabase {
	
	private int id;
	private	int parentId;
	private int index;
	private String titleEn;
	private String titlePl;
	private int version;
	private String type;
	private String contentEn;
	private String contentPl;
	private String additionalEn;
	private String additionalPl;
	private float latitude;
	private float longitude;
	private int zoom;
	
	public LayoutDatabase() {
		this.id = -1;
		this.parentId = -1;
		this.index = -1;
		this.titleEn = "";
		this.titlePl = "";
		this.version = -1;
		this.type = "";
		this.contentEn = "";
		this.contentPl = "";
		this.additionalEn = "";
		this.additionalPl = "";
		this.latitude = -1;
		this.longitude = -1;
		this.zoom = -1;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setParentId(int id) {
		this.parentId = id;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public void setTitleEn(String title) {
		this.titleEn = title;
	}
	
	public void setTitlePl(String title) {
		this.titlePl = title;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setContentEn(String content) {
		this.contentEn = content;
	}
	
	public void setContentPl(String content) {
		this.contentPl = content;
	}
	
	public void setAdditionalEn(String additional) {
		this.additionalEn = additional;
	}
	
	public void setAdditionalPl(String additional) {
		this.additionalPl = additional;
	}
	
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	
	public void setZoom(int zoom) {
		this.zoom = zoom;
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getParentId() {
		return this.parentId;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public String getTitleEn() {
		return this.titleEn;
	}
	
	public String getTitlePl() {
		return this.titlePl;
	}
	
	public int getVersion() {
		return this.version;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getContentEn() {
		return this.contentEn;
	}
	
	public String getContentPl() {
		return this.contentPl;
	}
	
	public String getAdditionalEn() {
		return this.additionalEn;
	}
	
	public String getAdditionalPl() {
		return this.additionalPl;
	}
	
	public float getLatitude() {
		return this.latitude;
	}
	
	public float getLongitude() {
		return this.longitude;
	}
	
	public int getZoom() {
		return this.zoom;
	}
}
