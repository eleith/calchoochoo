package com.eleith.calchoochoo.data;

import org.parceler.Parcel;


@Parcel(analyze = FareRules.class)
public class FareRules {
  public String fare_id;
  public String route_id;
  public int origin_id;
  public int destination_id;
}
