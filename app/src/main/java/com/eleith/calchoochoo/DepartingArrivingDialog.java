package com.eleith.calchoochoo;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class DepartingArrivingDialog extends android.support.v4.app.DialogFragment {

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View view = inflater.inflate(R.layout.fragment_departing_arriving_selector, null);
    builder.setView(view);

    View cancelButton = view.findViewById(R.id.departOrArriveCancel);
    cancelButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getDialog().dismiss();
      }
    });

    View selectButton = view.findViewById(R.id.departOrArriveSelect);
    selectButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getDialog().dismiss();
      }
    });

    return builder.create();
  }
}
