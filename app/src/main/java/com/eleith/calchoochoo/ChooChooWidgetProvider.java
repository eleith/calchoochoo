package com.eleith.calchoochoo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.eleith.calchoochoo.data.ChooChooDatabase;
import com.eleith.calchoochoo.data.PossibleTrain;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.PossibleTrainUtils;

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
    ChooChooDatabase chooChooDatabase = new ChooChooDatabase(context);
    SQLiteDatabase db = chooChooDatabase.getReadableDatabase();

    Stop stop = ChooChooWidgetConfigure.getStopFromPreferences(context, appWidgetId);
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.fragment_stop_card_widget);
    appWidgetManager.updateAppWidget(appWidgetId, views);

    if (stop != null) {
      views.setTextViewText(R.id.stop_card_stop_name, stop.stop_name);

      Cursor cursor = PossibleTrainUtils.getPossibleTrainQuery(db, stop.stop_id, new LocalDateTime().toDateTime().getMillis());
      ArrayList<PossibleTrain> possibleTrains = PossibleTrainUtils.getPossibleTrainFromCursor(cursor);
      possibleTrains = PossibleTrainUtils.filterByDateTime(possibleTrains, new LocalDateTime());
      cursor.close();

      views.removeAllViews(R.id.stop_card_widget_train_items);

      if (possibleTrains.size() > 0) {
        for (int i = 0; i < 3 && i < possibleTrains.size(); i++) {
          PossibleTrain possibleTrain = possibleTrains.get(i);
          DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("h:mma");
          RemoteViews item = new RemoteViews(context.getPackageName(), R.layout.fragment_stop_card_widget_trainitem);

          Intent intent = new Intent(context, TripActivity.class);

          Bundle bundle = new Bundle();
          bundle.putString(BundleKeys.STOP_SOURCE, possibleTrain.getStopId());
          bundle.putString(BundleKeys.TRIP, possibleTrain.getTripId());

          intent.setAction(Intent.ACTION_VIEW);
          intent.putExtras(bundle);
          PendingIntent pendingIntent = PendingIntent.getActivity(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);

          item.setTextViewText(R.id.stop_card_widget_trainitem_number, possibleTrain.getTripShortName());
          if (possibleTrain.getTripDirectionId() == 1) {
            item.setTextViewText(R.id.stop_card_widget_direction, context.getString(R.string.south_bound));
          } else {
            item.setTextViewText(R.id.stop_card_widget_direction, context.getString(R.string.north_bound));
          }

          if (possibleTrain.getRouteLongName().contains("Bullet")) {
            item.setImageViewResource(R.id.stop_card_widget_trainitem_image, R.drawable.ic_train_bullet);
          } else {
            item.setImageViewResource(R.id.stop_card_widget_trainitem_image, R.drawable.ic_train_local);
          }

          item.setOnClickPendingIntent(R.id.stop_card_widget_train_item, pendingIntent);
          item.setTextViewText(R.id.stop_card_widget_trainitem_time, dateTimeFormatter.print(possibleTrain.getDepartureTime()));
          views.addView(R.id.stop_card_widget_train_items, item);
        }
      } else {
        RemoteViews item = new RemoteViews(context.getPackageName(), R.layout.fragment_stop_card_widget_train_nomore);
        views.addView(R.id.stop_card_widget_train_items, item);
      }
    }

    appWidgetManager.updateAppWidget(appWidgetId, views);
  }
}
