
package com.trindade.about.activity.ui;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.provider.*;
import android.view.*;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.app.*;
import androidx.appcompat.app.ActionBar;
import android.content.pm.*;
import android.graphics.drawable.*;
import android.graphics.*;
import android.text.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import java.util.List;
import com.trindade.about.activity.*;
import com.trindade.about.activity.utils.*;
import com.trindade.about.activity.R;
import com.trindade.about.activity.model.NotificationMonitor;
import com.trindade.about.activity.service.*;
import com.trindade.about.activity.model.TypefaceSpan;
import java.io.*;
import android.util.DisplayMetrics;

public class MainActivity extends AppCompatActivity {
	public static final String EXTRA_FROM_QS_TILE = "from_qs_tile";
	public static final String ACTION_STATE_CHANGED = "com.trindade.about.activity.ACTION_STATE_CHANGED";
	private MaterialSwitch mWindowSwitch, mNotificationSwitch, mAccessibilitySwitch;
	private BroadcastReceiver mReceiver;
	private MaterialAlertDialogBuilder a, b, c, d, e, f, g;
	public static MainActivity INSTANCE;
	public MaterialToolbar mToolbar;
    
   // To the next programmer: Please optimize and improve this code haha  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);
        
		INSTANCE = this;
		if (AccessibilityMonitoringService.getInstance() == null && DatabaseUtil.hasAccess())
			startService(new Intent().setClass(this, AccessibilityMonitoringService.class));

		DatabaseUtil.setDisplayWidth(getScreenWidth(this));
        dialogsInit();

//		SpannableString s = new SpannableString(getString(getString(R.string.app_name));
//		s.setSpan(new TypefaceSpan(this, "fonts/google_sans_bold.ttf"), 0, s.length(),
//				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		ActionBar actionBar = getSupportActionBar();
//		actionBar.setTitle(s);

		mWindowSwitch = findViewById(R.id.sw_window);
		mNotificationSwitch = findViewById(R.id.sw_notification);
		mAccessibilitySwitch = findViewById(R.id.sw_accessibility);

		if (Build.VERSION.SDK_INT < 24) {
			mNotificationSwitch.setVisibility(View.INVISIBLE);
		//	findViewById(R.id.divider_useNotificationPref).setVisibility(View.INVISIBLE);
		}

		mReceiver = new UpdateSwitchReceiver();
		registerReceiver(mReceiver, new IntentFilter(ACTION_STATE_CHANGED));

		mNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
				DatabaseUtil.setNotificationToggleEnabled(!isChecked);
			}
		});
		mAccessibilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
				DatabaseUtil.setHasAccess(isChecked);
				if (isChecked && AccessibilityMonitoringService.getInstance() == null)
					startService(new Intent().setClass(MainActivity.this, AccessibilityMonitoringService.class));
			}
		});
		mWindowSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
				if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(MainActivity.this)) {
					a.setTitle(getString(R.string.overlay_permission_title)).setMessage(getString(R.string.overlay_permission_message)).setPositiveButton(getString(R.string.settings), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface di, int btn) {
									Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
									intent.setData(Uri.parse("package:" + getPackageName()));
									startActivity(intent);
									di.dismiss();
								}
							}).show();
					mWindowSwitch.setChecked(false);
				} else if (DatabaseUtil.hasAccess() && AccessibilityMonitoringService.getInstance() == null) {
					b.setTitle(getString(R.string.accessibility_permission_title)).setMessage(
							getString(R.string.accessibility_permission_message))
							.setPositiveButton(getString(R.string.settings), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface di, int btn) {
									Intent intent = new Intent();
									intent.setAction("android.settings.ACCESSIBILITY_SETTINGS");
									startActivity(intent);
									di.dismiss();
								}
							}).show();
					mWindowSwitch.setChecked(false);
				} else if (!usageStats(MainActivity.this)) {
					c.setTitle(getString(R.string.usage_access_permission_title)).setMessage(
							getString(R.string.usage_access_permission_message))
							.setPositiveButton(getString(R.string.settings), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface di, int btn) {
									Intent intent = new Intent();
									intent.setAction("android.settings.USAGE_ACCESS_SETTINGS");
									startActivity(intent);
									di.dismiss();
								}
							}).show();
					mWindowSwitch.setChecked(false);
				} else {
					DatabaseUtil.setAppInitiated(true);
					DatabaseUtil.setIsShowWindow(isChecked);
					if (!isChecked) {
						WindowUtil.dismiss(MainActivity.this);
					} else {
						WindowUtil.show(MainActivity.this, getPackageName(), getClass().getName());
						startService(new Intent(MainActivity.this, MonitoringService.class));
					}
				}
			}
		});

		if (getIntent().getBooleanExtra(EXTRA_FROM_QS_TILE, false))
			mWindowSwitch.setChecked(true);
	}
    
    public void dialogsInit () {
        	a = new MaterialAlertDialogBuilder(this).setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
			@Override
	  		public void onClick(DialogInterface di, int btn) {
				di.dismiss();
	  		}
    		}).setCancelable(false);
        	b = new MaterialAlertDialogBuilder(this).setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
			@Override
	  		public void onClick(DialogInterface di, int btn) {
				di.dismiss();
		  	}
	    	}).setCancelable(false);
            c = new MaterialAlertDialogBuilder(this).setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
    		@Override
	  		public void onClick(DialogInterface di, int btn) {
				di.dismiss();
      		}
	    	}).setCancelable(false);
            d = new MaterialAlertDialogBuilder(this).setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
			@Override
		  	public void onClick(DialogInterface di, int btn) {
				di.dismiss();
		   	}
	    	}).setCancelable(false);
            e = new MaterialAlertDialogBuilder(this).setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
    		@Override
	  		public void onClick(DialogInterface di, int btn) {
				di.dismiss();
      		}
	    	}).setCancelable(false);
            f = new MaterialAlertDialogBuilder(this).setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
			@Override
		  	public void onClick(DialogInterface di, int btn) {
				di.dismiss();
		   	}
	    	}).setCancelable(false);
            g = new MaterialAlertDialogBuilder(this).setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
			@Override
		  	public void onClick(DialogInterface di, int btn) {
				di.dismiss();
		   	}
	    	}).setCancelable(false);
    }

	public static int getScreenWidth(Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
			Insets insets = windowMetrics.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
			return windowMetrics.getBounds().width() - insets.left - insets.right;
		} else {
			DisplayMetrics displayMetrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			return displayMetrics.widthPixels;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (getIntent().getBooleanExtra(EXTRA_FROM_QS_TILE, false)) {
			mWindowSwitch.setChecked(true);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshWindowSwitch();
		refreshNotificationSwitch();
		refreshAccessibilitySwitch();
		NotificationMonitor.cancelNotification(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (DatabaseUtil.isShowWindow()) {
			NotificationMonitor.showNotification(this, false);
		}
	}

	private void refreshWindowSwitch() {
		mWindowSwitch.setChecked(DatabaseUtil.isShowWindow());
		if (DatabaseUtil.hasAccess() && AccessibilityMonitoringService.getInstance() == null) {
			mWindowSwitch.setChecked(false);
		}
	}

	private void refreshAccessibilitySwitch() {
		mAccessibilitySwitch.setChecked(DatabaseUtil.hasAccess());
	}

	private void refreshNotificationSwitch() {
		mNotificationSwitch.setChecked(!DatabaseUtil.isNotificationToggleEnabled());
	}

	public void showToast(String str, int length) {
		Toast.makeText(this, str, length).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(getString(R.string.github_repo_title)).setIcon(R.drawable.ic_github).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		SpannableString span = new SpannableString(getString(R.string.about_app_title));
		/*span.setSpan(new TypefaceSpan(this, "fonts/google_sans_regular.ttf"), 0, span.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/
		menu.add(span);
		span = new SpannableString(getString(R.string.crash_log_title));
		/*span.setSpan(new TypefaceSpan(this, "fonts/google_sans_regular.ttf"), 0, span.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/
		menu.add(span);
		span = new SpannableString(getString(R.string.bug_report_title));
		/*span.setSpan(new TypefaceSpan(this, "fonts/google_sans_regular.ttf"), 0, span.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/
		menu.add(span);
		
		return super.onCreateOptionsMenu(menu);
	}

	public String readFile(File file) {
		StringBuilder text = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while (line != null) {
				text.append(line);
				text.append("\n");
				line = br.readLine();
			}

			new FileOutputStream(file).write(text.toString().getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text.toString();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = item.getTitle().toString();
		if (title.equals(getString(R.string.about_app_title))) {
			d.setTitle(getString(R.string.about_app_title)).setMessage(
					getString(R.string.about_app_message))
					.create().show();
		} else if (title.equals(getString(R.string.crash_log_title))) {
			String errorLog = readFile(new File(App.getCrashLogDir(), "crash.txt"));
			if (errorLog.isEmpty())
				showToast(getString(R.string.crash_log_not_found), 0);
			else {
				Intent intent = new Intent(this, CrashActivity.class);
				intent.putExtra(CrashActivity.EXTRA_CRASH_INFO, errorLog);
				intent.putExtra("Restart", false);
				startActivity(intent);
			}
		} else if (title.equals(getString(R.string.github_repo_title))) {
			e.setTitle(getString(R.string.github_repo_title)).setMessage(
					getString(R.string.github_repo_message))
					.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface di, int btn) {
							di.dismiss();
							startActivity(new Intent().setAction(Intent.ACTION_VIEW)
									.setData(Uri.parse("https://github.com/aquilesTrindade/application-activity")));
						}
					}).create().show();
		} else if (title.equals(getString(R.string.bug_report_title))) {
			f.setTitle(getString(R.string.bug_report_title)).setMessage(
					getString(R.string.bug_report_message)
							+ new File(App.getCrashLogDir(), "crash.txt").getAbsolutePath()
							+ getString(R.string.bug_report_message_2))
					.setPositiveButton(getString(R.string.create), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface di, int btn) {
							di.dismiss();
							startActivity(new Intent().setAction(Intent.ACTION_VIEW).setData(
									Uri.parse("https://github.com/aquilesTrindade/application-activity/issues/new")));
						}
					}).create().show();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	class UpdateSwitchReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			refreshWindowSwitch();
			refreshNotificationSwitch();
			refreshAccessibilitySwitch();
		}
	}

	public static boolean usageStats(Context context) {
		boolean granted = false;
		AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
		int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(),
				context.getPackageName());

		if (mode == AppOpsManager.MODE_DEFAULT) {
			granted = (context.checkCallingOrSelfPermission(
					android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
		} else {
			granted = (mode == AppOpsManager.MODE_ALLOWED);
		}
		return granted;
	}

	public void setupBattery() {
		g.setTitle("Battery Optimizations").setMessage(
				"Please remove battery optimization/restriction from this app in order to run in background with full functionality")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface di, int btn) {
						di.dismiss();
						Intent intent = new Intent();
						intent.setAction("android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS");
						intent.setData(Uri.parse("package:" + getPackageName()));
						startActivity(intent);
					}
				}).show();

	}
}
