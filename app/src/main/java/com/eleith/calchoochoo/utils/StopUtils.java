package com.eleith.calchoochoo.utils;

import com.eleith.calchoochoo.data.Stop;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

public class StopUtils {
  static public ArrayList<Stop> filterByFuzzySearch(ArrayList<Stop> stops, String query) {
    ArrayList<Stop> filteredStops;
    if (query != null && !query.equals("")) {
      filteredStops =  new ArrayList<Stop>();
      final HashMap<String, Integer> stopFuzzyScores = new HashMap<String, Integer>();
      for (Stop stop : stops) {
        int fuzzyScore = StringUtils.getFuzzyDistance(stop.stop_name, query, Locale.getDefault());
        if (fuzzyScore >= query.length()) {
          stopFuzzyScores.put(stop.stop_id, fuzzyScore);
          filteredStops.add(stop);
        }
      }
      Collections.sort(filteredStops, new Comparator<Stop>() {
            @Override
            public int compare(Stop lhs, Stop rhs) {
              int rightFuzzyScore = stopFuzzyScores.get(rhs.stop_id);
              int leftFuzzyScore = stopFuzzyScores.get(lhs.stop_id);
              return Integer.compare(rightFuzzyScore, leftFuzzyScore);
            }
      });
    } else {
      filteredStops = stops;
      Collections.sort(filteredStops, Stop.nameComparator);
    }

    return filteredStops;
  }
}
