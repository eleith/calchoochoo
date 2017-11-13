package com.eleith.calchoochoo.data;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class ChooChooDatabase extends SQLiteAssetHelper {

  private static final String DATABASE_NAME = "caltrain-10.01.2017.db";
  private static final int DATABASE_VERSION = 4;

  public ChooChooDatabase(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }
}
