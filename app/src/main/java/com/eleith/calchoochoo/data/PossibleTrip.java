package com.eleith.calchoochoo.data;

import org.joda.time.LocalTime;
import org.parceler.Parcel;

@Parcel
public class PossibleTrip {
  private String firstStopId;
  private String lastStopId;
  private LocalTime arrivalTime;
  private LocalTime departureTime;
  private String routeId;
  private String tripId;
  private float price;
  private Integer firstStopSequence;

  public Integer getLastStopSequence() {
    return lastStopSequence;
  }

  public void setLastStopSequence(Integer lastStopSequence) {
    this.lastStopSequence = lastStopSequence;
  }

  private Integer lastStopSequence;

  public Integer getFirstStopSequence() {
    return firstStopSequence;
  }

  public void setFirstStopSequence(Integer firstStopSequence) {
    this.firstStopSequence = firstStopSequence;
  }

  public String getTripId() {
    return tripId;
  }

  public void setTripId(String tripId) {
    this.tripId = tripId;
  }

  public String getFirstStopId() {
    return firstStopId;
  }

  public String getLastStopId() {
    return lastStopId;
  }

  public LocalTime getArrivalTime() {
    return arrivalTime;
  }

  public String getRouteId() {
    return routeId;
  }

  public float getPrice() {
    return price;
  }

  public Integer getNumberOfStops() {
    return Math.abs(this.firstStopSequence - this.lastStopSequence);
  }

  public void setFirstStopId(String firstStopId) {
    this.firstStopId = firstStopId;
  }

  public void setLastStopId(String lastStopId) {
    this.lastStopId = lastStopId;
  }

  public void setArrivalTime(LocalTime arrivalTime) {
    this.arrivalTime = arrivalTime;
  }

  public void setRouteId(String routeId) {
    this.routeId = routeId;
  }

  public void setPrice(float price) {
    this.price = price;
  }

  public LocalTime getDepartureTime() {
    return departureTime;
  }

  public void setDepartureTime(LocalTime departureTime) {
    this.departureTime = departureTime;
  }
}
