
package com.trindade.about.activity.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.graphics.Typeface;
import android.content.Intent;
import com.google.android.material.imageview.ShapeableImageView;
import com.trindade.about.activity.R;
import com.trindade.about.activity.model.NotificationMonitor;
import com.google.android.material.textview.MaterialTextView;
import com.trindade.about.activity.ui.MainActivity;
import com.trindade.about.activity.ui.BackgroundActivity;
import com.trindade.about.activity.service.QuickSettingsTileService;
import com.trindade.about.activity.service.MonitoringService;
import com.trindade.about.activity.service.AccessibilityMonitoringService;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.trindade.about.activity.App;

public class WindowUtil {
	private static WindowManager.LayoutParams sWindowParams;
	public static WindowManager sWindowManager;
	private static View sView;
	private static int xInitCord = 0;
	private static int yInitCord = 0;
	private static int xInitMargin = 0;
	private static int yInitMargin = 0;
	private static String text, text1;
	private static MaterialTextView appName, packageName, className;
	private static ClipboardManager clipboard;
	public static boolean viewAdded = false;

	public static void init(final Context context) {
		sWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		sWindowParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
						: WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

		sWindowParams.gravity = Gravity.CENTER;
		sWindowParams.width = (DatabaseUtil.getDisplayWidth() / 2) + 300;
		sWindowParams.windowAnimations = android.R.style.Animation_Dialog;

		sView = LayoutInflater.from(context).inflate(R.layout.window_tasks, null);
		clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		appName = sView.findViewById(R.id.text);
		packageName = sView.findViewById(R.id.text1);
		className = sView.findViewById(R.id.text2);
		ShapeableImageView closeBtn = sView.findViewById(R.id.closeBtn);

		closeBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss(context);
				DatabaseUtil.setIsShowWindow(false);
				NotificationMonitor.cancelNotification(context);
				context.sendBroadcast(new Intent(MainActivity.ACTION_STATE_CHANGED));
			}
		});

		appName.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				copyString(context, text, "App name copied");
				return true;
			}
		});

		packageName.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				copyString(context, text, "Package name copied");
				return true;
			}
		});

		className.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				copyString(context, text1, "Class name copied");
				return true;
			}
		});

		sView.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View view, MotionEvent event) {
				WindowManager.LayoutParams layoutParams = sWindowParams;

				int xCord = (int) event.getRawX();
				int yCord = (int) event.getRawY();
				int xCordDestination;
				int yCordDestination;
				int action = event.getAction();

				if (action == MotionEvent.ACTION_DOWN) {
					xInitCord = xCord;
					yInitCord = yCord;
					xInitMargin = layoutParams.x;
					yInitMargin = layoutParams.y;
				}
				else if (action == MotionEvent.ACTION_MOVE) {
					int xDiffMove = xCord - xInitCord;
					int yDiffMove = yCord - yInitCord;
					xCordDestination = xInitMargin + xDiffMove;
					yCordDestination = yInitMargin + yDiffMove;

					layoutParams.x = xCordDestination;
					layoutParams.y = yCordDestination;
					sWindowManager.updateViewLayout(view, layoutParams);
				}
				return true;
			}
		});
	}

	private static void copyString(Context context, String str, String msg) {
		if (Build.VERSION.SDK_INT < 29) {
			ClipData clip = ClipData.newPlainText("Current Activity", str);
			clipboard.setPrimaryClip(clip);
		} else {
			context.startActivity(
					new Intent(context, BackgroundActivity.class).putExtra(BackgroundActivity.STRING_COPY, str)
							.putExtra(BackgroundActivity.COPY_MSG, msg).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}
		App.showToast(msg, 0);
	}

	public static String getAppName(Context context, String pkg) {
		try {
			PackageManager pm = context.getPackageManager();
			return pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString();
		} catch (Exception e) {
			return "Unknown";
		}
	}

	public static void show(Context context, String pkg, String clas) {
		if (sWindowManager == null) {
			init(context);
		}
		appName.setText(getAppName(context, pkg));
		packageName.setText(pkg);
		className.setText(clas);

		if (!viewAdded) {
			viewAdded = true;
			if (DatabaseUtil.isShowWindow()) {
				sWindowManager.addView(sView, sWindowParams);
			}
		}

		if (NotificationMonitor.builder != null) {
			NotificationMonitor.builder.setContentTitle(text);
			NotificationMonitor.builder.setContentText(text1);
			NotificationMonitor.notifManager.notify(NotificationMonitor.NOTIFICATION_ID,
					NotificationMonitor.builder.build());
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			QuickSettingsTileService.updateTile(context);
		}
	}

	public static void dismiss(Context context) {
		viewAdded = false;
		try {
			sWindowManager.removeView(sView);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			QuickSettingsTileService.updateTile(context);
		}
	}
}
