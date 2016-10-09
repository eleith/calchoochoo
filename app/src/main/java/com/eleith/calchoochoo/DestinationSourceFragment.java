package com.eleith.calchoochoo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleith.calchoochoo.data.Stop;


public class DestinationSourceFragment extends Fragment {
  private DestinationSourceFragmentListener listener;
  private Stop stopDestination;
  private Stop stopSource;

  public interface DestinationSourceFragmentListener {
    void onDestinationTouch();

    void onSourceTouch();
  }

  public DestinationSourceFragment() {
    // Required empty public constructor
  }

  public static DestinationSourceFragment newInstance() {
    DestinationSourceFragment fragment = new DestinationSourceFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle arguments = getArguments();
    if (arguments != null) {
      stopDestination = arguments.getParcelable(BundleKeys.DESTINATION);
      stopSource = arguments.getParcelable(BundleKeys.SOURCE);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_destination_source, container, false);

    TextView destinationEdit = (TextView) view.findViewById(R.id.destinationEdit);
    TextView sourceEdit = (TextView) view.findViewById(R.id.sourceEdit);
    TextView timeEdit = (TextView) view.findViewById(R.id.timeEdit);

    if (stopDestination != null) {
      destinationEdit.setText(stopDestination.getName());
    }

    if (stopSource != null) {
      sourceEdit.setText(stopSource.getName());
    }

    destinationEdit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        listener.onDestinationTouch();
      }
    });

    sourceEdit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        listener.onSourceTouch();
      }
    });

    timeEdit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        DepartingArrivingDialog dialog = new DepartingArrivingDialog();
        dialog.show(getFragmentManager(), "dialog");
      }
    });

    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof DestinationSourceFragmentListener) {
      listener = (DestinationSourceFragmentListener) context;
    } else {
      throw new RuntimeException(context.toString() + " must implement DestinationSourceFragmentListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    listener = null;
  }
}
