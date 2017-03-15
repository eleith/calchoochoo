package com.eleith.calchoochoo.adapters;

import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleith.calchoochoo.ChooChooFragmentManager;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.dagger.ChooChooScope;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.DistanceUtils;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStop;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ChooChooScope
public class SearchResultsViewAdapter extends RecyclerView.Adapter<SearchResultsViewAdapter.ViewHolder> {

  private ArrayList<Stop> stops = new ArrayList<Stop>();
  private Location location;
  private RxBus rxBus;
  private ChooChooFragmentManager chooChooFragmentManager;

  @Inject
  public SearchResultsViewAdapter(RxBus rxBus, ChooChooFragmentManager chooChooFragmentManager) {
    this.rxBus = rxBus;
    this.chooChooFragmentManager = chooChooFragmentManager;
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
    holder.mIdView.setText(stop.stop_name.replace(" Caltrain", ""));
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
    @BindView(R.id.search_result_distance)
    TextView mContentView;
    @BindView(R.id.search_result_name)
    TextView mIdView;

    Stop mItem;

    @OnClick(R.id.search_result_item)
    void onClickTripSummary() {
      rxBus.send(new RxMessageStop(RxMessageKeys.SEARCH_RESULT_STOP, mItem));
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
