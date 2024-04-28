
package com.trindade.about.activity.service;

import android.annotation.*;
import android.app.*;
import android.app.ActivityManager.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.util.*;

import java.util.*;
import android.app.usage.*;
import android.widget.Toast;
import com.trindade.about.activity.utils.DatabaseUtil;
import com.trindade.about.activity.utils.WindowUtil;

public class MonitoringService extends Service {
	public boolean serviceAlive = false;
	private boolean firstRun = true;
	public static MonitoringService INSTANCE;
	private UsageStatsManager usageStats;
	public Handler mHandler = new Handler();
	private String text;
	private String text1;

	@Override
	public void onCreate() {
		super.onCreate();
		INSTANCE = this;

		serviceAlive = true;
		usageStats = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
	}

	@Override
	public void onDestroy() {
		serviceAlive = false;
		super.onDestroy();
	}

	public void getActivityInfo() {
		long currentTimeMillis = System.currentTimeMillis();
		UsageEvents queryEvents = usageStats.queryEvents(currentTimeMillis - (firstRun ? 600000 : 60000),
				currentTimeMillis);
		while (queryEvents.hasNextEvent()) {
			UsageEvents.Event event = new UsageEvents.Event();
			queryEvents.getNextEvent(event);
			int type = event.getEventType();
			if (type == UsageEvents.Event.MOVE_TO_FOREGROUND) {
				text = event.getPackageName();
				text1 = event.getClassName();
			} else if (type == UsageEvents.Event.MOVE_TO_BACKGROUND) {
				if (event.getPackageName().equals(text)) {
					text = null;
					text1 = null;
				}
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		INSTANCE = this;
		Runnable runner = new Runnable() {
			@Override
			public void run() {
				if (!DatabaseUtil.isShowWindow()) {
					MonitoringService.INSTANCE.mHandler.removeCallbacks(this);
					MonitoringService.INSTANCE.stopSelf();
				}

				getActivityInfo();
				if (MonitoringService.INSTANCE.text == null)
					return;

				MonitoringService.INSTANCE.firstRun = false;
				if (DatabaseUtil.isShowWindow()) {
					WindowUtil.show(MonitoringService.INSTANCE, MonitoringService.INSTANCE.text,
							MonitoringService.INSTANCE.text1);
				} else {
					MonitoringService.INSTANCE.stopSelf();
				}
				mHandler.postDelayed(this, 500);
			}
		};

		mHandler.postDelayed(runner, 500);
		return super.onStartCommand(intent, flags, startId);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
		restartServiceIntent.setPackage(getPackageName());

		PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1,
				restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 500,
				restartServicePendingIntent);

		super.onTaskRemoved(rootIntent);
	}
}
