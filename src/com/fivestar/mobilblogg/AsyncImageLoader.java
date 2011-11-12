package com.fivestar.mobilblogg;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;


public class AsyncImageLoader {
	private HashMap<String, SoftReference<Drawable>> imageCache;
	final static String TAG = "AsyncImageLoader";

	public AsyncImageLoader() {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
	}

	public Drawable loadDrawable(final String imageUrl, final ImageCallback imageCallback) {

		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}
		
		final Handler uiCallback = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
			}
		};		
		new Thread() {
			@Override
			public void run() {
				Drawable drawable = loadImageFromUrl(imageUrl);
				if(drawable != null) {
					imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
					Message message = uiCallback.obtainMessage(0, drawable);
					uiCallback.sendMessage(message);
				}
			}
		}.start();
		return null;
	}

	public static Drawable loadImageFromUrl(String url) {
		InputStream inputStream;
		try {
			inputStream = new URL(url).openStream();
		} catch (IOException e) {
			return null;
		}
		return Drawable.createFromStream(inputStream, "src");
	}
}