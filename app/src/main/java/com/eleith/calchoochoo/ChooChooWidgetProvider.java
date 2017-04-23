package com.eleith.calchoochoo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.widget.RemoteViews;

import com.eleith.calchoochoo.data.ChooChooDatabase;
import com.eleith.calchoochoo.data.PossibleTrain;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.DataStringUtils;
import com.eleith.calchoochoo.utils.PossibleTrainUtils;
import com.eleith.calchoochoo.utils.TripUtils;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Locale;

public class ChooChooWidgetProvider extends AppWidgetProvider {

  @Override
  public void onEnabled(Context context) {
    super.onEnabled(context);
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    for (int appWidgetId : appWidgetIds) {
      updateOneWidget(context, appWidgetId, appWidgetManager);
    }
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);

    if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
      int appWidgetId = intent.getIntExtra(BundleKeys.APP_WIDGET_ID, -1);
      int direction = intent.getIntExtra(BundleKeys.DIRECTION, TripUtils.DIRECTION_NORTH);
      String stopId = intent.getStringExtra(BundleKeys.STOP);
      ChooChooWidgetConfigure.updateWidgetConfiguration(context, appWidgetId, stopId, direction);

      AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
      ComponentName thisAppWidget = new ComponentName(context.getPackageName(), ChooChooWidgetProvider.class.getName());
      int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

      onUpdate(context, appWidgetManager, appWidgetIds);
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
    DateTime now = new DateTime();
    Stop stop = ChooChooWidgetConfigure.getStopFromPreferences(context, appWidgetId);
    int direction = ChooChooWidgetConfigure.getDirectionFromPreferences(context, appWidgetId);
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.fragment_stop_card_widget);

    if (stop != null) {
      views.setTextViewText(R.id.stop_card_stop_name, DataStringUtils.removeCaltrain(stop.stop_name));
      views.setTextViewText(R.id.stop_card_zone, Integer.toString(stop.zone_id));

      if (direction == TripUtils.DIRECTION_NORTH) {
        views.setTextViewText(R.id.stop_card_direction_text, context.getString(R.string.north));
        views.setImageViewResource(R.id.stop_card_arrow_image, R.drawable.ic_arrow_upward_black_24dp);
      } else {
        views.setTextViewText(R.id.stop_card_direction_text, context.getString(R.string.south));
        views.setImageViewResource(R.id.stop_card_arrow_image, R.drawable.ic_arrow_downward_black_24dp);
      }

      Cursor cursor = PossibleTrainUtils.getPossibleTrainQuery(db, stop.stop_id, new LocalDateTime().toDateTime().getMillis());
      ArrayList<PossibleTrain> possibleTrains = PossibleTrainUtils.getPossibleTrainFromCursor(cursor);
      possibleTrains = PossibleTrainUtils.filterByDateTimeAndDirection(possibleTrains, new LocalDateTime(), direction);
      cursor.close();

      views.removeAllViews(R.id.stop_card_widget_train_items);

      Intent intentDirectionUpdate = new Intent(context, ChooChooWidgetProvider.class);
      intentDirectionUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
      intentDirectionUpdate.putExtra(BundleKeys.STOP, stop.stop_id);
      intentDirectionUpdate.putExtra(BundleKeys.APP_WIDGET_ID, appWidgetId);
      intentDirectionUpdate.putExtra(BundleKeys.DIRECTION, direction == TripUtils.DIRECTION_NORTH ? TripUtils.DIRECTION_SOUTH : TripUtils.DIRECTION_NORTH);
      PendingIntent pendingDirectionUpdate = PendingIntent.getBroadcast(context, 0, intentDirectionUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
      views.setOnClickPendingIntent(R.id.stop_card_switch_direction, pendingDirectionUpdate);

      if (possibleTrains.size() > 0) {
        for (int i = 0; i < 3 && i < possibleTrains.size(); i++) {
          PossibleTrain possibleTrain = possibleTrains.get(i);
          DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("h:mma");
          RemoteViews item = new RemoteViews(context.getPackageName(), R.layout.fragment_stop_card_widget_trainitem);
          Integer minutes = Minutes.minutesBetween(now, possibleTrain.getDepartureTime().toDateTimeToday()).getMinutes();

          Intent intent = new Intent(context, TripActivity.class);

          Bundle bundle = new Bundle();
          bundle.putString(BundleKeys.STOP_SOURCE, possibleTrain.getStopId());
          bundle.putString(BundleKeys.TRIP, possibleTrain.getTripId());

          intent.setAction(Intent.ACTION_VIEW);
          intent.putExtras(bundle);
          PendingIntent pendingIntent = PendingIntent.getActivity(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);

          item.setTextViewText(R.id.stop_card_widget_trainitem_number, possibleTrain.getTripShortName());

          if (possibleTrain.getRouteLongName().contains("Bullet")) {
            item.setImageViewResource(R.id.stop_card_widget_trainitem_image, R.drawable.ic_train_bullet);
          } else {
            item.setImageViewResource(R.id.stop_card_widget_trainitem_image, R.drawable.ic_train_local);
          }

          item.setOnClickPendingIntent(R.id.stop_card_widget_train_item, pendingIntent);
          item.setTextViewText(R.id.stop_card_widget_trainitem_time, dateTimeFormatter.print(possibleTrain.getDepartureTime()));

          if (minutes > 0 && minutes <= 60) {
            SpannableStringBuilder time = new SpannableStringBuilder(String.format(Locale.getDefault(), "in %d min", minutes));
            time.setSpan(new StyleSpan(Typeface.ITALIC), 0, time.length() - 1, 0);
            item.setTextViewText(R.id.stop_card_widget_trainitem_time, time);
          } else {
            item.setTextViewText(R.id.stop_card_widget_trainitem_time, dateTimeFormatter.print(possibleTrain.getDepartureTime()));
          }

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
