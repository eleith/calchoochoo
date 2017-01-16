package com.eleith.calchoochoo.data;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

@Parcel(analyze = FareAttributes.class)
@Table(database = CaltrainDatabase.class, name = "fare_attributes")
public class FareAttributes extends BaseModel {
  @PrimaryKey
  @Column
  public String fare_id;

  @Column
  public float price;

  @Column
  public int currency_type;

  @Column
  public int payment_method;

  @Column
  public int transfers;

  @Column
  public int transfer_duration;
}
