package com.eleith.calchoochoo.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.ScheduleExplorerActivity;
import com.eleith.calchoochoo.utils.InfinitePager;
import com.eleith.calchoochoo.utils.InfinitePagerDataDates;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageArrivalOrDepartDateTime;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DepartingArrivingDialogFragment extends android.support.v4.app.DialogFragment {
  @Inject
  RxBus rxBus;
  @BindView(R.id.timePicker)
  TimePicker timePicker;
  @BindView(R.id.dateSpinner)
  InfinitePager infinitePager;
  @BindView(R.id.rightDateButton)
  ImageButton rightDateButton;
  @BindView(R.id.leftDateButton)
  ImageButton leftDateButton;
  @BindView(R.id.departOrArriveCancel)
  TextView departOrArriveCancelText;
  @BindView(R.id.departOrArriveSelect)
  TextView getDepartOrArriveSelectText;
  @BindView(R.id.timeTabs)
  TabLayout timeTabs;

  private int departOrArriveMethod = RxMessageArrivalOrDepartDateTime.ARRIVING;
  private InfinitePagerDataDates infinitePagerDataDates = new InfinitePagerDataDates(new LocalDate());

  @OnClick(R.id.rightDateButton)
  public void rightDateButtonClick() {
    infinitePager.setCurrentItem(2, true);
  }

  @OnClick(R.id.leftDateButton)
  public void leftLeftDateButtonClick() {
    infinitePager.setCurrentItem(0, true);
  }

  @OnClick(R.id.departOrArriveCancel)
  public void cancelClick() {
    getDialog().dismiss();
  }

  @OnClick(R.id.departOrArriveSelect)
  public void selectClick() {
    LocalDate departOrArriveDate = (LocalDate) infinitePagerDataDates.getData(infinitePager.getCurrentItem());
    LocalTime departOrArriveTime = new LocalTime(timePicker.getHour(), timePicker.getMinute());
    LocalDateTime departOrArriveDateTime = departOrArriveDate.toLocalDateTime(departOrArriveTime);

    if (timeTabs.getSelectedTabPosition() == 0) {
      departOrArriveMethod = RxMessageArrivalOrDepartDateTime.ARRIVING;
    } else {
      departOrArriveMethod = RxMessageArrivalOrDepartDateTime.DEPARTING;
    }

    Pair<Integer, LocalDateTime> pair = new Pair<>(departOrArriveMethod, departOrArriveDateTime);

    rxBus.send(new RxMessageArrivalOrDepartDateTime(RxMessageKeys.DATE_TIME_SELECTED, pair));
    getDialog().dismiss();
  }

  public void openDatePicker() {
    LocalDate selectedDate = infinitePagerDataDates.getData(infinitePager.getCurrentItem());
    DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
      @Override
      public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        LocalDate setDate = new LocalDate(year, month - 1, dayOfMonth);
        infinitePager.setInfinitePagerData(new InfinitePagerDataDates(setDate));
      }
    }, selectedDate.getYear(), selectedDate.getMonthOfYear() - 1, selectedDate.getDayOfMonth());
    dialog.show();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((ScheduleExplorerActivity) getActivity()).getComponent().inject(this);
  }

  @Override
  @NonNull
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View view = inflater.inflate(R.layout.fragment_departing_arriving_selector, null);
    ButterKnife.bind(this, view);
    builder.setView(view);

    infinitePager.setInfinitePagerData(infinitePagerDataDates);
    infinitePager.setOnItemClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openDatePicker();
      }
    });

    return builder.create();
  }
}
