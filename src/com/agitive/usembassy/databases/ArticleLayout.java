package com.agitive.usembassy.databases;

import com.agitive.usembassy.interfaces.LayoutTypeInterface;

public class ArticleLayout implements LayoutTypeInterface {
	
	private int parentId;
	
	public ArticleLayout(int parentId) {
		this.parentId = parentId;
	}
	
	@Override
	public int getLayoutType() {
		return LayoutTypeInterface.ARTICLE_LAYOUT;
	}

	@Override
	public int getId() {
		return DatabaseReader.ARTICLE_LAYOUT_ID;
	}
	
	@Override
	public int getParentId() {
		return this.parentId;
	}

	@Override
	public boolean hasAdditionalContent() {
		return false;
	}
	
	@Override
	public String getTitleEn() {
		return null;
	}
	
	@Override
	public String getTitlePl() {
		return null;
	}
}
