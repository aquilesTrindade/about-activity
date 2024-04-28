
package com.trindade.about.activity.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;
import com.trindade.about.activity.utils.WindowUtil;
import com.trindade.about.activity.utils.DatabaseUtil;
import com.trindade.about.activity.model.NotificationMonitor;
import android.content.pm.PackageManager;
import java.util.List;
import android.content.pm.ResolveInfo;
import android.content.Context;


public class AccessibilityMonitoringService extends AccessibilityService {
	private static AccessibilityMonitoringService sInstance;

	public static AccessibilityMonitoringService getInstance() {
		return sInstance;
	}

	public boolean isPackageInstalled(String packageName) {
		final PackageManager packageManager = getPackageManager();
		Intent intent = packageManager.getLaunchIntentForPackage(packageName);
		if (intent == null) {
			return false;
		}
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	public boolean isSystemClass(String className) {
		try {
			ClassLoader.getSystemClassLoader().loadClass(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		if (WindowUtil.viewAdded && DatabaseUtil.isShowWindow() && DatabaseUtil.hasAccess()) {
			String act1 = String.valueOf(event.getClassName());
			String act2 = String.valueOf(event.getPackageName());

			if (isSystemClass(act1))
				return;
			WindowUtil.show(this, act2, act1);
		}
	}
	
	@Override
	public void onInterrupt() {
	}

	@Override
	protected void onServiceConnected() {
		sInstance = this;
		super.onServiceConnected();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		sInstance = null;
		WindowUtil.dismiss(this);
		NotificationMonitor.cancelNotification(this);
		sendBroadcast(new Intent(QuickSettingsTileService.ACTION_UPDATE_TITLE));
		return super.onUnbind(intent);
	}
}
