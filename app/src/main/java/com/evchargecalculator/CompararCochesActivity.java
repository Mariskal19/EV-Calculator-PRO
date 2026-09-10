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
        // Apply the persisted language before creating any comparison views.
        LanguageManager.applyStored(this);
        try {
            SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
            dark = p.contains("dark_theme")
                    ? p.getBoolean("dark_theme", false)
                    : (getResources().getConfiguration().uiMode & 0x30) == 0x20;
            selectedMarket = p.getString(KEY_MARKET, "ES");
            build();
            loadVehicles();
            if (!hasMarket(selectedMarket)) {
                selectedMarket = defaultMarket();
            }
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
        // Re-apply the selected language when returning from Configuration.
        LanguageManager.applyStored(this);
        SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
        boolean newDark = p.contains("dark_theme")
                ? p.getBoolean("dark_theme", false)
                : (getResources().getConfiguration().uiMode & 0x30) == 0x20;
        dark = newDark;
        // Rebuild the complete screen, not only the comparison table. The header,
        // introduction and other static labels are created in build() and otherwise
        // keep the language from the previous render.
        if (!vehicles.isEmpty()) {
            build();
            loadSelection();
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
        t.setText(LanguageManager.t(this, s));
        t.setTextSize(size);
        t.setTextColor(color);
        return t;
    }
