package com.eleith.calchoochoo.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.eleith.calchoochoo.ChooChooRouterManager;
import com.eleith.calchoochoo.MapSearchActivity;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.data.ChooChooLoader;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.DeviceLocation;
import com.eleith.calchoochoo.utils.DrawableUtils;
import com.eleith.calchoochoo.utils.MapUtils;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageLocation;
import com.eleith.calchoochoo.utils.StopUtils;
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
import rx.functions.Action1;

import static com.eleith.calchoochoo.utils.DrawableUtils.getBitmapCircle;

public class MapSearchFragment extends Fragment implements OnMapReadyCallback {
  private GoogleMap googleMap = null;
  private MapView googleMapView;
  private ArrayList<Stop> stops = null;
  private Location lastLocation;
  private Marker locationMarker;
  private LatLng myDefaultLatLng = new LatLng(37.30, -122.06);
  private Subscription subscriptionRxBus;
  private MapSearchActivity mapSearchActivity;

  @Inject
  RxBus rxBus;
  @Inject
  ChooChooRouterManager chooChooRouterManager;
  @Inject
  ChooChooLoader chooChooLoader;
  @Inject
  DeviceLocation deviceLocation;

  @BindView(R.id.map_search_input)
  EditText mapSearchInput;

  @BindView(R.id.search_google_maps)
  MapView mapSearchMaps;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mapSearchActivity = (MapSearchActivity) getActivity();
    mapSearchActivity.getComponent().inject(this);
    unWrapBundle(savedInstanceState == null ? getArguments() : savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_map_search, container, false);
    ButterKnife.bind(this, view);

    unWrapBundle(savedInstanceState);
    mapSearchActivity.fabEnable(R.drawable.ic_gps_not_fixed_black_24dp);

    // initialize the map!
    googleMapView = ((MapView) view.findViewById(R.id.search_google_maps));
    googleMapView.onCreate(savedInstanceState);
    googleMapView.getMapAsync(this);

    return view;
  }

  @OnClick(R.id.map_search_input)
  void onClickSearchInput() {
    Stop stop = StopUtils.findStopClosestTo(stops, lastLocation);
    ArrayList<String> filteredStopIds = new ArrayList<>();
    if (stop != null) {
      filteredStopIds.add(stop.stop_id);
    }
    chooChooRouterManager.loadStopSearchActivity(getActivity(), 0, filteredStopIds);
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
    googleMap.setOnMarkerClickListener(new OnMarkerClickListener());

    MapUtils.moveMapToLocation(lastLocation, googleMap, new CameraPosition.Builder().zoom(13));
    setMyLocationMarker(lastLocation);

    subscriptionRxBus = rxBus.observeEvents(RxMessage.class).subscribe(handleRxMessages());
  }

  private void setStopMarkers() {
    if (googleMap != null && stops != null) {
      mapSearchInput.setVisibility(View.VISIBLE);

      for (Stop stop : stops) {
        LatLng stopLatLng = new LatLng(stop.stop_lat, stop.stop_lon);
        Bitmap trainIcon = DrawableUtils.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_train_local, 0.25f);
        MarkerOptions markerOptions = new MarkerOptions().position(stopLatLng).title(stop.stop_name);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(trainIcon));

        Marker marker = googleMap.addMarker(markerOptions);
        marker.setTag(stop.stop_id);
      }
    }
  }

  private void unWrapBundle(Bundle bundle) {
    if (bundle != null) {
      stops = Parcels.unwrap(bundle.getParcelable(BundleKeys.STOPS));
      lastLocation = bundle.getParcelable(BundleKeys.LOCATION);
      setStopMarkers();
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
  public void onDestroyView() {
    super.onDestroyView();
    if (subscriptionRxBus != null) {
      subscriptionRxBus.unsubscribe();
    }
    ((MapSearchActivity) getActivity()).fabDisable();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    googleMapView.onDestroy();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    googleMapView.onSaveInstanceState(outState);
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    googleMapView.onLowMemory();
  }

  private class OnMarkerClickListener implements GoogleMap.OnMarkerClickListener {
    @Override
    public boolean onMarkerClick(Marker marker) {
      String stopId = (String) marker.getTag();
      chooChooRouterManager.loadTripFilterActivity(getActivity(), stopId, stopId);
      return true;
    }
  }

  private Action1<RxMessage> handleRxMessages() {
    return new Action1<RxMessage>() {
      @Override
      public void call(RxMessage rxMessage) {
        if (rxMessage.isMessageValidFor(RxMessageKeys.FAB_CLICKED)) {
          LatLng myLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
          CameraPosition cameraPosition = new CameraPosition.Builder().zoom(13).target(myLatLng).build();
          googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else if (rxMessage.isMessageValidFor(RxMessageKeys.MY_LOCATION_UPDATE)) {
          Location location = ((RxMessageLocation) rxMessage).getMessage();
          setMyLocationMarker(location);
        }
      }
    };
  }
}
