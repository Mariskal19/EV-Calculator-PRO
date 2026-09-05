package com.evchargecalculator;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Exchange-rate cache. EUR is the base currency and rates are obtained from
 * the ECB daily XML/JSON-compatible data service. Network failures never
 * invalidate the last known rates.
 */
public final class CurrencyRateManager {
    private static final String PREFS = "ev_charge_calculator";
    private static final String KEY_RATES = "currency_rates_eur";
    private static final String KEY_DATE = "currency_rates_date";
    private static final String API = "https://data-api.ecb.europa.eu/service/data/EXR/D.%s.EUR.SP00.A?format=jsondata";
    private static final String[] CURRENCIES = {"USD", "GBP", "CHF", "CAD", "AUD"};

    private CurrencyRateManager() {}

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
                    SharedPreferences p = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
                    p.edit().putString(KEY_RATES, rates.toString()).putString(KEY_DATE, today()).apply();
                }
            } catch (Exception ignored) {
                // Keep the previous cache when the network/source is unavailable.
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
        } catch (Exception e) {
            return 1.0;
        }
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) body.append(line);
            reader.close();
            JSONObject json = new JSONObject(body.toString());
            JSONObject dataSets = json.optJSONArray("data") != null ? json : json;
            // ECB's JSONData response exposes observations in the dataSets/series structure.
            JSONObject data = json.optJSONArray("data") != null ? json : json;
            return extractObservation(json);
        } catch (Exception ignored) {
            return null;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private static Double extractObservation(JSONObject json) {
        try {
            org.json.JSONArray data = json.optJSONArray("data");
            if (data != null && data.length() > 0) {
                JSONObject first = data.getJSONObject(data.length() - 1);
                org.json.JSONArray values = first.optJSONArray("observations");
                if (values != null && values.length() > 0) return values.getDouble(values.length() - 1);
            }
            JSONObject dataObj = json.optJSONObject("data");
            if (dataObj != null) {
                org.json.JSONArray values = dataObj.optJSONArray("observations");
                if (values != null && values.length() > 0) return values.getDouble(values.length() - 1);
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static String today() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new java.util.Date());
    }
}
