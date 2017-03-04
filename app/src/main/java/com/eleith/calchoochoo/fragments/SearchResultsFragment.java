package com.eleith.calchoochoo.fragments;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleith.calchoochoo.ChooChooActivity;
import com.eleith.calchoochoo.ChooChooFragmentManager;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.adapters.SearchResultsViewAdapter;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.Queries;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.Trips;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.DeviceLocation;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessagePairStopReason;
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
  private ArrayList<Stop> stops;
  private Stop otherStop;
  private Long localDateTime;
  private Trips trip;
  private Subscription subscription;
  private int searchReason;

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

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Activity activity = getActivity();
    super.onCreate(savedInstanceState);
    if (activity instanceof ChooChooActivity) {
      ((ChooChooActivity) activity).getComponent().inject(this);
    }
    stops = Queries.getAllParentStops();
    unWrapBundle(savedInstanceState != null ? savedInstanceState : getArguments());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    unWrapBundle(savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_search_results, container, false);
    ButterKnife.bind(this, view);

    searchResultsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

    searchResultsViewAdapter.setStops(stops);
    deviceLocation.requestLocation(new DeviceLocation.LocationGetListener() {
      @Override
      public void onLocationGet(Location location) {
        searchResultsViewAdapter.setLocation(location);
      }
    });
    subscription = rxBus.observeEvents(RxMessage.class).subscribe(handleScheduleExplorerRxMessages());
    searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    searchResultsRecyclerView.setAdapter(searchResultsViewAdapter);

    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    subscription.unsubscribe();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(BundleKeys.STOPS, Parcels.wrap(stops));
    outState.putInt(BundleKeys.SEARCH_REASON, searchReason);
    super.onSaveInstanceState(outState);
  }

  private void unWrapBundle(Bundle bundle) {
    if (bundle != null) {
      localDateTime = bundle.getLong(BundleKeys.STOP_DATETIME);
      otherStop = Parcels.unwrap(bundle.getParcelable(BundleKeys.STOP));
      trip = Parcels.unwrap(bundle.getParcelable(BundleKeys.TRIP));
      searchReason = bundle.getInt(BundleKeys.SEARCH_REASON);

      if (trip != null) {
        stops = Queries.findStopsOnTrip(trip.trip_id);
      }
    }
  }

  private Action1<RxMessage> handleScheduleExplorerRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.SEARCH_RESULT_STOP)) {
          Stop stop = (Stop) rxMessage.getMessage();
          Pair<Stop, Integer> pair = new Pair<>(stop, searchReason);
          if (trip != null) {
            if (searchReason == RxMessagePairStopReason.SEARCH_REASON_DESTINATION) {
              PossibleTrip possibleTrip = Queries.findPossibleTrip(otherStop, stop, trip.trip_id);
              chooChooFragmentManager.loadTripDetailsFragments(possibleTrip, stop, otherStop);
            } else {
              PossibleTrip possibleTrip = Queries.findPossibleTrip(stop, otherStop, trip.trip_id);
              chooChooFragmentManager.loadTripDetailsFragments(possibleTrip, otherStop, stop);
            }
          } else {
            //rxBus.send(new RxMessagePairStopReason(RxMessageKeys.SEARCH_RESULT_PAIR, pair));
            if (searchReason == RxMessagePairStopReason.SEARCH_REASON_DESTINATION) {
              chooChooFragmentManager.loadTripFilterFragment(searchReason, new LocalDateTime(localDateTime), stop, otherStop);
            } else {
              chooChooFragmentManager.loadTripFilterFragment(searchReason, new LocalDateTime(localDateTime), otherStop, stop);
            }
          }
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.SEARCH_INPUT_STRING)) {
          String filterString = ((RxMessageString) rxMessage).getMessage();
          filterResultsBy(filterString);
        }
      }
    };
  }

  public void filterResultsBy(String searchQuery) {
    ArrayList<Stop> filteredStops = filterByFuzzySearch(stops, searchQuery);

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
