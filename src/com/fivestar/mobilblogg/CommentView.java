/**
 * 
 */
package com.fivestar.mobilblogg;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class CommentView extends ListActivity implements View.OnClickListener {
	final String TAG = "CommentView";
	ProgressDialog dialog;
	Thread commentThread;
	ListActivity activity;
	MobilbloggApp app;
	int imgid = 0;
	ListView list;
	CommentViewAdapter adapter;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment);
		activity = this;
		this.setTitle("Kommentarer");

		imgid = Integer.parseInt(getIntent().getStringExtra("imgid"));

		Button b = new Button(this);
		b.setPadding(0, 15, 0, 15);
		b.setWidth(80);
		b.setText("Skriv en kommentar");
		b.setOnClickListener(this);

		list = (ListView)findViewById(android.R.id.list);	
		list.addFooterView(b);
		dialog = new ProgressDialog(CommentView.this);
		dialog.setMessage(getString(R.string.please_wait));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

		app = ((MobilbloggApp)getApplicationContext());

		dialog.show();
		commentThread = new Thread() {
			public void run() {

				final String jsonresponse = app.com.getComments(imgid);				

				Runnable action = new Runnable() {
					public void run() {
						dialog.dismiss();

						if (jsonresponse != null && jsonresponse.length()>0) {
							try {
								JSONArray json = new JSONArray(jsonresponse);
								int len = json.length();
								CommentInfo ci = new CommentInfo(len);
								for(int i=0; i<len;i++) {
									ci.username[i]   = json.getJSONObject(i).get("author").toString();
									ci.comment[i]    = json.getJSONObject(i).get("comment").toString();
									ci.createdate[i] = Utils.PrettyDate(json.getJSONObject(i).get("createdate").toString());

									ci.avatar[i] = app.com.getProfileAvatar(ci.username[i]);
									if(ci.avatar[i] == null) {
										ci.avatar[i] = "http://www.mobilblogg.nu/gfx/noavatar_100.gif";
									}
								}
								adapter = new CommentViewAdapter(activity, ci, app);
								activity.setListAdapter(adapter);
							} catch (JSONException j) {
								Log.e(TAG,"JSON error:" + j.toString());
							}
						} else {
							Toast.makeText(activity, "Hämtningen misslyckades", Toast.LENGTH_SHORT).show();
						}
					}
				};
				activity.runOnUiThread(action);
			}
		};
		commentThread.start();
	}

	public void onClick(View view) {
		Intent writecommentIntent = new Intent(view.getContext(), WriteCommentView.class);
		writecommentIntent.putExtra("imgid", ""+imgid);
		startActivityForResult(writecommentIntent, 0);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}