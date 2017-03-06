package com.eleith.calchoochoo.data;

import android.database.Cursor;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;

public class Queries {
  private static final ArrayList<Stop> allStops = new ArrayList<>(SQLite.select().from(Stop.class).queryList());

  private static final ArrayList<Stop> allParentStops = new ArrayList<>(SQLite.select().from(Stop.class)
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

  public static ArrayList<Stop> getAllParentStops() {
    return new ArrayList<>(allParentStops);
  }

  public static ArrayList<Routes> getAllRoutes() {
    return new ArrayList<>(allRoutes);
  }

  public static ArrayList<Trips> getAllTrips() {
    return new ArrayList<>(allTrips);
  }

  public static ArrayList<FareRules> getAllRules() {
    return new ArrayList<>(allFareRules);
  }

  public static ArrayList<FareAttributes> getAllFareAttributes() {
    return new ArrayList<>(allFareAttributes);
  }

  @Nullable
  public static Stop getParentStopById(String stop_id) {
    for (Stop stop : allParentStops) {
      if (stop.stop_id.equals(stop_id)) {
        return stop;
      }
    }
    return null;
  }

  @Nullable
  public static Stop getDirectionalStopById(String stop_id) {
    for (Stop stop : allDirectionalStops) {
      if (stop.stop_id.equals(stop_id)) {
        return stop;
      }
    }
    return null;
  }

  @Nullable
  public static Trips getTripById(String trip_id) {
    for (Trips trip : allTrips) {
      if (trip.trip_id.equals(trip_id)) {
        return trip;
      }
    }
    return null;
  }

  @Nullable
  public static Integer getZoneOfParentStop(String stop_id) {
    for (Stop stop : allDirectionalStops) {
      if (stop.parent_station.equals(stop_id)) {
        return stop.zone_id;
      }
    }
    return null;
  }

  @Nullable
  public static Stop findStopClosestTo(Location location) {
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

  private static boolean isExceptionDate(LocalDateTime dateTime) {
    ArrayList<CalendarDates> exceptions = new ArrayList<>(SQLite.select().from(CalendarDates.class)
        .where(CalendarDates_Table.date.is(dateTime.toLocalDate()))
        .queryList());

    return exceptions.size() > 0;
  }

  private static String getCalendarFilter(LocalDateTime dateTime) {
    String calendarFilter;
    int dayOfWeek = isExceptionDate(dateTime) ? 0 : dateTime.getDayOfWeek();

    switch (dateTime.getDayOfWeek()) {
      case 1:
        calendarFilter = "  AND calendar.monday = 1 ";
        break;
      case 2:
        calendarFilter = "  AND calendar.tuesday = 1 ";
        break;
      case 3:
        calendarFilter = "  AND calendar.wednesday = 1 ";
        break;
      case 5:
        calendarFilter = "  AND calendar.thursday = 1 ";
        break;
      case 6:
        calendarFilter = "  AND calendar.friday = 1 ";
        break;
      case 7:
        calendarFilter = "  AND calendar.saturday = 1 ";
        break;
      default:
        calendarFilter = " AND calendar.sunday = 1 ";
        break;
    }

    return calendarFilter;
  }

  public static ArrayList<PossibleTrain> findNextTrain(Stop stop, LocalDateTime dateTime) {
    ArrayList<PossibleTrain> possibleTrains = new ArrayList<>();
    String calendarFilter = getCalendarFilter(dateTime);
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
        calendarFilter +
        "ORDER BY st1__departure_time ASC ";

    String[] args = {stop.stop_id};
    Cursor cursor = FlowManager.getDatabase(CaltrainDatabase.class).getWritableDatabase().rawQuery(query, args);

    while (cursor.moveToNext()) {
      PossibleTrain possibleTrain = new PossibleTrain();

      String routeId = cursor.getString(cursor.getColumnIndex("route_id"));
      String tripId = cursor.getString(cursor.getColumnIndex("st1__trip_id"));
      String stopId = cursor.getString(cursor.getColumnIndex("st1__stop_id"));
      LocalTime departureTime = new LocalTime(cursor.getString(cursor.getColumnIndex("st1__departure_time")).replaceFirst("^24:", "01:"));
      LocalTime arrivalTime = new LocalTime(cursor.getString(cursor.getColumnIndex("st1__arrival_time")).replaceFirst("^24:", "01:"));

      if (departureTime.isAfter(dateTime.toLocalTime())) {
        possibleTrain.setRouteId(routeId);
        possibleTrain.setTripId(tripId);
        possibleTrain.setStopId(stopId);
        possibleTrain.setDepartureTime(departureTime);
        possibleTrain.setArrivalTime(arrivalTime);

        possibleTrains.add(possibleTrain);
      }
    }

    return possibleTrains;
  }

  @Nullable
  public static PossibleTrip findPossibleTrip(Stop stop1, Stop stop2, String trip_id) {
    PossibleTrip possibleTrip = new PossibleTrip();
    String query = "SELECT " +
        "routes.route_id as route_id, " +
        "fare_attributes.price as price, " +
        "st1.trip_id as st1__trip_id, st1.arrival_time as st1__arrival_time, st1.departure_time as st1__departure_time, " +
        "st1.stop_id as st1__stop_id, st1.stop_sequence as st1__stop_sequence, " +
        "st2.trip_id as st2__trip_id, st2.arrival_time as st2__arrival_time, st2.departure_time as st2__departure_time, " +
        "st2.stop_id as st2__stop_id, st2.stop_sequence as st2__stop_sequence " +
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

    String[] args = {trip_id, stop1.stop_id, trip_id, stop2.stop_id};
    Cursor cursor = FlowManager.getDatabase(CaltrainDatabase.class).getWritableDatabase().rawQuery(query, args);

    if (cursor.moveToFirst()) {
      Float price = cursor.getFloat(cursor.getColumnIndex("price"));

      String routeId = cursor.getString(cursor.getColumnIndex("route_id"));
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
        possibleTrip.setFirstStopId(stop1.stop_id);
        possibleTrip.setLastStopId(stop2.stop_id);
      } else {
        possibleTrip.setArrivalTime(stopTwoDepartureTime);
        possibleTrip.setDepartureTime(stopOneArrivalTime);
        possibleTrip.setFirstStopSequence(stopTwoSequence);
        possibleTrip.setLastStopSequence(stopOneSequence);
        possibleTrip.setFirstStopId(stop2.stop_id);
        possibleTrip.setLastStopId(stop1.stop_id);
      }

      possibleTrip.setPrice(price);
      possibleTrip.setTripId(trip_id);
      possibleTrip.setRouteId(routeId);

      return possibleTrip;
    }

    return null;
  }

  public static ArrayList<PossibleTrip> findPossibleTrips(Stop source, Stop destination, LocalDateTime dateTime, Boolean arriving) {
    ArrayList<PossibleTrip> possibleTrips = new ArrayList<>();
    String calendarFilter = getCalendarFilter(dateTime);
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
        calendarFilter;

    String[] args = {source.stop_id, destination.stop_id};
    Cursor cursor = FlowManager.getDatabase(CaltrainDatabase.class).getWritableDatabase().rawQuery(query, args);

    while (cursor.moveToNext()) {
      Float price = cursor.getFloat(cursor.getColumnIndex("price"));

      String routeId = cursor.getString(cursor.getColumnIndex("route_id"));
      String tripId = cursor.getString(cursor.getColumnIndex("st1__trip_id"));
      Integer firstStopSequence = cursor.getInt(cursor.getColumnIndex("st1__stop_sequence"));
      Integer secondStopSequence = cursor.getInt(cursor.getColumnIndex("st2__stop_sequence"));

      LocalTime arrivalTime = new LocalTime(cursor.getString(cursor.getColumnIndex("st1__departure_time")).replaceFirst("^24:", "01:"));
      LocalTime departureTime = new LocalTime(cursor.getString(cursor.getColumnIndex("st2__arrival_time")).replaceFirst("^24:", "01:"));

      PossibleTrip possibleTrip = new PossibleTrip();
      possibleTrip.setArrivalTime(arrivalTime);
      possibleTrip.setDepartureTime(departureTime);
      possibleTrip.setPrice(price);
      possibleTrip.setFirstStopId(source.stop_id);
      possibleTrip.setLastStopId(destination.stop_id);
      possibleTrip.setFirstStopSequence(firstStopSequence);
      possibleTrip.setLastStopSequence(secondStopSequence);
      possibleTrip.setTripId(tripId);
      possibleTrip.setRouteId(routeId);

      if (arriving) {
        if (departureTime.isBefore(dateTime.toLocalTime()) && departureTime.plusHours(3).isAfter(dateTime.toLocalTime())) {
          possibleTrips.add(possibleTrip);
        }
      } else {
        if (arrivalTime.isAfter(dateTime.toLocalTime())) {
          possibleTrips.add(possibleTrip);
        }
      }
    }

    return possibleTrips;
  }

  public static ArrayList<Stop> findStopsOnTrip(String trip_id) {
    ArrayList<StopTimes> stopTimes = new ArrayList<StopTimes>(SQLite.select().from(StopTimes.class).where(StopTimes_Table.trip_id.eq(trip_id)).queryList());
    ArrayList<Stop> stops = new ArrayList<>();

    for (StopTimes stopTime : stopTimes) {
      Stop stop = Queries.getDirectionalStopById(stopTime.stop_id);
      if (stop != null) {
        stops.add(Queries.getParentStopById(stop.parent_station));
      }
    }

    return stops;
  }

  public static ArrayList<Pair<Stop, StopTimes>> findTripDetails(String trip_id, Integer first_stop_sequence, Integer second_stop_sequence) {
    ArrayList<Pair<Stop, StopTimes>> stopAndTimes = new ArrayList<>();

    Integer higher_stop_sequence = first_stop_sequence > second_stop_sequence ? first_stop_sequence : second_stop_sequence;
    Integer lower_stop_sequence = first_stop_sequence > second_stop_sequence ? second_stop_sequence : first_stop_sequence;
    Integer direction = first_stop_sequence > second_stop_sequence ? 0 : 1;

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
    Cursor cursor = FlowManager.getDatabase(CaltrainDatabase.class).getWritableDatabase().rawQuery(query, args);

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

      stopAndTimes.add(new Pair<>(stop, stopTimes));
    }

    return stopAndTimes;
  }
}
