package com.agitive.usembassy.activities;

import com.agitive.usembassy.R;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayerActivity extends Activity {

	public static final String VIDEO_URL_0 = "videoUrl0";
	public static final String VIDEO_URL_1 = "videoUrl1";
	public static final String VIDEO_URL_2 = "videoUrl2";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_player_layout);
		
		MediaController mediaController = new MediaController(this);
		
		final VideoView videoPlayer = (VideoView) findViewById(R.id.video_player_layout_video_player);
		if (videoPlayer == null) {
			return;
		}
		
		videoPlayer.setMediaController(mediaController);
		
		String videoPriority0Url = getIntent().getStringExtra(VideoPlayerActivity.VIDEO_URL_0);
		final String videoPriority1Url = getIntent().getStringExtra(VideoPlayerActivity.VIDEO_URL_1);
		final String videoPriority2Url = getIntent().getStringExtra(VideoPlayerActivity.VIDEO_URL_2);
		
		videoPlayer.setVideoURI(Uri.parse(videoPriority0Url));
		videoPlayer.setOnErrorListener(new OnErrorListener() {
			int videoPriority = 0;
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				switch (videoPriority) {
					case 0: 
						videoPriority = 1;
						videoPlayer.setVideoURI(Uri.parse(videoPriority1Url));
						videoPlayer.start();
						
						return true;
					case 1:
						videoPriority = 2;
						videoPlayer.setVideoURI(Uri.parse(videoPriority2Url));
						videoPlayer.start();
						
						return true;
					default:
						return false;
				}
			}
		});
		videoPlayer.start();
	}
}
