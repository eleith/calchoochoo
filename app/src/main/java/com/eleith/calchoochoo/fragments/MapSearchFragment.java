package com.eleith.calchoochoo.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.ScheduleExplorerActivity;
import com.eleith.calchoochoo.data.Queries;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.MapUtils;
import com.eleith.calchoochoo.utils.Permissions;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStop;
import com.eleith.calchoochoo.utils.DrawableUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.eleith.calchoochoo.utils.DrawableUtils.getBitmapCircle;

public class MapSearchFragment extends Fragment
    implements
    OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener,
    ActivityCompat.OnRequestPermissionsResultCallback {
  private GoogleMap googleMap;
  private MapView googleMapView;
  private ArrayList<Stop> stops;
  private Location location;
  private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
  private GoogleApiClient googleApiClient;
  private Location lastLocation;
  private Marker locationMarker;
  private LatLng myDefaultLatLng = new LatLng(37.30, -122.06);

  @BindView(R.id.map_action_button)
  FloatingActionButton mapActionButton;

  @Inject
  RxBus rxBus;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((ScheduleExplorerActivity) getActivity()).getComponent().inject(this);
    initializeGoogleApiClient();
    unWrapBundle(getArguments());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_map_search, container, false);
    ButterKnife.bind(this, view);

    unWrapBundle(savedInstanceState);

    // initialize the map!
    googleMapView = ((MapView) view.findViewById(R.id.search_google_maps));
    googleMapView.onCreate(savedInstanceState);
    googleMapView.getMapAsync(this);

    return view;
  }

  @OnClick(R.id.map_search_input)
  void onClickSearchInput() {
    rxBus.send(new RxMessage(RxMessageKeys.DESTINATION_SELECTED));
  }

  @OnClick(R.id.map_action_button)
  void onClickActionButton() {
    LatLng myLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
    CameraPosition cameraPosition = new CameraPosition.Builder().zoom(13).target(myLatLng).build();
    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    this.googleMap = googleMap;
    CameraPosition.Builder cameraBuilder = new CameraPosition.Builder().zoom(13);
    LatLng myLatLng;

    setStopMarkers();

    if (location != null) {
      myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
    } else {
      myLatLng = myDefaultLatLng;
    }

    cameraBuilder.target(myLatLng);
    CameraPosition cameraPosition = cameraBuilder.build();
    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
      @Override
      public boolean onMarkerClick(Marker marker) {
        String stopId = (String) marker.getTag();
        Stop touchedStop = Queries.getStopById(stopId);
        if (touchedStop != null) {
          rxBus.send(new RxMessageStop(RxMessageKeys.STOP_SELECTED, touchedStop));
        }
        return true;
      }
    });
  }

  private void setStopMarkers() {
     for (Stop stop : stops) {
      LatLng stopLatLng = new LatLng(stop.stop_lat, stop.stop_lon);
      Bitmap trainIcon = DrawableUtils.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_train_local, 0.25f);
      MarkerOptions markerOptions = new MarkerOptions().position(stopLatLng).title(stop.stop_name);
      markerOptions.icon(BitmapDescriptorFactory.fromBitmap(trainIcon));

      Marker marker = googleMap.addMarker(markerOptions);
      marker.setTag(stop.stop_id);
    }
  }

  private void unWrapBundle(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      stops = Parcels.unwrap(savedInstanceState.getParcelable(BundleKeys.STOPS));
      location = savedInstanceState.getParcelable(BundleKeys.LOCATION);
      // if googleMap is set, then it never got the location!
      if (googleMap != null) {
        onMapReady(googleMap);
      }
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
      if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        onLocationChanged(location);
      }
    }
  }

  private void initializeGoogleApiClient() {
    if (googleApiClient == null) {
      googleApiClient = new GoogleApiClient.Builder(getContext())
          .addConnectionCallbacks(this)
          .addOnConnectionFailedListener(this)
          .addApi(LocationServices.API)
          .build();
      googleApiClient.connect();
    }
  }

  private void setMyLocationMarker(Location location) {
    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

    if (locationMarker != null) {
      MapUtils.animateMarker(locationMarker, latLng, googleMap);
    } else {
      MarkerOptions markerOptions = new MarkerOptions();
      markerOptions.position(latLng);
      markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getBitmapCircle(104, 3, Color.RED, Color.WHITE)));
      locationMarker = googleMap.addMarker(markerOptions);
    }
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    onLocationChanged(location);

    LocationRequest locationRequest = new LocationRequest();
    locationRequest.setInterval(5000); //5 seconds
    locationRequest.setFastestInterval(3000); //3 seconds
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    } else {
      requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Permissions.READ_GPS);
    }
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
  }

  @Override
  public void onConnectionSuspended(int i) {
  }

  @Override
  public void onLocationChanged(Location location) {
    if (location != null) {
      lastLocation = location;
      mapActionButton.setVisibility(View.VISIBLE);
      setMyLocationMarker(location);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    googleApiClient.reconnect();
    googleMapView.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
    googleMapView.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    googleApiClient.disconnect();
    LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    googleMapView.onDestroy();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    googleMapView.onSaveInstanceState(outState);
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    googleMapView.onLowMemory();
  }
}
