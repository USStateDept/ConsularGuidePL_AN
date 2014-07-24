package com.agitive.usembassy.databases;

import java.util.ArrayList;

import com.agitive.usembassy.interfaces.LayoutTypeInterface;

public class FAQLayout implements LayoutTypeInterface {
	
	private int id;
	private int parentId;
	private ArrayList<FAQItem> faqsEn;
	private ArrayList<FAQItem> faqsPl;
	private String titleEn;
	private String titlePl;
	
	public FAQLayout(int id, int parentId) { // NO_UCD (use default)
		this.id = id;
		this.parentId = parentId;
		this.faqsEn = new ArrayList<FAQItem>();
		this.faqsPl = new ArrayList<FAQItem>();
		this.titleEn = null;
		this.titlePl = null;
	}
	
	public void addFaqEn(FAQItem faq) { // NO_UCD (use default)
		this.faqsEn.add(faq);
	}
	
	public void addFaqPl(FAQItem faq) { // NO_UCD (use default)
		this.faqsPl.add(faq);
	}
	
	public void setTitleEn(String title) {
		this.titleEn = title;
	}
	
	public void setTitlePl(String title) {
		this.titlePl = title;
	}
	
	@Override
	public int getLayoutType() {
		return LayoutTypeInterface.FAQ_LAYOUT;
	}
	@Override
	public int getParentId() {
		return this.parentId;
	}
	@Override
	public int getId() {
		return this.id;
	}
	
	public ArrayList<FAQItem> getFaqsEn() {
		return this.faqsEn;
	}
	
	public ArrayList<FAQItem> getFaqsPl() {
		return this.faqsPl;
	}
	
	@Override
	public boolean hasAdditionalContent() {
		return false;
	}
	
	@Override
	public String getTitleEn() {
		return this.titleEn;
	}
	
	@Override
	public String getTitlePl() {
		return this.titlePl;
	}
}
