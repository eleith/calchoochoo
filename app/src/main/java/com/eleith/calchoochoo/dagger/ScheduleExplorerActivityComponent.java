package com.eleith.calchoochoo.dagger;

import com.eleith.calchoochoo.fragments.DepartingArrivingDialogFragment;
import com.eleith.calchoochoo.ScheduleExplorerActivity;
import com.eleith.calchoochoo.fragments.TripFilterFragment;
import com.eleith.calchoochoo.fragments.MapSearchFragment;
import com.eleith.calchoochoo.fragments.RouteStopsFragment;
import com.eleith.calchoochoo.fragments.SearchInputFragment;
import com.eleith.calchoochoo.fragments.SearchResultsFragment;
import com.eleith.calchoochoo.fragments.StopDetailsFragment;
import com.eleith.calchoochoo.fragments.StopSummaryFragment;
import com.eleith.calchoochoo.fragments.TripDetailFragment;
import com.eleith.calchoochoo.fragments.TripSummaryFragment;

import dagger.Subcomponent;

@ScheduleExplorerActivityScope
@Subcomponent(modules = ScheduleExplorerActivityModule.class)
public interface ScheduleExplorerActivityComponent {
  // injection for activity
  void inject(ScheduleExplorerActivity scheduleExplorerActivity);

  // injection for fragments
  void inject(SearchResultsFragment searchResultsFragment);
  void inject(RouteStopsFragment routeStopsFragment);
  void inject(SearchInputFragment searchInputFragment);
  void inject(TripFilterFragment tripFilterFragment);
  void inject(DepartingArrivingDialogFragment departingArrivingDialogFragment);
  void inject(StopSummaryFragment stopSummaryFragment);
  void inject(StopDetailsFragment stopDetailsFragment);
  void inject(TripSummaryFragment tripSummaryFragment);
  void inject(TripDetailFragment tripDetailFragment);
  void inject(MapSearchFragment mapSearchFragment);
}
