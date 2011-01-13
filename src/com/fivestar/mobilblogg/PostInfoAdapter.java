package com.fivestar.mobilblogg;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class PostInfoAdapter extends ArrayAdapter<PostInfo> {
	private final static String TAG = "PostInfoAdapter";
	MobilbloggApp app;
	int galleryItemBg;
	List<PostInfo> listInfo;
	
	public PostInfoAdapter(Activity activity, List<PostInfo> pi, MobilbloggApp a) {
		super(activity, 0, pi);
		listInfo = pi;
		app = a;
		TypedArray typArray =  app.obtainStyledAttributes(R.styleable.GalleryTheme);
		galleryItemBg = typArray.getResourceId(R.styleable.GalleryTheme_android_galleryItemBackground, 0);
		typArray.recycle();
	}

	public int getCount() {
		return listInfo.size();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();
		PostInfo pi = getItem(position);	
		
		// Load the image and set it on the ImageView
		final ImageView imageView = new ImageView((Context)app);
		Drawable cachedImage = app.asyncImageLoader.loadDrawable(pi.thumb, new ImageCallback() {
		    public void imageLoaded(Drawable imageDrawable, String imageUrl) {
		        imageView.setImageDrawable(imageDrawable);
		        imageView.setLayoutParams(new Gallery.LayoutParams(150, 120));
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				imageView.setBackgroundResource(galleryItemBg);
		        notifyDataSetChanged();
		    }
		});
	    imageView.setImageDrawable(cachedImage);
        imageView.setLayoutParams(new Gallery.LayoutParams(150, 120));
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		imageView.setBackgroundResource(galleryItemBg);

		return imageView;
	}

}