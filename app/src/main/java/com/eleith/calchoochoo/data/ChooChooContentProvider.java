package com.eleith.calchoochoo.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.eleith.calchoochoo.utils.PossibleTrainUtils;
import com.eleith.calchoochoo.utils.PossibleTripUtils;
import com.eleith.calchoochoo.utils.StopTimesUtils;

import java.util.ArrayList;
import java.util.List;

public class ChooChooContentProvider extends ContentProvider {
  private ChooChooDatabase database;

  private static final String AUTHORITY = "com.eleith.calchoochoo.data";
  public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
  private static final UriMatcher uriMatcher;

  public static final int URI_STOPS_PARENTS = 1;
  public static final int URI_STOPS_DIRECTIONAL = 2;
  public static final int URI_ROUTES = 3;
  public static final int URI_TRIPS = 4;
  public static final int URI_FARERULES = 5;
  public static final int URI_FAREATTRIBUTES = 6;
  public static final int URI_STOPS_PARENTS_ID = 7;
  public static final int URI_STOPS_DIRECTIONAL_ID = 8;
  public static final int URI_TRIPS_ID = 9;
  public static final int URI_ROUTES_ID = 10;
  public static final int URI_FAREATTRIBUTES_ID = 11;
  public static final int URI_FIND_POSSIBLE_TRAIN = 12;
  public static final int URI_FIND_POSSIBLE_TRIP = 13;
  public static final int URI_FIND_POSSIBLE_TRIPS = 14;
  public static final int URI_FIND_TRIP_STOPS = 15;
  public static final int URI_FIND_STOP_TIMES_TRIP = 16;
  public static final int URI_CALENDAR_DATES = 17;

  static {
    uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    uriMatcher.addURI(AUTHORITY, "stops/parents", URI_STOPS_PARENTS);
    uriMatcher.addURI(AUTHORITY, "stops/directional", URI_STOPS_DIRECTIONAL);
    uriMatcher.addURI(AUTHORITY, "routes", URI_ROUTES);
    uriMatcher.addURI(AUTHORITY, "trips", URI_TRIPS);
    uriMatcher.addURI(AUTHORITY, "fareRules", URI_FARERULES);
    uriMatcher.addURI(AUTHORITY, "fareAttributes", URI_FAREATTRIBUTES);
    uriMatcher.addURI(AUTHORITY, "stops/parent/*", URI_STOPS_PARENTS_ID);
    uriMatcher.addURI(AUTHORITY, "stops/directional/*", URI_STOPS_DIRECTIONAL_ID);
    uriMatcher.addURI(AUTHORITY, "trips/*", URI_TRIPS_ID);
    uriMatcher.addURI(AUTHORITY, "routes/#", URI_ROUTES_ID);
    uriMatcher.addURI(AUTHORITY, "fareAttributes/id", URI_FAREATTRIBUTES_ID);
    uriMatcher.addURI(AUTHORITY, "possibleTrains/*/#", URI_FIND_POSSIBLE_TRAIN);
    uriMatcher.addURI(AUTHORITY, "possibleTrips/trip/*/*/*", URI_FIND_POSSIBLE_TRIP);
    uriMatcher.addURI(AUTHORITY, "possibleTrips/on/#/*/*", URI_FIND_POSSIBLE_TRIPS);
    uriMatcher.addURI(AUTHORITY, "stopsAndTimes/*", URI_FIND_TRIP_STOPS);
    uriMatcher.addURI(AUTHORITY, "stopsAndTimes/*/*/*", URI_FIND_STOP_TIMES_TRIP);
    uriMatcher.addURI(AUTHORITY, "calendarDates", URI_CALENDAR_DATES);
  }

  @Override
  public boolean onCreate() {
    database = new ChooChooDatabase(getContext());
    return true;
  }

