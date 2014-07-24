package com.agitive.usembassy.fragments.asyncTaskFragments;

import com.agitive.usembassy.fragments.layoutFragments.FeedbackFragment;
import com.agitive.usembassy.network.FeedbackSenderAsyncTask;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class FeedbackSenderFragment extends Fragment {
	
	public static final String TAG = "com.agitive.usembassy.fragments.asyncTaskFragments.FeedbackSenderFragment.tag";
	public static final String ARGUMNETS_FEEDBACK_CONTENT_KEY = "com.agitive.usembassy.fragments.asyncTaskFragments.FeedbackSenderFragment.argumentsFeedbackContentKey";
	public static final String ARGUMNETS_EMAIL_KEY = "com.agitive.usembassy.fragments.asyncTaskFragments.FeedbackSenderFragment.argumentsEmailKey";
	public static final String ARGUMNETS_LAYOUT_ID_KEY = "com.agitive.usembassy.fragments.asyncTaskFragments.FeedbackSenderFragment.argumentsLayoutIdKey";
	
	private static final String SAVED_INSTANCE_STATE_IS_FIRST_RUN_KEY = "com.agitive.usembassy.fragments.asyncTaskFragments.FeedbackSenderFragment.savedInstanceStateIsFirstRunKey";
	private static FeedbackSenderFragment feedbackSenderFragment;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		FeedbackSenderFragment.feedbackSenderFragment = this;
		
		if (savedInstanceState != null) {
			return;
		}
		
		FeedbackSenderAsyncTask feedbackSender = new FeedbackSenderAsyncTask();
		feedbackSender.execute(getFeedbackContent(), getEmail(), getLayoutId());
	}
	
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        
        savedInstanceState.putBoolean(FeedbackSenderFragment.SAVED_INSTANCE_STATE_IS_FIRST_RUN_KEY, false);
    }
	
	public static void setFeedbackSenderResult(boolean result) {
		if (FeedbackSenderFragment.feedbackSenderFragment.getParentFragment() == null ||
				FeedbackSenderFragment.feedbackSenderFragment.getActivity() == null) {
			return;
		}
			
		((FeedbackFragment)FeedbackSenderFragment.feedbackSenderFragment.getParentFragment()).setFeedbackSenderResult(result);
		((FeedbackFragment)FeedbackSenderFragment.feedbackSenderFragment.getParentFragment()).removeFeedbackSenderFragment();
	}
	
	private String getFeedbackContent() {
		return getArguments().getString(FeedbackSenderFragment.ARGUMNETS_FEEDBACK_CONTENT_KEY);
	}
	
	private String getEmail() {
		return getArguments().getString(FeedbackSenderFragment.ARGUMNETS_EMAIL_KEY);
	}
	
	private int getLayoutId() {
		return getArguments().getInt(FeedbackSenderFragment.ARGUMNETS_LAYOUT_ID_KEY);
	}
}
