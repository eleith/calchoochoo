package com.eleith.calchoochoo.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.StopActivity;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.ColorUtils;
import com.eleith.calchoochoo.utils.DataStringUtils;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.TripUtils;

import org.parceler.Parcels;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class StopSummaryFragment extends Fragment {
  private Stop stop;
  private StopActivity stopActivity;
  private Subscription subscription;
  private int direction;

  @BindView(R.id.stop_summary_name)
  TextView stopName;
  @BindView(R.id.stop_summary_zone)
  TextView stopZone;
  @BindView(R.id.stop_summary_arrow_image)
  ImageView stopArrowImage;
  @BindView(R.id.stop_summary_direction_text)
  TextView stopDirectionText;

  @Inject
  RxBus rxBus;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    stopActivity = (StopActivity) getActivity();
    stopActivity.getComponent().inject(this);
    unWrapBundle(savedInstanceState == null ? getArguments() : savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_stop_summary, container, false);
    ButterKnife.bind(this, view);

    unWrapBundle(savedInstanceState);

    stopName.setText(DataStringUtils.removeCaltrain(stop.stop_name));
    stopZone.setText(String.format(Locale.getDefault(), "%d", stop.zone_id + 1));

    if (direction == TripUtils.DIRECTION_SOUTH) {
      stopArrowImage.setImageDrawable(stopActivity.getDrawable(R.drawable.ic_arrow_downward_black_24dp));
      stopDirectionText.setText(stopActivity.getString(R.string.san_jose));
    } else {
      stopArrowImage.setImageDrawable(stopActivity.getDrawable(R.drawable.ic_arrow_upward_black_24dp));
      stopDirectionText.setText(stopActivity.getString(R.string.san_francisco));
    }

    subscription = rxBus.observeEvents(RxMessage.class).subscribe(handleRxMessages());
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    subscription.unsubscribe();
  }

  @Override
  public void onStop() {
    super.onStop();
    subscription.unsubscribe();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(BundleKeys.STOP, Parcels.wrap(stop));
    super.onSaveInstanceState(outState);
  }

  private void unWrapBundle(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      stop = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.STOP));
      direction = savedInstanceState.getInt(BundleKeys.DIRECTION);
    }
  }

  private Action1<RxMessage> handleRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.FAB_CLICKED)) {
          CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
          CustomTabsIntent customTabsIntent = builder.build();
          builder.setToolbarColor(ColorUtils.getThemeColor(getActivity(), R.attr.colorPrimary));
          customTabsIntent.launchUrl(getActivity(), Uri.parse(stop.stop_url));
        }
      }
    };
  }

  @OnClick(R.id.stop_summary_switch_direction)
  public void switchDirection() {
    rxBus.send(new RxMessage(RxMessageKeys.SWITCH_SOURCE_DESTINATION_SELECTED));
  }
}
