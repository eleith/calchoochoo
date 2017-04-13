package com.eleith.calchoochoo.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.TripActivity;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.DataStringUtils;
import com.eleith.calchoochoo.utils.Notifications;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageNotificationPair;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetAlarmDialogFragment extends android.support.v4.app.DialogFragment {
  private PossibleTrip possibleTrip;

  @Inject
  RxBus rxBus;

  @BindView(R.id.alarm_dialog_arrival_minutes)
  Spinner arrivalSpinner;
  @BindView(R.id.alarm_dialog_departure_minutes)
  Spinner departureSpinner;
  @BindView(R.id.alarm_dialog_arrival_minutes_form)
  LinearLayout arrivalForm;
  @BindView(R.id.alarm_dialog_departure_minutes_form)
  LinearLayout departureForm;
  @BindView(R.id.alarm_dialog_arrival_enable)
  Switch arrivalSwitch;
  @BindView(R.id.alarm_dialog_departure_enable)
  Switch departureSwitch;
  @BindView(R.id.alarm_dialog_ok)
  Button okButton;
  @BindView(R.id.alarm_dialog_cancel)
  Button cancelButton;
  @BindView(R.id.alarm_dialog_arrival_text)
  TextView arrivalText;
  @BindView(R.id.alarm_dialog_departure_text)
  TextView departureText;
  @BindView(R.id.alarm_dialog_trip_number)
  TextView tripText;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((TripActivity) getActivity()).getComponent().inject(this);
    unWrapBundle(savedInstanceState == null ? getArguments() : savedInstanceState);
  }

  @Override
  @NonNull
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View view = inflater.inflate(R.layout.fragment_alarm_dialog, null);
    ButterKnife.bind(this, view);
    builder.setView(view);

    unWrapBundle(savedInstanceState);
    initializeDialogValues();

    return builder.create();
  }

  private void initializeDialogValues() {
    Notifications notifications = new Notifications(getContext());
    int arrivingMinutes = notifications.getAlarmMinutes(possibleTrip.getTripId(), Notifications.ARRIVING);
    int departingMinutes = notifications.getAlarmMinutes(possibleTrip.getTripId(), Notifications.DEPARTING);

    tripText.setText(possibleTrip.getTripShortName());
    arrivalText.setText(DataStringUtils.removeCaltrain(possibleTrip.getLastStopName()));
    departureText.setText(DataStringUtils.removeCaltrain(possibleTrip.getFirstStopName()));

    if (arrivingMinutes != -1) {
      arrivalSpinner.setSelection((arrivingMinutes / 5) - 1);
      arrivalSwitch.setChecked(true);
      toggleArrivalForm();
    } else {
      arrivalSpinner.setSelection(2);
      arrivalSwitch.setChecked(false);
    }

    if (departingMinutes != -1) {
      departureSpinner.setSelection((departingMinutes / 5) - 1);
      departureSwitch.setChecked(true);
      toggleDepartureForm();
    } else {
      departureSpinner.setSelection(2);
      departureSwitch.setChecked(false);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(BundleKeys.POSSIBLE_TRIP, Parcels.wrap(possibleTrip));
    super.onSaveInstanceState(outState);
  }

  private void unWrapBundle(Bundle bundle) {
    if (bundle != null) {
      possibleTrip = Parcels.unwrap(bundle.getParcelable(BundleKeys.POSSIBLE_TRIP));
    }
  }

  @OnClick(R.id.alarm_dialog_cancel)
  public void cancelDialog() {
    getDialog().dismiss();
  }

  @OnClick(R.id.alarm_dialog_ok)
  public void submitDialog() {
    Boolean arrivalOn = arrivalSwitch.isChecked();
    Boolean departureOn = departureSwitch.isChecked();

    int arrivalMinutes = arrivalOn ? (arrivalSpinner.getSelectedItemPosition() + 1) * 5 : -1;
    int departureMinutes = departureOn ? (departureSpinner.getSelectedItemPosition() + 1) * 5 : -1;

    rxBus.send(new RxMessageNotificationPair(RxMessageKeys.NOTIFICATION_SELECTED, new Pair<>(departureMinutes, arrivalMinutes)));

    getDialog().dismiss();
  }

  @OnClick(R.id.alarm_dialog_arrival_enable)
  public void toggleArrivalForm() {
    if (arrivalSwitch.isChecked()) {
      arrivalForm.setVisibility(View.VISIBLE);
    } else {
      arrivalForm.setVisibility(View.GONE);
    }
  }

  @OnClick(R.id.alarm_dialog_departure_enable)
  public void toggleDepartureForm() {
    if (departureSwitch.isChecked()) {
      departureForm.setVisibility(View.VISIBLE);
    } else {
      departureForm.setVisibility(View.GONE);
    }
  }
}
