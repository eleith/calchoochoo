package com.eleith.calchoochoo.data;

import android.database.Cursor;
import android.support.v4.util.Pair;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;

public class Queries {
  private static final ArrayList<Stop> allStops = new ArrayList<>(SQLite.select().from(Stop.class)
        .where(Stop_Table.stop_code.is(""))
        .and(Stop_Table.platform_code.is(""))
        .queryList());

  private static final ArrayList<Routes> allRoutes = new ArrayList<>(SQLite.select().from(Routes.class).queryList());

  private static final ArrayList<Trips> allTrips = new ArrayList<>(SQLite.select().from(Trips.class).queryList());

  public static ArrayList<Stop> getAllStops() {
    return allStops;
  }

  public static ArrayList<Routes> getAllRoutes() {
    return allRoutes;
  }

  public static ArrayList<Trips> getAllTrips() {
    return allTrips;
  }

  public static ArrayList<Pair<StopTimes, StopTimes>> findRoute(Stop source, Stop destination, LocalDateTime dateTime) {
    ArrayList<Pair<StopTimes, StopTimes>> stopTimesPairs = new ArrayList<>();

    String query = "SELECT " +
    "st1.platform_code as st1__platform_code, st1.trip_id as st1__trip_id, st1.arrival_time as st1__arrival_time, st1.departure_time as st1__departure_time, " +
    "st1.stop_id as st1__stop_id, st1.stop_sequence as st1__stop_sequence, st1.pickup_time as st1__pickup_time, st1.drop_off_type as st1__drop_off_type, " +
    "st2.platform_code as st2__platform_code, st2.trip_id as st2__trip_id, st2.arrival_time as st2__arrival_time, st2.departure_time as st2__departure_time, " +
    "st2.stop_id as st2__stop_id, st2.stop_sequence as st2__stop_sequence, st2.pickup_time as st2__pickup_time, st2.drop_off_type as st2__drop_off_type " +
      "FROM " +
      "    (SELECT * " +
      "    FROM stops, stop_times " +
      "    WHERE " +
      "     stop_times.stop_id = stops.stop_id " +
      "     AND stops.parent_station = ?) AS st1, " +
      "    (Select * " +
      "    FROM stops, stop_times " +
      "    WHERE " +
      "     stop_times.stop_id = stops.stop_id " +
      "     AND stops.parent_station = ?) AS st2, " +
      "  trips, " +
      "  routes, " +
      "  calendar " +
      "WHERE st1.trip_id = st2.trip_id " +
      "  AND st1.platform_code = st2.platform_code " +
      "  AND st1.stop_sequence < st2.stop_sequence " +
      "  AND trips.trip_id = st1.trip_id " +
      "  AND trips.route_id = routes.route_id " +
      "  AND calendar.service_id = trips.service_id " +
      "  AND calendar.monday = 1 ";

    String[] args = {source.getId(), destination.getId()};
    Cursor cursor = FlowManager.getDatabase(CaltrainDatabase.class).getWritableDatabase().rawQuery(query, args);

    while(cursor.moveToNext()) {
      StopTimes startStopTimes = new StopTimes();
      StopTimes endStopTimes = new StopTimes();

      startStopTimes.trip_id = cursor.getString(cursor.getColumnIndex("st1__trip_id"));
      startStopTimes.arrival_time = new LocalTime(cursor.getString(cursor.getColumnIndex("st1__arrival_time")).replaceFirst("^24:", "01:"));
      startStopTimes.departure_time = new LocalTime(cursor.getString(cursor.getColumnIndex("st1__departure_time")).replaceFirst("^24:", "01:"));
      startStopTimes.stop_id = cursor.getInt(cursor.getColumnIndex("st1__stop_id"));
      startStopTimes.stop_sequence = cursor.getInt(cursor.getColumnIndex("st1__stop_sequence"));
      startStopTimes.pickup_time = cursor.getInt(cursor.getColumnIndex("st1__pickup_time"));
      startStopTimes.drop_off_type = cursor.getInt(cursor.getColumnIndex("st1__drop_off_type"));

      endStopTimes.trip_id = cursor.getString(cursor.getColumnIndex("st2__trip_id"));
      endStopTimes.arrival_time = new LocalTime(cursor.getString(cursor.getColumnIndex("st2__arrival_time")).replaceFirst("^24:", "01:"));
      endStopTimes.departure_time = new LocalTime(cursor.getString(cursor.getColumnIndex("st2__departure_time")).replaceFirst("^24:", "01:"));
      endStopTimes.stop_id = cursor.getInt(cursor.getColumnIndex("st2__stop_id"));
      endStopTimes.stop_sequence = cursor.getInt(cursor.getColumnIndex("st2__stop_sequence"));
      endStopTimes.pickup_time = cursor.getInt(cursor.getColumnIndex("st2__pickup_time"));
      endStopTimes.drop_off_type = cursor.getInt(cursor.getColumnIndex("st2__drop_off_type"));

      stopTimesPairs.add(new Pair<>(startStopTimes, endStopTimes));
    }

    return stopTimesPairs;
  }
}
