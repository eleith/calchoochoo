package com.eleith.calchoochoo.data;

import android.location.Location;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.Comparator;

@Parcel
public class Stop {
  String name;
  int id;
  String url;
  Location location;
  Double distance;
  int fuzzyScore;

  public Stop() {

  }

  public Stop(int id, String name, String url, float lat, float lon) {
    this.id = id;
    this.name = name;
    this.url = url;
    this.location = new Location("");
    this.location.setLatitude(lat);
    this.location.setLongitude(lon);
    this.fuzzyScore = 0;
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
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getUrl() {
    return this.url;
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
