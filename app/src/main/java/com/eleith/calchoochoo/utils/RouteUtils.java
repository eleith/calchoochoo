package com.eleith.calchoochoo.utils;

import android.database.Cursor;
import android.location.Location;
import android.support.annotation.Nullable;

import com.eleith.calchoochoo.data.Routes;
import com.eleith.calchoochoo.data.Stop;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

public class RouteUtils {

  public static ArrayList<Routes> getRoutesFromCursor(Cursor cursor) {
    ArrayList<Routes> routes = new ArrayList<>();

    while (cursor.moveToNext()) {
      Routes route = getRouteFromCursor(cursor);
      routes.add(route);
    }

    return routes;
  }

  @Nullable
  public static Routes getRouteFromCursor(Cursor cursor) {
    Routes route = new Routes();

    if (!cursor.isAfterLast()) {
      route.route_id = cursor.getString(cursor.getColumnIndex("route_id"));
      route.route_short_name = cursor.getString(cursor.getColumnIndex("route_short_name"));
      route.route_long_name = cursor.getString(cursor.getColumnIndex("route_long_name"));
      route.route_type = cursor.getInt(cursor.getColumnIndex("route_type"));
      route.route_color = cursor.getString(cursor.getColumnIndex("route_color"));

      return route;
    } else {
      return null;
    }
  }

  @Nullable
  public static Routes getRouteById(ArrayList<Routes> allRoutes, String route_id) {
    for (Routes route : allRoutes) {
      if (route_id.equals(route.route_id)) {
        return route;
      }
    }
    return null;
  }
}
