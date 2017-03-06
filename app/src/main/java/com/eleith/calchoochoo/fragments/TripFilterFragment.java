package com.eleith.calchoochoo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleith.calchoochoo.ChooChooActivity;
import com.eleith.calchoochoo.ChooChooFragmentManager;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessagePossibleTrip;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStopMethodAndDateTime;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStopsAndDetails;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;


public class TripFilterFragment extends Fragment {
  private Stop stopDestination;
  private Stop stopSource;
  private LocalDateTime stopDateTime = new LocalDateTime();
  private int stopMethod = RxMessageStopsAndDetails.DETAIL_ARRIVING;
  private Subscription subscription;

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
  ChooChooFragmentManager chooChooFragmentManager;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ((ChooChooActivity) getActivity()).getComponent().inject(this);

    unWrapBundle(savedInstanceState == null ? getArguments() : savedInstanceState);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    unWrapBundle(savedInstanceState == null ? getArguments() : savedInstanceState);
    subscription = rxBus.observeEvents(RxMessage.class).subscribe(handleRxMessages());
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    subscription.unsubscribe();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_trip_filter, container, false);
    ButterKnife.bind(this, view);
    updateStops();
    return view;
  }

  private void updateStops() {
    if (stopDestination != null) {
      destinationEdit.setText(stopDestination.stop_name.replace(" Caltrain", ""));
    }

    if (stopSource != null) {
      sourceEdit.setText(stopSource.stop_name.replace(" Caltrain", ""));
    }
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
    timeEdit.setText(DateTimeFormat.forPattern("E, MMM d @ h:mma").print(stopDateTime));
  }

  @OnClick(R.id.trip_filter_refresh_date)
  void refreshDateClick() {
    stopDateTime = new LocalDateTime();
    updateTimeEdit();
  }

  @OnClick(R.id.trip_filter_destination)
  void destinationClick() {
    chooChooFragmentManager.loadSearchForSpotFragment(stopSource, null, stopMethod, stopDateTime);
  }

  @OnClick(R.id.trip_filter_source)
  void sourceClick() {
    chooChooFragmentManager.loadSearchForSpotFragment(null, stopDestination, stopMethod, stopDateTime);
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
    outState.putParcelable(BundleKeys.STOP_DESTINATION, Parcels.wrap(stopDestination));
    outState.putParcelable(BundleKeys.STOP_SOURCE, Parcels.wrap(stopSource));
    outState.putLong(BundleKeys.STOP_DATETIME, stopDateTime.toDate().getTime());
    outState.putInt(BundleKeys.STOP_METHOD, stopMethod);
    super.onSaveInstanceState(outState);
  }

  private void unWrapBundle(Bundle bundle) {
    if (bundle != null) {
      stopDestination = Parcels.unwrap(bundle.getParcelable(BundleKeys.STOP_DESTINATION));
      stopSource = Parcels.unwrap(bundle.getParcelable(BundleKeys.STOP_SOURCE));
      stopDateTime = new LocalDateTime(bundle.getLong(BundleKeys.STOP_DATETIME));
      stopMethod = bundle.getInt(BundleKeys.STOP_METHOD);
    }
  }

  private Action1<RxMessage> handleRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.SWITCH_SOURCE_DESTINATION_SELECTED)) {
          chooChooFragmentManager.loadTripFilterFragment(stopMethod, stopDateTime, stopDestination, stopSource);
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.DATE_TIME_SELECTED)) {
          Pair<Integer, LocalDateTime> pair = ((RxMessageStopMethodAndDateTime) rxMessage).getMessage();
          stopMethod = pair.first;
          stopDateTime = pair.second;
          chooChooFragmentManager.loadTripFilterFragment(stopMethod, stopDateTime, stopSource, stopDestination);
        }
      }
    };
  }
}
