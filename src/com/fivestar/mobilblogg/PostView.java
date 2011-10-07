/**
 * 
 */
package com.fivestar.mobilblogg;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.fivestar.mobilblogg.widgets.AspectRatioImageView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PostView extends Activity {
	final String TAG = "PostView";
	ProgressDialog dialog;
	Thread myBloggThread;
	String userName = null;
	String imgid = "";
	int nbrComments;
	int page = 1;
	int selectedIndex = 0;
	MobilbloggApp app;
	int listNum;
	List<PostInfo> postList = null;
	List<CommentInfo> commentList = null;
	PostInfo pi = null;
	Gallery gallery;
	TextView headline;
	TextView text;
	TextView date;
	TextView user;
	ImageView avatarImg;
	EditText comment;
	Button commentButton;
	LinearLayout commentHolder;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.post);

		listNum  = getIntent().getIntExtra("list",-1);
		userName = getIntent().getStringExtra("username");

		headline = (TextView) findViewById(R.id.headline);
		text     = (TextView) findViewById(R.id.text);
		date     = (TextView) findViewById(R.id.date);
		user     = (TextView) findViewById(R.id.username);
		comment  = (EditText) findViewById(R.id.commentText);
		commentHolder = (LinearLayout) findViewById(R.id.CommentHolder);
		avatarImg = (ImageView) findViewById(R.id.avatar);

		final AspectRatioImageView imgView = (AspectRatioImageView)findViewById(R.id.ImageView01);

		dialog = new ProgressDialog(PostView.this);
		dialog.setMessage(getString(R.string.please_wait));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

		app = (MobilbloggApp)getApplicationContext();

		selectedIndex = getIntent().getIntExtra("idx",0);

		Utils.log(TAG,"postview getList");
		postList = app.bc.getList(listNum, userName);

		this.setTitle(postList.get(selectedIndex).user);

		dialog.show();

		pi = postList.get(selectedIndex);
		imgid = pi.imgid;
		if(pi.img != null && pi.img.length() > 0) {
			Drawable cachedImage = app.asyncImageLoader.loadDrawable(pi.img, new ImageCallback() {
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					imgView.setImageDrawable(imageDrawable);
					Utils.log(TAG,"image loaded");
				}
			});
			imgView.setImageDrawable(cachedImage);
		}

		if(pi.avatar != null && pi.avatar.length() > 0) {
			Drawable cachedImage = app.asyncImageLoader.loadDrawable(pi.avatar, new ImageCallback() {
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					avatarImg.setImageDrawable(imageDrawable);
					final int w = (int)(36 * app.getResources().getDisplayMetrics().density + 0.5f);
					avatarImg.setLayoutParams(new LinearLayout.LayoutParams(w, w));
				}
			});
			avatarImg.setImageDrawable(cachedImage);
			final int w = (int)(36 * app.getResources().getDisplayMetrics().density + 0.5f);
			avatarImg.setLayoutParams(new LinearLayout.LayoutParams(w, w));
		} else {
			avatarImg.setImageResource(R.drawable.stub);
		}

		headline.setText(pi.headline);
		text.setText(Html.fromHtml(pi.text));
		date.setText(Utils.PrettyDate(pi.createdate, this));
		user.setText(userName);

		// Load comments
		loadComments();
	}

	private Handler uiCallback = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what >= 0) {
				commentList = app.bc.getComments(imgid);
				if(commentList != null) {
					Utils.log(TAG, "Num com: "+commentList.size());
					commentHolder.removeAllViews();
					for(int i=0; i<commentList.size(); i++) {
						TextView com = new TextView(app);
						if(i % 2 == 0) {
							com.setBackgroundResource(R.color.colorbg);
						} else {
							com.setBackgroundResource(R.color.colorwhite);
						}
						com.setTextColor(R.color.textcolor);
						com.setPadding(5, 8, 5, 8);
						com.setText(commentList.get(i).username + ":\n" + Html.fromHtml(commentList.get(i).comment));
						com.setId(i);
						commentHolder.addView(com);
					}
				}
				if(dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		}
	};

	private void loadComments() {
		Thread mThread = new Thread() {
			public void run() {
				String jsonresponse = null;
				JSONArray json = null;
				try {
					Utils.log(TAG, "imgid: "+imgid);
					jsonresponse = app.com.getComments(imgid);
				} catch (CommunicatorException c) {
					Utils.log(TAG, "No network?");
					uiCallback.sendEmptyMessage(-1);
				}
				try {
					json = new JSONArray(jsonresponse);
					Utils.log(TAG, "json resp: "+jsonresponse);
					int len = json.length();
					for(int i=0; i<len;i++) {
						CommentInfo ci = new CommentInfo();

						ci.username   = json.getJSONObject(i).get("author").toString();
						ci.comment    = json.getJSONObject(i).get("comment").toString();
						ci.createdate = Utils.PrettyDate(json.getJSONObject(i).get("createdate").toString(),app);
						ci.isMember	  = Integer.parseInt(json.getJSONObject(i).get("member").toString());
						ci.avatar 	  = json.getJSONObject(i).get("avatar").toString();
						app.bc.addComment(imgid, ci);
					}
					uiCallback.sendEmptyMessage(0);					
				} catch (JSONException j) {
					Utils.log(TAG, "No comments?");
					uiCallback.sendEmptyMessage(-1);
				}
			}
		};
		mThread.start();
	}

	public void PostViewClickHandler(View view) {
		switch(view.getId()) {
		case (R.id.avatar):
		case (R.id.username):
			Utils.log(TAG, "Goto blog!");
		Intent bIntent = new Intent(view.getContext(), GalleryView.class);
		bIntent.putExtra("username", userName);
		bIntent.putExtra("list", app.bc.BLOGGPAGE);
		startActivityForResult(bIntent,0);
		break;
		case (R.id.commentButton):
			Utils.log(TAG, "Kommentar: "+comment.getText().toString());
		break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}	
}