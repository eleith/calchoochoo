package com.eleith.calchoochoo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

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
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageArrivalOrDepartDateTime;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

public class ChooChooFragmentManager {
  private FragmentManager fragmentManager;
  private FragmentTransaction fragmentTransaction;
  private String lastStateID;
  private Bundle lastArguments;

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
    lastStateID = stateID;
    lastArguments = arguments;

    switch (stateID) {
      case STATE_SEARCH_FOR_STOPS:
        SearchInputFragment searchInputFragment = new SearchInputFragment();
        SearchResultsFragment searchResultsFragment = new SearchResultsFragment();

        searchResultsFragment.setArguments(arguments);
        searchInputFragment.setArguments(arguments);

        updateTopAndBottomFragments(searchInputFragment, searchResultsFragment);
        break;
      case STATE_SHOW_ALL_STOPS:
        StopDetailsFragment stopDetailsFragment = new StopDetailsFragment();
        StopCardsFragment stopCardsFragment = new StopCardsFragment();

        stopDetailsFragment.setArguments(arguments);
        stopCardsFragment.setArguments(arguments);

        updateTopAndBottomFragments(stopDetailsFragment, stopCardsFragment);
        break;
      case STATE_SHOW_TRIP:
        TripSummaryFragment tripSummaryFragment = new TripSummaryFragment();
        TripDetailFragment tripDetailFragment = new TripDetailFragment();

        tripSummaryFragment.setArguments(arguments);
        tripDetailFragment.setArguments(arguments);

        updateTopAndBottomFragments(tripSummaryFragment, tripDetailFragment);
        break;
      case STATE_SHOW_MAP:
        MapSearchFragment mapSearchFragment = new MapSearchFragment();

        mapSearchFragment.setArguments(arguments);

        updateTopAndBottomFragments(null, mapSearchFragment);
        break;
      case STATE_SHOW_TRIP_FILTER_EMPTY:
        TripFilterFragment tripFilterFragmentResults = new TripFilterFragment();
        TripFilterSelectMoreFragment tripFilterSelectMoreFragment = new TripFilterSelectMoreFragment();

        tripFilterFragmentResults.setArguments(arguments);
        tripFilterSelectMoreFragment.setArguments(arguments);

        updateTopAndBottomFragments(tripFilterFragmentResults, tripFilterSelectMoreFragment);
        break;
      case STATE_SHOW_TRIP_FILTER_RESULTS:
        RouteStopsFragment routeStopsFragment = new RouteStopsFragment();
        TripFilterFragment tripFilterFragment = new TripFilterFragment();

        routeStopsFragment.setArguments(arguments);
        tripFilterFragment.setArguments(arguments);

        updateTopAndBottomFragments(tripFilterFragment, routeStopsFragment);
        break;
    }
    commit();
  }

  private void updateTopAndBottomFragments(Fragment f1, Fragment f2) {
    updateTopFragment(f1);
    updateBottomFragment(f2);
  }

  private void updateTopFragment(Fragment fragment) {
    Fragment header = fragmentManager.findFragmentById(R.id.homeTopFragmentContainer);
    FragmentTransaction ft = getTransaction();

    if (header != null) {
      if (fragment == null) {
        ft.remove(header);
      } else {
        ft.replace(R.id.homeTopFragmentContainer, fragment);
      }
    } else if (fragment != null) {
      ft.add(R.id.homeTopFragmentContainer, fragment);
    }
  }

  private void updateBottomFragment(Fragment fragment) {
    Fragment main = fragmentManager.findFragmentById(R.id.homeFragmentContainer);
    FragmentTransaction ft = getTransaction();

    if (main != null) {
      if (fragment == null) {
        ft.remove(main);
      } else {
        ft.replace(R.id.homeFragmentContainer, fragment);
      }
    } else if (fragment != null) {
      ft.add(R.id.homeFragmentContainer, fragment);
    }
  }

  public void commit() {
    if (fragmentTransaction != null) {
      fragmentTransaction.addToBackStack(null);
      fragmentTransaction.commit();
      fragmentTransaction = null;
    }
  }

  public void loadSearchForSpotFragment(int reason) {
    Bundle arguments = new Bundle();
    ArrayList<Stop> stops = Queries.getAllStops();
    arguments.putParcelable(BundleKeys.STOPS, Parcels.wrap(stops));
    arguments.putInt(BundleKeys.SEARCH_REASON, reason);
    setNextState(ChooChooFragmentManager.STATE_SEARCH_FOR_STOPS, arguments);
  }

  public void loadStopsFragments(Stop stop) {
    Bundle arguments = new Bundle();
    arguments.putParcelable(BundleKeys.STOP, Parcels.wrap(stop));

    setNextState(ChooChooFragmentManager.STATE_SHOW_ALL_STOPS, arguments);
  }

  public void loadTripDetailsFragments(PossibleTrip possibleTrip, Stop stopDestination, Stop stopSource) {
    Bundle arguments = new Bundle();
    arguments.putParcelable(BundleKeys.POSSIBLE_TRIP, Parcels.wrap(possibleTrip));
    arguments.putParcelable(BundleKeys.STOP_DESTINATION, Parcels.wrap(stopDestination));
    arguments.putParcelable(BundleKeys.STOP_SOURCE, Parcels.wrap(stopSource));

    setNextState(ChooChooFragmentManager.STATE_SHOW_TRIP, arguments);
  }

  public void loadMapSearchFragment() {
    Bundle mapSearchArgs = new Bundle();
    ArrayList<Stop> stops = Queries.getAllStops();
    mapSearchArgs.putParcelable(BundleKeys.STOPS, Parcels.wrap(stops));

    setNextState(ChooChooFragmentManager.STATE_SHOW_MAP, mapSearchArgs);
  }

  public void loadTripFilterFragment(int stopMethod, LocalDateTime stopDateTime, Stop stopDestination, Stop stopSource) {
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
      ArrayList<PossibleTrip> possibleTrips = Queries.findTrips(stopSource, stopDestination, stopDateTime, stopMethod == RxMessageArrivalOrDepartDateTime.ARRIVING);
      arguments.putParcelable(BundleKeys.ROUTE_STOPS, Parcels.wrap(possibleTrips));

      setNextState(ChooChooFragmentManager.STATE_SHOW_TRIP_FILTER_RESULTS, arguments);
    } else {
      setNextState(ChooChooFragmentManager.STATE_SHOW_TRIP_FILTER_EMPTY, arguments);
    }
  }
}
