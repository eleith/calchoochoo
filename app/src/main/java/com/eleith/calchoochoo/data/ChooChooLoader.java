package com.eleith.calchoochoo.data;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.Pair;

import com.eleith.calchoochoo.ChooChooActivity;
import com.eleith.calchoochoo.dagger.ChooChooScope;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.PossibleTrainUtils;
import com.eleith.calchoochoo.utils.PossibleTripUtils;
import com.eleith.calchoochoo.utils.RouteUtils;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageNextTrains;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessagePossibleTrip;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessagePossibleTrips;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageRoute;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageRoutes;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageStops;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageTrip;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageTripStops;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageTrips;
import com.eleith.calchoochoo.utils.StopTimesUtils;
import com.eleith.calchoochoo.utils.StopUtils;
import com.eleith.calchoochoo.utils.TripUtils;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;

import javax.inject.Inject;

@ChooChooScope
public class ChooChooLoader implements LoaderManager.LoaderCallbacks<Cursor> {
  private ChooChooActivity chooChooActivity;
  private RxBus rxBus;

  private ArrayList<Stop> stopsParents = null;
  private ArrayList<Routes> routes = null;
  private ArrayList<Trips> trips = null;

  @Inject
  public ChooChooLoader(ChooChooActivity chooChooActivity, RxBus rxBus) {
    this.chooChooActivity = chooChooActivity;
    this.rxBus = rxBus;
  }

  public void loadParentStops() {
    if (stopsParents == null) {
      chooChooActivity.getLoaderManager().initLoader(ChooChooContentProvider.URI_STOPS_PARENTS, null, this);
    } else {
      notifyParentStopsLoaded();
    }
  }

  public void loadRoutes() {
    if (routes == null) {
      chooChooActivity.getLoaderManager().initLoader(ChooChooContentProvider.URI_ROUTES, null, this);
    } else {
      notifyRoutesLoaded();
    }
  }

  public void loadTrips() {
    if (trips == null) {
      chooChooActivity.getLoaderManager().initLoader(ChooChooContentProvider.URI_TRIPS, null, this);
    } else {
      notifyTripsLoaded();
    }
  }

  public void loadRoute(String route_id) {
    Bundle bundle = new Bundle();
    bundle.putString(BundleKeys.ROUTES, route_id);
    chooChooActivity.getLoaderManager().initLoader(ChooChooContentProvider.URI_ROUTES_ID, bundle, this);
  }

  public void loadTrip(String trip_id) {
    Bundle bundle = new Bundle();
    bundle.putString(BundleKeys.TRIP, trip_id);
    chooChooActivity.getLoaderManager().initLoader(ChooChooContentProvider.URI_TRIPS_ID, bundle, this);
  }

  public void loadTripStops(String trip_id, String stop1_id, String stop2_id) {
    Bundle bundle = new Bundle();
    bundle.putString(BundleKeys.STOP_SOURCE, stop1_id);
    bundle.putString(BundleKeys.STOP_DESTINATION, stop2_id);
    bundle.putString(BundleKeys.TRIP, trip_id);
    chooChooActivity.getLoaderManager().initLoader(ChooChooContentProvider.URI_FIND_STOP_TIMES_TRIP, bundle, this);
  }

  public void loadPossibleTrains(String stop_id, LocalDateTime localDateTime) {
    Bundle bundle = new Bundle();
    bundle.putString(BundleKeys.STOP_SOURCE, stop_id);
    bundle.putLong(BundleKeys.STOP_DATETIME, localDateTime.toDate().getTime());
    chooChooActivity.getLoaderManager().initLoader(ChooChooContentProvider.URI_FIND_POSSIBLE_TRAIN, bundle, this);
  }

  public void loadPossibleTrip(String trip_id, String stop1_id, String stop2_id) {
    Bundle bundle = new Bundle();
    bundle.putString(BundleKeys.TRIP, trip_id);
    bundle.putString(BundleKeys.STOP_SOURCE, stop1_id);
    bundle.putString(BundleKeys.STOP_DESTINATION, stop2_id);
    chooChooActivity.getLoaderManager().initLoader(ChooChooContentProvider.URI_FIND_POSSIBLE_TRIP, bundle, this);
  }

  public void loadPossibleTrips(String stop1_id, String stop2_id, LocalDateTime localDateTime) {
    Bundle bundle = new Bundle();
    bundle.putString(BundleKeys.STOP_SOURCE, stop1_id);
    bundle.putString(BundleKeys.STOP_DESTINATION, stop2_id);
    bundle.putLong(BundleKeys.STOP_DATETIME, localDateTime.toDateTime().getMillis());
    chooChooActivity.getLoaderManager().initLoader(ChooChooContentProvider.URI_FIND_POSSIBLE_TRIPS, bundle, this);
  }

