package com.eleith.calchoochoo.data;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;

public class Queries {

  public static ArrayList<Stop> getAllStops() {
    return new ArrayList<>(SQLite.select().from(Stop.class)
        .where(Stop_Table.stop_code.is(""))
        .and(Stop_Table.platform_code.is(""))
        .queryList());
  }
}
