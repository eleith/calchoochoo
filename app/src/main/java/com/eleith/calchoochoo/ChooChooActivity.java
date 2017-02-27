package com.eleith.calchoochoo;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;

import com.eleith.calchoochoo.dagger.ScheduleExplorerActivityComponent;
import com.eleith.calchoochoo.dagger.ScheduleExplorerActivityModule;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.Queries;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.fragments.MapSearchFragment;
import com.eleith.calchoochoo.fragments.RouteStopsFragment;
import com.eleith.calchoochoo.fragments.SearchInputFragment;
import com.eleith.calchoochoo.fragments.SearchResultsFragment;
import com.eleith.calchoochoo.fragments.StopCardsFragment;
import com.eleith.calchoochoo.fragments.StopDetailsFragment;
import com.eleith.calchoochoo.fragments.TripDetailFragment;
import com.eleith.calchoochoo.fragments.TripFilterFragment;
import com.eleith.calchoochoo.fragments.TripFilterSelectMoreFragment;
import com.eleith.calchoochoo.fragments.TripSummaryFragment;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.DeviceLocation;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageArrivalOrDepartDateTime;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessagePairStopReason;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessagePossibleTrip;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStop;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageString;
import com.google.android.gms.common.api.GoogleApiClient;

import org.joda.time.LocalDateTime;
import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;

public class ChooChooActivity extends AppCompatActivity {
  private SearchResultsFragment searchResultsFragment;
  private SearchInputFragment searchInputFragment;
  private Stop stopDestination;
  private Stop stopSource;
  private Integer stopMethod = RxMessageArrivalOrDepartDateTime.DEPARTING;
  private LocalDateTime stopDateTime = new LocalDateTime();
  private ScheduleExplorerActivityComponent scheduleExplorerActivityComponent;
  private Subscription subscription;

  @Inject
  RxBus rxbus;
  @Inject
  GoogleApiClient googleApiClient;
  @Inject
  DeviceLocation deviceLocation;
  @Inject
  ChooChooFragmentManager chooChooFragmentManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getComponent();
    setContentView(R.layout.activity_schedule_explorer);