  public void loadStopsOnTrip(String trip_id) {
    Bundle bundle = new Bundle();
    bundle.putString(BundleKeys.TRIP, trip_id);
    chooChooActivity.getLoaderManager().initLoader(ChooChooContentProvider.URI_FIND_TRIP_STOPS, bundle, this);
  }

  private void notifyParentStopsLoaded() {
    rxBus.send(new RxMessageStops(RxMessageKeys.LOADED_STOPS, stopsParents));
  }

  private void notifyRoutesLoaded() {
    rxBus.send(new RxMessageRoutes(RxMessageKeys.LOADED_ROUTES, routes));
  }

  private void notifyTripsLoaded() {
    rxBus.send(new RxMessageTrips(RxMessageKeys.LOADED_TRIPS, trips));
  }

  private void notifyStopTimesTripLoaded(ArrayList<Pair<Stop, StopTimes>> tripStops) {
    rxBus.send(new RxMessageTripStops(RxMessageKeys.LOADED_TRIP_DETAILS, tripStops));
  }

  private void notifyNextTrainsLoaded(ArrayList<PossibleTrain> possibleTrains) {
    rxBus.send(new RxMessageNextTrains(RxMessageKeys.LOADED_NEXT_TRAINS, possibleTrains));
  }

  private void notifyRouteLoaded(Routes route) {
    rxBus.send(new RxMessageRoute(RxMessageKeys.LOADED_ROUTE, route));
  }

  private void notifyTripLoaded(Trips trip) {
    rxBus.send(new RxMessageTrip(RxMessageKeys.LOADED_TRIP, trip));
  }

  private void notifyPossibleTripLoaded(PossibleTrip possibleTrip) {
    rxBus.send(new RxMessagePossibleTrip(RxMessageKeys.LOADED_POSSIBLE_TRIP, possibleTrip));
  }

  private void notifyPossibleTripsLoaded(ArrayList<PossibleTrip> possibleTrips) {
    rxBus.send(new RxMessagePossibleTrips(RxMessageKeys.LOADED_POSSIBLE_TRIPS, possibleTrips));
  }

