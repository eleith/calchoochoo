package com.eleith.calchoochoo.dagger;

import com.eleith.calchoochoo.ChooChooRouterManager;
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
  public ChooChooRouterManager providesChooChooFragmentManager() {
    return new ChooChooRouterManager(chooChooWidgetConfigure.getSupportFragmentManager());
  }

  @ChooChooWidgetConfigureScope
  @Provides
  DeviceLocation providesDeviceLocation(RxBus rxBus) {
    return new DeviceLocation(rxBus, chooChooWidgetConfigure);
  }
}
