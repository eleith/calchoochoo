package com.eleith.calchoochoo.data;

import org.joda.time.LocalDate;
import org.parceler.Parcel;

@Parcel(analyze = Calendar.class)
public class Calendar {
  public String service_id;
  public int monday;
  public int tuesday;
  public int wednesday;
  public int thursday;
  public int friday;
  public int saturday;
  public int sunday;
  public LocalDate start_date;
  public LocalDate end_date;
}
