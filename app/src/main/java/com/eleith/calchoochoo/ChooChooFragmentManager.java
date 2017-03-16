package com.eleith.calchoochoo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.Trips;
import com.eleith.calchoochoo.fragments.MapSearchFragment;
import com.eleith.calchoochoo.fragments.RouteStopsFragment;
import com.eleith.calchoochoo.fragments.SearchInputConfigureWidgetFragment;
import com.eleith.calchoochoo.fragments.SearchInputFragment;
import com.eleith.calchoochoo.fragments.SearchResultsConfigureWidgetFragment;
import com.eleith.calchoochoo.fragments.SearchResultsFragment;
import com.eleith.calchoochoo.fragments.StopDetailsFragment;
import com.eleith.calchoochoo.fragments.StopSummaryFragment;
import com.eleith.calchoochoo.fragments.TripDetailFragment;
import com.eleith.calchoochoo.fragments.TripFilterFragment;
import com.eleith.calchoochoo.fragments.TripFilterSelectMoreFragment;
import com.eleith.calchoochoo.fragments.TripSummaryFragment;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.PossibleTripUtils;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStopsAndDetails;

import org.joda.time.LocalDateTime;
import org.parceler.Parcels;

import java.util.ArrayList;

public class ChooChooFragmentManager {
  private FragmentManager fragmentManager;
  private FragmentTransaction fragmentTransaction;
  private ArrayList<View> sharedTransitions;

  public static final String STATE_CONFIGURE_WIDGET = "configure_widget";
  public static final String STATE_SEARCH_FOR_STOPS = "search_for_stops";
  public static final String STATE_SHOW_ALL_STOPS = "show_all_stops";
  public static final String STATE_SHOW_TRIP = "show_trip";
  public static final String STATE_SHOW_MAP = "show_map";
  public static final String STATE_SHOW_TRIP_FILTER = "show_trip_filter";

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
    Boolean addToBackStack = true;
    fragmentTransaction = getTransaction();

    switch (stateID) {
      case STATE_SEARCH_FOR_STOPS:
        SearchInputFragment searchInputFragment = new SearchInputFragment();
        SearchResultsFragment searchResultsFragment = new SearchResultsFragment();

        searchResultsFragment.setArguments(arguments);
        searchInputFragment.setArguments(arguments);

        updateTopAndBottomFragments(searchInputFragment, searchResultsFragment, false, stateID);
        break;
      case STATE_CONFIGURE_WIDGET:
        SearchInputConfigureWidgetFragment searchInputConfigureWidgetFragment = new SearchInputConfigureWidgetFragment();
        SearchResultsConfigureWidgetFragment searchResultsConfigureWidgetFragment = new SearchResultsConfigureWidgetFragment();

        searchInputConfigureWidgetFragment.setArguments(arguments);
        searchResultsConfigureWidgetFragment.setArguments(arguments);

        updateTopAndBottomFragments(searchInputConfigureWidgetFragment, searchResultsConfigureWidgetFragment, false, stateID);
        break;
      case STATE_SHOW_ALL_STOPS:
        StopSummaryFragment stopSummaryFragment = new StopSummaryFragment();
        StopDetailsFragment stopDetailsFragment = new StopDetailsFragment();

        stopSummaryFragment.setArguments(arguments);
        stopDetailsFragment.setArguments(arguments);

        updateTopAndBottomFragments(stopSummaryFragment, stopDetailsFragment, true, stateID);
        break;
      case STATE_SHOW_TRIP:
        TripSummaryFragment tripSummaryFragment = new TripSummaryFragment();
        TripDetailFragment tripDetailFragment = new TripDetailFragment();

        tripSummaryFragment.setArguments(arguments);
        tripDetailFragment.setArguments(arguments);

        updateTopAndBottomFragments(tripSummaryFragment, tripDetailFragment, true, stateID);
        break;
      case STATE_SHOW_MAP:
        MapSearchFragment mapSearchFragment = new MapSearchFragment();

        mapSearchFragment.setArguments(arguments);

        updateTopAndBottomFragments(null, mapSearchFragment, false, stateID);
        addToBackStack = false;
        break;
      case STATE_SHOW_TRIP_FILTER:
        TripFilterFragment tripFilterFragmentResults = new TripFilterFragment();
        TripFilterSelectMoreFragment tripFilterSelectMoreFragment = new TripFilterSelectMoreFragment();
        RouteStopsFragment routeStopsFragment = new RouteStopsFragment();

        tripFilterFragmentResults.setArguments(arguments);
        tripFilterSelectMoreFragment.setArguments(arguments);
        routeStopsFragment.setArguments(arguments);

        if (arguments.getParcelable(BundleKeys.ROUTE_STOPS) != null) {
          updateTopAndBottomFragments(tripFilterFragmentResults, routeStopsFragment, true, stateID);
        } else {
          updateTopAndBottomFragments(tripFilterFragmentResults, tripFilterSelectMoreFragment, true, stateID);
        }
        break;
    }

