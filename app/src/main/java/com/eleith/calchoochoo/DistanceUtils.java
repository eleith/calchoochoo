package com.eleith.calchoochoo;

public class DistanceUtils {
  public static Double METERS_IN_A_MILE = 1609.34;

  public static Double meterToMiles(Double meters) {
    return meters / METERS_IN_A_MILE;
  }
}
