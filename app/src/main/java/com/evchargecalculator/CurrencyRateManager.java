package com.evchargecalculator;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.json.JSONObject;

/** Daily ECB reference-rate cache. EUR is the base currency. */
public final class CurrencyRateManager {
    private static final String PREFS = "ev_charge_calculator";
    private static final String KEY_RATES = "currency_rates_eur";
    private static final String KEY_DATE = "currency_rates_date";
    private static final String KEY_REFRESH = "currency_rates_last_attempt";
    private static final String API = "https://data-api.ecb.europa.eu/service/data/EXR/D.%s.EUR.SP00.A?format=csvdata";
    private static final String[] CURRENCIES = {"USD", "GBP", "CHF", "CAD", "AUD"};

    private CurrencyRateManager() {}

    public static void refreshIfNeeded(Context context) {
        SharedPreferences p = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String today = today();
        if (today.equals(p.getString(KEY_REFRESH, ""))) return;
        p.edit().putString(KEY_REFRESH, today).apply();
        refreshAsync(context);
    }

    public static void refreshAsync(Context context) {
        final Context app = context.getApplicationContext();
        new Thread(() -> {
            try {
                JSONObject rates = new JSONObject();
                for (String currency : CURRENCIES) {
                    Double rate = fetchLatest(currency);
                    if (rate != null && rate > 0) rates.put(currency, rate);
                }
                if (rates.length() > 0) {
                    app.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                            .putString(KEY_RATES, rates.toString())
                            .putString(KEY_DATE, today())
                            .apply();
                }
            } catch (Exception ignored) {
                // Offline or source failure: keep the last valid cache.
            }
        }, "currency-rate-refresh").start();
    }

    public static double rate(Context context, String currency) {
        if (currency == null || "EUR".equals(currency)) return 1.0;
        try {
            JSONObject rates = new JSONObject(context.getApplicationContext()
                    .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .getString(KEY_RATES, "{}"));
            return rates.optDouble(currency, 1.0);
        } catch (Exception ignored) {
            return 1.0;
        }
    }

    public static double convertFromEur(Context context, double eurAmount, String currency) {
        return eurAmount * rate(context, currency);
    }

    public static String lastUpdate(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_DATE, "");
    }

    private static Double fetchLatest(String currency) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(String.format(Locale.US, API, currency));
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) return null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            String lastValue = null;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("KEY,")) continue;
                String[] fields = line.split(",", -1);
                if (fields.length >= 8) lastValue = fields[fields.length - 1].trim();
            }
            reader.close();
            return lastValue == null ? null : Double.parseDouble(lastValue);
        } catch (Exception ignored) {
            return null;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private static String today() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }
}
