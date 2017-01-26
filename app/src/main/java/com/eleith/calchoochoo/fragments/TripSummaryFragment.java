package com.eleith.calchoochoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.ScheduleExplorerActivity;
import com.eleith.calchoochoo.data.Queries;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.StopTimes;
import com.eleith.calchoochoo.utils.BundleKeys;
import android.support.v4.app.Fragment;
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class TripSummaryFragment extends Fragment{
  private Stop stopDestination;
  private Stop stopSource;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((ScheduleExplorerActivity) getActivity()).getComponent().inject(this);
    unWrapBundle(getArguments());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_trip_summary, container, false);
    ButterKnife.bind(this, view);

    unWrapBundle(savedInstanceState);

    return view;
  }

  private void unWrapBundle(Bundle savedInstanceState) {
     if (savedInstanceState != null) {
      stopDestination = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.STOP_DESTINATION));
      stopSource = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.STOP_SOURCE));
    }
  }
}
