package com.eleith.calchoochoo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.eleith.calchoochoo.dagger.ChooChooComponent;
import com.eleith.calchoochoo.dagger.ChooChooModule;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.data.Trips;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.DeviceLocation;
import com.eleith.calchoochoo.utils.RxBus;
import com.google.android.gms.common.api.GoogleApiClient;

import org.parceler.Parcels;

import javax.inject.Inject;

public class ChooChooActivity extends AppCompatActivity {
  private ChooChooComponent chooChooComponent;

  @Inject
  RxBus rxBus;
  @Inject
  GoogleApiClient googleApiClient;
  @Inject
  DeviceLocation deviceLocation;
  @Inject
  ChooChooFragmentManager chooChooFragmentManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Intent intent = getIntent();
    chooChooComponent = ChooChooApplication.from(this).getAppComponent()
        .activityComponent(new ChooChooModule(this));
    chooChooComponent.inject(this);

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_schedule_explorer);

    if (intent != null) {
      String action = intent.getAction();
      Bundle bundle = intent.getExtras();
      if (action.equals(Intent.ACTION_VIEW) && bundle != null) {
        Trips trip = Parcels.unwrap(bundle.getParcelable(BundleKeys.TRIP));
        Stop stop = Parcels.unwrap(bundle.getParcelable(BundleKeys.STOP));

        if (trip != null && stop != null) {
          chooChooFragmentManager.loadSearchForSpotFragment(stop, trip);
          return;
        }
      }
    }

    if (savedInstanceState == null) {
      chooChooFragmentManager.loadMapSearchFragment();
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    googleApiClient.connect();
  }

  @Override
  protected void onStop() {
    super.onStop();
    googleApiClient.disconnect();
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  @Override
  protected void onResume() {
    super.onResume();
    googleApiClient.reconnect();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  public ChooChooComponent getComponent() {
    return chooChooComponent;
  }
}
