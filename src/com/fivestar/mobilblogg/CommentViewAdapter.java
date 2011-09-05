package com.fivestar.mobilblogg;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentViewAdapter extends BaseAdapter {

	private CommentInfo ci;
	private Activity context;
	public AsyncImageLoader imageLoader; 
	public MobilbloggApp app;

	
	public CommentViewAdapter(Activity a, CommentInfo comInfo, MobilbloggApp mba) {
		super();
		context = a;
		app = mba;
		ci = comInfo;
		imageLoader = app.asyncImageLoader;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		if(row == null){
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.item, null);
		}
		final ImageView avatar = (ImageView)row.findViewById(R.id.avatar);
		TextView  comment = (TextView)row.findViewById(R.id.comment);
		TextView  username = (TextView)row.findViewById(R.id.username);
		TextView  date = (TextView)row.findViewById(R.id.date);
//		imageLoader.DisplayImage(ci.avatar[position], context, avatar);

		Drawable cachedImage = app.asyncImageLoader.loadDrawable(ci.avatar[position], new ImageCallback() {
		    public void imageLoaded(Drawable imageDrawable, String imageUrl) {
		        avatar.setImageDrawable(imageDrawable);
		    }
		});
	    avatar.setImageDrawable(cachedImage);
	    if(ci.noMember[position] == 0) {
	    	avatar.setTag(ci.username[position]);
	    }
		comment.setText(Html.fromHtml(ci.comment[position]));
		username.setText(ci.username[position]);
		date.setText(ci.createdate[position]);

		return row;
	}

	public int getCount() {
		return ci.length;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return ci;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
}