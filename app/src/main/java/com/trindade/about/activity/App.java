
package com.trindade.about.activity;

import android.app.Application;
import com.trindade.about.activity.model.CrashHandler;
import android.content.Context;
import java.io.File;
import android.app.Activity;
import com.trindade.about.activity.ui.MainActivity;
import android.content.Intent;
import com.trindade.about.activity.ui.CrashActivity;
import android.widget.Toast;
import android.os.Environment;

public class App extends Application {
	private static App sApp;

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		sApp = this;
		Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public static String getCrashLogDir() {
		return getCrashLogFolder().getAbsolutePath();
	}

	public static File getCrashLogFolder() {
		return sApp.getExternalFilesDir(null);
	}

	public static App getApp() {
		return sApp;
	}

	public static void showToast(String str, int length) {
		Toast.makeText(getApp(), str, length).show();
	}

}
