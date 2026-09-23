package com.evchargecalculator;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.ScrollView;

public class AppMenuApplication extends Application {
  private static final String PREFS = "ev_charge_calculator", KEY_DARK_THEME = "dark_theme";

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

}
