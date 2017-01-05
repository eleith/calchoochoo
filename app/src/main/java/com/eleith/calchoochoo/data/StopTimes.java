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
  String trip_id;

  @Column(typeConverter = LocalTimeConverter.class)
  LocalTime arrival_time;

  @Column(typeConverter = LocalTimeConverter.class)
  LocalTime departure_time;

  @Column
  int stop_id;

  @Column
  int stop_sequence;

  @Column
  int pickup_time;

  @Column
  int drop_off_type;
}
