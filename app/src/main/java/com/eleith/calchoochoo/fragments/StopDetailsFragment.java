package com.eleith.calchoochoo.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eleith.calchoochoo.ChooChooRouterManager;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.StopActivity;
import com.eleith.calchoochoo.adapters.StopTrainsAdapter;
import com.eleith.calchoochoo.data.ChooChooLoader;
import com.eleith.calchoochoo.data.PossibleTrain;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.TripUtils;

import org.joda.time.LocalTime;
import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StopDetailsFragment extends Fragment {
  private Stop stop;
  private ArrayList<PossibleTrain> possibleTrains = new ArrayList<>();
  private Handler refreshHandler;

  @Inject
  RxBus rxBus;
  @Inject
  ChooChooRouterManager chooChooRouterManager;
  @Inject
  ChooChooLoader chooChooLoader;

  @Inject
  @Named("north")
  StopTrainsAdapter stopTrainsAdapterNorth;
  @Inject
  @Named("south")
  StopTrainsAdapter stopTrainsAdapterSouth;

  @BindView(R.id.stop_details_recyclerview_north)
  RecyclerView stopDetailsRecyclerViewNorth;

  @BindView(R.id.stop_details_recyclerview_south)
  RecyclerView stopDetailsRecyclerViewSouth;

  @BindView(R.id.stop_details_nomore_north)
  CardView stopsNoMoreNorth;

  @BindView(R.id.stop_details_nomore_south)
  CardView stopsNoMoreSouth;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((StopActivity) getActivity()).getComponent().inject(this);
    unPackBundle(savedInstanceState != null ? savedInstanceState : getArguments());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    unPackBundle(savedInstanceState);

    View view = inflater.inflate(R.layout.fragment_stop_details, container, false);
    ButterKnife.bind(this, view);

    stopDetailsRecyclerViewNorth.setLayoutManager(new LinearLayoutManager(view.getContext()));
    stopDetailsRecyclerViewSouth.setLayoutManager(new LinearLayoutManager(view.getContext()));

    return view;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(BundleKeys.STOP, Parcels.wrap(stop));
    outState.putParcelable(BundleKeys.POSSIBLE_TRAINS, Parcels.wrap(possibleTrains));
    super.onSaveInstanceState(outState);
  }

  private void setAdapterData() {

    if (stop != null && possibleTrains.size() > 0) {
      ArrayList<PossibleTrain> northTrains = new ArrayList<>();
      ArrayList<PossibleTrain> southTrains = new ArrayList<>();
      LocalTime now = new LocalTime();

      for (PossibleTrain possibleTrain : possibleTrains) {
        if (possibleTrain.getTripDirectionId() == TripUtils.DIRECTION_NORTH) {
          if (possibleTrain.getDepartureTime().isAfter(now)) {
            northTrains.add(possibleTrain);
          }
        } else {
          if (possibleTrain.getDepartureTime().isAfter(now)) {
            southTrains.add(possibleTrain);
          }
        }
      }

      if (southTrains.size() > 0) {
        stopsNoMoreSouth.setVisibility(View.GONE);
        stopDetailsRecyclerViewSouth.setVisibility(View.VISIBLE);
        stopTrainsAdapterSouth.setPossibleTrains(southTrains);
        stopTrainsAdapterSouth.notifyDataSetChanged();
        stopDetailsRecyclerViewSouth.setAdapter(stopTrainsAdapterSouth);
      } else {
        stopsNoMoreSouth.setVisibility(View.VISIBLE);
        stopDetailsRecyclerViewSouth.setVisibility(View.GONE);
      }

      if (northTrains.size() > 0) {
        stopsNoMoreNorth.setVisibility(View.GONE);
        stopDetailsRecyclerViewNorth.setVisibility(View.VISIBLE);
        stopTrainsAdapterNorth.setPossibleTrains(northTrains);
        stopTrainsAdapterNorth.notifyDataSetChanged();
        stopDetailsRecyclerViewNorth.setAdapter(stopTrainsAdapterNorth);
      } else {
        stopDetailsRecyclerViewNorth.setVisibility(View.GONE);
        stopsNoMoreNorth.setVisibility(View.VISIBLE);
      }

    }
  }

  private void unPackBundle(Bundle bundle) {
    if (bundle != null) {
      stop = Parcels.unwrap(bundle.getParcelable(BundleKeys.STOP));
      possibleTrains = Parcels.unwrap(bundle.getParcelable(BundleKeys.POSSIBLE_TRAINS));
    }
  }

  @Override
  public void onStart() {
    refreshHandler = new Handler();
    refreshHandler.postDelayed(new Runnable() {
      @Override
      public void run() {
        setAdapterData();
        refreshHandler.postDelayed(this, 60 * 1000);
      }
    }, 60 * 1000);
    setAdapterData();
    super.onStart();
  }

  @Override
  public void onStop() {
    refreshHandler.removeCallbacksAndMessages(null);
    super.onStop();
  }
}
