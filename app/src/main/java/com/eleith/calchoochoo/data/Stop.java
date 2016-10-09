package com.eleith.calchoochoo.data;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

public class Stop implements Parcelable {
  private String name;
  private int id;
  private String url;
  private Location location;
  private Double distance;
  private int fuzzyScore;

  public Stop(int id, String name, String url, float lat, float lon) {
    this.id = id;
    this.name = name;
    this.url = url;
    this.location = new Location("");
    this.location.setLatitude(lat);
    this.location.setLongitude(lon);
    this.fuzzyScore = 0;
  }

  private Stop(Parcel in) {
    this.id = in.readInt();
    this.name = in.readString();
    this.url = in.readString();
    this.distance = (Double) in.readValue(Double.class.getClassLoader());
    this.location = Location.CREATOR.createFromParcel(in);
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

  public static final Parcelable.Creator<Stop> CREATOR = new Parcelable.Creator<Stop>() {
    public Stop createFromParcel(Parcel in) {
      return new Stop(in);
    }

    public Stop[] newArray(int size) {
      return new Stop[size];
    }
  };

  @Override
  public void writeToParcel(Parcel out, int flags) {
    out.writeInt(this.id);
    out.writeString(this.name);
    out.writeString(this.url);
    out.writeValue(this.distance);
    this.location.writeToParcel(out, flags);
  }

  @Override
  public int describeContents() {
    return 0;
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
