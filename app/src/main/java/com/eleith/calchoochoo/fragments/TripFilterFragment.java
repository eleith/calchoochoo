package com.eleith.calchoochoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.ScheduleExplorerActivity;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageArrivalOrDepartDateTime;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TripFilterFragment extends Fragment {
  private Stop stopDestination;
  private Stop stopSource;
  private LocalDateTime stopDateTime = new LocalDateTime();
  private int stopMethod = RxMessageArrivalOrDepartDateTime.ARRIVING;

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

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ((ScheduleExplorerActivity) getActivity()).getComponent().inject(this);

    Bundle arguments = getArguments();
    if (arguments != null) {
      stopDestination = Parcels.unwrap(arguments.getParcelable(BundleKeys.STOP_DESTINATION));
      stopSource = Parcels.unwrap(arguments.getParcelable(BundleKeys.STOP_SOURCE));
      stopDateTime = new LocalDateTime(arguments.getLong(BundleKeys.STOP_DATETIME));
      stopMethod = arguments.getInt(BundleKeys.STOP_METHOD);
    }
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
    if (stopDestination != null) {
      destinationEdit.setText(stopDestination.stop_name);
    }

    if (stopSource != null) {
      sourceEdit.setText(stopSource.stop_name);
    }
    updateTimeEdit();
  }

  private void updateTimeEdit() {
    if (stopMethod == RxMessageArrivalOrDepartDateTime.ARRIVING) {
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
    rxBus.send(new RxMessage(RxMessageKeys.DESTINATION_SELECTED));
  }

  @OnClick(R.id.trip_filter_source)
  void sourceClick() {
    rxBus.send(new RxMessage(RxMessageKeys.SOURCE_SELECTED));
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
}
