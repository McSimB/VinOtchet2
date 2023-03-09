package com.mcsimb.vinotchet2.util;

import static com.mcsimb.vinotchet2.util.Const.DATE_FORMAT;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {

  @SuppressWarnings("FieldMayBeFinal")
  private static SimpleDateFormat dateFormat =
      new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

  public static float round(float value, float precis) {
    return Math.round(value * precis) / precis;
  }

  public static String roundStr(float value, float precis) {
    return String.valueOf(Math.round(value * precis) / precis);
  }

  public static String noZero2(float value) {
    String s;
    float f = round(value, 100.0f);
    if (value == 0) {
      s = "-";
    } else {
      if (f - Math.round(value) == 0) {
        s = String.format(Locale.getDefault(), "%.0f", value);
      } else if (f - round(value, 10.0f) == 0) {
        s = String.format(Locale.getDefault(), "%.1f", value);
      } else {
        s = String.format(Locale.getDefault(), "%.2f", f);
      }
    }
    return s;
  }

  @SuppressWarnings("ConstantConditions")
  public static long stringToTimestamp(String value) {
    try {
      return dateFormat.parse(value).getTime();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return 0L;
  }
  
  public static String timestampToString(long value) {
    return dateFormat.format(new Date(value));
  }
  
  public static String dayFromTimestamp(long date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date(date));
    return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
  }
}
