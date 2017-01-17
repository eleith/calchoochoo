package com.eleith.calchoochoo.data;

import android.database.Cursor;
import android.support.annotation.Nullable;

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

  private static final ArrayList<Stop> allDirectionalStops = new ArrayList<>(SQLite.select().from(Stop.class)
        .where(Stop_Table.stop_code.isNot(""))
        .and(Stop_Table.platform_code.isNot(""))
        .queryList());

  private static final ArrayList<FareAttributes> allFareAttributes = new ArrayList<>(SQLite.select().from(FareAttributes.class).queryList());

  private static final ArrayList<FareRules> allFareRules = new ArrayList<>(SQLite.select().from(FareRules.class).queryList());

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

  public static ArrayList<FareRules> getAllRules() {
    return allFareRules;
  }

  public static ArrayList<FareAttributes> getAllFareAttributes() {
    return allFareAttributes;
  }

  @Nullable
  public static Stop getStopById(String stop_id) {
    for (Stop stop : allDirectionalStops) {
      if (stop.stop_id.equals(stop_id)) {
        return stop;
      }
    }
    return null;
  }

  @Nullable
  public static Routes getRouteById(String route_id) {
    for (Routes route : allRoutes) {
      if (route_id.equals(route.route_id)) {
        return route;
      }
    }
    return null;
  }

  @Nullable
  public static FareAttributes getFareAttributesByRoute(String route_id, int origin_zone, int destination_zone) {
    for (FareRules fairRules : allFareRules) {
      if (fairRules.route_id.equals(route_id) && fairRules.destination_id == 0 && fairRules.origin_id == origin_zone) {
        return getFareAttributesById(fairRules.fare_id);
      }
    }
    return null;
  }

  @Nullable
  public static FareAttributes getFareAttributesById(String fare_id) {
    for (FareAttributes fareAttributes : allFareAttributes)
      if (fareAttributes.fare_id.equals(fare_id)) {
        return fareAttributes;
      }
    return null;
  }

  public static ArrayList<PossibleTrip> findTrips(Stop source, Stop destination, LocalDateTime dateTime, Boolean arriving) {
    ArrayList<PossibleTrip> possibleTrips = new ArrayList<>();
    String calendarFilter = " AND calendar.sunday = 1";

    switch(dateTime.getDayOfWeek()) {
      case 1: calendarFilter = "  AND calendar.monday = 1 "; break;
      case 2: calendarFilter = "  AND calendar.tuesday = 1 "; break;
      case 3: calendarFilter = "  AND calendar.wednesday = 1 "; break;
      case 5: calendarFilter = "  AND calendar.thursday = 1 "; break;
      case 6: calendarFilter = "  AND calendar.friday = 1 "; break;
      case 7: calendarFilter = "  AND calendar.saturday = 1 "; break;
    }

    String query = "SELECT " +
    "routes.route_id as route_id, " +
    "fare_attributes.price as price, " +
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
      calendarFilter; // AND calendar.sunday = 1

    String[] args = {source.stop_id, destination.stop_id};
    Cursor cursor = FlowManager.getDatabase(CaltrainDatabase.class).getWritableDatabase().rawQuery(query, args);

    while(cursor.moveToNext()) {
      Float price = cursor.getFloat(cursor.getColumnIndex("price"));

      String routeId = cursor.getString(cursor.getColumnIndex("route_id"));
      String tripId = cursor.getString(cursor.getColumnIndex("st1__trip_id"));
      String firstStopId = cursor.getString(cursor.getColumnIndex("st1__stop_id"));
      String lastStopId = cursor.getString(cursor.getColumnIndex("st2__stop_id"));
      Integer numberOfStops = cursor.getInt(cursor.getColumnIndex("st1__stop_sequence")) - cursor.getInt(cursor.getColumnIndex("st2__stop_sequence"));

      LocalTime arrivalTime = new LocalTime(cursor.getString(cursor.getColumnIndex("st1__arrival_time")).replaceFirst("^24:", "01:"));
      LocalTime departureTime = new LocalTime(cursor.getString(cursor.getColumnIndex("st2__departure_time")).replaceFirst("^24:", "01:"));

      PossibleTrip possibleTrip = new PossibleTrip();
      possibleTrip.setArrivalTime(arrivalTime);
      possibleTrip.setDepartureTime(departureTime);
      possibleTrip.setPrice(price);
      possibleTrip.setNumberOfStops(numberOfStops);
      possibleTrip.setFirstStopId(firstStopId);
      possibleTrip.setLastStopId(lastStopId);
      possibleTrip.setTripId(tripId);
      possibleTrip.setRouteId(routeId);

      if (arriving) {
        if (arrivalTime.isBefore(dateTime.toLocalTime().plusMinutes(1))) {
          possibleTrips.add(possibleTrip);
        }
      } else {
        if (departureTime.isBefore(dateTime.toLocalTime().minusMinutes(1))) {
          possibleTrips.add(possibleTrip);
        }
      }
    }

    return possibleTrips;
  }
}
