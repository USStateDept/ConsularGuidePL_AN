package com.agitive.usembassy.objects;

public class FileItem {

	private int id;
	private String nameEn;
	private String namePl;
	private int updatedDay;
	private int updatedMonth;
	private int updatedYear;
	private int version;
	private double size;
	private String urlEn;
	private String urlPl;
	private boolean downloading;
	
	public FileItem(int id, String nameEn, String namePl, int updatedDay, int updatedMonth, int updatedYear, int version, double size, String urlPl, String urlEn, boolean downloading) {
		this.id = id;
		this.nameEn = nameEn;
		this.namePl = namePl;
		this.updatedDay = updatedDay;
		this.updatedMonth = updatedMonth;
		this.updatedYear = updatedYear;
		this.version = version;
		this.size = size;
		this.urlEn = urlEn;
		this.urlPl = urlPl;
		this.downloading = downloading;
	}
	
	public void setDownloading(boolean downloading) {
		this.downloading = downloading;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getNameEn() {
		return this.nameEn;
	}
	
	public String getNamePl() {
		return this.namePl;
	}
	
	public int getUpdatedDay() {
		return this.updatedDay;
	}
	
	public int getUpdatedMonth() {
		return this.updatedMonth;
	}
	
	public int getUpdatedYear() {
		return this.updatedYear;
	}
	
	public int getVersion() {
		return this.version;
	}
	
	public double getSize() {
		return this.size;
	}
	
	public String getUrlEn() {
		return this.urlEn;
	}
	
	public String getUrlPl() {
		return this.urlPl;
	}
	
	public boolean getDownloading() {
		return this.downloading;
	}
}
