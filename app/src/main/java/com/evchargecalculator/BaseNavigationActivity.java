package com.evchargecalculator;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

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

    /*
     * Keep the navigation over the content, as it was originally designed,
     * but give scrollable screens enough extra scroll range to bring their
     * last real item above the fixed navigation bar. This avoids both:
     *  - content being hidden underneath the bar;
     *  - a visible 64dp empty band between the last card and the bar.
     */
    addBottomScrollSpace(view, dp(64));

    FrameLayout.LayoutParams contentParams =
        new FrameLayout.LayoutParams(-1, -1);
    host.addView(view, contentParams);

    View bottomNavigation =
        BottomNavigationHelper.create(this, dark, getBottomNavigationIndex());
    FrameLayout.LayoutParams navParams =
        new FrameLayout.LayoutParams(-1, dp(64), android.view.Gravity.BOTTOM);
    host.addView(bottomNavigation, navParams);

    super.setContentView(host);
  }

  /**
   * Ensures every ScrollView has enough bottom scroll range for the fixed
   * navigation bar without creating a visible gap outside the scroll view.
   */
  private void addBottomScrollSpace(View view, int minBottomPadding) {
    if (view instanceof ScrollView) {
      ScrollView scrollView = (ScrollView) view;
      scrollView.setPadding(
          scrollView.getPaddingLeft(),
          scrollView.getPaddingTop(),
          scrollView.getPaddingRight(),
          Math.max(scrollView.getPaddingBottom(), minBottomPadding));
    }

    if (view instanceof ViewGroup) {
      ViewGroup group = (ViewGroup) view;
      for (int i = 0; i < group.getChildCount(); i++) {
        addBottomScrollSpace(group.getChildAt(i), minBottomPadding);
      }
    }
  }

  protected boolean isDarkTheme() {
    return getSharedPreferences("ev_charge_calculator", MODE_PRIVATE)
        .getBoolean("dark_theme", false);
  }

  private int dp(int value) {
    return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
  }
}
