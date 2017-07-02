package com.eleith.calchoochoo.data;

import android.location.Location;

import org.parceler.Parcel;

import java.util.Comparator;

@Parcel
public class Stop {
  public String stop_id;
  public String stop_name;
  public float stop_lat;
  public String parent_station;
  public float stop_lon;
  public String stop_url;
  public String platform_code;
  public String stop_code;
  public int zone_id;
  public int wheelchar_board;

  private Location location;

  public Location getLocation() {
    if (location == null) {
      location = new Location("");
      location.setLongitude(this.stop_lon);
      location.setLatitude(this.stop_lat);
    }
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public static Comparator<Stop> nameComparator = new Comparator<Stop>() {
    @Override
    public int compare(Stop lhs, Stop rhs) {
      return lhs.stop_name.compareTo(rhs.stop_name);
    }
  };
}
