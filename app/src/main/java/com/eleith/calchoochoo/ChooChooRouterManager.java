package com.eleith.calchoochoo;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.eleith.calchoochoo.data.PossibleTrain;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.StopTimes;
import com.eleith.calchoochoo.fragments.MapSearchFragment;
import com.eleith.calchoochoo.fragments.SearchInputConfigureWidgetFragment;
import com.eleith.calchoochoo.fragments.SearchInputFragment;
import com.eleith.calchoochoo.fragments.SearchResultsConfigureWidgetFragment;
import com.eleith.calchoochoo.fragments.SearchResultsFragment;
import com.eleith.calchoochoo.fragments.StopDetailsFragment;
import com.eleith.calchoochoo.fragments.StopSummaryFragment;
import com.eleith.calchoochoo.fragments.TripDetailFragment;
import com.eleith.calchoochoo.fragments.TripFilterFragment;
import com.eleith.calchoochoo.fragments.TripFilterSelectMoreFragment;
import com.eleith.calchoochoo.fragments.TripFilterSuggestionsFragment;
import com.eleith.calchoochoo.fragments.TripSummaryFragment;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.IntentKeys;
import com.eleith.calchoochoo.utils.PossibleTripUtils;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStopsAndDetails;

import org.joda.time.LocalDateTime;
import org.parceler.Parcels;

import java.util.ArrayList;

public class ChooChooRouterManager {
  private FragmentManager fragmentManager;
  private FragmentTransaction fragmentTransaction;

  public static final String STATE_CONFIGURE_WIDGET = "configure_widget";
  public static final String STATE_SEARCH_FOR_STOPS = "search_for_stops";
  public static final String STATE_SHOW_ALL_STOPS = "show_all_stops";
  public static final String STATE_SHOW_TRIP = "show_trip";
  public static final String STATE_SHOW_MAP = "show_map";
  public static final String STATE_SHOW_TRIP_FILTER = "show_trip_filter";

  public ChooChooRouterManager(FragmentManager fragmentManager) {
    this.fragmentManager = fragmentManager;
  }

  private FragmentTransaction getTransaction() {
    if (fragmentTransaction == null) {
      fragmentTransaction = fragmentManager.beginTransaction();
    }
    return fragmentTransaction;
  }

  private void setNextState(String stateID, Bundle arguments) {
    Boolean addToBackStack = false;
    fragmentTransaction = getTransaction();

    switch (stateID) {
      case STATE_SEARCH_FOR_STOPS:
        SearchInputFragment searchInputFragment = new SearchInputFragment();
        SearchResultsFragment searchResultsFragment = new SearchResultsFragment();

        searchResultsFragment.setArguments(arguments);
        searchInputFragment.setArguments(arguments);

        updateLinearLayoutFragments(searchInputFragment, searchResultsFragment, stateID);
        break;
      case STATE_CONFIGURE_WIDGET:
        SearchInputConfigureWidgetFragment searchInputConfigureWidgetFragment = new SearchInputConfigureWidgetFragment();
        SearchResultsConfigureWidgetFragment searchResultsConfigureWidgetFragment = new SearchResultsConfigureWidgetFragment();

        searchInputConfigureWidgetFragment.setArguments(arguments);
        searchResultsConfigureWidgetFragment.setArguments(arguments);

        updateLinearLayoutFragments(searchInputConfigureWidgetFragment, searchResultsConfigureWidgetFragment, stateID);
        break;
      case STATE_SHOW_ALL_STOPS:
        StopSummaryFragment stopSummaryFragment = new StopSummaryFragment();
        StopDetailsFragment stopDetailsFragment = new StopDetailsFragment();

        stopSummaryFragment.setArguments(arguments);
        stopDetailsFragment.setArguments(arguments);

        updateAppBarFragments(stopSummaryFragment, stopDetailsFragment, stateID);
        break;
      case STATE_SHOW_TRIP:
        TripSummaryFragment tripSummaryFragment = new TripSummaryFragment();
        TripDetailFragment tripDetailFragment = new TripDetailFragment();

        tripSummaryFragment.setArguments(arguments);
        tripDetailFragment.setArguments(arguments);

        updateAppBarFragments(tripSummaryFragment, tripDetailFragment, stateID);
        break;
      case STATE_SHOW_MAP:
        MapSearchFragment mapSearchFragment = new MapSearchFragment();
        mapSearchFragment.setArguments(arguments);
        updateLinearLayoutFragments(null, mapSearchFragment, stateID);
        break;
      case STATE_SHOW_TRIP_FILTER:
        TripFilterFragment tripFilterFragmentResults = new TripFilterFragment();
        TripFilterSelectMoreFragment tripFilterSelectMoreFragment = new TripFilterSelectMoreFragment();
        TripFilterSuggestionsFragment tripFilterSuggestionsFragment = new TripFilterSuggestionsFragment();

        tripFilterFragmentResults.setArguments(arguments);
        tripFilterSelectMoreFragment.setArguments(arguments);
        tripFilterSuggestionsFragment.setArguments(arguments);

        if (arguments.getString(BundleKeys.STOP_DESTINATION) != null && arguments.getString(BundleKeys.STOP_SOURCE) != null) {
          updateAppBarFragments(tripFilterFragmentResults, tripFilterSuggestionsFragment, stateID);
        } else {
          updateAppBarFragments(tripFilterFragmentResults, tripFilterSelectMoreFragment, stateID);
        }
        break;
    }

    commit(stateID, addToBackStack);
  }

