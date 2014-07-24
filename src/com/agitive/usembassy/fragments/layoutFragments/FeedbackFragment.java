package com.agitive.usembassy.fragments.layoutFragments;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.databases.DatabaseReader;
import com.agitive.usembassy.fragments.asyncTaskFragments.FeedbackSenderFragment;
import com.agitive.usembassy.fragments.dialogFragments.ServerErrorDialogFragment;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.layouts.CustomTextView;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class FeedbackFragment extends Fragment {
	
	public static final String LAYOUT_ID_KEY = "com.agitive.usembassy.fragments.FeedbackFragment.layoutId";
	
	private static final String LAST_SEND_TIME_KEY = "com.agitive.usembassy.fragments.FeedbackFragment.lastSendTime";
	private static final double BUTTON_WIDTH_TO_CONTENT_WIDTH = 0.75;
	
	private View rootView;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		this.rootView = inflater.inflate(R.layout.feedback_layout, container, false);
		
		setMarginForEmblem();
		setBackButton();
		setContactInformationButtonWidth();
		setContactInformationText();
		setContactInformationButtonOnClickListener();
		setSendButtonOnClickListener();
		
		return rootView;
	}
	
	public void setFeedbackSenderResult(boolean isSuccess) {
		if (isSuccess) {
			saveSendTime();
			
			Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.feedback_send_successful, Toast.LENGTH_LONG);
			toast.show();
			
			((MainActivity)getActivity()).closeFeedbackLayout();
		} else {
			showServerError();
		}
	}
	
	public void removeFeedbackSenderFragment() {
		FragmentManager fragmentManager = getChildFragmentManager();
		Fragment feedbackSenderFragment = fragmentManager.findFragmentByTag(FeedbackSenderFragment.TAG);
		if (feedbackSenderFragment != null) {
			try {
				fragmentManager.beginTransaction().remove(feedbackSenderFragment).commit();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void changeLanguage() {
		changeDescriptionLanguage();
		changeLayoutNameLanguage();
		changeEmailHintLanguage();
		changeContentHintLanguage();
		changeSendButtonLanguage();
		setContactInformationText();
	}
	
	private void setContactInformationText() {
		CustomTextView buttonText = (CustomTextView) this.rootView.findViewById(R.id.feedback_layout_contact_information_button_text);
		if (buttonText == null) {
			return;
		}
		
		buttonText.setText(getButtonText());
	}
	
	private void setContactInformationButtonOnClickListener() {
		RelativeLayout contactInformationButton = (RelativeLayout) this.rootView.findViewById(R.id.feedback_layout_contact_information_button);
		if (contactInformationButton == null) {
			return;
		}
		
		contactInformationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((MainActivity)getActivity()).openLayout(DatabaseReader.CONTACT_INFORMATION_LAYOUT_ID, 0);
			}
			
		});
	}
	
	private String getButtonText() {
		DatabaseReader databaseReader = new DatabaseReader(getActivity());
		
		if(getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			return databaseReader.getLayout(DatabaseReader.CONTACT_INFORMATION_LAYOUT_ID).getTitleEn();
		} else {
			return databaseReader.getLayout(DatabaseReader.CONTACT_INFORMATION_LAYOUT_ID).getTitlePl();
		}
	}
	
	private String getAppLanguage() {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		return sharedPreferences.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
	}
	
	private void showServerError() {
		ServerErrorDialogFragment serverErrorDialogFragment = new ServerErrorDialogFragment();
		serverErrorDialogFragment.setArguments(createArgumentsForServerErrorDialogFragment());
		serverErrorDialogFragment.setCancelable(false);
		FragmentManager fragmentManager = getChildFragmentManager();
		
		try {
			fragmentManager.beginTransaction().add(serverErrorDialogFragment, ServerErrorDialogFragment.TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private Bundle createArgumentsForServerErrorDialogFragment() {
		Bundle arguments = new Bundle();
		arguments.putString(ServerErrorDialogFragment.MESSAGE_KEY, getResources().getString(R.string.feedback_server_error_message));
		
		return arguments;
	}
	
	private void setBackButton() {
		ImageView backButton = (ImageView) this.rootView.findViewById(R.id.feedback_layout_back_button);
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
	
	private void setMarginForEmblem() {
		CustomTextView layoutName = (CustomTextView) this.rootView.findViewById(R.id.feedback_layout_layout_name);
		if (layoutName == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutName.getLayoutParams();
		params.topMargin = ((MainActivity)getActivity()).getEmblemPartOutsideSize();
		params.topMargin += getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_small);
		layoutName.setLayoutParams(params);
	}
	
	private boolean isContentEmpty() {
		EditText feedbackContent = (EditText) rootView.findViewById(R.id.feedback_layout_content);
		if (feedbackContent == null) {
			return true;
		}
		
		return feedbackContent.getText().toString().isEmpty();
	}
	
	private boolean isEmailValid() {
		EditText email = (EditText) rootView.findViewById(R.id.feedback_layout_email);
		if (email == null) {
			return false;
		}
		
		if (email.getText().toString().isEmpty()) {
			return true;
		}
		
		if (Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
			return true;
		}
		
		return false;
	}
	
	private void setSendButtonOnClickListener() {
		RelativeLayout sendButtonLayout = (RelativeLayout) this.rootView.findViewById(R.id.feedback_layout_send_button_layout);
		if (sendButtonLayout == null) {
			return;
		}
		
		sendButtonLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (isContentEmpty()) {
					showToastContentEmpty();
					
					return;
				}
				
				if (!isEmailValid()) {
					showToastEmailAddressNotValid();
						
					return;
				}
				
				
				if (isTooShortTimeToSend()) {
					showToastTooShortTime();
					
					return;
				}
				
				if (!isOnline()) {
					showNoInternetToast();
					return;
				}
				
				addFeedbackSenderFragment();
			}
			
		});
	}
	
	private void addFeedbackSenderFragment() {
		FragmentManager fragmentManager = getChildFragmentManager();
		FeedbackSenderFragment feedbackSenderFragment = new FeedbackSenderFragment();
		feedbackSenderFragment.setArguments(createArgumentsForFeedbackSenderFragment());
		
		try {
			fragmentManager.beginTransaction().add(feedbackSenderFragment, FeedbackSenderFragment.TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private Bundle createArgumentsForFeedbackSenderFragment() {
		Bundle arguments = new Bundle();
		arguments.putString(FeedbackSenderFragment.ARGUMNETS_FEEDBACK_CONTENT_KEY, getFeedbackContent());
		arguments.putString(FeedbackSenderFragment.ARGUMNETS_EMAIL_KEY, getEmail());
		arguments.putInt(FeedbackSenderFragment.ARGUMNETS_LAYOUT_ID_KEY, getArguments().getInt(FeedbackFragment.LAYOUT_ID_KEY));
		
		return arguments;
	}
	
	private String getEmail() {
		EditText email = (EditText) this.rootView.findViewById(R.id.feedback_layout_email);
		if (email == null) {
			return null;
		}
		
		return email.getText().toString();
	}
	
	private String getFeedbackContent() {
		EditText content = (EditText) this.rootView.findViewById(R.id.feedback_layout_content);
		if (content == null) {
			return null;
		}
		
		return content.getText().toString();
	}
	
	private void setContactInformationButtonWidth() {
		RelativeLayout contactInformationButton = (RelativeLayout) this.rootView.findViewById(R.id.feedback_layout_contact_information_button);
		if (contactInformationButton == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) contactInformationButton.getLayoutParams();
		params.width = getButtonWidth();
		contactInformationButton.setLayoutParams(params);
	}
	
	private int getButtonWidth() {
		return (int) (getDisplayWidth() * FeedbackFragment.BUTTON_WIDTH_TO_CONTENT_WIDTH);
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
	
	private void changeDescriptionLanguage() {
		CustomTextView description = (CustomTextView) this.rootView.findViewById(R.id.feedback_layout_description);
		description.setText(R.string.feedback_description);
	}
	
	private void changeSendButtonLanguage() {
		CustomTextView sendButtonText = (CustomTextView) this.rootView.findViewById(R.id.feedback_layout_send_button_text);
		if (sendButtonText == null) {
			return;
		}
		
		sendButtonText.setText(R.string.feedback_send);
	}
	
	private void changeEmailHintLanguage() {
		EditText email = (EditText) this.rootView.findViewById(R.id.feedback_layout_email);
		if (email == null) {
			return;
		}
		
		email.setHint(R.string.feedback_email);
	}
	
	private void changeLayoutNameLanguage() {
		CustomTextView layoutName = (CustomTextView) this.rootView.findViewById(R.id.feedback_layout_layout_name);
		if (layoutName == null) {
			return;
		}
		
		layoutName.setText(R.string.feedback_title);
	}
	
	private void changeContentHintLanguage() {
		EditText content = (EditText) this.rootView.findViewById(R.id.feedback_layout_content);
		if (content == null) {
			return;
		}
		
		content.setHint(R.string.feedback_content);
	}
	
	private void showToastContentEmpty() {
		Toast toast = Toast.makeText(getActivity(), R.string.feedback_empty_content, Toast.LENGTH_LONG);
		toast.show();
	}
	
	private void showToastEmailAddressNotValid() {
		Toast toast = Toast.makeText(getActivity(), R.string.feedback_email_address_not_valid, Toast.LENGTH_LONG);
		toast.show();
	}
	
	private boolean isOnline() {
	    ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	 
	    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
	    return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
	}
	
	private void showNoInternetToast() {
		Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.feedback_no_internet, Toast.LENGTH_LONG);
		toast.show();
	}
	
	private boolean isTooShortTimeToSend() {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		if (!sharedPreferences.contains(FeedbackFragment.LAST_SEND_TIME_KEY)) {	
			return false;
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		Date lastSendDate;
		try {
			lastSendDate = dateFormat.parse(sharedPreferences.getString(FeedbackFragment.LAST_SEND_TIME_KEY, "01.01.1970 00:00"));
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			Log.e(Global.TAG, "date parsing error in feedback");
			
			return false;
		}
		
		
		Date date1MinuteAgo = new Date(System.currentTimeMillis() - 60 * 1000);
		if (lastSendDate.before(date1MinuteAgo)) {			
			return false;
		}
		
		return true;
	}
	
	private void saveSendTime() {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		
		Date nowDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		editor.putString(FeedbackFragment.LAST_SEND_TIME_KEY, dateFormat.format(nowDate));
		
		editor.commit();
	}
	
	private void showToastTooShortTime() {
		Toast toast = Toast.makeText(getActivity(), R.string.feedback_too_short_time, Toast.LENGTH_LONG);
		toast.show();
	}
}
