package com.eleith.calchoochoo.data;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.LocalTime;
import org.parceler.Parcel;

@Parcel(analyze = StopTimes.class)
@Table(database = CaltrainDatabase.class, name = "stop_times")
public class StopTimes extends BaseModel {
  @PrimaryKey
  @Column
  public String trip_id;

  @Column(typeConverter = LocalTimeConverter.class)
  public LocalTime arrival_time;

  @Column(typeConverter = LocalTimeConverter.class)
  public LocalTime departure_time;

  @Column
  public String stop_id;

  @Column
  public int stop_sequence;

  @Column
  public int pickup_time;

  @Column
  public int drop_off_type;
}
