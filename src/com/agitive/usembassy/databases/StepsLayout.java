package com.agitive.usembassy.databases;

import java.util.ArrayList;

import android.app.Activity;

import com.agitive.usembassy.interfaces.AdditionalContentInterface;
import com.agitive.usembassy.interfaces.LayoutTypeInterface;

public class StepsLayout implements LayoutTypeInterface, AdditionalContentInterface {

	private int id;
	private int parentId;
	private String titleEn;
	private String titlePl;
	private String contentEn;
	private String contentPl;
	private String additionalEn;
	private String additionalPl;
	private ArrayList<StepsLayoutItem> itemsEn;
	private ArrayList<StepsLayoutItem> itemsPl;
	private Integer leftStepId;
	private String leftStepNameEn;
	private String leftStepNamePl;
	private Integer rightStepId;
	private String rightStepNameEn;
	private String rightStepNamePl;
	
	public StepsLayout(int id, int parentId, Activity activity) { // NO_UCD (use default)
		this.id = id;
		this.parentId = parentId;
		this.titleEn = null;
		this.titlePl = null;
		this.contentEn = null;
		this.contentPl = null;
		this.additionalEn = null;
		this.additionalPl = null;
		this.itemsEn = new ArrayList<StepsLayoutItem>();
		this.itemsPl = new ArrayList<StepsLayoutItem>();
		this.leftStepId = null;
		this.leftStepNameEn = null;
		this.leftStepNamePl = null;
		this.rightStepId = null;
		this.rightStepNameEn = null;
		this.rightStepNamePl = null;
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
	
	public void addStepEn(StepsLayoutItem item) { // NO_UCD (use default)
		this.itemsEn.add(item);
	}
	
	public void addStepPl(StepsLayoutItem item) { // NO_UCD (use default)
		this.itemsPl.add(item);
	}
	
	@Override
	public int getLayoutType() {
		return LayoutTypeInterface.STEPS_LAYOUT;
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
	
	public ArrayList<StepsLayoutItem> getItemsEn() {
		return this.itemsEn;
	}
	
	public ArrayList<StepsLayoutItem> getItemsPl() {
		return this.itemsPl;
	}
	
	public int getLeftStepId() {
		return this.leftStepId;
	}
	
	public String getLeftStepNameEn() {
		return this.leftStepNameEn;
	}
	
	public String getLeftStepNamePl() {
		return this.leftStepNamePl;
	}
	
	public int getRightStepId() {
		return this.rightStepId;
	}
	
	public String getRightStepNameEn() {
		return this.rightStepNameEn;
	}
	
	public String getRightStepNamePl() {
		return this.rightStepNamePl;
	}
	
	public boolean isInSteps() {
		return (this.leftStepId != null || this.rightStepId != null);
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
