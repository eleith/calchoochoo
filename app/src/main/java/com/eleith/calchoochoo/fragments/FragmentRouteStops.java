package com.eleith.calchoochoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.RouteViewAdapter;
import com.eleith.calchoochoo.ScheduleExplorerActivity;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RxBus;

import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Subscription;

public class FragmentRouteStops extends Fragment {
  private Subscription subscription;
  private ArrayList<PossibleTrip> possibleTrips;

  @Inject RxBus rxBus;
  @Inject RouteViewAdapter routeViewAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((ScheduleExplorerActivity) getActivity()).getComponent().inject(this);
    unPackBundle(savedInstanceState != null ? savedInstanceState : getArguments());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    unPackBundle(savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_search_results, container, false);
    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.searchResults);

    if (recyclerView != null) {
      //subscription = rxBus.observeEvents(RxMessage.class).subscribe(handleScheduleExplorerRxMessages());
      routeViewAdapter.setPossibleTrips(possibleTrips);

      recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
      recyclerView.setAdapter(routeViewAdapter);
    }

    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    subscription.unsubscribe();
  }

  /*
  private Action1<RxMessage> handleScheduleExplorerRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.SEARCH_RESULT_STOP)) {
          Stop stop = (Stop) rxMessage.getMessage();
          Pair<Stop, Integer> pair = new Pair<>(stop, searchReason);
          rxBus.send(new RxMessagePairStopReason(RxMessageKeys.SEARCH_RESULT_PAIR, pair));
        }
      }
    };
  }
  */

  private void unPackBundle(Bundle bundle) {
    if (bundle != null) {
      possibleTrips = Parcels.unwrap(bundle.getParcelable(BundleKeys.ROUTE_STOPS));
    }
  }
}
