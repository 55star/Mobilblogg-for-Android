package com.fivestar.mobilblogg;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;


public class AsyncImageLoader {
	private HashMap<String, SoftReference<Drawable>> imageCache;

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
		final Handler handler = new Handler() {
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
					Message message = handler.obtainMessage(0, drawable);
					handler.sendMessage(message);
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
			//			throw new RuntimeException(e);
		}
		return Drawable.createFromStream(inputStream, "src");
	}
}