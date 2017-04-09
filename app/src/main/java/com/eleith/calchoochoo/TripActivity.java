package com.eleith.calchoochoo;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;

import com.eleith.calchoochoo.dagger.ChooChooComponent;
import com.eleith.calchoochoo.dagger.ChooChooModule;
import com.eleith.calchoochoo.data.ChooChooLoader;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.StopTimes;
import com.eleith.calchoochoo.data.Trips;
import com.eleith.calchoochoo.fragments.SetAlarmDialogFragment;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.ColorUtils;
import com.eleith.calchoochoo.utils.Notifications;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageNotificationPair;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessagePossibleTrip;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageTrip;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageTripStops;
import com.eleith.calchoochoo.utils.StopTimesUtils;
import com.google.android.gms.common.api.GoogleApiClient;

import org.joda.time.LocalDateTime;
import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class TripActivity extends AppCompatActivity {
  private ChooChooComponent chooChooComponent;
  private Subscription subscription;
  private String sourceId;
  private String destinationId;
  private ArrayList<StopTimes> tripStops;
  private PossibleTrip possibleTrip;
  private String tripId;
  private Trips trip;
  private static final String PREFS_NAME = "com.eleith.calchoochoo.TripActivity";
  private static final String PREF_PREFIX_KEY = "choochoo_trip_";
  private Notifications notifications;

  @Inject
  RxBus rxBus;
  @Inject
  GoogleApiClient googleApiClient;
  @Inject
  ChooChooRouterManager chooChooRouterManager;
  @Inject
  ChooChooLoader chooChooLoader;

  @BindView(R.id.activityFloatingActionButton)
  FloatingActionButton floatingActionButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    chooChooComponent = ChooChooApplication.from(this).getAppComponent().activityComponent(new ChooChooModule(this));
    chooChooComponent.inject(this);

    super.onCreate(savedInstanceState);
    postponeEnterTransition();

    setContentView(R.layout.activity_appbar_main_with_fab);

    ButterKnife.bind(this);
    subscription = rxBus.observeEvents(RxMessage.class).subscribe(new HandleRxMessages());
    notifications = new Notifications(this);

    floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_add_alarm_black_24dp));
    floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(ColorUtils.getThemeColor(this, R.attr.colorAccent)));

    Intent intent = getIntent();
    if (intent != null) {
      Bundle bundle = intent.getExtras();
      if (bundle != null) {
        tripId = bundle.getString(BundleKeys.TRIP);
        sourceId = bundle.getString(BundleKeys.STOP_SOURCE);
        destinationId = bundle.getString(BundleKeys.STOP_DESTINATION);

        chooChooLoader.loadTripStops(tripId);

        if (destinationId != null) {
          chooChooLoader.loadPossibleTrip(tripId, sourceId, destinationId);
        }
      }
    }
  }

  @OnClick(R.id.activityFloatingActionButton)
  public void openAlarmDialog() {
    Bundle bundle = new Bundle();
    SetAlarmDialogFragment dialog = new SetAlarmDialogFragment();
    bundle.putParcelable(BundleKeys.POSSIBLE_TRIP, Parcels.wrap(possibleTrip));
    dialog.setArguments(bundle);
    dialog.show(getSupportFragmentManager(), "dialog");
  }

  private void changeNotificationPreferences(int departureMinutes, int arrivalMinutes) {
    Bundle bundle = new Bundle();
    bundle.putString(BundleKeys.TRIP, tripId);
    bundle.putString(BundleKeys.STOP_SOURCE, sourceId);
    bundle.putString(BundleKeys.STOP_DESTINATION, destinationId);
    bundle.putString(BundleKeys.STOP_SOURCE_NAME, possibleTrip.getFirstStopName());
    bundle.putLong(BundleKeys.STOP_SOURCE_TIME, possibleTrip.getDepartureTime().toDateTimeToday().getMillis());
    bundle.putString(BundleKeys.STOP_DESTINATION_NAME, possibleTrip.getLastStopName());
    bundle.putLong(BundleKeys.STOP_DESTINATION_TIME, possibleTrip.getArrivalTime().toDateTimeToday().getMillis());

    notifications.cancel(tripId, Notifications.ARRIVING);
    notifications.cancel(tripId, Notifications.DEPARTING);
    floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(ColorUtils.getThemeColor(this, R.attr.colorAccent)));

    if (arrivalMinutes > 0) {
      LocalDateTime arrivingDateTime = new LocalDateTime(possibleTrip.getArrivalTime().toDateTimeToday());

      bundle.putString(BundleKeys.STOP_METHOD, Notifications.ARRIVING);
      notifications.set(tripId, arrivingDateTime, arrivalMinutes, Notifications.ARRIVING, bundle);
      floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(ColorUtils.getThemeColor(this, R.attr.colorPrimary)));
    }

    if (departureMinutes > 0) {
      LocalDateTime departingDateTime = new LocalDateTime(possibleTrip.getDepartureTime().toDateTimeToday());

      bundle.putString(BundleKeys.STOP_METHOD, Notifications.DEPARTING);
      notifications.set(tripId, departingDateTime, departureMinutes, Notifications.DEPARTING, bundle);
      floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(ColorUtils.getThemeColor(this, R.attr.colorPrimary)));
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    googleApiClient.connect();
    if (subscription.isUnsubscribed()) {
      subscription = rxBus.observeEvents(RxMessage.class).subscribe(new HandleRxMessages());
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    googleApiClient.disconnect();
    subscription.unsubscribe();
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  @Override
  protected void onResume() {
    super.onResume();
    googleApiClient.reconnect();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  public ChooChooComponent getComponent() {
    return chooChooComponent;
  }

  private void loadFragments() {
    if (tripStops != null && possibleTrip != null) {
      if (notifications.getAlarmMinutes(possibleTrip.getTripId(), Notifications.ARRIVING) != -1) {
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(ColorUtils.getThemeColor(this, R.attr.colorPrimary)));
      }

      if (notifications.getAlarmMinutes(possibleTrip.getTripId(), Notifications.DEPARTING) != -1) {
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(ColorUtils.getThemeColor(this, R.attr.colorPrimary)));
      }

      tripStops = StopTimesUtils.filterAndOrder(tripStops, possibleTrip.getTripDirection(), sourceId, destinationId);
      chooChooRouterManager.loadTripDetailsFragments(possibleTrip, tripStops);
    }
  }

  private class HandleRxMessages implements Action1<RxMessage> {
    @Override
    public void call(RxMessage rxMessage) {
      if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_TRIP_DETAILS)) {
        tripStops = ((RxMessageTripStops) rxMessage).getMessage();
        if (destinationId != null) {
          loadFragments();
        } else {
          chooChooLoader.loadTripById(tripId);
        }
      } else if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_POSSIBLE_TRIP)) {
        possibleTrip = ((RxMessagePossibleTrip) rxMessage).getMessage();
        loadFragments();
      } else if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_TRIP)) {
        trip = ((RxMessageTrip) rxMessage).getMessage();
        tripStops = StopTimesUtils.filterAndOrder(tripStops, trip.direction_id, sourceId);
        destinationId = tripStops.get(tripStops.size() - 1).stop_id;
        chooChooLoader.loadPossibleTrip(tripId, sourceId, destinationId);
      } else if (rxMessage.isMessageValidFor(RxMessageKeys.NOTIFICATION_SELECTED)) {
        Pair<Integer, Integer> notifications = ((RxMessageNotificationPair) rxMessage).getMessage();
        changeNotificationPreferences(notifications.first, notifications.second);
      }
    }
  }
}
