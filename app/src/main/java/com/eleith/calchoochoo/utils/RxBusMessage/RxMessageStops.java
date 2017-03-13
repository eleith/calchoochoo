package com.eleith.calchoochoo.utils.RxBusMessage;

import com.eleith.calchoochoo.data.Stop;

import java.util.ArrayList;

public class RxMessageStops extends RxMessage<ArrayList<Stop>>{
  public RxMessageStops(String key, ArrayList<Stop> stops) {
    super(key, stops);
  }
}
