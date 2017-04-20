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
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageLocation;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStops;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

public class StopSearchActivity extends AppCompatActivity {
  private ChooChooComponent chooChooComponent;
  private Subscription subscription;
  private Subscription subscriptionLocation;
  private Location location;
  private ArrayList<Stop> stops;
  private ArrayList<String> filteredStopIds;
  private Integer reason;

  @Inject
  RxBus rxBus;
  @Inject
  GoogleApiClient googleApiClient;
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

    setContentView(R.layout.activity_linear_top_bottom);
    ButterKnife.bind(this);

    subscription = rxBus.observeEvents(RxMessage.class).subscribe(handleRxMessages());
    subscriptionLocation = rxBus.observeEvents(RxMessageLocation.class).take(1).subscribe(handleRxLocationMessages());
    chooChooLoader.loadParentStops();
    deviceLocation.requestLocation();
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
    subscriptionLocation.unsubscribe();
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
    if (stops != null && location != null) {
      chooChooRouterManager.loadSearchForSpotFragment(stops, reason, filteredStopIds, location);
    }
  }

  private Action1<RxMessage> handleRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_STOPS)) {
          stops = ((RxMessageStops) rxMessage).getMessage();
          Intent intent = getIntent();
          if (intent != null) {
            Bundle bundle = intent.getExtras();
            filteredStopIds = bundle.getStringArrayList(BundleKeys.STOPS);
            reason = bundle.getInt(BundleKeys.SEARCH_REASON);
            loadFragments();
          }
        }
      }
    };
  }

  private Action1<RxMessageLocation> handleRxLocationMessages() {
    return new Action1<RxMessageLocation>() {
      @Override
      public void call(RxMessageLocation rxMessage) {
        location = rxMessage.getMessage();
        loadFragments();
      }
    };
  }
}
