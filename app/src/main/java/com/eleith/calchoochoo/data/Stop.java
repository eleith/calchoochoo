package com.eleith.calchoochoo.data;

import android.location.Location;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.Comparator;

@Parcel(analyze = Stop.class)
@Table(database = CaltrainDatabase.class, name = "stops")
public class Stop extends BaseModel {
  @Column
  @PrimaryKey
  public String stop_id;

  @Column
  public String stop_name;

  @Column
  public float stop_lat;

  @Column
  public float stop_lon;

  @Column
  public String stop_url;

  @Column
  public String platform_code;

  @Column
  public String stop_code;

  private Location location;

  public Stop() {
  }

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
      return rhs.stop_name.compareTo(lhs.stop_name);
    }
  };
}
