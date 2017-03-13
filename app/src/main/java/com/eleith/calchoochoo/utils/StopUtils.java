package com.eleith.calchoochoo.utils;

import android.database.Cursor;
import android.location.Location;
import android.support.annotation.Nullable;

import com.eleith.calchoochoo.data.Stop;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

public class StopUtils {
  static public ArrayList<Stop> filterByFuzzySearch(ArrayList<Stop> stops, String query) {
    ArrayList<Stop> filteredStops;
    if (query != null && !query.equals("")) {
      filteredStops = new ArrayList<Stop>();
      final HashMap<String, Integer> stopFuzzyScores = new HashMap<String, Integer>();
      for (Stop stop : stops) {
        int fuzzyScore = StringUtils.getFuzzyDistance(stop.stop_name, query, Locale.getDefault());
        if (fuzzyScore >= query.length()) {
          stopFuzzyScores.put(stop.stop_id, fuzzyScore);
          filteredStops.add(stop);
        }
      }
      Collections.sort(filteredStops, new Comparator<Stop>() {
        @Override
        public int compare(Stop lhs, Stop rhs) {
          int rightFuzzyScore = stopFuzzyScores.get(rhs.stop_id);
          int leftFuzzyScore = stopFuzzyScores.get(lhs.stop_id);
          return Integer.compare(rightFuzzyScore, leftFuzzyScore);
        }
      });
    } else {
      filteredStops = stops;
      Collections.sort(filteredStops, Stop.nameComparator);
    }

    return filteredStops;
  }

  public static ArrayList<Stop> getStopsFromCursor(Cursor cursor) {
    ArrayList<Stop> stops = new ArrayList<>();

    while (cursor.moveToNext()) {
      Stop stop = getStopFromCursor(cursor);
      stops.add(stop);
    }

    cursor.close();
    return stops;
  }

  @Nullable
  public static Stop getStopFromCursor(Cursor cursor) {
    Stop stop = new Stop();

    if (!cursor.isAfterLast()) {
      stop.stop_name = cursor.getString(cursor.getColumnIndex("stop_name"));
      stop.stop_id = cursor.getString(cursor.getColumnIndex("stop_id"));
      stop.stop_lat = cursor.getFloat(cursor.getColumnIndex("stop_lat"));
      stop.stop_lon = cursor.getFloat(cursor.getColumnIndex("stop_lon"));
      stop.parent_station = cursor.getString(cursor.getColumnIndex("parent_station"));
      stop.stop_url = cursor.getString(cursor.getColumnIndex("stop_url"));
      stop.stop_code = cursor.getString(cursor.getColumnIndex("stop_code"));
      stop.platform_code = cursor.getString(cursor.getColumnIndex("platform_code"));
      stop.zone_id = cursor.getInt(cursor.getColumnIndex("zone_id"));
      stop.wheelchar_board = cursor.getInt(cursor.getColumnIndex("wheelchar_board"));

      return stop;
    } else {
      return null;
    }
  }

  @Nullable
  public static Stop getParentStopById(ArrayList<Stop> allParentStops, String stop_id) {
    for (Stop stop : allParentStops) {
      if (stop.stop_id.equals(stop_id)) {
        return stop;
      }
    }
    return null;
  }

  @Nullable
  public static Stop findStopClosestTo(ArrayList<Stop> allParentStops, Location location) {
    Float smallestDistance = null;
    Stop nearestStop = null;

    for (Stop stop : allParentStops) {
      Float distance = Math.abs(location.distanceTo(stop.getLocation()));
      if (smallestDistance == null || distance < smallestDistance) {
        smallestDistance = distance;
        nearestStop = stop;
      }
    }
    return nearestStop;
  }
}
