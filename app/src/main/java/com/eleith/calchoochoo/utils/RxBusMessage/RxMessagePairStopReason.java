package com.eleith.calchoochoo.utils.RxBusMessage;

import android.support.v4.util.Pair;
import com.eleith.calchoochoo.data.Stop;

public class RxMessagePairStopReason extends RxMessage<Pair<Stop, Integer>>{
  public static final int SEARCH_REASON_DESTINATION = 0;
  public static final int SEARCH_REASON_SOURCE = 1;

  public RxMessagePairStopReason(String type, Pair<Stop, Integer> pair) {
    super(type, pair);
  }
}
