package com.evchargecalculator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
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

  private static final long DEFAULT_CROSSFADE_DURATION_MS = 280L;

  private String appliedLanguage;
  private boolean appliedDarkTheme;
  private String appliedCurrency;

  private FrameLayout hostRoot;
  private FrameLayout contentContainer;
  private FrameLayout navigationContainer;
  private View currentContentView;
  private AnimatorSet contentTransition;

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
    installContent(view, 0);
  }

  /** Crossfades the outgoing and incoming central screens while the bottom bar stays fixed. */
  protected void setContentViewWithFade(View view, long durationMs) {
    installContent(view, durationMs > 0 ? durationMs : DEFAULT_CROSSFADE_DURATION_MS);
  }

  private void installContent(View view, long fadeDurationMs) {
    if (view == null) {
      throw new IllegalArgumentException("Content view must not be null");
    }

    ensureHostCreated();

    if (view == currentContentView && contentContainer.indexOfChild(view) >= 0) {
      refreshBottomNavigation();
      return;
    }

    // Stop an interrupted transition cleanly before starting another one.
    if (contentTransition != null) {
      contentTransition.cancel();
      contentTransition = null;
    }

    View outgoing = currentContentView;
    for (int i = contentContainer.getChildCount() - 1; i >= 0; i--) {
      View child = contentContainer.getChildAt(i);
      if (child != outgoing) {
        contentContainer.removeViewAt(i);
      }
    }
    if (outgoing != null && contentContainer.indexOfChild(outgoing) >= 0) {
      outgoing.setAlpha(1f);
    }

    // A cached screen may still be attached to another parent.
    if (view.getParent() instanceof ViewGroup) {
      ((ViewGroup) view.getParent()).removeView(view);
    }

    addBottomScrollSpace(view, dp(48));
    if (fadeDurationMs > 0 && outgoing != null
        && contentContainer.indexOfChild(outgoing) >= 0) {
      view.setAlpha(0f);
      contentContainer.addView(view, new FrameLayout.LayoutParams(-1, -1));

      ObjectAnimator fadeOut = ObjectAnimator.ofFloat(outgoing, View.ALPHA, 1f, 0f);
      ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f);
      contentTransition = new AnimatorSet();
      contentTransition.playTogether(fadeOut, fadeIn);
      contentTransition.setDuration(fadeDurationMs);
      contentTransition.setInterpolator(new AccelerateDecelerateInterpolator());
      contentTransition.addListener(new AnimatorListenerAdapter() {
        private boolean cancelled;

        @Override
        public void onAnimationCancel(Animator animation) {
          cancelled = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
          if (!cancelled && contentTransition == animation) {
            if (outgoing.getParent() == contentContainer) {
              contentContainer.removeView(outgoing);
            }
            outgoing.setAlpha(1f);
            view.setAlpha(1f);
            currentContentView = view;
            contentTransition = null;
          }
        }
      });
      currentContentView = view;
      contentTransition.start();
    } else {
      contentContainer.removeAllViews();
      view.setAlpha(1f);
      contentContainer.addView(view, new FrameLayout.LayoutParams(-1, -1));
      currentContentView = view;
    }

    refreshBottomNavigation();
  }

  private void refreshBottomNavigation() {
    // Keep the navigation slot fixed in the root; only refresh its selected state.
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
