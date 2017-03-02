package com.eleith.calchoochoo.fragments;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleith.calchoochoo.ChooChooWidgetConfigure;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.adapters.SearchResultsConfigureWidgetAdapter;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.DeviceLocation;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStop;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageString;

import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

import static com.eleith.calchoochoo.utils.StopUtils.filterByFuzzySearch;

public class SearchResultsConfigureWidgetFragment extends Fragment {
  private ArrayList<Stop> stops;
  private Subscription subscription;

  @BindView(R.id.search_results_empty_state)
  TextView searchResultsEmptyState;
  @BindView(R.id.search_results_recyclerview)
  RecyclerView searchResultsRecyclerView;

  @Inject
  RxBus rxBus;
  @Inject
  SearchResultsConfigureWidgetAdapter searchResultsConfigureWidgetAdapter;
  @Inject
  DeviceLocation deviceLocation;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Activity activity = getActivity();
    super.onCreate(savedInstanceState);
    if (activity instanceof ChooChooWidgetConfigure) {
      ((ChooChooWidgetConfigure) activity).getComponent().inject(this);
    }
    unPackBundle(savedInstanceState != null ? savedInstanceState : getArguments());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    unPackBundle(savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_search_results, container, false);
    ButterKnife.bind(this, view);

    searchResultsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

    searchResultsConfigureWidgetAdapter.setStops(stops);
    deviceLocation.requestLocation(new DeviceLocation.LocationGetListener() {
      @Override
      public void onLocationGet(Location location) {
        searchResultsConfigureWidgetAdapter.setLocation(location);
      }
    });
    subscription = rxBus.observeEvents(RxMessage.class).subscribe(handleScheduleExplorerRxMessages());
    searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    searchResultsRecyclerView.setAdapter(searchResultsConfigureWidgetAdapter);

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
    super.onSaveInstanceState(outState);
  }

  private void unPackBundle(Bundle bundle) {
    if (bundle != null) {
      stops = Parcels.unwrap(bundle.getParcelable(BundleKeys.STOPS));
    }
  }

  private Action1<RxMessage> handleScheduleExplorerRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.SEARCH_RESULT_STOP)) {
          Stop stop = (Stop) rxMessage.getMessage();
          rxBus.send(new RxMessageStop(RxMessageKeys.STOP_SELECTED, stop));
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
      searchResultsConfigureWidgetAdapter.setStops(filteredStops);
      searchResultsConfigureWidgetAdapter.notifyDataSetChanged();
    } else {
      searchResultsRecyclerView.setVisibility(View.GONE);
      searchResultsEmptyState.setVisibility(View.VISIBLE);
    }

    searchResultsRecyclerView.swapAdapter(searchResultsConfigureWidgetAdapter, false);
  }
}
