package com.eleith.calchoochoo.dagger;

import android.support.v4.app.FragmentManager;

import com.eleith.calchoochoo.ScheduleExplorerActivity;
import com.eleith.calchoochoo.SearchResultsViewAdapter;
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
  public FragmentManager provideFragmentManager() {
    return scheduleExplorerActivity.getSupportFragmentManager();
  }
}
