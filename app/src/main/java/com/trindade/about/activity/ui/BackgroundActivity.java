
package com.trindade.about.activity.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.content.ClipboardManager;
import android.content.ClipData;
import androidx.appcompat.app.AppCompatActivity;
import com.trindade.about.activity.App;

@TargetApi(Build.VERSION_CODES.O)
public class BackgroundActivity extends AppCompatActivity {
    public static String STRING_COPY = "com.trindade.about.activity.COPY_STRING";
    public static String COPY_MSG = "com.trindade.about.activity.COPY_STRING_MSG";
	public static boolean isAlive;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		if (!getIntent().hasExtra(STRING_COPY))
			finish();
        String str = getIntent().getStringExtra(STRING_COPY);
        String msg = getIntent().getStringExtra(COPY_MSG);
		msg = (msg == null || msg.trim().isEmpty()) ? "Copied" : msg;
        
        if (str != null) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = new ClipData(ClipData.newPlainText("", str));
            clipboard.setPrimaryClip(clip);
        }
		finish();
    }

	@Override
	protected void onStop() {
		isAlive = false;
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		isAlive = false;
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		isAlive = true;
		super.onStart();
	}
}
