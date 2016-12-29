package com.eleith.calchoochoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.eleith.calchoochoo.ScheduleExplorerActivity;
import com.eleith.calchoochoo.utils.RxBus;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageString;

import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

public class SearchInputFragment extends Fragment {
    @Inject RxBus rxBus;
    @BindView(R.id.searchInput) EditText searchInput;

    @OnTextChanged(R.id.searchInput)
    public void OnTextChange(CharSequence s) {
      rxBus.send(new RxMessageString(RxMessageKeys.SEARCH_INPUT_STRING, s.toString()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      ((ScheduleExplorerActivity) getActivity()).getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_input, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
