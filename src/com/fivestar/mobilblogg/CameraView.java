package com.fivestar.mobilblogg;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class CameraView extends Activity {
	private static final String TAG = "CameraView";
	CameraPreview preview;
	Button buttonClick;
	Dialog dialog;
	String filePath;
	private Context mContext = this;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);

		preview = new CameraPreview(this);
		((FrameLayout) findViewById(R.id.preview)).addView(preview);

		buttonClick = (Button) findViewById(R.id.buttonClick);
		buttonClick.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
			}
		});
				
		// Setup Dialog
		dialog = new Dialog(mContext);
		dialog.setContentView(R.layout.cameradialog);
		dialog.setTitle("Vill du blogga den här bilden?");
		dialog.setCancelable(true);

		Button yesButton = (Button) dialog.findViewById(R.id.Button01);
		yesButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "Yes click in dialog");
				dialog.dismiss();
				
				Intent composeIntent = new Intent(mContext, ComposeView.class);
				composeIntent.putExtra("filepath", filePath);
				startActivityForResult(composeIntent, 0);
				finish();

			}
		});
		Button noButton = (Button) dialog.findViewById(R.id.Button02);
		noButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "No click in dialog");
				dialog.dismiss();
				finish();
			}
		});

		Log.d(TAG, "onCreate'd");
	}

	// Called when shutter is opened
	ShutterCallback shutterCallback = new ShutterCallback() { // <6>
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	// Handles data for raw picture
	PictureCallback rawCallback = new PictureCallback() { // <7>
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};

	// Handles data for jpeg picture
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			FileOutputStream outStream = null;
			try {
				// Write to SD Card
				filePath = String.format("/sdcard/Mobilblogg/latest/%d.jpg", System.currentTimeMillis());
				outStream = new FileOutputStream(filePath);
				outStream.write(data);
				outStream.close();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + "to " + filePath);

				dialog.show();
//
//				Intent composeIntent = new Intent(mContext, ComposeView.class);
//				startActivityForResult(composeIntent, 0);
//				finish();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};
}