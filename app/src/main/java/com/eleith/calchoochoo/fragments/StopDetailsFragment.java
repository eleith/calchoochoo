package com.eleith.calchoochoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eleith.calchoochoo.ChooChooActivity;
import com.eleith.calchoochoo.ChooChooFragmentManager;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.adapters.StopTrainsAdapter;
import com.eleith.calchoochoo.data.ChooChooLoader;
import com.eleith.calchoochoo.data.PossibleTrain;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.Trips;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageNextTrains;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageTrips;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

public class StopDetailsFragment extends Fragment {
  private Stop stop;
  private Subscription subscription;
  private ArrayList<PossibleTrain> possibleTrains = new ArrayList<>();
  private ArrayList<Trips> trips = new ArrayList<>();

  @Inject
  RxBus rxBus;
  @Inject
  ChooChooFragmentManager chooChooFragmentManager;
  @Inject
  ChooChooLoader chooChooLoader;
  @Inject
  StopTrainsAdapter stopTrainsAdapter;

  @BindView(R.id.stop_details_recyclerview)
  RecyclerView stopDetailsRecyclerView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((ChooChooActivity) getActivity()).getComponent().inject(this);
    unPackBundle(savedInstanceState != null ? savedInstanceState : getArguments());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    unPackBundle(savedInstanceState);

    View view = inflater.inflate(R.layout.fragment_stop_details, container, false);
    ButterKnife.bind(this, view);

    stopDetailsRecyclerView.setNestedScrollingEnabled(false);
    stopDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    setAdapterData();
    stopDetailsRecyclerView.setAdapter(stopTrainsAdapter);

    subscription = rxBus.observeEvents(RxMessage.class).subscribe(handleRxMessages());
    chooChooLoader.loadPossibleTrains(stop.stop_id, new LocalDateTime());
    chooChooLoader.loadRoutes();
    chooChooLoader.loadTrips();

    return view;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(BundleKeys.STOP, Parcels.wrap(stop));
    super.onSaveInstanceState(outState);
  }

  private void setAdapterData() {
    if (stop != null && possibleTrains.size() > 0 && trips.size() > 0) {
      stopTrainsAdapter.setStop(stop);
      stopTrainsAdapter.setPossibleTrains(possibleTrains);
      stopTrainsAdapter.setTrips(trips);

      Integer positionToScrollTo = 0;
      Integer northSelected = 0;
      Integer southSelected = 0;
      LocalTime now = new LocalTime();
      for (PossibleTrain possibleTrain : possibleTrains) {
        if (possibleTrain.getDepartureTime().isBefore(now)) {
          positionToScrollTo++;
          if (possibleTrain.getTripDirectionId() == 1) {
            northSelected = positionToScrollTo;
          } else {
            southSelected = positionToScrollTo;
          }
        }
      }

      stopTrainsAdapter.setSouthSelected(northSelected);
      stopTrainsAdapter.setNorthSelected(southSelected);
      stopTrainsAdapter.notifyDataSetChanged();
      stopDetailsRecyclerView.scrollToPosition(positionToScrollTo);
    }
  }

  private void unPackBundle(Bundle bundle) {
    if (bundle != null) {
      stop = Parcels.unwrap(bundle.getParcelable(BundleKeys.STOP));
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    subscription.unsubscribe();
  }

  private Action1<RxMessage> handleRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_NEXT_TRAINS)) {
          possibleTrains = ((RxMessageNextTrains) rxMessage).getMessage();
          setAdapterData();
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_TRIPS)) {
          trips = ((RxMessageTrips) rxMessage).getMessage();
          setAdapterData();
        }
      }
    };
  }
}
