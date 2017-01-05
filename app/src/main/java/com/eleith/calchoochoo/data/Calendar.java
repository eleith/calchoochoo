package com.eleith.calchoochoo.data;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.LocalDate;
import org.parceler.Parcel;

@Parcel(analyze = Calendar.class)
@Table(database = CaltrainDatabase.class, name = "calendar")
public class Calendar extends BaseModel {
  @PrimaryKey
  @Column
  String service_id;

  @Column
  int monday;

  @Column
  int tuesday;

  @Column
  int wednesday;

  @Column
  int thursday;

  @Column
  int friday;

  @Column
  int saturday;

  @Column
  int sunday;

  @Column(typeConverter = LocalDateConverter.class)
  LocalDate start_date;

  @Column(typeConverter = LocalDateConverter.class)
  LocalDate end_date;
}
