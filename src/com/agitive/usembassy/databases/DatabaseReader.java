package com.agitive.usembassy.databases;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.util.Pair;

import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.interfaces.LayoutTypeInterface;

public class DatabaseReader {
	
	public static final int MAIN_SCREEN_LAYOUT_ID = 0;
	public static final int GENERAL_INFORMATION_LAYOUT_ID = 1;
	public static final int VISAS_LAYOUT_ID = 2;
	public static final int SERVICES_FOR_US_CITIZENS_LAYOUT_ID = 3;
	public static final int NEWS_LAYOUT_ID = 4;
	public static final int VISA_APPLICATION_STATUS_LAYOUT_ID = 5;
	public static final int PASSPORT_TRACKING_LAYOUT_ID = 389;
	public static final int CONTACT_INFORMATION_LAYOUT_ID = 274;
	public static final int TNT_LOCATION_LAYOUT_ID = 209;
	public static final int ARTICLE_LAYOUT_ID = -2;
	
	private Activity activity;
	
	public DatabaseReader(Activity activity) {
		this.activity = activity;
	}
	
	public LayoutTypeInterface getLayout(int id) {
		if (id == DatabaseReader.MAIN_SCREEN_LAYOUT_ID) {
			return new MainScreenLayout();
		}
		
		if (id == DatabaseReader.ARTICLE_LAYOUT_ID) {
			return new ArticleLayout(getNewsId());
		}
		
		if (id < -6) {
			return getCustomContentForStep0(id);
		}
		
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.activity);
		LayoutDatabase layout = databaseAdapter.getLayoutDatabase(id);
		
		if (layout.getType().equals(DatabaseAdapter.TYPE_MENU)) {
			return getMenuLayout(id);
		} else if (layout.getType().equals(DatabaseAdapter.TYPE_TEXT)) {
			return getCustomContentLayout(id);
		} else if (layout.getType().equals(DatabaseAdapter.TYPE_LIST)) {
			return getListLayout(id);
		} else if (layout.getType().equals(DatabaseAdapter.TYPE_STEPS)) {
			return getStepsLayout(id);
		} else if (layout.getType().equals(DatabaseAdapter.TYPE_CONTACT)) {
			return getMapLayout(id);
		} else if (layout.getType().equals(DatabaseAdapter.TYPE_STATUS)) {
			
		} else if (layout.getType().equals(DatabaseAdapter.TYPE_FAQ)) {
			return getFAQLayout(id);
		} else if (layout.getType().equals(DatabaseAdapter.TYPE_FILES)) {
			return getFileManagerLayout(id);
		} else if (layout.getType().equals(DatabaseAdapter.TYPE_HEADLINES)) {
			return getAllNewsLayout(id);
		} else if (layout.getType().equals(DatabaseAdapter.TYPE_PUBLICATIONS)) {
			
		} else if (layout.getType().equals(DatabaseAdapter.TYPE_REPORTS)) {
			
		} else if (layout.getType().equals(DatabaseAdapter.TYPE_VIDEOS)) {
			return getVideosLayout(id);
		} else if (layout.getType().equals(DatabaseAdapter.TYPE_FACEBOOK)) {
			return getFacebookLayout(id);
		} else if (layout.getType().equals(DatabaseAdapter.TYPE_PASSPORT)) {
			return getPassportTrackingLayout(id);
		}
		
