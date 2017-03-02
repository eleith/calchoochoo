package com.eleith.calchoochoo.dagger;

import com.eleith.calchoochoo.fragments.TripFilterTimeAndMethodDialogFragment;
import com.eleith.calchoochoo.ChooChooActivity;
import com.eleith.calchoochoo.fragments.StopCardsFragment;
import com.eleith.calchoochoo.fragments.TripFilterFragment;
import com.eleith.calchoochoo.fragments.MapSearchFragment;
import com.eleith.calchoochoo.fragments.RouteStopsFragment;
import com.eleith.calchoochoo.fragments.SearchInputFragment;
import com.eleith.calchoochoo.fragments.SearchResultsFragment;
import com.eleith.calchoochoo.fragments.StopDetailsFragment;
import com.eleith.calchoochoo.fragments.TripDetailFragment;
import com.eleith.calchoochoo.fragments.TripSummaryFragment;

import dagger.Subcomponent;

@ChooChooScope
@Subcomponent(modules = ChooChooModule.class)
public interface ChooChooComponent {
  // injection for activity
  void inject(ChooChooActivity chooChooActivity);

  // injection for fragments
  void inject(SearchResultsFragment searchResultsFragment);
  void inject(RouteStopsFragment routeStopsFragment);
  void inject(SearchInputFragment searchInputFragment);
  void inject(TripFilterFragment tripFilterFragment);
  void inject(TripFilterTimeAndMethodDialogFragment tripFilterTimeAndMethodDialogFragment);
  void inject(StopDetailsFragment stopDetailsFragment);
  void inject(TripSummaryFragment tripSummaryFragment);
  void inject(TripDetailFragment tripDetailFragment);
  void inject(MapSearchFragment mapSearchFragment);
  void inject(StopCardsFragment stopCardsFragment);
}
