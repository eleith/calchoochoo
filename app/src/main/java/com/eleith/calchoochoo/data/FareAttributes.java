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
  String fare_id;

  @Column
  float price;

  @Column
  int currency_type;

  @Column
  int payment_method;

  @Column
  int transfers;

  @Column
  int transfer_duration;
}
