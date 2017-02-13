package com.eleith.calchoochoo.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.data.Queries;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessagePairStopReason;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StopCardAdapter extends RecyclerView.Adapter<StopCardAdapter.StopCardHolder> {
  private ArrayList<Stop> stops;
  private RxBus rxBus;

  public StopCardAdapter(RxBus rxBus) {
    stops = Queries.getAllStops();
    this.rxBus = rxBus;
  }

  @Override
  public StopCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_stop_card, parent, false);
    return new StopCardHolder(view);
  }

  @Override
  public void onBindViewHolder(StopCardHolder holder, int position) {
    Stop stop = stops.get(position);
    Integer zone = Queries.getZoneOfParentStop(stop.stop_id);
    holder.stopName.setText(stop.stop_name);

    if (zone != null) {
      holder.stopZone.setText(String.format(Locale.getDefault(), "%d", zone));
    }
  }

  @Override
  public int getItemCount() {
    return stops.size();
  }

  class StopCardHolder extends RecyclerView.ViewHolder {
    private Stop stop;

    @BindView(R.id.stop_card_stop_name)
    TextView stopName;

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
      Pair<Stop, Integer> pair = new Pair<>(stop, RxMessagePairStopReason.SEARCH_REASON_SOURCE);
      rxBus.send(new RxMessagePairStopReason(RxMessageKeys.SEARCH_RESULT_PAIR, pair));
    }

    @OnClick(R.id.stop_card_go_to)
    void onClickGoTo() {
      Stop stop = stops.get(getAdapterPosition());
      Pair<Stop, Integer> pair = new Pair<>(stop, RxMessagePairStopReason.SEARCH_REASON_DESTINATION);
      rxBus.send(new RxMessagePairStopReason(RxMessageKeys.SEARCH_RESULT_PAIR, pair));
    }

    private StopCardHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }

  }
}