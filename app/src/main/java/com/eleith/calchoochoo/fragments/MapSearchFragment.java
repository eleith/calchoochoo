package com.eleith.calchoochoo.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.ScheduleExplorerActivity;
import com.eleith.calchoochoo.data.Queries;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.DeviceLocation;
import com.eleith.calchoochoo.utils.DrawableUtils;
import com.eleith.calchoochoo.utils.MapUtils;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStop;
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
import rx.Subscription;

import static com.eleith.calchoochoo.utils.DrawableUtils.getBitmapCircle;

public class MapSearchFragment extends Fragment implements OnMapReadyCallback {
  private GoogleMap googleMap;
  private MapView googleMapView;
  private ArrayList<Stop> stops;
  private Location lastLocation;
  private Marker locationMarker;
  private LatLng myDefaultLatLng = new LatLng(37.30, -122.06);
  private Subscription subscription;

  @BindView(R.id.map_action_button)
  FloatingActionButton mapActionButton;

  @Inject
  RxBus rxBus;
  @Inject
  DeviceLocation deviceLocation;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((ScheduleExplorerActivity) getActivity()).getComponent().inject(this);
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
  public void onMapReady(final GoogleMap googleMap) {
    this.googleMap = googleMap;
    CameraPosition.Builder cameraBuilder = new CameraPosition.Builder().zoom(13);
    LatLng myLatLng;

    setStopMarkers();

    if (locationMarker != null) {
      locationMarker.remove();
      locationMarker = null;
    }

    if (lastLocation != null) {
      myLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
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

    deviceLocation.requestLocation(new DeviceLocation.LocationGetListener() {
      @Override
      public void onLocationGet(Location location) {
        MapUtils.moveMapToLocation(location, googleMap, new CameraPosition.Builder().zoom(13));
        setMyLocationMarker(location);
      }
    });

    subscription = deviceLocation.subscribeToLocationUpdates(new DeviceLocation.LocationGetListener() {
      @Override
      public void onLocationGet(Location location) {
        setMyLocationMarker(location);
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
      // if googleMap is set, then it never got the location!
      if (googleMap != null) {
        onMapReady(googleMap);
      }
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
    lastLocation = location;
    mapActionButton.setVisibility(View.VISIBLE);
  }

  @Override
  public void onResume() {
    super.onResume();
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
    subscription.unsubscribe();
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
