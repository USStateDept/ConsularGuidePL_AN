package com.agitive.usembassy.fragments.layoutFragments;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.databases.DatabaseReader;
import com.agitive.usembassy.databases.FAQItem;
import com.agitive.usembassy.databases.FAQLayout;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.interfaces.FragmentAboveInterface;
import com.agitive.usembassy.interfaces.LayoutFragmentInterface;
import com.agitive.usembassy.layouts.CustomTextView;
import com.agitive.usembassy.parsers.CustomContentParser;

public class FAQFragment extends Fragment implements LayoutFragmentInterface, FragmentAboveInterface {
	
	
	public static final String LAYOUT_ID_KEY = "com.agitive.usembassy.fragments.FAQFragment.layoutId";
	
	private View rootView;
	private FAQLayout faqLayout;
	private int id;
	private HashMap<Integer, Boolean> isQuestionOpen;
	private ArrayList<View> dividers = new ArrayList<View>();
	private ArrayList<RelativeLayout> questionsLayouts = new ArrayList<RelativeLayout>();
	private ArrayList<RelativeLayout> answersLayouts = new ArrayList<RelativeLayout>();
	private ArrayList<CustomTextView> questionsTexts = new ArrayList<CustomTextView>();
	private ArrayList<ImageView> arrows = new ArrayList<ImageView>();

	@SuppressLint("UseSparseArrays")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		this.rootView = inflater.inflate(R.layout.faq_layout, container, false);
		this.id = 0;
		this.isQuestionOpen = new HashMap<Integer, Boolean>();
		this.dividers = new ArrayList<View>();
		this.answersLayouts = new ArrayList<RelativeLayout>();
		this.questionsTexts = new ArrayList<CustomTextView>();
		this.arrows = new ArrayList<ImageView>();
		
		setLayout();
		setMarginForEmblem();
        setLayoutName();
        setBackButton();
        setContent(savedInstanceState);
        
