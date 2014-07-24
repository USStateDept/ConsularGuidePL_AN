package com.agitive.usembassy.fragments.layoutFragments;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.databases.DatabaseReader;
import com.agitive.usembassy.databases.PassportTrackingLayout;
import com.agitive.usembassy.fragments.asyncTaskFragments.PassportTrackerAsyncTaskFragment;
import com.agitive.usembassy.fragments.dialogFragments.ServerErrorDialogFragment;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.interfaces.FragmentAboveInterface;
import com.agitive.usembassy.interfaces.LayoutFragmentInterface;
import com.agitive.usembassy.layouts.CustomTextView;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class PassportTrackingFragment extends Fragment implements LayoutFragmentInterface, FragmentAboveInterface {

	public static final String LAYOUT_ID_KEY = "com.agitive.usembassy.fragments.layoutFragments.PassportTrackingFragment.layoutIdKey";
	
	private static final String SAVED_INSTANCE_STATE_PROGRESS_BAR_VISIBILITY_KEY = "com.agitive.usembassy.fragments.layoutFragments.PassportTrackingFragment.savedInstanceStateProgressBarVisibilityKey";
	private static final String SAVED_INSTANCE_STATE_PASSPORT_STATUS_KEY = "com.agitive.usembassy.fragments.layoutFragments.PassportTrackingFragment.savedInstanceStateProgressPassportStatusKey";
	private static final double BUTTON_WIDTH_TO_CONTENT_WIDTH = 0.75;
	
	private View rootView;
	private PassportTrackingLayout passportTrackingLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		this.rootView = inflater.inflate(R.layout.passport_tracking_layout, container, false);
		
		setLayout();
		setMarginForEmblem();
		setLayoutName();
		setBackButton();
		setTNTLocationButtonWidth();
		setTNTLocationText();
		setTNTLocationButtonOnClickListener();
		setSendButtonOnClickListener();
		
		restoreSaveInstanceState(savedInstanceState);
		setContent(savedInstanceState);
		
		return this.rootView;
	}
	
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    
        savedInstanceState.putInt(PassportTrackingFragment.SAVED_INSTANCE_STATE_PROGRESS_BAR_VISIBILITY_KEY, getProgressBarVisibility());
        savedInstanceState.putString(PassportTrackingFragment.SAVED_INSTANCE_STATE_PASSPORT_STATUS_KEY, getPassportStatusText());
    }

	@Override
	public void changeLanguage() {
		setLayoutName();
		changeDescriptionLanguage();
		setTNTLocationText();
		changePassportNumberLanguage();
		changeSendButtonLanguage();
	}
	
	@Override
	public void setContent() {
		RelativeLayout mainProgressBarLayout = (RelativeLayout) this.rootView.findViewById(R.id.passport_tracking_layout_main_progress_bar_layout);
		if (mainProgressBarLayout == null) {
			return;
		}
		
		mainProgressBarLayout.setVisibility(RelativeLayout.INVISIBLE);
	}
	
	public void removePassportTrackerAsyncTaskFragment() {
		FragmentManager fragmentManager = getChildFragmentManager();
		Fragment passportTrackerAsyncTaskFragment = fragmentManager.findFragmentByTag(PassportTrackerAsyncTaskFragment.TAG);
		if (passportTrackerAsyncTaskFragment != null) {
			try {
				fragmentManager.beginTransaction().remove(passportTrackerAsyncTaskFragment).commit();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setPassportStatus(String status) {
		if (status == null) {
			setProgressBarVisibility(View.INVISIBLE);
			showServerError();
			return;
		}
		
		setProgressBarVisibility(View.INVISIBLE);
		showPassportStatus(status);
	}
	
	public void setContent(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		}
		
		setContent();
	}
	
	private void setLayout() {
		DatabaseReader databaseReader = new DatabaseReader(getActivity());
		int id = getArguments().getInt(PassportTrackingFragment.LAYOUT_ID_KEY);
		
		this.passportTrackingLayout = (PassportTrackingLayout) databaseReader.getLayout(id);
	}
	
	private String getAppLanguage() {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		return sharedPreferences.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
	}
	
	private void setLayoutName() {
		CustomTextView layoutName = (CustomTextView) this.rootView.findViewById(R.id.passport_tracking_layout_layout_name);
		if (layoutName == null) {
			return;
		}
		
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			layoutName.setText(this.passportTrackingLayout.getTitleEn());
		} else {
			layoutName.setText(this.passportTrackingLayout.getTitlePl());
		}
	}
	
	private void changeDescriptionLanguage() {
		CustomTextView description = (CustomTextView) this.rootView.findViewById(R.id.passport_tracking_layout_description);
		description.setText(R.string.passport_tracking_description);
	}
	
	private void setTNTLocationButtonWidth() {
		RelativeLayout tntLocationButton = (RelativeLayout) this.rootView.findViewById(R.id.passport_tracking_layout_tnt_location_button);
		if (tntLocationButton == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tntLocationButton.getLayoutParams();
		params.width = getButtonWidth();
		tntLocationButton.setLayoutParams(params);
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
	
	private int getButtonWidth() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return (int) (getDisplayWidth() * PassportTrackingFragment.BUTTON_WIDTH_TO_CONTENT_WIDTH);
		} else {
			return (int) (getDisplayWidth() * Global.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH * PassportTrackingFragment.BUTTON_WIDTH_TO_CONTENT_WIDTH);
		}
	}
	
	private void setTNTLocationButtonOnClickListener() {
		RelativeLayout tntLocationButton = (RelativeLayout) this.rootView.findViewById(R.id.passport_tracking_layout_tnt_location_button);
		if (tntLocationButton == null) {
			return;
		}
		
		tntLocationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((MainActivity)getActivity()).openLayout(DatabaseReader.TNT_LOCATION_LAYOUT_ID, DatabaseReader.PASSPORT_TRACKING_LAYOUT_ID);
			}
			
		});
	}
	
	private int getOrientation() {
		return getResources().getConfiguration().orientation;
	}
	
	private void setTNTLocationText() {
		CustomTextView buttonText = (CustomTextView) this.rootView.findViewById(R.id.passport_tracking_layout_tnt_location_button_text);
		if (buttonText == null) {
			return;
		}
		
		buttonText.setText(getButtonText());
	}
	
	private String getButtonText() {
		DatabaseReader databaseReader = new DatabaseReader(getActivity());
		
		if(getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			return databaseReader.getLayout(DatabaseReader.TNT_LOCATION_LAYOUT_ID).getTitleEn();
		} else {
			return databaseReader.getLayout(DatabaseReader.TNT_LOCATION_LAYOUT_ID).getTitlePl();
		}
	}
	
	private String getPassportStatusText() {
		CustomTextView statusText = (CustomTextView) this.rootView.findViewById(R.id.passport_tracking_layout_status_text);
		if (statusText == null) {
			return null;
		}
		
		return statusText.getText().toString();
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
		arguments.putString(ServerErrorDialogFragment.MESSAGE_KEY, getResources().getString(R.string.passport_tracking_server_error_message));
		
		return arguments;
	}
	
	private void showPassportStatus(String status) {
		CustomTextView statusText = (CustomTextView) this.rootView.findViewById(R.id.passport_tracking_layout_status_text);
		if (statusText == null) {
			return;
		}
		
		statusText.setText(status);
	}
	
	private void changePassportNumberLanguage() {
		EditText passportNumber = (EditText) this.rootView.findViewById(R.id.passport_tracking_layout_passport_number);
		if (passportNumber == null) {
			return;
		}
		
		passportNumber.setHint(R.string.passport_tracking_passport_number);
	}
	
	private void changeSendButtonLanguage() {
		CustomTextView sendButton = (CustomTextView) this.rootView.findViewById(R.id.passport_tracking_layout_send_button);
		if (sendButton == null) {
			return;
		}
		
		sendButton.setText(R.string.passport_tracking_send);
	}
	
	private void restoreSaveInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		}
		
		setProgressBarVisibility(savedInstanceState.getInt(PassportTrackingFragment.SAVED_INSTANCE_STATE_PROGRESS_BAR_VISIBILITY_KEY));
		setPassportStatus(savedInstanceState.getString(PassportTrackingFragment.SAVED_INSTANCE_STATE_PASSPORT_STATUS_KEY));
	}
	
	private void setSendButtonOnClickListener() {
		CustomTextView sendButton = (CustomTextView) this.rootView.findViewById(R.id.passport_tracking_layout_send_button);
		if (sendButton == null) {
			return;
		}
		
		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isPassportNumberEmpty()) {
					showPassportNumberEmptyToast();
					return;
				}
				
				if (!isOnline()){
					showNoInternetToast();
					return;
				}
				
				showPassportStatus("");
				setProgressBarVisibility(View.VISIBLE);
				addPassportTrackerAsyncTaskFragment();
			}
			
		});
	}
	
	private void addPassportTrackerAsyncTaskFragment() {
		PassportTrackerAsyncTaskFragment passportTrackerAsyncTaskFragment = new PassportTrackerAsyncTaskFragment();
		passportTrackerAsyncTaskFragment.setArguments(createArgumentsForPassportTracker());
		
		FragmentManager fragmentManager = getChildFragmentManager();
		
		try {
			fragmentManager.beginTransaction().add(passportTrackerAsyncTaskFragment, PassportTrackerAsyncTaskFragment.TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isPassportNumberEmpty() {
		EditText passportNumber = (EditText) this.rootView.findViewById(R.id.passport_tracking_layout_passport_number);
		if (passportNumber == null) {
			return true;
		}
		
		return passportNumber.getText().toString().isEmpty();
	}
	
	private void showNoInternetToast() {
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.passport_tracking_no_internet, Toast.LENGTH_LONG);
        toast.show();
    }
	
	private void showPassportNumberEmptyToast() {
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.passport_tracking_passport_number_empty, Toast.LENGTH_LONG);
        toast.show();
    }
	
	private boolean isOnline() {
	    ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	 
	    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
	    return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
	}
	
	private void setProgressBarVisibility(int visibility) {
		ProgressBar progressBar = (ProgressBar) this.rootView.findViewById(R.id.passport_tracking_layout_progress_bar);
		if (progressBar == null) {
			return;
		}
		
		progressBar.setVisibility(visibility);
	}
	
	private int getProgressBarVisibility() {
		ProgressBar progressBar = (ProgressBar) this.rootView.findViewById(R.id.passport_tracking_layout_progress_bar);
		if (progressBar == null) {
			return ProgressBar.INVISIBLE;
		}
		
		return progressBar.getVisibility();
	}
	
	private String getPassportNumber() {
		EditText passportNumber = (EditText) this.rootView.findViewById(R.id.passport_tracking_layout_passport_number);
		if (passportNumber == null) {
			return null;
		}
		
		return passportNumber.getText().toString();
	}
	
	private Bundle createArgumentsForPassportTracker() {
		Bundle arguments = new Bundle();
		arguments.putString(PassportTrackerAsyncTaskFragment.ARGUMENTS_PASSPORT_NUMBER_KEY, getPassportNumber());
		
		return arguments;
	}
	
	private void setMarginForEmblem() {
		CustomTextView layoutName = (CustomTextView) this.rootView.findViewById(R.id.passport_tracking_layout_layout_name);
		if (layoutName == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutName.getLayoutParams();
		params.topMargin = ((MainActivity)getActivity()).getEmblemPartOutsideSize();
		params.topMargin += getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_small);
		layoutName.setLayoutParams(params);
	}
	
	private void setBackButton() {
		ImageView backButton = (ImageView) this.rootView.findViewById(R.id.passport_tracking_layout_back_button);
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
}
