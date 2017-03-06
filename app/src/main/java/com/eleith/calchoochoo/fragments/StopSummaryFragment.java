package com.eleith.calchoochoo.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleith.calchoochoo.ChooChooActivity;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.parceler.Parcels;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

public class StopSummaryFragment extends Fragment {
  private Stop stop;
  private ChooChooActivity chooChooActivity;
  private Subscription subscription;

  @BindView(R.id.stop_summary_name)
  TextView stopName;
  @BindView(R.id.stop_summary_zone)
  TextView stopZone;
  @BindView(R.id.stop_summary_datetime)
  TextView stopDateTime;

  @Inject
  RxBus rxBus;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    chooChooActivity = (ChooChooActivity) getActivity();
    chooChooActivity.getComponent().inject(this);
    unWrapBundle(savedInstanceState == null ? getArguments() : savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_stop_summary, container, false);
    ButterKnife.bind(this, view);

    unWrapBundle(savedInstanceState);
    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("h:mm a 'on' M/d");

    stopName.setText(stop.stop_name);
    stopZone.setText(String.format(Locale.getDefault(), "%d", stop.zone_id + 1));
    stopDateTime.setText(dateTimeFormatter.print(new LocalDateTime()));

    chooChooActivity.fabEnable(R.drawable.ic_link_black_24dp);
    subscription = rxBus.observeEvents(RxMessage.class).subscribe(handleRxMessages());

    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    chooChooActivity.fabDisable();
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
    }
  }

  private Action1<RxMessage> handleRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.FAB_CLICKED)) {
          Intent intent = new Intent(Intent.ACTION_VIEW);
          intent.setData(Uri.parse(stop.stop_url));
          getContext().startActivity(intent);
        }
      }
    };
  }
}
