package com.eleith.calchoochoo.utils.RxBusMessage;

import com.eleith.calchoochoo.data.Routes;
import com.eleith.calchoochoo.data.Trips;

import java.util.ArrayList;

public class RxMessageTrips extends RxMessage<ArrayList<Trips>>{
  public RxMessageTrips(String key, ArrayList<Trips> trips) {
    super(key, trips);
  }
}
