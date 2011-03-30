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
import android.text.Html;
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

public class FirstPageView extends Activity {
	final String TAG = "FirstPageView";
	ProgressDialog dialog;
	Thread myBloggThread;
	PostInfo pi;
	String username;
	String imgid;
	int nbrComments;
	int page = 1;
	int selectedIndex = 0;
	MobilbloggApp app;
	List<PostInfo> postList = null;
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

		this.setTitle(getString(R.string.mainMenuFirstPage));

		imgView = (ImageView)findViewById(R.id.ImageView01);
		headlineView = (TextView)findViewById(R.id.headline);
		textView = (TextView)findViewById(R.id.text);
		dateView = (TextView)findViewById(R.id.date);
		commentButton = (Button)findViewById(R.id.commentButton);
		bloggButton = (Button)findViewById(R.id.bloggButton);
		gallery = (Gallery) findViewById(R.id.examplegallery);

		dialog = new ProgressDialog(FirstPageView.this);
		dialog.setMessage(getString(R.string.please_wait));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

		app = ((MobilbloggApp)getApplicationContext());
		username = app.getUserName();

		activity = this;

		dialog.show();
		myBloggThread = new Thread() {
			public void run() {
				postList = app.com.getBloggs(postList, 2, username, page);
				page++;

				Runnable action = new Runnable() {
					public void run() {
						dialog.dismiss();
						if(postList != null) {
							fillList(app, postList);
						} else {
							Toast.makeText(activity, getText(R.string.geterror), Toast.LENGTH_SHORT).show();
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
				if(!pi.loadMoreImg) {
					Drawable cachedImage = app.asyncImageLoader.loadDrawable(pi.img, new ImageCallback() {
						public void imageLoaded(Drawable imageDrawable, String imageUrl) {
							imgView.setImageDrawable(imageDrawable);
							imgView.setLayoutParams(new LinearLayout.LayoutParams(pi.imgX, pi.imgY));
							imgView.setScaleType(ImageView.ScaleType.FIT_XY);
						}
					});
					imgView.setImageDrawable(cachedImage);

					headlineView.setText(Html.fromHtml(pi.headline));
					dateView.setText(Utils.PrettyDate(pi.createdate, activity) + " " + getString(R.string.by) + " " + pi.user);
					textView.setText(Html.fromHtml(pi.text));

					username = pi.user;
					imgid = pi.imgid;
					bloggButton.setVisibility(View.VISIBLE);
					commentButton.setVisibility(View.VISIBLE);

					bloggButton.setText(username);
					bloggButton.setEnabled(true);

					nbrComments = pi.numComment;
					commentButton.setEnabled(true);
					if(nbrComments == 0) {
						commentButton.setText(getString(R.string.firstcomment));
					}
					if(nbrComments == 1) {
						commentButton.setText(nbrComments + " " + getString(R.string.comment));
					}
					if(nbrComments > 1) {
						commentButton.setText(nbrComments + " " + getString(R.string.comments));
					}
					((ScrollView) findViewById(R.id.scroll01)).scrollTo(0, 0);
				} else {
					dialog.setMessage(getString(R.string.loading_images));
					dialog.show();
					selectedIndex = postList.size() - 1;
					myBloggThread = new Thread() {
						public void run() {
							postList = app.com.getBloggs(postList, 2, username, page);				
							page++;
							Runnable action = new Runnable() {
								public void run() {
									dialog.dismiss();
									if(postList != null) {
										fillList(app, postList);
									} else {
										Toast.makeText(activity, getText(R.string.geterror), Toast.LENGTH_SHORT).show();
									}
								}
							};
							activity.runOnUiThread(action);
						}
					};
					myBloggThread.start();
				}
			}

		});
		gallery.setSelection(selectedIndex);
	}


	public void startPageClickHandler(View view) {
		switch(view.getId()) {
		case R.id.bloggButton:
			Intent bloggIntent = new Intent(view.getContext(), BloggView.class);
			bloggIntent.putExtra("username", username);
			startActivityForResult(bloggIntent, 0);
			break;
		case R.id.commentButton:
			if(nbrComments > 0) {
				Intent commentIntent = new Intent(view.getContext(), CommentView.class);
				commentIntent.putExtra("imgid", imgid);
				startActivityForResult(commentIntent, 0);
			} else {
				Intent writeCommentIntent = new Intent(view.getContext(), WriteCommentView.class);
				writeCommentIntent.putExtra("imgid", imgid);
				startActivityForResult(writeCommentIntent, 0);			
			}
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}	
}