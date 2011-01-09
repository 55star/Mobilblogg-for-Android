/**
 * 
 */
package com.fivestar.mobilblogg;

import org.json.JSONArray;
import org.json.JSONException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BloggView extends Activity {
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
	Activity activity;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myblogg);

		username = getIntent().getStringExtra("username");
		
		imgView = (ImageView)findViewById(R.id.ImageView01);
		headlineView = (TextView)findViewById(R.id.headline);
		textView = (TextView)findViewById(R.id.text);
		dateView = (TextView)findViewById(R.id.date);
		gallery = (Gallery) findViewById(R.id.examplegallery);

		dialog = new ProgressDialog(BloggView.this);
		dialog.setMessage(getString(R.string.please_wait));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

		app = ((MobilbloggApp)getApplicationContext());

		this.setTitle(username);
		
		activity = this;
		
		dialog.show();
		myBloggThread = new Thread() {
			public void run() {

				final String jsonresponse = app.com.getBlogg(username);				

				Runnable action = new Runnable() {
					public void run() {
						dialog.dismiss();

						if (jsonresponse != null && jsonresponse.length()>0) {
							try {
								JSONArray json = new JSONArray(jsonresponse);
								int len = json.length();
								pi = new PostInfo(len);
								for(int i=0; i<len;i++) {
									pi.img[i]      = json.getJSONObject(i).get("picture_large").toString();
									pi.imgX[i]     = Integer.parseInt(json.getJSONObject(i).get("picture_large_x").toString());
									pi.imgY[i]     = Integer.parseInt(json.getJSONObject(i).get("picture_large_y").toString());
									pi.headline[i] = json.getJSONObject(i).get("caption").toString();
									pi.text[i]     = json.getJSONObject(i).get("body").toString();
									pi.user[i]     = json.getJSONObject(i).get("user").toString();
									pi.createdate[i] = json.getJSONObject(i).get("createdate").toString();
									pi.imgid[i]    = json.getJSONObject(i).get("id").toString();
									pi.numComment[i] = json.getJSONObject(i).getInt("nbr_comments");
								}
								fillList(app,pi);
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

	public void fillList(Context c, PostInfo p) {
		final PostInfo pi = p;
		
		gallery.setAdapter(new AddImgAdp(c, this, pi));

		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position, long id) {				
				app.imgLoader.DisplayImage(pi.img[position], activity, imgView);

				imgView.setLayoutParams(new LinearLayout.LayoutParams(pi.imgX[position], pi.imgY[position]));
				imgView.setScaleType(ImageView.ScaleType.FIT_XY);

				headlineView.setText(pi.headline[position]);
				dateView.setText(Utils.prettyDate(pi.createdate[position]) + " av " + pi.user[position]);
				textView.setText(Html.fromHtml(pi.text[position]));
				username = pi.user[position];
				imgid = pi.imgid[position];
				((ScrollView) findViewById(R.id.scroll01)).scrollTo(0, 0);
			}
		});
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

	public class AddImgAdp extends BaseAdapter {
		int GalItemBg;
		private Context cont;
		private Activity activity;
		private PostInfo pi;
		private ImageLoader imageLoader;

		public AddImgAdp(Context c, Activity a, PostInfo p) {
			cont = c;
			activity = a;
			pi = p;
			imageLoader=app.imgLoader;
			TypedArray typArray = obtainStyledAttributes(R.styleable.GalleryTheme);
			GalItemBg = typArray.getResourceId(R.styleable.GalleryTheme_android_galleryItemBackground, 0);
			typArray.recycle();
		}

		public int getCount() {
			return pi.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imgView = new ImageView(cont);

			//		imgView.setImageResource(Imgid[position]);
			imageLoader.DisplayImage(pi.img[position], activity, imgView);
			imgView.setLayoutParams(new Gallery.LayoutParams(150, 120));
			imgView.setScaleType(ImageView.ScaleType.FIT_XY);
			imgView.setBackgroundResource(GalItemBg);

			return imgView;
		}
	}
}