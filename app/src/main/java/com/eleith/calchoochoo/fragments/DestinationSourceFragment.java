package com.eleith.calchoochoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
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
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessagePairStopReason;

import org.joda.time.LocalDateTime;
import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;


public class DestinationSourceFragment extends Fragment {
  private Stop stopDestination;
  private Stop stopSource;
  private LocalDateTime stopDateTime = new LocalDateTime();
  private int stopMethod = RxMessageArrivalOrDepartDateTime.ARRIVING;

  @BindView(R.id.destinationEdit) TextView destinationEdit;
  @BindView(R.id.sourceEdit) TextView sourceEdit;
  @BindView(R.id.timeEdit) TextView timeEdit;

  @Inject RxBus rxBus;

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
    View view = inflater.inflate(R.layout.fragment_destination_source, container, false);
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
            timeEdit.setText("arriving on " + stopDateTime.toString());
          } else {
            timeEdit.setText("departing on " + stopDateTime.toString());
          }

    //if (stopSource == null || stopDestination == null) {
    //  timeEdit.setVisibility(View.INVISIBLE);
    //} else {
    //  timeEdit.setVisibility(View.VISIBLE);
    //}
  }

  @OnClick(R.id.destinationEdit)
  void destinationClick() {
    rxBus.send(new RxMessage(RxMessageKeys.DESTINATION_SELECTED));
  }

  @OnClick(R.id.sourceEdit)
  void sourceClick() {
    rxBus.send(new RxMessage(RxMessageKeys.SOURCE_SELECTED));
  }

  @OnClick(R.id.timeEdit)
  void timeClick() {
    DepartingArrivingDialogFragment dialog = new DepartingArrivingDialogFragment();
    dialog.show(getFragmentManager(), "dialog");
  }
}
