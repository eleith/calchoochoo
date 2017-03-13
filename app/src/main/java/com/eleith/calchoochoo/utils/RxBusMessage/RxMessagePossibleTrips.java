package com.eleith.calchoochoo.utils.RxBusMessage;

import com.eleith.calchoochoo.data.PossibleTrip;

import java.util.ArrayList;

public class RxMessagePossibleTrips extends RxMessage<ArrayList<PossibleTrip>>{
  public RxMessagePossibleTrips(String key, ArrayList<PossibleTrip> possibleTrips) {
    super(key, possibleTrips);
  }
}
