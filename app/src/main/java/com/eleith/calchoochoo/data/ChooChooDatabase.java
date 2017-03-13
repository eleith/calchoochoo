package com.eleith.calchoochoo.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class ChooChooDatabase extends SQLiteAssetHelper {

  private static final String DATABASE_NAME = "caltrain.db";
  private static final int DATABASE_VERSION = 1;

  public ChooChooDatabase(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }
}
