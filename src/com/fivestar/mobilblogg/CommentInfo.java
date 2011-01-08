package com.fivestar.mobilblogg;


public class CommentInfo {
	public String[] comment;
	public String[] username;
	public String[] createdate;
	public String[] avatar;
	public int length;

	public CommentInfo(int len) {
		comment     = new String[len];
		username    = new String[len];
		createdate  = new String[len];
		avatar     	= new String[len];
		length      = len;
	}
}