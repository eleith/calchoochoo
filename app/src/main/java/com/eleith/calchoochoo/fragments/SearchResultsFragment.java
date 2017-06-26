package com.eleith.calchoochoo.fragments;

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

import com.eleith.calchoochoo.ChooChooRouterManager;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.StopSearchActivity;
import com.eleith.calchoochoo.adapters.SearchResultsViewAdapter;
import com.eleith.calchoochoo.data.ChooChooLoader;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.KeyboardUtils;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
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
  private ArrayList<Stop> parentStops;
  private ArrayList<String> filteredStopIds;
  private Subscription subscription;
  private Integer reason;
  private Location location;

  @BindView(R.id.search_results_empty_state)
  TextView searchResultsEmptyState;
  @BindView(R.id.search_results_recyclerview)
  RecyclerView searchResultsRecyclerView;

  @Inject
  RxBus rxBus;
  @Inject
  SearchResultsViewAdapter searchResultsViewAdapter;
  @Inject
  ChooChooRouterManager chooChooRouterManager;
  @Inject
  ChooChooLoader chooChooLoader;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((StopSearchActivity) getActivity()).getComponent().inject(this);

    unWrapBundle(savedInstanceState != null ? savedInstanceState : getArguments());
    setEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.slide_up));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_search_results, container, false);
    ButterKnife.bind(this, view);
    unWrapBundle(savedInstanceState);

    searchResultsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

    subscription = rxBus.observeEvents(RxMessage.class).subscribe(new HandleRxMessages());
    searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    searchResultsRecyclerView.setAdapter(searchResultsViewAdapter);

    filterStopsWithKnownStops();
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    subscription.unsubscribe();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putStringArrayList(BundleKeys.STOP_IDS, filteredStopIds);
    outState.putParcelable(BundleKeys.STOPS, Parcels.wrap(parentStops));
    outState.putParcelable(BundleKeys.LOCATION, location);
    outState.putInt(BundleKeys.SEARCH_REASON, reason);
    super.onSaveInstanceState(outState);
  }

  private void unWrapBundle(Bundle bundle) {
    if (bundle != null) {
      filteredStopIds = bundle.getStringArrayList(BundleKeys.STOP_IDS);
      parentStops = Parcels.unwrap(bundle.getParcelable(BundleKeys.STOPS));
      reason = bundle.getInt(BundleKeys.SEARCH_REASON);
      location = bundle.getParcelable(BundleKeys.LOCATION);
    }
  }

  private void filterStopsWithKnownStops() {
    searchResultsViewAdapter.setLocation(location);
    searchResultsViewAdapter.setStops(parentStops, filteredStopIds);
    searchResultsViewAdapter.notifyDataSetChanged();
  }

  private class HandleRxMessages implements Action1<RxMessage> {
    @Override
    public void call(RxMessage rxMessage) {
      if (rxMessage.isMessageValidFor(RxMessageKeys.SEARCH_RESULT_STOP)) {
        Stop stop = (Stop) rxMessage.getMessage();
        KeyboardUtils.hide(getActivity());
        chooChooRouterManager.loadStopSearchReturnActivity(getActivity(), reason, stop.stop_id);
      } else if (rxMessage.isMessageValidFor(RxMessageKeys.SEARCH_INPUT_STRING)) {
        String filterString = ((RxMessageString) rxMessage).getMessage();
        filterResultsBy(filterString);
      }
    }
  }

  public void filterResultsBy(String searchQuery) {
    ArrayList<Stop> filteredStops = filterByFuzzySearch(parentStops, searchQuery);

    if (filteredStops.size() > 0) {
      searchResultsEmptyState.setVisibility(View.GONE);
      searchResultsRecyclerView.setVisibility(View.VISIBLE);
      searchResultsViewAdapter.setStops(filteredStops, filteredStopIds);
      searchResultsViewAdapter.notifyDataSetChanged();
    } else {
      searchResultsRecyclerView.setVisibility(View.GONE);
      searchResultsEmptyState.setVisibility(View.VISIBLE);
    }

    searchResultsRecyclerView.swapAdapter(searchResultsViewAdapter, false);
  }
}
