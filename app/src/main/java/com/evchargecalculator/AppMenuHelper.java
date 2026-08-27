package com.evchargecalculator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.PopupMenu;

/** Common top-right app menu used on every app screen. */
public final class AppMenuHelper {
    private static final String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.evcalculatorpro";

    public interface Listener {
        boolean isDark();
        void setDark(boolean dark);
    }

    private AppMenuHelper() {}

    public static void show(Activity activity, View anchor, Listener listener) {
        PopupMenu popup = new PopupMenu(activity, anchor);
        popup.getMenu().add("Compartir app");
        popup.getMenu().add("Calificar app");
        popup.getMenu().add(1, 100, 100, "Tema claro").setCheckable(true).setChecked(!listener.isDark());
        popup.getMenu().add(1, 101, 101, "Tema oscuro").setCheckable(true).setChecked(listener.isDark());
        popup.getMenu().setGroupCheckable(1, true, true);
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 0) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, "Descarga EV Calculator PRO en Google Play: " + PLAY_STORE_URL);
                activity.startActivity(Intent.createChooser(share, "Compartir app"));
                return true;
            }
            if (item.getItemId() == 1) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_URL)));
                return true;
            }
            if (item.getItemId() == 100) {
                listener.setDark(false);
                return true;
            }
            if (item.getItemId() == 101) {
                listener.setDark(true);
                return true;
            }
            return false;
        });
        popup.show();
    }
}