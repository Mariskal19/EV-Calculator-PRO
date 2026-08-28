package com.evchargecalculator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import java.util.Locale;

/** Handles the app language. Default is the device language; unsupported languages fall back to English. */
public final class LanguageManager {
    private static final String PREFS = "ev_charge_calculator";
    private static final String KEY_LANGUAGE = "app_language";
    private static final String SYSTEM = "system";
    private LanguageManager() {}

    public static String getSelectedLanguage(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_LANGUAGE, SYSTEM);
    }

    public static String getEffectiveLanguage(Context context) {
        String selected = getSelectedLanguage(context);
        if (!SYSTEM.equals(selected)) return isSupported(selected) ? selected : "en";
        String device = Locale.getDefault().getLanguage();
        return isSupported(device) ? device : "en";
    }

    public static void setLanguage(Context context, String language) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_LANGUAGE, language).apply();
        apply(context, language);
    }

    public static void apply(Context context, String language) {
        String effective = SYSTEM.equals(language) ? getEffectiveLanguage(context) : (isSupported(language) ? language : "en");
        Locale locale = Locale.forLanguageTag(effective);
        Locale.setDefault(locale);
        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static void applyStored(Context context) { apply(context, getSelectedLanguage(context)); }

    public static boolean isSupported(String language) {
        return "en".equals(language) || "es".equals(language) || "fr".equals(language) || "de".equals(language) || "it".equals(language) || "pt".equals(language);
    }

    public static String displayName(String language) {
        if ("en".equals(language)) return "🇬🇧  English";
        if ("es".equals(language)) return "🇪🇸  Español";
        if ("fr".equals(language)) return "🇫🇷  Français";
        if ("de".equals(language)) return "🇩🇪  Deutsch";
        if ("it".equals(language)) return "🇮🇹  Italiano";
        if ("pt".equals(language)) return "🇵🇹  Português";
        return language;
    }

    public static void showSelector(Activity activity) {
        final String[] codes = {"en", "es", "fr", "de", "it", "pt"};
        String current = getSelectedLanguage(activity);
        int checked = -1;
        if (isSupported(current)) for (int i = 0; i < codes.length; i++) if (codes[i].equals(current)) checked = i;
        new android.app.AlertDialog.Builder(activity)
                .setTitle("Idioma")
                .setSingleChoiceItems(new String[]{displayName("en"),displayName("es"),displayName("fr"),displayName("de"),displayName("it"),displayName("pt")}, checked,
                        (dialog, which) -> { setLanguage(activity, codes[which]); dialog.dismiss(); activity.recreate(); })
                .show();
    }
}