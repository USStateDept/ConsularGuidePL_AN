package com.agitive.usembassy.fragments.layoutFragments;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.layouts.CustomTextView;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class ArticleFragment extends Fragment {
	
	public static String ARTICLE_TITLE_KEY = "com.agitive.usembassy.fragment.ArticleFragment.articleTitle";
	public static String ARTICLE_DATE_KEY = "com.agitive.usembassy.fragment.ArticleFragment.articleDate";
	public static String ARTICLE_TEXT_KEY = "com.agitive.usembassy.fragment.ArticleFragment.articleText";
	public static String ARTICLE_LANDSCAPE_ID_KEY = "com.agitive.usembassy.fragment.ArticleFragment.landscapeId";
	public static String ARTICLE_LANDSCAPE_HEIGHT_KEY = "com.agitive.usembassy.fragment.ArticleFragment.landscapeHeight";
	public static String ARTICLE_LAYOUT_BACKGROUND_ANIMATE_KEY = "com.agitive.usembassy.fragment.ArticleFragment.articleLayoutBackgroundAnimateKey";
	
	private static final int ANIMATION_DURATION = 500;
	
	private View rootView;
	private int[] newsBackgroundsIds;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.rootView = inflater.inflate(R.layout.article_layout, container, false);
        
        setMarginForEmblem();
        setLayoutName();
        setBackButton();
        setNewsBackgroundsNames();
        setLandscape();
        setLandscapeGradient();
        setArticleTitle();
        setCalendarLayoutVisibility();
        setArticleDate();
        setArticleText();
        setArticleTitleMargin();
        animateGradient();
        disableBackgroundAnimate();
	
        return this.rootView;
    }
	
	private void disableBackgroundAnimate() {
		getArguments().putBoolean(ArticleFragment.ARTICLE_LAYOUT_BACKGROUND_ANIMATE_KEY, false);
	}
	
	private void animateGradient() {
		AnimatorSet animatorSet = new AnimatorSet();
		
		if (getArguments().getBoolean(ArticleFragment.ARTICLE_LAYOUT_BACKGROUND_ANIMATE_KEY)) {
			RelativeLayout rootLayout = (RelativeLayout) this.rootView.findViewById(R.id.article_layout_root_layout);
			if (rootLayout == null) {
				return;
			}
			
			ObjectAnimator rootLayoutAnimator = ObjectAnimator.ofFloat(rootLayout, "alpha", 0, 1);
			animatorSet.play(rootLayoutAnimator);
		} else {
			ImageView landscape = (ImageView) this.rootView.findViewById(R.id.article_layout_landscape);
			if (landscape == null) {
				return;
			}
			
			ScrollView scrollView = (ScrollView) this.rootView.findViewById(R.id.article_layout_scroll_view);
			if (scrollView == null) {
				return;
			}
			
			ObjectAnimator landscapeAnimator = ObjectAnimator.ofFloat(landscape, "alpha", 0, 1);
			ObjectAnimator scrollViewAnimator = ObjectAnimator.ofFloat(scrollView, "alpha", 0, 1);
			animatorSet.playTogether(landscapeAnimator, scrollViewAnimator);
		}
	
		animatorSet.setDuration(ArticleFragment.ANIMATION_DURATION);
		animatorSet.start();
	}
	
	private void setArticleTitleMargin() {
		CustomTextView layoutTitle = (CustomTextView) this.rootView.findViewById(R.id.article_layout_title);
		if (layoutTitle == null) {
			return;
		}
		
		layoutTitle.setPadding(layoutTitle.getPaddingLeft(), getArguments().getInt(ArticleFragment.ARTICLE_LANDSCAPE_HEIGHT_KEY), layoutTitle.getPaddingRight(), layoutTitle.getPaddingBottom());
	}
	
	private void setMarginForEmblem() {
		CustomTextView menuName = (CustomTextView) this.rootView.findViewById(R.id.article_layout_name);
		if (menuName == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) menuName.getLayoutParams();
		params.topMargin = ((MainActivity)getActivity()).getEmblemPartOutsideSize();
		params.topMargin += getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_small);
		menuName.setLayoutParams(params);
	}
	
	private void setNewsBackgroundsNames() {
		this.newsBackgroundsIds = new int[12];
		
		this.newsBackgroundsIds[0] = R.drawable.news_background_0;
		this.newsBackgroundsIds[1] = R.drawable.news_background_1;
		this.newsBackgroundsIds[2] = R.drawable.news_background_2;
		this.newsBackgroundsIds[3] = R.drawable.news_background_3;
		this.newsBackgroundsIds[4] = R.drawable.news_background_4;
		this.newsBackgroundsIds[5] = R.drawable.news_background_5;
		this.newsBackgroundsIds[6] = R.drawable.news_background_6;
		this.newsBackgroundsIds[7] = R.drawable.news_background_7;
		this.newsBackgroundsIds[8] = R.drawable.news_background_8;
		this.newsBackgroundsIds[9] = R.drawable.news_background_9;
		this.newsBackgroundsIds[10] = R.drawable.news_background_10;
		this.newsBackgroundsIds[11] = R.drawable.news_background_11;
	}
	
	private void setLandscape() {
		ImageView landscape = (ImageView) this.rootView.findViewById(R.id.article_layout_landscape);
		if (landscape == null) {
			return;
		}
		
		int landscapeId = this.newsBackgroundsIds[getArguments().getInt(ArticleFragment.ARTICLE_LANDSCAPE_ID_KEY)];
		landscape.setImageResource(landscapeId);
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) landscape.getLayoutParams();
		params.height = getArguments().getInt(ArticleFragment.ARTICLE_LANDSCAPE_HEIGHT_KEY);
		landscape.setLayoutParams(params);
	}
	
	private void setLandscapeGradient() {
		final ImageView landscapeGradient = (ImageView) this.rootView.findViewById(R.id.article_layout_gradient);
		if (landscapeGradient == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) landscapeGradient.getLayoutParams();
		params.height = getArguments().getInt(ArticleFragment.ARTICLE_LANDSCAPE_HEIGHT_KEY);
		landscapeGradient.setLayoutParams(params);
	}

	private void setArticleTitle() {
		CustomTextView title = (CustomTextView) this.rootView.findViewById(R.id.article_layout_title);
		if (title == null) {
			return;
		}
		
		title.setText(getArguments().getString(ArticleFragment.ARTICLE_TITLE_KEY));
	}
	
	private void setCalendarLayoutVisibility() {
		RelativeLayout calendarLayout = (RelativeLayout) this.rootView.findViewById(R.id.article_layout_calendar);
		if (calendarLayout == null) {
			return;
		}
		
		if (getArguments().getString(ArticleFragment.ARTICLE_DATE_KEY) == null) {
			calendarLayout.setVisibility(RelativeLayout.GONE);
		} else {
			calendarLayout.setVisibility(RelativeLayout.VISIBLE);
		}
	}
	
	private void setLayoutName() {
		CustomTextView layoutName = (CustomTextView) this.rootView.findViewById(R.id.article_layout_name);
		if (layoutName == null) {
			return;
		}
		
		layoutName.setText(R.string.article_layout_layout_name);
	}
	
	private void setBackButton() {
		ImageView backButton = (ImageView) this.rootView.findViewById(R.id.article_back_button);
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
	
	private void setArticleDate() {
		CustomTextView date = (CustomTextView) this.rootView.findViewById(R.id.article_layout_date);
		if (date == null) {
			return;
		}
		
		if (getArguments().getString(ArticleFragment.ARTICLE_DATE_KEY) == null) {
			return;
		}
		
		date.setVisibility(CustomTextView.VISIBLE);
		date.setText(getArguments().getString(ArticleFragment.ARTICLE_DATE_KEY));
	}
	
	private void setArticleText() {
		CustomTextView layoutContent = (CustomTextView) this.rootView.findViewById(R.id.article_layout_content);
		if (layoutContent == null) {
			return;
		}
		
		layoutContent.setText(getArguments().getString(ArticleFragment.ARTICLE_TEXT_KEY));
	}
}
