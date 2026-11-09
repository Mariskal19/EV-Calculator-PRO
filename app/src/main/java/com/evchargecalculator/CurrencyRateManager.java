package com.evchargecalculator;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONObject;

/** Daily ECB reference-rate cache. Stored rates are foreign-currency units per EUR. */
public final class CurrencyRateManager {
    private static final String PREFS = "ev_charge_calculator";
    private static final String KEY_RATES = "currency_rates_eur";
    private static final String KEY_DATE = "currency_rates_date";
    private static final String KEY_CURRENCY = "app_currency";
    private static final String API =
            "https://data-api.ecb.europa.eu/service/data/EXR/D.%s.EUR.SP00.A?format=csvdata";
    private static final AtomicBoolean REFRESH_IN_PROGRESS = new AtomicBoolean(false);

    private CurrencyRateManager() {}

    public static void refreshIfNeeded(Context context) {
        SharedPreferences preferences = preferences(context);
        CurrencyRateRepository.Cache cache = new PreferencesCache(preferences);
        if (today().equals(CurrencyRateRepository.lastUpdate(cache))) return;
        refreshAsync(context);
    }

    /** Starts one background refresh. A failed attempt is not persisted as a daily success. */
    public static void refreshAsync(Context context) {
        final Context app = context.getApplicationContext();
        if (!REFRESH_IN_PROGRESS.compareAndSet(false, true)) return;
        try {
            new Thread(() -> {
                try {
                    SharedPreferences preferences = preferences(app);
                    CurrencyRateRepository.refresh(
                            new PreferencesCache(preferences),
                            CurrencyRateManager::fetchCsv,
                            today());
                } finally {
                    REFRESH_IN_PROGRESS.set(false);
                }
            }, "currency-rate-refresh").start();
        } catch (RuntimeException ex) {
            REFRESH_IN_PROGRESS.set(false);
            throw ex;
        }
    }

    /** Returns a valid quote or fails explicitly when the requested quote is unavailable. */
    public static double rate(Context context, String currency) {
        return CurrencyRateRepository.rate(readCachedRates(context), currency);
    }

    public static double convertFromEur(Context context, double eurAmount, String currency) {
        return CurrencyRateRepository.convertFromEur(readCachedRates(context), eurAmount, currency);
    }

    /** Converts between supported currencies through the EUR reference rate. */
    public static double convert(Context context, double amount, String from, String to) {
        return CurrencyRateRepository.convert(readCachedRates(context), amount, from, to);
    }

    /** Formats an immutable EUR catalog price in the selected currency when a cached rate exists. */
    public static String formatCatalogPrice(Context context, double priceEur) {
        SharedPreferences preferences = preferences(context);
        String currency = preferences.getString(KEY_CURRENCY, "EUR");
        return CatalogPriceFormatter.format(priceEur, currency, readCachedRates(context));
    }

    /** Date of the last complete validated cache replacement; empty if no valid full cache exists. */
    public static String lastUpdate(Context context) {
        return CurrencyRateRepository.lastUpdate(new PreferencesCache(preferences(context)));
    }

    private static SharedPreferences preferences(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static Map<String, Double> readCachedRates(Context context) {
        Map<String, Double> result = new LinkedHashMap<>();
        try {
            JSONObject json = new JSONObject(preferences(context).getString(KEY_RATES, "{}"));
            for (String currency : CurrencyRateRepository.CURRENCIES) {
                if (!json.has(currency) || json.isNull(currency)) continue;
                double value = json.getDouble(currency);
                if (Double.isFinite(value) && value > 0.0d) result.put(currency, value);
            }
        } catch (Exception ignored) {
            // A malformed cache is treated as having no usable quotes.
        }
        return result;
    }

    private static String fetchCsv(String currency) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(String.format(Locale.US, API, currency));
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setRequestMethod("GET");
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new IOException("ECB returned HTTP " + status + " for " + currency);
            }
            StringBuilder csv = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) csv.append(line).append('\n');
            }
            return csv.toString();
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private static String today() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    private static final class PreferencesCache implements CurrencyRateRepository.Cache {
        private final SharedPreferences preferences;

        PreferencesCache(SharedPreferences preferences) {
            this.preferences = preferences;
        }

        @Override public Map<String, Double> readRates() {
            Map<String, Double> result = new LinkedHashMap<>();
            try {
                JSONObject json = new JSONObject(preferences.getString(KEY_RATES, "{}"));
                for (String currency : CurrencyRateRepository.CURRENCIES) {
                    if (!json.has(currency) || json.isNull(currency)) continue;
                    double value = json.getDouble(currency);
                    if (Double.isFinite(value) && value > 0.0d) result.put(currency, value);
                }
            } catch (Exception ignored) {
                // Invalid persisted JSON is equivalent to an empty cache.
            }
            return result;
        }

        @Override public String lastSuccessfulUpdate() {
            return preferences.getString(KEY_DATE, "");
        }

        @Override public void saveRates(Map<String, Double> rates, String updateDate) {
            JSONObject json = new JSONObject();
            try {
                for (String currency : CurrencyRateRepository.CURRENCIES) {
                    json.put(currency, rates.get(currency));
                }
            } catch (Exception ex) {
                throw new IllegalStateException("Could not serialize complete exchange rates", ex);
            }
            preferences.edit()
                    .putString(KEY_RATES, json.toString())
                    .putString(KEY_DATE, updateDate)
                    .apply();
        }
    }
}
