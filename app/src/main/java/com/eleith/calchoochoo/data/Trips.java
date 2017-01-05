package com.eleith.calchoochoo.data;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

@Parcel(analyze = Trips.class)
@Table(database = CaltrainDatabase.class, name = "trips")
public class Trips extends BaseModel {
  @Column
  String route_id;

  @Column
  String service_id;

  @PrimaryKey
  @Column
  String trip_id;

  @Column
  String trip_headsign;

  @Column
  String trip_short_name;

  @Column
  int direction_id;

  @Column
  String shape_id;

  @Column
  int wheelchar_accessible;

  @Column
  int bikes_allowed;
}
