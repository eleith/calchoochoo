package com.eleith.calchoochoo.utils.RxBusMessage;

import android.support.v4.util.Pair;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;

import org.joda.time.LocalDateTime;

public class RxMessageStopsAndDetails extends RxMessage<Pair<Pair<Stop, Stop>, Pair<Integer, LocalDateTime>>>{
  public static int DETAIL_DEPARTING = RxMessageStopMethodAndDateTime.DEPARTING;
  public static int DETAIL_ARRIVING = RxMessageStopMethodAndDateTime.ARRIVING;

  public RxMessageStopsAndDetails(String type, Pair<Pair<Stop, Stop>, Pair<Integer, LocalDateTime>> pair) {
    super(type, pair);
  }
}
