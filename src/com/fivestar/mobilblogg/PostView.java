/**
 * 
 */
package com.fivestar.mobilblogg;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.fivestar.mobilblogg.widgets.AspectRatioImageView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
	ScrollView scroll;
	Activity activity;

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
		scroll   = (ScrollView) findViewById(R.id.scroll01);

		final AspectRatioImageView imgView = (AspectRatioImageView)findViewById(R.id.ImageView01);

		dialog = new ProgressDialog(PostView.this);
		dialog.setMessage(getString(R.string.loading));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

		app = (MobilbloggApp)getApplicationContext();
		activity = this;

		selectedIndex = getIntent().getIntExtra("idx",0);

		Utils.log(TAG,"postview getList");
		postList = app.bc.getList(listNum, userName);
		pi = postList.get(selectedIndex);

		this.setTitle(pi.user);
		userName = pi.user;

		dialog.show();

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
			if (msg.what == 0) {	
				List<CommentInfo> commentList = app.bc.getComments(imgid);
				if(commentList != null) {
					for(int i=0; i<commentList.size(); i++) {
						Boolean newView = false;
						TextView com = (TextView)commentHolder.getChildAt(i);
						if(com == null) {
							com = new TextView(app);
							newView = true;
						}
						CommentInfo ci = commentList.get(i);
						if(i % 2 == 0) {
							com.setBackgroundResource(R.color.colorverylightgray);
						} else {
							com.setBackgroundResource(R.color.colorwhite);
						}
						com.setTextColor(R.color.textcolor);
						com.setPadding(5, 8, 5, 8);
						com.setText(Html.fromHtml("<b>"+ci.username+"</b> @ <i>" + ci.createdate + "</i><br />" + ci.comment));
						com.setId(i);
						if(newView) {
							commentHolder.addView(com);
						}
					}
				}
				if(dialog.isShowing()) {
					dialog.dismiss();
				}
			} else {
				AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
				alertbox.setMessage(getText(R.string.no_network));

				alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						// the button was clicked
					}
				});
				alertbox.show();
			}
			Utils.log(TAG, "Scroll to top, is that enough?");
			scroll.scrollTo(0, 0);
		}
	};

	private void loadComments() {
		Utils.log(TAG,  "LOAD COMMENTS");
		Thread mThread = new Thread() {
			public void run() {
				String jsonresponse = null;
				JSONArray json = null;
				try {
					jsonresponse = app.com.getComments(imgid);
				} catch (CommunicatorException c) {
					Utils.log(TAG, "No network?");
					uiCallback.sendEmptyMessage(-1);
				}
				try {
					json = new JSONArray(jsonresponse);
					Utils.log(TAG, "json resp: "+jsonresponse);
					int len = json.length();
					app.bc.cleanComments(imgid);
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
			Intent bIntent = new Intent(view.getContext(), GalleryView.class);
		bIntent.putExtra("username", userName);
		bIntent.putExtra("list", app.bc.BLOGGPAGE);
		startActivityForResult(bIntent, 0);
		break;
		case (R.id.commentButton):
			dialog.show();
		Thread cThread = new Thread() {
			public void run() {
				String commentText = comment.getText().toString();
				if(commentText.length() > 0) {
					app.com.postComment(imgid, commentText);
				}
				postCommentCallback.sendEmptyMessage(0);
			}
		};
		cThread.start();
		break;
		}
	}

	private Handler postCommentCallback = new Handler() {
		public void handleMessage(Message msg) {
			// Clean up and remove keyboard
			comment.setText("");
			InputMethodManager imm = (InputMethodManager)getSystemService(activity.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(comment.getWindowToken(), 0);

			loadComments();
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}	
}