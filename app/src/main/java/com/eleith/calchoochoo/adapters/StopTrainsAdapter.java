package com.eleith.calchoochoo.adapters;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eleith.calchoochoo.ChooChooRouterManager;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.StopActivity;
import com.eleith.calchoochoo.dagger.ChooChooScope;
import com.eleith.calchoochoo.data.PossibleTrain;
import com.eleith.calchoochoo.utils.ColorUtils;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ChooChooScope
public class StopTrainsAdapter extends RecyclerView.Adapter<StopTrainsAdapter.RouteViewHolder> {
  private ArrayList<PossibleTrain> possibleTrains = new ArrayList<>();
  private AppCompatActivity activity;
  private ChooChooRouterManager chooChooRouterManager;
  private Integer selected = null;
  private DateTime now = new DateTime();

  @Inject
  public StopTrainsAdapter(AppCompatActivity activity, ChooChooRouterManager chooChooRouterManager) {
    this.activity = activity;
    this.chooChooRouterManager = chooChooRouterManager;
  }

  public void setPossibleTrains(ArrayList<PossibleTrain> possibleTrains) {
    this.now = new DateTime();
    this.possibleTrains = possibleTrains;
  }

  public void setSelected(Integer selected) {
    this.selected = selected;
  }

  @Override
  public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_stop_card_widget_trainitem, parent, false);
    return new RouteViewHolder(view);
  }

  @Override
  public void onBindViewHolder(RouteViewHolder holder, int position) {
    PossibleTrain possibleTrain = possibleTrains.get(position);
    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("h:mma");
    Integer minutes = Minutes.minutesBetween(now, possibleTrain.getDepartureTime().toDateTimeToday()).getMinutes();

    holder.stopCardTrainItemNumber.setText(possibleTrain.getTripShortName());
    holder.stopCardTrainItemTime.setTypeface(null, Typeface.NORMAL);
    holder.stopCardTrainItemBack.setBackgroundColor(ContextCompat.getColor(activity, R.color.cardview_light_background));

    if (possibleTrain.getRouteLongName().contains("Bullet")) {
      holder.stopCardTrainItemImage.setImageDrawable(activity.getDrawable(R.drawable.ic_train_bullet));
    } else {
      holder.stopCardTrainItemImage.setImageDrawable(activity.getDrawable(R.drawable.ic_train_local));
    }

    if (minutes >= 0 && minutes <= 60) {
      holder.stopCardTrainItemTime.setText(String.format(Locale.getDefault(), "in %d min", minutes));
      holder.stopCardTrainItemTime.setTypeface(null, Typeface.ITALIC);
      holder.stopCardTrainItemBack.setBackgroundColor(ContextCompat.getColor(activity, R.color.cardview_light_background));
    } else {
      holder.stopCardTrainItemTime.setText(dateTimeFormatter.print(possibleTrain.getDepartureTime()));
    }
  }

  @Override
  public int getItemViewType(int position) {
    if (selected != null && selected == position) {
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
    @BindView(R.id.stop_card_widget_train_item_back)
    LinearLayout stopCardTrainItemBack;

    private Integer minutes;

    @OnClick(R.id.stop_card_widget_train_item)
    void onClickTripSummary() {
      PossibleTrain possibleTrain = possibleTrains.get(getAdapterPosition());
      chooChooRouterManager.loadTripActivity(activity, possibleTrain.getTripId(), possibleTrain.getStopId());
    }

    private RouteViewHolder(View v) {
      super(v);
      ButterKnife.bind(this, v);
    }
  }
}