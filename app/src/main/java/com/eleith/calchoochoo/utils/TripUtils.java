package com.eleith.calchoochoo.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.eleith.calchoochoo.data.Trips;

import java.util.ArrayList;

public class TripUtils {
  public static final int DIRECTION_SOUTH = 1;
  public static final int DIRECTION_NORTH = 0;

  public static ArrayList<Trips> getTripsFromCursor(Cursor cursor) {
    ArrayList<Trips> trips = new ArrayList<>();

    while (cursor.moveToNext()) {
      Trips trip = getTripFromCursor(cursor);
      trips.add(trip);
    }

    return trips;
  }

  @Nullable
  public static Trips getTripFromCursor(Cursor cursor) {
    Trips trip = new Trips();

    if (cursor.moveToNext()) {
      trip.route_id = cursor.getString(cursor.getColumnIndex("route_id"));
      trip.service_id = cursor.getString(cursor.getColumnIndex("service_id"));
      trip.trip_headsign = cursor.getString(cursor.getColumnIndex("trip_headsign"));
      trip.trip_short_name = cursor.getString(cursor.getColumnIndex("trip_short_name"));
      trip.direction_id = cursor.getInt(cursor.getColumnIndex("direction_id"));
      trip.shape_id = cursor.getString(cursor.getColumnIndex("shape_id"));
      trip.wheelchar_accessible = cursor.getInt(cursor.getColumnIndex("wheelchar_accessible"));
      trip.bikes_allowed = cursor.getInt(cursor.getColumnIndex("bikes_allowed"));
      trip.trip_id = cursor.getString(cursor.getColumnIndex("trip_id"));

      return trip;
    } else {
      return null;
    }
  }

  @Nullable
  public static Trips getTripById(ArrayList<Trips> allTrips, String trip_id) {
    for (Trips trip : allTrips) {
      if (trip.trip_id.equals(trip_id)) {
        return trip;
      }
    }
    return null;
  }
}
