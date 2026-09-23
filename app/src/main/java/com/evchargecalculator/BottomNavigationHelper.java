package com.evchargecalculator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Common bottom navigation for all primary and future app screens. */
public final class BottomNavigationHelper {
  private BottomNavigationHelper() {}

  public static View create(Activity activity, boolean dark, int selected) {
    LinearLayout bar = new LinearLayout(activity);
    bar.setOrientation(LinearLayout.HORIZONTAL);
    bar.setGravity(Gravity.CENTER);
    bar.setPadding(dp(activity, 6), dp(activity, 6), dp(activity, 6), dp(activity, 6));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(dark ? Color.rgb(21, 31, 42) : Color.WHITE);
    bg.setCornerRadius(dp(activity, 28));
    bg.setStroke(dp(activity, 1), dark ? Color.rgb(48, 64, 84) : Color.rgb(225, 231, 238));
    bar.setBackground(bg);
    bar.setElevation(dp(activity, 10));

    addItem(activity, bar, "⚡", "Cargar", 0, selected, dark, v -> {
      if (selected != 0) {
        Intent i = new Intent(activity, PersistentMainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(i);
      }
    });
    addItem(activity, bar, "🔋", "Coste", 1, selected, dark, v -> {
      if (selected != 1) {
        Intent i = new Intent(activity, ElectricVsCombustionActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(i);
      }
    });
    addItem(activity, bar, "🚗", "Coches", 2, selected, dark, v -> {
      if (selected != 2) {
        Intent i = new Intent(activity, CompararCochesActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(i);
      }
    });
    addItem(activity, bar, "⋮", "Más", 3, selected, dark, v ->
        AppMenuHelper.show(activity, v, new AppMenuHelper.Listener() {
          public boolean isDark() {
            return dark;
          }

          public void setDark(boolean value) {
            if (value != dark) {
              activity.getSharedPreferences("ev_charge_calculator", Activity.MODE_PRIVATE)
                  .edit().putBoolean("dark_theme", value).apply();
              activity.recreate();
            }
          }
        }));

    return bar;
  }

  private static void addItem(
      Activity activity,
      LinearLayout bar,
      String icon,
      String label,
      int index,
      int selected,
      boolean dark,
      View.OnClickListener listener) {
    LinearLayout item = new LinearLayout(activity);
    item.setOrientation(LinearLayout.VERTICAL);
    item.setGravity(Gravity.CENTER);
    item.setClickable(true);
    item.setFocusable(true);
    item.setOnClickListener(listener);
    item.setContentDescription(label);

    TextView iconView = new TextView(activity);
    iconView.setText(icon);
    iconView.setTextSize(index == 3 ? 25 : 20);
    iconView.setGravity(Gravity.CENTER);
    iconView.setIncludeFontPadding(false);

    TextView labelView = new TextView(activity);
    labelView.setText(LanguageManager.t(activity, label));
    labelView.setTextSize(11);
    labelView.setTypeface(null, index == selected ? 1 : 0);
    labelView.setGravity(Gravity.CENTER);
    labelView.setIncludeFontPadding(false);

    int active = dark ? Color.rgb(105, 175, 255) : Color.rgb(46, 107, 255);
    int inactive = dark ? Color.rgb(180, 190, 205) : Color.rgb(90, 111, 137);
    iconView.setTextColor(index == selected ? active : inactive);
    labelView.setTextColor(index == selected ? active : inactive);

    item.addView(iconView, new LinearLayout.LayoutParams(-1, dp(activity, 30)));
    item.addView(labelView, new LinearLayout.LayoutParams(-1, dp(activity, 20)));
    bar.addView(item, new LinearLayout.LayoutParams(0, 54, 1));
  }

  private static int dp(Activity activity, int value) {
    return (int) (value * activity.getResources().getDisplayMetrics().density + 0.5f);
  }
}
