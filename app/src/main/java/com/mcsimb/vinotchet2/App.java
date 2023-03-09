package com.mcsimb.vinotchet2;

import android.app.Application;
import com.mcsimb.vinotchet2.common.crash.CrashHandler;

public class App extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    CrashHandler.init(this);
  }
}
