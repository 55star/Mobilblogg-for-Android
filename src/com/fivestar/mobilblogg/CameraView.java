package com.fivestar.mobilblogg;

import java.io.IOException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class CameraView extends Activity implements SurfaceHolder.Callback {
	static final int FOTO_MODE = 0;
	private static final String TAG = "Camera";
	Camera mCamera;
	boolean mPreviewRunning = false;
	private Context mContext = this;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

//		Bundle extras = getIntent().getExtras();

		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.camera);
		mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void cameraClickHandler(View view) {
		if(view.getId() == R.id.camera_button) {
			AutoFocusCallBackImpl autoFocusCallBack = new AutoFocusCallBackImpl();
			mCamera.autoFocus(autoFocusCallBack);
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] imageData, Camera c) {

			if (imageData != null) {
				int jpegcomp = 80;
				String filename = "0";
				Utils.StoreByteImage(mContext, imageData, jpegcomp, filename);

				mCamera.startPreview();

				Intent composeIntent = new Intent(mContext, composeView.class);
				startActivityForResult(composeIntent, 0);
//				finish();
			}
		}
	};

	protected void onResume() {
		Log.e(TAG, "onResume");
		super.onResume();
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	protected void onStop() {
		Log.e(TAG, "onStop");
		super.onStop();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(TAG, "surfaceCreated");
		mCamera = Camera.open();

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Log.e(TAG, "surfaceChanged");

		// XXX stopPreview() will crash if preview is not running
		if (mPreviewRunning) {
			mCamera.stopPreview();
		}

		Camera.Parameters p = mCamera.getParameters();
		p.setPreviewSize(w, h);
		mCamera.setParameters(p);
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mCamera.startPreview();
		mPreviewRunning = true;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e(TAG, "surfaceDestroyed");
		mCamera.stopPreview();
		mPreviewRunning = false;
		mCamera.release();
	}

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;

	private class AutoFocusCallBackImpl implements Camera.AutoFocusCallback {
		public void onAutoFocus(boolean success, Camera camera) {
			Log.i(TAG, "Inside autofocus callback. autofocused="+success);
			//play the autofocus sound
			//MediaPlayer.create(mContext, R.raw.auto_focus).start();
			mCamera.takePicture(null, mPictureCallback, mPictureCallback);
		}
	}
}


