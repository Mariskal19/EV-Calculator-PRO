package com.evchargecalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

/** Ensures the user's selected light/dark theme is shared across screens. */
public class PersistentMainActivity extends MainActivity {
  private static final String PREFS = "ev_charge_calculator";
  private static final String KEY_DARK_THEME = "dark_theme";

  @Override
  @SuppressWarnings("deprecation")
  public void onCreate(Bundle savedInstanceState) {
    SharedPreferences prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    if (prefs.contains(KEY_DARK_THEME)) {
      boolean dark = prefs.getBoolean(KEY_DARK_THEME, false);
      Configuration config = new Configuration(getResources().getConfiguration());
      config.uiMode =
          (config.uiMode & ~Configuration.UI_MODE_NIGHT_MASK)
              | (dark ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO);
      getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
    CurrencyRateManager.refreshIfNeeded(this);
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void onResume() {
    super.onResume();
    CurrencyRateManager.refreshIfNeeded(this);
    // BaseNavigationActivity owns configuration-change recreation. Do not
    // rebuild the launcher content here: doing so immediately after
    // super.onResume() can race with recreate() on Android 10 and briefly
    // replace a restored secondary tab with the default Calculate screen.
  }
}
