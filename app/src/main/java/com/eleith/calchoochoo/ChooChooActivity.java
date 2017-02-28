package com.eleith.calchoochoo;

import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;

import com.eleith.calchoochoo.dagger.ScheduleExplorerActivityComponent;
import com.eleith.calchoochoo.dagger.ScheduleExplorerActivityModule;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.Queries;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.DeviceLocation;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageArrivalOrDepartDateTime;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessagePairStopReason;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessagePossibleTrip;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStop;
import com.google.android.gms.common.api.GoogleApiClient;

import org.joda.time.LocalDateTime;
import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;

public class ChooChooActivity extends AppCompatActivity {
  private Stop stopDestination;
  private Stop stopSource;
  private Integer stopMethod = RxMessageArrivalOrDepartDateTime.DEPARTING;
  private LocalDateTime stopDateTime = new LocalDateTime();
  private ScheduleExplorerActivityComponent scheduleExplorerActivityComponent;
  private Subscription subscription;

  @Inject
  RxBus rxBus;
  @Inject
  GoogleApiClient googleApiClient;
  @Inject
  DeviceLocation deviceLocation;
  @Inject
  ChooChooFragmentManager chooChooFragmentManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    scheduleExplorerActivityComponent = ChooChooApplication.from(this).getAppComponent()
        .activityComponent(new ScheduleExplorerActivityModule(this));
    scheduleExplorerActivityComponent.inject(this);

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_schedule_explorer);

    subscription = rxBus.observeEvents(RxMessage.class).subscribe(handleScheduleExplorerRxMessages());

    if (savedInstanceState == null) {
      chooChooFragmentManager.loadMapSearchFragment();
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
    subscription.unsubscribe();
  }

  private Action1<RxMessage> handleScheduleExplorerRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.SEARCH_RESULT_PAIR)) {
          Pair<Stop, Integer> pair = ((RxMessagePairStopReason) rxMessage).getMessage();
          if (pair.second.equals(RxMessagePairStopReason.SEARCH_REASON_DESTINATION)) {
            stopDestination = pair.first;
          } else {
            stopSource = pair.first;
          }
          chooChooFragmentManager.loadTripFilterFragment(stopMethod, stopDateTime, stopDestination, stopSource);
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.DESTINATION_SELECTED)) {
          chooChooFragmentManager.loadSearchForSpotFragment(RxMessagePairStopReason.SEARCH_REASON_DESTINATION);
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.SOURCE_SELECTED)) {
          chooChooFragmentManager.loadSearchForSpotFragment(RxMessagePairStopReason.SEARCH_REASON_SOURCE);
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.SWITCH_SOURCE_DESTINATION_SELECTED)) {
          Stop tempStop = stopDestination;
          stopDestination = stopSource;
          stopSource = tempStop;
          chooChooFragmentManager.loadTripFilterFragment(stopMethod, stopDateTime, stopDestination, stopSource);
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.DATE_TIME_SELECTED)) {
          Pair<Integer, LocalDateTime> pair = ((RxMessageArrivalOrDepartDateTime) rxMessage).getMessage();
          stopMethod = pair.first;
          stopDateTime = pair.second;
          chooChooFragmentManager.loadTripFilterFragment(stopMethod, stopDateTime, stopDestination, stopSource);
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.DATE_TIME_SELECTED)) {
          Pair<Integer, LocalDateTime> pair = ((RxMessageArrivalOrDepartDateTime) rxMessage).getMessage();
          stopMethod = pair.first;
          stopDateTime = pair.second;
          chooChooFragmentManager.loadTripFilterFragment(stopMethod, stopDateTime, stopDestination, stopSource);
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.TRIP_SELECTED)) {
          PossibleTrip possibleTrip = ((RxMessagePossibleTrip) rxMessage).getMessage();
          chooChooFragmentManager.loadTripDetailsFragments(possibleTrip, stopDestination, stopSource);
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.STOP_SELECTED)) {
          Stop stop = ((RxMessageStop) rxMessage).getMessage();
          chooChooFragmentManager.loadStopsFragments(stop);
        }
      }
    };
  }

  public ScheduleExplorerActivityComponent getComponent() {
    return scheduleExplorerActivityComponent;
  }
}
