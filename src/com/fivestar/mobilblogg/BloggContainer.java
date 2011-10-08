package com.fivestar.mobilblogg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BloggContainer {
	final String TAG = "BloggContainer";

	public final int BLOGGPAGE  = 0;
	public final int FRIENDPAGE = 1;
	public final int FIRSTPAGE  = 2;

	public List<PostInfo> firstPageList = null;
	public List<PostInfo> friendPageList = null;

	public Map<String, List<PostInfo>> userBloggs;
	public Map<String, Integer> userPage;
	public Map<String, List<CommentInfo>> imgidComment;

	public int page[] = {1, 1, 1};


	public BloggContainer() {
		firstPageList = new ArrayList<PostInfo>();
		friendPageList = new ArrayList<PostInfo>();
		userBloggs = new HashMap<String, List<PostInfo>>();
		userPage = new HashMap<String, Integer>();
		imgidComment = new HashMap<String, List<CommentInfo>>();
	}

	public void add(int listNum, PostInfo pi, String username) {
		switch (listNum) {
		case FIRSTPAGE: 
			firstPageList.add(pi);
			break;
		case FRIENDPAGE: 
			friendPageList.add(pi);
			break;
		case BLOGGPAGE:
			if(username == null) {
				return;
			}
			if(userBloggs.containsKey(username)) {
				List<PostInfo> list = userBloggs.get(username);
				list.add(pi);
			} else {
				List<PostInfo> list = new ArrayList<PostInfo>();
				list.add(pi);
				userBloggs.put(username, list);
			}
			break;
		}
	}

	public PostInfo get(int listNum, int index, String username) {
		PostInfo pi = null;
		switch (listNum) {
		case FIRSTPAGE: 
			pi = firstPageList.get(index);
			break;
		case FRIENDPAGE: 
			pi = friendPageList.get(index);
			break;
		case BLOGGPAGE: 
			if(username == null) {
				return null;
			}
			if(userBloggs.containsKey(username)) {
				List<PostInfo> list = userBloggs.get(username);
				pi = list.get(index);
			}
			break;
		}
		return pi;
	}

	public List<PostInfo> getList(int listNum, String username) {
		switch (listNum) {
		case FIRSTPAGE: 
			return firstPageList;
		case FRIENDPAGE: 
			return friendPageList;
		case BLOGGPAGE: 
			if(username == null) {
				return null;
			}
			if(userBloggs.containsKey(username)) {
				return userBloggs.get(username);
			}
		}
		return null;
	}


	public int size(int listNum, String username) {
		int s = 0;
		switch (listNum) {
		case FIRSTPAGE: 
			s = firstPageList.size();
			break;
		case FRIENDPAGE: 
			s = friendPageList.size();
			break;
		case BLOGGPAGE: 
			if(username == null) {
				return 0;
			}
			if(userBloggs.containsKey(username)) {
				List<PostInfo> list = userBloggs.get(username);
				s = list.size();
			}
			break;
		}
		return s;
	}

	public void increasePage(int listNum, String username) {
		if(listNum == BLOGGPAGE) {
			if(username == null) {
				return;
			}
			if(userPage.containsKey(username)) {
				int page = userPage.get(username) + 1;
				userPage.remove(username);
				userPage.put(username, page);
			} else {
				userPage.put(username, 1);
			}

		}
		page[listNum]++;
	}

	public void addComment(String imgId, CommentInfo ci) {
		if(imgidComment.containsKey(imgId)) {
			List<CommentInfo> list = imgidComment.get(imgId);
			list.add(ci);
		} else {
			List<CommentInfo> list = new ArrayList<CommentInfo>();
			list.add(ci);
			imgidComment.put(imgId, list);
		}
	}

	public List<CommentInfo> getComments(String imgId) {
		if(imgidComment.containsKey(imgId)) {
			return imgidComment.get(imgId);
		}
		return null;
	}


	public int getPage(int listNum, String username) {
		if(listNum == BLOGGPAGE) {
			if(username == null) {
				return 1;
			}
			if(userPage.containsKey(username)) {
				return userPage.get(username);
			} else {
				userPage.put(username, 1);
				return 1;
			}
		}
		return page[listNum];
	}

	public void addComments() {


	}
}