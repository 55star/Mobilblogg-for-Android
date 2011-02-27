/**
 * 
 */
package com.fivestar.mobilblogg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class mainMenuView extends Activity {
	private static final int CAMERA_PIC_REQUEST = 1336;
	private static final String TAG = "MainMenu";
	private MobilbloggApp app;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainmenu);
		app = ((MobilbloggApp)getApplicationContext());
	}

	public void mainMenuClickHandler(View view) {
		switch(view.getId()) {
		case R.id.Button01:
			Intent mbIntent = new Intent(view.getContext(), BloggView.class);
			mbIntent.putExtra("username", app.getUserName());
			startActivityForResult(mbIntent, 0);
			break;
		case R.id.Button02:
			Intent spIntent = new Intent(view.getContext(), StartPageView.class);
			startActivityForResult(spIntent, 0);
			break;

		case R.id.Button05:
			Intent fpIntent = new Intent(view.getContext(), FirstPageView.class);
			startActivityForResult(fpIntent, 0);
			break;

		case R.id.Button03:
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
			
			Log.d(TAG, "Create file for picture " + filePath);

			Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filePath)));
			startActivityForResult(i, CAMERA_PIC_REQUEST);
			break;

		case R.id.Button04:
			app.com.shutdownHttpClient();
			app.setUserName("");
			app.setLoggedInStatus(false);

			/* goto splashscreen and exit */
			Intent quitIntent = new Intent(view.getContext(), SplashView.class);
			quitIntent.putExtra("func", "quit");
			startActivity(quitIntent);
			finish();
		}
	}

	/* back from camera */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if (requestCode == CAMERA_PIC_REQUEST) {  
			File file = new File(app.filePath);

			try {
				Uri u = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(),
							file.getAbsolutePath(), null, null));

				Log.i(TAG,"file.getAbs(): "+file.getAbsolutePath());
				Log.i(TAG,"filePath: "+app.filePath);
				
				Intent composeIntent = new Intent(this, ComposeView.class);
				composeIntent.putExtra("filepath", app.filePath);
				startActivityForResult(composeIntent, 0);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(this, "Något blev fel", Toast.LENGTH_SHORT).show();
			}

		}  
	}
}