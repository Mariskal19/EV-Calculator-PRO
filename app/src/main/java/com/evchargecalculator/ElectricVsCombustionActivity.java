package com.evchargecalculator;

import android.os.Bundle;

/** Activity compatibility shell; the calculator UI itself is a reusable View. */
public class ElectricVsCombustionActivity extends BaseNavigationActivity {
  private ElectricVsCombustionScreen screen;

  @Override
  protected int getBottomNavigationIndex() { return 1; }

  @Override
  protected void onCreate(Bundle state) {
    super.onCreate(state);
    screen = new ElectricVsCombustionScreen(this);
    setContentView(screen);
    EdgeToEdgeHelper.apply(this, screen.isDarkTheme(), true);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (screen != null) screen.onHostResume();
  }

  @Override
  protected void onPause() {
    if (screen != null) screen.onHostPause();
    super.onPause();
  }

  public void refreshLanguage() {
    if (screen != null) screen.refreshLanguage();
  }
}
