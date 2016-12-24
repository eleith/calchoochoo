package com.eleith.calchoochoo.dagger;

import com.eleith.calchoochoo.fragments.DepartingArrivingDialogFragment;
import com.eleith.calchoochoo.ScheduleExplorerActivity;
import com.eleith.calchoochoo.fragments.DestinationSourceFragment;
import com.eleith.calchoochoo.fragments.SearchInputFragment;
import com.eleith.calchoochoo.fragments.SearchResultsFragment;

import dagger.Subcomponent;

@ScheduleExplorerActivityScope
@Subcomponent(modules = ScheduleExplorerActivityModule.class)
public interface ScheduleExplorerActivityComponent {
  // injection for activity
  void inject(ScheduleExplorerActivity scheduleExplorerActivity);

  // injection for fragments
  void inject(SearchResultsFragment searchResultsFragment);
  void inject(SearchInputFragment searchInputFragment);
  void inject(DestinationSourceFragment destinationSourceFragment);
  void inject(DepartingArrivingDialogFragment departingArrivingDialogFragment);
}
