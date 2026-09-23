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
    host.addView(view, new FrameLayout.LayoutParams(-1, -1));

    View bottomNavigation =
        BottomNavigationHelper.create(this, isDarkTheme(), getBottomNavigationIndex());
    FrameLayout.LayoutParams navParams =
        new FrameLayout.LayoutParams(-1, dp(56), android.view.Gravity.BOTTOM);
    navParams.setMargins(dp(10), 0, dp(10), dp(10));
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
