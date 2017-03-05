package com.eleith.calchoochoo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.Queries;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.Trips;
import com.eleith.calchoochoo.fragments.MapSearchFragment;
import com.eleith.calchoochoo.fragments.RouteStopsFragment;
import com.eleith.calchoochoo.fragments.SearchInputConfigureWidgetFragment;
import com.eleith.calchoochoo.fragments.SearchInputFragment;
import com.eleith.calchoochoo.fragments.SearchResultsConfigureWidgetFragment;
import com.eleith.calchoochoo.fragments.SearchResultsFragment;
import com.eleith.calchoochoo.fragments.StopCardsFragment;
import com.eleith.calchoochoo.fragments.StopDetailsFragment;
import com.eleith.calchoochoo.fragments.TripDetailFragment;
import com.eleith.calchoochoo.fragments.TripFilterFragment;
import com.eleith.calchoochoo.fragments.TripFilterSelectMoreFragment;
import com.eleith.calchoochoo.fragments.TripSummaryFragment;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStopsAndDetails;

import org.joda.time.LocalDateTime;
import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

public class ChooChooFragmentManager {
  private FragmentManager fragmentManager;
  private FragmentTransaction fragmentTransaction;

  public static final String STATE_CONFIGURE_WIDGET = "configure_widget";
  public static final String STATE_SEARCH_FOR_STOPS = "search_for_stops";
  public static final String STATE_SHOW_ALL_STOPS = "show_all_stops";
  public static final String STATE_SHOW_TRIP = "show_trip";
  public static final String STATE_SHOW_MAP = "show_map";
  public static final String STATE_SHOW_TRIP_FILTER_EMPTY = "show_trip_filter_empty";
  public static final String STATE_SHOW_TRIP_FILTER_RESULTS = "show_trip_filter_results";

  @Inject
  public ChooChooFragmentManager(FragmentManager fragmentManager) {
    this.fragmentManager = fragmentManager;
  }

  private FragmentTransaction getTransaction() {
    if (fragmentTransaction == null) {
      fragmentTransaction = fragmentManager.beginTransaction();
    }
    return fragmentTransaction;
  }

  private void setNextState(String stateID, Bundle arguments) {
    switch (stateID) {
      case STATE_SEARCH_FOR_STOPS:
        SearchInputFragment searchInputFragment = new SearchInputFragment();
        SearchResultsFragment searchResultsFragment = new SearchResultsFragment();

        searchResultsFragment.setArguments(arguments);
        searchInputFragment.setArguments(arguments);

        updateTopAndBottomFragments(searchInputFragment, searchResultsFragment, false);
        break;
      case STATE_CONFIGURE_WIDGET:
        SearchInputConfigureWidgetFragment searchInputConfigureWidgetFragment = new SearchInputConfigureWidgetFragment();
        SearchResultsConfigureWidgetFragment searchResultsConfigureWidgetFragment = new SearchResultsConfigureWidgetFragment();

        searchInputConfigureWidgetFragment.setArguments(arguments);
        searchResultsConfigureWidgetFragment.setArguments(arguments);

        updateTopAndBottomFragments(searchInputConfigureWidgetFragment, searchResultsConfigureWidgetFragment, false);
        break;
      case STATE_SHOW_ALL_STOPS:
        StopDetailsFragment stopDetailsFragment = new StopDetailsFragment();
        StopCardsFragment stopCardsFragment = new StopCardsFragment();

        stopDetailsFragment.setArguments(arguments);
        stopCardsFragment.setArguments(arguments);

        updateTopAndBottomFragments(stopDetailsFragment, stopCardsFragment, true);
        break;
      case STATE_SHOW_TRIP:
        TripSummaryFragment tripSummaryFragment = new TripSummaryFragment();
        TripDetailFragment tripDetailFragment = new TripDetailFragment();

        tripSummaryFragment.setArguments(arguments);
        tripDetailFragment.setArguments(arguments);

        updateTopAndBottomFragments(tripSummaryFragment, tripDetailFragment, true);
        break;
      case STATE_SHOW_MAP:
        MapSearchFragment mapSearchFragment = new MapSearchFragment();

        mapSearchFragment.setArguments(arguments);

        updateTopAndBottomFragments(null, mapSearchFragment, false);
        break;
      case STATE_SHOW_TRIP_FILTER_EMPTY:
        TripFilterFragment tripFilterFragmentResults = new TripFilterFragment();
        TripFilterSelectMoreFragment tripFilterSelectMoreFragment = new TripFilterSelectMoreFragment();

        tripFilterFragmentResults.setArguments(arguments);
        tripFilterSelectMoreFragment.setArguments(arguments);

        updateTopAndBottomFragments(tripFilterFragmentResults, tripFilterSelectMoreFragment, true);
        break;
      case STATE_SHOW_TRIP_FILTER_RESULTS:
        RouteStopsFragment routeStopsFragment = new RouteStopsFragment();
        TripFilterFragment tripFilterFragment = new TripFilterFragment();

        routeStopsFragment.setArguments(arguments);
        tripFilterFragment.setArguments(arguments);

        updateTopAndBottomFragments(tripFilterFragment, routeStopsFragment, true);
        break;
    }

    commit(stateID);
  }

  private void updateTopAndBottomFragments(Fragment f1, Fragment f2, Boolean useCoordinatorLayout) {
    updateTopFragment(f1, useCoordinatorLayout);
    updateBottomFragment(f2, useCoordinatorLayout);
  }

