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

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.ScheduleExplorerActivity;
import com.eleith.calchoochoo.adapters.SearchResultsViewAdapter;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessagePairStopReason;

import org.apache.commons.lang3.StringUtils;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

public class SearchResultsFragment extends Fragment {
  private ArrayList<Stop> stops;
  private RecyclerView recyclerView;
  private Location location;
  private int searchReason;
  private Subscription subscription;

  @BindView(R.id.search_results_empty_state)
  LinearLayout searchResultsEmptyState;
  @BindView(R.id.search_results_recyclerview)
  RecyclerView searchResultsRecyclerView;

  @Inject RxBus rxBus;
  @Inject SearchResultsViewAdapter searchResultsViewAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((ScheduleExplorerActivity) getActivity()).getComponent().inject(this);
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
      searchResultsViewAdapter.setLocation(location);
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

  private Action1<RxMessage> handleScheduleExplorerRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.SEARCH_RESULT_STOP)) {
          Stop stop = (Stop) rxMessage.getMessage();
          Pair<Stop, Integer> pair = new Pair<>(stop, searchReason);
          rxBus.send(new RxMessagePairStopReason(RxMessageKeys.SEARCH_RESULT_PAIR, pair));
        }
      }
    };
  }

  private void unPackBundle(Bundle bundle) {
    if (bundle != null) {
      stops = Parcels.unwrap(bundle.getParcelable(BundleKeys.STOPS));
      location = bundle.getParcelable(BundleKeys.LOCATION);
      searchReason = bundle.getInt(BundleKeys.SEARCH_REASON);
    }
  }

  public void filterResultsBy(String searchQuery, Location location) {
    ArrayList<Stop> filteredStops = filterByFuzzySearch(stops, searchQuery);
    searchResultsViewAdapter.setLocation(location);

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

  public ArrayList<Stop> filterByFuzzySearch(ArrayList<Stop> stops, String query) {
    ArrayList<Stop> filteredStops;
    if (query != null && !query.equals("")) {
      filteredStops =  new ArrayList<Stop>();
      final HashMap<String, Integer> stopFuzzyScores = new HashMap<String, Integer>();
      for (Stop stop : stops) {
        int fuzzyScore = StringUtils.getFuzzyDistance(stop.stop_name, query, Locale.getDefault());
        if (fuzzyScore >= query.length()) {
          stopFuzzyScores.put(stop.stop_id, fuzzyScore);
          filteredStops.add(stop);
        }
      }
      Collections.sort(filteredStops, new Comparator<Stop>() {
            @Override
            public int compare(Stop lhs, Stop rhs) {
              int rightFuzzyScore = stopFuzzyScores.get(rhs.stop_id);
              int leftFuzzyScore = stopFuzzyScores.get(lhs.stop_id);
              return Integer.compare(rightFuzzyScore, leftFuzzyScore);
            }
      });
    } else {
      filteredStops = stops;
      Collections.sort(filteredStops, Stop.nameComparator);
    }

    return filteredStops;
  }
}
