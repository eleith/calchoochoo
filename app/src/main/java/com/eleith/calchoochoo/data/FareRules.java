package com.eleith.calchoochoo.data;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;


@Parcel(analyze = FareRules.class)
@Table(database = CaltrainDatabase.class, name = "fare_rules")
public class FareRules extends BaseModel {
  @PrimaryKey
  @Column
  public String fare_id;

  @Column
  public String route_id;

  @Column
  public int origin_id;

  @Column
  public int destination_id;
}
