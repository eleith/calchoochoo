package com.eleith.calchoochoo;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
import com.google.android.gms.common.api.GoogleApiClient;

import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class MapSearchActivity extends AppCompatActivity {
  private ChooChooComponent chooChooComponent;
  private Subscription subscription;
  private Subscription subscriptionLocation;
  private ArrayList<Stop> stops;
  private Location location;

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

  @BindView(R.id.activityFloatingActionButton)
  FloatingActionButton floatingActionButton;

  @OnClick(R.id.activityFloatingActionButton)
  void onFabClicked() {
    rxBus.send(new RxMessage(RxMessageKeys.FAB_CLICKED));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    chooChooComponent = ChooChooApplication.from(this).getAppComponent().activityComponent(new ChooChooModule(this));
    chooChooComponent.inject(this);

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_linear_top_bottom);
    ButterKnife.bind(this);

    subscription = rxBus.observeEvents(RxMessage.class).subscribe(handleRxMessages());
    subscriptionLocation = rxBus.observeEvents(RxMessageLocation.class).take(1).subscribe(handleRxLocationMessages());
    unWrapBundle(savedInstanceState);

    if (stops == null) {
      chooChooLoader.loadParentStops();
    }

    deviceLocation.requestLocationUpdates();
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
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == IntentKeys.STOP_SEARCH_RESULT) {
      if (data != null) {
        Bundle bundle = data.getExtras();
        String stopId = bundle.getString(BundleKeys.STOP);
        Stop closestStop = StopUtils.findStopClosestTo(stops, location);
        if (resultCode == RESULT_OK) {
          if (closestStop != null) {
            chooChooRouterManager.loadTripFilterActivity(this, closestStop.stop_id, stopId);
          } else {
            chooChooRouterManager.loadTripFilterActivity(this, null, stopId);
          }
        }
      }
    }
  }

  public ChooChooComponent getComponent() {
    return chooChooComponent;
  }

  public void fabEnable(int drawableId) {
    floatingActionButton.setVisibility(View.VISIBLE);
    floatingActionButton.setImageDrawable(getDrawable(drawableId));
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
      chooChooRouterManager.loadMapSearchFragment(stops, location);
    }
  }

  public void fabDisable() {
    floatingActionButton.setVisibility(View.GONE);
  }

  private Action1<RxMessage> handleRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_STOPS)) {
          stops = ((RxMessageStops) rxMessage).getMessage();
          initializeFragments();
        }
      }
    };
  }

  private Action1<RxMessageLocation> handleRxLocationMessages() {
    return new Action1<RxMessageLocation>() {
      @Override
      public void call(RxMessageLocation rxMessage) {
        location = rxMessage.getMessage();
        initializeFragments();
      }
    };
  }
}
