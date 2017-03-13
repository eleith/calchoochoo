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
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.Routes;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RouteUtils;
import com.eleith.calchoochoo.utils.StopUtils;

import org.joda.time.Minutes;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripSummaryFragment extends Fragment {
  private Stop stopDestination;
  private Stop stopSource;
  private PossibleTrip possibleTrip;
  private ArrayList<Stop> stops;
  private ArrayList<Routes> routes;

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

    tripSummaryFrom.setText(stopSource.stop_name.replace(" Caltrain", ""));
    tripSummaryTo.setText(stopDestination.stop_name.replace(" Caltrain", ""));
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
    return view;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(BundleKeys.POSSIBLE_TRIP, Parcels.wrap(possibleTrip));
    super.onSaveInstanceState(outState);
  }

  private void unWrapBundle(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      stops = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.STOPS));
      routes = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.ROUTES));
      possibleTrip = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.POSSIBLE_TRIP));
      stopDestination = StopUtils.getParentStopById(stops, possibleTrip.getLastStopId());
      stopSource = StopUtils.getParentStopById(stops, possibleTrip.getFirstStopId());
    }
  }
}
