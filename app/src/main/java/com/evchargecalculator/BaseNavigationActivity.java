package com.evchargecalculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Common host for app screens that use the permanent bottom navigation.
 *
 * Any Activity that extends this class and returns its section index from
 * getBottomNavigationIndex() automatically receives the same navigation bar
 * whenever it calls setContentView().
 */
public abstract class BaseNavigationActivity extends Activity {

  /** 0 = Cargar, 1 = Coste, 2 = Coches, 3 = Más. */
  protected abstract int getBottomNavigationIndex();

  @Override
  public void setContentView(View view) {
    FrameLayout host = new FrameLayout(this);
    boolean dark = isDarkTheme();
    host.setBackgroundColor(dark
        ? android.graphics.Color.rgb(16, 28, 42)
        : android.graphics.Color.rgb(242, 246, 252));
    // Reserve the navigation bar's 64dp inside the content host. This makes
    // the bar the actual bottom limit of every scrollable screen, rather than
    // overlaying and hiding the last cards. Screen-level spacers remain the
    // only visual gap above the navigation bar.
    FrameLayout.LayoutParams contentParams = new FrameLayout.LayoutParams(-1, -1);
    contentParams.bottomMargin = dp(64);
    host.addView(view, contentParams);

    View bottomNavigation =
        BottomNavigationHelper.create(this, dark, getBottomNavigationIndex());
    FrameLayout.LayoutParams navParams =
        new FrameLayout.LayoutParams(-1, dp(64), android.view.Gravity.BOTTOM);
    host.addView(bottomNavigation, navParams);

    super.setContentView(host);
  }

  /** Screens can override this when their theme state is not stored in the common preference. */
  protected boolean isDarkTheme() {
    return getSharedPreferences("ev_charge_calculator", MODE_PRIVATE)
        .getBoolean("dark_theme", false);
  }

  private int dp(int value) {
    return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
  }
}
