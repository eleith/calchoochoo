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
  String stop_id;

  @Column
  String stop_name;

  @Column
  float stop_lat;

  @Column
  float stop_lon;

  @Column
  String stop_url;

  @Column
  String platform_code;

  @Column
  String stop_code;

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

  public String getId() {
    return this.stop_id;
  }

  public String getName() {
    return this.stop_name;
  }

  public String getUrl() {
    return this.stop_url;
  }

  public float getLatitude() {
    return this.stop_lat;
  }

  public float getLongitude() {
    return this.stop_lon;
  }

  public static Comparator<Stop> nameComparator = new Comparator<Stop>() {
    @Override
    public int compare(Stop lhs, Stop rhs) {
      return rhs.getName().compareTo(lhs.getName());
    }
  };
}
