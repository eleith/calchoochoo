package com.eleith.calchoochoo.dagger;

import com.eleith.calchoochoo.StopActivity;
import com.eleith.calchoochoo.StopSearchActivity;
import com.eleith.calchoochoo.TripActivity;
import com.eleith.calchoochoo.TripFilterActivity;
import com.eleith.calchoochoo.fragments.SetAlarmDialogFragment;
import com.eleith.calchoochoo.fragments.TripFilterSuggestionsFragment;
import com.eleith.calchoochoo.fragments.TripFilterTimeAndMethodDialogFragment;
import com.eleith.calchoochoo.MapSearchActivity;
import com.eleith.calchoochoo.fragments.StopDetailsFragment;
import com.eleith.calchoochoo.fragments.TripFilterFragment;
import com.eleith.calchoochoo.fragments.MapSearchFragment;
import com.eleith.calchoochoo.fragments.SearchInputFragment;
import com.eleith.calchoochoo.fragments.SearchResultsFragment;
import com.eleith.calchoochoo.fragments.StopSummaryFragment;
import com.eleith.calchoochoo.fragments.TripDetailFragment;
import com.eleith.calchoochoo.fragments.TripSummaryFragment;

import dagger.Subcomponent;

@ChooChooScope
@Subcomponent(modules = ChooChooModule.class)
public interface ChooChooComponent {
  // injection for activity
  void inject(MapSearchActivity mapSearchActivity);
  void inject(TripFilterActivity tripFilterActivity);
  void inject(TripActivity tripActivity);
  void inject(StopSearchActivity stopSearchActivity);
  void inject(StopActivity stopActivity);

  // injection for fragments
  void inject(SearchResultsFragment searchResultsFragment);
  void inject(TripFilterSuggestionsFragment tripFilterSuggestionsFragment);
  void inject(SearchInputFragment searchInputFragment);
  void inject(TripFilterFragment tripFilterFragment);
  void inject(TripFilterTimeAndMethodDialogFragment tripFilterTimeAndMethodDialogFragment);
  void inject(StopSummaryFragment stopSummaryFragment);
  void inject(TripSummaryFragment tripSummaryFragment);
  void inject(TripDetailFragment tripDetailFragment);
  void inject(MapSearchFragment mapSearchFragment);
  void inject(StopDetailsFragment stopDetailsFragment);
  void inject(SetAlarmDialogFragment setAlarmDialogFragment);
}
