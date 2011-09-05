/**
 * 
 */
package com.fivestar.mobilblogg;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PostView extends Activity {
	final String TAG = "PostView";
	ProgressDialog dialog;
	Thread myBloggThread;
	String userName = null;
	int imgid;
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
	Button commentButton;
	Button bloggButton;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.post);

		final ImageView imgView = (ImageView)findViewById(R.id.ImageView01);
		final ImageView avatar = (ImageView)findViewById(R.id.avatar);

		headline = (TextView)findViewById(R.id.headline);
		text = (TextView)findViewById(R.id.text);
		date = (TextView)findViewById(R.id.date);
		user = (TextView)findViewById(R.id.username);

		dialog = new ProgressDialog(PostView.this);
		dialog.setMessage(getString(R.string.please_wait));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

		app = ((MobilbloggApp)getApplicationContext());
		imgid = getIntent().getIntExtra("imgid",0);

		selectedIndex = getIntent().getIntExtra("idx",0);
		listNum = getIntent().getIntExtra("list",-1);
		userName = getIntent().getStringExtra("username");

		Log.w(TAG,"postview getList");
		postList = app.bc.getList(listNum, userName);

		//		this.setTitle(postList.get(selectedIndex).user +"'s " + getString(R.string.moblog));

		dialog.show();

		pi = postList.get(selectedIndex);
		Drawable bloggImage = app.asyncImageLoader.loadDrawable(pi.img, new ImageCallback() {
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				imgView.setImageDrawable(imageDrawable);
				//				final int w = (int)(36 * app.getResources().getDisplayMetrics().density + 0.5f);
				imgView.setLayoutParams(new LinearLayout.LayoutParams(pi.imgX, pi.imgY));
				imgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			}
		});
		if(pi.avatar != null && pi.avatar.length() > 0) {
			Drawable avatarImage = app.asyncImageLoader.loadDrawable(pi.avatar, new ImageCallback() {
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					avatar.setImageDrawable(imageDrawable);
					//				final int w = (int)(36 * app.getResources().getDisplayMetrics().density + 0.5f);
					avatar.setLayoutParams(new LinearLayout.LayoutParams(36, 36));
					avatar.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				}
			});
			avatar.setImageDrawable(avatarImage);
		} else {
			avatar.setImageResource(R.drawable.stub);
		}

		imgView.setImageDrawable(bloggImage);


		headline.setText(pi.headline);
		text.setText(Html.fromHtml(pi.text));
		date.setText(Utils.PrettyDate(pi.createdate, this));
		user.setText(pi.user);
		userName = pi.user;
		dialog.dismiss();
	}

	public void PostViewClickHandler(View view) {
		switch(view.getId()) {
		case (R.id.avatar):
		case (R.id.username):
			Intent bIntent = new Intent(view.getContext(), GalleryView.class);
			bIntent.putExtra("username", userName);
			bIntent.putExtra("list", app.bc.BLOGGPAGE);
			startActivityForResult(bIntent,0);
			break;
		case R.id.bloggButton:
			Intent bloggIntent = new Intent(view.getContext(), PostView.class);
			bloggIntent.putExtra("username", userName);
			startActivityForResult(bloggIntent,0);
			break;
		case R.id.commentButton:
			if(nbrComments > 0) {
				Intent commentIntent = new Intent(view.getContext(), CommentView.class);
				commentIntent.putExtra("imgid", imgid);
				startActivityForResult(commentIntent,0);
			} else {
				Intent writeCommentIntent = new Intent(view.getContext(), WriteCommentView.class);
				writeCommentIntent.putExtra("imgid", imgid);
				startActivityForResult(writeCommentIntent,0);			
			}
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}	
}