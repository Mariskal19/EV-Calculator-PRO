package com.evchargecalculator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

public class ElectricVsCombustionActivity extends BaseNavigationActivity {
  @Override
  protected int getBottomNavigationIndex() {
    return 1;
  }

  private boolean dark;
  private SharedPreferences prefs;
  private ScrollView scroll;
  private String lastLanguage;
  private String lastCurrency;
  private EditText distance, evConsumption, electricityPrice, fuelConsumption, fuelPrice;
  private TextView evCost,
      fuelCost,
      saving,
      savingPercent,
      evPer100,
      fuelPer100,
      electricityPriceUnit,
      fuelPriceUnit,
      headerTitle;
  private final int blue = Color.rgb(46, 107, 255),
      white = Color.rgb(22, 42, 63),
      secondary = Color.rgb(90, 111, 137),
      cardLight = Color.argb(245, 255, 255, 255),
      cardDark = Color.argb(220, 21, 31, 42),
      borderLight = Color.rgb(217, 228, 241),
      darkBg = Color.rgb(7, 19, 28);
  private static final String PREFS = "ev_charge_calculator",
      KEY_DARK_THEME = "dark_theme",
      KEY_CURRENCY = "app_currency";

  @Override
  public void onCreate(Bundle b) {
    super.onCreate(b);
    prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
    dark =
        prefs.contains(KEY_DARK_THEME)
            ? prefs.getBoolean(KEY_DARK_THEME, false)
            : (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    build();
    EdgeToEdgeHelper.apply(this, dark);
    load();
    applyCurrency();
    calculate();
    lastLanguage = LanguageManager.getSelectedLanguage(this);
    lastCurrency = prefs.getString(KEY_CURRENCY, "EUR");
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (prefs == null) prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
    if (distance != null) {
      LanguageManager.applyStored(this);
      String currentLanguage = LanguageManager.getSelectedLanguage(this);
      String currentCurrency = prefs.getString(KEY_CURRENCY, "EUR");
      boolean languageChanged = lastLanguage == null || !currentLanguage.equals(lastLanguage);
      boolean currencyChanged = lastCurrency == null || !currentCurrency.equals(lastCurrency);
      boolean selectedDark = prefs.contains(KEY_DARK_THEME) ? prefs.getBoolean(KEY_DARK_THEME, false) : (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
      boolean themeChanged = selectedDark != dark;
      lastLanguage = currentLanguage;
      lastCurrency = currentCurrency;
      if (languageChanged || currencyChanged || themeChanged) {
        dark = selectedDark;
        build();
        load();
        applyCurrency();
        calculate();
        return;
      }
      applyCurrency();
      calculate();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    save();
  }

  private int text() {
    return dark ? Color.rgb(245, 248, 255) : white;
  }

  private int sub() {
    return dark ? Color.rgb(170, 183, 204) : secondary;
  }

  private int dp(int n) {
    return (int) (n * getResources().getDisplayMetrics().density + .5f);
  }

  private String currencySymbol() {
    String c = prefs.getString(KEY_CURRENCY, "EUR");
    if ("USD".equals(c)) return "$";
    if ("GBP".equals(c)) return "£";
    if ("CHF".equals(c)) return "CHF";
    if ("CAD".equals(c)) return "CA$";
    if ("AUD".equals(c)) return "A$";
    return "€";
  }

  private void applyCurrency() {
    String s = currencySymbol();
    if (electricityPriceUnit != null) electricityPriceUnit.setText(s + "/kWh");
    if (fuelPriceUnit != null) fuelPriceUnit.setText(s + "/l");
  }

  private TextView tv(String s, int sp, int c) {
    TextView t = new TextView(this);
    t.setText(LanguageManager.t(this, s));
    t.setTextSize(sp);
    t.setTextColor(c);
    return t;
  }

  private GradientDrawable bg(int color, float r, int stroke) {
    GradientDrawable g = new GradientDrawable();
    g.setColor(color);
    g.setCornerRadius(dp((int) r));
    if (stroke > 0) g.setStroke(dp(1), dark ? Color.rgb(38, 59, 85) : borderLight);
    return g;
  }

  private LinearLayout card() {
    LinearLayout l = new LinearLayout(this);
    l.setOrientation(LinearLayout.VERTICAL);
    l.setPadding(dp(18), dp(18), dp(18), dp(18));
    l.setBackground(bg(dark ? cardDark : cardLight, 22, 1));
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
    lp.setMargins(dp(12), 0, dp(12), 0);
    l.setLayoutParams(lp);
    return l;
  }

  private void space(LinearLayout p, int h) {
    Space s = new Space(this);
    p.addView(s, new LinearLayout.LayoutParams(1, dp(h)));
  }

  private EditText edit(String value) {
    EditText e = new EditText(this);
    e.setText(value);
    e.setTextColor(text());
    e.setTextSize(16);
    e.setSingleLine();
    e.setGravity(Gravity.CENTER);
    e.setBackground(bg(dark ? Color.rgb(21, 34, 51) : Color.rgb(246, 249, 253), 12, 1));
    e.setPadding(dp(8), 0, dp(8), 0);
    e.setSelectAllOnFocus(true);
    e.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    e.setKeyListener(DigitsKeyListener.getInstance("0123456789,."));
    e.setImeOptions(EditorInfo.IME_ACTION_DONE);
    e.addTextChangedListener(
        new android.text.TextWatcher() {
          public void beforeTextChanged(CharSequence s, int st, int c, int a) {}

          public void onTextChanged(CharSequence s, int st, int b, int c) {
            calculate();
          }

          public void afterTextChanged(android.text.Editable e) {}
        });
    e.setOnEditorActionListener(
        (v, a, event) -> {
          if (a == EditorInfo.IME_ACTION_DONE
              || (event != null && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER)) {
            v.clearFocus();
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(v.getWindowToken(), 0);
            return true;
          }
          return false;
        });
    return e;
  }

  private void row(LinearLayout p, String label, EditText e, String unit) {
    LinearLayout r = new LinearLayout(this);
    r.setGravity(Gravity.CENTER_VERTICAL);
    TextView l = tv(label, 14, sub());
    r.addView(l, new LinearLayout.LayoutParams(0, 54, 1));
    r.addView(e, new LinearLayout.LayoutParams(dp(88), 54));
    TextView u = tv(unit, 13, sub());
    u.setGravity(Gravity.CENTER);
    u.setIncludeFontPadding(false);
    r.addView(u, new LinearLayout.LayoutParams(dp(58), 54));
    p.addView(r);
  }

  private TextView resultValue(String s) {
    TextView t = tv(s, 20, text());
    t.setTypeface(null, 1);
    t.setGravity(Gravity.CENTER);
    t.setIncludeFontPadding(false);
    t.setSingleLine(true);
    return t;
  }

  private void build() {
    FrameLayout frame = new FrameLayout(this);
    PremiumBackgroundView background = new PremiumBackgroundView(this);
    background.setDark(dark);
    background.setShowVehicle(false);
    frame.addView(background, new FrameLayout.LayoutParams(-1, -1));
    scroll = new ScrollView(this);
    scroll.setFillViewport(true);
    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(0, 0, 0, dp(84));
    scroll.addView(root);
    frame.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
    setContentView(frame);
    FrameLayout hero = new FrameLayout(this);
    hero.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(260)));
    ImageView header = new ImageView(this);
    header.setImageResource(R.drawable.cabecera_electric_vs_combustion);
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
    headerTitle = tv("Electric Vs\nCombustion Calculator", 20, Color.WHITE);
    headerTitle.setTypeface(null, 1);
    headerTitle.setGravity(Gravity.CENTER);
    headerTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    headerTitle.setIncludeFontPadding(false);
    headerTitle.setMaxLines(2);
    headerTitle.setShadowLayer(dp(4), 0, dp(2), Color.argb(90, 0, 0, 0));
    FrameLayout.LayoutParams tp =
        new FrameLayout.LayoutParams(-1, dp(56), Gravity.TOP | Gravity.CENTER_HORIZONTAL);
    tp.leftMargin = dp(44);
    tp.rightMargin = dp(44);
    tp.topMargin = dp(4);
    hero.addView(headerTitle, tp);
    root.addView(hero);
    LinearLayout trip = card();
    LinearLayout.LayoutParams tripLp = (LinearLayout.LayoutParams) trip.getLayoutParams();
    tripLp.topMargin = -dp(20);
    trip.setLayoutParams(tripLp);
    TextView h = tv("Datos del viaje", 18, text());
    h.setTypeface(null, 1);
    trip.addView(h);
    space(trip, 12);
    distance = edit("350");
    row(trip, "Distancia", distance, "km");
    root.addView(trip);
    space(root, 14);
    LinearLayout ev = card();
    TextView eh = tv("Vehículo eléctrico", 18, text());
    eh.setTypeface(null, 1);
    ev.addView(eh);
    space(ev, 12);
    evConsumption = edit("17,0");
    row(ev, "Consumo", evConsumption, "kWh/100 km");
    space(ev, 8);
    electricityPrice = edit("0,15");
    row(ev, "Precio electricidad", electricityPrice, currencySymbol() + "/kWh");
    electricityPriceUnit =
        (TextView) ((LinearLayout) ev.getChildAt(ev.getChildCount() - 1)).getChildAt(2);
    root.addView(ev);
    space(root, 14);
    LinearLayout combustion = card();
    TextView ch = tv("Vehículo de combustión", 18, text());
    ch.setTypeface(null, 1);
    combustion.addView(ch);
    space(combustion, 12);
    fuelConsumption = edit("6,0");
    row(combustion, "Consumo", fuelConsumption, "l/100 km");
    space(combustion, 8);
    fuelPrice = edit("1,55");
    row(combustion, "Precio combustible", fuelPrice, currencySymbol() + "/l");
    fuelPriceUnit =
        (TextView)
            ((LinearLayout) combustion.getChildAt(combustion.getChildCount() - 1)).getChildAt(2);
    root.addView(combustion);
    space(root, 14);
    LinearLayout results = card();
    TextView rh = tv("Resultado del viaje", 18, text());
    rh.setTypeface(null, 1);
    results.addView(rh);
    space(results, 14);
    LinearLayout labelsRow = new LinearLayout(this);
    labelsRow.setGravity(Gravity.CENTER);
    labelsRow.setWeightSum(2);
    TextView evLabel = tv("⚡  Eléctrico", 14, sub());
    evLabel.setGravity(Gravity.CENTER);
    evLabel.setSingleLine(true);
    labelsRow.addView(evLabel, new LinearLayout.LayoutParams(0, dp(28), 1));
    TextView fuLabel = tv("⛽  Combustión", 14, sub());
    fuLabel.setGravity(Gravity.CENTER);
    fuLabel.setSingleLine(true);
    labelsRow.addView(fuLabel, new LinearLayout.LayoutParams(0, dp(28), 1));
    results.addView(labelsRow);
    LinearLayout valuesRow = new LinearLayout(this);
    valuesRow.setGravity(Gravity.CENTER);
    valuesRow.setWeightSum(2);
    evCost = resultValue("0,00 " + currencySymbol());
    valuesRow.addView(evCost, new LinearLayout.LayoutParams(0, dp(40), 1));
    fuelCost = resultValue("0,00 " + currencySymbol());
    valuesRow.addView(fuelCost, new LinearLayout.LayoutParams(0, dp(40), 1));
    results.addView(valuesRow);
    space(results, 12);
    saving = tv("Ahorro con el eléctrico: 0,00 " + currencySymbol(), 17, text());
    saving.setTypeface(null, 1);
    saving.setGravity(Gravity.CENTER);
    results.addView(saving);
    savingPercent = tv("0,0 % de ahorro", 14, sub());
    savingPercent.setGravity(Gravity.CENTER);
    results.addView(savingPercent);
    space(results, 14);
    LinearLayout perLabels = new LinearLayout(this);
    perLabels.setGravity(Gravity.CENTER);
    perLabels.setWeightSum(2);
    TextView evPerLabel = tv("Eléctrico", 12, sub());
    evPerLabel.setGravity(Gravity.CENTER);
    evPerLabel.setSingleLine(true);
    evPerLabel.setIncludeFontPadding(false);
    perLabels.addView(evPerLabel, new LinearLayout.LayoutParams(0, dp(24), 1));
    TextView fuelPerLabel = tv("Combustión", 12, sub());
    fuelPerLabel.setGravity(Gravity.CENTER);
    fuelPerLabel.setSingleLine(true);
    fuelPerLabel.setIncludeFontPadding(false);
    perLabels.addView(fuelPerLabel, new LinearLayout.LayoutParams(0, dp(24), 1));
    results.addView(perLabels);
    LinearLayout perValues = new LinearLayout(this);
    perValues.setGravity(Gravity.CENTER);
    perValues.setWeightSum(2);
    evPer100 = resultValue("0,00 " + currencySymbol() + "/100 km");
    evPer100.setTextSize(12);
    perValues.addView(evPer100, new LinearLayout.LayoutParams(0, dp(24), 1));
    fuelPer100 = resultValue("0,00 " + currencySymbol() + "/100 km");
    fuelPer100.setTextSize(12);
    perValues.addView(fuelPer100, new LinearLayout.LayoutParams(0, dp(24), 1));
    results.addView(perValues);
    root.addView(results);
    Space bottom = new Space(this);
    root.addView(bottom, new LinearLayout.LayoutParams(1, 0, 1));
    TextView privacy = tv("Política de privacidad", 13, dark ? Color.rgb(105, 175, 255) : blue);
    privacy.setGravity(Gravity.CENTER);
    privacy.setTypeface(null, 1);
    privacy.setOnClickListener(
        v ->
            startActivity(
                new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://mariskal19.github.io/EV-Calculator-PRO-Privacy/"))));
    root.addView(privacy, new LinearLayout.LayoutParams(-1, dp(34)));
    TextView foot = tv("Powered by EV Calculator · v" + version(), 12, sub());
    foot.setGravity(Gravity.CENTER);
    root.addView(foot, new LinearLayout.LayoutParams(-1, dp(28)));
  }

  public void refreshLanguage() {
    if (isFinishing()) return;
    String currentDistance = distance != null ? distance.getText().toString() : "350";
    String currentEv = evConsumption != null ? evConsumption.getText().toString() : "17,0";
    String currentElec = electricityPrice != null ? electricityPrice.getText().toString() : "0,15";
    String currentFuel = fuelConsumption != null ? fuelConsumption.getText().toString() : "6,0";
    String currentFuelPrice = fuelPrice != null ? fuelPrice.getText().toString() : "1,55";
    build();
    distance.setText(currentDistance);
    evConsumption.setText(currentEv);
    electricityPrice.setText(currentElec);
    fuelConsumption.setText(currentFuel);
    fuelPrice.setText(currentFuelPrice);
    applyCurrency();
    calculate();
  }

  private String version() {
    try {
      return getPackageManager()
          .getPackageInfo(getPackageName(), 0)
          .versionName
          .replaceFirst("^[vV]", "");
    } catch (Exception e) {
      return "1.0.2";
    }
  }

  private double number(EditText e) {
    try {
      return Double.parseDouble(e.getText().toString().trim().replace(',', '.'));
    } catch (Exception x) {
      return 0;
    }
  }

  private String money(double v) {
    return CurrencyNumberFormatter.format(v, 2, prefs.getString(KEY_CURRENCY, "EUR"))
        + " "
        + currencySymbol();
  }

  private String one(double v) {
    return CurrencyNumberFormatter.format(v, 1, prefs.getString(KEY_CURRENCY, "EUR"));
  }

  private void calculate() {
    if (distance == null || evCost == null) return;
    double km = number(distance),
        evC = number(evConsumption),
        elec = number(electricityPrice),
        fuelC = number(fuelConsumption),
        fuelP = number(fuelPrice);
    double eCost = km * evC / 100.0 * elec,
        fCost = km * fuelC / 100.0 * fuelP,
        diff = fCost - eCost,
        pct = fCost > 0 ? diff / fCost * 100.0 : 0;
    evCost.setText(money(eCost));
    fuelCost.setText(money(fCost));
    saving.setText(
        (diff >= 0
                ? LanguageManager.t(this, "Ahorro con el eléctrico:")
                : LanguageManager.t(this, "Diferencia:"))
            + " "
            + money(Math.abs(diff)));
    savingPercent.setText(
        diff >= 0
            ? one(pct) + " % " + LanguageManager.t(this, "de ahorro")
            : LanguageManager.t(this, "El eléctrico cuesta")
                + " "
                + one(Math.abs(pct))
                + " % "
                + LanguageManager.t(this, "más"));
    evPer100.setText(money(evC * elec) + "/100 km");
    fuelPer100.setText(money(fuelC * fuelP) + "/100 km");
  }

  private void save() {
    if (distance == null) return;
    prefs
        .edit()
        .putString("cmp_distance", distance.getText().toString())
        .putString("cmp_ev_consumption", evConsumption.getText().toString())
        .putString("cmp_electricity_price", electricityPrice.getText().toString())
        .putString("cmp_fuel_consumption", fuelConsumption.getText().toString())
        .putString("cmp_fuel_price", fuelPrice.getText().toString())
        .apply();
  }

  private void load() {
    distance.setText(prefs.getString("cmp_distance", distance.getText().toString()));
    evConsumption.setText(
        prefs.getString("cmp_ev_consumption", evConsumption.getText().toString()));
    electricityPrice.setText(
        prefs.getString("cmp_electricity_price", electricityPrice.getText().toString()));
    fuelConsumption.setText(
        prefs.getString("cmp_fuel_consumption", fuelConsumption.getText().toString()));
    fuelPrice.setText(prefs.getString("cmp_fuel_price", fuelPrice.getText().toString()));
  }

}
