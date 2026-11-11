package com.evchargecalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
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
    private static final String KEY_LAST_ERROR = "currency_rates_last_error";
    private static final String TAG = "CurrencyRateManager";
    public static final String ACTION_RATES_UPDATED =
            "com.evcalculatorpro.action.CURRENCY_RATES_UPDATED";
    private static final String API =
            "https://data-api.ecb.europa.eu/service/data/EXR/D.%s.EUR.SP00.A?format=csvdata";
    private static final AtomicBoolean REFRESH_IN_PROGRESS = new AtomicBoolean(false);
    private static final AtomicBoolean REFRESH_REQUESTED_WHILE_BUSY = new AtomicBoolean(false);

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
        if (!REFRESH_IN_PROGRESS.compareAndSet(false, true)) {
            // Do not lose a refresh request made while the first attempt is running.
            // If that attempt fails, one queued retry is started after it completes.
            REFRESH_REQUESTED_WHILE_BUSY.set(true);
            return;
        }
        try {
            new Thread(() -> {
                try {
                    SharedPreferences preferences = preferences(app);
                    boolean updated = CurrencyRateRepository.refresh(
                            new PreferencesCache(preferences),
                            CurrencyRateManager::fetchCsv,
                            today(),
                            failure -> {
                                String detail = failure.getClass().getSimpleName()
                                        + ": " + String.valueOf(failure.getMessage());
                                preferences.edit().putString(KEY_LAST_ERROR, detail).apply();
                                Log.w(TAG, "ECB exchange-rate refresh failed", failure);
                            });
                    if (updated) {
                        preferences.edit().remove(KEY_LAST_ERROR).apply();
                        Log.i(TAG, "ECB exchange rates refreshed successfully");
                        // The active catalog screen may have rendered its EUR fallback
                        // while this background download was still running.
                        Intent intent = new Intent(ACTION_RATES_UPDATED);
                        intent.setPackage(app.getPackageName());
                        app.sendBroadcast(intent);
                    }
                } finally {
                    REFRESH_IN_PROGRESS.set(false);
                    if (REFRESH_REQUESTED_WHILE_BUSY.getAndSet(false)) {
                        refreshIfNeeded(app);
                    }
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

    /** Last failed refresh reason, retained for diagnostics until a complete refresh succeeds. */
    public static String lastRefreshError(Context context) {
        return preferences(context).getString(KEY_LAST_ERROR, "");
    }

    /** Human-readable snapshot used by the in-app currency diagnostics. */
    public static String diagnostics(Context context) {
        String selected = preferences(context).getString(KEY_CURRENCY, "EUR");
        Map<String, Double> rates = readCachedRates(context);
        StringBuilder report = new StringBuilder();
        String update = lastUpdate(context);
        report.append("Moneda seleccionada: ").append(selected).append('\n');
        report.append("Última descarga completa: ")
                .append(update.isEmpty() ? "ninguna" : update).append('\n');
        report.append("Tasas guardadas (unidades por 1 EUR):\n");
        report.append("EUR: 1.000000\n");
        for (String currency : CurrencyRateRepository.CURRENCIES) {
            Double value = rates.get(currency);
            report.append(currency).append(": ")
                    .append(value == null ? "NO DISPONIBLE" : String.format(Locale.US, "%.6f", value))
                    .append('\n');
        }
        String error = lastRefreshError(context);
        report.append("Último error: ").append(error.isEmpty() ? "ninguno registrado" : error);
        if (!"EUR".equals(selected)) {
            Double rate = rates.get(selected);
            report.append("\nConversión de prueba (100 EUR): ")
                    .append(rate == null ? "no posible: falta la tasa" :
                            String.format(Locale.US, "%.2f %s", 100.0d * rate, selected));
        }
        return report.toString();
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

    /** Retries one transient ECB/network failure once before failing the atomic refresh. */
    private static String fetchCsv(String currency) throws IOException {
        IOException lastFailure = null;
        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                return fetchCsvOnce(currency);
            } catch (IOException failure) {
                lastFailure = failure;
                if (attempt == 1) {
                    try {
                        Thread.sleep(750L);
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        throw new IOException("ECB retry interrupted for " + currency, interrupted);
                    }
                }
            }
        }
        throw lastFailure != null ? lastFailure
                : new IOException("ECB request failed for " + currency);
    }

    private static String fetchCsvOnce(String currency) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(String.format(Locale.US, API, currency));
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Accept", "text/csv, text/plain;q=0.9, */*;q=0.8");
            connection.setRequestProperty("User-Agent", "EV-Calculator-PRO/1.0.6 Android");
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
