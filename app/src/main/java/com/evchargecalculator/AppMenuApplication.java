package com.evchargecalculator;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class AppMenuApplication extends Application {
  private static final String PREFS = "ev_charge_calculator", KEY_DARK_THEME = "dark_theme";
  private static final int MENU_ID = 0x7ECAFE;

  @Override
  public void onCreate() {
    super.onCreate();
    LanguageManager.applyStored(this);
    CurrencyRateManager.refreshIfNeeded(this);
    registerActivityLifecycleCallbacks(
        new ActivityLifecycleCallbacks() {
          @Override
          public void onActivityCreated(Activity activity, Bundle state) {
            activity
                .getWindow()
                .getDecorView()
                .post(
                    () -> {
                      LanguageManager.applyStored(activity);
                      protectSystemBars(activity);
                      LanguageManager.translateViews(activity);
                    });
          }

          @Override
          public void onActivityStarted(Activity activity) {}

          @Override
          public void onActivityResumed(Activity activity) {
            activity
                .getWindow()
                .getDecorView()
                .post(
                    () -> {
                      protectSystemBars(activity);
                    });
          }

          @Override
          public void onActivityPaused(Activity activity) {}

          @Override
          public void onActivityStopped(Activity activity) {}

          @Override
          public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

          @Override
          public void onActivityDestroyed(Activity activity) {}
        });
  }

  private void protectSystemBars(Activity activity) {
    if (Build.VERSION.SDK_INT < 35) return;
    ViewGroup content = activity.findViewById(android.R.id.content);
    if (content == null) return;
    protectScrollViews(content);
  }

  private void protectScrollViews(View view) {
    if (view instanceof ScrollView) {
      ScrollView scroll = (ScrollView) view;
      if (scroll.getTag() == null) {
        int left = scroll.getPaddingLeft(),
            top = scroll.getPaddingTop(),
            right = scroll.getPaddingRight(),
            bottom = scroll.getPaddingBottom();
        scroll.setTag(new int[] {left, top, right, bottom});
        scroll.setClipToPadding(true);
        scroll.setOnApplyWindowInsetsListener(
            (v, insets) -> {
              android.graphics.Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
              int[] base = (int[]) v.getTag();
              int topInset = bars.top, bottomInset = bars.bottom;
              v.setPadding(base[0], topInset, base[2], Math.max(base[3], bottomInset));
              if (((ScrollView) v).getChildCount() > 0) {
                View child = ((ScrollView) v).getChildAt(0);
                if (child instanceof ViewGroup) {
                  ViewGroup root = (ViewGroup) child;
                  Object tag = root.getTag();
                  int rootTop = tag instanceof Integer ? (Integer) tag : 36;
                  if (!(tag instanceof Integer)) root.setTag(rootTop);
                  root.setPadding(
                      root.getPaddingLeft(),
                      Math.max(0, rootTop - topInset),
                      root.getPaddingRight(),
                      root.getPaddingBottom());
                }
              }
              return WindowInsets.CONSUMED;
            });
        scroll.requestApplyInsets();
      }
    }
    if (view instanceof ViewGroup) {
      ViewGroup group = (ViewGroup) view;
      for (int i = 0; i < group.getChildCount(); i++) protectScrollViews(group.getChildAt(i));
    }
  }

  private void installMenu(Activity activity) {
    ViewGroup content = activity.findViewById(android.R.id.content);
    if (content == null || content.findViewById(MENU_ID) != null) return;
    removeLegacyMenus(content);
    TextView menu = new TextView(activity);
    menu.setId(MENU_ID);
    menu.setText("⋮");
    menu.setTextSize(30);
    menu.setTextColor(Color.WHITE);
    menu.setGravity(Gravity.CENTER);
    menu.setIncludeFontPadding(false);
    menu.setContentDescription(LanguageManager.t(activity, "Menú de la aplicación"));
    menu.setShadowLayer(dp(activity, 4), 0, dp(activity, 2), Color.argb(90, 0, 0, 0));
    menu.setBackgroundColor(Color.TRANSPARENT);
    menu.setOnClickListener(v -> showMenu(activity, menu));
    FrameLayout.LayoutParams lp =
        new FrameLayout.LayoutParams(dp(activity, 40), dp(activity, 40), Gravity.TOP | Gravity.END);
    lp.rightMargin = dp(activity, 14);
    lp.topMargin = dp(activity, 12);
    content.addView(menu, lp);
  }

  private void removeLegacyMenus(ViewGroup parent) {
    for (int i = parent.getChildCount() - 1; i >= 0; i--) {
      View child = parent.getChildAt(i);
      CharSequence d = child.getContentDescription();
      if (d != null
          && (LanguageManager.t((Context) parent.getContext(), "Menú de la aplicación")
                  .contentEquals(d)
              || "Cambiar tema".contentEquals(d)
              || "Tema claro".contentEquals(d)
              || "Tema oscuro".contentEquals(d)
              || "Más opciones".contentEquals(d))) {
        parent.removeViewAt(i);
      } else if (child instanceof ViewGroup) removeLegacyMenus((ViewGroup) child);
    }
  }

  private void showMenu(Activity activity, View anchor) {
    SharedPreferences prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    AppMenuHelper.show(
        activity,
        anchor,
        new AppMenuHelper.Listener() {
          @Override
          public boolean isDark() {
            return prefs.getBoolean(KEY_DARK_THEME, false);
          }

          @Override
          public void setDark(boolean value) {
            prefs.edit().putBoolean(KEY_DARK_THEME, value).apply();
            activity.recreate();
          }
        });
  }

  private static int dp(Activity a, int n) {
    return (int) (n * a.getResources().getDisplayMetrics().density + .5f);
  }
}
