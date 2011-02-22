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
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

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
			System.out.println("Goto my blogg");
			Intent mbIntent = new Intent(view.getContext(), BloggView.class);
			mbIntent.putExtra("username", app.getUserName());
			startActivityForResult(mbIntent, 0);
			break;
		case R.id.Button02:
			System.out.println("Goto my start page");
			Intent spIntent = new Intent(view.getContext(), StartPageView.class);
			startActivityForResult(spIntent, 0);
			break;

		case R.id.Button05:
			System.out.println("Goto firstpage");
			Intent fpIntent = new Intent(view.getContext(), FirstPageView.class);
			startActivityForResult(fpIntent, 0);
			break;

		case R.id.Button03:
			System.out.println("Goto camera");
			/* old stuff
			Intent cIntent = new Intent(view.getContext(), CameraView.class);
			startActivityForResult(cIntent, 0);
			 */

			Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);


			break;

		case R.id.Button04:
			System.out.println("Logout");
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
			Bitmap bmp = (Bitmap) data.getExtras().get("data");

			FileOutputStream outStream = null;
			try {
				// Write to SD Card
				String path = Environment.getExternalStorageDirectory() + "/" + "Mobilblogg";
				String name = String.format("%d.jpg", System.currentTimeMillis());
				String filePath = path + "/" + name;

				Log.d(TAG, "jpegCallback: create: " + filePath);

				File fpath = new File(path);
				fpath.mkdirs();

				File file = new File(path, name);
				file.createNewFile();


				outStream = new FileOutputStream(filePath);
				bmp.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
				outStream.flush();
				outStream.close();

				Intent composeIntent = new Intent(this, ComposeView.class);
				composeIntent.putExtra("filepath", filePath );
				startActivityForResult(composeIntent, 0);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}  
	}
}