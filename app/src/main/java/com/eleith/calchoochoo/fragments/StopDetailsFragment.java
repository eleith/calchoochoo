package com.eleith.calchoochoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.ScheduleExplorerActivity;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.parceler.Parcels;

import butterknife.ButterKnife;

public class StopDetailsFragment extends Fragment {
  private Stop stop;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((ScheduleExplorerActivity) getActivity()).getComponent().inject(this);
    unWrapBundle(getArguments());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_stop_details, container, false);
    ButterKnife.bind(this, view);

    unWrapBundle(savedInstanceState);

    return view;
  }

  private void unWrapBundle(Bundle savedInstanceState) {
     if (savedInstanceState != null) {
      stop = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.STOP));
    }
  }
}
