package com.eleith.calchoochoo.data;

import org.parceler.Parcel;

@Parcel(analyze = FareAttributes.class)
public class FareAttributes {
  public String fare_id;
  public float price;
  public int currency_type;
  public int payment_method;
  public int transfers;
  public int transfer_duration;
}
