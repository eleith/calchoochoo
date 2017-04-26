package com.eleith.calchoochoo.dagger;

import android.support.v7.app.AppCompatActivity;

import com.eleith.calchoochoo.ChooChooRouterManager;
import com.eleith.calchoochoo.MapSearchActivity;
import com.eleith.calchoochoo.StopActivity;
import com.eleith.calchoochoo.StopSearchActivity;
import com.eleith.calchoochoo.TripActivity;
import com.eleith.calchoochoo.TripFilterActivity;
import com.eleith.calchoochoo.adapters.StopTrainsAdapter;
import com.eleith.calchoochoo.data.ChooChooLoader;
import com.eleith.calchoochoo.utils.DeviceLocation;
import com.eleith.calchoochoo.utils.RxBus;
import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ChooChooModule {
  private AppCompatActivity activity;

  public ChooChooModule(AppCompatActivity activity) {
    this.activity = activity;
  }

  @ChooChooScope
  @Provides
  public MapSearchActivity providesMapSearchActivity() {
    return (MapSearchActivity) activity;
  }

  @ChooChooScope
  @Provides
  public TripActivity providesTripActivity() {
    return (TripActivity) activity;
  }

  @ChooChooScope
  @Provides
  public TripFilterActivity providesTripFilterActivity() {
    return (TripFilterActivity) activity;
  }

  @ChooChooScope
  @Provides
  public StopSearchActivity providesStopSearchActivity() {
    return (StopSearchActivity) activity;
  }

  @ChooChooScope
  @Provides
  public StopActivity providesStopActivity() {
    return (StopActivity) activity;
  }

  @ChooChooScope
  @Provides
  public ChooChooRouterManager providesChooChooFragmentManager() {
    return new ChooChooRouterManager(activity.getSupportFragmentManager());
  }

  @ChooChooScope
  @Provides
  public ChooChooLoader providesChooChooLoader(RxBus rxBus) {
    return new ChooChooLoader(activity, rxBus);
  }

  @ChooChooScope
  @Provides
  DeviceLocation providesDeviceLocation(RxBus rxBus) {
    return new DeviceLocation(rxBus, activity);
  }

  @ChooChooScope
  @Provides
  @Named("north")
  StopTrainsAdapter providesStopTrainsAdapter1(ChooChooRouterManager chooChooRouterManager) {
    return new StopTrainsAdapter(activity, chooChooRouterManager);
  }

  @ChooChooScope
  @Provides
  @Named("south")
  StopTrainsAdapter providesStopTrainsAdapter2(ChooChooRouterManager chooChooRouterManager) {
    return new StopTrainsAdapter(activity, chooChooRouterManager);
  }
}
