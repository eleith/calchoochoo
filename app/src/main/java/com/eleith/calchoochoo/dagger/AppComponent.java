package com.eleith.calchoochoo.dagger;

import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
  // instantiate subcomponents
  ChooChooComponent activityComponent(ChooChooModule chooChooModule);
  ChooChooWidgetConfigureComponent activityComponent(ChooChooWidgetConfigureModule chooChooWidgetConfigureModule);
}