  private void updateLinearLayoutFragments(Fragment top, Fragment bottom, String stateId) {
    int topId = R.id.activityLinearLayoutTop;
    int bottomId = R.id.activityLinearLayoutBottom;
    Fragment linearLayoutTop = fragmentManager.findFragmentById(topId);
    FragmentTransaction ft = getTransaction();

    if (top == null) {
      if (linearLayoutTop != null) {
        ft.hide(linearLayoutTop);
      }
    } else {
      if (linearLayoutTop != null && linearLayoutTop.isHidden()) {
        ft.show(linearLayoutTop);
      }
      ft.replace(topId, top, stateId + "top");
    }

    ft.replace(bottomId, bottom, stateId + "bottom");
  }

  private void updateAppBarFragments(Fragment top, Fragment bottom, String stateId) {
    int topId = R.id.activityAppBarLayoutFragment;
    int bottomId = R.id.activityMainLayout;
    FragmentTransaction ft = getTransaction();

    ft.replace(topId, top, stateId + "top");
    ft.replace(bottomId, bottom, stateId + "bottom");
  }

  public void commit(String stateId, Boolean addToBackStack) {
    if (fragmentTransaction != null) {
      if (addToBackStack) {
        fragmentTransaction.addToBackStack(stateId);
      }
      fragmentTransaction.commit();
      fragmentTransaction = null;
    }
  }

  public void loadSearchForSpotFragment(ArrayList<Stop> stops, Integer reason, ArrayList<String> filteredStopIds, Location location) {
    Bundle arguments = new Bundle();
    arguments.putParcelable(BundleKeys.STOPS, Parcels.wrap(stops));
    arguments.putInt(BundleKeys.SEARCH_REASON, reason);
    arguments.putStringArrayList(BundleKeys.STOP_IDS, filteredStopIds);
    arguments.putParcelable(BundleKeys.LOCATION, location);
    setNextState(ChooChooRouterManager.STATE_SEARCH_FOR_STOPS, arguments);
  }

  public void loadStopsFragments(Stop stop, ArrayList<PossibleTrain> possibleTrains, int direction) {
    Bundle arguments = new Bundle();
    arguments.putParcelable(BundleKeys.STOP, Parcels.wrap(stop));
    arguments.putParcelable(BundleKeys.POSSIBLE_TRAINS, Parcels.wrap(possibleTrains));
    arguments.putInt(BundleKeys.DIRECTION, direction);

    setNextState(ChooChooRouterManager.STATE_SHOW_ALL_STOPS, arguments);
  }

  public void loadTripDetailsFragments(PossibleTrip possibleTrip, ArrayList<StopTimes> tripStops) {
    Bundle arguments = new Bundle();
    arguments.putParcelable(BundleKeys.TRIP_STOP_STOPTIMES, Parcels.wrap(tripStops));
    arguments.putParcelable(BundleKeys.POSSIBLE_TRIP, Parcels.wrap(possibleTrip));
    setNextState(ChooChooRouterManager.STATE_SHOW_TRIP, arguments);
  }

  public void loadMapSearchFragment(ArrayList<Stop> stops, Location location) {
    Bundle mapSearchArgs = new Bundle();
    mapSearchArgs.putParcelable(BundleKeys.STOPS, Parcels.wrap(stops));
    mapSearchArgs.putParcelable(BundleKeys.LOCATION, location);
    setNextState(ChooChooRouterManager.STATE_SHOW_MAP, mapSearchArgs);
  }

  public void loadTripFilterActivity(Activity activity, String stopSourceId, String stopDestinationId) {
    loadTripFilterActivity(activity, stopSourceId, stopDestinationId, RxMessageStopsAndDetails.DETAIL_DEPARTING, new LocalDateTime().toDateTime().getMillis());
  }

