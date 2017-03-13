package com.eleith.calchoochoo.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.eleith.calchoochoo.data.PossibleTrain;
import com.eleith.calchoochoo.data.PossibleTrip;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;

public class PossibleTrainUtils {

  public static ArrayList<PossibleTrain> getPossibleTrainFromCursor(Cursor cursor) {
    ArrayList<PossibleTrain> possibleTrains = new ArrayList<>();

    while (cursor.moveToNext()) {
      PossibleTrain possibleTrain = new PossibleTrain();

      String routeId = cursor.getString(cursor.getColumnIndex("route_id"));
      String tripId = cursor.getString(cursor.getColumnIndex("st1__trip_id"));
      String stopId = cursor.getString(cursor.getColumnIndex("st1__stop_id"));
      LocalTime departureTime = new LocalTime(cursor.getString(cursor.getColumnIndex("st1__departure_time")).replaceFirst("^24:", "01:"));
      LocalTime arrivalTime = new LocalTime(cursor.getString(cursor.getColumnIndex("st1__arrival_time")).replaceFirst("^24:", "01:"));

      possibleTrain.setRouteId(routeId);
      possibleTrain.setTripId(tripId);
      possibleTrain.setStopId(stopId);
      possibleTrain.setDepartureTime(departureTime);
      possibleTrain.setArrivalTime(arrivalTime);

      possibleTrains.add(possibleTrain);
    }

    return possibleTrains;
  }

  public static Cursor getPossibleTrainQuery(SQLiteDatabase db, String stop_id, Long dateTimeString) {
    String query = "SELECT " +
        "routes.route_id as route_id, " +
        "st1.trip_id as st1__trip_id, st1.arrival_time as st1__arrival_time, st1.departure_time as st1__departure_time, " +
        "st1.stop_id as st1__stop_id, st1.stop_sequence as st1__stop_sequence " +
        "FROM " +
        "    (SELECT * " +
        "    FROM stops, stop_times " +
        "    WHERE " +
        "     stop_times.stop_id = stops.stop_id " +
        "     AND stops.parent_station = ?) AS st1, " +
        "  trips, " +
        "  routes, " +
        "  calendar " +
        "WHERE trips.trip_id = st1.trip_id " +
        "  AND trips.route_id = routes.route_id " +
        "  AND calendar.service_id = trips.service_id " +
        CalendarDateUtils.getCalendarFilter(db, dateTimeString) +
        "ORDER BY st1__departure_time ASC ";
    String[] args = {stop_id};
    return db.rawQuery(query, args);
  }

  public static ArrayList<PossibleTrain> filterByDateTime(ArrayList<PossibleTrain> possibleTrains, LocalDateTime dateTime) {
    ArrayList<PossibleTrain> possibleTrainsFiltered = new ArrayList<>();

    for (PossibleTrain possibleTrain : possibleTrains) {
      if (possibleTrain.getDepartureTime().isAfter(dateTime.toLocalTime())) {
        possibleTrainsFiltered.add(possibleTrain);
      }
    }

    return possibleTrainsFiltered;
  }
}
