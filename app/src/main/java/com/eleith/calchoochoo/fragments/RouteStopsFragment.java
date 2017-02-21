package com.eleith.calchoochoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.adapters.RouteViewAdapter;
import com.eleith.calchoochoo.ScheduleExplorerActivity;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;

import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RouteStopsFragment extends Fragment {
  private ArrayList<PossibleTrip> possibleTrips;

  @Inject
  RxBus rxBus;
  @Inject
  RouteViewAdapter routeViewAdapter;

  @BindView(R.id.trips_possible_empty_state)
  LinearLayout tripsPossibleEmptyState;
  @BindView(R.id.trips_possible_recyclerview)
  RecyclerView tripsPossibleRecyclerView;

  @OnClick(R.id.trips_possible_switch)
  void switchRoutesClick() {
    rxBus.send(new RxMessage(RxMessageKeys.SWITCH_SOURCE_DESTINATION_SELECTED));
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((ScheduleExplorerActivity) getActivity()).getComponent().inject(this);
    unPackBundle(savedInstanceState != null ? savedInstanceState : getArguments());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    unPackBundle(savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_trips_possible, container, false);
    ButterKnife.bind(this, view);

    if (possibleTrips.size() > 0) {
      tripsPossibleEmptyState.setVisibility(View.GONE);
      tripsPossibleRecyclerView.setVisibility(View.VISIBLE);
      RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.trips_possible_recyclerview);
      recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

      routeViewAdapter.setPossibleTrips(possibleTrips);

      recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
      recyclerView.setAdapter(routeViewAdapter);
    } else {
      tripsPossibleRecyclerView.setVisibility(View.GONE);
      tripsPossibleEmptyState.setVisibility(View.VISIBLE);
    }

    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

  private void unPackBundle(Bundle bundle) {
    if (bundle != null) {
      possibleTrips = Parcels.unwrap(bundle.getParcelable(BundleKeys.ROUTE_STOPS));
    }
  }
}
