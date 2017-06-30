package com.eleith.calchoochoo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.eleith.calchoochoo.dagger.ChooChooComponent;
import com.eleith.calchoochoo.dagger.ChooChooModule;
import com.eleith.calchoochoo.data.ChooChooLoader;
import com.eleith.calchoochoo.data.PossibleTrain;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageNextTrains;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStop;

import org.joda.time.LocalDateTime;
import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

public class StopActivity extends AppCompatActivity {
  private ChooChooComponent chooChooComponent;
  private Subscription subscription;
  private ArrayList<PossibleTrain> possibleTrains;
  private Stop stop;

  @Inject
  RxBus rxBus;
  @Inject
  ChooChooRouterManager chooChooRouterManager;
  @Inject
  ChooChooLoader chooChooLoader;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    chooChooComponent = ChooChooApplication.from(this).getAppComponent().activityComponent(new ChooChooModule(this));
    chooChooComponent.inject(this);

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_appbar_drawer);
    ButterKnife.bind(this);

    subscription = rxBus.observeEvents(RxMessage.class).subscribe(new HandleRxMessages());
    ChooChooDrawer chooChooDrawer = new ChooChooDrawer(this, getWindow().getDecorView().getRootView());
    Intent intent = getIntent();

    if (savedInstanceState != null && intent == null) {
      unWrapBundle(savedInstanceState);
    } else if(intent != null){
      Bundle bundle = intent.getExtras();
      if (bundle != null) {
        String stopId = bundle.getString(BundleKeys.STOP);
        chooChooLoader.loadPossibleTrains(stopId, new LocalDateTime());
        chooChooLoader.loadStopByParentId(stopId);
        chooChooDrawer.setStopSource(stopId);
      }
    } else {
        chooChooRouterManager.loadMapSearchActivity(this);
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (subscription.isUnsubscribed()) {
      subscription = rxBus.observeEvents(RxMessage.class).subscribe(new HandleRxMessages());
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    subscription.unsubscribe();
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
  protected void onDestroy() {
    super.onDestroy();
  }

  public ChooChooComponent getComponent() {
    return chooChooComponent;
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(BundleKeys.STOP, Parcels.wrap(stop));
    outState.putParcelable(BundleKeys.POSSIBLE_TRAINS, Parcels.wrap(possibleTrains));
    super.onSaveInstanceState(outState);
  }

  private void unWrapBundle(Bundle bundle) {
    if (bundle != null) {
      stop = Parcels.unwrap(bundle.getParcelable(BundleKeys.STOP));
      possibleTrains = Parcels.unwrap(bundle.getParcelable(BundleKeys.POSSIBLE_TRAINS));
    }
  }

  private void loadFragment() {
    if (possibleTrains != null && stop != null) {
      chooChooRouterManager.loadStopsFragments(stop, possibleTrains);
    }
  }

  private class HandleRxMessages implements Action1<RxMessage> {
    @Override
    public void call(RxMessage rxMessage) {
      if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_NEXT_TRAINS)) {
        possibleTrains = ((RxMessageNextTrains) rxMessage).getMessage();
        loadFragment();
      } else if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_STOP)) {
        stop = ((RxMessageStop) rxMessage).getMessage();
        loadFragment();
      }
    }
  }
}
