package com.eleith.calchoochoo.data;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.LocalDate;
import org.parceler.Parcel;

@Parcel(analyze = CalendarDates.class)
@Table(database = CaltrainDatabase.class, name = "calendar_dates")
public class CalendarDates extends BaseModel {
  @PrimaryKey
  @Column
  String service_id;

  @Column(typeConverter = LocalDateConverter.class)
  LocalDate date;

  @Column
  int exception_type;
}
