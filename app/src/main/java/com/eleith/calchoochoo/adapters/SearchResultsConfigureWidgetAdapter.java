package com.eleith.calchoochoo.adapters;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleith.calchoochoo.ChooChooWidgetConfigure;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.dagger.ChooChooWidgetConfigureScope;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.DistanceUtils;
import com.eleith.calchoochoo.utils.RxBus;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ChooChooWidgetConfigureScope
public class SearchResultsConfigureWidgetAdapter extends RecyclerView.Adapter<SearchResultsConfigureWidgetAdapter.ViewHolder> {

  private ArrayList<Stop> stops = new ArrayList<Stop>();
  private Location location;
  private ChooChooWidgetConfigure activity;

  @Inject
  public SearchResultsConfigureWidgetAdapter(ChooChooWidgetConfigure activity) {
    this.activity = activity;
  }

  public void setStops(ArrayList<Stop> stops) {
    this.stops = stops;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_search_result, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    Stop stop = stops.get(position);

    if (location != null) {
      Double distance = location.distanceTo(stop.getLocation()) / 1.0;
      holder.mContentView.setText(String.format(Locale.getDefault(), "%.2f", DistanceUtils.meterToMiles(distance)));
      holder.mContentView.setVisibility(View.VISIBLE);
    } else {
      holder.mContentView.setVisibility(View.INVISIBLE);
    }

    holder.mItem = stop;
    holder.mIdView.setText(stop.stop_name);
  }

  @Override
  public int getItemCount() {
    return stops.size();
  }

  public void setLocation(Location location) {
    this.location = location;
    this.notifyDataSetChanged();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.search_result_description)
    TextView mContentView;
    @BindView(R.id.search_result_name)
    TextView mIdView;
    Stop mItem;

    @OnClick(R.id.search_result_item)
    void onClickTripSummary() {
      activity.chooseStopToConfigure(mItem);
    }

    ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + mContentView.getText() + "'";
    }
  }
}
