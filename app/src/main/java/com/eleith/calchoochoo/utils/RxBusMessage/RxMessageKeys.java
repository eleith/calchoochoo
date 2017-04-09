package com.eleith.calchoochoo.utils.RxBusMessage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RxMessageKeys {
  public static final String SEARCH_INPUT_STRING = "searchInputString";
  public static final String SEARCH_RESULT_PAIR = "searchResultPair";
  public static final String SEARCH_RESULT_STOP = "searchResultStop";
  public static final String DESTINATION_SELECTED = "destinationSelected";
  public static final String SOURCE_SELECTED = "arrivalSelected";
  public static final String DATE_TIME_SELECTED = "dateTimeSelected";
  public static final String TRIP_SELECTED = "tripSelected";
  public static final String STOP_SELECTED = "stopSelected";
  public static final String SWITCH_SOURCE_DESTINATION_SELECTED = "switchSourceDestinationSelected";
  public static final String MY_LOCATION = "myLocation";
  public static final String MY_LOCATION_UPDATE = "myLocationUpdate";
  public static final String FAB_CLICKED = "fabClicked";
  public static final String LOADED_STOPS = "loadedStops";
  public static final String LOADED_ROUTES = "loadedRoutes";
  public static final String LOADED_TRIP_DETAILS = "loadedTripDetails";
  public static final String LOADED_NEXT_TRAINS = "loadedNextTrains";
  public static final String LOADED_ROUTE = "loadedRoute";
  public static final String LOADED_TRIP = "loadedTrip";
  public static final String LOADED_STOP = "loadedStop";
  public static final String LOADED_TRIPS = "loadedTrips";
  public static final String LOADED_POSSIBLE_TRIP = "loadedPossibleTrip";
  public static final String LOADED_POSSIBLE_TRIPS = "loadedPossibleTrips";
  public static final String LOADED_STOPS_ON_TRIP = "loadedStopsOnTrip";
  public static final String NOTIFICATION_SELECTED = "notificationSelected";

  private static final Map<String, Class> keyToClassMap = createKeyMap();

  private static Map<String, Class> createKeyMap() {
    Map<String, Class> map = new HashMap<>();

    map.put(FAB_CLICKED, RxMessage.class);
    map.put(SEARCH_INPUT_STRING, RxMessageString.class);
    map.put(SEARCH_RESULT_PAIR, RxMessageStopsAndDetails.class);
    map.put(SEARCH_RESULT_STOP, RxMessageStop.class);
    map.put(DESTINATION_SELECTED, RxMessageStopsAndDetails.class);
    map.put(SOURCE_SELECTED, RxMessageStopsAndDetails.class);
    map.put(SWITCH_SOURCE_DESTINATION_SELECTED, RxMessage.class);
    map.put(DATE_TIME_SELECTED, RxMessageStopMethodAndDateTime.class);
    map.put(TRIP_SELECTED, RxMessagePossibleTrip.class);
    map.put(STOP_SELECTED, RxMessageStop.class);
    map.put(MY_LOCATION, RxMessageLocation.class);
    map.put(MY_LOCATION_UPDATE, RxMessageLocation.class);
    map.put(LOADED_STOPS, RxMessageStops.class);
    map.put(LOADED_ROUTES, RxMessageRoutes.class);
    map.put(LOADED_TRIP_DETAILS, RxMessageTripStops.class);
    map.put(LOADED_NEXT_TRAINS, RxMessageNextTrains.class);
    map.put(LOADED_ROUTE, RxMessageRoute.class);
    map.put(LOADED_TRIP, RxMessageTrip.class);
    map.put(LOADED_STOP, RxMessageStop.class);
    map.put(LOADED_TRIPS, RxMessageTrips.class);
    map.put(LOADED_POSSIBLE_TRIP, RxMessagePossibleTrip.class);
    map.put(LOADED_STOPS_ON_TRIP, RxMessageStops.class);
    map.put(LOADED_POSSIBLE_TRIPS, RxMessagePossibleTrips.class);
    map.put(NOTIFICATION_SELECTED, RxMessageNotificationPair.class);

    return Collections.unmodifiableMap(map);
  }

  static Class validMessageClassFor(String type) {
    return keyToClassMap.get(type);
  }
}
