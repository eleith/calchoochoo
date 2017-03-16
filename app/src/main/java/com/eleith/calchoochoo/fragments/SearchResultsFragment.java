package com.eleith.calchoochoo.fragments;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleith.calchoochoo.ChooChooActivity;
import com.eleith.calchoochoo.ChooChooFragmentManager;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.adapters.SearchResultsViewAdapter;
import com.eleith.calchoochoo.data.ChooChooLoader;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.Trips;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.DeviceLocation;
import com.eleith.calchoochoo.utils.PossibleTripUtils;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessagePossibleTrip;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessagePossibleTrips;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStopMethodAndDateTime;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStops;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageString;

import org.joda.time.LocalDateTime;
import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

import static com.eleith.calchoochoo.utils.StopUtils.filterByFuzzySearch;

public class SearchResultsFragment extends Fragment {
  private ArrayList<Stop> parentStops = null;
  private ArrayList<Stop> searchStops = null;
  private Stop stopSource;
  private int stopMethod;
  private Stop stopDestination;
  private Long stopDateTime;
  private Stop stopUnknown;
  private String tripId;
  private Subscription subscription;

  @BindView(R.id.search_results_empty_state)
  TextView searchResultsEmptyState;
  @BindView(R.id.search_results_recyclerview)
  RecyclerView searchResultsRecyclerView;

  @Inject
  RxBus rxBus;
  @Inject
  SearchResultsViewAdapter searchResultsViewAdapter;
  @Inject
  DeviceLocation deviceLocation;
  @Inject
  ChooChooFragmentManager chooChooFragmentManager;
  @Inject
  ChooChooLoader chooChooLoader;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Activity activity = getActivity();
    super.onCreate(savedInstanceState);
    if (activity instanceof ChooChooActivity) {
      ((ChooChooActivity) activity).getComponent().inject(this);
    }

    unWrapBundle(savedInstanceState != null ? savedInstanceState : getArguments());
    setEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.slide_up));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_search_results, container, false);
    ButterKnife.bind(this, view);
    unWrapBundle(savedInstanceState);

    searchResultsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    deviceLocation.requestLocation(new DeviceLocation.LocationGetListener() {
      @Override
      public void onLocationGet(Location location) {
        searchResultsViewAdapter.setLocation(location);
      }
    });

    subscription = rxBus.observeEvents(RxMessage.class).subscribe(handleRxMessages());
    searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    searchResultsRecyclerView.setAdapter(searchResultsViewAdapter);

    chooChooLoader.loadParentStops();
    if (tripId != null && stopUnknown != null) {
      chooChooLoader.loadStopsOnTrip(tripId);
    }

    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    subscription.unsubscribe();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    if (stopSource != null) {
      outState.putParcelable(BundleKeys.STOP_SOURCE, Parcels.wrap(stopSource));
    }
    if (stopDestination != null) {
      outState.putParcelable(BundleKeys.STOP_DESTINATION, Parcels.wrap(stopDestination));
    }
    if (tripId != null) {
      outState.putString(BundleKeys.TRIP, tripId);
    }
    outState.putLong(BundleKeys.STOP_DATETIME, stopDateTime);
    outState.putInt(BundleKeys.STOP_METHOD, stopMethod);
    super.onSaveInstanceState(outState);
  }

  private void unWrapBundle(Bundle bundle) {
    if (bundle != null) {
      stopDateTime = bundle.getLong(BundleKeys.STOP_DATETIME);
      stopDestination = Parcels.unwrap(bundle.getParcelable(BundleKeys.STOP_DESTINATION));
      stopSource = Parcels.unwrap(bundle.getParcelable(BundleKeys.STOP_SOURCE));
      stopMethod = bundle.getInt(BundleKeys.STOP_METHOD);
      stopUnknown = Parcels.unwrap(bundle.getParcelable(BundleKeys.STOP));
      tripId = bundle.getString(BundleKeys.TRIP);
    }
  }

  private void filterStopsWithKnownStops() {
    if ((tripId == null || stopUnknown == null) && parentStops != null) {
      searchStops = new ArrayList<>(parentStops);
    }

    if (searchStops != null) {
      if (stopDestination != null) {
        searchStops.remove(stopDestination);
      }

      if (stopSource != null) {
        searchStops.remove(stopSource);
      }

      if (stopUnknown != null) {
        searchStops.remove(stopUnknown);
      }

      searchResultsViewAdapter.setStops(searchStops);
      searchResultsViewAdapter.notifyDataSetChanged();
    }
  }

  private Action1<RxMessage> handleRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.SEARCH_RESULT_STOP)) {
          Stop stop = (Stop) rxMessage.getMessage();
          if (tripId != null) {
            chooChooLoader.loadPossibleTrip(tripId, stopUnknown.stop_id, stop.stop_id);
          } else {
            if (stopDestination == null) {
              stopDestination = stop;
            } else {
              stopSource = stop;
            }
            chooChooLoader.loadPossibleTrips(stopSource.stop_id, stopDestination.stop_id, new LocalDateTime(stopDateTime));
          }
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.SEARCH_INPUT_STRING)) {
          String filterString = ((RxMessageString) rxMessage).getMessage();
          filterResultsBy(filterString);
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_POSSIBLE_TRIP)) {
          PossibleTrip possibleTrip = ((RxMessagePossibleTrip) rxMessage).getMessage();
          if (possibleTrip != null) {
            chooChooFragmentManager.loadTripDetailsFragments(possibleTrip);
          } else {
            chooChooLoader.loadPossibleTrips(stopSource.stop_id, stopDestination.stop_id, new LocalDateTime(stopDateTime));
          }
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_POSSIBLE_TRIPS)) {
          ArrayList<PossibleTrip> possibleTrips = ((RxMessagePossibleTrips) rxMessage).getMessage();
          chooChooFragmentManager.loadTripFilterFragment(possibleTrips, stopMethod, new LocalDateTime(stopDateTime), stopSource, stopDestination);
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_STOPS_ON_TRIP)) {
          searchStops = ((RxMessageStops) rxMessage).getMessage();
          filterStopsWithKnownStops();
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_STOPS)) {
          parentStops = ((RxMessageStops) rxMessage).getMessage();
          filterStopsWithKnownStops();
        }
      }
    };
  }

  public void filterResultsBy(String searchQuery) {
    ArrayList<Stop> filteredStops = filterByFuzzySearch(searchStops, searchQuery);

    if (filteredStops.size() > 0) {
      searchResultsEmptyState.setVisibility(View.GONE);
      searchResultsRecyclerView.setVisibility(View.VISIBLE);
      searchResultsViewAdapter.setStops(filteredStops);
      searchResultsViewAdapter.notifyDataSetChanged();
    } else {
      searchResultsRecyclerView.setVisibility(View.GONE);
      searchResultsEmptyState.setVisibility(View.VISIBLE);
    }

    searchResultsRecyclerView.swapAdapter(searchResultsViewAdapter, false);
  }
}
