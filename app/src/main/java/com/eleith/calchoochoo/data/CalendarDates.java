package com.eleith.calchoochoo.data;

import org.joda.time.LocalDate;
import org.parceler.Parcel;

@Parcel(analyze = CalendarDates.class)
public class CalendarDates {
  public String service_id;
  public LocalDate date;
  public int exception_type;
}
