package com.eleith.calchoochoo.utils.RxBusMessage;

import com.eleith.calchoochoo.data.StopTimes;

import java.util.ArrayList;

public class RxMessageTripStops extends RxMessage<ArrayList<StopTimes>> {

  public RxMessageTripStops(String type, ArrayList<StopTimes> pair) {
    super(type, pair);
  }
}
