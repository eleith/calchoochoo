package com.eleith.calchoochoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.ScheduleExplorerActivity;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.Queries;
import com.eleith.calchoochoo.data.Routes;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.StopTimes;
import com.eleith.calchoochoo.utils.BundleKeys;

import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.TextView;

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

  @BindView(R.id.trip_summary_from)
  TextView tripSummaryFrom;

  @BindView(R.id.trip_summary_to)
  TextView tripSummaryTo;

  @BindView(R.id.trip_summary_price)
  TextView tripSummaryPrice;

  @BindView(R.id.trip_summary_total_time)
  TextView tripSummaryTotalTime;

  @BindView(R.id.trip_summary_train_bullet)
  ImageView tripSummaryTrainBullet;

  @BindView(R.id.trip_summary_train_local)
  ImageView tripSummaryTrainLocal;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((ScheduleExplorerActivity) getActivity()).getComponent().inject(this);
    unWrapBundle(getArguments());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_trip_summary, container, false);
    Routes route = Queries.getRouteById(possibleTrip.getRouteId());
    ButterKnife.bind(this, view);

    tripSummaryFrom.setText(stopSource.stop_name.replace(" Caltrain", ""));
    tripSummaryTo.setText(stopDestination.stop_name.replace(" Caltrain", ""));
    tripSummaryPrice.setText(String.format(Locale.getDefault(), "$%.2f", possibleTrip.getPrice()));
    tripSummaryTotalTime.setText(String.format(Locale.getDefault(), "%d min", Minutes.minutesBetween(possibleTrip.getArrivalTime(), possibleTrip.getDepartureTime()).getMinutes()));

    if(route != null && route.route_long_name.contains("Bullet")) {
      tripSummaryTrainLocal.setVisibility(View.GONE);
      tripSummaryTrainBullet.setVisibility(View.VISIBLE);
    } else {
      tripSummaryTrainBullet.setVisibility(View.GONE);
      tripSummaryTrainLocal.setVisibility(View.VISIBLE);
    }

    unWrapBundle(savedInstanceState);

    return view;
  }

  private void unWrapBundle(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      stopDestination = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.STOP_DESTINATION));
      stopSource = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.STOP_SOURCE));
      possibleTrip = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.POSSIBLE_TRIP));
    }
  }
}
