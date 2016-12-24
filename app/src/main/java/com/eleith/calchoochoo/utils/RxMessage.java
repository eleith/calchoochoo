package com.eleith.calchoochoo.utils;

public class RxMessage {
  private String type = "";
  private Object message;

  RxMessage() {

  }

  public RxMessage(String type, Object message) {
    this.type = type;
    this.message = message;
  }

  public RxMessage(String type) {
    this.type = type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setMessage(Object message) {
    this.message = message;
  }

  public String getType() {
    return this.type;
  }

  public Object getMessage() {
    return this.message;
  }
}
