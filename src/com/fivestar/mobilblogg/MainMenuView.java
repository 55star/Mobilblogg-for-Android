/**
 * 
 */
package com.fivestar.mobilblogg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class MainMenuView extends Activity {
	private static final int CAMERA_PIC_REQUEST = 1336;
	private static final int GALLERY_PIC_REQUEST = 1337;
	final String TAG = "MainMenuView";
	private MobilbloggApp app;
	Activity activity;
	View mView;
	EditText blog;
	ProgressDialog dialog;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mainmenu);
		app = ((MobilbloggApp)getApplicationContext());
		activity = this;
		blog = (EditText) findViewById(R.id.gotoblog);
		AppRater.app_launched(this);

		dialog = new ProgressDialog(MainMenuView.this);
		dialog.setMessage(getString(R.string.loading));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

	}

	public void mainMenuClickHandler(View view) {
		switch(view.getId()) {
		case R.id.firstpage:
			Intent fpIntent = new Intent(view.getContext(), GalleryView.class);
			fpIntent.putExtra("list", app.bc.FIRSTPAGE);
			startActivityForResult(fpIntent, 0);
			break;

		case R.id.startpage:
			Intent spIntent = new Intent(view.getContext(), GalleryView.class);
			spIntent.putExtra("list", app.bc.FRIENDPAGE);
			startActivityForResult(spIntent, 0);
			break;

		case R.id.myblogg:
			Intent mbIntent = new Intent(view.getContext(), GalleryView.class);
			mbIntent.putExtra("username", app.getUserName());
			mbIntent.putExtra("list", app.bc.BLOGGPAGE);
			startActivityForResult(mbIntent, 0);
			break;

		case R.id.blogga:
			String path = Environment.getExternalStorageDirectory() + "/" + "Mobilblogg";
			String name = String.format("%d.jpg", System.currentTimeMillis());
			String filePath = path + "/" + name;

			File fpath = new File(path);
			fpath.mkdirs();

			File file = new File(path, name);
			try {
				file.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			app.filePath = filePath;
			promptCameraOrGallery();
			break;

		case R.id.gotobutton:
			mView = view;
			String userName = blog.getText().toString();
			if(userName.length() > 0) {
				dialog.show();
				mView = view;
				checkUserName(userName);
			}
			break;
		}
	}

	private void checkUserName(String userName) {
		final String user = userName;
		Thread mThread = new Thread() {
			public void run() {
				try {
					if(app.com.foundUser(user)) {
						checkUserCallback.sendEmptyMessage(0);	
					} else {
						checkUserCallback.sendEmptyMessage(1);
					}
				} catch (CommunicatorException c) {
					checkUserCallback.sendEmptyMessage(0);
				}
			}
		};
		mThread.start();
	}

	private Handler checkUserCallback = new Handler() {
		public void handleMessage(Message msg) {
			dialog.dismiss();
			Utils.log(TAG, "MESSAGE ID: " + msg.what);
			if (msg.what == 0) {
				Intent mbIntent = new Intent(mView.getContext(), GalleryView.class);
				mbIntent.putExtra("username", blog.getText().toString());
				mbIntent.putExtra("list", app.bc.BLOGGPAGE);
				startActivityForResult(mbIntent, 0);
			} else {
				AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
				alertbox.setMessage(getText(R.string.blognamenotfound));
				alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						// the button was clicked
						// blog.requestFocus();
					}
				});
				alertbox.show();
			}
		}
	};

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.logout:
			app.com.shutdownHttpClient();
			app.setUserName("");
			app.setLoggedInStatus(false);

			/* goto splashview and exit */
			Intent quitIntent = new Intent(this, SplashView.class);
			quitIntent.putExtra("func", "quit");
			startActivity(quitIntent);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	public void promptCameraOrGallery() {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.bloggdialogtitle);
		builder.setMessage(R.string.bloggdialogtext);

		builder.setPositiveButton(getString(R.string.camera), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				/* Camera intent */
				Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(app.filePath)));
				startActivityForResult(i, CAMERA_PIC_REQUEST);
			}
		});

		builder.setNegativeButton(getString(R.string.gallery), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				/* Gallery intent */
				Intent i = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
				startActivityForResult(i, GALLERY_PIC_REQUEST);
			} 
		});
		builder.show();
	}

	/* back from camera or gallery */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		if (requestCode == CAMERA_PIC_REQUEST) {  
			File file = new File(app.filePath);

			try {
				Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),
						file.getAbsolutePath(), null, null));

				Intent composeIntent = new Intent(this, ComposeView.class);
				composeIntent.putExtra("filepath", app.filePath);
				startActivityForResult(composeIntent, 0);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(this, getString(R.string.geterror), Toast.LENGTH_SHORT).show();
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
			}
		}
		if (requestCode == GALLERY_PIC_REQUEST && data != null) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = {MediaStore.Images.Media.DATA};

			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String filePath = cursor.getString(columnIndex);

			Intent composeIntent = new Intent(this, ComposeView.class);
			composeIntent.putExtra("filepath", filePath);
			startActivityForResult(composeIntent, 0);
		}
	}  
}