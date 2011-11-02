package com.fivestar.mobilblogg;

import android.app.Application;

public class MobilbloggApp extends Application {
	private String userName = "";
	private boolean loggedInStatus = false;
	private String latestImg;
	public Communicator com;
	public AsyncImageLoader asyncImageLoader;
	public String filePath = null;
	public BloggContainer bc = null;
	public String uploadJson = null;
	
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
		initBloggContainer();
	}
	
	private void initBloggContainer() {
		bc = new BloggContainer();
	}
	
	private void startImageLoader() {
		asyncImageLoader = new AsyncImageLoader();
	}
	
	private void startHttpEngine() {
		com = new Communicator();
	}
}