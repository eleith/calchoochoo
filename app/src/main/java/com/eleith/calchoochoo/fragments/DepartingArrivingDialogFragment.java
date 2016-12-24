package com.eleith.calchoochoo.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.util.TimeUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleith.calchoochoo.R;
import com.eleith.calchoochoo.ScheduleExplorerActivity;
import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxMessage;
import com.eleith.calchoochoo.utils.RxMessageKeys;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.w3c.dom.Text;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DepartingArrivingDialogFragment extends android.support.v4.app.DialogFragment {

  @Inject RxBus rxBus;
  @BindView(R.id.picker) View picker;
  @BindView(R.id.timeTabs) TabLayout tabLayout;
  @BindView(R.id.dateSpinner) ViewPager viewPager;
  @BindView(R.id.dateSpinnerText) TextView dateSpinnerText;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((ScheduleExplorerActivity) getActivity()).getComponent().inject(this);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View view = inflater.inflate(R.layout.fragment_departing_arriving_selector, null);
    ButterKnife.bind(this, view);

    viewPager.setAdapter(new CustomAdapter(getContext()));
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
        rxBus.send(new RxMessage(RxMessageKeys.TIME_SELECTED, new Date()));
        getDialog().dismiss();
      }
    });

    return builder.create();
  }

  public class CustomAdapter extends PagerAdapter {
    private Date today;
    private Date now;
    private Context context;

    public CustomAdapter(Context context) {
      today = new Date();
      now = new Date();
      this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      Log.d("poop", "setting date spinner etxt");
      dateSpinnerText.setText(today.toString());
      container.addView(dateSpinnerText);
      return dateSpinnerText;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
      return POSITION_NONE;
    }

    @Override
    public int getCount() {
      return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
      return view == object;
    }
  }
}
