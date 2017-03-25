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
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.TripFilterActivity;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.InfinitePager;
import com.eleith.calchoochoo.utils.InfinitePagerDataDates;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStopMethodAndDateTime;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStopsAndDetails;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TripFilterTimeAndMethodDialogFragment extends android.support.v4.app.DialogFragment {
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
  TextView methodCancelText;
  @BindView(R.id.departOrArriveSelect)
  TextView methodSelectText;
  @BindView(R.id.timeTabs)
  TabLayout timeTabs;

  private int stopMethod = RxMessageStopsAndDetails.DETAIL_ARRIVING;
  private LocalDateTime localDateTime = new LocalDateTime();
  private InfinitePagerDataDates infinitePagerDataDates;

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
    LocalDate departOrArriveDate = infinitePagerDataDates.getData(infinitePager.getCurrentItem());
    LocalTime departOrArriveTime = new LocalTime(timePicker.getHour(), timePicker.getMinute());
    LocalDateTime departOrArriveDateTime = departOrArriveDate.toLocalDateTime(departOrArriveTime);

    if (timeTabs.getSelectedTabPosition() == 0) {
      stopMethod = RxMessageStopMethodAndDateTime.ARRIVING;
    } else {
      stopMethod = RxMessageStopMethodAndDateTime.DEPARTING;
    }

    Pair<Integer, LocalDateTime> pair = new Pair<>(stopMethod, departOrArriveDateTime);

    rxBus.send(new RxMessageStopMethodAndDateTime(RxMessageKeys.DATE_TIME_SELECTED, pair));
    getDialog().dismiss();
  }

  private void openDatePicker() {
    LocalDate selectedDate = infinitePagerDataDates.getData(infinitePager.getCurrentItem());
    DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
      @Override
      public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        LocalDate setDate = new LocalDate(year, month + 1, dayOfMonth);
        infinitePager.setInfinitePagerData(new InfinitePagerDataDates(setDate));
      }
    }, selectedDate.getYear(), selectedDate.getMonthOfYear() - 1, selectedDate.getDayOfMonth());
    dialog.show();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((TripFilterActivity) getActivity()).getComponent().inject(this);
    unWrapBundle(savedInstanceState == null ? getArguments() : savedInstanceState);
  }

  @Override
  @NonNull
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View view = inflater.inflate(R.layout.fragment_departing_arriving_selector, null);
    ButterKnife.bind(this, view);
    builder.setView(view);

    unWrapBundle(savedInstanceState);
    initializeDialogValues();

    return builder.create();
  }

  private void initializeDialogValues() {
    timePicker.setHour(localDateTime.getHourOfDay());
    timePicker.setMinute(localDateTime.getMinuteOfHour());

    int position = stopMethod == RxMessageStopsAndDetails.DETAIL_ARRIVING ? 0 : 1;
    TabLayout.Tab tab = timeTabs.getTabAt(position);
    if (tab != null) {
      tab.select();
    }

    infinitePagerDataDates = new InfinitePagerDataDates(localDateTime.toLocalDate());
    infinitePager.setInfinitePagerData(infinitePagerDataDates);
    infinitePager.setOnItemClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openDatePicker();
      }
    });
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putLong(BundleKeys.STOP_DATETIME, localDateTime.toDate().getTime());
    outState.putInt(BundleKeys.STOP_METHOD, stopMethod);
    super.onSaveInstanceState(outState);
  }

  private void unWrapBundle(Bundle bundle) {
    if (bundle != null) {
      stopMethod = bundle.getInt(BundleKeys.STOP_METHOD);
      localDateTime = new LocalDateTime(bundle.getLong(BundleKeys.STOP_DATETIME));
    }
  }
}
