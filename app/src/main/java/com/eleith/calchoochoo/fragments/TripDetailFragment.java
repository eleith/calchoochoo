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
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.StopTimes;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStops;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageTripStops;

import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

public class TripDetailFragment extends Fragment {
  private PossibleTrip possibleTrip;
  private ArrayList<Pair<Stop, StopTimes>> tripStops;
  private ArrayList<Stop> parentStops;
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
      possibleTrip = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.POSSIBLE_TRIP));
    }
  }

  private Action1<RxMessage> handleRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_TRIP_DETAILS)) {
          tripStops = ((RxMessageTripStops) rxMessage).getMessage();
          tripStopsAdapter.setTripStops(tripStops);
          if (tripStops != null && parentStops != null) {
            tripStopsAdapter.notifyDataSetChanged();
          }
        } else if(rxMessage.isMessageValidFor(RxMessageKeys.LOADED_STOPS)) {
          parentStops = ((RxMessageStops) rxMessage).getMessage();
          tripStopsAdapter.setStops(parentStops);
          if (tripStops != null && parentStops != null) {
            tripStopsAdapter.notifyDataSetChanged();
          }
        }
      }
    };
  }
}
