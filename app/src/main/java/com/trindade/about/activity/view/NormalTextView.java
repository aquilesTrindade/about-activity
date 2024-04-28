
package com.trindade.about.activity.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import com.google.android.material.textview.MaterialTextView;

public class NormalTextView extends MaterialTextView {
    public void setRegularFont(Context context) {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/google_sans_regular.ttf"); 
        super.setTypeface(face);
    }

    public NormalTextView(Context context) {
        super(context);
        setRegularFont(context);
    }

    public NormalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setRegularFont(context);
    }

    public NormalTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setRegularFont(context); 
    }
	
	public NormalTextView(Context context, AttributeSet attrs, int defStyle, int res) {
		super(context, attrs, defStyle, res);
		setRegularFont(context);
	}

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }
}
