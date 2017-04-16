package com.eleith.calchoochoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StopDetailsFragment extends Fragment {
  private Stop stop;
  private ArrayList<PossibleTrain> possibleTrains = new ArrayList<>();

  @Inject
  RxBus rxBus;
  @Inject
  ChooChooRouterManager chooChooRouterManager;
  @Inject
  ChooChooLoader chooChooLoader;
  @Inject
  StopTrainsAdapter stopTrainsAdapter;

  @BindView(R.id.stop_details_recyclerview)
  RecyclerView stopDetailsRecyclerView;

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

    stopDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    setAdapterData();
    stopDetailsRecyclerView.setAdapter(stopTrainsAdapter);

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
      stopTrainsAdapter.setPossibleTrains(possibleTrains);

      Integer positionToScrollTo = 0;
      Integer selected = 0;
      LocalTime now = new LocalTime();
      for (PossibleTrain possibleTrain : possibleTrains) {
        if (possibleTrain.getDepartureTime().isBefore(now)) {
          positionToScrollTo++;
          selected = positionToScrollTo;
        }
      }

      stopTrainsAdapter.setSelected(selected);
      stopTrainsAdapter.notifyDataSetChanged();
      if (positionToScrollTo > 0) {
        stopDetailsRecyclerView.scrollToPosition(positionToScrollTo - 1);
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
  public void onDestroy() {
    super.onDestroy();
  }
}
