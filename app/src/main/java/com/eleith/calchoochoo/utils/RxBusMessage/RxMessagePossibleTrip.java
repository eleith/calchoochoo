package com.eleith.calchoochoo.utils.RxBusMessage;

import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.Stop;

public class RxMessagePossibleTrip extends RxMessage<PossibleTrip>{
  public RxMessagePossibleTrip(String key, PossibleTrip possibleTrip) {
    super(key, possibleTrip);
  }
}
