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

  private static final Map<String, Class> keyToClassMap = createKeyMap();

  private static Map<String, Class> createKeyMap() {
    Map<String, Class> map = new HashMap<>();

    map.put(SEARCH_INPUT_STRING, RxMessageString.class);
    map.put(SEARCH_RESULT_PAIR, RxMessagePairStopReason.class);
    map.put(SEARCH_RESULT_STOP, RxMessageStop.class);
    map.put(DESTINATION_SELECTED, RxMessage.class);
    map.put(SOURCE_SELECTED, RxMessage.class);
    map.put(DATE_TIME_SELECTED, RxMessageArrivalOrDepartDateTime.class);

    return Collections.unmodifiableMap(map);
  }

  static Class validMessageClassFor(String type) {
    return keyToClassMap.get(type);
  }
}
