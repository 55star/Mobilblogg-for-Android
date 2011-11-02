package com.fivestar.mobilblogg;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class PostInfoAdapter extends ArrayAdapter<PostInfo> {
	final String TAG = "PostInfoAdapter";
	MobilbloggApp app;
	int galleryItemBg;
	List<PostInfo> listInfo;

	public PostInfoAdapter(Activity activity, List<PostInfo> pi, MobilbloggApp a) {
		super(activity, 0, pi);
		listInfo = pi;
		app = a;
// should not be needed when we switched away from galleryview 2011-08-17
//		TypedArray typArray =  app.obtainStyledAttributes(R.styleable.GalleryTheme);
//		galleryItemBg = typArray.getResourceId(R.styleable.GalleryTheme_android_galleryItemBackground, 0);
//		typArray.recycle();
	}

	public int getCount() {
		if(listInfo == null) {
			return 0;
		}
		return listInfo.size();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		PostInfo pi = getItem(position);	
		if(pi == null) {
			Utils.log(TAG,"getItem == null @ position " + position);
		}
		final ImageView imageView = new ImageView((Context)app);
		if(!pi.loadMoreImg) {
			// Load the image and set it on the ImageView
			Drawable cachedImage = app.asyncImageLoader.loadDrawable(pi.thumb, new ImageCallback() {
			    public void imageLoaded(Drawable imageDrawable, String imageUrl) {
			        imageView.setImageDrawable(imageDrawable);
					final int w = (int)(36 * app.getResources().getDisplayMetrics().density + 0.5f);
					imageView.setLayoutParams(new GridView.LayoutParams(w * 2, w * 2));
					imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			    }
			});
			imageView.setImageDrawable(cachedImage);
			final int w = (int)(36 * app.getResources().getDisplayMetrics().density + 0.5f);
			imageView.setLayoutParams(new GridView.LayoutParams(w * 2, w * 2));
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		}
		return imageView;
	}

}