package com.evchargecalculator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Common bottom navigation for all primary and future app screens. */
public final class BottomNavigationHelper {
  private BottomNavigationHelper() {}

  public static View create(Activity activity, boolean dark, int selected) {
    LinearLayout bar = new LinearLayout(activity);
    bar.setOrientation(LinearLayout.HORIZONTAL);
    bar.setGravity(Gravity.CENTER);
    bar.setPadding(0, dp(activity, 4), 0, dp(activity, 2));

    // Google Play-style full-width navigation surface: no outer margins,
    // rounded card or border. The system navigation area uses the same surface
    // color so the bottom of the screen reads as one continuous navigation bar.
    bar.setBackgroundColor(dark ? Color.rgb(16, 28, 42) : Color.rgb(242, 246, 252));
    bar.setElevation(0);

    addItem(activity, bar, R.drawable.ic_nav_charge, "Cargar", 0, selected, dark, v -> {
      if (selected != 0) {
        Intent i = new Intent(activity, PersistentMainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(i);
      }
    });
    addItem(activity, bar, R.drawable.ic_nav_cost, "Coste", 1, selected, dark, v -> {
      if (selected != 1) {
        Intent i = new Intent(activity, ElectricVsCombustionActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(i);
      }
    });
    addItem(activity, bar, R.drawable.ic_nav_car_modern, "Coches", 2, selected, dark, v -> {
      if (selected != 2) {
        Intent i = new Intent(activity, CompararCochesActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(i);
      }
    });
    addItem(activity, bar, R.drawable.ic_nav_more, "Más", 3, selected, dark, v ->
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
      int icon,
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

    // Google Play-style selected capsule behind the icon.
    FrameLayout iconSlot = new FrameLayout(activity);

    ImageView iconView = new ImageView(activity);
    iconView.setImageResource(icon);
    iconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

    // The Coches PNG is square while the artwork is intentionally wider.
    if (index == 2) {
      iconView.setScaleX(1.22f);
    }

    int active = Color.rgb(0, 125, 255);
    int inactive = dark ? Color.rgb(180, 190, 205) : Color.rgb(75, 80, 88);

    if (index == selected) {
      GradientDrawable pill = new GradientDrawable();
      pill.setColor(dark ? Color.rgb(35, 67, 91) : Color.rgb(207, 233, 255));
      pill.setCornerRadius(dp(activity, 24));
      iconSlot.setBackground(pill);
    }

    iconView.setColorFilter(new PorterDuffColorFilter(
        index == selected ? active : inactive, PorterDuff.Mode.SRC_IN));

    iconSlot.addView(iconView, new FrameLayout.LayoutParams(
        dp(activity, 24), dp(activity, 24), Gravity.CENTER));

    TextView labelView = new TextView(activity);
    labelView.setText(LanguageManager.t(activity, label));
    labelView.setTextSize(12);
    labelView.setTypeface(null, index == selected ? 1 : 0);
    labelView.setGravity(Gravity.CENTER);
    labelView.setIncludeFontPadding(false);
    // The Más label sits slightly closer to its icon in dark theme;
    // lower it by 1dp to match the other navigation labels.
    if (dark && index == 3) {
      labelView.setTranslationY(dp(activity, 1));
    }
    labelView.setTextColor(index == selected ? active : inactive);

    item.addView(iconSlot, new LinearLayout.LayoutParams(
        dp(activity, 64), dp(activity, 30), 0));
    item.addView(labelView, new LinearLayout.LayoutParams(
        -1, dp(activity, 20), 0));
    bar.addView(item, new LinearLayout.LayoutParams(0, -1, 1));
  }

  private static int dp(Activity activity, int value) {
    return (int) (value * activity.getResources().getDisplayMetrics().density + 0.5f);
  }
}
