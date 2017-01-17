package com.eleith.calchoochoo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.Queries;
import com.eleith.calchoochoo.data.Routes;
import com.eleith.calchoochoo.utils.RxBus;

import org.joda.time.Minutes;

import java.util.ArrayList;
import java.util.Locale;

public class RouteViewAdapter extends RecyclerView.Adapter<RouteViewAdapter.RouteViewHolder> {
  private ArrayList<PossibleTrip> possibleTrips;

  public RouteViewAdapter(RxBus rxBus) {

  }

  public void setPossibleTrips(ArrayList<PossibleTrip> possibleTrips) {
    this.possibleTrips = possibleTrips;
  }

  @Override
  public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_stop_times_trip, parent, false);
    return new RouteViewHolder(view);
  }

  @Override
  public void onBindViewHolder(RouteViewHolder holder, int position) {
    PossibleTrip possibleTrip = possibleTrips.get(position);
    Float price = possibleTrip.getPrice();
    Routes route = Queries.getRouteById(possibleTrip.getRouteId());

    holder.arrivalTime.setText(possibleTrip.getArrivalTime().toString());
    holder.departureTime.setText(possibleTrip.getDepartureTime().toString());
    holder.tripPrice.setText(String.format(Locale.getDefault(), "$%.2f", price));

    if (route != null) {
      holder.tripRouteName.setText(route.route_long_name);
    }

    holder.tripTotalTime.setText(String.format(Locale.getDefault(), "%d", Minutes.minutesBetween(possibleTrip.getDepartureTime(), possibleTrip.getArrivalTime()).getMinutes()));
    holder.tripNumber.setText(possibleTrip.getTripId());
  }

  @Override
  public int getItemCount() {
    return possibleTrips.size();
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