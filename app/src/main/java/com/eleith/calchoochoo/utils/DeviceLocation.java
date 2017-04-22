package com.eleith.calchoochoo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

public class DeviceLocation
    implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    com.google.android.gms.location.LocationListener,
    ActivityCompat.OnRequestPermissionsResultCallback {

  private RxBus rxBus;
  private GoogleApiClient googleApiClient;
  private Activity activity;
  private Boolean googleApiClientReady = false;
  private int requestedUpdates = 0;
  private int requestedLocation = 0;
  private Boolean requestingUpdates = false;
  private LatLng myDefaultLatLng = new LatLng(37.30, -122.06);

  @Inject
  public DeviceLocation(RxBus rxBus, GoogleApiClient googleApiClient, Activity activity) {
    this.rxBus = rxBus;
    this.googleApiClient = googleApiClient;
    this.activity = activity;
    googleApiClient.registerConnectionFailedListener(this);
    googleApiClient.registerConnectionCallbacks(this);
  }

  public void requestLocation() {
    if (googleApiClientReady) {
      if (android.os.Build.VERSION.SDK_INT >= 23) {
        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
          Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
          if (location != null) {
            onLocationChanged(location);
          } else {
            location = new Location("default");
            location.setLatitude(myDefaultLatLng.latitude);
            location.setLongitude(myDefaultLatLng.longitude);
            onLocationChanged(location);
          }
          return;
        } else {
          activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Permissions.READ_GPS);
        }
      }
    } else {
      Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
      if (location != null) {
        onLocationChanged(location);
      } else {
        location = new Location("default");
        location.setLatitude(myDefaultLatLng.latitude);
        location.setLongitude(myDefaultLatLng.longitude);
        onLocationChanged(location);
      }
      return;
    }
    requestedLocation++;
  }

  public void requestLocationUpdates() {
    if (googleApiClientReady) {
      if (android.os.Build.VERSION.SDK_INT >= 23) {
        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
          if (!requestingUpdates) {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(5000); //5 seconds
            locationRequest.setFastestInterval(3000); //3 seconds
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            requestingUpdates = true;
          }
          return;
        } else {
          activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Permissions.READ_GPS);
        }
      } else {
        if (!requestingUpdates) {
          LocationRequest locationRequest = new LocationRequest();
          locationRequest.setInterval(5000); //5 seconds
          locationRequest.setFastestInterval(3000); //3 seconds
          locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
          LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
          requestingUpdates = true;
        }
        return;
      }
    }
    requestedUpdates++;
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    googleApiClientReady = true;

    if (requestedUpdates > 0) {
      requestLocationUpdates();
    }

    if (requestedLocation > 0) {
      requestLocation();
    }
  }

  @Override
  public void onConnectionSuspended(int i) {
    googleApiClientReady = false;
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    googleApiClientReady = false;
  }

  @Override
  public void onLocationChanged(Location location) {
    if (location != null) {
      rxBus.send(new RxMessageLocation(RxMessageKeys.MY_LOCATION_UPDATE, location));
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
    switch (requestCode) {
      case Permissions.READ_GPS: {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          if (requestedUpdates > 0) {
            requestLocationUpdates();
          } else if (requestedLocation > 0) {
            requestLocation();
          }
        } else {
          rxBus.send(new RxMessageLocation(RxMessageKeys.MY_LOCATION_UPDATE, null));
        }
      }
    }
  }

}
