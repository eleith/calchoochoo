package com.eleith.calchoochoo.dagger;

import android.support.v4.app.FragmentManager;

import com.eleith.calchoochoo.ChooChooActivity;
import com.eleith.calchoochoo.ChooChooFragmentManager;
import com.eleith.calchoochoo.utils.DeviceLocation;
import com.eleith.calchoochoo.utils.RxBus;
import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Singleton;

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
  DeviceLocation providesDeviceLocation(RxBus rxBus, GoogleApiClient googleApiClient) {
    return new DeviceLocation(rxBus, googleApiClient, chooChooActivity);
  }
}
