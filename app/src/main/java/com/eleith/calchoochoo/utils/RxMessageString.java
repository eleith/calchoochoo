package com.eleith.calchoochoo.utils;

public class RxMessageString extends RxMessage {

  public RxMessageString(String type, String message) {
    this.setType(type);
    this.setMessage(message);
  }

  public String getMessageString() {
    return (String) super.getMessage();
  }
}
