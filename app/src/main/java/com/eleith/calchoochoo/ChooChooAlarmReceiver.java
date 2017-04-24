package com.eleith.calchoochoo;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;

import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.Notifications;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

public class ChooChooAlarmReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    Bundle bundle = intent.getExtras();
    String tripId = bundle.getString(BundleKeys.TRIP);
    String tripName = bundle.getString(BundleKeys.TRIP_NAME);
    String sourceId = bundle.getString(BundleKeys.STOP_SOURCE);
    String destinationId = bundle.getString(BundleKeys.STOP_DESTINATION);
    String sourceName = bundle.getString(BundleKeys.STOP_SOURCE_NAME);
    String destinationName = bundle.getString(BundleKeys.STOP_DESTINATION_NAME);
    Long sourceTime = bundle.getLong(BundleKeys.STOP_SOURCE_TIME);
    Long destinationTime = bundle.getLong(BundleKeys.STOP_DESTINATION_TIME);
    String method = bundle.getString(BundleKeys.STOP_METHOD, Notifications.ARRIVING);

    Intent notificationIntent = new Intent(context, TripActivity.class);
    notificationIntent.putExtras(bundle);

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
    stackBuilder.addParentStack(TripActivity.class);
    stackBuilder.addNextIntent(notificationIntent);

    PendingIntent pendingIntent;
    String title;
    String timeString;
    String content;
    String ticker;

    if (method.equals(Notifications.ARRIVING)) {
      pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
      timeString = DateTimeFormat.forPattern("h:mma").print(new LocalTime(destinationTime));
      title = String.format("Train #%s", tripName);
      content = String.format("%s at %s", "Arriving", timeString);
      ticker = "Caltrain Train Arrival Notification";
    } else {
      pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
      timeString = DateTimeFormat.forPattern("h:mma").print(new LocalTime(sourceTime));
      title = String.format("Train #%s", tripName);
      content = String.format("%s at %s", "Departing", timeString);
      ticker = "Caltrain Train Departure Notification";
    }

    Notifications notifications = new Notifications(context);
    Notification notificationBuilder = notifications.build(title, content, ticker, R.drawable.ic_cal_choo_choo, pendingIntent);
    notifications.send(notificationBuilder, tripId, method);
  }
}