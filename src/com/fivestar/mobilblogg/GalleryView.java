package com.fivestar.mobilblogg;

/**
 * 
 */
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class GalleryView extends Activity {
	final String TAG = "GalleryView";
	ProgressDialog dialog;
	Thread myBloggThread;
	PostInfo pi;
	String imgid;
	int nbrComments;
	int selectedIndex = 0;
	int listNum;
	MobilbloggApp app;
	GridView imggrid;
	Activity activity;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);

		listNum = getIntent().getIntExtra("list",-1);

		imggrid = (GridView) findViewById(R.id.dataGrid);

		dialog = new ProgressDialog(GalleryView.this);
		dialog.setMessage(getString(R.string.please_wait));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

		app = ((MobilbloggApp)getApplicationContext());

		activity = this;

		if(app.bc.size(listNum) == 0) {
			dialog.show();
			loadBloggPosts();
		} else {
			fillList(app, app.bc.getList(listNum));
		}
	}

	private void loadBloggPosts() {
		Thread mThread = new Thread() {
			public void run() {
				try {
					app.com.loadBloggs(app, listNum);
				} catch (CommunicatorException c) {
					Log.e(TAG,c.getError());
					return;
				}
				uiCallback.sendEmptyMessage(0);
			}
		};
		mThread.start();
	}

	private Handler uiCallback = new Handler() {
		public void handleMessage(Message msg) {
			dialog.dismiss();
			fillList(app, app.bc.getList(listNum));
		}
	};

	public void fillList(Context c, List<PostInfo> p) {
		final List<PostInfo> piList = p;
		final Context mContext = c;
		imggrid.setAdapter(new PostInfoAdapter(activity, piList, app));
		imggrid.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView parent, View v, int position, long id) {
				Intent pvIntent = new Intent(mContext, PostView.class);
				pvIntent.putExtra("idx", position);
				pvIntent.putExtra("list", listNum);
				startActivity(pvIntent);
			}

		});
		imggrid.setSelection(selectedIndex);
	}


	public void galleryClickHandler(View view) {
		if(view.getId() == R.id.loadbutton) {
			// Load more images
			dialog.show();
			loadBloggPosts();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}	
}