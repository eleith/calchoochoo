package com.eleith.calchoochoo.data;

import org.joda.time.LocalTime;
import org.parceler.Parcel;

import java.util.Comparator;

@Parcel(analyze = StopTimes.class)
public class StopTimes {
  public String trip_id;
  public LocalTime arrival_time;
  public LocalTime departure_time;
  public String stop_id;
  public int stop_sequence;
  public int drop_off_type;
  public Stop stop;

  public static Comparator<StopTimes> sequenceComparator = new Comparator<StopTimes>() {
    @Override
    public int compare(StopTimes lhs, StopTimes rhs) {
      return lhs.stop_sequence - rhs.stop_sequence;
    }
  };
}
