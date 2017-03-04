package com.eleith.calchoochoo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.eleith.calchoochoo.data.PossibleTrain;
import com.eleith.calchoochoo.data.Queries;
import com.eleith.calchoochoo.data.Routes;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.Trips;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessagePairStopReason;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.parceler.Parcels;

import java.util.ArrayList;

public class ChooChooWidgetProvider extends AppWidgetProvider {

  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
  }

  @Override
  public void onEnabled(Context context) {
    super.onEnabled(context);
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    for (int appWidgetId : appWidgetIds) {
      ChooChooWidgetProvider.updateOneWidget(context, appWidgetId, appWidgetManager);
    }
  }

  @Override
  public void onDeleted(Context context, int[] appWidgetIds) {
    super.onDeleted(context, appWidgetIds);
  }

  @Override
  public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
    super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
  }

  public static void updateOneWidget(Context context, int appWidgetId, AppWidgetManager appWidgetManager) {
    Stop stop = ChooChooWidgetConfigure.getStopFromPreferences(context, appWidgetId);
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.fragment_stop_card_widget);
    appWidgetManager.updateAppWidget(appWidgetId, views);

    if (stop != null) {
      views.setTextViewText(R.id.stop_card_stop_name, stop.stop_name);
      ArrayList<PossibleTrain> possibleTrains = Queries.findNextTrain(stop, new LocalDateTime());

      for (int i = 0; i < 3 && i < possibleTrains.size(); i++) {
        PossibleTrain possibleTrain = possibleTrains.get(i);
        Trips trip = Queries.getTripById(possibleTrain.getTripId());
        Routes route = Queries.getRouteById(possibleTrain.getRouteId());
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("h:mma");
        RemoteViews item = new RemoteViews(context.getPackageName(), R.layout.fragment_stop_card_widget_trainitem);
        Intent intent = new Intent(context, ChooChooActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable(BundleKeys.TRIP, Parcels.wrap(trip));
        bundle.putParcelable(BundleKeys.STOP, Parcels.wrap(stop));
        bundle.putInt(BundleKeys.SEARCH_REASON, RxMessagePairStopReason.SEARCH_REASON_DESTINATION);

        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        if (trip != null) {
          item.setTextViewText(R.id.stop_card_widget_trainitem_number, trip.trip_id);
          if (trip.direction_id == 1) {
            item.setTextViewText(R.id.stop_card_widget_direction, context.getString(R.string.south_bound));
          } else {
            item.setTextViewText(R.id.stop_card_widget_direction, context.getString(R.string.north_bound));
          }
        }

        if (route != null && route.route_long_name.contains("Bullet")) {
          item.setImageViewResource(R.id.stop_card_widget_trainitem_image, R.drawable.ic_train_bullet);
        } else {
          item.setImageViewResource(R.id.stop_card_widget_trainitem_image, R.drawable.ic_train_local);
        }

        item.setOnClickPendingIntent(R.id.stop_card_widget_train_item, pendingIntent);
        item.setTextViewText(R.id.stop_card_widget_trainitem_time, dateTimeFormatter.print(possibleTrain.getDepartureTime()));
        views.addView(R.id.stop_card_widget_train_items, item);
      }
    }

    appWidgetManager.updateAppWidget(appWidgetId, views);
  }
}
