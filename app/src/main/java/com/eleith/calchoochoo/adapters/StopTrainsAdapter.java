package com.eleith.calchoochoo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eleith.calchoochoo.ChooChooRouterManager;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.StopActivity;
import com.eleith.calchoochoo.dagger.ChooChooScope;
import com.eleith.calchoochoo.data.PossibleTrain;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStopsAndDetails;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ChooChooScope
public class StopTrainsAdapter extends RecyclerView.Adapter<StopTrainsAdapter.RouteViewHolder> {
  private ArrayList<PossibleTrain> possibleTrains = new ArrayList<>();
  private StopActivity activity;
  private ChooChooRouterManager chooChooRouterManager;
  private Integer southSelected = null;
  private Integer northSelected = null;

  @Inject
  public StopTrainsAdapter(StopActivity activity, ChooChooRouterManager chooChooRouterManager) {
    this.activity = activity;
    this.chooChooRouterManager = chooChooRouterManager;
  }

  public void setPossibleTrains(ArrayList<PossibleTrain> possibleTrains) {
    this.possibleTrains = possibleTrains;
  }

  public void setNorthSelected(Integer northSelected) {
    this.northSelected = northSelected;
  }

  public void setSouthSelected(Integer southSelected) {
    this.southSelected = southSelected;
  }

  @Override
  public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    if (viewType == 0) {
      view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_stop_card_widget_trainitem, parent, false);
    } else {
      view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_stop_card_widget_trainitem_highlighted, parent, false);
    }
    return new RouteViewHolder(view);
  }

  @Override
  public void onBindViewHolder(RouteViewHolder holder, int position) {
    PossibleTrain possibleTrain = possibleTrains.get(position);
    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("h:mma");

    holder.stopCardTrainItemNumber.setText(possibleTrain.getTripShortName());

    if (possibleTrain.getRouteLongName().contains("Bullet")) {
      holder.stopCardTrainItemImage.setImageDrawable(activity.getDrawable(R.drawable.ic_train_bullet));
    } else {
      holder.stopCardTrainItemImage.setImageDrawable(activity.getDrawable(R.drawable.ic_train_local));
    }

    holder.stopCardTrainItemTime.setText(dateTimeFormatter.print(possibleTrain.getDepartureTime()));
  }

  @Override
  public int getItemViewType(int position) {
    if (northSelected != null && northSelected == position) {
      return 1;
    } else if (southSelected != null && southSelected == position) {
      return 1;
    } else {
      return 0;
    }
  }

  @Override
  public int getItemCount() {
    return possibleTrains.size();
  }

  public class RouteViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.stop_card_widget_trainitem_number)
    TextView stopCardTrainItemNumber;
    @BindView(R.id.stop_card_widget_trainitem_image)
    ImageView stopCardTrainItemImage;
    @BindView(R.id.stop_card_widget_trainitem_time)
    TextView stopCardTrainItemTime;

    @OnClick(R.id.stop_card_widget_train_item)
    void onClickTripSummary() {
      PossibleTrain possibleTrain = possibleTrains.get(getAdapterPosition());
      chooChooRouterManager.loadTripFilterActivity(activity, possibleTrain.getStopId(), null, RxMessageStopsAndDetails.DETAIL_DEPARTING, possibleTrain.getArrivalTime().toDateTimeToday().getMillis());
    }

    private RouteViewHolder(View v) {
      super(v);
      ButterKnife.bind(this, v);
    }
  }
}