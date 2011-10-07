package com.fivestar.mobilblogg.widgets;

import com.fivestar.mobilblogg.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AspectRatioImageView extends ImageView {
	final String TAG = "AspectRatioImageView";

	public AspectRatioImageView(Context context) {
		super(context);
	}

	public AspectRatioImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		if(getDrawable() == null) {
			Utils.log(TAG,"drawable == null, set height to width");
			height = width;
		} else {
			height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
		}
		Utils.log(TAG,"width: "+width+" height: "+height);
		setMeasuredDimension(width, height);			
	}
}