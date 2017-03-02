package com.eleith.calchoochoo;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.eleith.calchoochoo.dagger.AppComponent;
import com.eleith.calchoochoo.dagger.AppModule;
import com.eleith.calchoochoo.dagger.DaggerAppComponent;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

public class ChooChooApplication extends Application {
  private AppComponent appComponent;

  @Override
  public void onCreate() {
    super.onCreate();
    appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    FlowManager.init(new FlowConfig.Builder(this).openDatabasesOnInit(true).build());
  }

  public AppComponent getAppComponent() {
    return appComponent;
  }

  public static ChooChooApplication from(@NonNull Context context) {
    return (ChooChooApplication) context.getApplicationContext();
  }
}
