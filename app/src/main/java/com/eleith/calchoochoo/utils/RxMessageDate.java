package com.eleith.calchoochoo.utils;

import java.util.Date;

public class RxMessageDate extends RxMessage {

  public RxMessageDate(String type, Date date) {
    this.setType(type);
    this.setMessage(date);
  }

  public Date getMessageDate() {
    return (Date) super.getMessage();
  }
}
