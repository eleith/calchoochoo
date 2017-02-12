package com.eleith.calchoochoo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.data.PossibleTrip;
import com.eleith.calchoochoo.data.Queries;
import com.eleith.calchoochoo.data.Routes;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessagePossibleTrip;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class RouteViewAdapter extends RecyclerView.Adapter<RouteViewAdapter.RouteViewHolder> {
  private ArrayList<PossibleTrip> possibleTrips;
  private RxBus rxBus;

  public RouteViewAdapter(RxBus rxBus) {
    this.rxBus = rxBus;
  }

  public void setPossibleTrips(ArrayList<PossibleTrip> possibleTrips) {
    this.possibleTrips = possibleTrips;
  }

  @Override
  public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_trip_possible, parent, false);
    return new RouteViewHolder(view);
  }

  @Override
  public void onBindViewHolder(RouteViewHolder holder, int position) {
    PossibleTrip possibleTrip = possibleTrips.get(position);
    Float price = possibleTrip.getPrice();
    Routes route = Queries.getRouteById(possibleTrip.getRouteId());
    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("h:mma");

    holder.arrivalTime.setText(dateTimeFormatter.print(possibleTrip.getArrivalTime()));
    holder.departureTime.setText(dateTimeFormatter.print(possibleTrip.getDepartureTime()));
    holder.tripPrice.setText(String.format(Locale.getDefault(), "$%.2f", price));

    if(route != null && route.route_long_name.contains("Bullet")) {
      holder.trainLocalImage.setVisibility(View.GONE);
      holder.trainBulletImage.setVisibility(View.VISIBLE);
    } else {
      holder.trainBulletImage.setVisibility(View.GONE);
      holder.trainLocalImage.setVisibility(View.VISIBLE);
    }

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
    TextView tripNumber;
    ImageView trainBulletImage;
    ImageView trainLocalImage;

    @OnClick(R.id.trip_possible_summary)
    void onClickTripSummary() {
      rxBus.send(new RxMessagePossibleTrip(RxMessageKeys.TRIP_SELECTED, possibleTrips.get(getAdapterPosition())));
    }

    private RouteViewHolder(View v) {
      super(v);

      ButterKnife.bind(this, v);

      arrivalTime = (TextView) v.findViewById(R.id.trip_possible_trip_stop_start_time);
      departureTime = (TextView) v.findViewById(R.id.trip_possible_trip_stop_end_time);
      tripPrice = (TextView) v.findViewById(R.id.trip_possible_trip_price);
      tripNumber = (TextView) v.findViewById(R.id.trip_possible_trip_id);
      trainBulletImage = (ImageView) v.findViewById(R.id.trip_possible_train_bullet);
      trainLocalImage = (ImageView) v.findViewById(R.id.trip_possible_train_local);
    }
  }
}