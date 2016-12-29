package com.eleith.calchoochoo.data;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = CaltrainDatabase.NAME, version = CaltrainDatabase.VERSION)
public class CaltrainDatabase {
  public static final String NAME = "caltrain_sqlite";
  public static final int VERSION = 2;
}
