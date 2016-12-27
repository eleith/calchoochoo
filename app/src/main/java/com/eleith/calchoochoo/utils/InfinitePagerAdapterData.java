package com.eleith.calchoochoo.utils;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

public class InfinitePagerAdapterData<T> {
  private T[] dataArray;
  private T temporarySavedData;
  private Boolean stateChanged = false;

  public InfinitePagerAdapterData(final ViewPager viewPager, T[] dataArray) {
    setDataArray(dataArray);
    setViewPager(viewPager);
  }

  public void setDataArray(T[] dataArray) {
    this.dataArray = dataArray;
  }

  public void setViewPager(final ViewPager viewPager) {
    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
        if (!stateChanged) {
          if (position == dataArray.length - 1) {
            shiftRight();
          } else if (position == 0) {
            shiftLeft();
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
            viewPager.getAdapter().notifyDataSetChanged();
            viewPager.setCurrentItem(dataArray.length / 2, false);
            stateChanged = false;
          }
        }
      }
    });
  }

  public void shiftRight() {
    int size = dataArray.length;
    temporarySavedData = dataArray[0];

    for (int i = 0; i < size - 1; i++) {
      dataArray[i] = dataArray[i + 1];
    }

    dataArray[size - 1] = getNextData();
    stateChanged = true;
  }

  public T getNextData() {
    return temporarySavedData;
  }

  public void shiftLeft() {
    int size = dataArray.length;
    temporarySavedData = dataArray[size - 1];

    for (int i = dataArray.length - 1; i > 0; i--) {
      dataArray[i] = dataArray[i - 1];
    }

    dataArray[0] = getPreviousData();
    stateChanged = true;
  }

  public T getPreviousData() {
    return temporarySavedData;
  }

  public T getData(int position) {
    return dataArray[position];
  }

  public int getDataSize() {
    return dataArray.length;
  }

  public String getTextFor(int position) {
    return dataArray[position].toString();
  }
}
