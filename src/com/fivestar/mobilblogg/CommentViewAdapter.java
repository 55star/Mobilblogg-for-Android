package com.fivestar.mobilblogg;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentViewAdapter extends BaseAdapter {

	private Activity context;
	private CommentInfo ci;
	public ImageLoader imageLoader; 

	public CommentViewAdapter(Activity a, CommentInfo comInfo) {
		super();
		context = a;
		ci = comInfo;
		imageLoader = new ImageLoader(context.getApplicationContext());
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		if(row == null){
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.item, null);
		}
		ImageView avatar = (ImageView)row.findViewById(R.id.avatar);
		TextView  comment = (TextView)row.findViewById(R.id.comment);
		TextView  username = (TextView)row.findViewById(R.id.username);
		TextView  date = (TextView)row.findViewById(R.id.date);

		imageLoader.DisplayImage(ci.avatar[position], context, avatar);
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