package com.eleith.calchoochoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.eleith.calchoochoo.ChooChooRouterManager;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.TripActivity;
import com.eleith.calchoochoo.data.ChooChooLoader;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.DataStringUtils;
import com.eleith.calchoochoo.utils.RxBus;

import org.joda.time.Minutes;
import org.parceler.Parcels;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TripSummaryFragment extends Fragment {
  private PossibleTrip possibleTrip;

  @Inject
  ChooChooLoader chooChooLoader;
  @Inject
  ChooChooRouterManager chooChooRouterManager;

  @Inject
  RxBus rxBus;

  @BindView(R.id.trip_summary_from)
  TextView tripSummaryFrom;

  @BindView(R.id.trip_summary_number)
  TextView tripSummaryNumber;

  @BindView(R.id.trip_summary_to)
  TextView tripSummaryTo;

  @BindView(R.id.trip_summary_price)
  TextView tripSummaryPrice;

  @BindView(R.id.trip_summary_total_time)
  TextView tripSummaryTotalTime;

  @BindView(R.id.trip_summary_train_image)
  ImageView tripSummaryImage;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((TripActivity) getActivity()).getComponent().inject(this);
    unWrapBundle(savedInstanceState == null ? getArguments() : savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_trip_summary, container, false);
    unWrapBundle(savedInstanceState);
    ButterKnife.bind(this, view);
    updateSummaryBar();

    view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
      public boolean onPreDraw() {
        view.getViewTreeObserver().removeOnPreDrawListener(this);
        getActivity().startPostponedEnterTransition();
        return true;
      }
    });

    return view;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(BundleKeys.POSSIBLE_TRIP, Parcels.wrap(possibleTrip));
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  private void unWrapBundle(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      possibleTrip = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.POSSIBLE_TRIP));
    }
  }

  @OnClick
  public void exploreOtherStationTrips() {
    chooChooRouterManager.loadTripFilterActivity(getActivity(), possibleTrip.getFirstParentStopId(), possibleTrip.getLastParentStopId());
  }

  private void updateSummaryBar() {
    tripSummaryFrom.setText(DataStringUtils.removeCaltrain(possibleTrip.getFirstStopName()));
    tripSummaryTo.setText(DataStringUtils.removeCaltrain(possibleTrip.getLastStopName()));

    tripSummaryPrice.setText(String.format(Locale.getDefault(), "$%.2f", possibleTrip.getPrice()));
    tripSummaryTotalTime.setText(String.format(Locale.getDefault(), "%d min", Minutes.minutesBetween(possibleTrip.getArrivalTime(), possibleTrip.getDepartureTime()).getMinutes()));
    tripSummaryNumber.setText(possibleTrip.getTripShortName());

    if (possibleTrip.getRouteLongName().contains("Bullet")) {
      tripSummaryImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_train_bullet));
      tripSummaryImage.setContentDescription(getString(R.string.bullet_train));
    } else {
      tripSummaryImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_train_local));
      tripSummaryImage.setContentDescription(getString(R.string.local_train));
    }
  }
}
