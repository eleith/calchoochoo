package com.eleith.calchoochoo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;

import com.eleith.calchoochoo.dagger.ScheduleExplorerActivityComponent;
import com.eleith.calchoochoo.dagger.ScheduleExplorerActivityModule;
import com.eleith.calchoochoo.data.DatabaseHelper;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.fragments.DestinationSourceFragment;
import com.eleith.calchoochoo.fragments.HomeFragment;
import com.eleith.calchoochoo.fragments.SearchInputFragment;
import com.eleith.calchoochoo.fragments.SearchResultsFragment;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.Permissions;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxMessage;
import com.eleith.calchoochoo.utils.RxMessagePair;
import com.eleith.calchoochoo.utils.RxMessageString;
import com.eleith.calchoochoo.utils.RxMessageKeys;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.functions.Action1;

public class ScheduleExplorerActivity extends AppCompatActivity {
  private SearchResultsFragment searchResultsFragment;
  private SearchInputFragment searchInputFragment;
  private DatabaseHelper databaseHelper;
  private Location location;
  private Stop stopDestination;
  private Stop stopSource;
  private LocationListener locationListener;
  private static final String SEARCH_REASON_DESTINATION = "destination";
  private static final String SEARCH_REASON_SOURCE = "source";
  private ScheduleExplorerActivityComponent scheduleExplorerActivityComponent;

  @Inject RxBus rxbus;
  @Inject LocationManager locationManager;
  @Inject FragmentManager fragmentManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    scheduleExplorerActivityComponent = ((ChooChooApplication) getApplication()).getAppComponent()
        .activityComponent(new ScheduleExplorerActivityModule(this));

    scheduleExplorerActivityComponent.inject(this);
    initializeLocationListener();

    setContentView(R.layout.schedule_explorer_activity);
    databaseHelper = new DatabaseHelper(this);
    updateFragments(new DestinationSourceFragment(), new HomeFragment());
    rxbus.observeEvents(RxMessage.class).subscribe(handleScheduleExplorerRxMessages());
  }

  private Action1<RxMessage> handleScheduleExplorerRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        String type = rxMessage.getType();

        if (type.equals(RxMessageKeys.SEARCH_INPUT_STRING) && rxMessage instanceof RxMessageString) {
          filterSearchResults(((RxMessageString) rxMessage).getMessageString());
        } else if(type.equals(RxMessageKeys.SEARCH_RESULT_PAIR) && rxMessage instanceof RxMessagePair) {
          Pair pair = ((RxMessagePair) rxMessage).getMessagePair();
          if (pair.first instanceof Stop && pair.second instanceof String) {
            selectSearchResult((Stop) pair.first, (String) pair.second);
          }
        } else if(type.equals(RxMessageKeys.DESTINATION_SELECTED)) {
          selectDestination();
        } else if(type.equals(RxMessageKeys.SOURCE_SELECTED)) {
          selectSource();
        }
      }
    };
  }

  private void filterSearchResults(String filterString) {
    if (searchResultsFragment != null && searchResultsFragment.isVisible()) {
      searchResultsFragment.filterResultsBy(filterString, location);
    }
  }

  private void selectDestination() {
    searchInputFragment = new SearchInputFragment();
    searchResultsFragment = new SearchResultsFragment();

    Bundle searchResultsArgs = new Bundle();
    ArrayList<Stop> stops = databaseHelper.getAllStations();
    searchResultsArgs.putParcelableArrayList(BundleKeys.STOPS, stops);
    searchResultsArgs.putParcelable(BundleKeys.LOCATION, location);
    searchResultsArgs.putString(BundleKeys.SEARCH_REASON, SEARCH_REASON_DESTINATION);
    searchResultsFragment.setArguments(searchResultsArgs);

    updateFragments(searchInputFragment, searchResultsFragment);
  }

  private void selectSource() {
    searchInputFragment = new SearchInputFragment();
    searchResultsFragment = new SearchResultsFragment();

    Bundle searchResultsArgs = new Bundle();
    ArrayList<Stop> stops = databaseHelper.getAllStations();
    searchResultsArgs.putParcelableArrayList(BundleKeys.STOPS, stops);
    searchResultsArgs.putParcelable(BundleKeys.LOCATION, location);
    searchResultsArgs.putString(BundleKeys.SEARCH_REASON, SEARCH_REASON_SOURCE);
    searchResultsFragment.setArguments(searchResultsArgs);

    updateFragments(searchInputFragment, searchResultsFragment);
  }

  private void selectSearchResult(Stop stop, String searchReason) {
    if (searchReason.equals(SEARCH_REASON_DESTINATION)) {
      stopDestination = stop;
    } else if (searchReason.equals(SEARCH_REASON_SOURCE)) {
      stopSource = stop;
    }

    Bundle destinationSourceArgs = new Bundle();
    DestinationSourceFragment destinationSourceFragment = new DestinationSourceFragment();
    destinationSourceArgs.putParcelable(BundleKeys.DESTINATION, stopDestination);
    destinationSourceArgs.putParcelable(BundleKeys.SOURCE, stopSource);
    destinationSourceFragment.setArguments(destinationSourceArgs);

    updateFragments(destinationSourceFragment, new HomeFragment());
  }

  private void initializeLocationListener() {
    locationListener = new LocationListener() {
      public void onLocationChanged(Location newLocation) {
        stopListeningForLocation();
        location = newLocation;
      }

      public void onStatusChanged(String provider, int status, Bundle extras) {
      }

      public void onProviderEnabled(String provider) {
      }

      public void onProviderDisabled(String provider) {
      }
    };

    startListeningForLocation(true);
  }

  private void startListeningForLocation(Boolean shouldRequest) {
    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10000, locationListener);
      location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    } else if (shouldRequest) {
      requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Permissions.READ_GPS);
    }
  }

  private void stopListeningForLocation() {
    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      locationManager.removeUpdates(locationListener);
    }
  }

  private void updateFragments(Fragment f1, Fragment f2) {
    FragmentTransaction ft = fragmentManager.beginTransaction();
    ft.replace(R.id.homeTopFragmentContainer, f1);
    ft.replace(R.id.homeFragmentContainer, f2);
    ft.addToBackStack(null);
    ft.commit();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode) {
      case Permissions.READ_GPS: {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          startListeningForLocation(false);
        }
      }
    }
  }

  public ScheduleExplorerActivityComponent getComponent() {
    return this.scheduleExplorerActivityComponent;
  }
}
