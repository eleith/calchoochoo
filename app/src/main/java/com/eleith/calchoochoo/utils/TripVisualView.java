package com.eleith.calchoochoo.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.eleith.calchoochoo.R;

public class TripVisualView extends View {

  private Paint paint;
  private String tripType;
  private int viewWidth;
  private int viewHeight;
  private float tripLineWidth;
  private float tripLineRadius;
  private Canvas canvas;
  private Context context;

  static float TRIP_LINE_WIDTH_DEFAULT = 10.0f;
  static float TRIP_LINE_CIRCLE_RADIUS_DEFAULT = 20.0f;
  static String TRIP_TYPE_DEFAULT = "middle";

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
      tripType = typedArray.getString(R.styleable.TripVisualView_lineType);
      tripLineRadius = typedArray.getDimension(R.styleable.TripVisualView_lineCircleRadius, TRIP_LINE_CIRCLE_RADIUS_DEFAULT);
      tripLineWidth = typedArray.getDimension(R.styleable.TripVisualView_lineWidth, TRIP_LINE_WIDTH_DEFAULT);

      if (tripType == null) {
        tripType = TRIP_TYPE_DEFAULT;
      }
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
    paint.setColor(getResources().getColor(android.support.v7.appcompat.R.color.accent_material_light, context.getTheme()));
    switch (tripType) {
      case "source":
        canvas.drawCircle(viewWidth / 2, viewHeight / 2, tripLineRadius, paint);
        canvas.drawRect(viewWidth / 2 - tripLineWidth, viewHeight / 2 + tripLineRadius + 2, viewWidth / 2 + tripLineWidth, viewHeight, paint);
        break;
      case "destination":
        canvas.drawCircle(viewWidth / 2, viewHeight / 2, tripLineRadius, paint);
        canvas.drawRect(viewWidth / 2 - tripLineWidth, 0, viewWidth / 2 + tripLineWidth, viewHeight / 2 - tripLineRadius - 2, paint);
        break;
      default:
        canvas.drawRect(viewWidth / 2 - tripLineWidth, 0, viewWidth / 2 + tripLineWidth, viewHeight, paint);
        break;
    }
  }
}
