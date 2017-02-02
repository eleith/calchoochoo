package com.eleith.calchoochoo.fragments;

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
import com.eleith.calchoochoo.adapters.TripStopsAdapter;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.Queries;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.StopTimes;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RxBus;

import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class TripDetailFragment extends Fragment {
  private Stop stopDestination;
  private Stop stopSource;
  private PossibleTrip possibleTrip;
  private ArrayList<Pair<Stop, StopTimes>> tripStops;

  @Inject
  RxBus rxBus;
  @Inject
  TripStopsAdapter tripStopsAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((ScheduleExplorerActivity) getActivity()).getComponent().inject(this);
    unWrapBundle(getArguments());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_trip_detail, container, false);
    ButterKnife.bind(this, view);

    unWrapBundle(savedInstanceState);
    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.trip_stop_times);

    if (recyclerView != null) {
      tripStopsAdapter.setTripStops(tripStops);

      recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
      recyclerView.setAdapter(tripStopsAdapter);
    }

    return view;
  }

  private void unWrapBundle(Bundle savedInstanceState) {
     if (savedInstanceState != null) {
      stopDestination = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.STOP_DESTINATION));
      stopSource = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.STOP_SOURCE));
      possibleTrip = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.POSSIBLE_TRIP));
      tripStops = Queries.findTripDetails(possibleTrip.getTripId(), possibleTrip.getFirstStopSequence(), possibleTrip.getLastStopSequence());
    }
  }
}