package com.evchargecalculator;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/**
 * Centralized Android 15+ edge-to-edge handling.
 *
 * The app targets SDK 36, so Android 15+ enforces edge-to-edge. This helper
 * also enables the same behavior on older supported Android versions and
 * applies system-bar insets to the activity content so existing screens
 * are not obscured by system bars.
 */
public final class EdgeToEdgeHelper {
  private EdgeToEdgeHelper() {}

  public static void apply(Activity activity, boolean dark) {
    Window window = activity.getWindow();
    WindowCompat.setDecorFitsSystemWindows(window, false);


    WindowInsetsControllerCompat controller =
        WindowCompat.getInsetsController(window, window.getDecorView());
    controller.setAppearanceLightStatusBars(!dark);
    controller.setAppearanceLightNavigationBars(!dark);

    View content = activity.findViewById(android.R.id.content);
    if (content == null) return;

    // Apply insets to the actual activity root, not the decor content
    // container. Padding the decor container creates a visible strip between
    // the status bar and the screen header, exposing the window background.
    View root = content;
    if (content instanceof ViewGroup && ((ViewGroup) content).getChildCount() > 0) {
      root = ((ViewGroup) content).getChildAt(0);
    }

    final int left = root.getPaddingLeft();
    final int right = root.getPaddingRight();
    final int bottom = root.getPaddingBottom();

    ViewCompat.setOnApplyWindowInsetsListener(
        root,
        (view, insets) -> {
          Insets bars =
              insets.getInsets(WindowInsetsCompat.Type.systemBars()
                  | WindowInsetsCompat.Type.displayCutout());
          // Keep the top edge-to-edge so the header/background reaches the
          // status bar. Protect the sides and bottom from system UI instead.
          view.setPadding(
              left + bars.left,
              0,
              right + bars.right,
              bottom + bars.bottom);
          return insets;
        });
    ViewCompat.requestApplyInsets(root);
  }
}
