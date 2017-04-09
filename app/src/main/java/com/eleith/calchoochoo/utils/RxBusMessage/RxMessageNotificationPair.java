package com.eleith.calchoochoo.utils.RxBusMessage;

import android.support.v4.util.Pair;

public class RxMessageNotificationPair extends RxMessage<Pair<Integer, Integer>> {

    public RxMessageNotificationPair(String key, Pair<Integer, Integer> message) {
        super(key, message);
    }
}
