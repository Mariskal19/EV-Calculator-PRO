package com.evchargecalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class CompararCochesActivity extends Activity {

    private static final String PREFS = "ev_charge_calculator";
    private static final String KEY_SELECTED = "compare_vehicle_ids";
    private static final String KEY_SELECTED_ORDERED = "compare_vehicle_ids_ordered";
    private static final String KEY_CURRENCY = "app_currency";
    private static final String KEY_MARKET = "compare_market";
    private static final String KEY_SEARCH_COUNT_PREFIX = "compare_search_count_";

    private final int blue = Color.rgb(46, 107, 255);
    private final int white = Color.rgb(22, 42, 63);
    private final int secondary = Color.rgb(90, 111, 137);

    private boolean dark;
    private LinearLayout carsRow, table, summary;
    private Spinner marketSpinner;

    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<String> selectedIds = new ArrayList<>();
    private String selectedMarket = "ES";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        try {
            SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
            dark = p.contains("dark_theme")
                    ? p.getBoolean("dark_theme", false)
                    : (getResources().getConfiguration().uiMode & 0x30) == 0x20;
            CurrencyRateManager.refreshIfNeeded(this);
            loadVehicles();
            selectedMarket = p.getString(KEY_MARKET, "ES");
            if (!hasMarket(selectedMarket)) {
                selectedMarket = defaultMarket();
            }
            build();
            loadSelection();
            rebuild();
        } catch (Throwable t) {
            TextView e = tv(
                    "Error al abrir Comparar coches\n\n" + t.getClass().getSimpleName(),
                    16,
                    Color.WHITE
            );
            e.setGravity(Gravity.CENTER);
            e.setPadding(dp(24), dp(24), dp(24), dp(24));
            e.setBackgroundColor(Color.rgb(8, 34, 58));
            setContentView(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CurrencyRateManager.refreshIfNeeded(this);
        SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
        boolean newDark = p.contains("dark_theme")
                ? p.getBoolean("dark_theme", false)
                : (getResources().getConfiguration().uiMode & 0x30) == 0x20;
        if (newDark != dark) {
            dark = newDark;
            build();
            loadSelection();
            rebuild();
        } else if (table != null) {
            rebuild();
        }
    }

    private int dp(int n) {
        return (int) (n * getResources().getDisplayMetrics().density + 0.5f);
    }

    private int text() {
        return dark ? Color.rgb(245, 248, 255) : white;
    }

    private int sub() {
        return dark ? Color.rgb(170, 183, 204) : secondary;
    }

    private int rowAlt() {
        return dark ? Color.rgb(14, 25, 36) : Color.rgb(248, 251, 255);
    }

    private int bestBg() {
        return dark ? Color.rgb(13, 36, 58) : Color.rgb(235, 243, 255);
    }

    private GradientDrawable bg(int c, float r) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(c);
        g.setCornerRadius(dp((int) r));
        return g;
    }

    private GradientDrawable strokeBg(int fill, int stroke, float r) {
        GradientDrawable g = bg(fill, r);
        g.setStroke(dp(1), stroke);
        return g;
    }

    private TextView tv(String s, float size, int color) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        return t;
    }

    private void loadVehicles() {
        vehicles.clear();
        loadAssetVehicles("vehicles.json", true);
        loadAssetVehicles("vehicle_variants.json", false);
        loadAssetVehicles("vehicle_market_additions.json", false);
        normalizeVehicleList();
        if (vehicles.isEmpty()) {
            throw new IllegalStateException("vehicles array missing");
        }
    }

    private void loadAssetVehicles(String asset, boolean required) {
        try (InputStream in = getAssets().open(asset);
             BufferedReader r = new BufferedReader(new InputStreamReader(in))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line);
            }
            JSONArray a = new JSONObject(sb.toString()).optJSONArray("vehicles");
            if (a == null) {
                throw new IllegalStateException(asset + ": vehicles array missing");
            }
            for (int i = 0; i < a.length(); i++) {
                JSONObject o = a.optJSONObject(i);
                if (o != null) {
                    addOrMergeVehicle(new Vehicle(o));
                }
            }
        } catch (Exception e) {
            if (required) {
                throw new IllegalStateException("No se ha podido cargar " + asset, e);
            }
        }
    }

    private String effectiveBatteryKey(Vehicle v) {
        if (v.batteryKwh > 0) {
            return String.format(Locale.US, "%.1f", v.batteryKwh);
        }
        String s = v.version == null ? "" : v.version.trim();
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("^(\\d+(?:\\.\\d+)?)\\s*kwh\\b", java.util.regex.Pattern.CASE_INSENSITIVE)
                .matcher(s);
        return m.find() ? m.group(1) : "0";
    }

    private String normalizedVersion(Vehicle v) {
        String ver = v.version == null ? "" : v.version.trim().toLowerCase(Locale.ROOT);
        ver = ver.replaceAll("^\\d+(?:\\.\\d+)?\\s*kwh\\s*", "");
        ver = ver.replaceAll("\\s+", " ").trim();
        return ver;
    }

    private String logicalKey(Vehicle v) {
        return (v.make + "|" + v.model + "|" + v.market + "|" + v.year + "|"
                + effectiveBatteryKey(v) + "|" + normalizedVersion(v))
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private void addOrMergeVehicle(Vehicle incoming) {
        String key = logicalKey(incoming);
        for (Vehicle existing : vehicles) {
            if (!logicalKey(existing).equals(key)) {
                continue;
            }
            if (existing.price <= 0) existing.price = incoming.price;
            if (existing.batteryKwh <= 0) existing.batteryKwh = incoming.batteryKwh;
            if (existing.usableBatteryKwh <= 0) existing.usableBatteryKwh = incoming.usableBatteryKwh;
            if (existing.batteryType.isEmpty()) existing.batteryType = incoming.batteryType;
            if (existing.wltpKm <= 0) existing.wltpKm = incoming.wltpKm;
            if (existing.consumption <= 0) existing.consumption = incoming.consumption;
            if (existing.powerKw <= 0) existing.powerKw = incoming.powerKw;
            if (existing.drivetrain.isEmpty()) existing.drivetrain = incoming.drivetrain;
            if (existing.acKw <= 0) existing.acKw = incoming.acKw;
            if (existing.dcKw <= 0) existing.dcKw = incoming.dcKw;
            if (existing.chargeMin <= 0) existing.chargeMin = incoming.chargeMin;
            if (existing.acc <= 0) existing.acc = incoming.acc;
            if (existing.trunk <= 0) existing.trunk = incoming.trunk;
            if (existing.weight <= 0) existing.weight = incoming.weight;
            return;
        }
        vehicles.add(incoming);
    }

    private void normalizeVehicleList() {
        Iterator<Vehicle> it = vehicles.iterator();
        while (it.hasNext()) {
            Vehicle v = it.next();
            if (v.version == null || !v.version.contains("/")) continue;
            boolean allPresent = true;
            for (String part : v.version.split("/")) {
                boolean found = false;
                String wanted = part.trim();
                for (Vehicle other : vehicles) {
                    if (other != v
                            && other.make.equalsIgnoreCase(v.make)
                            && other.model.equalsIgnoreCase(v.model)
                            && other.market.equalsIgnoreCase(v.market)
                            && other.year == v.year
                            && other.version.trim().equalsIgnoreCase(wanted)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    allPresent = false;
                    break;
                }
            }
            if (allPresent) it.remove();
        }
        Collections.sort(vehicles, (a, b) -> {
            int c = a.make.compareToIgnoreCase(b.make);
            if (c != 0) return c;
            c = a.model.compareToIgnoreCase(b.model);
            if (c != 0) return c;
            c = Integer.compare(b.year, a.year);
            if (c != 0) return c;
            c = Double.compare(a.batteryKwh, b.batteryKwh);
            if (c != 0) return c;
            return a.version.compareToIgnoreCase(b.version);
        });
    }

    private String defaultMarket() {
        for (Vehicle v : vehicles) {
            if (v.market.equalsIgnoreCase("ES")) return "ES";
        }
        return vehicles.get(0).market;
    }

    private boolean hasMarket(String m) {
        for (Vehicle v : vehicles) {
            if (v.market.equalsIgnoreCase(m)) return true;
        }
        return false;
    }

    private List<String> markets() {
        LinkedHashSet<String> s = new LinkedHashSet<>();
        for (Vehicle v : vehicles) {
            if (v.market != null && !v.market.trim().isEmpty()) {
                s.add(v.market.toUpperCase(Locale.ROOT));
            }
        }
        List<String> o = new ArrayList<>(s);
        Collections.sort(o, (a, b) -> marketName(a).compareToIgnoreCase(marketName(b)));
        return o;
    }

    private String marketName(String c) {
        if (c == null || c.trim().isEmpty()) return "";
        String code = c.equalsIgnoreCase("UK") ? "GB" : c.toUpperCase(Locale.ROOT);
        if (code.matches("[A-Z]{2}")) {
            Locale displayLocale = Locale.forLanguageTag(LanguageManager.getEffectiveLanguage(this));
            String name = new Locale("", code).getDisplayCountry(displayLocale);
            if (name != null && !name.trim().isEmpty() && !name.equalsIgnoreCase(code)) {
                return name;
            }
        }
        return code;
    }

    private String marketFlag(String c) {
        if ("ES".equalsIgnoreCase(c)) return "🇪🇸";
        if ("FR".equalsIgnoreCase(c)) return "🇫🇷";
        if ("DE".equalsIgnoreCase(c)) return "🇩🇪";
        if ("IT".equalsIgnoreCase(c)) return "🇮🇹";
        if ("PT".equalsIgnoreCase(c)) return "🇵🇹";
        if ("GB".equalsIgnoreCase(c) || "UK".equalsIgnoreCase(c)) return "🇬🇧";
        if (c != null && c.matches("[A-Za-z]{2}")) {
            int a = Character.toUpperCase(c.charAt(0)) - 'A' + 127462;
            int b = Character.toUpperCase(c.charAt(1)) - 'A' + 127462;
            return new String(Character.toChars(a)) + new String(Character.toChars(b));
        }
        return "🌐";
    }

    private String marketLabel(String c) {
        return marketFlag(c) + "  " + marketName(c);
    }

    private void build() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        int statusBarHeight = 0;
        int statusBarId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (statusBarId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(statusBarId);
        }
        root.setPadding(0, statusBarHeight, 0, 0);
        root.setBackgroundColor(dark ? Color.rgb(7, 19, 28) : Color.rgb(241, 246, 251));

        FrameLayout hero = new FrameLayout(this);
        hero.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(156)));

        ImageView heroImage = new ImageView(this);
        heroImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        heroImage.setImageResource(R.drawable.cabecera_tema_claro);
        hero.addView(heroImage, new FrameLayout.LayoutParams(-1, -1));

        View shade = new View(this);
        shade.setBackgroundColor(Color.argb(dark ? 145 : 90, 0, 18, 32));
        hero.addView(shade, new FrameLayout.LayoutParams(-1, -1));

        TextView back = tv("‹", 40, Color.WHITE);
        back.setGravity(Gravity.CENTER);
        back.setTypeface(null, Typeface.BOLD);
        back.setShadowLayer(8, 0, 2, Color.BLACK);
        back.setOnClickListener(v -> finish());
        FrameLayout.LayoutParams bp = new FrameLayout.LayoutParams(dp(52), dp(58), Gravity.START | Gravity.TOP);
        bp.setMargins(dp(8), dp(10), 0, 0);
        hero.addView(back, bp);

        TextView menuButton = tv("⋮", 30, Color.WHITE);
        menuButton.setGravity(Gravity.CENTER);
        menuButton.setIncludeFontPadding(false);
        menuButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        menuButton.setShadowLayer(8, 0, 2, Color.BLACK);
        menuButton.setBackgroundColor(Color.TRANSPARENT);
        menuButton.setContentDescription(LanguageManager.t(this, "Menú"));
        menuButton.setOnClickListener(v -> AppMenuHelper.show(this, menuButton, new AppMenuHelper.Listener() {
            public boolean isDark() {
                return dark;
            }

            public void setDark(boolean value) {
                if (dark != value) {
                    dark = value;
                    getSharedPreferences(PREFS, MODE_PRIVATE).edit().putBoolean("dark_theme", dark).apply();
                    build();
                    loadSelection();
                    rebuild();
                }
            }
        }));
        FrameLayout.LayoutParams mbp = new FrameLayout.LayoutParams(dp(44), dp(52), Gravity.END | Gravity.TOP);
        mbp.setMargins(0, dp(10), dp(8), 0);
        hero.addView(menuButton, mbp);

        TextView title = tv("Comparar coches", 25, Color.WHITE);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setShadowLayer(8, 0, 2, Color.BLACK);
        FrameLayout.LayoutParams tp = new FrameLayout.LayoutParams(-1, dp(58), Gravity.CENTER);
        tp.setMargins(dp(50), dp(48), dp(50), 0);
        hero.addView(title, tp);

        TextView subtitle = tv("Compara hasta 3 vehículos", 14, Color.WHITE);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setShadowLayer(6, 0, 2, Color.BLACK);
        FrameLayout.LayoutParams sp = new FrameLayout.LayoutParams(-1, dp(34), Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        sp.setMargins(dp(24), 0, dp(24), dp(12));
        hero.addView(subtitle, sp);
        root.addView(hero);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(14), dp(14), dp(14), dp(12));

        LinearLayout intro = new LinearLayout(this);
        intro.setOrientation(LinearLayout.VERTICAL);
        intro.setPadding(dp(16), dp(14), dp(16), dp(14));
        intro.setBackground(strokeBg(
                dark ? Color.rgb(17, 31, 44) : Color.WHITE,
                dark ? Color.rgb(43, 64, 82) : Color.rgb(218, 228, 239),
                18
        ));

        TextView introTitle = tv("Elige tus vehículos", 17, text());
        introTitle.setTypeface(null, Typeface.BOLD);
        intro.addView(introTitle, new LinearLayout.LayoutParams(-1, dp(26)));

        TextView hint = tv(
                "Añade hasta 3 coches para ver sus características y compararlos.",
                13,
                sub()
        );
        hint.setPadding(0, dp(2), 0, 0);
        intro.addView(hint, new LinearLayout.LayoutParams(-1, dp(36)));
        content.addView(intro, new LinearLayout.LayoutParams(-1, -2));

        HorizontalScrollView carsScroll = new HorizontalScrollView(this);
        carsScroll.setHorizontalScrollBarEnabled(false);
        carsScroll.setClipToPadding(false);
        carsScroll.setPadding(0, dp(12), 0, dp(4));
        carsRow = new LinearLayout(this);
        carsRow.setOrientation(LinearLayout.HORIZONTAL);
        carsRow.setGravity(Gravity.TOP);
        carsScroll.addView(carsRow, new HorizontalScrollView.LayoutParams(-2, -2));
        content.addView(carsScroll, new LinearLayout.LayoutParams(-1, -2));

        TextView section = tv("Características", 19, text());
        section.setTypeface(null, Typeface.BOLD);
        section.setPadding(dp(2), dp(12), 0, dp(2));
        content.addView(section, new LinearLayout.LayoutParams(-1, dp(42)));

        TextView legend = tv("✦  Mejor valor", 12, blue);
        legend.setGravity(Gravity.CENTER_VERTICAL);
        legend.setPadding(dp(4), 0, 0, dp(4));
        if (selectedIds.size() >= 2) {
            content.addView(legend, new LinearLayout.LayoutParams(-1, dp(28)));
        }

        table = new LinearLayout(this);
        table.setOrientation(LinearLayout.VERTICAL);
        table.setPadding(0, dp(2), 0, 0);
        HorizontalScrollView tableScroll = new HorizontalScrollView(this);
        tableScroll.setHorizontalScrollBarEnabled(false);
        tableScroll.addView(table, new HorizontalScrollView.LayoutParams(-2, -2));
        content.addView(tableScroll, new LinearLayout.LayoutParams(-1, -2));

        summary = new LinearLayout(this);
        summary.setOrientation(LinearLayout.VERTICAL);
        summary.setPadding(0, dp(18), 0, dp(8));
        content.addView(summary, new LinearLayout.LayoutParams(-1, -2));

        LinearLayout footer = new LinearLayout(this);
        footer.setOrientation(LinearLayout.VERTICAL);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(dp(14), 0, dp(14), dp(4));

        String appVersion = "1.0.4";
        try {
            appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception ignored) {
        }
        if (appVersion.startsWith("v") || appVersion.startsWith("V")) {
            appVersion = appVersion.substring(1);
        }

        TextView privacyLink = tv(
                "Política de privacidad",
                13,
                dark ? Color.rgb(105, 175, 255) : blue
        );
        privacyLink.setGravity(Gravity.CENTER);
        privacyLink.setTypeface(null, Typeface.BOLD);
        privacyLink.setClickable(true);
        privacyLink.setFocusable(true);
        privacyLink.setContentDescription("Política de privacidad");
        privacyLink.setOnClickListener(v -> startActivity(new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://mariskal19.github.io/EV-Calculator-PRO-Privacy/")
        )));
        footer.addView(privacyLink, new LinearLayout.LayoutParams(-1, dp(30)));

        TextView foot = tv("Powered by EV Calculator · v" + appVersion, 12, sub());
        foot.setGravity(Gravity.CENTER);
        footer.addView(foot, new LinearLayout.LayoutParams(-1, dp(24)));

        // El footer sigue formando parte del contenido desplazable.
        Space footerSpacer = new Space(this);
        content.addView(footerSpacer, new LinearLayout.LayoutParams(-1, 0, 1));
        content.addView(footer, new LinearLayout.LayoutParams(-1, dp(62)));
        scroll.addView(content, new ScrollView.LayoutParams(-1, -1));
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        setContentView(root);
        getWindow().setStatusBarColor(dark ? Color.rgb(7, 19, 28) : Color.rgb(241, 246, 251));
        getWindow().setNavigationBarColor(dark ? Color.rgb(7, 19, 28) : Color.rgb(241, 246, 251));
        getWindow().getDecorView().setSystemUiVisibility(dark ? 0 : View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private List<Vehicle> marketVehicles() {
        List<Vehicle> o = new ArrayList<>();
        for (Vehicle v : vehicles) {
            if (v.market.equalsIgnoreCase(selectedMarket)) {
                o.add(v);
            }
        }
        return o;
    }

    private int tableWidth() {
        return dp(112 + 145 * Math.max(1, selectedIds.size()));
    }

    private void loadSelection() {
        selectedIds.clear();
        SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
        String ordered = p.getString(KEY_SELECTED_ORDERED, "");

        if (!ordered.trim().isEmpty()) {
            for (String id : ordered.split(",")) {
                id = id.trim();
                Vehicle v = find(id);
                if (!id.isEmpty() && v != null && !selectedIds.contains(id) && selectedIds.size() < 3) {
                    selectedIds.add(id);
                }
            }
        }

        if (selectedIds.isEmpty()) {
            Set<String> s = p.getStringSet(KEY_SELECTED, null);
            if (s != null) {
                for (String id : s) {
                    Vehicle v = find(id);
                    if (v != null && !selectedIds.contains(id) && selectedIds.size() < 3) {
                        selectedIds.add(id);
                    }
                }
            }
        }
    }

    private Vehicle find(String id) {
        for (Vehicle v : vehicles) {
            if (v.id.equals(id)) return v;
        }
        return null;
    }

    private void saveSelection() {
        SharedPreferences.Editor e = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        e.putString(KEY_SELECTED_ORDERED, joinSelection());
        e.putStringSet(KEY_SELECTED, new LinkedHashSet<>(selectedIds));
        e.apply();
    }

    private String joinSelection() {
        StringBuilder s = new StringBuilder();
        for (String id : selectedIds) {
            if (s.length() > 0) s.append(',');
            s.append(id);
        }
        return s.toString();
    }

    private void rebuild() {
        if (carsRow == null || table == null || summary == null) return;
        carsRow.removeAllViews();
        table.removeAllViews();
        summary.removeAllViews();

        for (String id : selectedIds) {
            Vehicle v = find(id);
            if (v != null) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(145), -2);
                lp.setMargins(dp(3), 0, dp(3), 0);
                carsRow.addView(carCard(v), lp);
            }
        }

        if (selectedIds.size() < 3) {
            LinearLayout empty = new LinearLayout(this);
            empty.setOrientation(LinearLayout.VERTICAL);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(8), dp(10), dp(8), dp(10));
            empty.setBackground(strokeBg(
                    dark ? Color.rgb(14, 26, 38) : Color.WHITE,
                    dark ? Color.rgb(43, 64, 82) : Color.rgb(220, 229, 240),
                    16
            ));
            TextView plus = tv("＋", 28, blue);
            plus.setGravity(Gravity.CENTER);
            empty.addView(plus, new LinearLayout.LayoutParams(-1, dp(34)));
            TextView n = tv("Añadir coche", 12, sub());
            n.setGravity(Gravity.CENTER);
            empty.addView(n, new LinearLayout.LayoutParams(-1, dp(24)));
            empty.setOnClickListener(v -> showSearch());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(145), dp(150));
            lp.setMargins(dp(3), 0, dp(3), 0);
            carsRow.addView(empty, lp);
        }

        if (selectedIds.size() >= 1) {
            addSection("Batería y autonomía");
            addRow("Batería", "battery", false);
            addRow("Tipo batería", "type", false);
            addRow("Autonomía WLTP", "range", true);
            addRow("Consumo", "cons", true);
            addSection("Prestaciones");
            addRow("Potencia", "power", true);
            addRow("Tracción", "drive", false);
            addRow("0–100 km/h", "acc", false);
            addSection("Carga");
            addRow("Carga AC", "ac", true);
            addRow("Carga DC", "dc", true);
            addRow("10–80 %", "charge", false);
            addSection("Practicidad");
            addRow("Maletero", "trunk", true);
            addRow("Peso", "weight", false);
            addSection("Precio");
            addRow("Precio", "price", false);
            if (selectedIds.size() >= 2) buildSummary();
        } else {
            TextView t = tv(
                    "Selecciona un coche para mostrar sus características.",
                    14,
                    sub()
            );
            t.setGravity(Gravity.CENTER);
            t.setPadding(dp(10), dp(18), dp(10), dp(18));
            table.addView(t, new LinearLayout.LayoutParams(tableWidth(), -2));
        }
    }

    private void addSection(String title) {
        TextView s = tv(title, 14, blue);
        s.setTypeface(null, Typeface.BOLD);
        s.setGravity(Gravity.CENTER_VERTICAL);
        s.setPadding(dp(4), dp(12), dp(4), dp(6));
        table.addView(s, new LinearLayout.LayoutParams(tableWidth(), dp(40)));
    }

    private ImageView carImage(Vehicle v) {
        ImageView image = new ImageView(this);
        image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        image.setPadding(dp(4), dp(6), dp(4), dp(3));
        image.setBackground(bg(dark ? Color.rgb(12, 29, 43) : Color.rgb(239, 245, 252), 14));
        image.setImageResource(modelImageResource(v));
        return image;
    }

    private int modelImageResource(Vehicle v) {
        String key = (v.make + " " + v.model)
                .toLowerCase(Locale.ROOT)
                .replace("+", "plus")
                .replace("-", " ");
        if (key.contains("model 3")) return R.drawable.car_model3;
        if (key.contains("model y")) return R.drawable.car_modely;
        if (key.contains("dolphin surf")) return R.drawable.car_dolphin_surf;
        if (key.contains("kia ev3")) return R.drawable.car_kia_ev3;
        if (key.contains("toyota c hr") || key.contains("c-hr")) return R.drawable.car_toyota_chr_plus;
        if (key.contains("atto 2")) return R.drawable.car_byd_atto2;
        if (key.contains("renault 5")) return R.drawable.car_renault5;
        if (key.contains("elroq")) return R.drawable.car_skoda_elroq;
        if (key.contains("leapmotor b10")) return R.drawable.car_leapmotor_b10;
        if (key.contains("mercedes benz cla") || key.contains("mercedes cla")) return R.drawable.car_mercedes_cla;
        if (key.contains("byd seal")) return R.drawable.car_byd_seal;
        if (key.contains("mercedes benz eqa") || key.contains("mercedes eqa")) return R.drawable.car_mercedes_eqa;
        if (key.contains("xpeng g6")) return R.drawable.car_xpeng_g6;
        if (key.contains("atto 3")) return R.drawable.car_byd_atto3;
        if (key.contains("id.4") || key.contains("id 4")) return R.drawable.car_vw_id4;
        if (key.contains("audi q4")) return R.drawable.car_audi_q4;
        if (key.contains("ioniq 5")) return R.drawable.car_ioniq5;
        if (key.contains("id.3") || key.contains("id 3")) return R.drawable.car_vw_id3;
        if (key.contains("cupra born")) return R.drawable.car_cupra_born;
        if (key.contains("megane")) return R.drawable.car_renault_megane;
        if (key.contains("sedan") || key.contains("berlina") || key.contains("et5") || key.contains(" i4")) return R.drawable.ev_illustration_sedan;
        if (key.contains("coupe") || key.contains("coupé")) return R.drawable.ev_illustration_coupe;
        if (key.contains("hatch") || key.contains("5 e tech")) return R.drawable.ev_illustration_hatch;
        if (key.contains("crossover")) return R.drawable.ev_illustration_crossover;
        return R.drawable.ev_illustration_suv;
    }

    private android.view.View carCard(Vehicle v) {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setGravity(Gravity.CENTER_HORIZONTAL);
        c.setPadding(dp(9), dp(9), dp(9), dp(8));
        c.setBackground(strokeBg(
                dark ? Color.rgb(18, 32, 45) : Color.WHITE,
                dark ? Color.rgb(49, 72, 91) : Color.rgb(214, 225, 237),
                18
        ));

        ImageView photo = carImage(v);
        c.addView(photo, new LinearLayout.LayoutParams(-1, dp(68)));
        TextView make = tv(v.make, 12, blue);
        make.setTypeface(null, Typeface.BOLD);
        make.setGravity(Gravity.CENTER);
        make.setPadding(0, dp(8), 0, 0);
        c.addView(make, new LinearLayout.LayoutParams(-1, dp(28)));
        TextView model = tv(v.model, 17, text());
        model.setTypeface(null, Typeface.BOLD);
        model.setGravity(Gravity.CENTER);
        c.addView(model, new LinearLayout.LayoutParams(-1, dp(27)));
        TextView market = tv(marketLabel(v.market), 11, sub());
        market.setGravity(Gravity.CENTER);
        c.addView(market, new LinearLayout.LayoutParams(-1, dp(25)));
        TextView ver = tv(v.version, 11, sub());
        ver.setGravity(Gravity.CENTER);
        ver.setMaxLines(2);
        c.addView(ver, new LinearLayout.LayoutParams(-1, dp(34)));
        TextView year = tv(v.year > 0 ? String.valueOf(v.year) : "", 11, sub());
        year.setGravity(Gravity.CENTER);
        c.addView(year, new LinearLayout.LayoutParams(-1, dp(21)));
        TextView rem = tv("✕  Quitar", 12, Color.rgb(210, 70, 70));
        rem.setGravity(Gravity.CENTER);
        rem.setTypeface(null, Typeface.BOLD);
        rem.setPadding(0, dp(5), 0, 0);
        rem.setOnClickListener(x -> remove(v.id));
        c.addView(rem, new LinearLayout.LayoutParams(-1, dp(31)));
        return c;
    }

    private void remove(String id) {
        selectedIds.remove(id);
        saveSelection();
        rebuild();
    }

    private void showSearch() {
        final EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setHint("Marca, modelo, año, batería o versión");
        input.setTextColor(text());
        input.setHintTextColor(sub());

        final LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);

        LinearLayout marketRow = new LinearLayout(this);
        marketRow.setOrientation(LinearLayout.HORIZONTAL);
        marketRow.setGravity(Gravity.CENTER_VERTICAL);
        marketRow.setPadding(dp(18), dp(8), dp(18), dp(2));

        TextView marketTitle = tv("Mercado", 14, text());
        marketTitle.setTypeface(null, Typeface.BOLD);
        marketRow.addView(marketTitle, new LinearLayout.LayoutParams(0, dp(48), 1));

        final Spinner searchMarketSpinner = new Spinner(this);
        List<String> ms = markets();
        List<String> labels = new ArrayList<>();
        for (String m : ms) labels.add(marketLabel(m));

        ArrayAdapter<String> marketAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                labels
        ) {
            @Override
            public View getView(int p, View c, android.view.ViewGroup parent) {
                TextView v = (TextView) super.getView(p, c, parent);
                v.setTextColor(text());
                v.setTextSize(14);
                v.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
                return v;
            }

            @Override
            public View getDropDownView(int p, View c, android.view.ViewGroup parent) {
                TextView v = (TextView) super.getDropDownView(p, c, parent);
                v.setTextColor(text());
                v.setTextSize(15);
                v.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                v.setPadding(dp(14), dp(10), dp(14), dp(10));
                v.setBackgroundColor(dark ? Color.rgb(18, 30, 42) : Color.WHITE);
                return v;
            }
        };

        searchMarketSpinner.setAdapter(marketAdapter);
        int marketIndex = 0;
        for (int i = 0; i < ms.size(); i++) {
            if (ms.get(i).equalsIgnoreCase(selectedMarket)) {
                marketIndex = i;
                break;
            }
        }
        searchMarketSpinner.setSelection(marketIndex, false);
        searchMarketSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(android.widget.AdapterView<?> p) {
            }

            public void onItemSelected(android.widget.AdapterView<?> p, View v, int pos, long id) {
                if (pos >= ms.size() || ms.get(pos).equalsIgnoreCase(selectedMarket)) return;
                selectedMarket = ms.get(pos);
                getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(KEY_MARKET, selectedMarket).apply();
                saveSelection();
                rebuild();
                renderSearchResults(input, list);
            }
        });
        marketRow.addView(searchMarketSpinner, new LinearLayout.LayoutParams(dp(190), dp(48)));

        LinearLayout box = new LinearLayout(this);
        box.setPadding(dp(18), dp(4), dp(18), dp(2));
        box.addView(input, new LinearLayout.LayoutParams(-1, dp(52)));

        ScrollView scroll = new ScrollView(this);
        scroll.addView(list);
        LinearLayout wrap = new LinearLayout(this);
        wrap.setOrientation(LinearLayout.VERTICAL);
        wrap.addView(marketRow);
        wrap.addView(box);
        wrap.addView(scroll, new LinearLayout.LayoutParams(-1, dp(430)));

        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("Añadir coche")
                .setView(wrap)
                .create();

        input.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {
            }

            public void onTextChanged(CharSequence s, int st, int b, int c) {
                renderSearchResults(input, list);
            }

            public void afterTextChanged(Editable e) {
            }
        });

        d.setOnShowListener(x -> {
            d.getWindow().setBackgroundDrawable(bg(dark ? Color.rgb(15, 27, 39) : Color.WHITE, 20));
            TextView titleView = d.findViewById(getResources().getIdentifier("alertTitle", "id", "android"));
            if (titleView != null) titleView.setTextColor(text());
            renderSearchResults(input, list);
            input.requestFocus();
            d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        });
        d.show();
        input.setTag(d);
    }

    private String popularKey(Vehicle v) {
        return v.make + " " + v.model;
    }

    private int curatedPopularRank(Vehicle v) {
        String k = popularKey(v).toLowerCase(Locale.ROOT);
        String[] p = {
                "tesla model 3",
                "tesla model y",
                "xpeng g6",
                "byd seal",
                "kia ev3",
                "hyundai ioniq 5",
                "volkswagen id.4",
                "byd atto 3",
                "hyundai kona electric",
                "kia ev6",
                "skoda enyaq",
                "renault scenic e-tech",
                "volvo ex30",
                "bmw ix1",
                "mercedes-benz cla"
        };
        for (int i = 0; i < p.length; i++) {
            if (k.equals(p[i])) return i;
        }
        return 1000;
    }

    private int trimRank(Vehicle v) {
        String s = (v.version == null ? "" : v.version).toLowerCase(Locale.ROOT);
        int r = 500;
        if (s.contains("standard") || s.contains("base") || s.contains("pure")
                || s.contains("active") || s.contains("essential")) r = 100;
        if (s.contains("comfort") || s.contains("air") || s.contains("advance")
                || s.contains("evolution")) r = 200;
        if (s.contains("boost") || s.contains("pro") || s.contains("long range")
                || s.contains("earth") || s.contains("techno") || s.contains("design")) r = 300;
        if (s.contains("premium") || s.contains("gt-line") || s.contains("spirit")
                || s.contains("esprit alpine") || s.contains("performance")) r = 400;
        if (s.contains("excellence") || s.contains("gt") || s.contains("awd")) r = 500;
        return r;
    }

    private String searchLabel(Vehicle v) {
        String first = v.make + " " + v.model;
        String second = v.year > 0
                ? "MY" + String.valueOf(v.year).substring(Math.max(0, String.valueOf(v.year).length() - 2)) + ". "
                : "";
        if (v.version != null && !v.version.trim().isEmpty()) {
            String ver = v.version.trim();
            ver = ver.replaceFirst("(?i)^\\d+(?:\\.\\d+)?\\s*kwh\\s*", "");
            second += ver;
        }
        return first + "\n" + second;
    }

    private List<Vehicle> orderedSearchVehicles(String q) {
        List<Vehicle> all = new ArrayList<>();
        for (Vehicle v : marketVehicles()) {
            if (selectedIds.contains(v.id)) continue;
            String hay = (v.make + " " + v.model + " " + v.year + " " + v.batteryKwh + " "
                    + v.batteryType + " " + v.drivetrain + " " + v.version)
                    .toLowerCase(Locale.ROOT);
            if (!q.isEmpty() && !hay.contains(q)) continue;
            all.add(v);
        }

        Collections.sort(all, (a, b) -> {
            int c = a.make.compareToIgnoreCase(b.make);
            if (c != 0) return c;
            c = a.model.compareToIgnoreCase(b.model);
            if (c != 0) return c;
            c = Integer.compare(a.year, b.year);
            if (c != 0) return c;
            c = Integer.compare(trimRank(a), trimRank(b));
            if (c != 0) return c;
            c = Double.compare(a.price > 0 ? a.price : Double.MAX_VALUE,
                    b.price > 0 ? b.price : Double.MAX_VALUE);
            if (c != 0) return c;
            c = Double.compare(a.batteryKwh, b.batteryKwh);
            if (c != 0) return c;
            return a.version.compareToIgnoreCase(b.version);
        });
        return all;
    }

    private List<Vehicle> popularVehicles(List<Vehicle> all) {
        List<Vehicle> p = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        for (Vehicle v : all) {
            if (curatedPopularRank(v) < 1000
                    || prefs.getInt(KEY_SEARCH_COUNT_PREFIX + v.id, 0) > 0) {
                p.add(v);
            }
        }
        Collections.sort(p, (a, b) -> {
            int ca = prefs.getInt(KEY_SEARCH_COUNT_PREFIX + a.id, 0);
            int cb = prefs.getInt(KEY_SEARCH_COUNT_PREFIX + b.id, 0);
            if (ca != cb) return Integer.compare(cb, ca);
            int c = Integer.compare(curatedPopularRank(a), curatedPopularRank(b));
            if (c != 0) return c;
            c = a.model.compareToIgnoreCase(b.model);
            if (c != 0) return c;
            c = Integer.compare(b.year, a.year);
            if (c != 0) return c;
            return trimRank(a) - trimRank(b);
        });
        return p;
    }

    private void renderSearchResults(EditText input, LinearLayout list) {
        list.removeAllViews();
        String q = input.getText().toString().trim().toLowerCase(Locale.ROOT);
        List<Vehicle> ordered = orderedSearchVehicles(q);
        int shownAll = 0;
        Set<String> popularIds = new HashSet<>();

        if (q.isEmpty()) {
            List<Vehicle> popular = popularVehicles(ordered);
            if (!popular.isEmpty()) {
                TextView h = tv("⭐  Más buscados", 13, blue);
                h.setTypeface(null, Typeface.BOLD);
                h.setPadding(dp(18), dp(12), dp(18), dp(8));
                list.addView(h, new LinearLayout.LayoutParams(-1, -2));

                Set<String> shownModels = new HashSet<>();
                for (Vehicle v : popular) {
                    String mk = popularKey(v).toLowerCase(Locale.ROOT);
                    if (shownModels.contains(mk)) continue;
                    addSearchItem(v, input, list);
                    shownModels.add(mk);
                    popularIds.add(v.id);
                    if (shownModels.size() >= 10) break;
                }

                TextView allH = tv("Todos los coches", 13, blue);
                allH.setTypeface(null, Typeface.BOLD);
                allH.setPadding(dp(18), dp(16), dp(18), dp(8));
                list.addView(allH, new LinearLayout.LayoutParams(-1, -2));
            }
        }

        for (Vehicle v : ordered) {
            if (popularIds.contains(v.id)) continue;
            addSearchItem(v, input, list);
            if (++shownAll >= 80) break;
        }

        if (shownAll == 0 && popularIds.isEmpty()) {
            TextView empty = tv("No hay coincidencias en " + marketLabel(selectedMarket) + ".", 14, sub());
            empty.setPadding(dp(18), dp(20), dp(18), dp(20));
            list.addView(empty, new LinearLayout.LayoutParams(-1, -2));
        }
    }

    private void addSearchItem(Vehicle v, EditText input, LinearLayout list) {
        SpannableString label = new SpannableString(searchLabel(v));
        int nl = label.toString().indexOf("\n");
        if (nl >= 0) {
            label.setSpan(new StyleSpan(Typeface.BOLD), 0, nl, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            label.setSpan(new RelativeSizeSpan(0.87f), nl + 1, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            label.setSpan(new StyleSpan(Typeface.BOLD), 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        TextView item = tv(label.toString(), 15, text());
        item.setText(label);
        item.setLineSpacing(0, 1.08f);
        item.setPadding(dp(18), dp(10), dp(18), dp(10));
        item.setBackgroundColor(dark ? Color.rgb(15, 27, 39) : Color.WHITE);
        item.setOnClickListener(x -> {
            if (selectedIds.size() < 3) {
                SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
                p.edit().putInt(KEY_SEARCH_COUNT_PREFIX + v.id,
                        p.getInt(KEY_SEARCH_COUNT_PREFIX + v.id, 0) + 1).apply();
                selectedIds.add(v.id);
                saveSelection();
                AlertDialog dialog = (AlertDialog) input.getTag();
                if (dialog != null && dialog.isShowing()) dialog.dismiss();
                rebuild();
            }
        });
        list.addView(item, new LinearLayout.LayoutParams(-1, -2));
    }

    private String value(Vehicle v, String key) {
        if ("battery".equals(key)) return fmt(v.batteryKwh) + " kWh";
        if ("type".equals(key)) return empty(v.batteryType);
        if ("range".equals(key)) return v.wltpKm > 0 ? v.wltpKm + " km" : "—";
        if ("cons".equals(key)) return v.consumption > 0 ? fmt(v.consumption) + " kWh/100 km" : "—";
        if ("power".equals(key)) return v.powerKw > 0 ? fmt(v.powerKw) + " kW" : "—";
        if ("drive".equals(key)) return empty(v.drivetrain);
        if ("acc".equals(key)) return v.acc > 0 ? fmt(v.acc) + " s" : "—";
        if ("ac".equals(key)) return v.acKw > 0 ? fmt(v.acKw) + " kW" : "—";
        if ("dc".equals(key)) return v.dcKw > 0 ? fmt(v.dcKw) + " kW" : "—";
        if ("charge".equals(key)) return v.chargeMin > 0 ? v.chargeMin + " min" : "—";
        if ("trunk".equals(key)) return v.trunk > 0 ? v.trunk + " L" : "—";
        if ("weight".equals(key)) return v.weight > 0 ? v.weight + " kg" : "—";
        if ("price".equals(key)) return formatPrice(v.price);
        return "—";
    }

    private void addRow(String label, String key, boolean higherBetter) {
        LinearLayout r = new LinearLayout(this);
        r.setOrientation(LinearLayout.HORIZONTAL);
        r.setGravity(Gravity.CENTER_VERTICAL);
        r.setBackgroundColor(rowAlt());

        TextView l = tv(label, 13, text());
        l.setPadding(dp(5), dp(10), dp(5), dp(10));
        r.addView(l, new LinearLayout.LayoutParams(dp(112), dp(52)));

        List<Vehicle> chosen = new ArrayList<>();
        for (String id : selectedIds) {
            Vehicle v = find(id);
            if (v != null) chosen.add(v);
        }

        double best = Double.NaN;
        for (Vehicle v : chosen) {
            double n = numeric(v, key);
            if (Double.isNaN(n)) continue;
            if (Double.isNaN(best) || (higherBetter ? n > best : n < best)) best = n;
        }

        for (Vehicle v : chosen) {
            TextView cell = tv(value(v, key), 12, text());
            cell.setGravity(Gravity.CENTER);
            cell.setPadding(dp(4), 0, dp(4), 0);
            double n = numeric(v, key);
            if (selectedIds.size() >= 2 && !Double.isNaN(best) && !Double.isNaN(n)
                    && Math.abs(n - best) < 0.0001) {
                cell.setTextColor(blue);
            }
            r.addView(cell, new LinearLayout.LayoutParams(dp(145), dp(52)));
        }
        table.addView(r, new LinearLayout.LayoutParams(tableWidth(), dp(52)));
    }

    private double numeric(Vehicle v, String key) {
        if ("battery".equals(key)) return v.batteryKwh;
        if ("range".equals(key)) return v.wltpKm;
        if ("cons".equals(key)) return v.consumption;
        if ("power".equals(key)) return v.powerKw;
        if ("acc".equals(key)) return v.acc;
        if ("ac".equals(key)) return v.acKw;
        if ("dc".equals(key)) return v.dcKw;
        if ("charge".equals(key)) return v.chargeMin;
        if ("trunk".equals(key)) return v.trunk;
        if ("weight".equals(key)) return v.weight;
        if ("price".equals(key)) return v.price;
        return Double.NaN;
    }

    private void buildSummary() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        card.setBackground(strokeBg(
                dark ? Color.rgb(17, 31, 44) : Color.WHITE,
                dark ? Color.rgb(43, 64, 82) : Color.rgb(218, 228, 239),
                18
        ));

        TextView h = tv("Resumen de la comparativa", 18, text());
        h.setTypeface(null, Typeface.BOLD);
        card.addView(h, new LinearLayout.LayoutParams(-1, dp(30)));
        TextView intro = tv("Resultado rápido de los vehículos seleccionados", 13, sub());
        card.addView(intro, new LinearLayout.LayoutParams(-1, dp(28)));

        addSummaryWinner(card, "Autonomía", "range", true);
        addSummaryWinner(card, "Consumo", "cons", false);
        addSummaryWinner(card, "Potencia", "power", true);
        addSummaryWinner(card, "Carga DC", "dc", true);
        addSummaryWinner(card, "Precio", "price", false);

        TextView selected = tv("Vehículos comparados", 14, blue);
        selected.setTypeface(null, Typeface.BOLD);
        selected.setIncludeFontPadding(true);
        selected.setPadding(0, dp(4), 0, dp(4));
        card.addView(selected, new LinearLayout.LayoutParams(-1, dp(36)));

        for (String id : selectedIds) {
            Vehicle v = find(id);
            if (v == null) continue;
            TextView s = tv(
                    "•  " + v.make + " " + v.model + " · " + v.version + "  ·  " + marketLabel(v.market),
                    13,
                    text()
            );
            s.setPadding(0, dp(3), 0, dp(3));
            card.addView(s, new LinearLayout.LayoutParams(-1, -2));
        }
        summary.addView(card, new LinearLayout.LayoutParams(-1, -2));

        LinearLayout commentCard = new LinearLayout(this);
        commentCard.setOrientation(LinearLayout.VERTICAL);
        commentCard.setPadding(dp(16), dp(14), dp(16), dp(14));
        commentCard.setBackground(strokeBg(
                dark ? Color.rgb(17, 31, 44) : Color.WHITE,
                dark ? Color.rgb(43, 64, 82) : Color.rgb(218, 228, 239),
                18
        ));
        TextView commentTitle = tv("Comentario", 16, blue);
        commentTitle.setTypeface(null, Typeface.BOLD);
        commentTitle.setPadding(0, 0, 0, dp(6));
        commentCard.addView(commentTitle, new LinearLayout.LayoutParams(-1, dp(30)));
        TextView comment = tv(summaryComment(), 13, sub());
        comment.setLineSpacing(0, 1.2f);
        commentCard.addView(comment, new LinearLayout.LayoutParams(-1, -2));
        LinearLayout.LayoutParams commentLp = new LinearLayout.LayoutParams(-1, -2);
        commentLp.setMargins(0, dp(10), 0, 0);
        summary.addView(commentCard, commentLp);
    }

    private void addSummaryWinner(LinearLayout parent, String label, String key, boolean higherBetter) {
        List<Vehicle> chosen = new ArrayList<>();
        for (String id : selectedIds) {
            Vehicle v = find(id);
            if (v != null) chosen.add(v);
        }

        Vehicle bestV = null;
        double best = Double.NaN;
        for (Vehicle v : chosen) {
            double n = numeric(v, key);
            if (Double.isNaN(n)) continue;
            if (Double.isNaN(best) || (higherBetter ? n > best : n < best)) {
                best = n;
                bestV = v;
            }
        }
        if (bestV == null) return;

        String val = value(bestV, key);
        TextView row = tv(
                label + "  ·  " + bestV.make + " " + bestV.model + "  →  " + val,
                13,
                text()
        );
        row.setPadding(0, dp(4), 0, dp(4));
        parent.addView(row, new LinearLayout.LayoutParams(-1, dp(30)));
    }

    private String summaryComment() {
        List<Vehicle> chosen = new ArrayList<>();
        for (String id : selectedIds) {
            Vehicle v = find(id);
            if (v != null) chosen.add(v);
        }
        if (chosen.size() < 2) return "";

        Vehicle bestRange = bestVehicle("range", true);
        Vehicle bestCons = bestVehicle("cons", false);
        Vehicle bestPrice = bestVehicle("price", false);
        StringBuilder out = new StringBuilder();
        out.append("En conjunto, ");
        if (bestRange != null) {
            out.append(bestRange.make).append(" ").append(bestRange.model).append(" destaca por autonomía");
        }
        if (bestCons != null && bestCons != bestRange) {
            out.append(", mientras que ").append(bestCons.make).append(" ").append(bestCons.model)
                    .append(" ofrece el menor consumo");
        }
        if (bestPrice != null && bestPrice != bestRange && bestPrice != bestCons) {
            out.append(" y ").append(bestPrice.make).append(" ").append(bestPrice.model)
                    .append(" es la opción más económica");
        }
        out.append(". La elección final dependerá de si priorizas autonomía, eficiencia, prestaciones, velocidad de carga o precio.");
        return out.toString();
    }

    private Vehicle bestVehicle(String key, boolean higherBetter) {
        Vehicle bestV = null;
        double best = Double.NaN;
        for (String id : selectedIds) {
            Vehicle v = find(id);
            if (v == null) continue;
            double n = numeric(v, key);
            if (Double.isNaN(n)) continue;
            if (Double.isNaN(best) || (higherBetter ? n > best : n < best)) {
                best = n;
                bestV = v;
            }
        }
        return bestV;
    }

    private String empty(String s) {
        return s == null || s.trim().isEmpty() ? "—" : s;
    }

    private String fmt(double n) {
        return String.format(Locale.US, "%.1f", n).replace('.', ',');
    }

    private String formatPrice(double p) {
        if (p <= 0) return "—";
        String currency = getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_CURRENCY, "EUR");
        return String.format(Locale.US, "%,.0f %s", p, currency).replace(',', '.');
    }

    static class Vehicle {
        String id, make, model, version, batteryType, drivetrain, market;
        int year;
        double price, batteryKwh, usableBatteryKwh, wltpKm, consumption, powerKw,
                acKw, dcKw, chargeMin, acc, trunk, weight;

        Vehicle(JSONObject o) {
            id = o.optString("id", UUID.randomUUID().toString());
            make = o.optString("make", o.optString("brand", ""));
            model = o.optString("model", "");
            version = o.optString("version", o.optString("trim", ""));
            batteryType = o.optString("batteryType", "");
            drivetrain = o.optString("drivetrain", "");
            market = o.optString("market", o.optString("mercado", "ES"))
                    .toUpperCase(Locale.ROOT);
            year = o.optInt("year", o.optInt("modelYear", 0));
            price = o.optDouble("price", 0);
            batteryKwh = o.optDouble("batteryKwh", o.optDouble("battery_capacity_kwh", 0));
            usableBatteryKwh = o.optDouble("usableBatteryKwh", 0);
            wltpKm = o.optDouble("wltpKm", o.optDouble("rangeKm", 0));
            consumption = o.optDouble("consumption", 0);
            powerKw = o.optDouble("powerKw", 0);
            acKw = o.optDouble("acKw", 0);
            dcKw = o.optDouble("dcKw", 0);
            chargeMin = o.optDouble("chargeMin", 0);
            acc = o.optDouble("acc", o.optDouble("acceleration", 0));
            trunk = o.optDouble("trunk", o.optDouble("trunkLiters", 0));
            weight = o.optDouble("weight", 0);
        }
    }
}
