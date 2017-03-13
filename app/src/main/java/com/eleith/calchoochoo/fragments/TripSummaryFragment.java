package com.eleith.calchoochoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eleith.calchoochoo.ChooChooActivity;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.data.ChooChooLoader;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.Routes;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RouteUtils;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageRoutes;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStops;
import com.eleith.calchoochoo.utils.StopUtils;

import org.joda.time.Minutes;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

public class TripSummaryFragment extends Fragment {
  private PossibleTrip possibleTrip;
  private ArrayList<Routes> routes;
  private ArrayList<Stop> stops;
  private Subscription subscription;

  @Inject
  ChooChooLoader chooChooLoader;

  @Inject
  RxBus rxBus;

  @BindView(R.id.trip_summary_from)
  TextView tripSummaryFrom;

  @BindView(R.id.trip_summary_number)
  TextView tripSummaryNumber;

  @BindView(R.id.trip_summary_to)
  TextView tripSummaryTo;

  @BindView(R.id.trip_summary_price)
  TextView tripSummaryPrice;

  @BindView(R.id.trip_summary_total_time)
  TextView tripSummaryTotalTime;

  @BindView(R.id.trip_summary_train_image)
  ImageView tripSummaryImage;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((ChooChooActivity) getActivity()).getComponent().inject(this);
    unWrapBundle(savedInstanceState == null ? getArguments() : savedInstanceState);
    setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.image_transform));
    setSharedElementReturnTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.image_transform));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_trip_summary, container, false);
    unWrapBundle(savedInstanceState);
    ButterKnife.bind(this, view);

    subscription = rxBus.observeEvents(RxMessage.class).subscribe(handleRxMessages());
    chooChooLoader.loadParentStops();
    chooChooLoader.loadRoutes();

    updateSummaryBar();
    return view;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(BundleKeys.POSSIBLE_TRIP, Parcels.wrap(possibleTrip));
    super.onSaveInstanceState(outState);
  }

  private void unWrapBundle(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      possibleTrip = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.POSSIBLE_TRIP));
    }
  }

  private void updateSummaryBar() {
    if (routes != null && stops != null) {
      Stop stopDestination = StopUtils.getStopById(stops, possibleTrip.getLastStopId());
      Stop stopSource = StopUtils.getStopById(stops, possibleTrip.getFirstStopId());

      if (stopDestination != null && stopSource != null) {
        tripSummaryFrom.setText(stopSource.stop_name.replace(" Caltrain", ""));
        tripSummaryTo.setText(stopDestination.stop_name.replace(" Caltrain", ""));
      }

      tripSummaryPrice.setText(String.format(Locale.getDefault(), "$%.2f", possibleTrip.getPrice()));
      tripSummaryTotalTime.setText(String.format(Locale.getDefault(), "%d min", Minutes.minutesBetween(possibleTrip.getArrivalTime(), possibleTrip.getDepartureTime()).getMinutes()));
      tripSummaryNumber.setText(possibleTrip.getTripId());

      Routes route = RouteUtils.getRouteById(routes, possibleTrip.getRouteId());
      if (route != null && route.route_long_name.contains("Bullet")) {
        tripSummaryImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_train_bullet));
        tripSummaryImage.setContentDescription(getString(R.string.bullet_train));
      } else {
        tripSummaryImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_train_local));
        tripSummaryImage.setContentDescription(getString(R.string.local_train));
      }

      tripSummaryImage.setTransitionName(getString(R.string.transition_train_image) + possibleTrip.getTripId());
    }
  }

  private Action1<RxMessage> handleRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_STOPS)) {
          stops = ((RxMessageStops) rxMessage).getMessage();
          updateSummaryBar();
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_ROUTES)) {
          routes = ((RxMessageRoutes) rxMessage).getMessage();
          updateSummaryBar();
        }
      }
    };
  }
}
