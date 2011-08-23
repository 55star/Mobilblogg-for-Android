/**
 * 
 */
package com.fivestar.mobilblogg;

import java.util.List;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PostView extends Activity {
	final String TAG = "PostView";
	ProgressDialog dialog;
	Thread myBloggThread;
	String username;
	int imgid;
	int nbrComments;
	int page = 1;
	int selectedIndex = 0;
	MobilbloggApp app;
	int listNum;
	List<PostInfo> postList = null;
	Gallery gallery;
	TextView headline;
	TextView text;
	TextView date;
	Button commentButton;
	Button bloggButton;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post);

		final ImageView imgView = (ImageView)findViewById(R.id.ImageView01);
		final ImageView avatar = (ImageView)findViewById(R.id.avatar);

		headline = (TextView)findViewById(R.id.headline);
		text = (TextView)findViewById(R.id.text);
		date = (TextView)findViewById(R.id.date);

		dialog = new ProgressDialog(PostView.this);
		dialog.setMessage(getString(R.string.please_wait));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

		app = ((MobilbloggApp)getApplicationContext());
		imgid = getIntent().getIntExtra("imgid",0);

		selectedIndex = getIntent().getIntExtra("idx",0);
		listNum = getIntent().getIntExtra("list",-1);

		Log.w(TAG,"listNum: "+listNum);
		postList = app.bc.getList(listNum);

		this.setTitle(username +"'s " + getString(R.string.moblog));

		dialog.show();

		if(postList == null) {
			Log.w(TAG,"we're null");
		}

		final PostInfo pi = postList.get(selectedIndex);
		Log.w(TAG,"Load img: "+pi.img);
		Log.w(TAG,"user: "+pi.user);
		Drawable cachedImage = app.asyncImageLoader.loadDrawable(pi.img, new ImageCallback() {
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				Log.w(TAG,"Loaded!");
				imgView.setImageDrawable(imageDrawable);
				final int w = (int)(36 * app.getResources().getDisplayMetrics().density + 0.5f);
				imgView.setLayoutParams(new LinearLayout.LayoutParams(pi.imgX, pi.imgY));
				imgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			}
		});
		headline.setText(pi.headline);
		text.setText(Html.fromHtml(pi.text));
		date.setText(Utils.PrettyDate(pi.createdate, this) + " " + getString(R.string.by) + " " + pi.user);
		imgView.setImageDrawable(cachedImage);
		dialog.dismiss();
	}

	public void startPageClickHandler(View view) {
		switch(view.getId()) {
		case R.id.bloggButton:
			Intent bloggIntent = new Intent(view.getContext(), PostView.class);
			bloggIntent.putExtra("username", username);
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