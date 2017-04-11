package com.eleith.calchoochoo.data;

import org.parceler.Parcel;

@Parcel(analyze = Trips.class)
public class Trips {
  public String route_id;
  public String service_id;
  public String trip_id;
  public String trip_headsign;
  public String trip_short_name;
  public int direction_id;
  public String shape_id;
  public int wheelchair_accessible;
  public int bikes_allowed;
}
