
package com.trindade.about.activity.ui;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.material.textview.MaterialTextView;
import androidx.appcompat.app.AppCompatActivity;
import com.trindade.about.activity.R;
import android.view.MenuItem;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.view.Menu;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Toast;
import android.text.SpannableString;
import androidx.appcompat.app.ActionBar;
import android.text.Spannable;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.trindade.about.activity.model.TypefaceSpan;


public class CrashActivity extends AppCompatActivity {
	public static String EXTRA_CRASH_INFO = "crash";
	private String crashInfo;
	private boolean restart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crash_view);

		SpannableString s = new SpannableString(getString(R.string.app_name));
		s.setSpan(new TypefaceSpan(this, "fonts/google_sans_bold.ttf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(s);

		restart = getIntent().getBooleanExtra("Restart", true);
		String mLog = getIntent().getStringExtra(EXTRA_CRASH_INFO);
		crashInfo = mLog;
		MaterialTextView crashed = findViewById(R.id.crashed);
		crashed.setText(mLog);
	}

	@Override
	public void onBackPressed() {
		if (!restart) {
			finish();
			return;
		}
		new MaterialAlertDialogBuilder(this).setTitle("Exit").setMessage("App will restart, are you sure to exit")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface di, int btn) {
						di.dismiss();
						restart();
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface di, int btn) {
						di.dismiss();
					}
				}).setCancelable(false).show();
	}

	private void restart() {
		PackageManager pm = getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(getPackageName());
		if (intent != null) {
			intent.addFlags(
					Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
		}
		finish();
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.copy) {
			ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			cm.setPrimaryClip(ClipData.newPlainText(getPackageName(), crashInfo));
			Toast.makeText(this, "Copied", 0).show();
		} else if (item.getItemId() == android.R.id.redo) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SpannableString s = new SpannableString("Copy Log");
		s.setSpan(new TypefaceSpan(this, "fonts/google_sans_regular.ttf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		menu.add(0, android.R.id.copy, 0, s);
		if (restart) {
			s = new SpannableString("Restart App");
			s.setSpan(new TypefaceSpan(this, "fonts/google_sans_regular.ttf"), 0, s.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			menu.add(1, android.R.id.redo, 1, s);
		}
		return super.onCreateOptionsMenu(menu);
	}

}
