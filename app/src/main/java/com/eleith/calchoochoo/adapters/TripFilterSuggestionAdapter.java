package com.eleith.calchoochoo.adapters;

import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eleith.calchoochoo.ChooChooRouterManager;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.TripFilterActivity;
import com.eleith.calchoochoo.dagger.ChooChooScope;
import com.eleith.calchoochoo.data.PossibleTrip;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ChooChooScope
public class TripFilterSuggestionAdapter extends RecyclerView.Adapter<TripFilterSuggestionAdapter.RouteViewHolder> {
  private ArrayList<PossibleTrip> possibleTrips;
  private TripFilterActivity tripFilterActivity;
  private ChooChooRouterManager chooChooRouterManager;

  @Inject
  public TripFilterSuggestionAdapter(TripFilterActivity tripFilterActivity, ChooChooRouterManager chooChooRouterManager) {
    this.tripFilterActivity = tripFilterActivity;
    this.chooChooRouterManager = chooChooRouterManager;
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
    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("h:mma");

    holder.arrivalTime.setText(dateTimeFormatter.print(possibleTrip.getArrivalTime()));
    holder.departureTime.setText(dateTimeFormatter.print(possibleTrip.getDepartureTime()));
    holder.tripPrice.setText(String.format(Locale.getDefault(), "$%.2f", price));

    if (possibleTrip.getRouteLongName().contains("Bullet")) {
      holder.trainImage.setImageDrawable(tripFilterActivity.getDrawable(R.drawable.ic_train_bullet));
      holder.trainImage.setContentDescription(tripFilterActivity.getString(R.string.bullet_train));
    } else {
      holder.trainImage.setImageDrawable(tripFilterActivity.getDrawable(R.drawable.ic_train_local));
      holder.trainImage.setContentDescription(tripFilterActivity.getString(R.string.local_train));
    }

    holder.tripNumber.setText(possibleTrip.getTripId());
  }

  @Override
  public int getItemCount() {
    return possibleTrips.size();
  }

  public class RouteViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.trip_possible_trip_stop_start_time)
    TextView arrivalTime;
    @BindView(R.id.trip_possible_trip_stop_end_time)
    TextView departureTime;
    @BindView(R.id.trip_possible_trip_price)
    TextView tripPrice;
    @BindView(R.id.trip_possible_trip_id)
    TextView tripNumber;
    @BindView(R.id.trip_possible_train_image)
    ImageView trainImage;
    @BindView(R.id.trip_possible_train_number_text)
    TextView tripNumberText;

    @OnClick(R.id.trip_possible_summary)
    void onClickTripSummary() {
      PossibleTrip possibleTrip = possibleTrips.get(getAdapterPosition());
      Pair<View, String> p1 = new Pair<>((View) trainImage, tripFilterActivity.getString(R.string.transition_train_image));
      Pair<View, String> p2 = new Pair<>((View) tripNumber, tripFilterActivity.getString(R.string.transition_train_number));
      Pair<View, String> p3 = new Pair<>((View) tripNumberText, tripFilterActivity.getString(R.string.transition_train_number_text));

      ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(tripFilterActivity, p1, p2, p3);
      chooChooRouterManager.loadTripActivity(tripFilterActivity, possibleTrip.getTripId(), possibleTrip.getFirstStopId(), possibleTrip.getLastStopId(), options);
    }

    private RouteViewHolder(View v) {
      super(v);
      ButterKnife.bind(this, v);
    }
  }
}