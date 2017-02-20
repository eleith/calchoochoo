package com.eleith.calchoochoo.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class InfinitePager extends ViewPager{
  private Boolean stateChanged = false;
  private InfinitePagerAdapter infinitePagerAdapter;

  public InfinitePager(Context context) {
    super(context);
  }

  public InfinitePager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setOnItemClickListener(OnClickListener onClickListener) {
    infinitePagerAdapter.setOnClickListener(onClickListener);
  }

  public void setInfinitePagerData(final InfinitePagerData infinitePagerData) {
    infinitePagerAdapter = new InfinitePagerAdapter(getContext(), infinitePagerData);
    setAdapter(infinitePagerAdapter);
    setCurrentItem(infinitePagerData.getDataSize() / 2, false);

    addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
        if (!stateChanged) {
          if (position == infinitePagerData.getDataSize() - 1) {
            infinitePagerData.shiftRight();
            stateChanged = true;
          } else if (position == 0) {
            infinitePagerData.shiftLeft();
            stateChanged = true;
          }
        }
      }

      @Override
      public void onPageScrolled(int arg0, float arg1, int arg2) {

      }

      @Override
      public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
          if (stateChanged) {
            getAdapter().notifyDataSetChanged();
            setCurrentItem(infinitePagerData.getDataSize() / 2, false);
            stateChanged = false;
          }
        }
      }
    });
  }
}
