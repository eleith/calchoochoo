package com.eleith.calchoochoo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.data.Queries;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.RxBus;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import butterknife.ButterKnife;

public class StopCardAdapter extends RecyclerView.Adapter<StopCardAdapter.StopCardHolder> {
  private ArrayList<Stop> stops;
  private RxBus rxBus;

  public StopCardAdapter(RxBus rxBus) {
    stops = Queries.getAllStops();
    this.rxBus = rxBus;
  }

  @Override
  public StopCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_stop_card, parent, false);
    return new StopCardHolder(view);
  }

  @Override
  public void onBindViewHolder(StopCardHolder holder, int position) {
    Stop stop = stops.get(position);
    holder.stopName.setText(stop.stop_name);
  }

  @Override
  public int getItemCount() {
    return stops.size();
  }

  class StopCardHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap googleMap;
    private Stop stop;
    TextView stopName;

    //@OnClick(R.id.trip_possible_summary)
    //void onClickTripSummary() {
    //  rxBus.send(new RxMessagePossibleTrip(RxMessageKeys.TRIP_SELECTED, possibleTrips.get(getAdapterPosition())));
    //}

    private StopCardHolder(View view) {
      super(view);

      // initialize the map!
      mapView = ((MapView) view.findViewById(R.id.stop_card_map_view));
      mapView.onCreate(null);
      mapView.getMapAsync(this);

      stopName = (TextView) view.findViewById(R.id.stop_card_stop_name);

      ButterKnife.bind(this, view);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
      this.googleMap = googleMap;
      int position = getAdapterPosition();

      if (position >= 0 && position < stops.size()) {
        stop = stops.get(position);

        LatLng stopLatLng = new LatLng(stop.stop_lat, stop.stop_lon);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraPosition cameraPosition = new CameraPosition.Builder().tilt(15).zoom(18).target(stopLatLng).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
      }
    }
  }
}