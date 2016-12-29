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
  int stop_id;

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

  Location location;
  Double distance;
  int fuzzyScore;

  public Stop() {
    setLocation(this.stop_lat, this.stop_lon);
  }

  public void setLocation(float lon, float lat) {
    location = new Location("");
    location.setLongitude(lon);
    location.setLatitude(lat);
  }

  public Location getLocation() {
    return this.location;
  }

  public void setDistanceFrom(Location location) {
    if (location != null) {
      this.distance = location.distanceTo(this.location) / 1.0;
    } else {
      this.distance = null;
    }
  }

  public void setFuzzyScore(int score) {
    this.fuzzyScore = score;
  }

  public Double getDistance() {
    return this.distance;
  }

  public int getId() {
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

  public static Comparator<Stop> distanceComparator = new Comparator<Stop>() {
    @Override
    public int compare(Stop lhs, Stop rhs) {
      return Double.compare(rhs.distance, lhs.distance);
    }
  };

  public static Comparator<Stop> fuzzyScoreComparator = new Comparator<Stop>() {
    @Override
    public int compare(Stop lhs, Stop rhs) {
      return Integer.compare(rhs.fuzzyScore, lhs.fuzzyScore);
    }
  };
}
