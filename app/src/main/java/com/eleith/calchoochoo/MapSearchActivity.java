package com.eleith.calchoochoo;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.eleith.calchoochoo.dagger.ChooChooComponent;
import com.eleith.calchoochoo.dagger.ChooChooModule;
import com.eleith.calchoochoo.data.ChooChooLoader;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.DeviceLocation;
import com.eleith.calchoochoo.utils.IntentKeys;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageLocation;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStops;
import com.eleith.calchoochoo.utils.StopUtils;
import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

public class MapSearchActivity extends AppCompatActivity {
  private ChooChooComponent chooChooComponent;
  private Subscription subscription;
  private Subscription subscriptionLocation;
  private ArrayList<Stop> stops;
  private Location location;
  private ChooChooDrawer chooChooDrawer;
  private LatLng myDefaultLatLng = new LatLng(37.3860517, -122.0838511);

  @Inject
  RxBus rxBus;
  @Inject
  ChooChooRouterManager chooChooRouterManager;
  @Inject
  ChooChooLoader chooChooLoader;
  @Inject
  DeviceLocation deviceLocation;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    chooChooComponent = ChooChooApplication.from(this).getAppComponent().activityComponent(new ChooChooModule(this));
    chooChooComponent.inject(this);

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_drawer_fab);
    ButterKnife.bind(this);

    chooChooDrawer = new ChooChooDrawer(this, getWindow().getDecorView().getRootView());
    ChooChooFab chooChooFab = new ChooChooFab(this, rxBus, getWindow().getDecorView().getRootView());

    chooChooFab.setImageDrawable(getDrawable(R.drawable.ic_gps_not_fixed_black_24dp));
    unWrapBundle(savedInstanceState);

    subscription = rxBus.observeEvents(RxMessage.class).subscribe(handleRxMessages());
    subscriptionLocation = rxBus.observeEvents(RxMessageLocation.class).take(1).subscribe(handleRxLocationMessages());

    if (stops == null) {
      chooChooLoader.loadParentStops();
    }
    deviceLocation.requestLocation();
    deviceLocation.listenForLocationUpdates();
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (subscription.isUnsubscribed()) {
      subscription = rxBus.observeEvents(RxMessage.class).subscribe(handleRxMessages());
    }

    if (subscriptionLocation.isUnsubscribed()) {
      subscriptionLocation = rxBus.observeEvents(RxMessageLocation.class).take(1).subscribe(handleRxLocationMessages());
    }
    deviceLocation.listenForLocationUpdates();
  }

  @Override
  protected void onStop() {
    super.onStop();
    subscription.unsubscribe();
    subscriptionLocation.unsubscribe();
    deviceLocation.stopListeningForLocationUpdates();
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == IntentKeys.STOP_SEARCH_RESULT) {
      if (data != null) {
        if (resultCode == RESULT_OK) {
          Bundle bundle = data.getExtras();
          String stopId = bundle.getString(BundleKeys.STOP);
          chooChooRouterManager.loadStopActivity(this, stopId);
        }
      }
    }
  }

  public ChooChooComponent getComponent() {
    return chooChooComponent;
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(BundleKeys.STOPS, Parcels.wrap(stops));
    super.onSaveInstanceState(outState);
  }

  private void unWrapBundle(Bundle bundle) {
    if (bundle != null) {
      stops = Parcels.unwrap(bundle.getParcelable(BundleKeys.STOPS));
    }
  }

  public void initializeFragments() {
    if (stops != null && location != null) {
      updateDrawerTripFilter();
      chooChooRouterManager.loadMapSearchFragment(stops, location);
    }
  }

  public void updateDrawerTripFilter() {
    if (stops != null && location != null) {
      Stop stop = StopUtils.findStopClosestTo(stops, location);
      chooChooDrawer.setStopSource(stop.stop_id);
    }
  }

  private Action1<RxMessage> handleRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_STOPS)) {
          stops = ((RxMessageStops) rxMessage).getMessage();
          initializeFragments();
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.DRAWER_TOGGLE)) {
          chooChooDrawer.open();
        }
      }
    };
  }

  private Action1<RxMessageLocation> handleRxLocationMessages() {
    return new Action1<RxMessageLocation>() {
      @Override
      public void call(RxMessageLocation rxMessage) {
        location = rxMessage.getMessage();
        if (location == null) {
          location = new Location("default");
          location.setLongitude(myDefaultLatLng.longitude);
          location.setLatitude(myDefaultLatLng.latitude);
        }
        initializeFragments();
      }
    };
  }
}
