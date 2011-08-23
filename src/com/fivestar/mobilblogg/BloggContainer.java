package com.fivestar.mobilblogg;

import java.util.ArrayList;
import java.util.List;

public class BloggContainer {

	public final int BLOGGPAGE  = 0;
	public final int FRIENDPAGE = 1;
	public final int FIRSTPAGE  = 2;

	public List<PostInfo> firstPageList = null;
	public List<PostInfo> friendPageList = null;
	public List<PostInfo> bloggList = null;
	
	public int page[] = {1, 1, 1};


	public BloggContainer() {
		firstPageList = new ArrayList<PostInfo>();
		friendPageList = new ArrayList<PostInfo>();
		bloggList = new ArrayList<PostInfo>();
	}

	public void add(int listNum, PostInfo pi) {
		switch (listNum) {
		case FIRSTPAGE: 
			firstPageList.add(pi);
			break;
		case FRIENDPAGE: 
			friendPageList.add(pi);
			break;
		case BLOGGPAGE: 
			bloggList.add(pi);
			break;
		}
	}

	public PostInfo get(int listNum, int index) {
		PostInfo pi = null;
		switch (listNum) {
		case FIRSTPAGE: 
			pi = firstPageList.get(index);
			break;
		case FRIENDPAGE: 
			pi = friendPageList.get(index);
			break;
		case BLOGGPAGE: 
			pi = bloggList.get(index);
			break;
		}
		return pi;
	}

	public List<PostInfo> getList(int listNum) {
		PostInfo pi = null;
		switch (listNum) {
		case FIRSTPAGE: 
			return firstPageList;
		case FRIENDPAGE: 
			return friendPageList;
		case BLOGGPAGE: 
			return bloggList;
		}
		return null;
	}

	
	public int size(int listNum) {
		int s = 0;
		switch (listNum) {
		case FIRSTPAGE: 
			s = firstPageList.size();
			break;
		case FRIENDPAGE: 
			s = friendPageList.size();
			break;
		case BLOGGPAGE: 
			s = bloggList.size();
			break;
		}
		return s;
	}
	
	public void increasePage(int listNum) {
		page[listNum]++;
	}
	
	public int getPage(int listNum) {
		return page[listNum];
	}
}