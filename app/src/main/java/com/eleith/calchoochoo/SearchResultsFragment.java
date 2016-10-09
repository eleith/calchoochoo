package com.eleith.calchoochoo;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eleith.calchoochoo.data.Stop;

import java.util.ArrayList;

public class SearchResultsFragment extends Fragment {
  private SearchResultsFragmentListener listener;
  private ArrayList<Stop> stops;
  private SearchResultsViewAdapter searchResultsViewAdapter;
  private RecyclerView recyclerView;
  private Location location;
  private String searchReason;

  public interface SearchResultsFragmentListener {
    void onSearchResultSelect(Stop stop, String searchReason);
  }

  public SearchResultsFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    unPackBundle(savedInstanceState != null ? savedInstanceState : getArguments());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    unPackBundle(savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_search_results, container, false);
    recyclerView = (RecyclerView) view.findViewById(R.id.searchResults);

    if (recyclerView != null) {
      searchResultsViewAdapter = new SearchResultsViewAdapter(stops, location);
      searchResultsViewAdapter.setLocation(location);
      searchResultsViewAdapter.setSearchResultsViewAdapterListener(new SearchResultsViewAdapter.SearchResultsViewAdapterListener() {
        @Override
        public void onSearchResultSelect(Stop stop) {
          listener.onSearchResultSelect(stop, searchReason);
        }
      });
      recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
      recyclerView.setAdapter(searchResultsViewAdapter);
    }

    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof SearchResultsFragmentListener) {
      listener = (SearchResultsFragmentListener) context;
    } else {
      throw new RuntimeException(context.toString() + " must implement SearchResultsFragmentListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    listener = null;
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
