package com.eleith.calchoochoo.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eleith.calchoochoo.ChooChooActivity;
import com.eleith.calchoochoo.ChooChooFragmentManager;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.dagger.ChooChooScope;
import com.eleith.calchoochoo.data.PossibleTrain;
import com.eleith.calchoochoo.data.Queries;
import com.eleith.calchoochoo.data.Routes;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.Trips;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStopsAndDetails;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ChooChooScope
public class StopCardAdapter extends RecyclerView.Adapter<StopCardAdapter.StopCardHolder> {
  private ArrayList<Stop> stops;
  private RxBus rxBus;
  private ChooChooFragmentManager chooChooFragmentManager;
  private int highlightedStopPosition;
  private static final int TYPE_NORMAL_STOP = 0;
  private static final int TYPE_HIGHLIGHTED_STOP = 1;

  @Inject
  public StopCardAdapter(RxBus rxBus, ChooChooFragmentManager chooChooFragmentManager) {
    stops = Queries.getAllParentStops();
    this.rxBus = rxBus;
    this.chooChooFragmentManager = chooChooFragmentManager;
  }

  @Override
  public StopCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    if (viewType == TYPE_NORMAL_STOP) {
      view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_stop_card, parent, false);
    } else {
      view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_stop_card_highlighted, parent, false);
    }
    return new StopCardHolder(view, parent);
  }

  @Override
  public void onBindViewHolder(StopCardHolder holder, int position) {
    Stop stop = stops.get(position);
    Integer zone = Queries.getZoneOfParentStop(stop.stop_id);
    holder.stopName.setText(stop.stop_name);

    if (zone != null) {
      holder.stopZone.setText(String.format(Locale.getDefault(), "%d", zone));
    }

    addRecentTrains(holder, stop);
  }

  private void addRecentTrains(StopCardHolder holder, final Stop stop) {
    holder.recentTrains.removeAllViews();
    ArrayList<PossibleTrain> possibleTrains = Queries.findNextTrain(stop, new LocalDateTime());

    if (possibleTrains.size() > 0) {
      for (int i = 0; i < 3 && i < possibleTrains.size(); i++) {
        PossibleTrain possibleTrain = possibleTrains.get(i);
        final Trips trip = Queries.getTripById(possibleTrain.getTripId());
        Routes route = Queries.getRouteById(possibleTrain.getRouteId());
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("h:mma");

        View recentTrains = LayoutInflater.from(holder.viewGroup.getContext()).inflate(R.layout.fragment_stop_card_widget_trainitem, holder.viewGroup, false);
        TextView recentTrainNumber = (TextView) recentTrains.findViewById(R.id.stop_card_widget_trainitem_number);
        TextView recentTrainDirection = (TextView) recentTrains.findViewById(R.id.stop_card_widget_direction);
        ImageView recentTrainImage = (ImageView) recentTrains.findViewById(R.id.stop_card_widget_trainitem_image);
        TextView recentTrainTime = (TextView) recentTrains.findViewById(R.id.stop_card_widget_trainitem_time);

        if (trip != null) {
          recentTrainNumber.setText(trip.trip_id);
          if (trip.direction_id == 1) {
            recentTrainDirection.setText(holder.viewGroup.getContext().getString(R.string.south_bound));
          } else {
            recentTrainDirection.setText(holder.viewGroup.getContext().getString(R.string.north_bound));
          }
        }

        if (route != null && route.route_long_name.contains("Bullet")) {
          recentTrainImage.setImageDrawable(holder.viewGroup.getContext().getDrawable(R.drawable.ic_train_bullet));
        } else {
          recentTrainImage.setImageDrawable(holder.viewGroup.getContext().getDrawable(R.drawable.ic_train_local));
        }

        recentTrains.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            chooChooFragmentManager.loadSearchForSpotFragment(stop, trip);
          }
        });
        recentTrainTime.setText(dateTimeFormatter.print(possibleTrain.getDepartureTime()));
        holder.recentTrains.addView(recentTrains);
      }
    } else {
      View noMoreTrains = LayoutInflater.from(holder.viewGroup.getContext()).inflate(R.layout.fragment_stop_card_widget_train_nomore, holder.viewGroup, false);
      holder.recentTrains.addView(noMoreTrains);
    }
  }

  @Override
  public int getItemCount() {
    return stops.size();
  }

  @Override
  public int getItemViewType(int position) {
    if (position == highlightedStopPosition) {
      return TYPE_HIGHLIGHTED_STOP;
    } else {
      return TYPE_NORMAL_STOP;
    }
  }

  public void setHighlightedStop(int position) {
    highlightedStopPosition = position;
  }

  class StopCardHolder extends RecyclerView.ViewHolder {
    private ViewGroup viewGroup;

    @BindView(R.id.stop_card_stop_name)
    TextView stopName;
    @BindView(R.id.stop_card_trains)
    LinearLayout recentTrains;

    @BindView(R.id.stop_card_zone)
    TextView stopZone;

    @OnClick(R.id.stop_card_link)
    void onClickLink() {
      Stop stop = stops.get(getAdapterPosition());
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse(stop.stop_url));
      itemView.getContext().startActivity(intent);
    }

    @OnClick(R.id.stop_card_map_to)
    void onClickMap() {
      Stop stop = stops.get(getAdapterPosition());
      Uri gmmIntentUri = Uri.parse("geo:" + stop.stop_lat + "," + stop.stop_lon + "?z=13&q=" + Uri.encode(stop.stop_name));
      Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
      mapIntent.setPackage("com.google.android.apps.maps");
      itemView.getContext().startActivity(mapIntent);
    }

    @OnClick(R.id.stop_card_leave_from)
    void onClickLeaveFrom() {
      Stop stop = stops.get(getAdapterPosition());
      chooChooFragmentManager.loadSearchForSpotFragment(stop, null, RxMessageStopsAndDetails.DETAIL_DEPARTING, new LocalDateTime());
    }

    @OnClick(R.id.stop_card_go_to)
    void onClickGoTo() {
      Stop stop = stops.get(getAdapterPosition());
      chooChooFragmentManager.loadSearchForSpotFragment(null, stop, RxMessageStopsAndDetails.DETAIL_ARRIVING, new LocalDateTime());
    }

    private StopCardHolder(View view, ViewGroup viewGroup) {
      super(view);
      this.viewGroup = viewGroup;
      ButterKnife.bind(this, view);
    }

  }
}