package com.eleith.calchoochoo.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.StopTimes;

import org.joda.time.LocalTime;

import java.util.ArrayList;

public class StopTimesUtils {
  @Nullable
  public static Integer getStopSequence(SQLiteDatabase db, String stop_id) {
    Integer stop_sequence = null;
    String[] projection = {"stop_sequence"};
    String[] selectionArgs = {stop_id};
    Cursor cursor = db.query("stop_times", projection, "stop_id = ?", selectionArgs, null, null, null);

    if (cursor.getCount() > 0) {
      cursor.moveToFirst();
      stop_sequence = cursor.getInt(cursor.getColumnIndex("stop_sequence"));
    }
    cursor.close();
    return stop_sequence;
  }

  @Nullable
  public static Cursor getStopTimesTripQuery(SQLiteDatabase db, String trip_id, String stop1_id, String stop2_id) {
    Integer stop1_sequence = StopTimesUtils.getStopSequence(db, stop1_id);
    Integer stop2_sequence = StopTimesUtils.getStopSequence(db, stop2_id);

    if (stop1_sequence == null || stop2_sequence == null) {
      return null;
    } else {
      Integer higher_stop_sequence = stop1_sequence > stop2_sequence ? stop1_sequence : stop2_sequence;
      Integer lower_stop_sequence = higher_stop_sequence.equals(stop1_sequence) ? stop2_sequence : stop1_sequence;
      Integer direction = stop1_sequence > stop2_sequence ? 0 : 1;

      String query = "SELECT " +
          "  st.trip_id as st__trip_id, st.arrival_time as st__arrival_time, st.departure_time as st__departure_time, " +
          "  st.stop_id as st__stop_id, st.stop_sequence as st__stop_sequence, st.pickup_time as st__pickup_time, st.drop_off_type as st__drop_off_type, " +
          "  s.stop_id as s__stop_id, s.zone_id as s__zone_id, s.stop_name as s__stop_name, s.stop_lat as s__stop_lat, s.stop_lon as s__stop_lon, " +
          "  s.parent_station as s__parent_station, s.stop_url as s__stop_url, s.platform_code as s__platform_code, s.stop_code as s__stop_code " +
          "FROM stops as s, stop_times as st " +
          "WHERE st.trip_id = ? " +
          "  AND s.stop_id = st.stop_id " +
          "  AND st.stop_sequence >= ? " +
          "  AND st.stop_sequence <= ? " +
          "  ORDER BY st.stop_sequence " + ((direction == 1) ? "ASC" : "DESC");

      String[] args = {trip_id, Integer.toString(lower_stop_sequence), Integer.toString(higher_stop_sequence)};
      return db.rawQuery(query, args);
    }
  }

  @Nullable
  public static Cursor getStopsFromStopTimesQuery(SQLiteDatabase db, String trip_id) {
    String query = "SELECT * FROM stops, " +
        " (SELECT " +
        "     st.trip_id as st__trip_id, st.arrival_time as st__arrival_time, st.departure_time as st__departure_time, " +
        "     st.stop_id as st__stop_id, st.stop_sequence as st__stop_sequence, st.pickup_time as st__pickup_time, st.drop_off_type as st__drop_off_type, " +
        "     s.stop_id as s_stop_id, s.zone_id as s_zone_id, s.stop_name as s_stop_name, s.stop_lat as s_stop_lat, s.stop_lon as s_stop_lon, " +
        "     s.parent_station as s_parent_station, s.stop_url as s_stop_url, s.platform_code as s_platform_code, s.stop_code as s_stop_code, s.wheelchar_board as s_wheelchar_board " +
        "   FROM stops as s, stop_times as st " +
        "   WHERE st.trip_id = ? " +
        "   AND s.stop_id = st.stop_id) as stop_in_stop_times " +
        " WHERE stop_id = stop_in_stop_times.s_parent_station " +
        " ORDER BY stop_name ASC";

    String[] args = {trip_id};
    return db.rawQuery(query, args);
  }

  public static ArrayList<StopTimes> getStopTimesTripFromCursor(Cursor cursor) {
    ArrayList<StopTimes> stopAndTimes = new ArrayList<>();

    while (cursor.moveToNext()) {
      StopTimes stopTimes = new StopTimes();
      Stop stop = new Stop();

      stopTimes.arrival_time = new LocalTime(cursor.getString(cursor.getColumnIndex("st__arrival_time")).replaceFirst("^24:", "01:"));
      stopTimes.departure_time = new LocalTime(cursor.getString(cursor.getColumnIndex("st__departure_time")).replaceFirst("^24:", "01:"));
      stopTimes.trip_id = cursor.getString(cursor.getColumnIndex("st__trip_id"));
      stopTimes.stop_id = cursor.getString(cursor.getColumnIndex("st__stop_id"));
      stopTimes.stop_sequence = cursor.getInt(cursor.getColumnIndex("st__stop_sequence"));
      stopTimes.pickup_time = cursor.getInt(cursor.getColumnIndex("st__pickup_time"));
      stopTimes.drop_off_type = cursor.getInt(cursor.getColumnIndex("st__drop_off_type"));

      stop.stop_id = stopTimes.stop_id;
      stop.stop_name = cursor.getString(cursor.getColumnIndex("s__stop_name"));
      stop.stop_code = cursor.getString(cursor.getColumnIndex("s__stop_code"));
      stop.parent_station = cursor.getString(cursor.getColumnIndex("s__parent_station"));
      stop.zone_id = cursor.getInt(cursor.getColumnIndex("s__zone_id"));
      stop.platform_code = cursor.getString(cursor.getColumnIndex("s__platform_code"));
      stop.stop_url = cursor.getString(cursor.getColumnIndex("s__stop_url"));
      stop.stop_lon = cursor.getFloat(cursor.getColumnIndex("s__stop_lon"));
      stop.stop_lat = cursor.getFloat(cursor.getColumnIndex("s__stop_lat"));

      stopTimes.stop = stop;

      stopAndTimes.add(stopTimes);
    }

    return stopAndTimes;
  }
}
