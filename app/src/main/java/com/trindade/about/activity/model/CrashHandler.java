
package com.trindade.about.activity.model;

import com.trindade.about.activity.App;
import com.trindade.about.activity.ui.*;
import android.app.*;
import java.text.*;
import java.io.*;
import android.content.pm.*;
import android.text.*;
import android.os.*;
import android.content.*;
import java.util.*;
import java.lang.Thread.UncaughtExceptionHandler;
import android.widget.Toast;

public class CrashHandler implements UncaughtExceptionHandler {

    private static UncaughtExceptionHandler DEFAULT = Thread.getDefaultUncaughtExceptionHandler();

    public static CrashHandler getInstance(App app) {
        return new CrashHandler(app);
    }

    private App mApp;
    private File crashDirectory;
    private String fullStackTrace, versionName;
    private long versionCode;

    public CrashHandler(App app) {
        mApp = app;
        crashDirectory = app.getExternalFilesDir(null);
        try {
            PackageInfo packageInfo = mApp.getPackageManager().getPackageInfo(mApp.getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = Build.VERSION.SDK_INT >= 28 ? packageInfo.getLongVersionCode() : packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException ignored) {
            ignored.printStackTrace();
			versionName = "unknown";
        }
    }

    @Override
    public void uncaughtException(Thread main, Throwable mThrowable) {
        if (tryUncaughtException(main, mThrowable) || DEFAULT == null) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                android.os.Process.sendSignal(android.os.Process.myPid(), android.os.Process.SIGNAL_KILL);
            }
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            DEFAULT.uncaughtException(main, mThrowable);
        }
    }

    private void showToast(String str, int length) {
        Toast.makeText(mApp, str, length).show();
    }

    private boolean tryUncaughtException(Thread thread, Throwable throwable) {
        if (throwable == null) {
            return false;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    showToast("Saving Crash Log", 0);
                    Looper.loop();
                }
            }.start();
        }
        File crashFile = new File(crashDirectory, "crash.txt");
        long timestamp = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String time = format.format(new Date(timestamp));

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
		Throwable cause = throwable.getCause();
        if (cause != null)
            cause.printStackTrace(pw);
        fullStackTrace = sw.toString();
        pw.close();

        StringBuilder sb = new StringBuilder();
        sb.append("*********************** Crash Head ***********************\n");
        sb.append("Time Of Crash : ").append(time).append("\n");
        sb.append("Device Manufacturer : ").append(Build.MANUFACTURER).append("\n");
        sb.append("Device Model : ").append(Build.MODEL).append("\n");
        sb.append("Android Version : ").append(Build.VERSION.RELEASE).append("\n");
        sb.append("Android SDK : ").append(Build.VERSION.SDK_INT).append("\n");
        sb.append("App VersionName : ").append(versionName).append("\n");
        sb.append("App VersionCode : ").append(versionCode).append("\n");
        sb.append("\n*********************** Crash Log ***********************");
        sb.append("\n").append(fullStackTrace);

        String errorLog = sb.toString();

        try {
            writeFile(crashFile, errorLog);
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }

        gotoCrashActiviy: {
            Intent intent = new Intent(mApp, CrashActivity.class);
            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(CrashActivity.EXTRA_CRASH_INFO, errorLog);
            mApp.startActivity(intent);
        }

        return errorLog != null;
    }

    private void writeFile(File file, String content) throws IOException {
        File parentFile = file.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            parentFile.mkdirs();
        }
        file.createNewFile();
        try {
            FileWriter writer = new FileWriter(file);
			writer.write(content);
			writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
