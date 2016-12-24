package com.eleith.calchoochoo.dagger;

import android.location.LocationManager;

import com.eleith.calchoochoo.utils.RxBus;

import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
  // instantiate subcomponents
  ScheduleExplorerActivityComponent activityComponent(ScheduleExplorerActivityModule scheduleExplorerActivityModule);
}
