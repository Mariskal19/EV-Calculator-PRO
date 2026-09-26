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

  private String appliedLanguage;
  private boolean appliedDarkTheme;
  private String appliedCurrency;

  /** 0 = Cargar, 1 = Coste, 2 = Coches, 3 = Más. */
  protected abstract int getBottomNavigationIndex();

  @Override
  protected void onResume() {
    super.onResume();
    String language = LanguageManager.getSelectedLanguage(this);
    boolean dark = isDarkTheme();
    String currency = getSharedPreferences("ev_charge_calculator", MODE_PRIVATE)
        .getString("app_currency", "EUR");

    if (appliedLanguage == null) {
      appliedLanguage = language;
      appliedDarkTheme = dark;
      appliedCurrency = currency;
      return;
    }

    // Settings are changed in another Activity. When the user presses the
    // system Back button, refresh this screen so the new preferences are
    // visible immediately instead of leaving the old UI cached underneath.
    if (!language.equals(appliedLanguage)
        || dark != appliedDarkTheme
        || !currency.equals(appliedCurrency)) {
      recreate();
    }
  }

  @Override
  public void setContentView(View view) {
    FrameLayout host = new FrameLayout(this);
    boolean dark = isDarkTheme();
    host.setBackgroundColor(dark
        ? android.graphics.Color.rgb(16, 28, 42)
        : android.graphics.Color.rgb(242, 246, 252));
    host.addView(view, new FrameLayout.LayoutParams(-1, -1));

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
