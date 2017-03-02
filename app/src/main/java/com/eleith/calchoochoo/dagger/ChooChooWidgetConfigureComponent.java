package com.eleith.calchoochoo.dagger;

import com.eleith.calchoochoo.ChooChooWidgetConfigure;
import com.eleith.calchoochoo.adapters.SearchResultsConfigureWidgetAdapter;
import com.eleith.calchoochoo.fragments.SearchInputConfigureWidgetFragment;
import com.eleith.calchoochoo.fragments.SearchInputFragment;
import com.eleith.calchoochoo.fragments.SearchResultsConfigureWidgetFragment;
import com.eleith.calchoochoo.fragments.SearchResultsFragment;

import dagger.Subcomponent;

@ChooChooWidgetConfigureScope
@Subcomponent(modules = ChooChooWidgetConfigureModule.class)
public interface ChooChooWidgetConfigureComponent {
  // injection for activity
  void inject(ChooChooWidgetConfigure chooChooWidgetConfigure);

  // injection for fragments
  void inject(SearchInputConfigureWidgetFragment searchInputConfigureWidgetFragment);
  void inject(SearchResultsConfigureWidgetFragment searchResultsConfigureWidgetFragment);
}
