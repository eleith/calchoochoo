package com.eleith.calchoochoo.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.ScheduleExplorerActivity;
import com.eleith.calchoochoo.SearchResultsViewAdapter;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxMessage;
import com.eleith.calchoochoo.utils.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxMessagePair;
import com.eleith.calchoochoo.utils.RxMessageString;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.functions.Action1;

public class SearchResultsFragment extends Fragment {
  private ArrayList<Stop> stops;
  private RecyclerView recyclerView;
  private Location location;
  private String searchReason;

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
    recyclerView = (RecyclerView) view.findViewById(R.id.searchResults);

    if (recyclerView != null) {
      searchResultsViewAdapter.setStops(stops);
      searchResultsViewAdapter.setLocation(location);

      rxBus.observeEvents(RxMessage.class).subscribe(handleScheduleExplorerRxMessages());

      recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
      recyclerView.setAdapter(searchResultsViewAdapter);
    }

    return view;
  }

  private Action1<RxMessage> handleScheduleExplorerRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        String type = rxMessage.getType();

        if (type.equals(RxMessageKeys.SEARCH_RESULT_STOP)) {
          Stop stop = (Stop) rxMessage.getMessage();
          Pair<Stop, String> pair = new Pair<Stop, String>(stop, searchReason);
          rxBus.send(new RxMessagePair<Stop, String>(RxMessageKeys.SEARCH_RESULT_PAIR, pair));
        }
      }
    };
  }

  private void unPackBundle(Bundle bundle) {
    if (bundle != null) {
      stops = bundle.getParcelableArrayList(BundleKeys.STOPS);
      location = bundle.getParcelable(BundleKeys.LOCATION);
      searchReason = bundle.getString(BundleKeys.SEARCH_REASON);
    }
  }

  public void filterResultsBy(String searchQuery, Location location) {
    searchResultsViewAdapter.setLocation(location);
    searchResultsViewAdapter.filterByFuzzySearch(stops, searchQuery);
    recyclerView.swapAdapter(searchResultsViewAdapter, false);
  }
}
