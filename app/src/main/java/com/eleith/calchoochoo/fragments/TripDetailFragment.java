package com.eleith.calchoochoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.TripActivity;
import com.eleith.calchoochoo.adapters.TripStopsAdapter;
import com.eleith.calchoochoo.data.ChooChooLoader;
import com.eleith.calchoochoo.data.StopTimes;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RxBus;

import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class TripDetailFragment extends Fragment {
  private ArrayList<StopTimes> tripStops;

  @Inject
  RxBus rxBus;
  @Inject
  TripStopsAdapter tripStopsAdapter;
  @Inject
  ChooChooLoader chooChooLoader;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((TripActivity) getActivity()).getComponent().inject(this);
    unWrapBundle(savedInstanceState == null ? getArguments() : savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_trip_detail, container, false);
    ButterKnife.bind(this, view);

    unWrapBundle(savedInstanceState);
    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.trip_stop_times);

    recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    recyclerView.setAdapter(tripStopsAdapter);
    return view;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(BundleKeys.TRIP_STOP_STOPTIMES, Parcels.wrap(tripStops));
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

  private void unWrapBundle(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      tripStops = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.TRIP_STOP_STOPTIMES));
      tripStopsAdapter.setTripStops(tripStops);
      tripStopsAdapter.notifyDataSetChanged();
    }
  }
}
