package com.fivestar.mobilblogg;

import android.app.Application;

public class MobilbloggApp extends Application{
	private String userName;
	private boolean loggedInStatus = false;
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
	}
	
	private void startHttpEngine() {
		com = new Communicator();
	}
}