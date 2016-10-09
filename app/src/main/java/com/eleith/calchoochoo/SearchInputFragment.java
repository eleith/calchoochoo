package com.eleith.calchoochoo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class SearchInputFragment extends Fragment {
    private SearchInputFragmentListener listener;

    public interface SearchInputFragmentListener {
        void onSearchInputChange(String searchText);
    }

    public SearchInputFragment() {
        // Required empty public constructor
    }

    public static SearchInputFragment newInstance() {
        SearchInputFragment fragment = new SearchInputFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_input, container, false);

        EditText searchInput = (EditText) view.findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listener.onSearchInputChange(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchInputFragmentListener) {
            listener = (SearchInputFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement SearchInputFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
