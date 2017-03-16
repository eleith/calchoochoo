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
import com.eleith.calchoochoo.data.ChooChooLoader;
import com.eleith.calchoochoo.data.PossibleTrain;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.Trips;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.PossibleTrainUtils;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageNextTrains;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageTrips;
import com.eleith.calchoochoo.utils.TripUtils;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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

  @BindView(R.id.stop_details_trains)
  LinearLayout stopDetailsTrains;

  @Inject
  RxBus rxBus;
  @Inject
  ChooChooFragmentManager chooChooFragmentManager;
  @Inject
  ChooChooLoader chooChooLoader;

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

  private void addRecentTrains() {
    if (possibleTrains != null && possibleTrains.size() > 0 && trips.size() > 0) {
      for (int i = 0; i < possibleTrains.size(); i++) {
        final PossibleTrain possibleTrain = possibleTrains.get(i);
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("h:mma");

        View recentTrains = LayoutInflater.from(getContext()).inflate(R.layout.fragment_stop_card_widget_trainitem, stopDetailsTrains, false);
        TextView recentTrainNumber = (TextView) recentTrains.findViewById(R.id.stop_card_widget_trainitem_number);
        TextView recentTrainDirection = (TextView) recentTrains.findViewById(R.id.stop_card_widget_direction);
        ImageView recentTrainImage = (ImageView) recentTrains.findViewById(R.id.stop_card_widget_trainitem_image);
        TextView recentTrainTime = (TextView) recentTrains.findViewById(R.id.stop_card_widget_trainitem_time);

        recentTrainNumber.setText(possibleTrain.getTripId());
        if (possibleTrain.getTripDirectionId() == 1) {
          recentTrainDirection.setText(getContext().getString(R.string.south_bound));
        } else {
          recentTrainDirection.setText(getContext().getString(R.string.north_bound));
        }

        if (possibleTrain.getRouteLongName().contains("Bullet")) {
          recentTrainImage.setImageDrawable(getContext().getDrawable(R.drawable.ic_train_bullet));
        } else {
          recentTrainImage.setImageDrawable(getContext().getDrawable(R.drawable.ic_train_local));
        }

        recentTrains.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            chooChooFragmentManager.loadSearchForSpotFragment(stop, TripUtils.getTripById(trips, possibleTrain.getTripId()));
          }
        });
        recentTrainTime.setText(dateTimeFormatter.print(possibleTrain.getDepartureTime()));
        stopDetailsTrains.addView(recentTrains);
      }
    }
  }

  private Action1<RxMessage> handleRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_NEXT_TRAINS)) {
          possibleTrains = ((RxMessageNextTrains) rxMessage).getMessage();
          possibleTrains = PossibleTrainUtils.filterByDateTime(possibleTrains, new LocalDateTime());
          addRecentTrains();
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_TRIPS)) {
          trips = ((RxMessageTrips) rxMessage).getMessage();
          addRecentTrains();
        }
      }
    };
  }
}
