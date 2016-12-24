package com.eleith.calchoochoo.utils;

import android.support.v4.util.Pair;

public class RxMessagePair<F, S> extends RxMessage {

  public RxMessagePair(String type, Pair<F, S> message) {
    this.setType(type);
    this.setMessage(message);
  }

  @SuppressWarnings("unchecked")
  public Pair<F, S> getMessagePair() {
    return (Pair<F, S>) super.getMessage();
  }
}
