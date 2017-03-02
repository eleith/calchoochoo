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
  public String service_id;

  @Column
  public int monday;

  @Column
  public int tuesday;

  @Column
  public int wednesday;

  @Column
  public int thursday;

  @Column
  public int friday;

  @Column
  public int saturday;

  @Column
  public int sunday;

  @Column(typeConverter = LocalDateConverter.class)
  public LocalDate start_date;

  @Column(typeConverter = LocalDateConverter.class)
  public LocalDate end_date;
}
