package com.mcsimb.vinotchet2.common.crash;

import static com.mcsimb.vinotchet2.util.MathUtils.dp2px;

import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class CrashHandler {

  public static final UncaughtExceptionHandler DEFAULT_UNCAUGHT_EXCEPTION_HANDLER =
      Thread.getDefaultUncaughtExceptionHandler();

  public static void init(Application app) {
    init(app, null);
  }

  public static void init(final Application app, final String crashDir) {
    Thread.setDefaultUncaughtExceptionHandler(
        new UncaughtExceptionHandler() {

          @SuppressWarnings("NullableProblems")
          @Override
          public void uncaughtException(Thread thread, Throwable throwable) {
            try {
              tryUncaughtException(thread, throwable);
            } catch (Throwable e) {
              e.printStackTrace();
              if (DEFAULT_UNCAUGHT_EXCEPTION_HANDLER != null)
                DEFAULT_UNCAUGHT_EXCEPTION_HANDLER.uncaughtException(thread, throwable);
            }
          }

          private void tryUncaughtException(Thread thread, Throwable throwable) {
            final String time =
                new SimpleDateFormat("yy_MM_dd-HH_mm_ss", Locale.getDefault()).format(new Date());
            File crashFile =
                new File(
                    TextUtils.isEmpty(crashDir)
                        ? new File(app.getExternalFilesDir(null), "crash")
                        : new File(crashDir),
                    "crash_" + time + ".txt");

            String versionName = "unknown";
            long versionCode = 0;
            try {
              PackageInfo packageInfo =
                  app.getPackageManager().getPackageInfo(app.getPackageName(), 0);
              versionName = packageInfo.versionName;
              versionCode =
                  (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                      ? packageInfo.getLongVersionCode()
                      : packageInfo.versionCode;

            } catch (PackageManager.NameNotFoundException ignored) {
            }

            String fullStackTrace;
            {
              StringWriter sw = new StringWriter();
              PrintWriter pw = new PrintWriter(sw);
              throwable.printStackTrace(pw);
              fullStackTrace = sw.toString();
              pw.close();
            }

            String errorLog =
                "************* Crash Head ****************\n"
                    + "Time Of Crash "
                    + " "
                    + "    : "
                    + time
                    + "\n"
                    + "Device Manufacturer: "
                    + Build.MANUFACTURER
                    + "\n"
                    + "Device Model       : "
                    + Build.MODEL
                    + "\n"
                    + "Android Version   "
                    + " "
                    + ": "
                    + Build.VERSION.RELEASE
                    + "\n"
                    + "Android SDK        : "
                    + Build.VERSION.SDK_INT
                    + "\n"
                    + "App VersionName    : "
                    + versionName
                    + "\n"
                    + "App VersionCode    : "
                    + versionCode
                    + "\n"
                    + "************* Crash Head ****************\n"
                    + "\n"
                    + fullStackTrace;

            try {
              writeFile(crashFile, errorLog);
            } catch (IOException ignored) {
            }

            {
              Intent intent = new Intent(app, CrashActivity.class);
              intent.addFlags(
                  Intent.FLAG_ACTIVITY_NEW_TASK
                      | Intent.FLAG_ACTIVITY_CLEAR_TOP
                      | Intent.FLAG_ACTIVITY_CLEAR_TASK);
              intent.putExtra(CrashActivity.EXTRA_CRASH_INFO, errorLog);
              try {
                app.startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
              } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                if (DEFAULT_UNCAUGHT_EXCEPTION_HANDLER != null)
                  DEFAULT_UNCAUGHT_EXCEPTION_HANDLER.uncaughtException(thread, throwable);
              }
            }
          }

          @SuppressWarnings("ResultOfMethodCallIgnored")
          private void writeFile(File file, String content) throws IOException {
            File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
              parentFile.mkdirs();
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            try {
              fos.close();
            } catch (IOException ignored) {
            }
          }
        });
  }

  public static final class CrashActivity extends Activity
      implements MenuItem.OnMenuItemClickListener {

    private static final String EXTRA_CRASH_INFO = "crashInfo";

    private String mLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setTheme(android.R.style.Theme_DeviceDefault);
      mLog = getIntent().getStringExtra(EXTRA_CRASH_INFO);
      {
        ScrollView contentView = new ScrollView(this);
        contentView.setFillViewport(true);
        HorizontalScrollView hw = new HorizontalScrollView(this);
        TextView message = new TextView(this);
        {
          int padding = dp2px(16);
          message.setPadding(padding, padding, padding, padding);
          message.setText(mLog);
          message.setTextIsSelectable(true);
          message.setTextColor(Color.RED);
        }
        hw.addView(message);
        contentView.addView(
            hw, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(contentView);
      }
    }

    @Override
    public void onBackPressed() {
      restart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      menu.add(0, android.R.id.copy, 0, android.R.string.copy)
          .setOnMenuItemClickListener(this)
          .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
      return true;
    }

    private void restart() {
      PackageManager pm = getPackageManager();
      Intent intent = pm.getLaunchIntentForPackage(getPackageName());
      if (intent != null) {
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
      }
      finish();
      android.os.Process.killProcess(android.os.Process.myPid());
      System.exit(0);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
      if (item.getItemId() == android.R.id.copy) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText(getPackageName(), mLog));
      }
      return false;
    }
  }
}
