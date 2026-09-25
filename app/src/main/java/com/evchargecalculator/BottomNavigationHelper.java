package com.evchargecalculator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.Gravity;
import android.view.View;
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
    bar.setPadding(dp(activity, 5), dp(activity, 4), dp(activity, 5), dp(activity, 4));
    bar.setMinimumHeight(dp(activity, 76));

    // Barra tipo tarjeta de la referencia: blanca, redondeada y limpia.
    GradientDrawable bg = new GradientDrawable();
    bg.setColor(dark ? Color.rgb(21, 31, 42) : Color.WHITE);
    bg.setCornerRadius(dp(activity, 20));
    bg.setStroke(dp(activity, 1), dark ? Color.rgb(48, 64, 84) : Color.rgb(235, 239, 243));
    bar.setBackground(bg);
    bar.setElevation(dp(activity, 10));

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
    addItem(activity, bar, R.drawable.ic_nav_car, "Coches", 2, selected, dark, v -> {
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

    ImageView iconView = new ImageView(activity);
    iconView.setImageResource(icon);
    iconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

    // El icono de Coches de la opción 2 es más ancho que alto.
    if (index == 2) {
      iconView.setScaleX(1.22f);
    }

    TextView labelView = new TextView(activity);
    labelView.setText(LanguageManager.t(activity, label));
    labelView.setTextSize(15);
    labelView.setTypeface(null, index == selected ? 1 : 0);
    labelView.setGravity(Gravity.CENTER);
    labelView.setIncludeFontPadding(false);

    // Colores de la referencia: verde para la opción activa y azul marino para las demás.
    int active = dark ? Color.rgb(96, 181, 82) : Color.rgb(58, 151, 50);
    int inactive = dark ? Color.rgb(205, 214, 224) : Color.rgb(31, 52, 70);
    int color = index == selected ? active : inactive;
    iconView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
    labelView.setTextColor(color);

    item.addView(iconView, new LinearLayout.LayoutParams(-1, dp(activity, 45)));
    item.addView(labelView, new LinearLayout.LayoutParams(-1, dp(activity, 21)));
    bar.addView(item, new LinearLayout.LayoutParams(0, dp(activity, 68), 1));

    // Separadores verticales finos, como en la imagen de referencia.
    if (index < 3) {
      View separator = new View(activity);
      separator.setBackgroundColor(dark ? Color.rgb(55, 72, 89) : Color.rgb(229, 233, 237));
      LinearLayout.LayoutParams separatorParams =
          new LinearLayout.LayoutParams(dp(activity, 1), dp(activity, 58));
      separatorParams.gravity = Gravity.CENTER_VERTICAL;
      bar.addView(separator, separatorParams);
    }
  }

  private static int dp(Activity activity, int value) {
    return (int) (value * activity.getResources().getDisplayMetrics().density + 0.5f);
  }
}
