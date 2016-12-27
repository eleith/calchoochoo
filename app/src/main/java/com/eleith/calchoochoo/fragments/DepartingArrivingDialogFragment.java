package com.eleith.calchoochoo.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.ScheduleExplorerActivity;
import com.eleith.calchoochoo.utils.InfinitePager;
import com.eleith.calchoochoo.utils.InfinitePagerDataDates;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxMessage;
import com.eleith.calchoochoo.utils.RxMessageKeys;

import org.joda.time.LocalDate;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DepartingArrivingDialogFragment extends android.support.v4.app.DialogFragment {
  @Inject
  RxBus rxBus;
  @BindView(R.id.picker)
  View picker;
  @BindView(R.id.timeTabs)
  TabLayout tabLayout;
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
    // send departing or arriving
    // send DateTime
    rxBus.send(new RxMessage(RxMessageKeys.TIME_SELECTED, new Date()));
    getDialog().dismiss();
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

    LocalDate[] pagerData = {new LocalDate().minusDays(1), new LocalDate(), new LocalDate().plusDays(1)};
    infinitePager.setInfinitePagerData(new InfinitePagerDataDates(pagerData));

    builder.setView(view);
    return builder.create();
  }
}
