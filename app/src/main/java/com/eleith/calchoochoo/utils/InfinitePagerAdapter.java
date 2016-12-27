package com.eleith.calchoochoo.utils;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class InfinitePagerAdapter extends PagerAdapter {
  private Context context;
  private InfinitePagerAdapterData pagerAdapterData;

  public InfinitePagerAdapter(Context context, InfinitePagerAdapterData pagerAdapterData) {
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

  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    TextView textView = new TextView(context);
    textView.setText(pagerAdapterData.getTextFor(position));
    container.addView(textView);
    return textView;
  }

  @Override
  public boolean isViewFromObject(View view, Object obj) {
    return view == obj;
  }
}
