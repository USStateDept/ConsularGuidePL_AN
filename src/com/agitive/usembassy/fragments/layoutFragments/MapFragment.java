package com.agitive.usembassy.fragments.layoutFragments;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.databases.DatabaseReader;
import com.agitive.usembassy.databases.MapLayout;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.interfaces.FragmentAboveInterface;
import com.agitive.usembassy.interfaces.LayoutFragmentInterface;
import com.agitive.usembassy.layouts.CustomTextView;
import com.agitive.usembassy.parsers.CustomContentParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MapFragment extends Fragment implements LayoutFragmentInterface, FragmentAboveInterface {
	
	
	public static final String LAYOUT_ID_KEY = "com.agitive.usembassy.fragments.MapFragment.layoutId";
	public static final String IS_IN_STEPS_KEY = "com.agitive.usembassy.fragments.MapFragment.isInStepsKey";
	
	private View rootView;
	private SupportMapFragment mapFragment;
	private MapLayout mapLayout;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    
        this.rootView = inflater.inflate(R.layout.map_layout, container, false);
        
    	
    	this.mapFragment = SupportMapFragment.newInstance();
    	
    	try {
    		getChildFragmentManager().beginTransaction().add(R.id.map_layout_map_container, mapFragment).commit();
    	} catch (IllegalStateException e) {
    		e.printStackTrace();
    	}
    	
    	setLayout();
    	setMarginForEmblem();
    	setLayoutName();
        setBackButton();
        
        setMapHeight();    	
    	setGoogleMap(this.mapLayout);
    	setClickListenerOnMap();
    	setContent(savedInstanceState);
        
        return this.rootView;
	}
	
	@Override
	public void changeLanguage() {
		showMainProgressBar();
		
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				setLayoutName();
				
				RelativeLayout rootLayout = (RelativeLayout) rootView.findViewById(R.id.map_layout_custom_content);
				if (rootLayout == null) {
					return;
				}
				
				rootLayout.removeAllViews();
				
				setCustomContent();
		    	hideMainProgressBar();
			}
		});
	}
	
	@Override
	public void setContent() {
		setCustomContent();
    	hideMainProgressBar();
	}
	
	private void setContent(Bundle savedInstanceState) {
		if (savedInstanceState == null &&
				!(getArguments().getBoolean(MapFragment.IS_IN_STEPS_KEY, false))) {
			return;
		}
		
		setContent();
	}
	
	private void setCustomContent() {
		String content = getContentByAppLanguage(this.mapLayout);
    	
    	CustomContentParser parser = new CustomContentParser(getActivity(), getContentWidth(), this.mapLayout.getId());
    	RelativeLayout customContent = parser.parseCustomContent(content);
    	
    	moveViewsToLocalCustomContent(customContent);
	}
	
	private void setLayout() {
		DatabaseReader databaseReader = new DatabaseReader(getActivity());
		int id = getArguments().getInt(MapFragment.LAYOUT_ID_KEY);
		this.mapLayout = (MapLayout) databaseReader.getLayout(id);
	}
	
	private void setGoogleMap(final MapLayout layout) {
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				GoogleMap googleMap = mapFragment.getMap();
				if (googleMap != null) {
			    	UiSettings uiSettings = googleMap.getUiSettings();
			    	uiSettings.setZoomControlsEnabled(false);
			    	uiSettings.setAllGesturesEnabled(false);
			    	
			    	LatLng location = new LatLng(layout.getLatitude(), layout.getLongitude());
			    	googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, layout.getZoom()));
			    	BitmapDescriptor marker = BitmapDescriptorFactory.fromResource(R.drawable.map_marker);
			    	googleMap.addMarker(new MarkerOptions().icon(marker).position(location));
				} else {
					handler.postDelayed(this, 100);
				}
			}
			
		}, 100);
	}
	
	private String getContentByAppLanguage(MapLayout mapLayout) {
		if (getAppLanguage().equals("EN")) {
    		return mapLayout.getContentEn();
    	} else {
    		return mapLayout.getContentPl();
    	}
	}
	
	private String getAppLanguage() {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		return sharedPreferences.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
	}
	
	private void moveViewsToLocalCustomContent(RelativeLayout customContent) {
		RelativeLayout localCustomContent = (RelativeLayout) this.rootView.findViewById(R.id.map_layout_custom_content);
		if (localCustomContent == null) {
			return;
		}
		
    	while (customContent.getChildCount() > 0) {
    		View view = customContent.getChildAt(0);
    		customContent.removeViewAt(0); 
    		localCustomContent.addView(view);
    	}
	}
	
	private void setClickListenerOnMap() {
		View mapClicker = this.rootView.findViewById(R.id.map_layout_map_clicker);
		if (mapClicker == null) {
			return;
		}
		
		mapClicker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String uri = "geo:" + mapLayout.getLatitude() + "," + mapLayout.getLongitude() + "?q=" + mapLayout.getLatitude() + "," + mapLayout.getLongitude() + "&z=" + mapLayout.getZoom();
				Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				startActivity(mapIntent);
			}
		});
	}
	
	private void hideMainProgressBar() {
		RelativeLayout mainProgressBarLayout = (RelativeLayout) this.rootView.findViewById(R.id.map_layout_main_progress_bar_layout);
		if (mainProgressBarLayout == null) {
			return;
		}
		
		mainProgressBarLayout.setVisibility(RelativeLayout.INVISIBLE);
	}
	
	private void showMainProgressBar() {
		RelativeLayout mainProgressBarLayout = (RelativeLayout) this.rootView.findViewById(R.id.map_layout_main_progress_bar_layout);
		if (mainProgressBarLayout == null) {
			return;
		}
		
		mainProgressBarLayout.setVisibility(RelativeLayout.VISIBLE);
	}
	
	private void setMarginForEmblem() {
		CustomTextView menuName = (CustomTextView) this.rootView.findViewById(R.id.map_layout_layout_name);
		if (menuName == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) menuName.getLayoutParams();
		params.topMargin = ((MainActivity)getActivity()).getEmblemPartOutsideSize();
		params.topMargin += getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_small);
		menuName.setLayoutParams(params);
	}
	
	private void setLayoutName() {
		CustomTextView layoutName = (CustomTextView) this.rootView.findViewById(R.id.map_layout_layout_name);
		if (layoutName == null) {
			return;
		}
		
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			layoutName.setText(this.mapLayout.getTitleEn());
		} else {
			layoutName.setText(this.mapLayout.getTitlePl());
		}
	}
	
	private void setBackButton() {
		ImageView backButton = (ImageView) this.rootView.findViewById(R.id.map_layout_back_button);
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
	
	private void setMapHeight() {
		RelativeLayout mapLayout = (RelativeLayout) this.rootView.findViewById(R.id.map_layout_map_layout);
		if (mapLayout == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mapLayout.getLayoutParams();
		
		params.height = (int) (getDisplayHeight() * 0.35);
		mapLayout.setLayoutParams(params);
	}
	
	@SuppressWarnings("deprecation")
	private int getDisplayHeight() {
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		
		if (Build.VERSION.SDK_INT < 13) {
			return display.getHeight();
		} else {
			Point size = new Point();
			display.getSize(size);
			
			return size.y;
		}
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
