
package com.trindade.about.activity.model;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.NotificationChannel;
import android.app.TaskStackBuilder;
import android.app.Notification;
import android.graphics.Color;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import java.util.List;
import java.lang.reflect.AnnotatedElement;

import com.trindade.about.activity.utils.DatabaseUtil;
import com.trindade.about.activity.R;
import com.trindade.about.activity.ui.MainActivity;
import com.trindade.about.activity.utils.WindowUtil;
import com.trindade.about.activity.service.QuickSettingsTileService;

public class NotificationMonitor extends BroadcastReceiver {
	public static final int NOTIFICATION_ID = 696969691;
	private static final int ACTION_STOP = 2;
	private static final String EXTRA_NOTIFICATION_ACTION = "command";
	public static Notification.Builder builder;
	public static NotificationManager notifManager;

	public static void showNotification(Context context, boolean isPaused) {
		if (!DatabaseUtil.isNotificationToggleEnabled()) {
			return;
		}
		notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent intent = new Intent(context, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(intent);
		PendingIntent pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			String CHANNEL_ID = context.getPackageName() + "_channel_007";
			CharSequence name = "Activity Info";

			NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_MAX);
			mChannel.setDescription("Information of activity (current)");
			mChannel.enableLights(false);
			mChannel.enableVibration(false);
			mChannel.setShowBadge(false);
			notifManager.createNotificationChannel(mChannel);

			builder = new Notification.Builder(context, CHANNEL_ID);
		}
		else {
			builder = new Notification.Builder(context);
		}

		builder.setContentTitle(context.getString(R.string.is_running, context.getString(R.string.app_name)))
			.setSmallIcon(R.drawable.ic_shortcut)
			.setContentText(context.getString(R.string.touch_to_open))
			.setColor(context.getColor(android.R.color.holo_blue_bright))
			.setVisibility(Notification.VISIBILITY_SECRET)
			.setOngoing(!isPaused)
			.setAutoCancel(true)
			.setContentIntent(pIntent);

		builder.addAction(R.drawable.ic_launcher, context.getString(R.string.noti_action_stop),
						  getPendingIntent(context, ACTION_STOP)).setContentIntent(pIntent);

	//	notifManager.notify(NOTIFICATION_ID, builder.build());
	}

	public static PendingIntent getPendingIntent(Context context, int command) {
		Intent intent = new Intent("com.trindade.about.activity.ACTION_NOTIFICATION_RECEIVER");
		intent.putExtra(EXTRA_NOTIFICATION_ACTION, command);
		return PendingIntent.getBroadcast(context, 0, intent, 0);
	}

	public static void cancelNotification(Context context) {
		if (notifManager != null)
			notifManager.cancel(NOTIFICATION_ID);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int command = intent.getIntExtra(EXTRA_NOTIFICATION_ACTION, -1);
		if (command == ACTION_STOP) {
			WindowUtil.dismiss(context);
			DatabaseUtil.setIsShowWindow(false);
			cancelNotification(context);
			context.sendBroadcast(new Intent(MainActivity.ACTION_STATE_CHANGED));
		}
		context.sendBroadcast(new Intent(QuickSettingsTileService.ACTION_UPDATE_TITLE));
	}
}
