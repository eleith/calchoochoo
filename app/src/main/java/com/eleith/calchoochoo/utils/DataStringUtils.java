package com.eleith.calchoochoo.utils;

public class DataStringUtils {
  public static String adjustLateTimes(String time) {
    return time.replaceFirst("^24:", "01:").replaceFirst("^25:", "02:");
  }

  public static String removeCaltrain(String stopName) {
    return stopName.replace(" Caltrain", "");
  }
}
