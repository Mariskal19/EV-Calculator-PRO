package com.evchargecalculator;

import android.content.Context;
import android.os.Handler;
import android.content.SharedPreferences;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

final class RemoteCatalogManager {
    private static final String REMOTE_URL =
            "https://raw.githubusercontent.com/Mariskal19/EV-Calculator-PRO/main/app/src/main/assets/catalog_remote_additions.json";
    private static final String CACHE_FILE = "catalog_remote_additions.json";
    private static final String PREFS = "remote_catalog";
    private static final String KEY_LAST_CHECK = "last_check_ms";
    private static final long CHECK_INTERVAL_MS = 24L * 60L * 60L * 1000L;

    private RemoteCatalogManager() {}

    static JSONArray loadCached(Context context) {
        File f = new File(context.getFilesDir(), CACHE_FILE);
        if (!f.isFile()) return new JSONArray();
        try (InputStream in = new FileInputStream(f);
             BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) sb.append(line);
            return parseVehicles(sb.toString());
        } catch (Exception ignored) {
            return new JSONArray();
        }
    }

    static void refreshIfDue(Context context, Callback callback) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        long last = prefs.getLong(KEY_LAST_CHECK, 0L);
        if (System.currentTimeMillis() - last < CHECK_INTERVAL_MS) {
            callback.onComplete(null);
            return;
        }
        prefs.edit().putLong(KEY_LAST_CHECK, System.currentTimeMillis()).apply();
        refresh(context, callback);
    }

    static void refresh(Context context, Callback callback) {
        new Thread(() -> {
            JSONArray result = null;
            try {
                HttpURLConnection c = (HttpURLConnection) new URL(REMOTE_URL).openConnection();
                c.setConnectTimeout(10000);
                c.setReadTimeout(15000);
                c.setRequestMethod("GET");
                c.setRequestProperty("User-Agent", "EV-Calculator-PRO-Android");
                c.setUseCaches(false);
                if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    try (InputStream in = c.getInputStream();
                         BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = r.readLine()) != null) sb.append(line);
                        result = parseVehicles(sb.toString());
                        if (result != null) writeCache(context, sb.toString());
                    }
                }
                c.disconnect();
            } catch (Exception ignored) {
            }

            JSONArray finalResult = result;
            new Handler(Looper.getMainLooper()).post(() -> callback.onComplete(finalResult));
        }).start();
    }

    private static JSONArray parseVehicles(String json) {
        try {
            JSONObject root = new JSONObject(json);
            JSONArray vehicles = root.optJSONArray("vehicles");
            return vehicles != null ? vehicles : new JSONArray();
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    private static void writeCache(Context context, String json) {
        File tmp = new File(context.getFilesDir(), CACHE_FILE + ".tmp");
        File dst = new File(context.getFilesDir(), CACHE_FILE);
        try (FileOutputStream out = new FileOutputStream(tmp)) {
            out.write(json.getBytes(StandardCharsets.UTF_8));
            out.flush();
            if (!tmp.renameTo(dst)) {
                try (FileOutputStream out2 = new FileOutputStream(dst)) {
                    out2.write(json.getBytes(StandardCharsets.UTF_8));
                }
                //noinspection ResultOfMethodCallIgnored
                tmp.delete();
            }
        } catch (Exception ignored) {
            // Keep the previous cached catalog if the write fails.
        }
    }

    interface Callback {
        void onComplete(JSONArray additions);
    }
}
