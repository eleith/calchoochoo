package com.eleith.calchoochoo.dagger;

import android.support.v4.app.FragmentManager;

import com.eleith.calchoochoo.ChooChooWidgetConfigure;
import com.eleith.calchoochoo.utils.DeviceLocation;
import com.eleith.calchoochoo.utils.RxBus;
import com.google.android.gms.common.api.GoogleApiClient;

import dagger.Module;
import dagger.Provides;

@Module
public class ChooChooWidgetConfigureModule {
  private ChooChooWidgetConfigure chooChooWidgetConfigure;

  public ChooChooWidgetConfigureModule(ChooChooWidgetConfigure chooChooWidgetConfigure) {
    this.chooChooWidgetConfigure = chooChooWidgetConfigure;
  }

  @ChooChooWidgetConfigureScope
  @Provides
  public ChooChooWidgetConfigure providesActivity() {
    return chooChooWidgetConfigure;
  }

  @ChooChooWidgetConfigureScope
  @Provides
  public FragmentManager provideFragmentManager() {
    return chooChooWidgetConfigure.getSupportFragmentManager();
  }

  @ChooChooWidgetConfigureScope
  @Provides
  DeviceLocation providesDeviceLocation(RxBus rxBus, GoogleApiClient googleApiClient) {
    return new DeviceLocation(rxBus, googleApiClient, chooChooWidgetConfigure);
  }
}