  private void updateTopFragment(Fragment fragment, Boolean useCoordinator) {
    Fragment header;
    int headerId;
    FragmentTransaction ft = getTransaction();

    if (useCoordinator) {
      Fragment linearLayoutTop = fragmentManager.findFragmentById(R.id.activityLinearLayoutTop);
      headerId = R.id.activityAppBarLayoutFragment;
      header = fragmentManager.findFragmentById(headerId);

      if (linearLayoutTop != null) {
        ft.hide(linearLayoutTop);
      }
    } else {
      Fragment appBar = fragmentManager.findFragmentById(R.id.activityAppBarLayout);
      headerId = R.id.activityLinearLayoutTop;
      header = fragmentManager.findFragmentById(headerId);

      if (appBar != null) {
        ft.hide(appBar);
      }
    }

    if (fragment == null) {
      if (header != null) {
        ft.hide(header);
      }
    } else {
      ft.replace(headerId, fragment);
    }
  }

  private void updateBottomFragment(Fragment fragment, Boolean useCoordinator) {
    Fragment main;
    int mainId;
        FragmentTransaction ft = getTransaction();

    if (useCoordinator) {
      Fragment linearLayoutBottom = fragmentManager.findFragmentById(R.id.activityLinearLayoutBottom);
      mainId = R.id.activityNestedScrollView;
      main = fragmentManager.findFragmentById(mainId);

      if (linearLayoutBottom != null) {
        ft.hide(linearLayoutBottom);
      }
    } else {
      Fragment nestedScrollView = fragmentManager.findFragmentById(R.id.activityNestedScrollView);
      mainId = R.id.activityLinearLayoutBottom;
      main = fragmentManager.findFragmentById(mainId);

      if (nestedScrollView != null) {
        ft.hide(nestedScrollView);
      }
    }

    if (fragment == null) {
      if (main != null) {
        ft.hide(main);
      }
    } else {
      ft.replace(mainId, fragment);
    }
  }

  public void commit(String stateId) {
    if (fragmentTransaction != null) {
      fragmentTransaction.addToBackStack(stateId);
      fragmentTransaction.commit();
      fragmentTransaction = null;
    }
  }

  public void loadSearchForSpotFragment(Stop stopSource, Stop stopDestination, Integer stopMethod, LocalDateTime stopDateTime) {
    Bundle arguments = new Bundle();
    arguments.putInt(BundleKeys.STOP_METHOD, stopMethod);
    arguments.putParcelable(BundleKeys.STOP_SOURCE, Parcels.wrap(stopSource));
    arguments.putParcelable(BundleKeys.STOP_DESTINATION, Parcels.wrap(stopDestination));
    arguments.putLong(BundleKeys.STOP_DATETIME, stopDateTime.toDate().getTime());
    setNextState(ChooChooFragmentManager.STATE_SEARCH_FOR_STOPS, arguments);
  }

  public void loadSearchForSpotFragment(Stop stop, Trips trip) {
    Bundle arguments = new Bundle();
    arguments.putParcelable(BundleKeys.STOP, Parcels.wrap(stop));
    arguments.putParcelable(BundleKeys.TRIP, Parcels.wrap(trip));
    setNextState(ChooChooFragmentManager.STATE_SEARCH_FOR_STOPS, arguments);
  }

  public void loadStopsFragments(Stop stop) {
    Bundle arguments = new Bundle();
    arguments.putParcelable(BundleKeys.STOP, Parcels.wrap(stop));

    setNextState(ChooChooFragmentManager.STATE_SHOW_ALL_STOPS, arguments);
  }

  public void loadTripDetailsFragments(PossibleTrip possibleTrip) {
    Bundle arguments = new Bundle();
    arguments.putParcelable(BundleKeys.POSSIBLE_TRIP, Parcels.wrap(possibleTrip));
    setNextState(ChooChooFragmentManager.STATE_SHOW_TRIP, arguments);
  }

  public void loadMapSearchFragment() {
    Bundle mapSearchArgs = new Bundle();
    ArrayList<Stop> stops = Queries.getAllParentStops();
    mapSearchArgs.putParcelable(BundleKeys.STOPS, Parcels.wrap(stops));

    setNextState(ChooChooFragmentManager.STATE_SHOW_MAP, mapSearchArgs);
  }

  public void loadTripFilterFragment(int stopMethod, LocalDateTime stopDateTime, Stop stopSource, Stop stopDestination) {
    Bundle arguments = new Bundle();
    arguments.putInt(BundleKeys.STOP_METHOD, stopMethod);

    if (stopSource != null) {
      arguments.putParcelable(BundleKeys.STOP_SOURCE, Parcels.wrap(stopSource));
    }

    if (stopDateTime != null) {
      arguments.putLong(BundleKeys.STOP_DATETIME, stopDateTime.toDate().getTime());
    }

    if (stopDestination != null) {
      arguments.putParcelable(BundleKeys.STOP_DESTINATION, Parcels.wrap(stopDestination));
    }

    if (stopSource != null && stopDestination != null && stopDateTime != null) {
      ArrayList<PossibleTrip> possibleTrips = Queries.findPossibleTrips(stopSource, stopDestination, stopDateTime, stopMethod == RxMessageStopsAndDetails.DETAIL_ARRIVING);
      arguments.putParcelable(BundleKeys.ROUTE_STOPS, Parcels.wrap(possibleTrips));

      setNextState(ChooChooFragmentManager.STATE_SHOW_TRIP_FILTER_RESULTS, arguments);
    } else {
      setNextState(ChooChooFragmentManager.STATE_SHOW_TRIP_FILTER_EMPTY, arguments);
    }
  }

  public void loadSearchWidgetConfigureFragment() {
    Bundle arguments = new Bundle();
    ArrayList<Stop> stops = Queries.getAllParentStops();
    arguments.putParcelable(BundleKeys.STOPS, Parcels.wrap(stops));
    setNextState(ChooChooFragmentManager.STATE_CONFIGURE_WIDGET, arguments);
  }
}
