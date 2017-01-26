package com.eleith.calchoochoo.adapters;

import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.DistanceUtils;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStop;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

public class SearchResultsViewAdapter extends RecyclerView.Adapter<SearchResultsViewAdapter.ViewHolder> {

  private ArrayList<Stop> stops = new ArrayList<Stop>();
  private Location location;
  private RxBus rxBus;

  public SearchResultsViewAdapter(RxBus rxBus) {
    this.rxBus = rxBus;
  }

  public void setStops(ArrayList<Stop> stops) {
    this.stops = stops;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_search_result, parent, false);
    final ViewHolder holder = new ViewHolder(view);

    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        rxBus.send(new RxMessageStop(RxMessageKeys.SEARCH_RESULT_STOP, holder.mItem));
      }
    });

    return holder;
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    Stop stop = stops.get(position);

    if (location != null) {
      Double distance = location.distanceTo(stop.getLocation()) / 1.0;
      holder.mContentView.setText(String.format(Locale.getDefault(), "%.2f mi", DistanceUtils.meterToMiles(distance)));
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

  public void filterByFuzzySearch(ArrayList<Stop> stops, String query) {
    if (query != null && !query.equals("")) {
      ArrayList<Stop> filteredStops =  new ArrayList<Stop>();
      final HashMap<String, Integer> stopFuzzyScores = new HashMap<String, Integer>();
      for (Stop stop : stops) {
        int fuzzyScore = StringUtils.getFuzzyDistance(stop.stop_name, query, Locale.getDefault());
        if (fuzzyScore >= query.length()) {
          stopFuzzyScores.put(stop.stop_id, fuzzyScore);
          filteredStops.add(stop);
        }
      }
      this.stops = filteredStops;
      Collections.sort(this.stops, new Comparator<Stop>() {
            @Override
            public int compare(Stop lhs, Stop rhs) {
              int rightFuzzyScore = stopFuzzyScores.get(rhs.stop_id);
              int leftFuzzyScore = stopFuzzyScores.get(lhs.stop_id);
              return Integer.compare(rightFuzzyScore, leftFuzzyScore);
            }
      });
    } else {
      this.stops = stops;
      Collections.sort(this.stops, Stop.nameComparator);
    }

    this.notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final TextView mIdView;
    public final TextView mContentView;
    public Stop mItem;

    public ViewHolder(View view) {
      super(view);
      mView = view;
      mIdView = (TextView) view.findViewById(R.id.name);
      mContentView = (TextView) view.findViewById(R.id.description);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + mContentView.getText() + "'";
    }
  }
}
