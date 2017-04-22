package com.eleith.calchoochoo.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.eleith.calchoochoo.R;

public class TripVisualView extends View {

  private Paint paint;
  private int tripType;
  private int viewWidth;
  private int viewHeight;
  private float tripLineWidth;
  private float tripLineRadius;
  private Canvas canvas;
  private Context context;

  private static float TRIP_LINE_WIDTH_DEFAULT = 10.0f;
  private static float TRIP_LINE_CIRCLE_RADIUS_DEFAULT = 20.0f;
  private final static int TRIP_TYPE_DEFAULT = 2;
  private final static int ITEM_TYPE_SOURCE = 0;
  private final static int ITEM_TYPE_DESTINATION = 1;
  private final static int ITEM_TYPE_MIDDLE = 2;

  public TripVisualView(Context context) {
    super(context);
    initializeView(context);
  }

  public TripVisualView(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
    readAttributes(context, attributeSet);
    initializeView(context);
  }

  public TripVisualView(Context context, AttributeSet attributeSet, int defStyle) {
    super(context, attributeSet, defStyle);
    readAttributes(context, attributeSet);
    initializeView(context);
  }

  private void initializeView(Context context) {
    this.context = context;
    paint = new Paint();
  }

  private void readAttributes(Context context, AttributeSet attributeSet) {
    TypedArray typedArray = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.TripVisualView, 0, 0);

    try {
      tripType = typedArray.getInt(R.styleable.TripVisualView_lineType, ITEM_TYPE_MIDDLE);
      tripLineRadius = typedArray.getDimension(R.styleable.TripVisualView_lineCircleRadius, TRIP_LINE_CIRCLE_RADIUS_DEFAULT);
      tripLineWidth = typedArray.getDimension(R.styleable.TripVisualView_lineWidth, TRIP_LINE_WIDTH_DEFAULT);
    } finally {
      typedArray.recycle();
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    this.canvas = canvas;

    viewWidth = getWidth();
    viewHeight = getHeight();

    drawTripLine();
  }

  private void drawTripLine() {
    paint.setColor(ContextCompat.getColor(context, android.support.v7.appcompat.R.color.accent_material_light));
    switch (tripType) {
      case ITEM_TYPE_SOURCE:
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(tripLineWidth);
        canvas.drawCircle(viewWidth / 2, viewHeight / 2, tripLineRadius, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(viewWidth / 2 - tripLineWidth, viewHeight / 2 + tripLineRadius + tripLineWidth, viewWidth / 2 + tripLineWidth, viewHeight, paint);
        break;
      case ITEM_TYPE_DESTINATION:
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(tripLineWidth);
        canvas.drawCircle(viewWidth / 2, viewHeight / 2, tripLineRadius, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(viewWidth / 2 - tripLineWidth, 0, viewWidth / 2 + tripLineWidth, viewHeight / 2 - tripLineRadius - tripLineWidth, paint);
        break;
      default:
        canvas.drawCircle(viewWidth / 2, viewHeight / 2, tripLineRadius, paint);
        canvas.drawRect(viewWidth / 2 - tripLineWidth, 0, viewWidth / 2 + tripLineWidth, viewHeight, paint);
        break;
    }
  }
}
