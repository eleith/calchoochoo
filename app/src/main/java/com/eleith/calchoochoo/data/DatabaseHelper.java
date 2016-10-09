package com.eleith.calchoochoo.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteAssetHelper {

  private static final String DATABASE_NAME = "calchoochoo.sqlite";
  private static final int DATABASE_VERSION = 2;
  private ArrayList<Stop> stops;

  public DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    setForcedUpgrade();
  }

  public ArrayList<Stop> getAllStations() {
    if (this.stops == null) {
      this.stops = new ArrayList<Stop>();
      SQLiteDatabase db = this.getReadableDatabase();

      String[] returnColumns = {
          DatabaseTableStops.COLUMN_STOP_NAME,
          DatabaseTableStops.COLUMN_STOP_ID,
          DatabaseTableStops.COLUMN_STOP_URL,
          DatabaseTableStops.COLUMN_STOP_LAT,
          DatabaseTableStops.COLUMN_STOP_LON
      };

      String where = DatabaseTableStops.COLUMN_PLATFORM_CODE + " = ? AND " + DatabaseTableStops.COLUMN_STOP_CODE + " = ?";
      String[] whereArgs = {"", ""};

      Cursor cursor = db.query(DatabaseTableStops.TABLE_NAME, returnColumns, where, whereArgs, null, null, null);

      while (cursor.moveToNext()) {
        String name = cursor.getString(cursor.getColumnIndex(DatabaseTableStops.COLUMN_STOP_NAME));
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseTableStops.COLUMN_STOP_ID));
        String url = cursor.getString(cursor.getColumnIndex(DatabaseTableStops.COLUMN_STOP_URL));
        float lat = cursor.getFloat(cursor.getColumnIndex(DatabaseTableStops.COLUMN_STOP_LAT));
        float lon = cursor.getFloat(cursor.getColumnIndex(DatabaseTableStops.COLUMN_STOP_LON));

        this.stops.add(new Stop(id, name, url, lat, lon));
      }

      cursor.close();
    }

    return this.stops;
  }
}
