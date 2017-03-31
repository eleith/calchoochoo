package com.eleith.calchoochoo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.eleith.calchoochoo.dagger.ChooChooComponent;
import com.eleith.calchoochoo.dagger.ChooChooModule;
import com.eleith.calchoochoo.data.ChooChooLoader;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.StopTimes;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageTripStops;
import com.google.android.gms.common.api.GoogleApiClient;

import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

public class TripActivity extends AppCompatActivity {
  private ChooChooComponent chooChooComponent;
  private Subscription subscription;
  private PossibleTrip possibleTrip;

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
        possibleTrip = Parcels.unwrap(bundle.getParcelable(BundleKeys.POSSIBLE_TRIP));
        chooChooLoader.loadTripStops(possibleTrip.getTripId(), possibleTrip.getFirstStopId(), possibleTrip.getLastStopId());
      }
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    googleApiClient.connect();
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

  private class HandleRxMessages implements Action1<RxMessage> {
    @Override
    public void call(RxMessage rxMessage) {
      if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_TRIP_DETAILS)) {
        ArrayList<StopTimes> tripStops = ((RxMessageTripStops) rxMessage).getMessage();
        chooChooRouterManager.loadTripDetailsFragments(possibleTrip, tripStops);
      }
    }
  }
}
