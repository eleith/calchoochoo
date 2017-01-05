package com.eleith.calchoochoo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleith.calchoochoo.utils.RxBus;

import java.util.ArrayList;

public class RouteViewAdapter extends RecyclerView.Adapter<RouteViewAdapter.RouteViewHolder> {
  //public RouteViewAdapter(ArrayList<String> trainNames) {
  //  this.trainNames = trainNames;
  //}

  public RouteViewAdapter(RxBus rxBus) {

  }

  @Override
  public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_route_stops_outer, parent, false);
    return new MainTrainViewHolder(view);
    //view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_route_stops_inner, parent, false);
    //return new InnerTrainViewHolder(view);
  }

  @Override
  public void onBindViewHolder(RouteViewHolder holder, int position) {
  }

  @Override
  public int getItemViewType(int position) {
    return 0;
  }

  @Override
  public int getItemCount() {
    return 0;
  }

  public class RouteViewHolder extends RecyclerView.ViewHolder {
    private RouteViewHolder(View v) {
      super(v);
    }
  }

  private class MainTrainViewHolder extends RouteViewHolder {
    TextView nameTextView;
    TextView timeTextView;

    private MainTrainViewHolder(View v) {
      super(v);
      nameTextView = (TextView) v.findViewById(R.id.main_destination_name_text_view);
      timeTextView = (TextView) v.findViewById(R.id.main_destination_time_text_view);
    }
  }

  private class InnerTrainViewHolder extends RouteViewHolder {
    TextView nameTextView;

    private InnerTrainViewHolder(View v) {
      super(v);
      nameTextView = (TextView) v.findViewById(R.id.inner_destination_text_view);
    }
  }
}