  @Override
  public Cursor query(@Nullable Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    SQLiteDatabase db = database.getReadableDatabase();
    Cursor cursor = null;
    List<String> args = new ArrayList<>();

    if (uri == null) {
      return null;
    }

    switch (uriMatcher.match(uri)) {
      case URI_STOPS_PARENTS:
        cursor = db.query("stops", projection, "stop_code = '' AND platform_code = ''", selectionArgs, null, null, sortOrder);
        break;
      case URI_STOPS_DIRECTIONAL:
        cursor = db.query("stops", projection, "stop_code != '' AND platform_code != ''", selectionArgs, null, null, sortOrder);
        break;
      case URI_ROUTES:
        cursor = db.query("routes", projection, selection, selectionArgs, null, null, sortOrder);
        break;
      case URI_TRIPS:
        cursor = db.query("trips", projection, selection, selectionArgs, null, null, sortOrder);
        break;
      case URI_FARERULES:
        cursor = db.query("fare_rules", projection, selection, selectionArgs, null, null, sortOrder);
        break;
      case URI_FAREATTRIBUTES:
        cursor = db.query("fare_attributes", projection, selection, selectionArgs, null, null, sortOrder);
        break;
      case URI_STOPS_PARENTS_ID:
        args.add(uri.getPathSegments().get(2));
        cursor = db.query("stops", projection, "stop_code = '' AND platform_code = '' AND stop_id = ?", args.toArray(new String[1]), null, null, sortOrder);
        break;
      case URI_STOPS_DIRECTIONAL_ID:
        args.add(uri.getPathSegments().get(1));
        cursor = db.query("stops", projection, "stop_code != '' AND platform_code != '' AND stop_id = ?", args.toArray(new String[1]), null, null, sortOrder);
        break;
      case URI_TRIPS_ID:
        args.add(uri.getPathSegments().get(1));
        cursor = db.query("trips", projection, "trip_id = ?", args.toArray(new String[1]), null, null, sortOrder);
        break;
      case URI_ROUTES_ID:
        args.add(uri.getPathSegments().get(1));
        cursor = db.query("routes", projection, "route_id = ?", args.toArray(new String[1]), null, null, sortOrder);
        break;
      case URI_FAREATTRIBUTES_ID:
        args.add(uri.getPathSegments().get(1));
        cursor = db.query("fare_attributes", projection, "fare_id = ?", args.toArray(new String[1]), null, null, sortOrder);
        break;
      case URI_FIND_POSSIBLE_TRAIN:
        String nextTrainStop = uri.getPathSegments().get(1);
        String nextTrainDateTime = uri.getPathSegments().get(2);
        cursor = PossibleTrainUtils.getPossibleTrainQuery(db, nextTrainStop, Long.valueOf(nextTrainDateTime));
        break;
      case URI_FIND_POSSIBLE_TRIPS:
        Long dateTime = Long.valueOf(uri.getPathSegments().get(2));
        String nextPossibleTripsStop1 = uri.getPathSegments().get(3);
        String nextPossibleTripsStop2 = uri.getPathSegments().get(4);
        cursor = PossibleTripUtils.getPossibleTripsByParentStopQuery(db, dateTime, nextPossibleTripsStop1, nextPossibleTripsStop2);
        break;
      case URI_FIND_POSSIBLE_TRIP:
        String nextPossibleTripTrip = uri.getPathSegments().get(2);
        String nextPossibleTripStop1 = uri.getPathSegments().get(3);
        String nextPossibleTripStop2 = uri.getPathSegments().get(4);
        cursor = PossibleTripUtils.getPossibleTripQuery(db, nextPossibleTripTrip, nextPossibleTripStop1, nextPossibleTripStop2);
        break;
      case URI_FIND_TRIP_STOPS:
        String stopFromStopTimesTripId = uri.getPathSegments().get(1);
        cursor = StopTimesUtils.getStopsFromStopTimesQuery(db, stopFromStopTimesTripId);
        break;
      case URI_FIND_STOP_TIMES_TRIP:
        String tripDetailsTrip = uri.getPathSegments().get(1);
        String tripDetailsStop1 = uri.getPathSegments().get(2);
        String tripDetailsStop2 = uri.getPathSegments().get(3);
        cursor = StopTimesUtils.getStopTimesTripQuery(db, tripDetailsTrip, tripDetailsStop1, tripDetailsStop2);
        break;
      case URI_CALENDAR_DATES:
        cursor = db.query("calendar_dates", projection, selection, selectionArgs, null, null, sortOrder);
        break;
      default:
        break;
    }

    return cursor;
  }

  @Nullable
  @Override
  public String getType(@NonNull Uri uri) {
    return null;
  }

  @Override
  public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
    return 0;
  }

  @Override
  public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
    return 0;
  }

  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
    return null;
  }
}
