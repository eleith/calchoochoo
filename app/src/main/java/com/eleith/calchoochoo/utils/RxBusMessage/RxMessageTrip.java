package com.eleith.calchoochoo.utils.RxBusMessage;

import com.eleith.calchoochoo.data.Trips;

public class RxMessageTrip extends RxMessage<Trips>{
  public RxMessageTrip(String key, Trips trip) {
    super(key, trip);
  }
}
