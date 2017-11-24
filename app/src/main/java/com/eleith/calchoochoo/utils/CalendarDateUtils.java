package com.eleith.calchoochoo.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.SparseArray;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CalendarDateUtils {
  public final static DateTimeFormatter formatter = DateTimeFormat.forPattern("YYYYMMdd");

  public static String getFilterForDate(SQLiteDatabase db, Long dateTime) {
    String calendarFilter = null;
    String calendarExceptionAdded = null;
    String calendarExceptionRemoved = null;
    SparseArray<ArrayList<String>> exceptions = getExceptionsForDate(db, dateTime);
    int dayOfWeek = new DateTime(dateTime).getDayOfWeek();
    switch (dayOfWeek) {
      case 1:
        calendarFilter = "  calendar.monday = 1 ";
        break;
      case 2:
        calendarFilter = "  calendar.tuesday = 1 ";
        break;
      case 3:
        calendarFilter = "  calendar.wednesday = 1 ";
        break;
      case 4:
        calendarFilter = "  calendar.thursday = 1 ";
        break;
      case 5:
        calendarFilter = "  calendar.friday = 1 ";
        break;
      case 6:
        calendarFilter = "  calendar.saturday = 1 ";
        break;
      case 7:
        calendarFilter = "  calendar.sunday = 1 ";
        break;
    }

    for(Integer i = 0; i < exceptions.size(); i++) {
      Integer exception = exceptions.keyAt(i);
      ArrayList<String> service_ids = exceptions.valueAt(i);

      if (exception == 1) {
        calendarExceptionAdded = TextUtils.join(",", service_ids);
      } else if (exception == 2) {
        calendarExceptionRemoved = TextUtils.join(",", service_ids);
      }
    }

    if (calendarExceptionAdded != null) {
      calendarFilter += " OR trips.service_id in (" + calendarExceptionAdded + ")";
    }

    if (calendarExceptionRemoved != null) {
      calendarFilter = "(" + calendarFilter + ")" + " AND trips.service_id not in (" + calendarExceptionRemoved + ")";
    }

    return " AND (" + calendarFilter + ") ";
  }

  public static SparseArray<ArrayList<String>> getExceptionsForDate(SQLiteDatabase db, Long dateTimeLong) {
    LocalDateTime dateTime = new LocalDateTime(dateTimeLong);
    String[] projection = {"service_id", "date", "exception_type"};
    String[] selectionArgs = {formatter.print(dateTime)};
    SparseArray<ArrayList<String>> exceptions = new SparseArray<>();

    Cursor cursor = db.query("calendar_dates", projection, "date = ?", selectionArgs, null, null, null);

    while (cursor.moveToNext()) {
      String serviceId = "'" + cursor.getString(cursor.getColumnIndex("service_id")) + "'";
      Integer exception = cursor.getInt(cursor.getColumnIndex("exception_type"));
      ArrayList<String> serviceIds = exceptions.get(exception);

      if (serviceIds != null) {
        serviceIds.add(serviceId);
      } else {
        serviceIds = new ArrayList<>();
        serviceIds.add(serviceId);
      }

      exceptions.put(exception, serviceIds);
    }

    return exceptions;
  }
}
