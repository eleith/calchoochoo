package com.eleith.calchoochoo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleith.calchoochoo.data.Queries;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.StopTimes;
import com.eleith.calchoochoo.utils.RxBus;

import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.Minutes;

import java.util.ArrayList;
import java.util.Locale;

public class RouteViewAdapter extends RecyclerView.Adapter<RouteViewAdapter.RouteViewHolder> {
  private ArrayList<Triple<StopTimes, StopTimes, Float>> routeViewStopTimesAndPrice = new ArrayList<>();

  public RouteViewAdapter(RxBus rxBus) {

  }

  public void setRouteStopTimesAndPrice(ArrayList<Triple<StopTimes, StopTimes, Float>> routeViewStopTimesAndPrice) {
    this.routeViewStopTimesAndPrice = routeViewStopTimesAndPrice;
  }

  @Override
  public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_stop_times_trip, parent, false);
    return new RouteViewHolder(view);
  }

  @Override
  public void onBindViewHolder(RouteViewHolder holder, int position) {
    Triple<StopTimes, StopTimes, Float> stopTimesAndPrice = routeViewStopTimesAndPrice.get(position);
    StopTimes stopTimeStart = stopTimesAndPrice.getMiddle();
    StopTimes stopTimeEnd = stopTimesAndPrice.getLeft();
    Float price = stopTimesAndPrice.getRight();

    holder.arrivalTime.setText(stopTimeEnd.arrival_time.toString());
    holder.departureTime.setText(stopTimeStart.departure_time.toString());
    holder.tripPrice.setText(String.format(Locale.getDefault(), "$%.2f", price));
    holder.tripRouteName.setText("Baby Bullet");
    holder.tripTotalTime.setText(String.format(Locale.getDefault(), "%d", Minutes.minutesBetween(stopTimeEnd.arrival_time, stopTimeStart.arrival_time).getMinutes()));
    holder.tripNumber.setText(stopTimeStart.trip_id);
  }

  @Override
  public int getItemViewType(int position) {
    return 0;
  }

  @Override
  public int getItemCount() {
    return routeViewStopTimesAndPrice.size();
  }

  public class RouteViewHolder extends RecyclerView.ViewHolder {
    TextView arrivalTime;
    TextView departureTime;
    TextView tripPrice;
    TextView tripTotalTime;
    TextView tripRouteName;
    TextView tripNumber;

    private RouteViewHolder(View v) {
      super(v);
      arrivalTime = (TextView) v.findViewById(R.id.trip_stop_start_time);
      departureTime = (TextView) v.findViewById(R.id.trip_stop_end_time);
      tripPrice = (TextView) v.findViewById(R.id.trip_price);
      tripTotalTime = (TextView) v.findViewById(R.id.trip_total_time);
      tripRouteName = (TextView) v.findViewById(R.id.trip_route_name);
      tripNumber = (TextView) v.findViewById(R.id.trip_id);
    }
  }
}