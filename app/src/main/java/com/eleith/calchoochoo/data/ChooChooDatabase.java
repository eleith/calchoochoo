package com.eleith.calchoochoo.data;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class ChooChooDatabase extends SQLiteAssetHelper {

  private static final String DATABASE_NAME = "caltrain2.db";
  private static final int DATABASE_VERSION = 2;

  public ChooChooDatabase(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }
}
