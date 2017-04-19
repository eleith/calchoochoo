package com.eleith.calchoochoo;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.eleith.calchoochoo.utils.RxBus;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessage;
import com.eleith.calchoochoo.utils.RxBusMessage.RxMessageKeys;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooChooFab {
  private RxBus rxBus;

  @BindView(R.id.activityFloatingActionButton)
  FloatingActionButton floatingActionButton;

  @OnClick(R.id.activityFloatingActionButton)
  void onFabClicked() {
    rxBus.send(new RxMessage(RxMessageKeys.FAB_CLICKED));
  }

  ChooChooFab(Activity activity, RxBus rxBus, View view) {
    ButterKnife.bind(this, view);
    this.rxBus = rxBus;
  }

  public void setImageDrawable(Drawable drawable) {
    floatingActionButton.setImageDrawable(drawable);
  }

  public void setBackgroundTintList(ColorStateList colorStateList) {
    floatingActionButton.setBackgroundTintList(colorStateList);
  }

  public void hide() {
    floatingActionButton.setVisibility(View.GONE);
  }

  public void show() {
    floatingActionButton.setVisibility(View.VISIBLE);
  }
}
