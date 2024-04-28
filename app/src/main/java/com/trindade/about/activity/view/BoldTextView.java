
package com.trindade.about.activity.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import com.google.android.material.textview.MaterialTextView;

public class BoldTextView extends MaterialTextView {
	public void setBoldFont(Context context) {
		Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/google_sans_bold.ttf");
		super.setTypeface(face);
	}

	public BoldTextView(Context context) {
		super(context);
		setBoldFont(context);
	}

	public BoldTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setBoldFont(context);
	}

	public BoldTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setBoldFont(context);
	}
	
	public BoldTextView(Context context, AttributeSet attrs, int defStyle, int res) {
		super(context, attrs, defStyle, res);
		setBoldFont(context);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
}
