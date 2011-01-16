package com.fivestar.mobilblogg;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class CameraPreview extends SurfaceView implements SurfaceHolder.Callback { // <1>
	private static final String TAG = "CameraPreview";

	SurfaceHolder mHolder;
	public Camera camera;

	CameraPreview(Context context) {
		super(context);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder(); 
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
	}

	// Called once the holder is ready
	public void surfaceCreated(SurfaceHolder holder) { 
		// The Surface has been created, acquire the camera and tell it where
		// to draw.
		camera = Camera.open();
		try {
			camera.setPreviewDisplay(holder);

			camera.setPreviewCallback(new PreviewCallback() { 
				// Called for each frame previewed
				public void onPreviewFrame(byte[] data, Camera camera) { 
				//	Log.d(TAG, "onPreviewFrame called at: " + System.currentTimeMillis());
					CameraPreview.this.invalidate(); 
				}
			});
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}

	// Called when the holder is destroyed
	public void surfaceDestroyed(SurfaceHolder holder) { 
		camera.release();
		//camera.stopPreview();
		camera = null;
	}

	// Called when holder has changed
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		camera.startPreview();
	}

}