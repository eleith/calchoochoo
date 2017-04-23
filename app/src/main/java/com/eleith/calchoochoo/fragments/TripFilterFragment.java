package com.eleith.calchoochoo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleith.calchoochoo.ChooChooRouterManager;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.TripFilterActivity;
import com.eleith.calchoochoo.data.ChooChooLoader;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.DataStringUtils;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessagePossibleTrips;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStop;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStopMethodAndDateTime;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStopsAndDetails;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;


public class TripFilterFragment extends Fragment {
  private LocalDateTime stopDateTime = new LocalDateTime();
  private int stopMethod = RxMessageStopsAndDetails.DETAIL_DEPARTING;
  private Subscription subscription;
  private ArrayList<PossibleTrip> possibleTrips;
  private String sourceStopId;
  private String destinationStopId;

  @BindView(R.id.trip_filter_destination)
  TextView destinationEdit;
  @BindView(R.id.trip_filter_source)
  TextView sourceEdit;
  @BindView(R.id.trip_filter_datetime)
  TextView timeEdit;
  @BindView(R.id.trip_filter_method_arriving)
  TextView methodArrivingText;
  @BindView(R.id.trip_filter_method_departing)
  TextView methodDepartingText;

  @Inject
  RxBus rxBus;
  @Inject
  ChooChooRouterManager chooChooRouterManager;
  @Inject
  ChooChooLoader chooChooLoader;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ((TripFilterActivity) getActivity()).getComponent().inject(this);
    unWrapBundle(savedInstanceState == null ? getArguments() : savedInstanceState);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    unWrapBundle(savedInstanceState == null ? getArguments() : savedInstanceState);
  }

  @Override
  public void onStart() {
    super.onStart();
    if (subscription == null || subscription.isUnsubscribed()) {
      subscription = rxBus.observeEvents(RxMessage.class).subscribe(handleRxMessages());
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    subscription.unsubscribe();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_trip_filter, container, false);
    ButterKnife.bind(this, view);
    updateStops();
    return view;
  }

  private void updateStops() {
    if (sourceStopId != null) {
      chooChooLoader.loadStopByParentId(sourceStopId);
    }

    if (destinationStopId != null) {
      chooChooLoader.loadStopByParentId(destinationStopId);
    }

    sourceEdit.setVisibility(View.VISIBLE);
    destinationEdit.setVisibility(View.VISIBLE);

    updateTimeEdit();
  }

  private void updateTimeEdit() {
    if (stopMethod == RxMessageStopsAndDetails.DETAIL_ARRIVING) {
      methodDepartingText.setVisibility(View.GONE);
      methodArrivingText.setVisibility(View.VISIBLE);
    } else {
      methodArrivingText.setVisibility(View.GONE);
      methodDepartingText.setVisibility(View.VISIBLE);
    }
    timeEdit.setText(DateTimeFormat.forPattern("M/d, E @ h:mma").print(stopDateTime));
  }

  @OnClick(R.id.trip_filter_refresh_date)
  void refreshDateClick() {
    stopDateTime = new LocalDateTime();
    Pair<Integer, LocalDateTime> pair = new Pair<>(stopMethod, stopDateTime);
    rxBus.send(new RxMessageStopMethodAndDateTime(RxMessageKeys.DATE_TIME_SELECTED, pair));
  }

  @OnClick(R.id.trip_filter_destination)
  void destinationClick() {
    ArrayList<String> filteredOutStopIds = new ArrayList<>();
    if (sourceStopId != null) {
      filteredOutStopIds.add(sourceStopId);
    }
    subscription.unsubscribe();
    chooChooRouterManager.loadStopSearchActivity(getActivity(), 1, filteredOutStopIds);
  }

  @OnClick(R.id.trip_filter_source)
  void sourceClick() {
    ArrayList<String> filteredOutStopIds = new ArrayList<>();
    if (destinationStopId != null) {
      filteredOutStopIds.add(destinationStopId);
    }
    subscription.unsubscribe();
    chooChooRouterManager.loadStopSearchActivity(getActivity(), 2, filteredOutStopIds);
  }

  @OnClick(R.id.trip_filter_datetime)
  void timeClick() {
    Bundle bundle = new Bundle();
    TripFilterTimeAndMethodDialogFragment dialog = new TripFilterTimeAndMethodDialogFragment();
    bundle.putInt(BundleKeys.STOP_METHOD, stopMethod);
    bundle.putLong(BundleKeys.STOP_DATETIME, stopDateTime.toDate().getTime());
    dialog.setArguments(bundle);
    dialog.show(getFragmentManager(), "dialog");
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(BundleKeys.POSSIBLE_TRIPS, Parcels.wrap(possibleTrips));
    outState.putLong(BundleKeys.STOP_DATETIME, stopDateTime.toDate().getTime());
    outState.putInt(BundleKeys.STOP_METHOD, stopMethod);
    super.onSaveInstanceState(outState);
  }

  private void unWrapBundle(Bundle bundle) {
    if (bundle != null) {
      possibleTrips = Parcels.unwrap(bundle.getParcelable(BundleKeys.POSSIBLE_TRIPS));
      stopDateTime = new LocalDateTime(bundle.getLong(BundleKeys.STOP_DATETIME));
      stopMethod = bundle.getInt(BundleKeys.STOP_METHOD);
      sourceStopId = bundle.getString(BundleKeys.STOP_SOURCE);
      destinationStopId = bundle.getString(BundleKeys.STOP_DESTINATION);
    }
  }

  private Action1<RxMessage> handleRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.SWITCH_SOURCE_DESTINATION_SELECTED)) {
          String tempId = destinationStopId;
          destinationStopId = sourceStopId;
          sourceStopId = tempId;
          chooChooLoader.loadPossibleTrips(sourceStopId, destinationStopId, stopDateTime);
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.DATE_TIME_SELECTED)) {
          Pair<Integer, LocalDateTime> pair = ((RxMessageStopMethodAndDateTime) rxMessage).getMessage();
          stopMethod = pair.first;
          stopDateTime = pair.second;
          updateTimeEdit();
          chooChooLoader.loadPossibleTrips(sourceStopId, destinationStopId, stopDateTime);
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_STOP)) {
          Stop stop = ((RxMessageStop) rxMessage).getMessage();

          if (destinationStopId != null && destinationStopId.equals(stop.stop_id)) {
            destinationEdit.setText(DataStringUtils.removeCaltrain(stop.stop_name));
          }

          if (sourceStopId != null && sourceStopId.equals(stop.stop_id)) {
            sourceEdit.setText(DataStringUtils.removeCaltrain(stop.stop_name));
          }
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.LOADED_POSSIBLE_TRIPS)) {
          possibleTrips = ((RxMessagePossibleTrips) rxMessage).getMessage();
          updateStops();
        }
      }
    };
  }
}
