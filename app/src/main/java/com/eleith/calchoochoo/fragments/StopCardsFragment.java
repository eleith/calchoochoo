package com.eleith.calchoochoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eleith.calchoochoo.ChooChooActivity;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.adapters.StopCardAdapter;
import com.eleith.calchoochoo.data.Queries;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;

import org.parceler.Parcels;

import javax.inject.Inject;

public class StopCardsFragment extends Fragment {
  private Stop currentStop;

  @Inject
  StopCardAdapter stopCardAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((ChooChooActivity) getActivity()).getComponent().inject(this);
    unPackBundle(savedInstanceState != null ? savedInstanceState : getArguments());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    unPackBundle(savedInstanceState);

    View view = inflater.inflate(R.layout.fragment_stop_cards, container, false);
    int position = Queries.getAllParentStops().indexOf(currentStop);

    stopCardAdapter.setHighlightedStop(position);

    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.stop_cards_recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    recyclerView.setAdapter(stopCardAdapter);
    recyclerView.scrollToPosition(position);

    return view;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(BundleKeys.STOP, Parcels.wrap(currentStop));
    super.onSaveInstanceState(outState);
  }

  private void unPackBundle(Bundle bundle) {
    if (bundle != null) {
      currentStop = Parcels.unwrap(bundle.getParcelable(BundleKeys.STOP));
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }
}
