package com.agitive.usembassy.parsers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.databases.DatabaseReader;
import com.agitive.usembassy.fragments.dialogFragments.PhoneCallDialogFragment;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.interfaces.LayoutTypeInterface;
import com.agitive.usembassy.layouts.CustomTextView;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class CustomContentParser {

	private static String CONTENT_NAME = "content";
	private static String PHONE_NAME = "phone";
	private static String ROW_NAME = "row";
	private static String MAIL_NAME = "mail";
	private static String HOURS_NAME = "hours";
	private static String PLACE_NAME = "place";
	private static String NAME_NAME = "name";
	private static String CONTACT_NAME = "contact";
	private static String INFO_NAME = "info";
	private static String CATEGORIES_NAME = "categories";
	private static String CITEM_NAME = "citem";
	private static String CNAME_NAME = "cname";
	private static String CTEXT_NAME = "ctext";
	private static String BUTTON_NAME = "btn";
	private static String H1_NAME = "h1";
	private static String H2_NAME = "h2";
	private static String STRONG_NAME = "strong";
	private static String UL_NAME = "ul";
	private static String OL_NAME = "ol";
	private static String LI_NAME = "li";
	private static String PARAGRAPH_NAME = "p";
	private static String PHONE_NUMBER_NAME = "data-number";
	private static String MAIL_ADDRESS_NAME = "data-address";
	private static String BUTTON_URL_NAME = "data-url";
	private static String BUTTON_PAGE_NAME = "data-page";
	private static String SHORT_INFO_NAME = "shortinfo";
	private static String LONG_INFO_NAME = "longinfo";
	private static String IMAGE_NAME = "img";
	private static String CITEM_PAGE_NAME = "data-page";
	private static final double BUTTON_WIDTH_TO_CONTENT_WIDTH_LONG_INFO_BOX = 0.6;
	private static final double BUTTON_WIDTH_TO_CONTENT_WIDTH = 0.75;
	private static final int CNAME_HEIGHT_TO_NORMAL_TEXT_SIZE = 5;
	
	private int id;
	private RelativeLayout popUp;
	
	private FragmentActivity activity;
	private int contentWidth;
	private int layoutId;
	
	public CustomContentParser(FragmentActivity activity, int contentWidth, int layoutId) {
		this.activity = activity;
		this.contentWidth = contentWidth;
		this.layoutId = layoutId;
	}
	
	public RelativeLayout parseCustomContent(String content) {
		RelativeLayout rootLayout = new RelativeLayout(this.activity);
		
		RelativeLayout customContentLayout = new RelativeLayout(this.activity);
		++this.id;
		customContentLayout.setId(this.id);
		RelativeLayout.LayoutParams customContentParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		customContentLayout.setLayoutParams(customContentParams);
		
		popUp = new RelativeLayout(this.activity);
		++this.id;
		popUp.setId(this.id);
		
		popUp.setBackgroundResource(R.drawable.info_view_background);
		
		popUp.setVisibility(RelativeLayout.GONE);
		
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlParser = factory.newPullParser();
			xmlParser.setInput(new StringReader(content));
			
			xmlParser.next();
			boolean isRoot = true;
			int previousViewId = -1;
			String previousTagName = "";
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG) {
					xmlParser.next();
				}
				
				if (isTagName(xmlParser, CustomContentParser.CONTENT_NAME) && isRoot) {
					isRoot = false;
				} else {
					String tagName = getName(xmlParser); 
					View newView = createView(xmlParser);
					if (newView != null) {		
						setTypography(newView, tagName, previousViewId, previousTagName, false);
						
						customContentLayout.addView(newView);
						
						previousViewId = newView.getId();
						previousTagName = tagName;
					}
				}
				
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG) {
					xmlParser.next();
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "custom content xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "custom content xml parsing error");
			
			return null;
		}
		
		rootLayout.addView(customContentLayout);
		rootLayout.addView(popUp);
		
		return rootLayout;
	}
	
	private boolean isTagName(XmlPullParser xmlParser, String name) {
		try {
			return (xmlParser.getEventType() == XmlPullParser.START_TAG &&
					(xmlParser.getName().equals("div") &&
					xmlParser.getAttributeValue(null, "class").equals(name))) ||
					(xmlParser.getEventType() == XmlPullParser.START_TAG &&
					(xmlParser.getName().equals("a") &&
					xmlParser.getAttributeValue(null, "class").equals(name))) ||
					(xmlParser.getEventType() == XmlPullParser.START_TAG &&
					xmlParser.getName().equals(name));
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "tag name xml parsing error");
			
			return false;
		}
	}
	
	private String getName(XmlPullParser xmlParser) {
		if (xmlParser.getName().equals("div")) {
			return xmlParser.getAttributeValue(null, "class");
		} else if (xmlParser.getName().equals("a")) {
			return xmlParser.getAttributeValue(null, "class");
		}
		else {
			return xmlParser.getName();
		}
	}
	
	private View createView(XmlPullParser xmlParser) {
		String tagName = getName(xmlParser);
		
		if (tagName.equals(PHONE_NAME)) {
			return createPhoneView(xmlParser);
		} else if (tagName.equals(ROW_NAME)) {
			return createRowView(xmlParser);
		} else if (tagName.equals(MAIL_NAME)) {
			return createMailView(xmlParser);
		} else if (tagName.equals(HOURS_NAME)) {
			return createHoursView(xmlParser);
		} else if (tagName.equals(PLACE_NAME)) {
			return createPlaceView(xmlParser);
		} else if (tagName.equals(NAME_NAME)) {
			return createNameView(xmlParser);
		} else if (tagName.equals(CONTACT_NAME)) {
			return createContactView(xmlParser);
		} else if (tagName.equals(INFO_NAME)) {
			return createInfoView(xmlParser);
		} else if (tagName.equals(CATEGORIES_NAME)) {
			return createCategoriesView(xmlParser);
		} else if (tagName.equals(CITEM_NAME)) {
			return createCitemView(xmlParser);
		} else if (tagName.equals(CNAME_NAME)) {
			return createCnameView(xmlParser);
		} else if (tagName.equals(CTEXT_NAME)) {
			return createCtextView(xmlParser);
		} else if (tagName.equals(BUTTON_NAME)) {
			return createButtonView(xmlParser);
		} else if (tagName.equals(H1_NAME)) {
			return createH1View(xmlParser);
		} else if (tagName.equals(H2_NAME)) {
			return createH2View(xmlParser);
		} else if (tagName.equals(UL_NAME)) {
			return createUlView(xmlParser);
		} else if (tagName.equals(OL_NAME)) {
			return createOlView(xmlParser);
		} else if (tagName.equals(LI_NAME)) {
			return createLiView(xmlParser);
		} else if (tagName.equals(PARAGRAPH_NAME)) {
			return createParagraphView(xmlParser);
		} else if (tagName.equals(SHORT_INFO_NAME)) {
			return createShortInfoView(xmlParser);
		} else if (tagName.equals(LONG_INFO_NAME)) {
			return createLongInfoView(xmlParser);
		} else if (tagName.equals(IMAGE_NAME)) {
			return createImageView(xmlParser);
		}
		
		return null;
	}
	
	private View createPhoneView(XmlPullParser xmlParser) {
		RelativeLayout phoneViewLayout = new RelativeLayout(this.activity);
		++this.id;
		phoneViewLayout.setId(this.id);
		
		ImageView phoneImage = new ImageView(this.activity);
		++this.id;
		phoneImage.setId(this.id);
		phoneImage.setImageResource(R.drawable.phone);
		
		RelativeLayout.LayoutParams phoneImageParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		phoneImageParams.rightMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_small);
		phoneImage.setLayoutParams(phoneImageParams);
		
		phoneViewLayout.addView(phoneImage);
		
		final String number = xmlParser.getAttributeValue(null, PHONE_NUMBER_NAME);
		if (number != null) {
			phoneImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					phoneImageOnClick(number);
				}
			});
		}
		
		boolean isRoot = true;
		int previousViewId = -1;
		try {
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG) {
					xmlParser.next();
				}
				
				if (isTagName(xmlParser, PHONE_NAME) && isRoot) {
					isRoot = false;
				} else {
					View newView = createView(xmlParser);
					if (newView != null) {
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						
						if (previousViewId == -1) {
							params.addRule(RelativeLayout.RIGHT_OF, phoneImage.getId());
							params.addRule(RelativeLayout.ALIGN_TOP, phoneImage.getId());
						} else {
							params.addRule(RelativeLayout.ALIGN_LEFT, previousViewId);
							params.addRule(RelativeLayout.BELOW, previousViewId);
						}
						
						newView.setLayoutParams(params);
						phoneViewLayout.addView(newView);
						
						previousViewId = newView.getId();
					}
				}
				
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG) {
					xmlParser.next();
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "phone view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "phone view xml parsing error");
			
			return null;
		}
		
		return phoneViewLayout;
	}
	
	private View createRowView(XmlPullParser xmlParser) {
		CustomTextView rowView = new CustomTextView(this.activity);
		++this.id;
		rowView.setId(this.id);
		
		rowView.setTextColor(this.activity.getApplicationContext().getResources().getColor(R.color.row_view_text));
		rowView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.activity.getApplicationContext().getResources().getDimension(R.dimen.text_normal));
		
		try {
			boolean isRoot = true;
			String result = "";
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG &&
						xmlParser.getEventType() != XmlPullParser.TEXT) {
					xmlParser.next();
				}
				
				if (xmlParser.getEventType() == XmlPullParser.START_TAG) {
					if (isTagName(xmlParser, ROW_NAME) && isRoot) {
						isRoot = false;
					} else {
						result += getStrongText(xmlParser);
					}
				} else if (xmlParser.getEventType() == XmlPullParser.TEXT) {
					result += escapeHtml(xmlParser.getText());
				}
				
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG &&
						xmlParser.getEventType() != XmlPullParser.TEXT) {
					xmlParser.next();
				}
			}
			rowView.setText(Html.fromHtml(result));
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "row view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "row view xml parsing error");
			
			return null;
		}
		
		return rowView;
	}
	
	private View createMailView(XmlPullParser xmlParser) {
		RelativeLayout mailViewLayout = new RelativeLayout(this.activity);
		++this.id;
		mailViewLayout.setId(this.id);
		
		ImageView mailImage = new ImageView(this.activity);
		++this.id;
		mailImage.setId(this.id);
		mailImage.setImageResource(R.drawable.email);
		
		RelativeLayout.LayoutParams mailImageParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mailImageParams.rightMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_small);
		mailImage.setLayoutParams(mailImageParams);
		
		mailViewLayout.addView(mailImage);
		
		final String address = xmlParser.getAttributeValue(null, MAIL_ADDRESS_NAME);
		if (address != null) {
			mailImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent emailIntent = new Intent(Intent.ACTION_SEND);
					emailIntent.setType("plain/text");
					String[] to = new String[1];
					to[0] = address;
					emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
					
					activity.startActivity(emailIntent);
				}
			});
		}
		
		boolean isRoot = true;
		int previousViewId = -1;
		try {
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG) {
					xmlParser.next();
				}
				
				if (isTagName(xmlParser, MAIL_NAME) && isRoot) {
					isRoot = false;
				} else {
					View newView = createView(xmlParser);
					if (newView != null) {
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						
						if (previousViewId == -1) {
							params.addRule(RelativeLayout.RIGHT_OF, mailImage.getId());
							params.addRule(RelativeLayout.ALIGN_TOP, mailImage.getId());
						} else {
							params.addRule(RelativeLayout.ALIGN_LEFT, previousViewId);
							params.addRule(RelativeLayout.BELOW, previousViewId);
						}
						
						newView.setLayoutParams(params);
						mailViewLayout.addView(newView);
						
						previousViewId = newView.getId();
					}
				}
				
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG) {
					xmlParser.next();
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "mail view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "mail view xml parsing error");
			
			return null;
		}
		
		return mailViewLayout;
	}
	
	private View createHoursView(XmlPullParser xmlParser) {
		RelativeLayout hoursViewLayout = new RelativeLayout(this.activity);
		++this.id;
		hoursViewLayout.setId(this.id);
		
		ImageView hoursImage = new ImageView(this.activity);
		++this.id;
		hoursImage.setId(this.id);
		hoursImage.setImageResource(R.drawable.clock);
		
		RelativeLayout.LayoutParams hoursImageParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		hoursImageParams.rightMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_small);
		hoursImage.setLayoutParams(hoursImageParams);
		
		hoursViewLayout.addView(hoursImage);
		
		boolean isRoot = true;
		int previousViewId = -1;
		try {
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG) {
					xmlParser.next();
				}
				
				if (isTagName(xmlParser, HOURS_NAME) && isRoot) {
					isRoot = false;
				} else {
					View newView = createView(xmlParser);
					if (newView != null) {
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						
						if (previousViewId == -1) {
							params.addRule(RelativeLayout.RIGHT_OF, hoursImage.getId());
							params.addRule(RelativeLayout.ALIGN_TOP, hoursImage.getId());
						} else {
							params.addRule(RelativeLayout.ALIGN_LEFT, previousViewId);
							params.addRule(RelativeLayout.BELOW, previousViewId);
						}
						
						newView.setLayoutParams(params);
						hoursViewLayout.addView(newView);
						
						previousViewId = newView.getId();
					}
				}
				
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG) {
					xmlParser.next();
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "hours view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "hours view xml parsing error");
			
			return null;
		}
		
		return hoursViewLayout;
	}
	
	private View createPlaceView(XmlPullParser xmlParser) {
		RelativeLayout placeViewLayout = new RelativeLayout(this.activity);
		++this.id;
		placeViewLayout.setId(this.id);
		
		boolean isRoot = true;
		int previousViewId = -1;
		try {
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG) {
					xmlParser.next();
				}
				
				if (isTagName(xmlParser, PLACE_NAME) && isRoot) {
					isRoot = false;
				} else {
					View newView = createView(xmlParser);
					if (newView != null) {
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						
						if (previousViewId != -1) {
							params.addRule(RelativeLayout.BELOW, previousViewId);
						}
						
						newView.setLayoutParams(params);
						placeViewLayout.addView(newView);
						
						previousViewId = newView.getId();
					}
				}
				
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG) {
					xmlParser.next();
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "place view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "place view xml parsing error");
			
			return null;
		}
		
		return placeViewLayout;
	}
	
	private View createNameView(XmlPullParser xmlParser) {
		CustomTextView nameView = new CustomTextView(this.activity);
		++this.id;
		nameView.setId(this.id);
		
		nameView.setTextColor(this.activity.getApplicationContext().getResources().getColor(R.color.name_view_text));
		nameView.setTypeface(null, Typeface.BOLD);
		nameView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.activity.getApplicationContext().getResources().getDimension(R.dimen.text_normal));
		
		try {
			boolean isRoot = true;
			String result = "";
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG &&
						xmlParser.getEventType() != XmlPullParser.TEXT) {
					xmlParser.next();
				}
				
				if (xmlParser.getEventType() == XmlPullParser.START_TAG) {
					if (isTagName(xmlParser, NAME_NAME) && isRoot) {
						isRoot = false;
					} else {
						result += getStrongText(xmlParser);
					}
				} else if (xmlParser.getEventType() == XmlPullParser.TEXT) {
					result += escapeHtml(xmlParser.getText());
				}
				
				xmlParser.next();		
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG &&
						xmlParser.getEventType() != XmlPullParser.TEXT) {
					xmlParser.next();
				}
			}
			nameView.setText(Html.fromHtml(result));
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "name view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "name view xml parsing error");
			
			return null;
		}
		
		return nameView;
	}
	
	private View createContactView(XmlPullParser xmlParser) {
		RelativeLayout contactViewLayout = new RelativeLayout(this.activity);
		++this.id;
		contactViewLayout.setId(this.id);
		
		boolean isRoot = true;
		int previousViewId = -1;
		int childNumber = 0;
		try {
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG) {
					xmlParser.next();
				}
				
				if (isTagName(xmlParser, CONTACT_NAME) && isRoot) {
					isRoot = false;
				} else {
					View newView = createView(xmlParser);
					if (newView != null) {
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						
						if (previousViewId != -1) {
							if (childNumber == 1) {
								params.addRule(RelativeLayout.RIGHT_OF, previousViewId);
								params.addRule(RelativeLayout.ALIGN_TOP, previousViewId);
							} else {
								params.addRule(RelativeLayout.ALIGN_LEFT, previousViewId);
								params.addRule(RelativeLayout.BELOW, previousViewId);
							}
						}
						
						newView.setLayoutParams(params);
						contactViewLayout.addView(newView);
						
						++childNumber;
						previousViewId = newView.getId();
					}
				}
				
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG) {
					xmlParser.next();
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "contact view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "contact view xml parsing error");
			
			return null;
		}
		
		return contactViewLayout;
	}
	
	private View createInfoView(XmlPullParser xmlParser) {
		final RelativeLayout infoViewLayout = new RelativeLayout(this.activity);
		++this.id;
		infoViewLayout.setId(this.id);
		
		infoViewLayout.setBackgroundResource(R.drawable.info_view_background);
		int paddingTopRightBottomLeft = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
		infoViewLayout.setPadding(paddingTopRightBottomLeft, paddingTopRightBottomLeft, paddingTopRightBottomLeft, paddingTopRightBottomLeft);
		
		ImageView infoImage = new ImageView(this.activity);
		++this.id;
		infoImage.setId(this.id);
		infoImage.setImageResource(R.drawable.info);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, infoViewLayout.getId());
		infoImage.setLayoutParams(params);
		infoViewLayout.addView(infoImage);
		
		RelativeLayout customContent = new RelativeLayout(this.activity);
		++this.id;
		customContent.setId(this.id);
		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.rightMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_small);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.LEFT_OF, infoImage.getId());
		customContent.setLayoutParams(params);
		infoViewLayout.addView(customContent);
		
		View longInfoView = null;
		
		boolean isRoot = true;
		int previousViewId = -1;
		String previousTagName = "";
		try {
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG) {
					xmlParser.next();
				}
				
				if (isTagName(xmlParser, INFO_NAME) && isRoot) {
					isRoot = false;
				} else {
					String name = getName(xmlParser);
					View newView = createView(xmlParser);
					if (name.equals(LONG_INFO_NAME)) {
						longInfoView = newView;
					} else if (newView != null) {
						setTypography(newView, name, previousViewId, previousTagName, false);
						
						customContent.addView(newView);
						
						previousTagName = name;
						previousViewId = newView.getId();
					}
				}
				
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG) {
					xmlParser.next();
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "info view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "info view xml parsing error");
			
			return null;
		}
		
		final View longInfoViewForOnClickListener = longInfoView;
		if (longInfoViewForOnClickListener != null) {
			infoImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					RelativeLayout.LayoutParams popUpParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					popUpParams.topMargin = infoViewLayout.getTop();
					popUpParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
					popUp.setLayoutParams(popUpParams);
					int paddingTopRightBottomLeft = (int) activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
					popUp.setPadding(paddingTopRightBottomLeft, paddingTopRightBottomLeft, paddingTopRightBottomLeft, paddingTopRightBottomLeft);
					
					popUp.removeAllViews();
					createPopUp(longInfoViewForOnClickListener);
					popUp.setVisibility(RelativeLayout.VISIBLE);
				}
			});
		}
		
		return infoViewLayout;
	}
	
	private View createCnameView(XmlPullParser xmlParser) {
		CustomTextView cnameView = new CustomTextView(this.activity);
		++this.id;
		cnameView.setId(this.id);
		
		cnameView.setBackgroundColor(this.activity.getApplicationContext().getResources().getColor(R.color.cname_view_background));
		int width = (int) (this.activity.getApplicationContext().getResources().getDimension(R.dimen.text_normal) * CustomContentParser.CNAME_HEIGHT_TO_NORMAL_TEXT_SIZE);
		cnameView.setWidth(width);
		cnameView.setMinHeight(width);
		cnameView.setGravity(Gravity.CENTER);
		cnameView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.activity.getApplicationContext().getResources().getDimension(R.dimen.text_normal));
		cnameView.setTextColor(this.activity.getApplicationContext().getResources().getColor(R.color.cname_view_text));
		
		int paddingTopRightBottomLeft = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
		cnameView.setPadding(paddingTopRightBottomLeft, paddingTopRightBottomLeft, paddingTopRightBottomLeft, paddingTopRightBottomLeft);
		
		try {
			boolean isRoot = true;
			String result = "";
			while(xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG &&
						xmlParser.getEventType() != XmlPullParser.TEXT) {
					xmlParser.next();
				}
			
				if (xmlParser.getEventType() == XmlPullParser.START_TAG) {
					if (isTagName(xmlParser, CNAME_NAME) && isRoot) {
						isRoot = false;
					} else {
						result += getStrongText(xmlParser);
					}
				} else if (xmlParser.getEventType() == XmlPullParser.TEXT) {
					result += escapeHtml(xmlParser.getText());
				}
			
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG &&
						xmlParser.getEventType() != XmlPullParser.TEXT) {
					xmlParser.next();
				}
			}
			
			cnameView.setText(Html.fromHtml(result));
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "cname view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "cname view xml parsing error");
			
			return null;
		}
		
		return cnameView;
	}
	
	private View createCtextView(XmlPullParser xmlParser) {
		CustomTextView ctextView = new CustomTextView(this.activity);
		++this.id;
		ctextView.setId(this.id);
		
		ctextView.setTextColor(this.activity.getApplicationContext().getResources().getColor(R.color.ctext_view_text));
		ctextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.activity.getApplicationContext().getResources().getDimension(R.dimen.text_normal));
		
		try {
			boolean isRoot = true;
			String result = "";
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG &&
						xmlParser.getEventType() != XmlPullParser.TEXT) {
					xmlParser.next();
				}
			
				if (xmlParser.getEventType() == XmlPullParser.START_TAG) {
					if (isTagName(xmlParser, CTEXT_NAME) && isRoot) {
						isRoot = false;
					} else {
						result += getStrongText(xmlParser);
					}
				} else if (xmlParser.getEventType() == XmlPullParser.TEXT) {
					result += escapeHtml(xmlParser.getText());
				}
		
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG &&
						xmlParser.getEventType() != XmlPullParser.TEXT) {
					xmlParser.next();
				}
			}
			ctextView.setText(Html.fromHtml(result));
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "ctext view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "ctext view xml parsing error");
			
			return null;
		}
		
		return ctextView;
	}
	
	private void citemViewOnClick(String page) {
		DatabaseReader adapter = new DatabaseReader(activity);
		LayoutTypeInterface layout = adapter.getLayout(Integer.parseInt(page));
		if (layout == null) {
			return;
		}
		
		if (layout.getLayoutType() == LayoutTypeInterface.STEPS_LAYOUT) {
			((MainActivity)activity).openLayout(-Integer.parseInt(page), layoutId);
		} else {
			((MainActivity)activity).openLayout(Integer.parseInt(page), layoutId);
		}
	}
	
	private View createCitemView(XmlPullParser xmlParser) {
		RelativeLayout citemViewLayout = new RelativeLayout(this.activity);
		++this.id;
		citemViewLayout.setId(this.id);
		
		final String page = xmlParser.getAttributeValue(null, CustomContentParser.CITEM_PAGE_NAME);
		if (page != null) {
			citemViewLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					citemViewOnClick(page);
				}
			});
		}
		
		boolean isRoot = true;
		int previousViewId = -1;
		try {
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG) {
					xmlParser.next();
				}
				
				if (isTagName(xmlParser, CITEM_NAME) && isRoot) {
					isRoot = false;
				} else {
					View newView = createView(xmlParser);
					if (newView != null) {
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						
						if (previousViewId != -1) {
							params.addRule(RelativeLayout.RIGHT_OF, previousViewId);
							params.addRule(RelativeLayout.ALIGN_TOP, previousViewId);
							params.leftMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
						}
						newView.setLayoutParams(params);
						citemViewLayout.addView(newView);
						
						previousViewId = newView.getId();
					}
				}
				
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG) {
					xmlParser.next();
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "citem view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "citem view xml parsing error");

			return null;
		}
		
		return citemViewLayout;
	}
	
	private View createCategoriesView(XmlPullParser xmlParser) {
		RelativeLayout categoriesViewLayout = new RelativeLayout(this.activity);
		++this.id;
		categoriesViewLayout.setId(this.id);
		
		boolean isRoot = true;
		int previousViewId = -1;
		int childNumber = 0;
		LinearLayout row = null;
		
		int citemsInRow = getColumnCountForCategories();
		try {
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG) {
					xmlParser.next();
				}
				
				if (isTagName(xmlParser, CATEGORIES_NAME) && isRoot) {
					isRoot = false;
				} else {
					View newView = createView(xmlParser);
					if (newView != null) {
						if (citemsInRow == 1) {
							RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
							
							if (previousViewId != -1) {
								relativeParams.addRule(RelativeLayout.BELOW, previousViewId);
							}
							
							relativeParams.topMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
							
							newView.setLayoutParams(relativeParams);
							categoriesViewLayout.addView(newView);
							
							previousViewId = newView.getId();
						} else {
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
						
							if (childNumber % 2 == 0) {
								row = new LinearLayout(this.activity);
								++this.id;
								row.setId(this.id);
								
								row.setWeightSum(2);
								row.setOrientation(LinearLayout.HORIZONTAL);
								params.topMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
								params.rightMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
								
								RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
								if (previousViewId != -1) {
									relativeParams.addRule(RelativeLayout.BELOW, previousViewId);
								}
								row.setLayoutParams(relativeParams);
								newView.setLayoutParams(params);
								row.addView(newView);
							} else {
								params.topMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
								newView.setLayoutParams(params);
								row.addView(newView);
								categoriesViewLayout.addView(row);
								
								previousViewId = row.getId();
							}
							
							++childNumber;
						}
					}
				}
				
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG) {
					xmlParser.next();
				}
			}
			
			if (childNumber % 2 != 0) {
				categoriesViewLayout.addView(row);
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "categories view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "categories view xml parsing error");
			
			return null;
		}
		
		return categoriesViewLayout;
	}
	
	private View createButtonView(XmlPullParser xmlParser) {
		RelativeLayout buttonLayout = new RelativeLayout(this.activity);
		++this.id;
		buttonLayout.setId(this.id); 
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		buttonLayout.setLayoutParams(params);
		
		int paddingTopBottomLeft = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
		int paddingRight = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_small);
		buttonLayout.setPadding(paddingTopBottomLeft, paddingTopBottomLeft, paddingRight, paddingTopBottomLeft);
		
		boolean isButtonToApp = false;
		
		final String url = xmlParser.getAttributeValue(null, CustomContentParser.BUTTON_URL_NAME);
		if (url != null) {
			isButtonToApp = false;
			
			buttonLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String validUrl;
					if (url.startsWith("http://") ||
							url.startsWith("https://")) {
						validUrl = url;
					} else {
						validUrl = "http://" + url;
					}
					
					Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(validUrl));
					activity.startActivity(urlIntent);
				}
			});
		}
		
		final String page = xmlParser.getAttributeValue(null, CustomContentParser.BUTTON_PAGE_NAME);
		if (page != null) {
			isButtonToApp = true;
			
			buttonLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DatabaseReader adapter = new DatabaseReader(activity);
					LayoutTypeInterface layout = adapter.getLayout(Integer.parseInt(page));
					if (layout == null) {
						return;
					}
					
					if (layout.getLayoutType() == LayoutTypeInterface.STEPS_LAYOUT) {
						((MainActivity)activity).openLayout(-Integer.parseInt(page), layoutId);
					} else {
						((MainActivity)activity).openLayout(Integer.parseInt(page), layoutId);
					}
				}
			});
		}
		
		if (isButtonToApp) {
			buttonLayout.setBackgroundColor(this.activity.getApplicationContext().getResources().getColor(R.color.button_view_to_app_background));
		} else {
			buttonLayout.setBackgroundResource(R.drawable.button_to_url_border);
		}
		
		ImageView icon = new ImageView(this.activity);
		++this.id;
		icon.setId(this.id);
		
		RelativeLayout.LayoutParams arrowParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		arrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		arrowParams.addRule(RelativeLayout.CENTER_VERTICAL);
		icon.setLayoutParams(arrowParams);
		
		if (isButtonToApp) {
			icon.setImageResource(R.drawable.arrow_button);
		} else {
			icon.setImageResource(R.drawable.globe);
		}
		buttonLayout.addView(icon);
		
		CustomTextView buttonView = new CustomTextView(this.activity);
		++this.id;
		buttonView.setId(this.id);
		
		RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		textParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		textParams.addRule(RelativeLayout.LEFT_OF, icon.getId());
		int marginRight = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_small);
		textParams.rightMargin = marginRight;
		
		buttonView.setLayoutParams(textParams);
		
		if (isButtonToApp) {
			buttonView.setTextColor(this.activity.getApplicationContext().getResources().getColor(R.color.button_view_to_app_text));
		} else {
			buttonView.setTextColor(this.activity.getApplicationContext().getResources().getColor(R.color.button_view_to_url_text));
		}
		
		try {
			boolean isRoot = true;
			String result = "";
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG &&
						xmlParser.getEventType() != XmlPullParser.TEXT) {
					xmlParser.next();
				}
			
				if (xmlParser.getEventType() == XmlPullParser.START_TAG) {
					if (isTagName(xmlParser, BUTTON_NAME) && isRoot) {
						isRoot = false;
					} else {
						result += getStrongText(xmlParser);
					}
				} else if (xmlParser.getEventType() == XmlPullParser.TEXT) {
					result += escapeHtml(xmlParser.getText());
				}
		
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG &&
						xmlParser.getEventType() != XmlPullParser.TEXT) {
					xmlParser.next();
				}
			}
			buttonView.setText(Html.fromHtml(result));
			buttonView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.activity.getApplicationContext().getResources().getDimension(R.dimen.text_normal));
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "button view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "button view xml parsing error");
			
			return null;
		}
		
		buttonLayout.addView(buttonView);
		
		return buttonLayout;
	}
	
	private View createH1View(XmlPullParser xmlParser) {
		CustomTextView h1View = new CustomTextView(this.activity);
		++this.id;
		h1View.setId(this.id);
		
		h1View.setTextAppearance(this.activity, android.R.style.TextAppearance_Large);
		h1View.setTextColor(this.activity.getApplicationContext().getResources().getColor(R.color.h1_view_text));
		h1View.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.activity.getApplicationContext().getResources().getDimension(R.dimen.header_1));
		h1View.setTypeface(null, Typeface.BOLD);
		
		try {
			boolean isRoot = true;
			String result = "";
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG &&
						xmlParser.getEventType() != XmlPullParser.TEXT) {
					xmlParser.next();
				}
			
				if (xmlParser.getEventType() == XmlPullParser.START_TAG) {
					if (isTagName(xmlParser, H1_NAME) && isRoot) {
						isRoot = false;
					} else {
						result += getStrongText(xmlParser);
					}
				} else if (xmlParser.getEventType() == XmlPullParser.TEXT) {
					result += escapeHtml(xmlParser.getText());
				}
			
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG &&
						xmlParser.getEventType() != XmlPullParser.TEXT) {
					xmlParser.next();
				}
			}
			h1View.setText(Html.fromHtml(result));
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "h1 view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "h1 view xml parsing error");
			
			return null;
		}
		
		return h1View;
	}
	
	private View createH2View(XmlPullParser xmlParser) {
		CustomTextView h2View = new CustomTextView(this.activity);
		++this.id;
		h2View.setId(this.id);
		
		h2View.setTextAppearance(this.activity, android.R.style.TextAppearance_Medium);
		h2View.setTextColor(this.activity.getApplicationContext().getResources().getColor(R.color.h2_view_text));
		h2View.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.activity.getApplicationContext().getResources().getDimension(R.dimen.header_2));
		h2View.setTypeface(null, Typeface.BOLD);
		
		try {
			boolean isRoot = true;
			String result = "";
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG &&
						xmlParser.getEventType() != XmlPullParser.TEXT) {
					xmlParser.next();
				}
			
				if (xmlParser.getEventType() == XmlPullParser.START_TAG) {
					if (isTagName(xmlParser, H2_NAME) && isRoot) {
						isRoot = false;
					} else {
						result += getStrongText(xmlParser);
					}
				} else if (xmlParser.getEventType() == XmlPullParser.TEXT) {
					result += escapeHtml(xmlParser.getText());
				}
			
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG &&
						xmlParser.getEventType() != XmlPullParser.TEXT) {
					xmlParser.next();
				}
			}
			h2View.setText(Html.fromHtml(result));
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "h2 view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "h2 view xml parsing error");
			
			return null;
		}
		
		return h2View;
	}
	
	private String getStrongText(XmlPullParser xmlParser) {
		try {
			xmlParser.require(XmlPullParser.START_TAG, null, STRONG_NAME);
			
			while (xmlParser.getEventType() != XmlPullParser.END_TAG &&
					xmlParser.getEventType() != XmlPullParser.TEXT) {
				xmlParser.next();
			}
			
			String strongText = "";
			
			if (xmlParser.getEventType() == XmlPullParser.TEXT) {
				strongText = escapeHtml(xmlParser.getText());
			}
			
			if (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
					xmlParser.next();
				}
			}
			
			return "<b>" + strongText + "</b>";
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "strong text xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "strong text xml parsing error");
			
			return null;
		}
	}
	
	private View createUlView(XmlPullParser xmlParser) {
		RelativeLayout ulViewLayout = new RelativeLayout(this.activity);
		++this.id;
		ulViewLayout.setId(this.id);
		
		boolean isRoot = true;
		int previousViewId = -1;
		int previousBulletId = -1;
		try {
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG) {
					xmlParser.next();
				}
				
				if (isTagName(xmlParser, UL_NAME) && isRoot) {
					isRoot = false;
				} else {
					View newView = createView(xmlParser);
					if (newView != null) {
						CustomTextView bulletView = new CustomTextView(this.activity);
						++this.id;
						bulletView.setId(this.id);
						bulletView.setText("\u2022");
						bulletView.setTextColor(this.activity.getApplicationContext().getResources().getColor(R.color.bullet_view_text));
						bulletView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.activity.getApplicationContext().getResources().getDimension(R.dimen.text_normal));		
						
						RelativeLayout.LayoutParams bulletViewParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						int marginRight = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
						bulletViewParams.rightMargin = marginRight;
						bulletViewParams.addRule(RelativeLayout.ALIGN_LEFT, previousBulletId);
						bulletViewParams.addRule(RelativeLayout.BELOW, previousViewId);
						bulletView.setLayoutParams(bulletViewParams);
						
						RelativeLayout.LayoutParams newViewParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						newViewParams.addRule(RelativeLayout.ALIGN_TOP, bulletView.getId());
						newViewParams.addRule(RelativeLayout.RIGHT_OF, bulletView.getId());
						newView.setLayoutParams(newViewParams);
						
						ulViewLayout.addView(bulletView);
						ulViewLayout.addView(newView);
						
						previousBulletId = bulletView.getId();
						previousViewId = newView.getId();
					}
				}
				
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG) {
					xmlParser.next();
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "ul view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "ul view xml parsing error");
			
			return null;
		}
		
		return ulViewLayout;
	}
	
	private View createOlView(XmlPullParser xmlParser) {
		RelativeLayout olViewLayout = new RelativeLayout(this.activity);
		++this.id;
		olViewLayout.setId(this.id);
		
		boolean isRoot = true;
		int previousViewId = -1;
		int previousNumberId = -1;
		int number = 1;
		try {
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG) {
					xmlParser.next();
				}
				
				if (isTagName(xmlParser, OL_NAME) && isRoot) {
					isRoot = false;
				} else {
					View newView = createView(xmlParser);
					if (newView != null) {
						CustomTextView numberView = new CustomTextView(this.activity);
						++this.id;
						numberView.setId(this.id);
						numberView.setText(Integer.toString(number) + ". ");
						numberView.setTextColor(this.activity.getApplicationContext().getResources().getColor(R.color.number_view_text));
						numberView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.activity.getApplicationContext().getResources().getDimension(R.dimen.text_normal));
						++number;
						
						RelativeLayout.LayoutParams numberViewParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						int marginRight = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
						numberViewParams.rightMargin = marginRight;
						numberViewParams.addRule(RelativeLayout.ALIGN_LEFT, previousNumberId);
						numberViewParams.addRule(RelativeLayout.BELOW, previousViewId);
						numberView.setLayoutParams(numberViewParams);
						
						RelativeLayout.LayoutParams newViewParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						newViewParams.addRule(RelativeLayout.ALIGN_TOP, numberView.getId());
						newViewParams.addRule(RelativeLayout.RIGHT_OF, numberView.getId());
						newView.setLayoutParams(newViewParams);
						
						olViewLayout.addView(numberView);
						olViewLayout.addView(newView);
						
						previousNumberId = numberView.getId();
						previousViewId = newView.getId();
					}
				}
				
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG) {
					xmlParser.next();
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "ol view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "ol view xml parsing error");
			
			return null;
		}
		
		return olViewLayout;
	}
	
	private View createLiView(XmlPullParser xmlParser) {
		RelativeLayout liView = new RelativeLayout(this.activity);
		++this.id;
		liView.setId(this.id);
		
		try {
			boolean isRoot = true;
			String result = "";
			int previousViewId = -1;
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG &&
						xmlParser.getEventType() != XmlPullParser.TEXT) {
					xmlParser.next();
				}
			
				if (xmlParser.getEventType() == XmlPullParser.START_TAG) {
					if (isTagName(xmlParser, LI_NAME) && isRoot) {
						isRoot = false;
					} else if (isTagName(xmlParser, STRONG_NAME)){
						result += getStrongText(xmlParser);
					} else {
						if (!result.isEmpty()) {
							CustomTextView textView = new CustomTextView(this.activity);
							++this.id;
							textView.setId(this.id);
							textView.setTextColor(this.activity.getApplicationContext().getResources().getColor(R.color.li_view_text));
							textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.activity.getApplicationContext().getResources().getDimension(R.dimen.text_normal));
							
							RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
							
							if (previousViewId != -1) {
								params.addRule(RelativeLayout.ALIGN_LEFT, previousViewId);
								params.addRule(RelativeLayout.BELOW, previousViewId);
							}
							
							textView.setLayoutParams(params);
							textView.setText(Html.fromHtml(result));
							
							liView.addView(textView);
							
							previousViewId = textView.getId();
							result = "";
						}
						
						View newView = createView(xmlParser);
						if (newView != null) {			
							RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
							
							if (previousViewId != -1) {
								params.addRule(RelativeLayout.ALIGN_LEFT, previousViewId);
								params.addRule(RelativeLayout.BELOW, previousViewId);
							}
							
							newView.setLayoutParams(params);
							liView.addView(newView);
							
							previousViewId = newView.getId();
						}
					}
				} else if (xmlParser.getEventType() == XmlPullParser.TEXT) {
					result += escapeHtml(xmlParser.getText());
				}
			
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG &&
						xmlParser.getEventType() != XmlPullParser.TEXT) {
					xmlParser.next();
				}
			}
			if (!result.isEmpty()) {
				CustomTextView textView = new CustomTextView(this.activity);
				++this.id;
				textView.setId(this.id);
				
				textView.setTextColor(this.activity.getApplicationContext().getResources().getColor(R.color.li_view_text));
				textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.activity.getApplicationContext().getResources().getDimension(R.dimen.text_normal));
				
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				
				if (previousViewId != -1) {
					params.addRule(RelativeLayout.ALIGN_LEFT, previousViewId);
					params.addRule(RelativeLayout.BELOW, previousViewId);
				}
				
				textView.setLayoutParams(params);
				textView.setText(Html.fromHtml(result));
				
				liView.addView(textView);
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "li view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "li view xml parsing error");
			
			return null;
		}
		
		return liView;
	}
	
	private View createParagraphView(XmlPullParser xmlParser) {
		CustomTextView paragraphView = new CustomTextView(this.activity);
		++this.id;
		paragraphView.setId(this.id);
		
		paragraphView.setTextColor(this.activity.getApplicationContext().getResources().getColor(R.color.paragraph_view_text));
		paragraphView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.activity.getApplicationContext().getResources().getDimension(R.dimen.text_normal));
		
		try {
			boolean isRoot = true;
			String result = "";
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG &&
						xmlParser.getEventType() != XmlPullParser.TEXT) {
					xmlParser.next();
				}
			
				if (xmlParser.getEventType() == XmlPullParser.START_TAG) {
					if (isTagName(xmlParser, PARAGRAPH_NAME) && isRoot) {
						isRoot = false;
					} else {
						result += getStrongText(xmlParser);
					}
				} else if (xmlParser.getEventType() == XmlPullParser.TEXT) {
					result += escapeHtml(xmlParser.getText());
				}
			
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG &&
						xmlParser.getEventType() != XmlPullParser.TEXT) {
					xmlParser.next();
				}
			}
			paragraphView.setText(Html.fromHtml(result));
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "paragraph view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "paragraph view xml parsing error");
			
			return null;
		}
		
		return paragraphView;
	}
	
	private View createShortInfoView(XmlPullParser xmlParser) {
		RelativeLayout shortInfoViewLayout = new RelativeLayout(this.activity);
		++this.id;
		shortInfoViewLayout.setId(this.id);
		
		boolean isRoot = true;
		int previousViewId = -1;
		try {
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG) {
					xmlParser.next();
				}
				
				if (isTagName(xmlParser, SHORT_INFO_NAME) && isRoot) {
					isRoot = false;
				} else {
					View newView = createView(xmlParser);
					if (newView != null) {
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						
						if (previousViewId != -1) {
							params.addRule(RelativeLayout.ALIGN_LEFT, previousViewId);
							params.addRule(RelativeLayout.BELOW, previousViewId);
						}
						
						newView.setLayoutParams(params);
						shortInfoViewLayout.addView(newView);
						
						previousViewId = newView.getId();
					}
				}
				
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG) {
					xmlParser.next();
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "short info view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "short info view xml parsing error");
			
			return null;
		}
		
		return shortInfoViewLayout;
	}
	
	private View createLongInfoView(XmlPullParser xmlParser) {
		RelativeLayout longInfoViewLayout = new RelativeLayout(this.activity);
		++this.id;
		longInfoViewLayout.setId(this.id);
		
		boolean isRoot = true;
		int previousViewId = -1;
		String previousTagName = "";
		try {
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				while (xmlParser.getEventType() != XmlPullParser.START_TAG) {
					xmlParser.next();
				}
				
				String tagName = getName(xmlParser);
				if (isTagName(xmlParser, LONG_INFO_NAME) && isRoot) {
					isRoot = false;
				} else {
					View newView = createView(xmlParser);
					if (newView != null) {
						setTypography(newView, tagName, previousViewId, previousTagName, true);
						
						longInfoViewLayout.addView(newView);

						previousViewId = newView.getId();
						previousTagName = tagName;
					}
				}
				
				xmlParser.next();
				while (xmlParser.getEventType() != XmlPullParser.START_TAG &&
						xmlParser.getEventType() != XmlPullParser.END_TAG) {
					xmlParser.next();
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "long info view xml parsing error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "long info view xml parsing error");
			
			return null;
		}
		
		return longInfoViewLayout;
	}
	
	private String getImageName(XmlPullParser xmlParser) {
		String src = xmlParser.getAttributeValue(null, "src");
		String[] srcParts = src.split("/");
		
		return srcParts[srcParts.length - 1];
	}
	
	private int getLayoutWidth() {
		if (isPortraitOrientation()) {
			return getDisplayWidth();
		} else {
			return (int) (getDisplayWidth() * Global.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH);
		}
	}
	
	private Bitmap getImageBitmap(String imageName) {
		FileInputStream fileInputStream;
		try {
			fileInputStream = this.activity.getApplicationContext().openFileInput(imageName);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			Log.e(Global.TAG, "parser image from filesystem error");
			
			return null;
		}
		
		return BitmapFactory.decodeStream(fileInputStream);
	}
	
	private Bitmap scaleImageBitmap(Bitmap bitmap) {		
		int maxImageWidth = (int) (getLayoutWidth() - 2 * this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_xlarge));
		
		Bitmap scaledBitmap = bitmap;
		if (bitmap.getWidth() > maxImageWidth) {
			double aspect = 1.0f * bitmap.getWidth() / bitmap.getHeight();
			int height = (int) (maxImageWidth / aspect); 
			scaledBitmap = Bitmap.createScaledBitmap(bitmap, maxImageWidth, height, false);
		}
		
		return scaledBitmap;
	}
	
	private View createImageView(XmlPullParser xmlParser) {
		ImageView imageView = new ImageView(this.activity);
		++this.id;
		imageView.setId(this.id);
		
		String imageName = getImageName(xmlParser);
		Bitmap imageBitmap = getImageBitmap(imageName);
		if (imageBitmap == null) {
			imageView = null;
		} else {
			imageView.setImageBitmap(scaleImageBitmap(imageBitmap));
		}
		
		try {
			while (xmlParser.getEventType() != XmlPullParser.END_TAG) {
				xmlParser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "content xml error");
			
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "content xml error");
			
			return null;
		}
		
		return imageView;
	}
	
	private void createPopUp(View longInfoViewForOnClickListener) {
		ImageView closePopUp = new ImageView(this.activity);
		++this.id;
		closePopUp.setId(id);
		
		closePopUp.setImageResource(R.drawable.popup_close);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		closePopUp.setLayoutParams(params);
		
		closePopUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popUp.removeAllViews();
				popUp.setVisibility(RelativeLayout.GONE);
			}
		});
		
		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.LEFT_OF, closePopUp.getId());
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.rightMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_small);
		longInfoViewForOnClickListener.setLayoutParams(params);
		
		popUp.addView(closePopUp);
		popUp.addView(longInfoViewForOnClickListener);
	}
	
	private String escapeHtml(String text) {
		String result = "";
		String[] substrings = text.split("\n");
		int index = 0;
		for(String substring: substrings) {
			if (index > 0) {
				result += "<br/>";
			}
			result += TextUtils.htmlEncode(substring);
			
			++index;
		}
		
		return result;
	}
	
	private void phoneImageOnClick(final String number) {
		PhoneCallDialogFragment phoneCallDialogFragment = new PhoneCallDialogFragment();
		phoneCallDialogFragment.setArguments(createArgumentsForPhoneCallDialogfragment(number));
		phoneCallDialogFragment.setCancelable(false);
		
		FragmentManager fragmentManager = this.activity.getSupportFragmentManager();
		try {
			fragmentManager.beginTransaction().add(phoneCallDialogFragment, PhoneCallDialogFragment.TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private Bundle createArgumentsForPhoneCallDialogfragment(String number) {
		Bundle arguments = new Bundle();
		arguments.putString(PhoneCallDialogFragment.PHONE_NUMBER_KEY, number);
		
		return arguments;
	}
	
	@SuppressWarnings("deprecation")
	private int getDisplayWidth() {
		Display display = this.activity.getWindowManager().getDefaultDisplay();
		
		if (Build.VERSION.SDK_INT < 13) {
			return display.getWidth();
		} else {
			Point size = new Point();
			display.getSize(size);
			
			return size.x;
		}
	}
	
	public boolean isPortraitOrientation() {
    	return this.activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }
	
	private void setTypography(View view, String tagName, int previousViewId, String previousTagName, boolean isInLongInfoBox) {		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		if (previousViewId != -1) {
			params.addRule(RelativeLayout.ALIGN_LEFT, previousViewId);
			params.addRule(RelativeLayout.BELOW, previousViewId);
		} else {
			if (tagName.equals(CustomContentParser.BUTTON_NAME)) {
				params.width = getButtonWidth(isInLongInfoBox);
				
				view.setLayoutParams(params);
			}
			
			return;
		}
		
		if (tagName.equals(CustomContentParser.PHONE_NAME)) {
			params.topMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
		} else if (tagName.equals(CustomContentParser.ROW_NAME)) {
			
		} else if (tagName.equals(CustomContentParser.MAIL_NAME)) {
			params.topMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
		} else if (tagName.equals(CustomContentParser.HOURS_NAME)) {
			params.topMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
		} else if (tagName.equals(CustomContentParser.PLACE_NAME)) {
			params.topMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
		} else if (tagName.equals(CustomContentParser.CONTACT_NAME)) {
			params.topMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
		} else if (tagName.equals(CustomContentParser.INFO_NAME)) {
			params.topMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
		} else if (tagName.equals(CustomContentParser.CATEGORIES_NAME)) {
			params.topMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
		} else if (tagName.equals(CustomContentParser.BUTTON_NAME)) {
			params.width = getButtonWidth(isInLongInfoBox);
			
			if (previousTagName.equals(CustomContentParser.OL_NAME) ||
					previousTagName.equals(CustomContentParser.UL_NAME)) {
			}
			if (previousTagName.equals(CustomContentParser.BUTTON_NAME)) {
				params.topMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_small);
			} else {
				params.topMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
			}
		} else if (tagName.equals(CustomContentParser.H1_NAME)) {
			params.topMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_large);
		} else if (tagName.equals(CustomContentParser.H2_NAME)) {
			params.topMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_large);
		} else if (tagName.equals(CustomContentParser.UL_NAME)) {
			params.topMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
		} else if (tagName.equals(CustomContentParser.OL_NAME)) {
			params.topMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
		} else if (tagName.equals(CustomContentParser.PARAGRAPH_NAME)) {
			if (previousTagName.equals(CustomContentParser.PARAGRAPH_NAME)) {
				params.topMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_small);
			} else {
				params.topMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
			}
		} else if (tagName.equals(CustomContentParser.IMAGE_NAME)) {
			if (previousTagName.equals(CustomContentParser.IMAGE_NAME)) {
				params.topMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_small);
			} else {
				params.topMargin = (int) this.activity.getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
			}
		} 
		
		view.setLayoutParams(params);
	}
	
	private int getButtonWidth(boolean isInLongInfoBox) {
		if (isInLongInfoBox) {
			return (int)(this.contentWidth * CustomContentParser.BUTTON_WIDTH_TO_CONTENT_WIDTH_LONG_INFO_BOX);
		} else {
			return (int) (this.contentWidth * CustomContentParser.BUTTON_WIDTH_TO_CONTENT_WIDTH);
		}
	}
	
	private boolean isAboveOrEqualLargeScreen() {
    	if ((this.activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==
    			Configuration.SCREENLAYOUT_SIZE_SMALL) {
    		return false;
    	}
    	
    	if ((this.activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==
    			Configuration.SCREENLAYOUT_SIZE_NORMAL) {
    		return false;
    	}
    	
    	if ((this.activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==
    			Configuration.SCREENLAYOUT_SIZE_LARGE) {
    		return true;
    	}
    	
    	if ((this.activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==
    			Configuration.SCREENLAYOUT_SIZE_XLARGE) {
    		return true;
    	}
    	
    	return true;
    }
	
	private int getColumnCountForCategories() {
		if (isPortraitOrientation()) {
			if (isAboveOrEqualLargeScreen()) {
				return 2;
			} else {
				return 1;
			}
		} else {
			return 1;
		}
	}
}
