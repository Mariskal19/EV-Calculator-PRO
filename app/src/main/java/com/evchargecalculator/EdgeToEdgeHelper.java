package com.evchargecalculator;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import java.util.Map;
import java.util.WeakHashMap;

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
  // Stores the padding defined by each activity before system-bar insets are applied.
  // This prevents repeated apply() calls from accumulating the same bottom inset.
  private static final Map<View, int[]> BASE_PADDING = new WeakHashMap<>();

  private EdgeToEdgeHelper() {}

  @SuppressWarnings("deprecation")
  public static void apply(Activity activity, boolean dark) {
    Window window = activity.getWindow();

    // Keep Android's automatic darkening disabled for this activity. The app
    // controls dark mode itself, so raster images (such as the comparison
    // header) must retain their original brightness and colors.
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
      window.getDecorView().setForceDarkAllowed(false);
      window.setNavigationBarContrastEnforced(false);
    }

    // The Android system navigation area must use the same surface as the
    // app's bottom navigation, including in dark mode.
    window.setNavigationBarColor(
        dark ? android.graphics.Color.rgb(16, 28, 42)
             : android.graphics.Color.rgb(242, 246, 252));

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
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
      root.setForceDarkAllowed(false);
    }
    if (content instanceof ViewGroup && ((ViewGroup) content).getChildCount() > 0) {
      root = ((ViewGroup) content).getChildAt(0);
    }

    int[] basePadding = BASE_PADDING.get(root);
    if (basePadding == null) {
      basePadding = new int[] {
        root.getPaddingLeft(),
        root.getPaddingTop(),
        root.getPaddingRight(),
        root.getPaddingBottom()
      };
      BASE_PADDING.put(root, basePadding);
    }
    final int left = basePadding[0];
    final int right = basePadding[2];
    final int bottom = basePadding[3];

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
