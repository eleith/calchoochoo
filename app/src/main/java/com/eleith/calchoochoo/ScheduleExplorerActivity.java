package com.eleith.calchoochoo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.eleith.calchoochoo.data.DatabaseHelper;
import com.eleith.calchoochoo.data.Stop;

import java.util.ArrayList;

public class ScheduleExplorerActivity extends AppCompatActivity
    implements DestinationSourceFragment.DestinationSourceFragmentListener,
    SearchResultsFragment.SearchResultsFragmentListener,
    SearchInputFragment.SearchInputFragmentListener {

  private SearchResultsFragment searchResultsFragment;
  private SearchInputFragment searchInputFragment;
  private DatabaseHelper databaseHelper;
  private LocationManager locationManager;
  private Location location;
  private Stop stopDestination;
  private Stop stopSource;
  private LocationListener locationListener;
  private static final String SEARCH_REASON_DESTINATION = "destination";
  private static final String SEARCH_REASON_SOURCE = "source";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

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

    // don't want to check for location everytime if user doesn't want to give it up ...
    startListeningForLocation(true);
    setContentView(R.layout.schedule_explorer_activity);
    databaseHelper = new DatabaseHelper(this);

    DestinationSourceFragment destinationSourceFragment = new DestinationSourceFragment();
    HomeFragment homeFragment = new HomeFragment();

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.homeTopFragmentContainer, destinationSourceFragment);
    transaction.replace(R.id.homeFragmentContainer, homeFragment);
    transaction.addToBackStack(null);

    transaction.commit();
  }

  @Override
  public void onSearchInputChange(String searchText) {
    if (searchResultsFragment != null && searchResultsFragment.isVisible()) {
      searchResultsFragment.filterResultsBy(searchText, location);
    }
  }

  @Override
  public void onDestinationTouch() {
    searchInputFragment = new SearchInputFragment();
    searchResultsFragment = new SearchResultsFragment();

    Bundle searchResultsArgs = new Bundle();
    ArrayList<Stop> stops = databaseHelper.getAllStations();
    searchResultsArgs.putParcelableArrayList(BundleKeys.STOPS, stops);
    searchResultsArgs.putParcelable(BundleKeys.LOCATION, location);
    searchResultsArgs.putString(BundleKeys.SEARCH_REASON, SEARCH_REASON_DESTINATION);

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    searchResultsFragment.setArguments(searchResultsArgs);

    transaction.replace(R.id.homeTopFragmentContainer, searchInputFragment);
    transaction.replace(R.id.homeFragmentContainer, searchResultsFragment);
    transaction.addToBackStack(null);

    // Commit the transaction
    transaction.commit();
  }

  @Override
  public void onSourceTouch() {
    searchInputFragment = new SearchInputFragment();
    searchResultsFragment = new SearchResultsFragment();

    Bundle searchResultsArgs = new Bundle();
    ArrayList<Stop> stops = databaseHelper.getAllStations();
    searchResultsArgs.putParcelableArrayList(BundleKeys.STOPS, stops);
    searchResultsArgs.putParcelable(BundleKeys.LOCATION, location);
    searchResultsArgs.putString(BundleKeys.SEARCH_REASON, SEARCH_REASON_SOURCE);

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    searchResultsFragment.setArguments(searchResultsArgs);
    transaction.replace(R.id.homeTopFragmentContainer, searchInputFragment);
    transaction.replace(R.id.homeFragmentContainer, searchResultsFragment);
    transaction.addToBackStack(null);

    // Commit the transaction
    transaction.commit();
  }

  @Override
  public void onSearchResultSelect(Stop stop, String searchReason) {
    if (searchReason == SEARCH_REASON_DESTINATION) {
      stopDestination = stop;
    } else if (searchReason == SEARCH_REASON_SOURCE) {
      stopSource = stop;
    }

    Bundle destinationSourceArgs = new Bundle();
    DestinationSourceFragment destinationSourceFragment = new DestinationSourceFragment();
    destinationSourceArgs.putParcelable(BundleKeys.DESTINATION, stopDestination);
    destinationSourceArgs.putParcelable(BundleKeys.SOURCE, stopSource);

    destinationSourceFragment.setArguments(destinationSourceArgs);
    HomeFragment homeFragment = new HomeFragment();

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.homeTopFragmentContainer, destinationSourceFragment);
    transaction.replace(R.id.homeFragmentContainer, homeFragment);
    transaction.addToBackStack(null);

    // Commit the transaction
    transaction.commit();
  }

  public void startListeningForLocation(Boolean shouldRequest) {
    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10000, locationListener);
      location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    } else if (shouldRequest) {
      requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Permissions.READ_GPS);
    }
  }

  public void stopListeningForLocation() {
    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      locationManager.removeUpdates(locationListener);
    }
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
}
