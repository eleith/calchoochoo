package com.eleith.calchoochoo.utils.RxBusMessage;

import android.support.v4.util.Pair;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.util.Date;

public class RxMessageStopMethodAndDateTime extends RxMessage<Pair<Integer, LocalDateTime>> {
  public static int DEPARTING = 0;
  public static int ARRIVING = 1;

  public RxMessageStopMethodAndDateTime(String key, Pair<Integer, LocalDateTime> pair) {
    super(key, pair);
  }
}
