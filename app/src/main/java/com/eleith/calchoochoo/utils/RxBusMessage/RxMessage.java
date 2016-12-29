package com.eleith.calchoochoo.utils.RxBusMessage;

public class RxMessage<T> {
  private String type = "";
  private T message;

  public RxMessage() {

  }

  public RxMessage(String type, T message) {
    this.type = type;
    this.message = message;
  }

  public RxMessage(String type) {
    this.type = type;
  }

  public String getType() {
    return this.type;
  }

  public Boolean isMessageValidFor(String type) {
    Class testClass = RxMessageKeys.validMessageClassFor(type);
    return getType().equals(type) && this.getClass().equals(testClass);
  }

  public T getMessage() {
    return this.message;
  }
}
