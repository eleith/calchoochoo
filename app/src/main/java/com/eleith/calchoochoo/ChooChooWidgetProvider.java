package com.eleith.calchoochoo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.eleith.calchoochoo.data.Stop;

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
    //Intent intent = new Intent(context, ChooChooActivity.class);
    //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
    Stop stop = ChooChooWidgetConfigure.getStopFromPreferences(context, appWidgetId);
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.fragment_stop_card_widget);
    appWidgetManager.updateAppWidget(appWidgetId, views);
    if (stop != null) {
      views.setTextViewText(R.id.stop_card_stop_name, stop.stop_name);
      views.setTextViewText(R.id.stop_card_zone, Integer.toString(stop.zone_id));
    }
    appWidgetManager.updateAppWidget(appWidgetId, views);
  }
}
