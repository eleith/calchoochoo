package com.eleith.calchoochoo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.eleith.calchoochoo.dagger.ChooChooComponent;
import com.eleith.calchoochoo.dagger.ChooChooModule;
import com.eleith.calchoochoo.data.ChooChooLoader;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.StopTimes;
import com.eleith.calchoochoo.data.Trips;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessagePossibleTrip;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageTrip;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageTripStops;
import com.eleith.calchoochoo.utils.StopTimesUtils;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
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

  @Inject
  RxBus rxBus;
  @Inject
  GoogleApiClient googleApiClient;
  @Inject
  ChooChooRouterManager chooChooRouterManager;
  @Inject
  ChooChooLoader chooChooLoader;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    chooChooComponent = ChooChooApplication.from(this).getAppComponent().activityComponent(new ChooChooModule(this));
    chooChooComponent.inject(this);

    super.onCreate(savedInstanceState);
    postponeEnterTransition();

    setContentView(R.layout.activity_appbar_main);

    ButterKnife.bind(this);
    subscription = rxBus.observeEvents(RxMessage.class).subscribe(new HandleRxMessages());

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
      }
    }
  }
}
