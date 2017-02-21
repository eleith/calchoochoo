package com.eleith.calchoochoo.utils;

import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapUtils {
  static public void animateMarker(final Marker marker, final LatLng toPosition, GoogleMap googleMap) {
    final Handler handler = new Handler();
    final long start = SystemClock.uptimeMillis();
    Projection projection = googleMap.getProjection();
    Point startPoint = projection.toScreenLocation(marker.getPosition());
    final LatLng startLatLng = projection.fromScreenLocation(startPoint);
    final long duration = 500;

    final Interpolator interpolator = new LinearInterpolator();

    handler.post(new Runnable() {
      @Override
      public void run() {
        long elapsed = SystemClock.uptimeMillis() - start;
        float t = interpolator.getInterpolation((float) elapsed
            / duration);
        double lng = t * toPosition.longitude + (1 - t)
            * startLatLng.longitude;
        double lat = t * toPosition.latitude + (1 - t)
            * startLatLng.latitude;
        marker.setPosition(new LatLng(lat, lng));

        if (t < 1.0) {
          // Post again 16ms later.
          handler.postDelayed(this, 16);
        } else {
          marker.setVisible(true);
        }
      }
    });
  }
}
