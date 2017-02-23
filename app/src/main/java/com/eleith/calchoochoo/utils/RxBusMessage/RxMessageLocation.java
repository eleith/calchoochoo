package com.eleith.calchoochoo.utils.RxBusMessage;


import android.location.Location;

public class RxMessageLocation extends RxMessage<Location>{
  public RxMessageLocation(String key, Location location) {
    super(key, location);
  }
}
