package com.eleith.calchoochoo.utils;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.TypedValue;

public class ColorUtils {
  @ColorInt
  public static int getThemeColor
      (
          @NonNull final Context context,
          @AttrRes final int attributeColor
      ) {
    final TypedValue value = new TypedValue();
    context.getTheme().resolveAttribute(attributeColor, value, true);
    return value.data;
  }
}
