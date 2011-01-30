/**
 * 
 */
package com.fivestar.mobilblogg;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.R.color;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class StartPageView extends Activity {
	ProgressDialog dialog;
	Thread myBloggThread;
	PostInfo pi;
	String username;
	String imgid;
	MobilbloggApp app;

	Gallery gallery;
	ImageView imgView;
	TextView headlineView;
	TextView textView;
	TextView dateView;
	Button commentButton;
	Button bloggButton;
	Activity activity;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blogg);

		this.setTitle("Min startsida");


		imgView = (ImageView)findViewById(R.id.ImageView01);
		headlineView = (TextView)findViewById(R.id.headline);
		textView = (TextView)findViewById(R.id.text);
		dateView = (TextView)findViewById(R.id.date);
		commentButton = (Button)findViewById(R.id.commentButton);
		bloggButton = (Button)findViewById(R.id.bloggButton);
		// change bgcolor
		PorterDuffColorFilter filter = new PorterDuffColorFilter(0x8C8D8C00, PorterDuff.Mode.MULTIPLY);  
		bloggButton.getBackground().setColorFilter(filter);  

		gallery = (Gallery) findViewById(R.id.examplegallery);

		dialog = new ProgressDialog(StartPageView.this);
		dialog.setMessage(getString(R.string.please_wait));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

		app = ((MobilbloggApp)getApplicationContext());
		username = app.getUserName();

		activity = this;

		dialog.show();
		myBloggThread = new Thread() {
			public void run() {
				final String jsonresponse = app.com.getStartPage();				

				Runnable action = new Runnable() {
					public void run() {
						dialog.dismiss();

						if (jsonresponse != null && jsonresponse.length()>0) {
							try {
								JSONArray json = new JSONArray(jsonresponse);
								int len = json.length();
								List<PostInfo> piList = new ArrayList<PostInfo>();
								for(int i=0; i<len;i++) {
									PostInfo pi = new PostInfo();
									pi.img      = json.getJSONObject(i).get("picture_large").toString();
									pi.imgX     = Integer.parseInt(json.getJSONObject(i).get("picture_large_x").toString());
									pi.imgY     = Integer.parseInt(json.getJSONObject(i).get("picture_large_y").toString());
									pi.thumb    = json.getJSONObject(i).get("picture_small").toString();
									pi.thumbX   = Integer.parseInt(json.getJSONObject(i).get("picture_small_x").toString());
									pi.thumbY   = Integer.parseInt(json.getJSONObject(i).get("picture_small_y").toString());
									pi.headline = json.getJSONObject(i).get("caption").toString();
									pi.text     = json.getJSONObject(i).get("body").toString();
									pi.user     = json.getJSONObject(i).get("user").toString();
									pi.createdate = json.getJSONObject(i).get("createdate").toString();
									pi.imgid    = json.getJSONObject(i).get("id").toString();
									pi.numComment = json.getJSONObject(i).getInt("nbr_comments");
									piList.add(pi);
								}
								fillList(app,piList);
							} catch (JSONException j) {
								System.out.println("JSON error:" + j.toString());
							}
						} else {
							System.out.println("StartPage failure");
							Toast.makeText(activity, "Hämtningen misslyckades", Toast.LENGTH_SHORT).show();
						}
					}
				};
				activity.runOnUiThread(action);
			}
		};
		myBloggThread.start();
	}

	public void fillList(Context c, List<PostInfo> p) {
		final List<PostInfo> piList = p;
		gallery.setAdapter(new PostInfoAdapter(activity, piList, app));
		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				final PostInfo pi = piList.get(position);

				Drawable cachedImage = app.asyncImageLoader.loadDrawable(pi.img, new ImageCallback() {
					public void imageLoaded(Drawable imageDrawable, String imageUrl) {
						imgView.setImageDrawable(imageDrawable);
						imgView.setLayoutParams(new LinearLayout.LayoutParams(pi.imgX, pi.imgY));
						imgView.setScaleType(ImageView.ScaleType.FIT_XY);
					}
				});
				imgView.setImageDrawable(cachedImage);

				headlineView.setText(Html.fromHtml(pi.headline));
				dateView.setText(Utils.prettyDate(pi.createdate) + " av " + pi.user);
				textView.setText(Html.fromHtml(pi.text));
				username = pi.user;
				imgid = pi.imgid;

				if(!username.equals(app.getUserName())) {
					bloggButton.setVisibility(View.VISIBLE);
					bloggButton.setEnabled(true);
				} else {
					bloggButton.setVisibility(View.INVISIBLE);
					bloggButton.setEnabled(false);
				}
				int num = pi.numComment;
				if(num > 0) { 
					commentButton.setVisibility(View.VISIBLE);
					commentButton.setEnabled(true);
				} else {
					commentButton.setVisibility(View.INVISIBLE);
					commentButton.setEnabled(false);
				}
				commentButton.setText(num+"");
				((ScrollView) findViewById(R.id.scroll01)).scrollTo(0, 0);
			}
		});
		//	gallery.postInvalidate();
	}

	public void startPageClickHandler(View view) {
		switch(view.getId()) {
		case R.id.bloggButton:
			System.out.println("Goto "+username+"s blogg");

			Intent bloggIntent = new Intent(view.getContext(), BloggView.class);
			bloggIntent.putExtra("username", username);
			startActivityForResult(bloggIntent, 0);
			break;
		case R.id.commentButton:
			System.out.println("Goto comment(s)");

			Intent commentIntent = new Intent(view.getContext(), CommentView.class);
			commentIntent.putExtra("imgid", imgid);
			startActivityForResult(commentIntent, 0);
			break;
		}
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
	}	
}