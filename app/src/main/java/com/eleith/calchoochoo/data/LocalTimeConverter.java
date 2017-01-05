package com.eleith.calchoochoo.data;

import android.util.Log;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import org.joda.time.LocalTime;

@com.raizlabs.android.dbflow.annotation.TypeConverter

public class LocalTimeConverter extends TypeConverter<String, LocalTime> {

  @Override
  public String getDBValue(LocalTime model) {
    return model == null ? null : model.toString();
  }

  @Override
  public LocalTime getModelValue(String data) {
    LocalTime dateTime = null;
    try {
      return new LocalTime(data);
    } catch(Exception e) {
      Log.e("db-error", "bad data in your database");
      return new LocalTime();
    }
  }
}
