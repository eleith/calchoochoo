package com.eleith.calchoochoo.utils.RxBusMessage;

import android.support.v4.util.Pair;

import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.StopTimes;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;

public class RxMessageTripStops extends RxMessage<ArrayList<Pair<Stop, StopTimes>>>{

  public RxMessageTripStops(String type, ArrayList<Pair<Stop, StopTimes>> pair) {
    super(type, pair);
  }
}
