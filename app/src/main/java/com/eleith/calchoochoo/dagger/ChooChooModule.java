package com.eleith.calchoochoo.dagger;

import com.eleith.calchoochoo.ChooChooActivity;
import com.eleith.calchoochoo.ChooChooFragmentManager;
import com.eleith.calchoochoo.data.ChooChooLoader;
import com.eleith.calchoochoo.utils.DeviceLocation;
import com.eleith.calchoochoo.utils.RxBus;
import com.google.android.gms.common.api.GoogleApiClient;

import dagger.Module;
import dagger.Provides;

@Module
public class ChooChooModule {
  private ChooChooActivity chooChooActivity;

  public ChooChooModule(ChooChooActivity chooChooActivity) {
    this.chooChooActivity = chooChooActivity;
  }

  @ChooChooScope
  @Provides
  public ChooChooActivity providesActivity() {
    return chooChooActivity;
  }

  @ChooChooScope
  @Provides
  public ChooChooFragmentManager providesChooChooFragmentManager() {
    return new ChooChooFragmentManager(chooChooActivity.getSupportFragmentManager());
  }

  @ChooChooScope
  @Provides
  public ChooChooLoader providesChooChooLoader(RxBus rxBus) {
    return new ChooChooLoader(chooChooActivity, rxBus);
  }

  @ChooChooScope
  @Provides
  DeviceLocation providesDeviceLocation(RxBus rxBus, GoogleApiClient googleApiClient) {
    return new DeviceLocation(rxBus, googleApiClient, chooChooActivity);
  }
}
