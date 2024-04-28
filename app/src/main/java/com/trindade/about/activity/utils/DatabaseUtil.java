
package com.trindade.about.activity.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.trindade.about.activity.App;

public class DatabaseUtil {
	private static SharedPreferences sp = App.getApp().getSharedPreferences("com.trindade.about.activity", 0);

	public static int getDisplayWidth() {
		return sp.getInt("width", 720);
	}

	public static void setDisplayWidth(int width) {
		sp.edit().putInt("width", width).apply();
	}

	public static boolean isShowWindow() {
		return sp.getBoolean("is_show_window", false);
	}

	public static boolean hasBattery() {
		return sp.getBoolean("hasBattery", false);
	}

	public static void setHasBattery(boolean bool) {
		sp.edit().putBoolean("hasBattery", bool).apply();
	}

	public static void setIsShowWindow(boolean isShow) {
		sp.edit().putBoolean("is_show_window", isShow).apply();
	}

	public static boolean appInitiated() {
		return sp.getBoolean("app_init", false);
	}

	public static void setAppInitiated(boolean added) {
		sp.edit().putBoolean("app_init", added).apply();
	}

	public static boolean hasAccess() {
		return sp.getBoolean("has_access", true);
	}

	public static void setHasAccess(boolean added) {
		sp.edit().putBoolean("has_access", added).apply();
	}

	public static boolean hasQSTileAdded() {
		return sp.getBoolean("has_qs_tile_added", false);
	}

	public static void setQSTileAdded(boolean added) {
		sp.edit().putBoolean("has_qs_tile_added", added).apply();
	}

	public static boolean isNotificationToggleEnabled() {
		if (!hasQSTileAdded()) {
			return true;
		}
		return sp.getBoolean("is_noti_toggle_enabled", true);
	}

	public static void setNotificationToggleEnabled(boolean isEnabled) {
		sp.edit().putBoolean("is_noti_toggle_enabled", isEnabled).apply();
	}
}
