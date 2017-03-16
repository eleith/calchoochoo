package com.eleith.calchoochoo.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.eleith.calchoochoo.data.PossibleTrip;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;

public class PossibleTripUtils {
  @Nullable
  public static PossibleTrip getPossibleTripFromCursor(Cursor cursor) {

    if (cursor.moveToNext()) {
      PossibleTrip possibleTrip = new PossibleTrip();
      Float price = cursor.getFloat(cursor.getColumnIndex("price"));

      String routeId = cursor.getString(cursor.getColumnIndex("route_id"));
      String stop1Id = cursor.getString(cursor.getColumnIndex("st1__stop_id"));
      String stop2Id = cursor.getString(cursor.getColumnIndex("st2__stop_id"));
      String stop1Name = cursor.getString(cursor.getColumnIndex("st1__stop_name"));
      String stop2Name = cursor.getString(cursor.getColumnIndex("st2__stop_name"));
      String tripId = cursor.getString(cursor.getColumnIndex("st1__trip_id"));
      String routeLongName = cursor.getString(cursor.getColumnIndex("route_long_name"));
      Integer stopOneSequence = cursor.getInt(cursor.getColumnIndex("st1__stop_sequence"));
      Integer stopTwoSequence = cursor.getInt(cursor.getColumnIndex("st2__stop_sequence"));

      LocalTime stopOneDepartureTime = new LocalTime(cursor.getString(cursor.getColumnIndex("st1__departure_time")).replaceFirst("^24:", "01:"));
      LocalTime stopOneArrivalTime = new LocalTime(cursor.getString(cursor.getColumnIndex("st1__arrival_time")).replaceFirst("^24:", "01:"));
      LocalTime stopTwoDepartureTime = new LocalTime(cursor.getString(cursor.getColumnIndex("st2__departure_time")).replaceFirst("^24:", "01:"));
      LocalTime stopTwoArrivalTime = new LocalTime(cursor.getString(cursor.getColumnIndex("st2__arrival_time")).replaceFirst("^24:", "01:"));

      if (stopOneSequence < stopTwoSequence) {
        possibleTrip.setArrivalTime(stopOneDepartureTime);
        possibleTrip.setDepartureTime(stopTwoArrivalTime);
        possibleTrip.setFirstStopSequence(stopOneSequence);
        possibleTrip.setLastStopSequence(stopTwoSequence);
        possibleTrip.setFirstStopId(stop1Id);
        possibleTrip.setLastStopId(stop2Id);
        possibleTrip.setFirstStopName(stop1Name);
        possibleTrip.setLastStopName(stop2Name);
      } else {
        possibleTrip.setArrivalTime(stopTwoDepartureTime);
        possibleTrip.setDepartureTime(stopOneArrivalTime);
        possibleTrip.setFirstStopSequence(stopTwoSequence);
        possibleTrip.setLastStopSequence(stopOneSequence);
        possibleTrip.setFirstStopId(stop2Id);
        possibleTrip.setLastStopId(stop1Id);
        possibleTrip.setFirstStopName(stop2Name);
        possibleTrip.setLastStopName(stop1Name);
      }

      possibleTrip.setRouteLongName(routeLongName);
      possibleTrip.setPrice(price);
      possibleTrip.setTripId(tripId);
      possibleTrip.setRouteId(routeId);

      return possibleTrip;
    }

    return null;
  }

  public static ArrayList<PossibleTrip> getPossibleTripsFromCursor(Cursor cursor) {
    ArrayList<PossibleTrip> possibleTrips = new ArrayList<>();

    while (cursor.getPosition() < cursor.getCount()) {
      PossibleTrip possibleTrip = getPossibleTripFromCursor(cursor);
      if (possibleTrip != null) {
        possibleTrips.add(possibleTrip);
      }
    }

    return possibleTrips;
  }

