package com.agitive.usembassy.objects;

import java.util.ArrayList;

public class MostViewedVideosAndRecentVideosPair {
	private VideoItem mostViewed;
	private ArrayList<VideoItem> recentVideos;
	
	public MostViewedVideosAndRecentVideosPair(VideoItem mostViewed, ArrayList<VideoItem> recentVideos) {
		this.mostViewed = mostViewed;
		this.recentVideos = recentVideos;
	}
	
	public VideoItem getMostViewedVideo() {
		return this.mostViewed;
	}
	
	public ArrayList<VideoItem> getRecentVideos() {
		return this.recentVideos;
	}
}
