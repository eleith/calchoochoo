package com.eleith.calchoochoo.data;

import org.joda.time.LocalTime;
import org.parceler.Parcel;

@Parcel
public class PossibleTrain {
  private String stopId;
  private LocalTime arrivalTime;
  private LocalTime departureTime;
  private String routeId;
  private String tripId;

  public String getTripId() {
    return tripId;
  }

  public void setTripId(String tripId) {
    this.tripId = tripId;
  }

  public String getStopId() {
    return stopId;
  }

  public LocalTime getArrivalTime() {
    return arrivalTime;
  }

  public String getRouteId() {
    return routeId;
  }

  public void setStopId(String stopId) {
    this.stopId = stopId;
  }

  public void setArrivalTime(LocalTime arrivalTime) {
    this.arrivalTime = arrivalTime;
  }

  public void setRouteId(String routeId) {
    this.routeId = routeId;
  }

  public LocalTime getDepartureTime() {
    return departureTime;
  }

  public void setDepartureTime(LocalTime departureTime) {
    this.departureTime = departureTime;
  }
}
