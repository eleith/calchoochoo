package com.eleith.calchoochoo.dagger;

import android.support.v4.app.FragmentManager;

import com.eleith.calchoochoo.ChooChooActivity;
import com.eleith.calchoochoo.utils.DeviceLocation;
import com.eleith.calchoochoo.utils.RxBus;
import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ScheduleExplorerActivityModule {
  private ChooChooActivity chooChooActivity;

  public ScheduleExplorerActivityModule(ChooChooActivity chooChooActivity) {
    this.chooChooActivity = chooChooActivity;
  }

  @ScheduleExplorerActivityScope
  @Provides
  public ChooChooActivity providesActivity() {
    return chooChooActivity;
  }

  @ScheduleExplorerActivityScope
  @Provides
  public FragmentManager provideFragmentManager() {
    return chooChooActivity.getSupportFragmentManager();
  }

  @ScheduleExplorerActivityScope
  @Provides
  DeviceLocation providesDeviceLocation(RxBus rxBus, GoogleApiClient googleApiClient) {
    return new DeviceLocation(rxBus, googleApiClient, chooChooActivity);
  }
}
