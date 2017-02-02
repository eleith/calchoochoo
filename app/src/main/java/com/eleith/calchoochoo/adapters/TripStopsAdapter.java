package com.eleith.calchoochoo.adapters;

import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.Queries;
import com.eleith.calchoochoo.data.Routes;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.StopTimes;
import com.eleith.calchoochoo.data.StopTimes$$Parcelable;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStop;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageString;

import org.joda.time.Minutes;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class TripStopsAdapter extends RecyclerView.Adapter<TripStopsAdapter.OneTripStopHolder> {
  private ArrayList<Pair<Stop, StopTimes>> tripStops;
  private RxBus rxBus;

  public TripStopsAdapter(RxBus rxBus) {
    this.rxBus = rxBus;
  }

  public void setTripStops(ArrayList<Pair<Stop, StopTimes>> tripStops) {
    this.tripStops = tripStops;
  }

  @Override
  public OneTripStopHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_trip_stop, parent, false);
    return new OneTripStopHolder(view);
  }

  @Override
  public void onBindViewHolder(OneTripStopHolder holder, int position) {
    Pair<Stop, StopTimes> pair = tripStops.get(position);
    // set data in holder
    holder.name.setText(pair.first.stop_name);
    holder.startTime.setText(pair.second.arrival_time.toString());
    holder.stopTime.setText(pair.second.departure_time.toString());
  }

  @Override
  public int getItemCount() {
    return tripStops.size();
  }

  public class OneTripStopHolder extends RecyclerView.ViewHolder {
    TextView name;
    TextView startTime;
    TextView stopTime;

    @OnClick(R.id.one_trip_stop_details)
    void onClickTripSummary() {
      rxBus.send(new RxMessageStop(RxMessageKeys.STOP_SELECTED, tripStops.get(getAdapterPosition()).first));
    }

    private OneTripStopHolder(View v) {
      super(v);

      ButterKnife.bind(this, v);
      name = (TextView) v.findViewById(R.id.one_trip_stop_end_time);
      stopTime = (TextView) v.findViewById(R.id.one_trip_stop_name);
      startTime = (TextView) v.findViewById(R.id.one_trip_stop_start_time);
    }
  }
}