/**
 * 
 */
package com.fivestar.mobilblogg;

import org.json.JSONArray;
import org.json.JSONException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WriteCommentView extends Activity {
	final String TAG = "WriteCommentView";
	MobilbloggApp app;
	String imgid;
	EditText commentText;
	Button writeCommentButton;
	ProgressDialog dialog;
	Thread writeCommentThread;
	Activity activity;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.writecomment);

		commentText = (EditText)findViewById(R.id.commentText);
		dialog = new ProgressDialog(WriteCommentView.this);
		dialog.setMessage(getString(R.string.please_wait));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

		app = ((MobilbloggApp)getApplicationContext());

		imgid = getIntent().getStringExtra("imgid");

		this.setTitle(getString(R.string.writecomment));

		activity = this;
	}

	public void writeCommentClickHandler(View view) {

		final String comment = commentText.getText().toString();
		final Activity activity = this;

		dialog.show();
		writeCommentThread = new Thread() {
			public void run() {
				String resp = null;
				final String jsonresponse;
				try {
					resp = app.com.postComment(imgid,comment);
				} catch (Throwable e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(resp != null) {
					jsonresponse = resp;
				} else {
					Toast.makeText(activity, getString(R.string.geterror), Toast.LENGTH_SHORT).show();
					return;
				}

				Runnable action = new Runnable() {
					public void run() {
						int postStatus = 0;

						dialog.dismiss();

						if (jsonresponse != null && jsonresponse.length()>0) {
							try {
								JSONArray json = new JSONArray(jsonresponse);
								postStatus = json.getJSONObject(0).optInt("Status");

							} catch (JSONException j) {
								Log.e(TAG,"JSON error:" + j.toString());
							}
						} else {
							Toast.makeText(activity, getString(R.string.geterror), Toast.LENGTH_SHORT).show();
						}

						if (postStatus > 0) {
							Toast.makeText(activity, "Kommentaren skickad", Toast.LENGTH_SHORT).show();
							Intent commentIntent = new Intent(activity, CommentView.class);
							commentIntent.putExtra("imgid", imgid);
							startActivityForResult(commentIntent, 0);
							finish();
						} else {
							Toast.makeText(activity, getString(R.string.geterror), Toast.LENGTH_SHORT).show();
						}
					}
				};
				activity.runOnUiThread(action);
			}
		};
		writeCommentThread.start();
	}		

	@Override
	public void onDestroy() {
		super.onDestroy();
	}	
}