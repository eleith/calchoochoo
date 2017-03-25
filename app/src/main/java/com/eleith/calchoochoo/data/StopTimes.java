package com.eleith.calchoochoo.data;

import org.joda.time.LocalTime;
import org.parceler.Parcel;

@Parcel(analyze = StopTimes.class)
public class StopTimes {
  public String trip_id;
  public LocalTime arrival_time;
  public LocalTime departure_time;
  public String stop_id;
  public int stop_sequence;
  public int pickup_time;
  public int drop_off_type;
  public Stop stop;
}
