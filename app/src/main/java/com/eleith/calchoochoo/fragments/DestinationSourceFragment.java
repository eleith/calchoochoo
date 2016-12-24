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
import com.eleith.calchoochoo.utils.RxMessage;
import com.eleith.calchoochoo.utils.RxMessageKeys;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;


public class DestinationSourceFragment extends Fragment {
  private Stop stopDestination;
  private Stop stopSource;

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
      stopDestination = arguments.getParcelable(BundleKeys.DESTINATION);
      stopSource = arguments.getParcelable(BundleKeys.SOURCE);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_destination_source, container, false);
    ButterKnife.bind(this, view);

    if (stopDestination != null) {
      destinationEdit.setText(stopDestination.getName());
    }

    if (stopSource != null) {
      sourceEdit.setText(stopSource.getName());
    }

    rxBus.observeEvents(RxMessage.class).subscribe(handleDestinationSourceFragmentRxMessages());

    return view;
  }

  private Action1<RxMessage> handleDestinationSourceFragmentRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        String type = rxMessage.getType();

        if (type.equals(RxMessageKeys.TIME_SELECTED)) {
          Date date = (Date) rxMessage.getMessage();
          timeEdit.setText(date.toString());
        }
      }
    };
  }
  @OnClick(R.id.destinationEdit)
  public void destinationClick() {
    rxBus.send(new RxMessage(RxMessageKeys.DESTINATION_SELECTED));
  }

  @OnClick(R.id.sourceEdit)
  public void sourceClick() {
    rxBus.send(new RxMessage(RxMessageKeys.SOURCE_SELECTED));
  }

  @OnClick(R.id.timeEdit)
  public void timeClick() {
    DepartingArrivingDialogFragment dialog = new DepartingArrivingDialogFragment();
    dialog.show(getFragmentManager(), "dialog");
  }
}