		return null;
    }
	
	public int getNewsId() {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.activity);
		LayoutDatabase layoutDatabase = databaseAdapter.getLayoutDatabaseByType(DatabaseAdapter.TYPE_HEADLINES);
		
		if (layoutDatabase == null) {
			return -1;
		}
		
		return layoutDatabase.getId();
	}
	
	public ArrayList<Pair<Integer, Integer>> getAllLayoutsVersions() {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.activity);
		ArrayList<Pair<Integer, Integer>> allVersions = databaseAdapter.getAllLayoutsVersions();
		
		return allVersions;
	}
	
	public ArrayList<LayoutTypeInterface> getChildren(int id) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.activity);
		ArrayList<LayoutTypeInterface> children = new ArrayList<LayoutTypeInterface>();
		
		ArrayList<LayoutDatabase> childrenInDatabase = databaseAdapter.getChildren(id);
		
		for (LayoutDatabase childInDatabase: childrenInDatabase) {
			children.add(getLayout(childInDatabase.getId()));
		}
		
		return children;
	}
	
	private boolean hasSubmenu(LayoutDatabase child) {	
		if (child.getType().equals(DatabaseAdapter.TYPE_MENU) ||
				child.getType().equals(DatabaseAdapter.TYPE_STEPS)) {
			return true;
		} else {
			return false;
		}
	}
	
	private CustomContentLayout getCustomContentForStep0(int id) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.activity);
		LayoutDatabase layout = databaseAdapter.getLayoutDatabase(-id);
		
		CustomContentLayout customContentLayout = new CustomContentLayout(-layout.getId(), layout.getId());
		customContentLayout.setTitleEn(layout.getTitleEn());
		customContentLayout.setTitlePl(layout.getTitlePl());
		customContentLayout.setContentEn(layout.getContentEn());
		customContentLayout.setContentPl(layout.getContentPl());
		customContentLayout.setAdditionalEn(layout.getAdditionalEn());
		customContentLayout.setAdditionalPl(layout.getAdditionalPl());
		
		return customContentLayout;
	}
	
	private MenuLayout getMenuLayout(int id) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.activity);
		LayoutDatabase layout = databaseAdapter.getLayoutDatabase(id);
		
		ArrayList<LayoutDatabase> children = databaseAdapter.getChildren(layout.getId());
		
		MenuLayout menuLayout = new MenuLayout(layout.getId(), layout.getParentId());
		menuLayout.setTitleEn(layout.getTitleEn());
		menuLayout.setTitlePl(layout.getTitlePl());
		
		for (LayoutDatabase child: children) {
			menuLayout.addMenuItemEn(new MenuLayoutItem(child.getTitleEn(), child.getId(), hasSubmenu(child)));
			menuLayout.addMenuItemPl(new MenuLayoutItem(child.getTitlePl(), child.getId(), hasSubmenu(child)));
		}
		
		return menuLayout;
	}
	
	private CustomContentLayout getCustomContentLayout(int id) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.activity);
		LayoutDatabase layout = databaseAdapter.getLayoutDatabase(id);
		
		CustomContentLayout customContentLayout = new CustomContentLayout(layout.getId(), layout.getParentId());
		customContentLayout.setTitleEn(layout.getTitleEn());
		customContentLayout.setTitlePl(layout.getTitlePl());
		customContentLayout.setContentEn(layout.getContentEn());
		customContentLayout.setContentPl(layout.getContentPl());
		customContentLayout.setAdditionalEn(layout.getAdditionalEn());
		customContentLayout.setAdditionalPl(layout.getAdditionalPl());
		
		return customContentLayout;
	}
	
	private ListLayout getListLayout(int id) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.activity);
		LayoutDatabase layout = databaseAdapter.getLayoutDatabase(id);
		
		ListLayout listLayout = new ListLayout(layout.getId(), layout.getParentId());
		listLayout.setTitleEn(layout.getTitleEn());
		listLayout.setTitlePl(layout.getTitlePl());
		listLayout.setContentEn(layout.getContentEn());
		listLayout.setContentPl(layout.getContentPl());
		listLayout.setAdditionalEn(layout.getAdditionalEn());
		listLayout.setAdditionalPl(layout.getAdditionalPl());
		
		ArrayList<LayoutDatabase> children = databaseAdapter.getChildren(layout.getId());
		
		for (LayoutDatabase child: children) {
			listLayout.addListItemEn(new ListLayoutItem(child.getId(), child.getTitleEn()));
			listLayout.addListItemPl(new ListLayoutItem(child.getId(), child.getTitlePl()));
		}
			
		return listLayout;
	}
	
	private StepsLayout getStepsLayout(int id) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.activity);
		LayoutDatabase layout = databaseAdapter.getLayoutDatabase(id);
		
		StepsLayout stepsLayout = new StepsLayout(layout.getId(), layout.getParentId(), this.activity);
		stepsLayout.setTitleEn(layout.getTitleEn());
		stepsLayout.setTitlePl(layout.getTitlePl());
		stepsLayout.setContentEn(layout.getContentEn());
		stepsLayout.setContentPl(layout.getContentPl());
		stepsLayout.setAdditionalEn(layout.getAdditionalEn());
		stepsLayout.setAdditionalPl(layout.getAdditionalPl());
		
		ArrayList<LayoutDatabase> children = databaseAdapter.getChildren(layout.getId());
		for (LayoutDatabase child: children) {
			stepsLayout.addStepEn(new StepsLayoutItem(child.getId(), child.getTitleEn(), hasSubmenu(child)));
			stepsLayout.addStepPl(new StepsLayoutItem(child.getId(), child.getTitlePl(), hasSubmenu(child)));
		}
		
		return stepsLayout;
	}
	
	private MapLayout getMapLayout(int id) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.activity);
		LayoutDatabase layout = databaseAdapter.getLayoutDatabase(id);
		
		MapLayout mapLayout = new MapLayout(layout.getId(), layout.getParentId());
		mapLayout.setTitleEn(layout.getTitleEn());
		mapLayout.setTitlePl(layout.getTitlePl());
		mapLayout.setContentEn(layout.getContentEn());
		mapLayout.setContentPl(layout.getContentPl());
		mapLayout.setAdditionalEn(layout.getAdditionalEn());
		mapLayout.setAdditionalPl(layout.getAdditionalPl());
		mapLayout.setLatitude(layout.getLatitude());
		mapLayout.setLongitude(layout.getLongitude());
		mapLayout.setZoom(layout.getZoom());
		
		return mapLayout;
	}
	
	private FAQLayout getFAQLayout(int id) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.activity);
		LayoutDatabase layout = databaseAdapter.getLayoutDatabase(id);
		
		FAQLayout faqLayout = new FAQLayout(layout.getId(), layout.getParentId());
		faqLayout.setTitleEn(layout.getTitleEn());
		faqLayout.setTitlePl(layout.getTitlePl());
		
		try {
			if (!layout.getContentEn().isEmpty()) {
				JSONArray faqsEn = new JSONArray(layout.getContentEn());
				for (int index = 0; index < faqsEn.length(); ++index) {
					FAQItem faq = new FAQItem();
					JSONObject jsonFAQ = faqsEn.getJSONObject(index);
					faq.setQuestion(jsonFAQ.getString("question"));
					faq.setAnswer(jsonFAQ.getString("answer"));
					faqLayout.addFaqEn(faq);
				}
			}
			
			if (!layout.getContentPl().isEmpty()) {
				JSONArray faqsPl = new JSONArray(layout.getContentPl());
				for (int index = 0; index < faqsPl.length(); ++index) {
					FAQItem faq = new FAQItem();
					JSONObject jsonFAQ = faqsPl.getJSONObject(index);
					faq.setQuestion(jsonFAQ.getString("question"));
					faq.setAnswer(jsonFAQ.getString("answer"));
					faqLayout.addFaqPl(faq);
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			
			Log.e(Global.TAG, "faqs json error");
		}
		
		return faqLayout;
	}
	
	private FileManagerLayout getFileManagerLayout(int id) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.activity);
		LayoutDatabase layout = databaseAdapter.getLayoutDatabase(id);
		
		FileManagerLayout fileManagerLayout = new FileManagerLayout(layout.getId(), layout.getParentId());
		fileManagerLayout.setTitleEn(layout.getTitleEn());
		fileManagerLayout.setTitlePl(layout.getTitlePl());
		
		return fileManagerLayout;
	}
	
	private AllNewsLayout getAllNewsLayout(int id) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.activity);
		LayoutDatabase layout = databaseAdapter.getLayoutDatabase(id);
		
		AllNewsLayout allNewsLayout = new AllNewsLayout(layout.getId(), layout.getParentId());
		allNewsLayout.setTitleEn(layout.getTitleEn());
		allNewsLayout.setTitlePl(layout.getTitlePl());
		
		return allNewsLayout;
	}
	
	private VideosLayout getVideosLayout(int id) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.activity);
		LayoutDatabase layout = databaseAdapter.getLayoutDatabase(id);
		
		VideosLayout videosLayout = new VideosLayout(layout.getId(), layout.getParentId());
		videosLayout.setTitleEn(layout.getTitleEn());
		videosLayout.setTitlePl(layout.getTitlePl());
		
		return videosLayout;
	}
	
	private FacebookLayout getFacebookLayout(int id) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.activity);
		LayoutDatabase layout = databaseAdapter.getLayoutDatabase(id);
		
		FacebookLayout facebookLayout = new FacebookLayout(layout.getId(), layout.getParentId());
		facebookLayout.setTitleEn(layout.getTitleEn());
		facebookLayout.setTitlePl(layout.getTitlePl());
		
		return facebookLayout;
	}
	
	private PassportTrackingLayout getPassportTrackingLayout(int id) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.activity);
		LayoutDatabase layout = databaseAdapter.getLayoutDatabase(id);
		
		PassportTrackingLayout passportTrackingLayout = new PassportTrackingLayout(layout.getId(), layout.getParentId());
		passportTrackingLayout.setTitleEn(layout.getTitleEn());
		passportTrackingLayout.setTitlePl(layout.getTitlePl());
		
		return passportTrackingLayout;
	}
}
