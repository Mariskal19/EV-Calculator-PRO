package com.evchargecalculator;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

/**
 * Common host for screens that share a fixed bottom navigation structure.
 *
 * The root, content slot and navigation slot are created once per Activity.
 * Subsequent setContentView(View) calls replace only the screen in the content
 * slot; they never replace the Activity root or move the navigation container.
 */
public abstract class BaseNavigationActivity extends Activity {

  private String appliedLanguage;
  private boolean appliedDarkTheme;
  private String appliedCurrency;

  private FrameLayout hostRoot;
  private FrameLayout contentContainer;
  private FrameLayout navigationContainer;

  /** 0 = Cargar, 1 = Coste, 2 = Buscar coches, 3 = Comparar, 4 = Más. */
  protected abstract int getBottomNavigationIndex();

  /** Whether primary-tab taps should be handled inside this Activity. */
  protected boolean handlesBottomNavigationInternally() { return false; }

  /** Called when the single-Activity host receives a primary-tab selection. */
  protected void onBottomNavigationSelected(int destination) { }

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
    if (view == null) {
      throw new IllegalArgumentException("Content view must not be null");
    }

    ensureHostCreated();

    // A screen View may be reused when returning to a previously selected tab.
    // Detach it from its old parent before placing it in the stable content slot.
    if (view.getParent() instanceof ViewGroup) {
      ((ViewGroup) view.getParent()).removeView(view);
    }
    contentContainer.removeAllViews();
    addBottomScrollSpace(view, dp(48));
    contentContainer.addView(
        view, new FrameLayout.LayoutParams(-1, -1));

    // Keep the navigation slot fixed in the root. Refresh its selected state
    // without replacing the Activity root or triggering a window transition.
    navigationContainer.removeAllViews();
    boolean dark = isDarkTheme();
    View bottomNavigation = BottomNavigationHelper.create(
        this, dark, getBottomNavigationIndex(),
        handlesBottomNavigationInternally()
            ? destination -> onBottomNavigationSelected(destination)
            : null);
    navigationContainer.addView(
        bottomNavigation, new FrameLayout.LayoutParams(-1, -1));
  }

  private void ensureHostCreated() {
    if (hostRoot != null) return;

    boolean dark = isDarkTheme();
    hostRoot = new FrameLayout(this);
    hostRoot.setBackgroundColor(dark
        ? android.graphics.Color.rgb(16, 28, 42)
        : android.graphics.Color.rgb(242, 246, 252));

    contentContainer = new FrameLayout(this);
    hostRoot.addView(
        contentContainer, new FrameLayout.LayoutParams(-1, -1));

    navigationContainer = new FrameLayout(this);
    FrameLayout.LayoutParams navParams = new FrameLayout.LayoutParams(
        -1, dp(64), android.view.Gravity.BOTTOM);
    hostRoot.addView(navigationContainer, navParams);

    // Install the Activity content exactly once. Tab changes only touch the
    // two stable slots above, so the host window itself is never relaunched.
    super.setContentView(hostRoot);
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
