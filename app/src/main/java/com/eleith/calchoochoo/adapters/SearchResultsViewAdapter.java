package com.eleith.calchoochoo.adapters;

import android.graphics.Typeface;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.dagger.ChooChooScope;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.DataStringUtils;
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
  private ArrayList<Stop> stops;
  private ArrayList<String> filteredStopIds;
  private Location location;
  private RxBus rxBus;
  private static final int ITEM_TYPE_AVAILABLE = 1;
  private static final int ITEM_TYPE_FILTERED = 0;

  @Inject
  public SearchResultsViewAdapter(RxBus rxBus) {
    this.rxBus = rxBus;
  }

  public void setStops(ArrayList<Stop> stops, ArrayList<String> filteredStopIds) {
    this.stops = stops;
    this.filteredStopIds = filteredStopIds;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_search_result, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    Stop stop = stops.get(position);
    int type = holder.getItemViewType();

    if (location != null) {
      Double distance = location.distanceTo(stop.getLocation()) / 1.0;
      holder.searchDistanceText.setText(String.format(Locale.getDefault(), "%.2f", DistanceUtils.meterToMiles(distance)));
      holder.searchDistanceText.setVisibility(View.VISIBLE);
    } else {
      holder.searchDistanceText.setVisibility(View.INVISIBLE);
    }

    holder.stopNameText.setText(DataStringUtils.removeCaltrain(stop.stop_name));

    if (type == ITEM_TYPE_FILTERED) {
      holder.stopNameText.setTypeface(null, Typeface.ITALIC);
    }
  }

  @Override
  public int getItemViewType(int position) {
    Stop stop = stops.get(position);
    if (filteredStopIds.indexOf(stop.stop_id) != -1) {
      return ITEM_TYPE_FILTERED;
    } else {
      return ITEM_TYPE_AVAILABLE;
    }
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
    TextView searchDistanceText;
    @BindView(R.id.search_result_name)
    TextView stopNameText;

    @OnClick(R.id.search_result_item)
    void onClickResult() {
      rxBus.send(new RxMessageStop(RxMessageKeys.SEARCH_RESULT_STOP, stops.get(getAdapterPosition())));
    }

    ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + searchDistanceText.getText() + "'";
    }
  }
}
