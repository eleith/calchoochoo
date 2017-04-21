package com.eleith.calchoochoo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.ColorUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooChooDrawer {
  private Activity activity;
  private String stopSourceId;

  @BindView(R.id.activityDrawer)
  DrawerLayout drawerLayout;

  @BindView(R.id.activityDrawerMapSearch)
  TextView activityDrawerMapSearchMenu;

  @BindView(R.id.activityDrawerTripExplorer)
  TextView getActivityDrawerTripMenu;

  @OnClick(R.id.activityDrawerNews)
  void goToNewsWWW() {
    String url = "https://twitter.com/caltrain";
    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
    CustomTabsIntent customTabsIntent = builder.build();
    builder.setToolbarColor(ColorUtils.getThemeColor(activity, R.attr.colorPrimary));
    customTabsIntent.launchUrl(activity, Uri.parse(url));
  }

  @OnClick(R.id.activityDrawerTripExplorer)
  void goToTripExplorer() {
    if (!activity.getClass().equals(TripFilterActivity.class)) {
      Intent intent = new Intent(activity, TripFilterActivity.class);
      if (stopSourceId != null) {
        intent.putExtra(BundleKeys.STOP_SOURCE, stopSourceId);
      }
      activity.startActivity(intent);
    }
  }

  @OnClick(R.id.activityDrawerMapSearch)
  void goToMapSearch() {
    if (!activity.getClass().equals(MapSearchActivity.class)) {
      Intent intent = new Intent(activity, MapSearchActivity.class);
      activity.startActivity(intent);
    }
  }

  @OnClick(R.id.activityDrawerAbout)
  void goToAbout() {
    String url = "https://github.com/eleith/calchoochoo/blob/master/README.md";
    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
    CustomTabsIntent customTabsIntent = builder.build();
    builder.setToolbarColor(ColorUtils.getThemeColor(activity, R.attr.colorPrimary));
    customTabsIntent.launchUrl(activity, Uri.parse(url));
  }

  ChooChooDrawer(Activity activity, View view) {
    this.activity = activity;
    ButterKnife.bind(this, view);

    if (activity.getClass().equals(MapSearchActivity.class)) {
      activityDrawerMapSearchMenu.setBackgroundColor(ColorUtils.getThemeColor(activity, android.R.attr.textColorSecondaryInverse));
    } else if (activity.getClass().equals(TripFilterActivity.class)) {
      getActivityDrawerTripMenu.setBackgroundColor(ColorUtils.getThemeColor(activity, android.R.attr.textColorSecondaryInverse));
    }
  }

  public void setStopSource(String stopSourceId) {
    this.stopSourceId = stopSourceId;
  }

  public void open() {
    drawerLayout.openDrawer(Gravity.LEFT);
  }
}
