package com.fivestar.mobilblogg;


public class PostInfo {
	public String[] thumb;
	public String[] img;
	public String[] headline;
	public String[] text;
	public int[] thumbX;
	public int[] thumbY;
	public int[] imgX;
	public int[] imgY;
	public String[] user;
	public String[] createdate;
	public String[] imgid;
	public int[] numComment;
	public int length;

	public PostInfo(int len) {
		thumb      = new String[len];
		thumbX     = new int[len];
		thumbY     = new int[len];
		img        = new String[len];
		imgX       = new int[len];
		imgY       = new int[len];
		headline   = new String[len];
		text       = new String[len];
		createdate = new String[len];
		user       = new String[len];
		imgid      = new String[len];
		numComment = new int[len];
		length     = len;
	}
}