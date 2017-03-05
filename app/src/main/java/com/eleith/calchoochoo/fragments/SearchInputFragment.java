package com.eleith.calchoochoo.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.eleith.calchoochoo.ChooChooActivity;
import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.data.Stop;
import com.eleith.calchoochoo.utils.BundleKeys;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageString;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

public class SearchInputFragment extends Fragment {
  private Stop stopDestination;
  private Stop stopSource;

  @Inject
  RxBus rxBus;
  @BindView(R.id.searchInput)
  EditText searchInput;

  @OnTextChanged(R.id.searchInput)
  public void OnTextChange(CharSequence s) {
    rxBus.send(new RxMessageString(RxMessageKeys.SEARCH_INPUT_STRING, s.toString()));
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Activity activity = getActivity();
    super.onCreate(savedInstanceState);

    if (activity instanceof ChooChooActivity) {
      ((ChooChooActivity) activity).getComponent().inject(this);
    }
    unWrapBundle(savedInstanceState != null ? savedInstanceState : getArguments());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_search_input, container, false);
    ButterKnife.bind(this, view);
    unWrapBundle(savedInstanceState);

    if (stopDestination != null) {
      searchInput.setHint(getString(R.string.where_from));
    } else if (stopSource != null) {
      searchInput.setHint(getString(R.string.where_to_go));
    } else {
      searchInput.setHint(getString(R.string.search_for_stop));
    }
    searchInput.requestFocus();
    showKeyboard();
    return view;
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    if (hidden) {
      hideKeyboard();
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    hideKeyboard();
  }

  private void unWrapBundle(Bundle bundle) {
    if (bundle != null) {
      stopDestination = Parcels.unwrap(bundle.getParcelable(BundleKeys.STOP_DESTINATION));
      stopSource = Parcels.unwrap(bundle.getParcelable(BundleKeys.STOP_SOURCE));
    }
  }

  private void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm.isActive()) {
      imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
    }
  }

  private void showKeyboard() {
    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
  }
}
