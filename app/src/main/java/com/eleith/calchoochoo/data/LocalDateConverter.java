package com.eleith.calchoochoo.data;

import android.util.Log;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

@com.raizlabs.android.dbflow.annotation.TypeConverter

public class LocalDateConverter extends TypeConverter<String, LocalDate> {

  @Override
  public String getDBValue(LocalDate model) {
    return model == null ? null : model.toString();
  }

  @Override
  public LocalDate getModelValue(String data) {
    LocalDate localDate = null;
    try {
      return new LocalDate(data);
    } catch(Exception e) {
      Log.e("db-error", "bad data in your database");
      return new LocalDate();
    }
  }
}