  public void loadTripFilterActivity(Activity activity, String stopSourceId, String stopDestinationId, Integer stopMethod, Long stopDateTime) {
    Intent intent = new Intent(activity, TripFilterActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    Bundle bundle = new Bundle();
    if (stopSourceId != null) {
      bundle.putString(BundleKeys.STOP_SOURCE, stopSourceId);
    }
    if (stopDestinationId != null) {
      bundle.putString(BundleKeys.STOP_DESTINATION, stopDestinationId);
    }
    bundle.putLong(BundleKeys.STOP_DATETIME, stopDateTime);
    bundle.putInt(BundleKeys.STOP_METHOD, stopMethod);
    intent.putExtras(bundle);
    activity.startActivity(intent);
  }

  public void loadTripActivity(Activity activity, String tripId, String sourceId) {
    loadTripActivity(activity, tripId, sourceId, null, null);
  }

  public void loadTripActivity(Activity activity, String tripId, String sourceId, String destinationId, ActivityOptionsCompat sharedElements) {
    Intent intent = new Intent(activity, TripActivity.class);

    Bundle bundle = new Bundle();
    bundle.putString(BundleKeys.TRIP, tripId);
    bundle.putString(BundleKeys.STOP_SOURCE, sourceId);
    bundle.putString(BundleKeys.STOP_DESTINATION, destinationId);
    intent.putExtras(bundle);

    if (sharedElements != null) {
      activity.startActivity(intent, sharedElements.toBundle());
    } else {
      activity.startActivity(intent);
    }
  }

  public void loadStopSearchActivity(Activity activity, Integer reason, ArrayList<String> filterOutStopIds) {
    Intent intent = new Intent(activity, StopSearchActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    Bundle bundle = new Bundle();
    bundle.putStringArrayList(BundleKeys.STOPS, filterOutStopIds);
    bundle.putInt(BundleKeys.SEARCH_REASON, reason);
    intent.putExtras(bundle);
    activity.startActivityForResult(intent, IntentKeys.STOP_SEARCH_RESULT);
  }

  public void loadStopActivity(Activity activity, String stopId) {
    Intent intent = new Intent(activity, StopActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    Bundle bundle = new Bundle();
    bundle.putString(BundleKeys.STOP, stopId);
    intent.putExtras(bundle);
    activity.startActivity(intent);
  }

  public void loadStopSearchReturnActivity(Activity activity, Integer reason, String stop_id) {
    Intent returnIntent = new Intent();
    returnIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    Bundle bundle = new Bundle();
    bundle.putString(BundleKeys.STOP, stop_id);
    bundle.putInt(BundleKeys.SEARCH_REASON, reason);
    returnIntent.putExtras(bundle);
    activity.setResult(Activity.RESULT_OK, returnIntent);
    activity.finish();
  }

  public void loadStopSearchCancelActivity(Activity activity) {
    Intent returnIntent = new Intent();
    returnIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    activity.setResult(Activity.RESULT_CANCELED, returnIntent);
    activity.finish();
  }

  public void loadTripFilterFragment(ArrayList<PossibleTrip> possibleTrips, int stopMethod, LocalDateTime stopDateTime, String stopSourceId, String stopDestinationId) {
    Bundle arguments = new Bundle();

    arguments.putInt(BundleKeys.STOP_METHOD, stopMethod);
    if (stopSourceId != null) {
      arguments.putString(BundleKeys.STOP_SOURCE, stopSourceId);
    }
    if (stopDestinationId != null) {
      arguments.putString(BundleKeys.STOP_DESTINATION, stopDestinationId);
    }

    if (stopDateTime != null) {
      arguments.putLong(BundleKeys.STOP_DATETIME, stopDateTime.toDateTime().getMillis());
    } else {
      arguments.putLong(BundleKeys.STOP_DATETIME, new LocalDateTime().toDateTime().getMillis());
    }

    if (possibleTrips != null && possibleTrips.size() > 0) {
      ArrayList<PossibleTrip> possibleTripsFiltered = PossibleTripUtils.filterByDateTimeAndDirection(possibleTrips, stopDateTime, stopMethod == RxMessageStopsAndDetails.DETAIL_ARRIVING);
      if (possibleTripsFiltered.size() > 0) {
        arguments.putParcelable(BundleKeys.POSSIBLE_TRIPS, Parcels.wrap(possibleTripsFiltered));
      }
    }

    setNextState(ChooChooRouterManager.STATE_SHOW_TRIP_FILTER, arguments);
  }

  public void loadSearchWidgetConfigureFragment() {
    setNextState(ChooChooRouterManager.STATE_CONFIGURE_WIDGET, null);
  }
}
