package com.eleith.calchoochoo.dagger;

import android.support.v4.app.FragmentManager;

import com.eleith.calchoochoo.adapters.RouteViewAdapter;
import com.eleith.calchoochoo.ScheduleExplorerActivity;
import com.eleith.calchoochoo.adapters.SearchResultsViewAdapter;
import com.eleith.calchoochoo.adapters.StopCardAdapter;
import com.eleith.calchoochoo.adapters.TripStopsAdapter;
import com.eleith.calchoochoo.utils.RxBus;

import dagger.Module;
import dagger.Provides;

@Module
public class ScheduleExplorerActivityModule {
  private ScheduleExplorerActivity scheduleExplorerActivity;

  public ScheduleExplorerActivityModule(ScheduleExplorerActivity scheduleExplorerActivity) {
    this.scheduleExplorerActivity = scheduleExplorerActivity;
  }

  @ScheduleExplorerActivityScope
  @Provides
  public SearchResultsViewAdapter provideResultsViewAdapter(RxBus rxBus) {
    return new SearchResultsViewAdapter(rxBus);
  }

  @ScheduleExplorerActivityScope
  @Provides
  public RouteViewAdapter provideRouteViewViewAdapter(RxBus rxBus) {
    return new RouteViewAdapter(rxBus);
  }

  @ScheduleExplorerActivityScope
  @Provides
  public TripStopsAdapter provideTripStopsAdapter(RxBus rxBus) {
    return new TripStopsAdapter(rxBus);
  }

  @ScheduleExplorerActivityScope
  @Provides
  public StopCardAdapter provideStopCardAdapter(RxBus rxBus) {
    return new StopCardAdapter(rxBus);
  }

  @ScheduleExplorerActivityScope
  @Provides
  public FragmentManager provideFragmentManager() {
    return scheduleExplorerActivity.getSupportFragmentManager();
  }
}
