package com.evchargecalculator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

public class PrincipalActivity extends BaseNavigationActivity {
  @Override
  protected int getBottomNavigationIndex() {
    return 0;
  }

  private boolean dark;
  private String displayedLanguage;
  private final int blue = Color.rgb(46, 107, 255),
      white = Color.rgb(22, 42, 63),
      secondary = Color.rgb(90, 111, 137),
      cardLight = Color.argb(245, 255, 255, 255),
      cardDark = Color.argb(220, 21, 31, 42),
      lightBg = Color.rgb(244, 248, 255),
      darkBg = Color.rgb(7, 19, 28);
  private static final String PRIVACY_URL =
      "https://mariskal19.github.io/EV-Calculator-PRO-Privacy/";
  private static final String PREFS = "ev_charge_calculator", KEY_DARK_THEME = "dark_theme";

  @Override
  protected void onCreate(Bundle b) {
    super.onCreate(b);
    SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
    dark =
        p.contains(KEY_DARK_THEME)
            ? p.getBoolean(KEY_DARK_THEME, false)
            : (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    displayedLanguage = LanguageManager.getSelectedLanguage(this);
    build();
    EdgeToEdgeHelper.apply(this, dark);
  }

  @Override
  protected void onResume() {
    super.onResume();
    SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
    if (p.contains(KEY_DARK_THEME)) {
      boolean d = p.getBoolean(KEY_DARK_THEME, false);
      if (d != dark) {
        dark = d;
        recreate();
        return;
      }
    }
    LanguageManager.applyStored(this);
    String currentLanguage = LanguageManager.getSelectedLanguage(this);
    if (!currentLanguage.equals(displayedLanguage)) {
      displayedLanguage = currentLanguage;
      build();
    }
  }

  private void build() {
    FrameLayout frame = new FrameLayout(this);
    PremiumBackgroundView bgv = new PremiumBackgroundView(this);
    bgv.setDark(dark);
    bgv.setShowVehicle(false);
    frame.addView(bgv, new FrameLayout.LayoutParams(-1, -1));
    ScrollView scroll = new ScrollView(this);
    scroll.setFillViewport(true);
    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(0, dp(0), 0, dp(84));
    scroll.addView(root);
    frame.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
    setContentView(frame);
    FrameLayout hero = new FrameLayout(this);
    hero.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(260)));
    ImageView header = new ImageView(this);
    header.setImageResource(R.drawable.cabecera_ev_calculator);
    header.setScaleType(ImageView.ScaleType.CENTER_CROP);
    header.setTranslationY(-dp(10));
    hero.addView(header, new FrameLayout.LayoutParams(-1, -1));
    View fade = new View(this);
    fade.setBackground(
        new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[] {
              Color.argb(200, 0, 0, 0),
              Color.argb(80, 0, 0, 0),
              Color.argb(20, 0, 0, 0),
              Color.argb(0, 0, 0, 0)
            }));
    hero.addView(fade, new FrameLayout.LayoutParams(-1, dp(170), Gravity.TOP));
    TextView title = tv("EV Calculator PRO", 22, Color.WHITE);
    title.setTypeface(null, 1);
    title.setGravity(Gravity.CENTER);
    title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    title.setShadowLayer(dp(4), 0, dp(2), Color.argb(90, 0, 0, 0));
    FrameLayout.LayoutParams tp = new FrameLayout.LayoutParams(-1, dp(48));
    tp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
    tp.leftMargin = dp(40);
    tp.rightMargin = dp(40);
    tp.topMargin = dp(12);
    hero.addView(title, tp);
    root.addView(hero);
    LinearLayout card = new LinearLayout(this);
    card.setOrientation(LinearLayout.VERTICAL);
    card.setPadding(dp(18), dp(18), dp(18), dp(18));
    card.setBackground(bg(dark ? cardDark : cardLight, 22));
    LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2);
    cp.setMargins(dp(12), -dp(26), dp(12), 0);
    card.setLayoutParams(cp);
    TextView heading = tv("Herramientas", 18, textColor());
    heading.setTypeface(null, 1);
    card.addView(heading);
    TextView description =
        tv("Calcula y planifica la carga de tu vehículo eléctrico.", 14, subColor());
    description.setPadding(0, dp(10), 0, dp(14));
    card.addView(description);
    TextView calculator = tv("⚡  EV Charge Calculator", 17, Color.WHITE);
    calculator.setGravity(Gravity.CENTER_VERTICAL);
    calculator.setTypeface(null, 1);
    calculator.setPadding(dp(18), 0, dp(18), 0);
    calculator.setBackground(bg(blue, 18));
    calculator.setOnClickListener(
        v -> startActivity(new Intent(this, PersistentMainActivity.class)));
    card.addView(calculator, new LinearLayout.LayoutParams(-1, dp(62)));
    Space between = new Space(this);
    card.addView(between, new LinearLayout.LayoutParams(1, dp(10)));
    TextView comparison = tv("⚡⛽  Electric Vs Combustion Calculator", 17, Color.WHITE);
    comparison.setGravity(Gravity.CENTER_VERTICAL);
    comparison.setTypeface(null, 1);
    comparison.setPadding(dp(18), 0, dp(18), 0);
    comparison.setBackground(bg(blue, 18));
    comparison.setOnClickListener(
        v -> startActivity(new Intent(this, ElectricVsCombustionActivity.class)));
    card.addView(comparison, new LinearLayout.LayoutParams(-1, dp(62)));
    Space betweenCompare = new Space(this);
    card.addView(betweenCompare, new LinearLayout.LayoutParams(1, dp(10)));
    TextView compareCars = tv("⚖  " + LanguageManager.t(this, "Comparar coches"), 17, Color.WHITE);
    compareCars.setGravity(Gravity.CENTER_VERTICAL);
    compareCars.setTypeface(null, 1);
    compareCars.setPadding(dp(18), 0, dp(18), 0);
    compareCars.setBackground(bg(blue, 18));
    compareCars.setOnClickListener(
        v -> startActivity(new Intent(this, CompararCochesActivity.class)));
    card.addView(compareCars, new LinearLayout.LayoutParams(-1, dp(62)));
    root.addView(card);
    Space bottomSpace = new Space(this);
    root.addView(bottomSpace, new LinearLayout.LayoutParams(1, 0, 1));
    TextView privacy = tv("Política de privacidad", 13, dark ? Color.rgb(105, 175, 255) : blue);
    privacy.setGravity(Gravity.CENTER);
    privacy.setTypeface(null, 1);
    privacy.setPadding(0, dp(4), 0, dp(4));
    privacy.setContentDescription(LanguageManager.t(this, "Política de privacidad"));
    privacy.setOnClickListener(
        v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_URL))));
    root.addView(privacy, new LinearLayout.LayoutParams(-1, dp(34)));
    String ver = "1.0.2";
    try {
      ver = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
    } catch (Exception ignored) {
    }
    if (ver.startsWith("v") || ver.startsWith("V")) ver = ver.substring(1);
    TextView foot = tv("Powered by EV Calculator · v" + ver, 12, subColor());
    foot.setGravity(Gravity.CENTER);
    root.addView(foot, new LinearLayout.LayoutParams(-1, dp(28)));
  }

  private TextView tv(String s, int sp, int c) {
    TextView t = new TextView(this);
    t.setText(LanguageManager.t(this, s));
    t.setTextSize(sp);
    t.setTextColor(c);
    return t;
  }

  private int textColor() {
    return dark ? Color.rgb(245, 248, 255) : white;
  }

  private int subColor() {
    return dark ? Color.rgb(170, 183, 204) : secondary;
  }

  private GradientDrawable bg(int c, float r) {
    GradientDrawable g = new GradientDrawable();
    g.setColor(c);
    g.setCornerRadius(r);
    return g;
  }

  private int dp(int n) {
    return (int) (n * getResources().getDisplayMetrics().density + .5f);
  }
}
