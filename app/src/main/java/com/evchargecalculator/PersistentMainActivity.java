package com.evchargecalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import java.lang.reflect.Field;

/** Ensures the user's selected light/dark theme is shared across screens. */
public class PersistentMainActivity extends MainActivity {
    private static final String PREFS = "ev_charge_calculator";
    private static final String KEY_DARK_THEME = "dark_theme";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (prefs.contains(KEY_DARK_THEME)) {
            boolean dark = prefs.getBoolean(KEY_DARK_THEME, false);
            Configuration config = new Configuration(getResources().getConfiguration());
            config.uiMode = (config.uiMode & ~Configuration.UI_MODE_NIGHT_MASK)
                    | (dark ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        saveSelectedTheme();
        super.onPause();
    }

    private void saveSelectedTheme() {
        try {
            Field field = MainActivity.class.getDeclaredField("dark");
            field.setAccessible(true);
            boolean dark = field.getBoolean(this);
            getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(KEY_DARK_THEME, dark)
                    .apply();
        } catch (Exception ignored) {
            // Keep normal activity behaviour if the implementation changes.
        }
    }
}
