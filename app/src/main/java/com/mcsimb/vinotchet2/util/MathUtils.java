package com.mcsimb.vinotchet2.util;

import android.content.res.Resources;

public class MathUtils {

  public static float floor1(float value) {
    return (float) Math.floor(value * 10f) / 10f;
  }

  public static float floor2(float value) {
    return (float) Math.floor(value * 100f) / 100f;
  }

  public static float round1(float value) {
    return (float) Math.round(value * 10f) / 10f;
  }

  public static float round2(float value) {
    return (float) Math.round(value * 100f) / 100f;
  }

  public static int toInt(String value) {
    return Integer.parseInt(value);
  }

  public static int dp2px(float dpValue) {
    float scale = Resources.getSystem().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  public static int px2dp(float pxValue) {
    float scale = Resources.getSystem().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }
}