  private void notifyStopsOnTripLoaded(ArrayList<Stop> stops) {
    rxBus.send(new RxMessageStops(RxMessageKeys.LOADED_STOPS_ON_TRIP, stops));
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    switch (loader.getId()) {
      case ChooChooContentProvider.URI_STOPS_PARENTS:
        stopsParents = StopUtils.getStopsFromCursor(data);
        notifyParentStopsLoaded();
        break;
      case ChooChooContentProvider.URI_ROUTES:
        routes = RouteUtils.getRoutesFromCursor(data);
        notifyRoutesLoaded();
        break;
      case ChooChooContentProvider.URI_TRIPS:
        trips = TripUtils.getTripsFromCursor(data);
        notifyRoutesLoaded();
        break;
      case ChooChooContentProvider.URI_FIND_TRIP_STOPS:
        ArrayList<Stop> stops = StopUtils.getStopsFromCursor(data);
        notifyStopsOnTripLoaded(stops);
        break;
      case ChooChooContentProvider.URI_FIND_POSSIBLE_TRIP:
        PossibleTrip possibleTrip = PossibleTripUtils.getPossibleTripFromCursor(data);
        notifyPossibleTripLoaded(possibleTrip);
        break;
      case ChooChooContentProvider.URI_FIND_POSSIBLE_TRIPS:
        ArrayList<PossibleTrip> possibleTrips = PossibleTripUtils.getPossibleTripsFromCursor(data);
        notifyPossibleTripsLoaded(possibleTrips);
        break;
      case ChooChooContentProvider.URI_FIND_STOP_TIMES_TRIP:
        ArrayList<Pair<Stop, StopTimes>> tripStops = StopTimesUtils.getStopTimesTripFromCursor(data);
        notifyStopTimesTripLoaded(tripStops);
        break;
      case ChooChooContentProvider.URI_FIND_POSSIBLE_TRAIN:
        ArrayList<PossibleTrain> possibleTrains = PossibleTrainUtils.getPossibleTrainFromCursor(data);
        notifyNextTrainsLoaded(possibleTrains);
        break;
      case ChooChooContentProvider.URI_ROUTES_ID:
        Routes routes = RouteUtils.getRouteFromCursor(data);
        notifyRouteLoaded(routes);
        break;
      case ChooChooContentProvider.URI_TRIPS_ID:
        Trips trip = TripUtils.getTripFromCursor(data);
        notifyTripLoaded(trip);
        break;
    }
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    CursorLoader cursorLoader = null;
    Uri baseUri;

    switch (id) {
      case ChooChooContentProvider.URI_STOPS_PARENTS:
        baseUri = Uri.withAppendedPath(ChooChooContentProvider.CONTENT_URI, "stops/parents");
        cursorLoader = new CursorLoader(chooChooActivity, baseUri, null, null, null, null);
        break;
      case ChooChooContentProvider.URI_ROUTES:
        baseUri = Uri.withAppendedPath(ChooChooContentProvider.CONTENT_URI, "routes");
        cursorLoader = new CursorLoader(chooChooActivity, baseUri, null, null, null, null);
        break;
      case ChooChooContentProvider.URI_TRIPS:
        baseUri = Uri.withAppendedPath(ChooChooContentProvider.CONTENT_URI, "trips");
        cursorLoader = new CursorLoader(chooChooActivity, baseUri, null, null, null, null);
        break;
      case ChooChooContentProvider.URI_ROUTES_ID:
        String routeId = args.getString(BundleKeys.ROUTES);
        baseUri = Uri.withAppendedPath(ChooChooContentProvider.CONTENT_URI, "routes/" + routeId);
        cursorLoader = new CursorLoader(chooChooActivity, baseUri, null, null, null, null);
        break;
      case ChooChooContentProvider.URI_TRIPS_ID:
        String tripId = args.getString(BundleKeys.TRIP);
        baseUri = Uri.withAppendedPath(ChooChooContentProvider.CONTENT_URI, "trips/" + tripId);
        cursorLoader = new CursorLoader(chooChooActivity, baseUri, null, null, null, null);
        break;
      case ChooChooContentProvider.URI_FIND_TRIP_STOPS:
        String tripStopsId = args.getString(BundleKeys.TRIP);
        baseUri = Uri.withAppendedPath(ChooChooContentProvider.CONTENT_URI, "stop_times/trip/" + tripStopsId);
        cursorLoader = new CursorLoader(chooChooActivity, baseUri, null, null, null, null);
        break;
      case ChooChooContentProvider.URI_FIND_POSSIBLE_TRIP:
        String possibleTripId = args.getString(BundleKeys.TRIP);
        String possibleTripSourceId = args.getString(BundleKeys.STOP_SOURCE);
        String possibleTripDestinationId = args.getString(BundleKeys.STOP_DESTINATION);
        String possibleTripPath = possibleTripId + "/" + possibleTripSourceId + "/" + possibleTripDestinationId;
        baseUri = Uri.withAppendedPath(ChooChooContentProvider.CONTENT_URI, "possibleTrips/trip/" + possibleTripPath);
        cursorLoader = new CursorLoader(chooChooActivity, baseUri, null, null, null, null);
        break;
      case ChooChooContentProvider.URI_FIND_POSSIBLE_TRIPS:
        String possibleTripsSourceId = args.getString(BundleKeys.STOP_SOURCE);
        String possibleTripsDestinationId = args.getString(BundleKeys.STOP_DESTINATION);
        Long possibleTripsDateTime = args.getLong(BundleKeys.STOP_DATETIME);
        String possibleTripsPath = possibleTripsDateTime + "/" + possibleTripsSourceId + "/" + possibleTripsDestinationId;
        baseUri = Uri.withAppendedPath(ChooChooContentProvider.CONTENT_URI, "possibleTrips/on/" + possibleTripsPath);
        cursorLoader = new CursorLoader(chooChooActivity, baseUri, null, null, null, null);
        break;
      case ChooChooContentProvider.URI_FIND_STOP_TIMES_TRIP:
        String tripDetailTripId = args.getString(BundleKeys.TRIP);
        String tripDetailSourceId = args.getString(BundleKeys.STOP_SOURCE);
        String tripDetailDestinationId = args.getString(BundleKeys.STOP_DESTINATION);
        String tripDetailPath = tripDetailTripId + "/" + tripDetailSourceId + "/" + tripDetailDestinationId;
        baseUri = Uri.withAppendedPath(ChooChooContentProvider.CONTENT_URI, "stopsAndTimes/" + tripDetailPath);
        cursorLoader = new CursorLoader(chooChooActivity, baseUri, null, null, null, null);
        break;
      case ChooChooContentProvider.URI_FIND_POSSIBLE_TRAIN:
        String nextTrainStopId = args.getString(BundleKeys.STOP_SOURCE);
        Long nextTrainDateTime = args.getLong(BundleKeys.STOP_DATETIME);
        String nextTrainPath = nextTrainStopId + "/" + nextTrainDateTime;
        baseUri = Uri.withAppendedPath(ChooChooContentProvider.CONTENT_URI, "possibleTrains/" + nextTrainPath);
        cursorLoader = new CursorLoader(chooChooActivity, baseUri, null, null, null, null);
        break;
    }
    return cursorLoader;
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {

  }
}
