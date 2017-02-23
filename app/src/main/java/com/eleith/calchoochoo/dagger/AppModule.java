package com.eleith.calchoochoo.dagger;

import android.content.Context;

import com.eleith.calchoochoo.ChooChooApplication;
import com.eleith.calchoochoo.utils.DeviceLocation;
import com.eleith.calchoochoo.utils.RxBus;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
  private ChooChooApplication application;

  public AppModule(ChooChooApplication application) {
    this.application = application;
  }

  @Provides
  @Singleton
  public RxBus provideRxBus() {
    return new RxBus();
  }

  @Provides
  @Singleton
  public Context provideApplicationContext() {
    return application;
  }

  @Provides
  @Singleton
  GoogleApiClient providesGoogleApiClient(Context context) {
    return new GoogleApiClient.Builder(context)
        .addApi(LocationServices.API)
        .build();
  }

  @Provides
  @Singleton
  DeviceLocation providesDeviceLocation(RxBus rxBus, GoogleApiClient googleApiClient) {
    return new DeviceLocation(rxBus, googleApiClient);
  }
}
