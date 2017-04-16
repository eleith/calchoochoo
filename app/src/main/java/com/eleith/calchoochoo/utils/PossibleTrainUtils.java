package com.eleith.calchoochoo.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.eleith.calchoochoo.data.PossibleTrain;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;

public class PossibleTrainUtils {

  public static ArrayList<PossibleTrain> getPossibleTrainFromCursor(Cursor cursor) {
    ArrayList<PossibleTrain> possibleTrains = new ArrayList<>();

    while (cursor != null && cursor.moveToNext()) {
      PossibleTrain possibleTrain = new PossibleTrain();

      String routeId = cursor.getString(cursor.getColumnIndex("route_id"));
      String tripId = cursor.getString(cursor.getColumnIndex("st1__trip_id"));
      String stopParentId = cursor.getString(cursor.getColumnIndex("st1__parent_station"));
      String stopId = cursor.getString(cursor.getColumnIndex("st1__stop_id"));
      String routeLongName = cursor.getString(cursor.getColumnIndex("route_long_name"));
      String tripShortName = cursor.getString(cursor.getColumnIndex("trip_short_name"));
      int tripDirectionId = cursor.getInt(cursor.getColumnIndex("trip_direction_id"));
      LocalTime departureTime = new LocalTime(DataStringUtils.adjustLateTimes(cursor.getString(cursor.getColumnIndex("st1__departure_time"))));
      LocalTime arrivalTime = new LocalTime(DataStringUtils.adjustLateTimes(cursor.getString(cursor.getColumnIndex("st1__arrival_time"))));

      possibleTrain.setRouteLongName(routeLongName);
      possibleTrain.setRouteId(routeId);
      possibleTrain.setStopParentId(stopParentId);
      possibleTrain.setStopId(stopId);
      possibleTrain.setDepartureTime(departureTime);
      possibleTrain.setArrivalTime(arrivalTime);
      possibleTrain.setTripDirectionId(tripDirectionId);
      possibleTrain.setTripShortName(tripShortName);
      possibleTrain.setTripId(tripId);

      possibleTrains.add(possibleTrain);
    }

    return possibleTrains;
  }

  public static Cursor getPossibleTrainQuery(SQLiteDatabase db, String stop_id, Long dateTimeString) {
    String query = "SELECT " +
        "trips.direction_id as trip_direction_id, " +
        "trips.trip_short_name as trip_short_name, " +
        "routes.route_id as route_id, " +
        "routes.route_long_name as route_long_name, " +
        "st1.trip_id as st1__trip_id, st1.arrival_time as st1__arrival_time, st1.departure_time as st1__departure_time, " +
        "st1.stop_id as st1__stop_id, st1.stop_sequence as st1__stop_sequence, st1.parent_station as st1__parent_station " +
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

  public static ArrayList<PossibleTrain> filterByDateTimeAndDirection(ArrayList<PossibleTrain> possibleTrains, LocalDateTime dateTime, int direction) {
    ArrayList<PossibleTrain> possibleTrainsFiltered = new ArrayList<>();

    for (PossibleTrain possibleTrain : possibleTrains) {
      if (possibleTrain.getDepartureTime().isAfter(dateTime.toLocalTime())) {
        if (possibleTrain.getTripDirectionId() == direction) {
          possibleTrainsFiltered.add(possibleTrain);
        }
      }
    }

    return possibleTrainsFiltered;
  }
}