  public static Cursor getPossibleTripQuery(SQLiteDatabase db, String trip_id, String stop1_id, String stop2_id) {
    String query = "SELECT " +
        "routes.route_id as route_id, " +
        "routes.route_long_name as route_long_name, " +
        "fare_attributes.price as price, " +
        "st1.trip_id as st1__trip_id, st1.arrival_time as st1__arrival_time, st1.departure_time as st1__departure_time, " +
        "st1.stop_name as st1__stop_name, st1.stop_id as st1__stop_id, st1.stop_sequence as st1__stop_sequence, " +
        "st2.trip_id as st2__trip_id, st2.arrival_time as st2__arrival_time, st2.departure_time as st2__departure_time, " +
        "st2.stop_name as st2__stop_name, st2.stop_id as st2__stop_id, st2.stop_sequence as st2__stop_sequence " +
        "FROM " +
        "    (SELECT * " +
        "    FROM stops, stop_times " +
        "    WHERE " +
        "     stops.stop_id = stop_times.stop_id " +
        "     AND stop_times.trip_id = ? " +
        "     AND stops.parent_station = ?) AS st1, " +
        "    (Select * " +
        "    FROM stops, stop_times " +
        "    WHERE " +
        "     stops.stop_id = stop_times.stop_id " +
        "     AND stop_times.trip_id = ? " +
        "     AND stops.parent_station = ?) AS st2, " +
        "  trips, " +
        "  routes, " +
        "  calendar, " +
        "  fare_rules, " +
        "  fare_attributes " +
        "WHERE st1.trip_id = st2.trip_id " +
        "  AND trips.route_id = routes.route_id " +
        "  AND calendar.service_id = trips.service_id " +
        "  AND fare_rules.origin_id = st1.zone_id " +
        "  AND fare_rules.destination_id = st2.zone_id " +
        "  AND fare_rules.route_id = routes.route_id " +
        "  AND fare_rules.fare_id = fare_attributes.fare_id ";
    String[] args = {trip_id, stop1_id, trip_id, stop2_id};
    return db.rawQuery(query, args);
  }

  public static Cursor getPossibleTripsQuery(SQLiteDatabase db, Long dateTime, String stop1_id, String stop2_id) {
    String calendarFilter = CalendarDateUtils.getCalendarFilter(db, dateTime);
    String query = "SELECT " +
        "routes.route_id as route_id, " +
        "routes.route_long_name as route_long_name, " +
        "fare_attributes.price as price, " +
        "st1.stop_name as st1__stop_name, st1.platform_code as st1__platform_code, st1.trip_id as st1__trip_id, st1.arrival_time as st1__arrival_time, st1.departure_time as st1__departure_time, " +
        "st1.stop_id as st1__stop_id, st1.stop_sequence as st1__stop_sequence, st1.pickup_time as st1__pickup_time, st1.drop_off_type as st1__drop_off_type, " +
        "st2.stop_name as st2__stop_name, st2.platform_code as st2__platform_code, st2.trip_id as st2__trip_id, st2.arrival_time as st2__arrival_time, st2.departure_time as st2__departure_time, " +
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
        "  calendar, " +
        "  fare_rules, " +
        "  fare_attributes " +
        "WHERE st1.trip_id = st2.trip_id " +
        "  AND st1.platform_code = st2.platform_code " +
        "  AND st1.stop_sequence < st2.stop_sequence " +
        "  AND trips.trip_id = st1.trip_id " +
        "  AND trips.route_id = routes.route_id " +
        "  AND calendar.service_id = trips.service_id " +
        "  AND fare_rules.origin_id = st1.zone_id " +
        "  AND fare_rules.destination_id = st2.zone_id " +
        "  AND fare_rules.route_id = routes.route_id " +
        "  AND fare_rules.fare_id = fare_attributes.fare_id " +
        calendarFilter;
    String[] args = {stop1_id, stop2_id};
    return db.rawQuery(query, args);
  }

  public static ArrayList<PossibleTrip> filterByDateTimeAndDirection(ArrayList<PossibleTrip> possibleTrips, LocalDateTime dateTime, Boolean arriving) {
    ArrayList<PossibleTrip> possibleTripsFiltered = new ArrayList<>();

    for (PossibleTrip possibleTrip : possibleTrips) {
      LocalTime departureTime = possibleTrip.getDepartureTime();
      LocalTime arrivalTime = possibleTrip.getArrivalTime();

      if (arriving) {
        if (departureTime.isBefore(dateTime.toLocalTime()) && departureTime.plusHours(3).isAfter(dateTime.toLocalTime())) {
          possibleTripsFiltered.add(possibleTrip);
        }
      } else {
        if (arrivalTime.isAfter(dateTime.toLocalTime())) {
          possibleTripsFiltered.add(possibleTrip);
        }
      }
    }

    return possibleTripsFiltered;
  }
}