    subscription = rxbus.observeEvents(RxMessage.class).subscribe(handleScheduleExplorerRxMessages());
    showMapSearchFragment();
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
          showDestinationSourceFragment();
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.DESTINATION_SELECTED)) {
          selectDestination();
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.SOURCE_SELECTED)) {
          selectSource();
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.SWITCH_SOURCE_DESTINATION_SELECTED)) {
          Stop tempStop = stopDestination;
          stopDestination = stopSource;
          stopSource = tempStop;
          showDestinationSourceFragment();
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.DATE_TIME_SELECTED)) {
          Pair<Integer, LocalDateTime> pair = ((RxMessageArrivalOrDepartDateTime) rxMessage).getMessage();
          stopMethod = pair.first;
          stopDateTime = pair.second;
          showDestinationSourceFragment();
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.DATE_TIME_SELECTED)) {
          Pair<Integer, LocalDateTime> pair = ((RxMessageArrivalOrDepartDateTime) rxMessage).getMessage();
          stopMethod = pair.first;
          stopDateTime = pair.second;
          showDestinationSourceFragment();
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.TRIP_SELECTED)) {
          PossibleTrip possibleTrip = ((RxMessagePossibleTrip) rxMessage).getMessage();
          showTripDetailsFragments(possibleTrip);
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.STOP_SELECTED)) {
          Stop stop = ((RxMessageStop) rxMessage).getMessage();
          showStopsFragments(stop);
        }
      }
    };
  }

  private void selectDestination() {
    searchInputFragment = new SearchInputFragment();
    searchResultsFragment = new SearchResultsFragment();

    Bundle searchResultsArgs = new Bundle();
    ArrayList<Stop> stops = Queries.getAllStops();
    searchResultsArgs.putParcelable(BundleKeys.STOPS, Parcels.wrap(stops));
    searchResultsArgs.putInt(BundleKeys.SEARCH_REASON, RxMessagePairStopReason.SEARCH_REASON_DESTINATION);
    searchResultsFragment.setArguments(searchResultsArgs);

    chooChooFragmentManager.updateTopAndBottomFragments(searchInputFragment, searchResultsFragment);
    chooChooFragmentManager.commit();
  }

  private void selectSource() {
    searchInputFragment = new SearchInputFragment();
    searchResultsFragment = new SearchResultsFragment();

    Bundle searchResultsArgs = new Bundle();
    ArrayList<Stop> stops = Queries.getAllStops();
    searchResultsArgs.putParcelable(BundleKeys.STOPS, Parcels.wrap(stops));
    searchResultsArgs.putInt(BundleKeys.SEARCH_REASON, RxMessagePairStopReason.SEARCH_REASON_SOURCE);
    searchResultsFragment.setArguments(searchResultsArgs);

    chooChooFragmentManager.updateTopAndBottomFragments(searchInputFragment, searchResultsFragment);
    chooChooFragmentManager.commit();
  }

  private void showStopsFragments(Stop stop) {
    Bundle stopSummaryArgs = new Bundle();

    StopDetailsFragment stopDetailsFragment = new StopDetailsFragment();
    StopCardsFragment stopCardsFragment = new StopCardsFragment();

    stopSummaryArgs.putParcelable(BundleKeys.STOP, Parcels.wrap(stop));

    stopDetailsFragment.setArguments(stopSummaryArgs);
    stopCardsFragment.setArguments(stopSummaryArgs);

    chooChooFragmentManager.updateTopAndBottomFragments(stopDetailsFragment, stopCardsFragment);
    chooChooFragmentManager.commit();
  }

  private void showTripDetailsFragments(PossibleTrip possibleTrip) {
    Bundle tripSummaryArgs = new Bundle();

    TripSummaryFragment tripSummaryFragment = new TripSummaryFragment();
    TripDetailFragment tripDetailFragment = new TripDetailFragment();

    tripSummaryArgs.putParcelable(BundleKeys.POSSIBLE_TRIP, Parcels.wrap(possibleTrip));
    tripSummaryArgs.putParcelable(BundleKeys.STOP_DESTINATION, Parcels.wrap(stopDestination));
    tripSummaryArgs.putParcelable(BundleKeys.STOP_SOURCE, Parcels.wrap(stopSource));

    tripSummaryFragment.setArguments(tripSummaryArgs);
    tripDetailFragment.setArguments(tripSummaryArgs);

    chooChooFragmentManager.updateTopAndBottomFragments(tripSummaryFragment, tripDetailFragment);
    chooChooFragmentManager.commit();
  }

  private void showMapSearchFragment() {
    Bundle mapSearchArgs = new Bundle();
    MapSearchFragment mapSearchFragment = new MapSearchFragment();
    ArrayList<Stop> stops = Queries.getAllStops();
    mapSearchArgs.putParcelable(BundleKeys.STOPS, Parcels.wrap(stops));
    mapSearchFragment.setArguments(mapSearchArgs);

    chooChooFragmentManager.updateTopAndBottomFragments(null, mapSearchFragment);
    chooChooFragmentManager.commit();
  }

  private void showDestinationSourceFragment() {
    Bundle destinationSourceArgs = new Bundle();

    TripFilterFragment tripFilterFragment = new TripFilterFragment();
    destinationSourceArgs.putParcelable(BundleKeys.STOP_DESTINATION, Parcels.wrap(stopDestination));
    destinationSourceArgs.putParcelable(BundleKeys.STOP_SOURCE, Parcels.wrap(stopSource));
    destinationSourceArgs.putInt(BundleKeys.STOP_METHOD, stopMethod);
    destinationSourceArgs.putLong(BundleKeys.STOP_DATETIME, stopDateTime.toDate().getTime());
    tripFilterFragment.setArguments(destinationSourceArgs);

    chooChooFragmentManager.updateTopAndBottomFragments(tripFilterFragment, new TripFilterSelectMoreFragment());
    updateRouteFragment();
    chooChooFragmentManager.commit();
  }

  private void updateRouteFragment() {
    if (stopSource != null && stopDestination != null && stopDateTime != null) {
      ArrayList<PossibleTrip> possibleTrips = Queries.findTrips(stopSource, stopDestination, stopDateTime, stopMethod == RxMessageArrivalOrDepartDateTime.ARRIVING);

      Bundle routeStopsArgs = new Bundle();
      routeStopsArgs.putParcelable(BundleKeys.ROUTE_STOPS, Parcels.wrap(possibleTrips));

      RouteStopsFragment routeStopsFragment = new RouteStopsFragment();
      routeStopsFragment.setArguments(routeStopsArgs);

      chooChooFragmentManager.updateBottomFragment(routeStopsFragment);
    }
  }

  public ScheduleExplorerActivityComponent getComponent() {
    if (scheduleExplorerActivityComponent == null) {
      scheduleExplorerActivityComponent = ((ChooChooApplication) getApplication()).getAppComponent()
          .activityComponent(new ScheduleExplorerActivityModule(this));

      scheduleExplorerActivityComponent.inject(this);
    }
    return scheduleExplorerActivityComponent;
  }
}
