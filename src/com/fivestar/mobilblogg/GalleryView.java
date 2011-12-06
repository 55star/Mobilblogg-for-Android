package com.fivestar.mobilblogg;

/**
 * 
 */
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class GalleryView extends Activity {
	final String TAG = "GalleryView";
	ProgressDialog dialog;
	Thread myBloggThread;
	PostInfo pi;
	String imgid;
	int nbrComments;
	int selectedIndex = 0;
	int listNum;
	String userName = null;
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

		app = ((MobilbloggApp)getApplicationContext());
		listNum = getIntent().getIntExtra("list", -1);

		if(listNum == app.bc.FIRSTPAGE) {
			this.setTitle(getString(R.string.mainMenuFirstPage));
		} else {
			if(listNum == app.bc.FRIENDPAGE) {
				this.setTitle(getString(R.string.mainMenuMyStartPage));
			} else {
				userName = getIntent().getStringExtra("username");
				if(userName != null) {
					this.setTitle(userName +"'s " + getString(R.string.moblog));
					Utils.log(TAG, "Add usr: "+userName);
					Utils.addVisitUser(app, userName);
				}				
			}
		}

		imggrid = (GridView) findViewById(R.id.dataGrid);

		dialog = new ProgressDialog(GalleryView.this);
		dialog.setMessage(getString(R.string.loading));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

		activity = this;

		if(app.bc.size(listNum, userName) == 0) {
			dialog.show();
			loadBloggPosts();
		} else {
			fillList(app, app.bc.getList(listNum, userName));
		}
	}


	private void loadBloggPosts() {
		Thread mThread = new Thread() {
			public void run() {
				int numPosts = 0; 
				try {
					numPosts = app.com.loadBloggs(app, listNum, userName);
				} catch (CommunicatorException c) {
					uiCallback.sendEmptyMessage(-1);				
				}
				uiCallback.sendEmptyMessage(numPosts);
			}
		};
		mThread.start();
	}

	private Handler uiCallback = new Handler() {
		public void handleMessage(Message msg) {
			if(dialog.isShowing()) {
				dialog.dismiss();
			}
			if(msg.what == -1) {
				Utils.log(TAG, "DŒligt teckning!!");

				AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
				alertbox.setMessage(getText(R.string.no_network));

				alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						// the button was clicked, nothing much todo
					}
				});
				alertbox.show();
			} else {
				if (msg.what == -2) {
					Toast.makeText(activity, R.string.noposts, Toast.LENGTH_SHORT).show();
				} else { 
					if (msg.what == 0) {
						Toast.makeText(activity, R.string.nomoreposts, Toast.LENGTH_SHORT).show();
					} else {
						fillList(app, app.bc.getList(listNum, userName));
					}
				}
			}
		}
	};

	public void fillList(Context c, List<PostInfo> p) {
		final List<PostInfo> piList = p;
		final Context mContext = c;
		//		selectedIndex += p.size();
		selectedIndex += 12;
		imggrid.setAdapter(new PostInfoAdapter(activity, piList, app));
		imggrid.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView parent, View v, int position, long id) {
				Intent pvIntent = new Intent(mContext, PostView.class);
				pvIntent.putExtra("idx", position);
				pvIntent.putExtra("list", listNum);
				pvIntent.putExtra("username", userName);
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


	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.reload_option_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.reload:
			Utils.log(TAG,"clear gallery: username:"+userName);
			app.bc.clear(listNum, userName);
			Utils.log(TAG,"Reload gallery");
			dialog.show();
			loadBloggPosts();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
	}	
}