    commit(stateID, addToBackStack);
  }

  private void updateTopAndBottomFragments(Fragment f1, Fragment f2, Boolean useCoordinatorLayout, String stateId) {
    updateBottomFragment(f2, useCoordinatorLayout, stateId + "bottom");
    updateTopFragment(f1, useCoordinatorLayout, stateId + "top");
  }

  private void updateTopFragment(Fragment fragment, Boolean useCoordinator, String tag) {
    Fragment linearLayoutTop = fragmentManager.findFragmentById(R.id.activityLinearLayoutTop);
    Fragment appBar = fragmentManager.findFragmentById(R.id.activityAppBarLayoutFragment);
    FragmentTransaction ft = getTransaction();

    if (useCoordinator) {
      if (linearLayoutTop != null && linearLayoutTop.isVisible()) {
        ft.remove(linearLayoutTop);
      }
      if (fragment == null) {
        ft.remove(appBar);
      } else {
        if (appBar != null) {
          ft.replace(R.id.activityAppBarLayoutFragment, fragment, tag);
        } else {
          ft.add(R.id.activityAppBarLayoutFragment, fragment, tag);
        }
      }
    } else {
      if (appBar != null && appBar.isVisible()) {
        ft.remove(appBar);
      }
      if (fragment == null) {
        if (linearLayoutTop != null && linearLayoutTop.isVisible()) {
          ft.remove(linearLayoutTop);
        }
      } else {
        if (linearLayoutTop != null) {
          ft.replace(R.id.activityLinearLayoutTop, fragment, tag);
        } else {
          ft.add(R.id.activityLinearLayoutTop, fragment, tag);
        }
      }
    }
  }

  private void updateBottomFragment(Fragment fragment, Boolean useCoordinator, String tag) {
    Fragment nestedScrollView = fragmentManager.findFragmentById(R.id.activityNestedScrollView);
    Fragment linearLayoutBottom = fragmentManager.findFragmentById(R.id.activityLinearLayoutBottom);
    FragmentTransaction ft = getTransaction();

    if (useCoordinator) {
      if (linearLayoutBottom != null && linearLayoutBottom.isVisible()) {
        ft.remove(linearLayoutBottom);
      }

      ft.replace(R.id.activityNestedScrollView, fragment);
    } else {
      if (nestedScrollView != null && nestedScrollView.isVisible()) {
        ft.hide(nestedScrollView);
      }
      if (linearLayoutBottom == null) {
        ft.add(R.id.activityLinearLayoutBottom, fragment, tag);
      } else {
        ft.replace(R.id.activityLinearLayoutBottom, fragment, tag);
      }
    }
  }

  public void commit(String stateId, Boolean addToBackStack) {
    if (fragmentTransaction != null) {
      if (addToBackStack) {
        fragmentTransaction.addToBackStack(stateId);
      }

      if (sharedTransitions != null) {
        for (View view : sharedTransitions) {
          fragmentTransaction.addSharedElement(view, view.getTransitionName());
        }
      }

      fragmentTransaction.commit();
      fragmentTransaction = null;
      sharedTransitions = null;
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
    loadTripDetailsFragments(possibleTrip, null);
  }

  public void loadTripDetailsFragments(PossibleTrip possibleTrip, ArrayList<View> views) {
    this.sharedTransitions = views;
    Bundle arguments = new Bundle();
    arguments.putParcelable(BundleKeys.POSSIBLE_TRIP, Parcels.wrap(possibleTrip));
    setNextState(ChooChooFragmentManager.STATE_SHOW_TRIP, arguments);
  }

  public void loadMapSearchFragment() {
    Bundle mapSearchArgs = new Bundle();
    setNextState(ChooChooFragmentManager.STATE_SHOW_MAP, mapSearchArgs);
  }

  public void loadTripFilterFragment(ArrayList<PossibleTrip> possibleTrips, int stopMethod, LocalDateTime stopDateTime, Stop stopSource, Stop stopDestination) {
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

    if (stopSource != null && stopDestination != null && stopDateTime != null && possibleTrips.size() > 0) {
      ArrayList<PossibleTrip> possibleTripsFiltered = PossibleTripUtils.filterByDateTimeAndDirection(possibleTrips, stopDateTime, stopMethod == RxMessageStopsAndDetails.DETAIL_ARRIVING);
      if (possibleTripsFiltered.size() > 0) {
        arguments.putParcelable(BundleKeys.ROUTE_STOPS, Parcels.wrap(possibleTripsFiltered));
      }
    }

    setNextState(ChooChooFragmentManager.STATE_SHOW_TRIP_FILTER, arguments);
  }

  public void loadSearchWidgetConfigureFragment() {
    setNextState(ChooChooFragmentManager.STATE_CONFIGURE_WIDGET, null);
  }
}