		return rootView;
	}
	
	@Override
	public void changeLanguage() {
		showMainProgressBarLayout();
		
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				setLayoutName();
				setQuestions();
				
				hideMainProgressBarLayout();
			}
			
		});
	}
	
	@Override
	public void setContent() {
		setQuestions();
		hideMainProgressBarLayout();
	}
	
	private void setContent(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		}
		
		setContent();
	}
	
	private void hideMainProgressBarLayout() {
		RelativeLayout mainProgressBarLayout = (RelativeLayout) this.rootView.findViewById(R.id.faq_layout_main_progress_bar_layout);
		if (mainProgressBarLayout == null) {
			return;
		}
		
		mainProgressBarLayout.setVisibility(RelativeLayout.INVISIBLE);
	}
	
	private void showMainProgressBarLayout() {
		RelativeLayout mainProgressBarLayout = (RelativeLayout) this.rootView.findViewById(R.id.faq_layout_main_progress_bar_layout);
		if (mainProgressBarLayout == null) {
			return;
		}
		
		mainProgressBarLayout.setVisibility(RelativeLayout.VISIBLE);
	}
	
	private void setLayout() {
		DatabaseReader databaseReader = new DatabaseReader(getActivity());
		int id = getArguments().getInt(FAQFragment.LAYOUT_ID_KEY);
		this.faqLayout = (FAQLayout) databaseReader.getLayout(id);
	}
	
	private void setMarginForEmblem() {
		CustomTextView layoutName = (CustomTextView) this.rootView.findViewById(R.id.faq_layout_layout_name);
		if (layoutName == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutName.getLayoutParams();
		params.topMargin = ((MainActivity)getActivity()).getEmblemPartOutsideSize();
		params.topMargin += getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_small);
		layoutName.setLayoutParams(params);
	}
	
	private void setLayoutName() {
		CustomTextView layoutName = (CustomTextView) this.rootView.findViewById(R.id.faq_layout_layout_name);
		if (layoutName == null) {
			return;
		}
		
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			layoutName.setText(this.faqLayout.getTitleEn());
		} else {
			layoutName.setText(this.faqLayout.getTitlePl());
		}
	}
	
	private void setBackButton() {
		ImageView backButton = (ImageView) this.rootView.findViewById(R.id.faq_layout_back_button);
		if (backButton == null) {
			return;
		}
		
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getActivity().onBackPressed();	
			}
			
		});
	}
	
	private void setQuestions() {
		RelativeLayout content = (RelativeLayout) this.rootView.findViewById(R.id.faq_layout_content);
		if (content == null) {
			return;
		}
		
		content.removeAllViews();
		
		ArrayList<FAQItem> faqItems;
		if (getAppLanguage().equals("EN")) {
			faqItems = this.faqLayout.getFaqsEn();
		} else {
			faqItems = this.faqLayout.getFaqsPl();
		}
		
		int previousIdQuestion = -1;
		
		for (FAQItem item: faqItems) {
			View divider = createDivider();
			this.dividers.add(divider);
			
			RelativeLayout questionLayout = createQuestion(item);
			questionsLayouts.add(questionLayout);
			
			RelativeLayout answerLayout = createAnswer(item);
			answersLayouts.add(answerLayout);
			
			if (previousIdQuestion == -1) {
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) divider.getLayoutParams();
				params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				divider.setLayoutParams(params);
			}	else  {
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) divider.getLayoutParams();
				params.addRule(RelativeLayout.BELOW, previousIdQuestion);
				divider.setLayoutParams(params);
			}
			
			LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, divider.getId());
			int marginTopBottom = (int) this.getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
			int marginLeftRight = (int) this.getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_xlarge);
			params.topMargin = marginTopBottom;
			params.bottomMargin = marginTopBottom;
			params.leftMargin = marginLeftRight;
			params.rightMargin = marginLeftRight;
			questionLayout.setLayoutParams(params);
			
			params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, questionLayout.getId());
			int marginTop = (int) this.getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_small);
			marginLeftRight = (int) this.getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_xlarge);
			params.leftMargin = marginLeftRight;
			params.topMargin = marginTop;
			params.rightMargin = marginLeftRight;
			answerLayout.setLayoutParams(params);
			
			int marginBottom = (int) this.getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
			answerLayout.setPadding(answerLayout.getPaddingLeft(), answerLayout.getPaddingTop(), answerLayout.getPaddingRight(), marginBottom);
			
			previousIdQuestion = questionLayout.getId();
			content.addView(divider);
			content.addView(questionLayout);
			content.addView(answerLayout);
		}
		
		
		for (int index = 0; index < answersLayouts.size() - 1; ++index) {
			final RelativeLayout questionToClick = questionsLayouts.get(index);
			final View dividerToMove = dividers.get(index + 1);
			final RelativeLayout answer = answersLayouts.get(index);
			final CustomTextView questionText = this.questionsTexts.get(index);
			final ImageView arrow = this.arrows.get(index);
			
			isQuestionOpen.put(questionToClick.getId(), false);
			
			ViewTreeObserver answerObserver = answer.getViewTreeObserver();
			answerObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				
				private boolean ready = false;
				
				@Override
				public void onGlobalLayout() {
					
					if (this.ready) {
						return;
					}
					this.ready = true;
					
					questionToClick.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dividerToMove.getLayoutParams();
							
							if (isQuestionOpen.get(questionToClick.getId())) {
								isQuestionOpen.put(questionToClick.getId(), false);
								
								params.topMargin = 0;
								
								answer.setVisibility(RelativeLayout.GONE);
								
								questionText.setTextColor(getActivity().getApplicationContext().getResources().getColor(R.color.faq_title_closed));
								arrow.setImageResource(R.drawable.faq_arrow);
							} else {
								isQuestionOpen.put(questionToClick.getId(), true);
								
								answer.setVisibility(RelativeLayout.VISIBLE);
								
								params.topMargin = answer.getHeight();
								
								questionText.setTextColor(getActivity().getApplicationContext().getResources().getColor(R.color.faq_title_opened));
								questionText.setTypeface(null, Typeface.BOLD);
								arrow.setImageResource(R.drawable.arrow_down);
							}
							
							dividerToMove.setLayoutParams(params);
						}
					});
					answer.setVisibility(RelativeLayout.GONE);
				}
			});
		}
		
		View divider = createDivider();
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) divider.getLayoutParams();
		params.addRule(RelativeLayout.BELOW, previousIdQuestion);
		divider.setLayoutParams(params);
		content.addView(divider);
		
		if (questionsLayouts.size() > 0) {
			final RelativeLayout questionToClick = questionsLayouts.get(questionsLayouts.size() - 1);
			final View dividerToMove = divider;
			final RelativeLayout answer = answersLayouts.get(answersLayouts.size() - 1);
			final CustomTextView questionText = this.questionsTexts.get(this.questionsTexts.size() - 1);
			final ImageView arrow = this.arrows.get(this.arrows.size() - 1);
			
			isQuestionOpen.put(questionToClick.getId(), false);
			
			ViewTreeObserver answerObserver = answer.getViewTreeObserver();
			answerObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				
				private boolean ready = false;
				
				@Override
				public void onGlobalLayout() {
					
					if (this.ready) {
						return;
					}
					this.ready = true;
					
					questionToClick.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dividerToMove.getLayoutParams();
							
							if (isQuestionOpen.get(questionToClick.getId())) {
								isQuestionOpen.put(questionToClick.getId(), false);
								
								params.topMargin = 0;
								
								answer.setVisibility(RelativeLayout.GONE);
								
								questionText.setTextColor(getActivity().getApplicationContext().getResources().getColor(R.color.faq_title_closed));
								arrow.setImageResource(R.drawable.faq_arrow);
							} else {
								isQuestionOpen.put(questionToClick.getId(), true);
								
								answer.setVisibility(RelativeLayout.VISIBLE);
								
								params.topMargin = answer.getHeight();
								
								questionText.setTextColor(getActivity().getApplicationContext().getResources().getColor(R.color.faq_title_opened));
								questionText.setTypeface(null, Typeface.BOLD);
								arrow.setImageResource(R.drawable.arrow_down);
							}
							
							dividerToMove.setLayoutParams(params);
						}
					});
					answer.setVisibility(RelativeLayout.GONE);
				}
			});
		}
	}
	
	private String getAppLanguage() {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		return sharedPreferences.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
	}
	
	private RelativeLayout createQuestion(FAQItem item) {
		RelativeLayout layout = new RelativeLayout(getActivity());
		++this.id;
		layout.setId(this.id);
		
		layout.setBackgroundColor(getActivity().getApplicationContext().getResources().getColor(R.color.layout_background));
		
		ImageView arrow = new ImageView(getActivity());
		++this.id;
		arrow.setId(this.id);
		arrow.setImageResource(R.drawable.faq_arrow);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		arrow.setLayoutParams(params);
		
		arrows.add(arrow);
		
		layout.addView(arrow);
		
		CustomTextView questionView = new CustomTextView(getActivity());
		++this.id;
		questionView.setId(this.id);
		
		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.LEFT_OF, arrow.getId());
		
		int rightMargin = (int) this.getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_xlarge);
		params.rightMargin = rightMargin;
		questionView.setLayoutParams(params);
		
		questionsTexts.add(questionView);
		
		questionView.setText(item.getQuestion());
		questionView.setTextColor(getActivity().getApplicationContext().getResources().getColor(R.color.faq_title_closed));
		questionView.setTypeface(null, Typeface.BOLD);
		questionView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getApplicationContext().getResources().getDimension(R.dimen.text_normal));
		
		layout.addView(questionView);
		
		return layout;
	}
	
	private RelativeLayout createAnswer(FAQItem item) {
		RelativeLayout layout = new RelativeLayout(getActivity());
		++this.id;
		layout.setId(this.id);
		
		layout.setBackgroundColor(getActivity().getApplicationContext().getResources().getColor(R.color.layout_background));
		
		ImageView arrow = new ImageView(getActivity());
		++this.id;
		arrow.setId(this.id);
		arrow.setImageResource(R.drawable.faq_arrow);
		arrow.setVisibility(ImageView.INVISIBLE);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		arrow.setLayoutParams(params);
		
		layout.addView(arrow);
		
		RelativeLayout answerView = new RelativeLayout(getActivity());
		++this.id;
		answerView.setId(this.id);
		
		CustomContentParser customContentParser = new CustomContentParser(getActivity(), getContentWidth(), this.faqLayout.getId());
		answerView = customContentParser.parseCustomContent(item.getAnswer());
		
		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.LEFT_OF, arrow.getId());
		int rightMargin = (int) this.getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_xlarge);
		params.rightMargin = rightMargin;
		answerView.setLayoutParams(params);
	
		layout.addView(answerView);
		
		return layout;
	}
	
	private View createDivider() {
		View divider = new View(getActivity());
		++this.id;
		divider.setId(this.id);
		
		LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) MainActivity.dpToPixel(1, getActivity().getApplicationContext()));
		divider.setLayoutParams(params);
		
		divider.setBackgroundColor(getActivity().getApplicationContext().getResources().getColor(R.color.faq_question_divider));
		
		return divider;
	}
	
	public boolean isPortraitOrientation() {
    	return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }
	
	@SuppressWarnings("deprecation")
	private int getDisplayWidth() {
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		
		if (Build.VERSION.SDK_INT < 13) {
			return display.getWidth();
		} else {
			Point size = new Point();
			display.getSize(size);
			
			return size.x;
		}
	}
	
	private int getContentWidth() {
		int marginLeftRight = (int) getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_xlarge);
    	
    	int contentWidth;
    	if (isPortraitOrientation()) {
    		contentWidth = getDisplayWidth() - 2 * marginLeftRight;
    	} else {
    		contentWidth = (int) ((getDisplayWidth() * MainActivity.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH) - (2 * marginLeftRight));
    	}
    	
    	return contentWidth;
	}
}
