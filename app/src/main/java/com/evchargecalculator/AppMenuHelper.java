package com.evchargecalculator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.PopupMenu;

/** Common top-right app menu used on every app screen. */
public final class AppMenuHelper {
  private static final String PLAY_STORE_URL =
      "https://play.google.com/store/apps/details?id=com.evcalculatorpro";

  public interface Listener {
    boolean isDark();

    void setDark(boolean dark);
  }

  private AppMenuHelper() {}

  public static void show(Activity activity, View anchor, Listener listener) {
    PopupMenu popup = new PopupMenu(activity, anchor);
    popup.getMenu().add(0, 10, 1, "⚙  " + configurationLabel(activity));
    popup.getMenu().add(0, 2, 2, "↗  " + LanguageManager.t(activity, "Compartir app"));
    popup.getMenu().add(0, 3, 3, "★  " + LanguageManager.t(activity, "Calificar app"));
    popup.getMenu().add(0, 4, 4, "☕  " + supportLabel(activity));
    popup.setOnMenuItemClickListener(
        item -> {
          if (item.getItemId() == 10) {
            activity.startActivity(new Intent(activity, ConfigurationActivity.class));
            return true;
          }
          if (item.getItemId() == 2) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(
                Intent.EXTRA_TEXT,
                LanguageManager.t(activity, "Descarga EV Calculator PRO en Google Play: ")
                    + PLAY_STORE_URL);
            activity.startActivity(
                Intent.createChooser(share, LanguageManager.t(activity, "Compartir app")));
            return true;
          }
          if (item.getItemId() == 4) {
            activity.startActivity(new Intent(activity, SupportProjectActivity.class));
            return true;
          }
          if (item.getItemId() == 3) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_URL)));
            return true;
          }
          return false;
        });
    popup.show();
  }

  private static String supportLabel(Activity a) {
    String l = LanguageManager.getSelectedLanguage(a);
    if ("es".equals(l)) return "Apoyar el proyecto";
    if ("fr".equals(l)) return "Soutenir le projet";
    if ("de".equals(l)) return "Projekt unterstützen";
    if ("it".equals(l)) return "Supporta il progetto";
    if ("pt".equals(l)) return "Apoiar o projeto";
    return "Support the project";
  }

  private static String configurationLabel(Activity a) {
    String l = LanguageManager.getSelectedLanguage(a);
    if ("es".equals(l)) return "Configuración";
    if ("fr".equals(l)) return "Configuration";
    if ("de".equals(l)) return "Einstellungen";
    if ("it".equals(l)) return "Impostazioni";
    if ("pt".equals(l)) return "Definições";
    return "Settings";
  }
}
