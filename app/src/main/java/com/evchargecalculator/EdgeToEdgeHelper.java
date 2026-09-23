package com.evchargecalculator;

import android.app.Activity;
import android.graphics.Insets;
import android.view.View;
import android.view.Window;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/**
 * Centralized Android 15+ edge-to-edge handling.
 *
 * The app targets SDK 36, so Android 15+ enforces edge-to-edge. This helper
 * also enables the same behavior on older supported Android versions and
 * applies system-bar insets to the activity content so existing screens are
 * not obscured by system bars.
 */
public final class EdgeToEdgeHelper {
  private EdgeToEdgeHelper() {}

  public static void apply(Activity activity, boolean dark) {
    Window window = activity.getWindow();
    WindowCompat.enableEdgeToEdge(window);

    WindowInsetsControllerCompat controller =
        WindowCompat.getInsetsController(window, window.getDecorView());
    controller.setAppearanceLightStatusBars(!dark);
    controller.setAppearanceLightNavigationBars(!dark);

    View content = activity.findViewById(android.R.id.content);
    if (content == null) return;

    final int left = content.getPaddingLeft();
    final int top = content.getPaddingTop();
    final int right = content.getPaddingRight();
    final int bottom = content.getPaddingBottom();

    ViewCompat.setOnApplyWindowInsetsListener(
        content,
        (view, insets) -> {
          android.graphics.Insets bars =
              insets.getInsets(WindowInsetsCompat.Type.systemBars()
                  | WindowInsetsCompat.Type.displayCutout());
          view.setPadding(
              left + bars.left,
              top + bars.top,
              right + bars.right,
              bottom + bars.bottom);
          return insets;
        });
    ViewCompat.requestApplyInsets(content);
  }
}
