/**
 * 
 */
package com.fivestar.mobilblogg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

public class MainMenuView extends Activity {
	private static final int CAMERA_PIC_REQUEST = 1336;
	private static final int GALLERY_PIC_REQUEST = 1337;
	private MobilbloggApp app;
	Activity activity;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainmenu);
		app = ((MobilbloggApp)getApplicationContext());
		activity = this;
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
			promptCameraOrGallery();
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

	public void promptCameraOrGallery() {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.bloggdialogtitle);
		builder.setMessage(R.string.bloggdialogtext);

		builder.setPositiveButton("Kameran", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				/* Camera intent */
				Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(app.filePath)));
				startActivityForResult(i, CAMERA_PIC_REQUEST);
			}
		});

		builder.setNegativeButton("Galleriet", new DialogInterface.OnClickListener() {
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
		System.out.println("BACK from camera/gallery");
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
				Toast.makeText(this, "Något blev fel", Toast.LENGTH_SHORT).show();
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