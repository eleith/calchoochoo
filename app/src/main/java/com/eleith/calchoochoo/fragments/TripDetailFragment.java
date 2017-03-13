package com.eleith.calchoochoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eleith.calchoochoo.ChooChooActivity;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.adapters.TripStopsAdapter;
import com.eleith.calchoochoo.data.ChooChooLoader;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.Routes;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.StopTimes;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageTripStops;
import com.eleith.calchoochoo.utils.StopUtils;

import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

public class TripDetailFragment extends Fragment {
  private Stop stopDestination;
  private Stop stopSource;
  private PossibleTrip possibleTrip;
  private ArrayList<Pair<Stop, StopTimes>> tripStops;
  private ArrayList<Stop> stops = null;
  private ArrayList<Routes> routes = null;
  private Subscription subscription;

  @Inject
  RxBus rxBus;
  @Inject
  TripStopsAdapter tripStopsAdapter;
  @Inject
  ChooChooLoader chooChooLoader;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((ChooChooActivity) getActivity()).getComponent().inject(this);
    unWrapBundle(savedInstanceState == null ? getArguments() : savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_trip_detail, container, false);
    ButterKnife.bind(this, view);

    unWrapBundle(savedInstanceState);
    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.trip_stop_times);
    recyclerView.setNestedScrollingEnabled(false);

    recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    recyclerView.setAdapter(tripStopsAdapter);

    subscription = rxBus.observeEvents(RxMessage.class).subscribe(handleRxMessages());
    chooChooLoader.loadTripStops(possibleTrip.getTripId(), possibleTrip.getFirstStopId(), possibleTrip.getLastStopId());
    return view;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(BundleKeys.POSSIBLE_TRIP, Parcels.wrap(possibleTrip));
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    subscription.unsubscribe();
  }

  private void unWrapBundle(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      stops = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.STOPS));
      routes = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.ROUTES));
      possibleTrip = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.POSSIBLE_TRIP));
      stopSource = StopUtils.getParentStopById(stops, possibleTrip.getFirstStopId());
      stopDestination = StopUtils.getParentStopById(stops, possibleTrip.getLastStopId());
    }
  }

  private Action1<RxMessage> handleRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_TRIP_DETAILS)) {
          tripStops = ((RxMessageTripStops) rxMessage).getMessage();
          tripStopsAdapter.setTripStops(tripStops);
          tripStopsAdapter.notifyDataSetChanged();
        }
      }
    };
  }
}
