package com.eleith.calchoochoo.fragments;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eleith.calchoochoo.ChooChooActivity;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.adapters.SearchResultsViewAdapter;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.DeviceLocation;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessagePairStopReason;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageString;

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
  private RecyclerView recyclerView;
  private Subscription subscription;
  private int searchReason;

  @BindView(R.id.search_results_empty_state)
  TextView searchResultsEmptyState;
  @BindView(R.id.search_results_recyclerview)
  RecyclerView searchResultsRecyclerView;

  @Inject RxBus rxBus;
  @Inject SearchResultsViewAdapter searchResultsViewAdapter;
  @Inject
  DeviceLocation deviceLocation;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((ChooChooActivity) getActivity()).getComponent().inject(this);
    unPackBundle(savedInstanceState != null ? savedInstanceState : getArguments());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    unPackBundle(savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_search_results, container, false);
    recyclerView = (RecyclerView) view.findViewById(R.id.search_results_recyclerview);
    recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

    if (recyclerView != null) {
      searchResultsViewAdapter.setStops(stops);
      deviceLocation.requestLocation(new DeviceLocation.LocationGetListener() {
        @Override
        public void onLocationGet(Location location) {
          searchResultsViewAdapter.setLocation(location);
        }
      });
      subscription = rxBus.observeEvents(RxMessage.class).subscribe(handleScheduleExplorerRxMessages());
      recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
      recyclerView.setAdapter(searchResultsViewAdapter);
    }

    ButterKnife.bind(this, view);
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    subscription.unsubscribe();
  }

  private void unPackBundle(Bundle bundle) {
    if (bundle != null) {
      stops = Parcels.unwrap(bundle.getParcelable(BundleKeys.STOPS));
      searchReason = bundle.getInt(BundleKeys.SEARCH_REASON);
    }
  }

  private Action1<RxMessage> handleScheduleExplorerRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.SEARCH_RESULT_STOP)) {
          Stop stop = (Stop) rxMessage.getMessage();
          Pair<Stop, Integer> pair = new Pair<>(stop, searchReason);
          rxBus.send(new RxMessagePairStopReason(RxMessageKeys.SEARCH_RESULT_PAIR, pair));
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

    recyclerView.swapAdapter(searchResultsViewAdapter, false);
  }
}
