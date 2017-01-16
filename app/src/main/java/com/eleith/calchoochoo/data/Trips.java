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
  public String route_id;

  @Column
  public String service_id;

  @PrimaryKey
  @Column
  public String trip_id;

  @Column
  public String trip_headsign;

  @Column
  public String trip_short_name;

  @Column
  public int direction_id;

  @Column
  public String shape_id;

  @Column
  public int wheelchar_accessible;

  @Column
  public int bikes_allowed;
}
