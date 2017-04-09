package com.eleith.calchoochoo.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import org.joda.time.LocalDateTime;

import java.util.UUID;

public class Notifications {
  public static final String ARRIVING = "arriving";
  public static final String DEPARTING = "departing";
  private static final String PREFS_NAME = "com.eleith.calchoochoo.TripActivity";
  private static final String PREF_PREFIX_KEY = "choochoo_trip_";
  private Context context;
  private SharedPreferences preferences;

  public Notifications(Context context) {
    this.context = context;
    this.preferences = context.getSharedPreferences(PREFS_NAME, 0);
  }

  public int getAlarmId(String tripId, String method) {
    return preferences.getInt(PREF_PREFIX_KEY + tripId + "_" + method, -1);
  }

  public int getAlarmMinutes(String tripId, String method) {
    return preferences.getInt(PREF_PREFIX_KEY + tripId + "_" + method + "_minutes", -1);
  }

  private void saveAlarmInfo(String tripId, String method, int alarmId, int minutes) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putInt(PREF_PREFIX_KEY + tripId + "_" + method, alarmId);
    editor.putInt(PREF_PREFIX_KEY + tripId + "_" + method + "_minutes", minutes);
    editor.apply();
  }

  private void removeAlarms(String tripId, String method) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.remove(PREF_PREFIX_KEY + tripId + "_" + method);
    editor.remove(PREF_PREFIX_KEY + tripId + "_" + method + "_minutes");
    editor.apply();
  }

  private Intent makeIntent() {
    Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
    notificationIntent.addCategory("android.intent.category.DEFAULT");

    return notificationIntent;
  }

  public void set(String tripId, LocalDateTime alarmTime, int minutesWarning, String method, Bundle bundle) {
    int alarmId = UUID.randomUUID().hashCode();
    Intent notificationIntent = makeIntent();
    notificationIntent.putExtras(bundle);

    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    PendingIntent arrivalIntent = PendingIntent.getBroadcast(context, alarmId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.minusMinutes(minutesWarning).toDate().getTime(), arrivalIntent);
    saveAlarmInfo(tripId, method, alarmId, minutesWarning);
  }

  public void cancel(String tripId, String method) {
    Intent notificationIntent = makeIntent();
    int alarmId = getAlarmId(tripId, method);

    if (alarmId != -1) {
      PendingIntent.getBroadcast(context, alarmId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();
      removeAlarms(tripId, method);
    }
  }

  public Notification build(String title, String content, String ticker, int drawableIconId, PendingIntent intent) {
    Bitmap bitmapIcon = DrawableUtils.getBitmapFromVectorDrawable(context.getApplicationContext(), drawableIconId, 0.25f);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext());
    return builder.setContentTitle(title)
        .setContentText(content)
        .setTicker(ticker)
        .setLargeIcon(bitmapIcon)
        .setDefaults(Notification.DEFAULT_ALL)
        .setSmallIcon(drawableIconId)
        .setContentIntent(intent).build();
  }

  public void send(Notification notification, String tripId, String method) {
    NotificationManager notificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(getAlarmId(tripId, method), notification);
    removeAlarms(tripId, method);
  }
}
