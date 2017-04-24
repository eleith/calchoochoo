package com.eleith.calchoochoo.dagger;

import android.content.Context;

import com.eleith.calchoochoo.ChooChooApplication;
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
}
