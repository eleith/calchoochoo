package com.eleith.calchoochoo.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.joda.time.DateTime;

public class CalendarDateUtils {

  public static String getCalendarFilter(SQLiteDatabase db, Long dateTime) {
    String calendarFilter;
    int dayOfWeek = isExceptionDate(db, dateTime) ? 0 : new DateTime(dateTime).getDayOfWeek();
    switch (dayOfWeek) {
      case 1:
        calendarFilter = "  AND calendar.monday = 1 ";
        break;
      case 2:
        calendarFilter = "  AND calendar.tuesday = 1 ";
        break;
      case 3:
        calendarFilter = "  AND calendar.wednesday = 1 ";
        break;
      case 4:
        calendarFilter = "  AND calendar.thursday = 1 ";
        break;
      case 5:
        calendarFilter = "  AND calendar.friday = 1 ";
        break;
      case 6:
        calendarFilter = "  AND calendar.saturday = 1 ";
        break;
      case 7:
        calendarFilter = "  AND calendar.saturday = 1 ";
        break;
      default:
        calendarFilter = " AND calendar.sunday = 1 ";
        break;
    }
    return calendarFilter;
  }

  public static boolean isExceptionDate(SQLiteDatabase db, Long dateTimeString) {
    String[] projection = {"date"};
    String[] selectionArgs = {Long.toString(dateTimeString)};

    Cursor cursor = db.query("calendar_dates", projection, "date = ?", selectionArgs, null, null, null);

    if (cursor != null) {
      Boolean isException = cursor.getCount() > 0;
      cursor.close();
      return isException;
    } else {
      return false;
    }
  }
}
