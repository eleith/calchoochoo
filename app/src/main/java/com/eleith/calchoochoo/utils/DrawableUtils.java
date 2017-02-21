package com.eleith.calchoochoo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;

public class DrawableUtils {
  public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId, float scale) {
    Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
    int scaledWidth = (int)(drawable.getIntrinsicWidth() * scale);
    int scaledHeight = (int)(drawable.getIntrinsicHeight() * scale);

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      drawable = (DrawableCompat.wrap(drawable)).mutate();
    }

    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);

    Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false);
    bitmap.recycle();

    return bitmapResized;
  }

  public static Bitmap getBitmapCircle(int size, int strokeWidth, int fillColor, int strokeColor) {
    final Paint paint = new Paint();
    Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(output);

    paint.setAntiAlias(true);
    paint.setColor(fillColor);
    paint.setStyle(Paint.Style.FILL);
    canvas.drawCircle(size / 2, size / 2, size / 4 - strokeWidth, paint);

    paint.setColor(strokeColor);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(strokeWidth);
    canvas.drawCircle(size / 2, size / 2, size / 4, paint);

    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

    return output;
  }
}
