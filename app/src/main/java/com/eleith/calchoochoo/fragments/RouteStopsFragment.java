package com.eleith.calchoochoo.fragments;

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

import com.eleith.calchoochoo.ChooChooActivity;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.adapters.RouteViewAdapter;
import com.eleith.calchoochoo.data.ChooChooLoader;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStopsAndDetails;

import org.joda.time.LocalDateTime;
import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

public class RouteStopsFragment extends Fragment {
  private ArrayList<PossibleTrip> possibleTrips;
  private LocalDateTime stopDateTime = new LocalDateTime();
  private int stopMethod = RxMessageStopsAndDetails.DETAIL_ARRIVING;
  private Subscription subscription;
  private ChooChooActivity chooChooActivity;

  @Inject
  RxBus rxBus;
  @Inject
  RouteViewAdapter routeViewAdapter;
  @Inject
  ChooChooLoader chooChooLoader;

  @BindView(R.id.trips_possible_empty_state)
  TextView tripsPossibleEmptyState;
  @BindView(R.id.trips_possible_recyclerview)
  RecyclerView tripsPossibleRecyclerView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    chooChooActivity = (ChooChooActivity) getActivity();
    chooChooActivity.getComponent().inject(this);
    unPackBundle(savedInstanceState != null ? savedInstanceState : getArguments());
    setSharedElementReturnTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.image_transform));
    setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.image_transform));
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
      recyclerView.setNestedScrollingEnabled(false);

      routeViewAdapter.setPossibleTrips(possibleTrips);

      recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
      recyclerView.setAdapter(routeViewAdapter);
      chooChooActivity.fabEnable(R.drawable.ic_swap_vert_black_24dp);
    } else {
      tripsPossibleRecyclerView.setVisibility(View.GONE);
      tripsPossibleEmptyState.setVisibility(View.VISIBLE);
    }
    subscription = rxBus.observeEvents(RxMessage.class).subscribe(handleRxMessages());
    chooChooLoader.loadRoutes();
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    subscription.unsubscribe();
    chooChooActivity.fabDisable();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(BundleKeys.ROUTE_STOPS, Parcels.wrap(possibleTrips));
    outState.putLong(BundleKeys.STOP_DATETIME, stopDateTime.toDate().getTime());
    outState.putInt(BundleKeys.STOP_METHOD, stopMethod);
    super.onSaveInstanceState(outState);
  }

  private void unPackBundle(Bundle bundle) {
    if (bundle != null) {
      possibleTrips = Parcels.unwrap(bundle.getParcelable(BundleKeys.ROUTE_STOPS));
      stopDateTime = new LocalDateTime(bundle.getLong(BundleKeys.STOP_DATETIME));
      stopMethod = bundle.getInt(BundleKeys.STOP_METHOD);
    }
  }

  private Action1<RxMessage> handleRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.FAB_CLICKED)) {
          rxBus.send(new RxMessage(RxMessageKeys.SWITCH_SOURCE_DESTINATION_SELECTED));
        }
      }
    };
  }
}
