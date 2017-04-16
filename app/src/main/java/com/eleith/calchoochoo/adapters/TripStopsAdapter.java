package com.eleith.calchoochoo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleith.calchoochoo.ChooChooRouterManager;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.TripActivity;
import com.eleith.calchoochoo.dagger.ChooChooScope;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.StopTimes;
import com.eleith.calchoochoo.utils.DataStringUtils;
import com.eleith.calchoochoo.utils.RxBus;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

@ChooChooScope
public class TripStopsAdapter extends RecyclerView.Adapter<TripStopsAdapter.OneTripStopHolder> {
  private ArrayList<StopTimes> tripStops = new ArrayList<>();
  private ChooChooRouterManager chooChooRouterManager;
  private TripActivity tripActivity;
  private final static int ITEM_TYPE_SOURCE = 0;
  private final static int ITEM_TYPE_DESTINATION = 1;
  private final static int ITEM_TYPE_MIDDLE = 2;

  @Inject
  public TripStopsAdapter(ChooChooRouterManager chooChooRouterManager, TripActivity tripActivity) {
    this.chooChooRouterManager = chooChooRouterManager;
    this.tripActivity = tripActivity;
  }

  public void setTripStops(ArrayList<StopTimes> tripStops) {
    this.tripStops = tripStops;
  }

  @Override
  public OneTripStopHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    // return different view depending on viewType
    View view;

    switch (viewType) {
      case ITEM_TYPE_DESTINATION:
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_trip_stop_destination, parent, false);
        break;
      case ITEM_TYPE_SOURCE:
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_trip_stop_source, parent, false);
        break;
      default:
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_trip_stop_middle, parent, false);
        break;
    }
    return new OneTripStopHolder(view);
  }

  @Override
  public void onBindViewHolder(OneTripStopHolder holder, int position) {
    StopTimes pair = tripStops.get(position);
    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("h:mma");

    holder.name.setText(DataStringUtils.removeCaltrain(pair.stop.stop_name));
    holder.startTime.setText(dateTimeFormatter.print(pair.arrival_time));
  }

  @Override
  public int getItemCount() {
    return tripStops.size();
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return ITEM_TYPE_SOURCE;
    } else if (position == getItemCount() - 1) {
      return ITEM_TYPE_DESTINATION;
    } else {
      return ITEM_TYPE_MIDDLE;
    }
  }

  class OneTripStopHolder extends RecyclerView.ViewHolder {
    TextView name;
    TextView startTime;

    private OneTripStopHolder(View v) {
      super(v);

      ButterKnife.bind(this, v);
      name = (TextView) v.findViewById(R.id.one_trip_stop_name);
      startTime = (TextView) v.findViewById(R.id.one_trip_stop_start_time);
    }
  }
}