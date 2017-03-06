package com.eleith.calchoochoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eleith.calchoochoo.ChooChooActivity;
import com.eleith.calchoochoo.ChooChooFragmentManager;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.data.PossibleTrain;
import com.eleith.calchoochoo.data.Queries;
import com.eleith.calchoochoo.data.Routes;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.Trips;
import com.eleith.calchoochoo.utils.BundleKeys;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StopDetailsFragment extends Fragment {
  private Stop stop;

  @BindView(R.id.stop_details_trains)
  LinearLayout stopDetailsTrains;

  @Inject
  ChooChooFragmentManager chooChooFragmentManager;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((ChooChooActivity) getActivity()).getComponent().inject(this);
    unPackBundle(savedInstanceState != null ? savedInstanceState : getArguments());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    unPackBundle(savedInstanceState);

    View view = inflater.inflate(R.layout.fragment_stop_cards, container, false);
    ButterKnife.bind(this, view);
    addRecentTrains();
    return view;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(BundleKeys.STOP, Parcels.wrap(stop));
    super.onSaveInstanceState(outState);
  }

  private void unPackBundle(Bundle bundle) {
    if (bundle != null) {
      stop = Parcels.unwrap(bundle.getParcelable(BundleKeys.STOP));
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  private void addRecentTrains() {
    ArrayList<PossibleTrain> possibleTrains = Queries.findNextTrain(stop, new LocalDateTime(2017, 3, 7, 10, 10));

    if (possibleTrains.size() > 0) {
      for (int i = 0; i < possibleTrains.size(); i++) {
        PossibleTrain possibleTrain = possibleTrains.get(i);
        final Trips trip = Queries.getTripById(possibleTrain.getTripId());
        Routes route = Queries.getRouteById(possibleTrain.getRouteId());
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("h:mma");

        View recentTrains = LayoutInflater.from(getContext()).inflate(R.layout.fragment_stop_card_widget_trainitem, stopDetailsTrains, false);
        TextView recentTrainNumber = (TextView) recentTrains.findViewById(R.id.stop_card_widget_trainitem_number);
        TextView recentTrainDirection = (TextView) recentTrains.findViewById(R.id.stop_card_widget_direction);
        ImageView recentTrainImage = (ImageView) recentTrains.findViewById(R.id.stop_card_widget_trainitem_image);
        TextView recentTrainTime = (TextView) recentTrains.findViewById(R.id.stop_card_widget_trainitem_time);

        if (trip != null) {
          recentTrainNumber.setText(trip.trip_id);
          if (trip.direction_id == 1) {
            recentTrainDirection.setText(getContext().getString(R.string.south_bound));
          } else {
            recentTrainDirection.setText(getContext().getString(R.string.north_bound));
          }
        }

        if (route != null && route.route_long_name.contains("Bullet")) {
          recentTrainImage.setImageDrawable(getContext().getDrawable(R.drawable.ic_train_bullet));
        } else {
          recentTrainImage.setImageDrawable(getContext().getDrawable(R.drawable.ic_train_local));
        }

        recentTrains.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            chooChooFragmentManager.loadSearchForSpotFragment(stop, trip);
          }
        });
        recentTrainTime.setText(dateTimeFormatter.print(possibleTrain.getDepartureTime()));
        stopDetailsTrains.addView(recentTrains);
      }
    } else {
      View noMoreTrains = LayoutInflater.from(getContext()).inflate(R.layout.fragment_stop_card_widget_train_nomore, stopDetailsTrains, false);
      stopDetailsTrains.addView(noMoreTrains);
    }
  }
}
