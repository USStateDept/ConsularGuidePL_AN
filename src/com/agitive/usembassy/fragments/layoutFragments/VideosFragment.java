package com.agitive.usembassy.fragments.layoutFragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.activities.VideoPlayerActivity;
import com.agitive.usembassy.activities.YouTubePlayerActivity;
import com.agitive.usembassy.databases.DatabaseReader;
import com.agitive.usembassy.databases.VideosLayout;
import com.agitive.usembassy.fragments.dialogFragments.ServerErrorDialogFragment;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.interfaces.FragmentAboveInterface;
import com.agitive.usembassy.interfaces.LayoutFragmentInterface;
import com.agitive.usembassy.layouts.CustomTextView;
import com.agitive.usembassy.network.VideoIdSenderAsyncTask;
import com.agitive.usembassy.network.VideosDownloaderAsyncTask;
import com.agitive.usembassy.objects.VideoItem;
import com.squareup.picasso.Picasso;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class VideosFragment extends Fragment implements LayoutFragmentInterface, FragmentAboveInterface {

	public static final String LAYOUT_ID_KEY = "com.agitive.usembassy.fragments.layoutFragments.VideosFragment.layotuIdKey";
	
	private static final double MINIATURE_WIDTH_TO_DISPLAY_WIDTH_PORTRAIT = 2.5;
	private static final double LAYOUT_WIDTH_TO_DISPLAY_WIDTH_PORTRAIT = 2.5;
	private static final double MINAITURES_ASPECT = 16.0 / 9.0;
	
	private View rootView;
	private VideoItem mostViewedVideo;
	private ArrayList<VideoItem> recentVideos;
	private HashMap<Integer, VideoItem> textViewIdVideoItem;
	private HashMap<Integer, VideoItem> titleViewIdVideoItem;
	private int id;
	private VideosLayout videoLayout;
	private VideosDownloaderAsyncTask videosDownloaderAsyncTask;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
		this.rootView = inflater.inflate(R.layout.videos_layout, container, false);
        
		removeServerErrorDialogFragment();
		
		initializeLocalVariables();
		setLayout();
        setMarginForEmblem();
        setLayoutName();
        setBackButton();
        
        setVideosPlaceholdersDimensions();
        setContent(savedInstanceState);
    	
        return this.rootView;
    }
	
	@Override
	public void onDetach() {
		super.onDetach();
		if (this.videosDownloaderAsyncTask != null) {
			this.videosDownloaderAsyncTask.cancel(true);
		}
	}
	
	public void notifyServerError() {
		showServerError();
		hideMainProgressBarLayout();
	}

	@Override
	public void changeLanguage() {
		showMainProgressBarLayout();
		
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				setLayoutName();
				changeRecentVideosLanguage();
				changeMostViewedVideoLanguage();
				changeTextLanguageInRecentVideos();
				
				setMostViewedVideo(mostViewedVideo);
				setRecentVideos(recentVideos);
				
				hideMainProgressBarLayout();
			}
			
		});
	}
	
	@Override
	public void setContent() {
		if (!isOnline()) {
			showNoInternetToastForVideosDownload();
			hideMainProgressBarLayout();
			return;
		}
		
		VideosDownloaderAsyncTask videoDownloaderAsyncTask = new VideosDownloaderAsyncTask(this);
		this.videosDownloaderAsyncTask = videoDownloaderAsyncTask;
		videoDownloaderAsyncTask.execute();
	}
	
	public LinearLayout getRecentVideos() {
		LinearLayout recentVideos = (LinearLayout) this.rootView.findViewById(R.id.videos_layout_recent_videos);
		if (recentVideos == null) {
			return null;
		}
		
		return recentVideos;
	}
	
	public View getRootView() {
		return this.rootView;
	}
	
	public void setMostViewedVideo(VideoItem item) {
		this.mostViewedVideo = item;
		
		if (this.mostViewedVideo == null) {
			return;
		}
		
		hideMostViewedVideoPlaceholderAndShowContent();
		setMiniatureForMostViewed(item);
		setTitleForMostViewed(item);
		setDateForMostVieved(item);
	}
	
	public void setRecentVideos(ArrayList<VideoItem> recentVideos) {		
		this.recentVideos = recentVideos;
		
		if (this.recentVideos == null) {
			return;
		}
		
		LinearLayout recentVideosLayout = (LinearLayout) this.rootView.findViewById(R.id.videos_layout_recent_videos);
		if (recentVideosLayout == null) {
			return;
		}
		recentVideosLayout.removeAllViews();
		
		int index = 0;
		for (VideoItem videoItem : recentVideos) {
			recentVideosLayout.addView(createVideoLayout(videoItem, index, recentVideos.size()));
			
			++index;
		}
		
		hideMainProgressBarLayout();
	}
	
	private void hideMostViewedVideoPlaceholderAndShowContent() {
		hideMostViewedVideoPlaceholder();
		showMostViewedMiniatureLayout();
		showMostViewedTitle();
		showMostViewedPublishedDate();
	}
	
	private void hideMostViewedVideoPlaceholder() {
		RelativeLayout mostViewedVideoPlaceholder = (RelativeLayout) this.rootView.findViewById(R.id.videos_layout_no_internet_most_viewed_video_placeholder);
		mostViewedVideoPlaceholder.setVisibility(View.GONE);
		
	}
	
	private void showMostViewedMiniatureLayout() {
		RelativeLayout mostViewedMiniatureLayout = (RelativeLayout) this.rootView.findViewById(R.id.videos_layout_most_viewed_miniature_layout);
		mostViewedMiniatureLayout.setVisibility(View.VISIBLE);
	}
	
	private void showMostViewedPublishedDate() {
		CustomTextView mostViewedPublishedDate = (CustomTextView) this.rootView.findViewById(R.id.videos_layout_most_viewed_published_date);
		mostViewedPublishedDate.setVisibility(View.VISIBLE);
	}
	
	private void showMostViewedTitle() {
		CustomTextView mostViewedTitle = (CustomTextView) this.rootView.findViewById(R.id.videos_layout_most_viewed_title);
		mostViewedTitle.setVisibility(View.VISIBLE);
	}
	
	private void setVideoPlaceholderDimensions(int id, int width, int height) {
		RelativeLayout videoPlaceholder = (RelativeLayout) this.rootView.findViewById(id);
		if (videoPlaceholder == null) {
			return;
		}
		
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) videoPlaceholder.getLayoutParams();
		if (params == null) {
			return;
		}
		
		params.width = width;
		params.height = height;
	}
	
	private void setVideosPlaceholdersDimensions() {
		setRecentVideosPlaceholdersDimensions();
		setMostViewedVideoPlaceholderDimensions();
	}
	
	private void setRecentVideosPlaceholdersDimensions() {
		int width = getMiniatureWidthForRecentVideo();
		int height = (int) (width / VideosFragment.MINAITURES_ASPECT);
		
		setVideoPlaceholderDimensions(R.id.videos_layout_no_internet_recent_video_placeholder_0, width, height);
		setVideoPlaceholderDimensions(R.id.videos_layout_no_internet_recent_video_placeholder_1, width, height);
		setVideoPlaceholderDimensions(R.id.videos_layout_no_internet_recent_video_placeholder_2, width, height);
	}
	
	private void setMostViewedVideoPlaceholderDimensions() {
		int width = getMiniatureWidthWithoutMargins();
	    int height = (int) (width / VideosFragment.MINAITURES_ASPECT);
	    
	    setVideoPlaceholderDimensions(R.id.videos_layout_no_internet_most_viewed_video_placeholder, width, height);
	}
	
	private void hideMainProgressBarLayout() {
		RelativeLayout mainProgressBarLayout = (RelativeLayout) this.rootView.findViewById(R.id.videos_layout_main_progress_bar_layout);
		if (mainProgressBarLayout == null) {
			return;
		}
		
		mainProgressBarLayout.setVisibility(RelativeLayout.INVISIBLE);
	}
	
	private void showMainProgressBarLayout() {
		RelativeLayout mainProgressBarLayout = (RelativeLayout) this.rootView.findViewById(R.id.videos_layout_main_progress_bar_layout);
		if (mainProgressBarLayout == null) {
			return;
		}
		
		mainProgressBarLayout.setVisibility(RelativeLayout.VISIBLE);
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
	
	private void removeServerErrorDialogFragment() {
		FragmentManager fragmentManager = getChildFragmentManager();
		DialogFragment serverErrorDialogFragment = (DialogFragment) fragmentManager.findFragmentByTag(ServerErrorDialogFragment.TAG);
		if (serverErrorDialogFragment != null) {
			try {
				fragmentManager.beginTransaction().remove(serverErrorDialogFragment).commit();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Bundle createArgumentsForServerErrorDialogFragment() {
		Bundle arguments = new Bundle();
		arguments.putString(ServerErrorDialogFragment.MESSAGE_KEY, getResources().getString(R.string.videos_fragment_server_error_message));
		
		return arguments;
	}
	
	private RelativeLayout createVideoLayout(VideoItem videoItem, int itemNumber, int itemCount) {
		RelativeLayout videoLayout = new RelativeLayout(getActivity());
		int width = getLayoutWidthForRecentVideo();
		setVideoLayoutParams(videoLayout, itemNumber, itemCount, width);
		
		RelativeLayout miniatureLayout = createMiniatureLayout(videoItem);
		videoLayout.addView(miniatureLayout);
		
		CustomTextView titleText = createTitleForRecentVideo(videoItem, miniatureLayout.getId());
		videoLayout.addView(titleText);
		
		CustomTextView publishedDate = createDateForRecentVideo(videoItem, titleText.getId());
		videoLayout.addView(publishedDate);
		
		return videoLayout;
	}
	
	private void setLayoutParamsForDateForRecentVideo(CustomTextView publishedDate, int titleTextId) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, titleTextId);
		params.topMargin = (int) getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
		publishedDate.setLayoutParams(params);
	}
	
	private CustomTextView createDateForRecentVideo(VideoItem videoItem, int titleTextId) {
		CustomTextView publishedDate = new CustomTextView(getActivity());
		++id;
		publishedDate.setId(id);
		
		setLayoutParamsForDateForRecentVideo(publishedDate, titleTextId);
		publishedDate.setText(createFormat(videoItem));
		publishedDate.setTextColor(getResources().getColor(R.color.video_date));
		publishedDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getApplicationContext().getResources().getDimension(R.dimen.text_small));
		
		return publishedDate; 
	}
	
	private void setLayoutParamsForTitle(CustomTextView titleText, int miniatureLayoutId) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, miniatureLayoutId);
		params.topMargin = (int) getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_medium);
		titleText.setLayoutParams(params);
	}
	
	private CustomTextView createTitleForRecentVideo(VideoItem videoItem, int miniatureLayoutId) {
		CustomTextView titleText = new CustomTextView(getActivity());
		++this.id;
		titleText.setId(this.id);
		
		setLayoutParamsForTitle(titleText, miniatureLayoutId);
		String title = getTitle(videoItem);
		titleText.setText(title);
		titleText.setTextColor(getResources().getColor(R.color.video_title));
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getApplicationContext().getResources().getDimension(R.dimen.text_normal));
		
		return titleText;
	}
	
	private ImageView createPlayIcon() {
		ImageView playIcon = new ImageView(getActivity());
		RelativeLayout.LayoutParams playParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		playParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		playIcon.setLayoutParams(playParams);
		
		playIcon.setImageResource(R.drawable.play);
		
		return playIcon;
	}
	
	private ImageView createMiniatureDimmer() {
		ImageView miniatureDimmer = new ImageView(getActivity());
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		miniatureDimmer.setLayoutParams(params);
		
		miniatureDimmer.setImageResource(R.drawable.video_miniature_dimmer);
		
		int width = getMiniatureWidthForRecentVideo();
		int height = (int) (width / VideosFragment.MINAITURES_ASPECT);
		setLayoutParamsForRecentVideoMiniatureDimmer(miniatureDimmer, width, height);
		
		return miniatureDimmer;
	}
	
	private RelativeLayout createMiniatureLayout(VideoItem videoItem) {
		RelativeLayout miniatureLayout = new RelativeLayout(getActivity());
		++id;
		miniatureLayout.setId(id);
		
		setOnClickListenerForMostViewed(miniatureLayout, videoItem);
		
		ImageView miniature = createMiniature(videoItem);
		miniatureLayout.addView(miniature);
		
		ImageView miniatureDimmer = createMiniatureDimmer();
		miniatureLayout.addView(miniatureDimmer);
		
		ImageView playIcon = createPlayIcon();
		miniatureLayout.addView(playIcon);
		
		return miniatureLayout;
	}
	
	private int getMiniatureWidthForRecentVideoPortrait() {
		return ((int) (getDisplayWidth() / VideosFragment.MINIATURE_WIDTH_TO_DISPLAY_WIDTH_PORTRAIT));
	}
	
	private int getMiniatureWidthForRecentVideoLandscape() {
		return ((int) (getDisplayWidth() / VideosFragment.MINIATURE_WIDTH_TO_DISPLAY_WIDTH_PORTRAIT * Global.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH));
	}
	
	private int getMiniatureWidthForRecentVideo() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return getMiniatureWidthForRecentVideoPortrait();
	    } else {
	    	return getMiniatureWidthForRecentVideoLandscape();
	    }
	}
	
	private void setLayout() {
		DatabaseReader databaseReader = new DatabaseReader(getActivity());
		int id = getArguments().getInt(VideosFragment.LAYOUT_ID_KEY);
		this.videoLayout = (VideosLayout) databaseReader.getLayout(id);
	}
	
	private void setLayoutParamsForRecentVideoMiniature(ImageView videoMiniature, int width, int height) {
		RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(width, height);
		videoMiniature.setLayoutParams(relativeLayoutParams);
	}
	
	private void setLayoutParamsForRecentVideoMiniatureDimmer(ImageView miniatureDimmer, int width, int height) {
		RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(width, height);
	    miniatureDimmer.setLayoutParams(relativeLayoutParams);
	}
	
	private ImageView createMiniature(VideoItem videoItem) {
		ImageView videoMiniature = new ImageView(getActivity());
		
		int width = getMiniatureWidthForRecentVideo();
		int height = (int) (width / VideosFragment.MINAITURES_ASPECT);
		setLayoutParamsForRecentVideoMiniature(videoMiniature, width, height);
		Picasso.with(getActivity()).load(videoItem.getMiniatureUrl()).resize(width, height).placeholder(R.drawable.video_miniature_placeholder).into(videoMiniature);
		
		return videoMiniature;
	}
	
	public void setContent(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		}
		
		setContent();
	}
	
	private void setVideoLayoutParams(RelativeLayout videoLayout, int itemNumber, int itemCount, int width) {
		LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
		if (itemNumber == 0) {
			linearLayoutParams.leftMargin = (int) getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_xlarge);
		} else if (itemNumber == itemCount - 1) {
			linearLayoutParams.leftMargin = (int) getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_small);
			linearLayoutParams.rightMargin = (int) getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_xlarge);
		} else {
			linearLayoutParams.leftMargin = (int) getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_small);
		}
		
		videoLayout.setLayoutParams(linearLayoutParams);	
	}
	
	private int getLayoutWidthForRecentVideo() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
	    	return (int) (getDisplayWidth() / VideosFragment.LAYOUT_WIDTH_TO_DISPLAY_WIDTH_PORTRAIT);
	    } else {
	    	return (int) (getDisplayWidth() / VideosFragment.LAYOUT_WIDTH_TO_DISPLAY_WIDTH_PORTRAIT * Global.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH);
	    }
	}
	
	private String createFormat(VideoItem videoItem) {
		GregorianCalendar date = new GregorianCalendar(videoItem.getYear(), videoItem.getMonth() - 1, videoItem.getDay());
		SimpleDateFormat simpleDate;
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			simpleDate = new SimpleDateFormat("LLLL dd, yyyy", Locale.US);
		} else {
			simpleDate = new SimpleDateFormat("dd.MM.yyyy");
		}
		
		return simpleDate.format(date.getTime());
	}
	
	private void setDateForMostVieved(VideoItem item) {
		CustomTextView publishedDate = (CustomTextView) this.rootView.findViewById(R.id.videos_layout_most_viewed_published_date);
		if (publishedDate == null) {
			return;
		}
		
		publishedDate.setText(createFormat(item));
	}
	
	private String getTitle(VideoItem item) {
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			return item.getTitleEn();
		} else {
			return item.getTitlePl();
		}
	}
	
	private void setTitleForMostViewed(VideoItem item) {
		CustomTextView titleText = (CustomTextView) this.rootView.findViewById(R.id.videos_layout_most_viewed_title);
		if (titleText == null) {
			return;
		}
		
		String title = getTitle(item);
		titleText.setText(title);
		titleText.setTextColor(getResources().getColor(R.color.video_title));
	}
	
	private int getMiniatureWidthWithoutMarginsPortrait() {
		return (int) (getDisplayWidth() - getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_xlarge) * 2);
	}
	
	private int getMiniatureWidthWithoutMarginsLandscape() {
		return (int) (getDisplayWidth() * Global.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH - getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_xlarge) * 2);
	}
	
	private int getMiniatureWidthWithoutMargins() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return getMiniatureWidthWithoutMarginsPortrait();
		} else {
			return getMiniatureWidthWithoutMarginsLandscape();
		}
	}
	
	private void setMiniatureForMostViewed(VideoItem item) {
		ImageView mostViewedMiniature = (ImageView) this.rootView.findViewById(R.id.videos_layout_most_viewed_miniature);
		if (mostViewedMiniature == null) {
			return;
		}
		
		ImageView mostViewedMiniatureDimmer = (ImageView) this.rootView.findViewById(R.id.videos_layout_most_viewed_miniature_dimmer);
		if (mostViewedMiniatureDimmer == null) {
			return;
		}
		
		RelativeLayout mostViewedMiniatureLayout = (RelativeLayout) this.rootView.findViewById(R.id.videos_layout_most_viewed_miniature_layout);
		if (mostViewedMiniatureLayout == null) {
			return;
		}
		
		int width = getMiniatureWidthWithoutMargins();
	    int height = (int) (width / VideosFragment.MINAITURES_ASPECT);
	    setLayoutParamsForMiniature(mostViewedMiniature, width, height);
	    setLayoutParamsForMiniatureDimmer(mostViewedMiniatureDimmer, width, height);
	    setOnClickListenerForMostViewed(mostViewedMiniatureLayout, item);
		Picasso.with(getActivity()).load(item.getMiniatureUrl()).resize(width, height).placeholder(R.drawable.video_miniature_placeholder).into(mostViewedMiniature);
	}
	
	private void setLayoutParamsForMiniatureDimmer(ImageView miniatureDimmer, int width, int height) {
		RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) miniatureDimmer.getLayoutParams();
	    relativeLayoutParams.width = width;
	    relativeLayoutParams.height = height;
	    miniatureDimmer.setLayoutParams(relativeLayoutParams);
	}
	
	private void playYoutubeVideo(VideoItem item) {
		Intent youTubePlayerActivityIntent = new Intent(getActivity(), YouTubePlayerActivity.class);
		String videoId = getYouTubeVideoId(item.getUrls()[0]);
		youTubePlayerActivityIntent.putExtra(YouTubePlayerActivity.videoIdName, videoId);
		startActivity(youTubePlayerActivityIntent);
	}
	
	private void playLocalVideo(VideoItem item) {
		Intent videoPlayerActivityIntent = new Intent(getActivity(), VideoPlayerActivity.class);
		videoPlayerActivityIntent.putExtra(VideoPlayerActivity.VIDEO_URL_0, item.getUrls()[0]);
		videoPlayerActivityIntent.putExtra(VideoPlayerActivity.VIDEO_URL_1, item.getUrls()[1]);
		videoPlayerActivityIntent.putExtra(VideoPlayerActivity.VIDEO_URL_2, item.getUrls()[2]);
		startActivity(videoPlayerActivityIntent);
	}
	
	private boolean isYouTubeVideo(VideoItem videoItem) {
		return !videoItem.getIsLocalSource();
	}
	
	private String getYouTubeVideoId(String url) {
		return Uri.parse(url).getQueryParameter("v");
	}
	
	private void setOnClickListenerForMostViewed(RelativeLayout miniatureLayout, final VideoItem item) {
		miniatureLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!isOnline()){
					showNoInternetToastForVideoPlay();
					return;
				}
				
				if (isYouTubeVideo(item)) {
					playYoutubeVideo(item);
				} else {
					playLocalVideo(item);
				}
				
				VideoIdSenderAsyncTask videoIdSenderAsyncTask = new VideoIdSenderAsyncTask();
				videoIdSenderAsyncTask.execute(item.getId());
			}
		});
	}
	
	private boolean isOnline() {
	    ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	 
	    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
	    return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
	}
	
	private void showNoInternetToastForVideoPlay() {
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.videos_fragment_no_internet_for_video_play, Toast.LENGTH_LONG);
        toast.show();
    }
	
	private void showNoInternetToastForVideosDownload() {
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.videos_fragment_no_internet_for_videos_download, Toast.LENGTH_LONG);
        toast.show();
    }
	
	private void setLayoutParamsForMiniature(ImageView miniature, int width, int height) {
		RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) miniature.getLayoutParams();
	    relativeLayoutParams.width = width;
	    relativeLayoutParams.height = height;
	    miniature.setLayoutParams(relativeLayoutParams);
	}
	
	private String getAppLanguage() {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		return sharedPreferences.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
	}
	
	@SuppressLint("UseSparseArrays")
	private void initializeLocalVariables() {
		this.textViewIdVideoItem = new HashMap<Integer, VideoItem>();
        this.titleViewIdVideoItem = new HashMap<Integer, VideoItem>();
        this.recentVideos = null;
		this.mostViewedVideo = null;
	}
	
	private void changeTextLanguageInRecentVideos() {
		if (this.textViewIdVideoItem == null) {
			return;
		}
		
		for (Map.Entry<Integer, VideoItem> entry: this.textViewIdVideoItem.entrySet()) {
			GregorianCalendar date = new GregorianCalendar(entry.getValue().getYear(), entry.getValue().getMonth() - 1, entry.getValue().getDay());
			SimpleDateFormat simpleDate;
			if (getAppLanguage().equals("EN")) {
				simpleDate = new SimpleDateFormat("LLLL dd, yyyy", Locale.US);
			} else {
				simpleDate = new SimpleDateFormat("dd.MM.yyyy");
			}
			
			CustomTextView publishedDate = (CustomTextView) this.rootView.findViewById(entry.getKey());
			if (publishedDate == null) {
				return;
			}
			
			publishedDate.setText(simpleDate.format(date.getTime()));
		}
		
		for (Map.Entry<Integer, VideoItem> entry: this.titleViewIdVideoItem.entrySet()) {
			String title;
			if (getAppLanguage().equals("EN")) {
				title = entry.getValue().getTitleEn();
			} else {
				title = entry.getValue().getTitlePl();
			}
			
			CustomTextView titleText = (CustomTextView) this.rootView.findViewById(entry.getKey());
			if (titleText == null) {
				return;
			}
			
			titleText.setText(title);
		}
	}
	
	private void changeRecentVideosLanguage() {
		CustomTextView recentVideosText = (CustomTextView) rootView.findViewById(R.id.videos_layout_recent_videos_text);
		if (recentVideosText == null) {
			return;
		}
		
		recentVideosText.setText(R.string.recent_videos);
	} 
	
	private void changeMostViewedVideoLanguage() {
		CustomTextView mostViewedText = (CustomTextView) rootView.findViewById(R.id.videos_layout_most_viewed_text);
		if (mostViewedText == null) {
			return;
		}
		
		mostViewedText.setText(R.string.most_viewed);
	}
	
	private void setMarginForEmblem() {
		CustomTextView menuName = (CustomTextView) this.rootView.findViewById(R.id.videos_layout_layout_name);
		if (menuName == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) menuName.getLayoutParams();
		params.topMargin = ((MainActivity)getActivity()).getEmblemPartOutsideSize();
		params.topMargin += getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_small);
		menuName.setLayoutParams(params);
	}
	
	private void setLayoutName() {
		CustomTextView layoutName = (CustomTextView) this.rootView.findViewById(R.id.videos_layout_layout_name);
		if (layoutName == null) {
			return;
		}
		
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			layoutName.setText(this.videoLayout.getTitleEn());
		} else {
			layoutName.setText(this.videoLayout.getTitlePl());
		}
	}
	
	private void setBackButton() {
		ImageView backButton = (ImageView) this.rootView.findViewById(R.id.videos_layout_back_button);
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
	
	private int getOrientation() {
		return getResources().getConfiguration().orientation;
	}
}
