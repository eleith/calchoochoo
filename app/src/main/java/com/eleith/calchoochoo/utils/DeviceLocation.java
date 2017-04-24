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

import javax.inject.Inject;

public class DeviceLocation
    implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    com.google.android.gms.location.LocationListener,
    ActivityCompat.OnRequestPermissionsResultCallback {

  private RxBus rxBus;
  private GoogleApiClient googleApiClient;
  private Activity activity;
  private int requestedUpdates = 0;
  private int requestedLocation = 0;
  private Boolean requestingUpdates = false;

  @Inject
  public DeviceLocation(RxBus rxBus, Activity activity) {
    this.rxBus = rxBus;
    this.activity = activity;
    googleApiClient = new GoogleApiClient.Builder(activity).addApi(LocationServices.API).build();
    googleApiClient.registerConnectionFailedListener(this);
    googleApiClient.registerConnectionCallbacks(this);
  }

  private void connectGoogleApiClient() {
    if (!googleApiClient.isConnected() && !googleApiClient.isConnecting()) {
      googleApiClient.connect();
    }
  }

  public void requestLocation() {
    if (googleApiClient.isConnected()) {
      if (android.os.Build.VERSION.SDK_INT >= 23) {
        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
          Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
          sendLastLocation(location);
        } else {
          activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Permissions.READ_GPS);
        }
      } else {
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        sendLastLocation(location);
      }
    } else {
      requestedLocation++;
      connectGoogleApiClient();
    }
  }

  public void listenForLocationUpdates() {
    if (googleApiClient.isConnected()) {
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
      }
    } else {
      requestedUpdates++;
      connectGoogleApiClient();
    }
  }

  public void stopListeningForLocationUpdates() {
    requestingUpdates = false;
    googleApiClient.disconnect();
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    if (requestedUpdates > 0) {
      listenForLocationUpdates();
    }

    if (requestedLocation > 0) {
      requestLocation();
    }
  }

  @Override
  public void onConnectionSuspended(int i) {
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
  }

  @Override
  public void onLocationChanged(Location location) {
    rxBus.send(new RxMessageLocation(RxMessageKeys.MY_LOCATION_UPDATE, location));
  }

  private void sendLastLocation(Location location) {
    requestedLocation--;
    if (requestedUpdates == 0 && !requestingUpdates && requestedLocation == 0) {
      googleApiClient.disconnect();
    }
    rxBus.send(new RxMessageLocation(RxMessageKeys.MY_LOCATION, location));
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
    switch (requestCode) {
      case Permissions.READ_GPS: {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          if (requestedUpdates > 0) {
            listenForLocationUpdates();
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
