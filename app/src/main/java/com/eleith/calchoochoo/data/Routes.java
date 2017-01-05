package com.eleith.calchoochoo.data;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

@Parcel(analyze = Routes.class)
@Table(database = CaltrainDatabase.class, name = "routes")
public class Routes extends BaseModel {
  @PrimaryKey
  @Column
  String route_id;

  @Column
  String route_short_name;

  @Column
  String route_long_name;

  @Column
  int route_type;

  @Column
  String route_color;
}
