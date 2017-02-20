package com.eleith.calchoochoo.utils;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class InfinitePagerAdapter extends PagerAdapter {
  private Context context;
  private InfinitePagerData pagerAdapterData;
  private View.OnClickListener listener;

  public InfinitePagerAdapter(Context context, InfinitePagerData pagerAdapterData) {
    this.pagerAdapterData = pagerAdapterData;
    this.context = context;
  }

  @Override
  public int getItemPosition(Object object) {
    return PagerAdapter.POSITION_NONE;
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
  }

  @Override
  public int getCount() {
    return pagerAdapterData.getDataSize();
  }

  public void setOnClickListener(View.OnClickListener listener) {
    this.listener = listener;
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    TextView textView = new TextView(context);
    textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    textView.setText(pagerAdapterData.getTextFor(position));
    textView.setGravity(Gravity.CENTER);
    textView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        listener.onClick(v);
      }
    });
    container.addView(textView);
    return textView;
  }

  @Override
  public boolean isViewFromObject(View view, Object obj) {
    return view == obj;
  }
}
