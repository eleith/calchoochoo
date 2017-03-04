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

  private static final Map<String, Class> keyToClassMap = createKeyMap();

  private static Map<String, Class> createKeyMap() {
    Map<String, Class> map = new HashMap<>();

    map.put(SEARCH_INPUT_STRING, RxMessageString.class);
    map.put(SEARCH_RESULT_PAIR, RxMessageStopsAndDetails.class);
    map.put(SEARCH_RESULT_STOP, RxMessageStop.class);
    map.put(DESTINATION_SELECTED, RxMessageStopsAndDetails.class);
    map.put(SOURCE_SELECTED, RxMessageStopsAndDetails.class);
    map.put(SWITCH_SOURCE_DESTINATION_SELECTED, RxMessage.class);
    map.put(DATE_TIME_SELECTED, RxMessageStopsAndDetails.class);
    map.put(TRIP_SELECTED, RxMessagePossibleTrip.class);
    map.put(STOP_SELECTED, RxMessageStop.class);
    map.put(MY_LOCATION, RxMessageLocation.class);
    map.put(MY_LOCATION_UPDATE, RxMessageLocation.class);

    return Collections.unmodifiableMap(map);
  }

  static Class validMessageClassFor(String type) {
    return keyToClassMap.get(type);
  }
}
