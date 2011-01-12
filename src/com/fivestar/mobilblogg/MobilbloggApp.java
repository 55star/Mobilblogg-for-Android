package com.fivestar.mobilblogg;

import android.app.Application;

public class MobilbloggApp extends Application {
	private String userName = "";
	private boolean loggedInStatus = false;
	private String latestImg;
	public Communicator com;
	public ImageLoader imgLoader;
	
	
	public String getUserName(){
		if(userName != null && loggedInStatus) {
			return userName;
		}
		return "";
	}
	
	public void setUserName(String s){
		userName = s;
	}
	
	public void setLatestImageFileName(String s) {
		latestImg = s;
	}
	
	public String getLatestImageFileName() {
		if(latestImg != null) {
			return latestImg;
		}
		return "";
	}
	
	public boolean getLoggedInStatus() {
		return loggedInStatus;
	}
	public void setLoggedInStatus(boolean l) {
		loggedInStatus = l;
	}
	
	public void startServices() {
		startImageLoader();
		startHttpEngine();
	}
	
	private void startImageLoader() {
		imgLoader = new ImageLoader(this);
		//clear cache on startup
		imgLoader.clearCache();
	}
	
	private void startHttpEngine() {
		com = new Communicator();
	}
}