package com.eleith.calchoochoo.data;

import org.parceler.Parcel;

@Parcel(analyze = Routes.class)
public class Routes {
  public String route_id;
  public String route_short_name;
  public String route_long_name;
  public int route_type;
  public String route_color;
}
