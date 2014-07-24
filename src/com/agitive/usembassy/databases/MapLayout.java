package com.agitive.usembassy.databases;

import com.agitive.usembassy.interfaces.AdditionalContentInterface;
import com.agitive.usembassy.interfaces.LayoutTypeInterface;

public class MapLayout implements LayoutTypeInterface, AdditionalContentInterface {

	private int id;
	private int parentId;
	private String titleEn;
	private String titlePl;
	private String contentEn;
	private String contentPl;
	private String additionalEn;
	private String additionalPl;
	private float latitude;
	private float longitude;
	private int zoom;
	
	public MapLayout(int id, int parentId) { // NO_UCD (use default)
		this.id = id;
		this.parentId = parentId;
		this.titleEn = null;
		this.titlePl = null;
		this.contentEn = null;
		this.contentPl = null;
		this.additionalEn = null;
		this.additionalPl = null;
		this.latitude = 0;
		this.longitude = 0;
		this.zoom = 0;
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
	
	@Override
	public int getLayoutType() {
		return LayoutTypeInterface.CONTACT_LAYOUT;
	}

	@Override
	public int getParentId() {
		return this.parentId;
	}

	@Override
	public int getId() {
		return this.id;
	}
	
	@Override
	public String getTitleEn() {
		return this.titleEn;
	}
	
	@Override
	public String getTitlePl() {
		return this.titlePl;
	}
	
	public String getContentEn() {
		return this.contentEn;
	}
	
	public String getContentPl() {
		return this.contentPl;
	}
	
	@Override
	public String getAdditionalEn() {
		return this.additionalEn;
	}
	
	@Override
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
	
	@Override
	public boolean hasAdditionalContent() {
		if ((this.additionalEn == null ||
				this.additionalEn.equals("")) &&
				(this.additionalPl == null ||
				this.additionalPl.equals(""))) {
			return false;
		} else {
			return true;
		}
	}
}
