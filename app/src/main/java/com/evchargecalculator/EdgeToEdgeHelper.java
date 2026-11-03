package com.evchargecalculator;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ScrollView;
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
  private static final Map<View, Integer> BASE_SCROLL_TOP_PADDING = new WeakHashMap<>();

  private EdgeToEdgeHelper() {}

  private static ScrollView findScrollView(View view) {
    if (view instanceof ScrollView) return (ScrollView) view;
    if (view instanceof ViewGroup) {
      ViewGroup group = (ViewGroup) view;
      for (int i = 0; i < group.getChildCount(); i++) {
        ScrollView found = findScrollView(group.getChildAt(i));
        if (found != null) return found;
      }
    }
    return null;
  }

  public static void apply(Activity activity, boolean dark) {
    apply(activity, dark, false);
  }

  /**
   * @param includeTopInset true for screens whose content must start below the status bar.
   *                        Existing screens retain their current edge-to-edge layout by default.
   */
  public static void apply(Activity activity, boolean dark, boolean includeTopInset) {
    Window window = activity.getWindow();

    // Keep Android's automatic darkening disabled for this activity. The app
    // controls dark mode itself, so raster images (such as the comparison
    // header) must retain their original brightness and colors.
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
      window.getDecorView().setForceDarkAllowed(false);
      window.setNavigationBarContrastEnforced(false);
    }

    // Keep the legacy navigation-bar color only on Android 14 and older.
    // Android 15+ uses edge-to-edge and this API is deprecated/ineffective
    // for gesture navigation.
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.VANILLA_ICE_CREAM) {
      window.setNavigationBarColor(
          dark ? android.graphics.Color.rgb(16, 28, 42)
               : android.graphics.Color.rgb(242, 246, 252));
    }

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
    final int top = basePadding[1];
    final int right = basePadding[2];
    final int bottom = basePadding[3];

    ViewCompat.setOnApplyWindowInsetsListener(
        root,
        (view, insets) -> {
          Insets bars =
              insets.getInsets(WindowInsetsCompat.Type.systemBars()
                  | WindowInsetsCompat.Type.displayCutout());
          // Keep the outer screen (including its full-bleed background) behind
          // the status bar. Apply the top inset to the scrollable foreground
          // instead, so the status-bar area shows the screen background rather
          // than the host window's light background. This is platform-neutral:
          // WindowInsetsCompat supplies the correct inset on each Android version.
          view.setPadding(
              left + bars.left,
              top,
              right + bars.right,
              bottom + bars.bottom);

          if (includeTopInset) {
            ScrollView scroll = findScrollView(view);
            if (scroll != null) {
              Integer baseTop = BASE_SCROLL_TOP_PADDING.get(scroll);
              if (baseTop == null) {
                baseTop = scroll.getPaddingTop();
                BASE_SCROLL_TOP_PADDING.put(scroll, baseTop);
              }
              scroll.setPadding(
                  scroll.getPaddingLeft(),
                  baseTop + bars.top,
                  scroll.getPaddingRight(),
                  scroll.getPaddingBottom());
            }
          }
          return insets;
        });
    // The first request can run before the Activity content is attached to the window.
    // On Android 10 this may leave the initial screen without its status-bar inset
    // until the Activity is resumed/re-entered. Request again as soon as the root
    // is attached, while keeping immediate updates for already attached screens.
    if (ViewCompat.isAttachedToWindow(root)) {
      ViewCompat.requestApplyInsets(root);
    } else {
      root.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View view) {
          view.removeOnAttachStateChangeListener(this);
          ViewCompat.requestApplyInsets(view);
        }

        @Override
        public void onViewDetachedFromWindow(View view) { }
      });
    }
  }
}
