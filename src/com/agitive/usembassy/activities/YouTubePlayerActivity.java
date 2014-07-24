package com.agitive.usembassy.activities;

import com.agitive.usembassy.R;
import com.agitive.usembassy.privateKeys.AgitivePrivateKeys;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import android.os.Bundle;

public class YouTubePlayerActivity extends YouTubeBaseActivity {

	public final static String videoIdName = "videoId";
	
	private final String API_KEY = AgitivePrivateKeys.YOUTUBE_API_KEY;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.you_tube_player_layout);
		
		YouTubePlayerView youTubePlayer = (YouTubePlayerView) findViewById(R.id.you_tube_player_layout_you_tube_player);
		if (youTubePlayer == null) {
			return;
		}
		
		final String videoId = getIntent().getStringExtra(YouTubePlayerActivity.videoIdName);
    	
		youTubePlayer.initialize(this.API_KEY, new OnInitializedListener() {

			@Override
			public void onInitializationFailure(Provider arg0,
					YouTubeInitializationResult arg1) {	
			}

			@Override
			public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
				if (!wasRestored) {
					player.setShowFullscreenButton(false);
				    player.cueVideo(videoId);
				}
			}
    		
    	});
	}
}
