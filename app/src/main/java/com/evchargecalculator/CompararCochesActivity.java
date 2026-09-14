package com.evchargecalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

public class CompararCochesActivity extends Activity {

    private static final String PREFS = "ev_charge_calculator";
    private static final String KEY_SELECTED = "compare_vehicle_ids";
    private static final String KEY_SELECTED_ORDERED = "compare_vehicle_ids_ordered";
    private static final String KEY_SELECTED_LOGICAL = "compare_vehicle_logical_ordered";
    private static final String KEY_CURRENCY = "app_currency";
    private static final String KEY_MARKET = "compare_market";
    private static final String KEY_SEARCH_COUNT_PREFIX = "compare_search_count_";

    private final int blue = Color.rgb(46, 107, 255);
    private final int white = Color.rgb(22, 42, 63);
    private final int secondary = Color.rgb(90, 111, 137);

    private boolean dark;
    private String lastLanguage = "";
    private String lastCurrency = "";
    private LinearLayout carsRow, table, summary;
    private Spinner marketSpinner;

    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<String> selectedIds = new ArrayList<>();
    private String selectedMarket = "ES";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        LanguageManager.applyStored(this);
        try {
            SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
            lastLanguage = LanguageManager.getSelectedLanguage(this);
            lastCurrency = p.getString(KEY_CURRENCY, "EUR");
            dark = p.contains("dark_theme")
                    ? p.getBoolean("dark_theme", false)
                    : (getResources().getConfiguration().uiMode & 0x30) == 0x20;
            selectedMarket = p.getString(KEY_MARKET, "ES");
            build();
            loadVehicles();
            if (!hasMarket(selectedMarket)) selectedMarket = defaultMarket();
            loadSelection();
            rebuild();
        } catch (Throwable t) {
            TextView e = tv("Error al abrir Comparar coches\n\n" + t.getClass().getSimpleName(), 16, Color.WHITE);
            e.setGravity(Gravity.CENTER);
            e.setPadding(dp(24), dp(24), dp(24), dp(24));
            e.setBackgroundColor(Color.rgb(8, 34, 58));
            setContentView(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LanguageManager.applyStored(this);
        SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
        String newLanguage = LanguageManager.getSelectedLanguage(this);
        String newCurrency = p.getString(KEY_CURRENCY, "EUR");
        boolean languageChanged = !newLanguage.equals(lastLanguage);
        boolean currencyChanged = !newCurrency.equals(lastCurrency);
        boolean newDark = p.contains("dark_theme") ? p.getBoolean("dark_theme", false) : (getResources().getConfiguration().uiMode & 0x30) == 0x20;
        boolean themeChanged = dark != newDark;
        dark = newDark;
        lastLanguage = newLanguage;
        lastCurrency = newCurrency;
        if (!vehicles.isEmpty()) {
            loadSelection();
            if (themeChanged || languageChanged || currencyChanged) build();
            rebuild();
        }
    }

    private int dp(int n) { return (int) (n * getResources().getDisplayMetrics().density + 0.5f); }
    private int text() { return dark ? Color.rgb(245, 248, 255) : white; }
    private int sub() { return dark ? Color.rgb(170, 183, 204) : secondary; }
    private int rowAlt() { return dark ? Color.rgb(14, 25, 36) : Color.rgb(248, 251, 255); }
    private int bestBg() { return dark ? Color.rgb(13, 36, 58) : Color.rgb(235, 243, 255); }
    private GradientDrawable bg(int c, float r) { GradientDrawable g = new GradientDrawable(); g.setColor(c); g.setCornerRadius(dp((int) r)); return g; }
    private GradientDrawable strokeBg(int fill, int stroke, float r) { GradientDrawable g = bg(fill, r); g.setStroke(dp(1), stroke); return g; }

    private TextView tv(String s, float size, int color) {
        TextView t = new TextView(this);
        t.setText(LanguageManager.t(this, s));
        t.setTextSize(size);
        t.setTextColor(color);
        return t;
    }

    private void loadVehicles() {
        vehicles.clear();
        try (InputStream in = getAssets().open("catalog_es_2024_2026.json"); BufferedReader r = new BufferedReader(new InputStreamReader(in))) {
            StringBuilder sb = new StringBuilder(); String line;
            while ((line = r.readLine()) != null) sb.append(line);
            JSONArray a = new JSONObject(sb.toString()).optJSONArray("vehicles");
            if (a == null) throw new IllegalStateException("catalog_es_2024_2026.json: vehicles array missing");
            for (int i = 0; i < a.length(); i++) { JSONObject o = a.optJSONObject(i); if (o != null) vehicles.add(new Vehicle(o)); }
        } catch (Exception e) { throw new IllegalStateException("No se ha podido cargar el catálogo español", e); }
        if (vehicles.isEmpty()) throw new IllegalStateException("El catálogo español está vacío");
    }

    private String effectiveBatteryKey(Vehicle v) {
        if (v.batteryKwh > 0) return String.format(Locale.US, "%.1f", v.batteryKwh);
        String s = v.version == null ? "" : v.version.trim();
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("^(\\d+(?:\\.\\d+)?)\\s*kwh\\b", java.util.regex.Pattern.CASE_INSENSITIVE).matcher(s);
        return m.find() ? m.group(1) : "0";
    }

    private String normalizedVersion(Vehicle v) {
        String ver = v.version == null ? "" : v.version.trim().toLowerCase(Locale.ROOT);
        ver = ver.replaceAll("^\\d+(?:\\.\\d+)?\\s*kwh\\s*", "");
        ver = ver.replaceAll("\\s+", " ").trim();
        return ver;
    }

    private String logicalKey(Vehicle v) { return (v.make + "|" + v.model + "|" + v.market + "|" + v.year + "|" + effectiveBatteryKey(v) + "|" + normalizedVersion(v)).trim().toLowerCase(Locale.ROOT); }
    private String stableId(Vehicle v) {
        String key = logicalKey(v);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(key.getBytes(StandardCharsets.UTF_8));
            StringBuilder s = new StringBuilder("catalog-");
            for (int i = 0; i < 8; i++) s.append(String.format(Locale.US, "%02x", hash[i]));
            return s.toString();
        } catch (Exception e) { return "catalog-" + Integer.toHexString(key.hashCode()); }
    }

    private void addOrMergeVehicle(Vehicle incoming) {
        String key = logicalKey(incoming);
        for (Vehicle existing : vehicles) {
            if (!logicalKey(existing).equals(key)) continue;
            if (existing.price <= 0) existing.price = incoming.price;
            if (existing.batteryKwh <= 0) existing.batteryKwh = incoming.batteryKwh;
            if (existing.usableBatteryKwh <= 0) existing.usableBatteryKwh = incoming.usableBatteryKwh;
            if (existing.batteryType.isEmpty()) existing.batteryType = incoming.batteryType;
            if (existing.wltpKm <= 0) existing.wltpKm = incoming.wltpKm;
            if (existing.consumption <= 0) existing.consumption = incoming.consumption;
            if (existing.powerKw <= 0) existing.powerKw = incoming.powerKw;
            if (existing.drivetrain.isEmpty()) existing.drivetrain = incoming.drivetrain;
            if (existing.acKw <= 0) existing.acKw = incoming.acKw;
            if (existing.dcKw <= 0) existing.dcKw = incoming.dcKw;
            if (existing.chargeMin <= 0) existing.chargeMin = incoming.chargeMin;
            if (existing.acc <= 0) existing.acc = incoming.acc;
            if (existing.trunk <= 0) existing.trunk = incoming.trunk;
            if (existing.weight <= 0) existing.weight = incoming.weight;
            return;
        }
        vehicles.add(incoming);
    }

    private void normalizeVehicleList() {
        Iterator<Vehicle> it = vehicles.iterator();
        while (it.hasNext()) {
            Vehicle v = it.next();
            if (v.version == null || !v.version.contains("/")) continue;
            boolean allPresent = true;
            for (String part : v.version.split("/")) {
                boolean found = false; String wanted = part.trim();
                for (Vehicle other : vehicles) {
                    if (other != v && other.make.equalsIgnoreCase(v.make) && other.model.equalsIgnoreCase(v.model) && other.market.equalsIgnoreCase(v.market) && other.year == v.year && other.version.trim().equalsIgnoreCase(wanted)) { found = true; break; }
                }
                if (!found) { allPresent = false; break; }
            }
            if (allPresent) it.remove();
        }
        Collections.sort(vehicles, (a, b) -> { int c = a.make.compareToIgnoreCase(b.make); if (c != 0) return c; c = a.model.compareToIgnoreCase(b.model); if (c != 0) return c; c = Integer.compare(b.year, a.year); if (c != 0) return c; c = Double.compare(a.batteryKwh, b.batteryKwh); if (c != 0) return c; return a.version.compareToIgnoreCase(b.version); });
    }

    private String defaultMarket() { for (Vehicle v : vehicles) if (v.market.equalsIgnoreCase("ES")) return "ES"; return vehicles.get(0).market; }
    private boolean hasMarket(String m) { for (Vehicle v : vehicles) if (v.market.equalsIgnoreCase(m)) return true; return false; }
    private List<String> markets() { LinkedHashSet<String> s = new LinkedHashSet<>(); for (Vehicle v : vehicles) if (v.market != null && !v.market.trim().isEmpty()) s.add(v.market.toUpperCase(Locale.ROOT)); List<String> o = new ArrayList<>(s); Collections.sort(o, (a,b)->marketName(a).compareToIgnoreCase(marketName(b))); return o; }

    private String marketName(String c) {
        if (c == null || c.trim().isEmpty()) return ""; String code = c.equalsIgnoreCase("UK") ? "GB" : c.toUpperCase(Locale.ROOT);
        if (code.matches("[A-Z]{2}")) { Locale displayLocale = Locale.forLanguageTag(LanguageManager.getEffectiveLanguage(this)); String name = new Locale("", code).getDisplayCountry(displayLocale); if (name != null && !name.trim().isEmpty() && !name.equalsIgnoreCase(code)) return name; }
        return code;
    }
    private String marketFlag(String c) { if ("ES".equalsIgnoreCase(c)) return "🇪🇸"; if ("FR".equalsIgnoreCase(c)) return "🇫🇷"; if ("DE".equalsIgnoreCase(c)) return "🇩🇪"; if ("IT".equalsIgnoreCase(c)) return "🇮🇹"; if ("PT".equalsIgnoreCase(c)) return "🇵🇹"; if ("GB".equalsIgnoreCase(c) || "UK".equalsIgnoreCase(c)) return "🇬🇧"; if (c != null && c.matches("[A-Za-z]{2}")) { int a = Character.toUpperCase(c.charAt(0)) - 'A' + 127462; int b = Character.toUpperCase(c.charAt(1)) - 'A' + 127462; return new String(Character.toChars(a)) + new String(Character.toChars(b)); } return "🌐"; }
    private String marketLabel(String c) { return marketFlag(c) + "  " + marketName(c); }

    private void build() {
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); int statusBarHeight = getResources().getIdentifier("status_bar_height", "dimen", "android") > 0 ? getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android")) : 0; root.setPadding(0,statusBarHeight,0,0); root.setBackgroundColor(dark ? Color.rgb(7,19,28) : Color.rgb(241,246,251));
        FrameLayout hero = new FrameLayout(this); hero.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(260)));
        ImageView heroImage = new ImageView(this); heroImage.setImageResource(R.drawable.cabecera_ev_calculator); heroImage.setScaleType(ImageView.ScaleType.CENTER_CROP); heroImage.setAdjustViewBounds(false); heroImage.setTranslationY(-dp(10)); hero.addView(heroImage, new FrameLayout.LayoutParams(-1,-1));
        View topFade = new View(this); topFade.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{Color.argb(200,0,0,0),Color.argb(80,0,0,0),Color.argb(20,0,0,0),Color.argb(0,0,0,0)})); hero.addView(topFade, new FrameLayout.LayoutParams(-1,dp(170),Gravity.TOP));
        TextView back = tv("←",30,Color.WHITE); back.setGravity(Gravity.CENTER); back.setIncludeFontPadding(false); back.setTextAlignment(View.TEXT_ALIGNMENT_CENTER); back.setBackgroundColor(Color.TRANSPARENT); back.setPadding(0,0,0,0); back.setTranslationY(-dp(4)); back.setOnClickListener(v->finish()); FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(dp(40),dp(40),Gravity.TOP|Gravity.START); bp.leftMargin=dp(14); bp.topMargin=dp(12); hero.addView(back,bp);
        TextView title = tv("Comparar coches",22,Color.WHITE); title.setTypeface(null,Typeface.BOLD); title.setGravity(Gravity.CENTER); title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER); title.setShadowLayer(dp(4),0,dp(2),Color.argb(90,0,0,0)); FrameLayout.LayoutParams tp=new FrameLayout.LayoutParams(-1,dp(56)); tp.gravity=Gravity.TOP|Gravity.CENTER_HORIZONTAL; tp.leftMargin=dp(40); tp.rightMargin=dp(40); tp.topMargin=dp(4); hero.addView(title,tp);
        TextView menuButton = tv("⋮",30,Color.WHITE); menuButton.setGravity(Gravity.CENTER); menuButton.setIncludeFontPadding(false); menuButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER); menuButton.setPadding(0,0,0,0); menuButton.setShadowLayer(dp(4),0,dp(2),Color.argb(90,0,0,0)); menuButton.setBackgroundColor(Color.TRANSPARENT); menuButton.setContentDescription(LanguageManager.t(this,"Menú")); menuButton.setOnClickListener(v->AppMenuHelper.show(this,menuButton,new AppMenuHelper.Listener(){ public boolean isDark(){return dark;} public void setDark(boolean value){if(dark!=value){dark=value;getSharedPreferences(PREFS,MODE_PRIVATE).edit().putBoolean("dark_theme",dark).apply();loadSelection();build();rebuild();}} })); FrameLayout.LayoutParams mbp=new FrameLayout.LayoutParams(dp(40),dp(40),Gravity.TOP|Gravity.END); mbp.rightMargin=dp(14); mbp.topMargin=dp(12); hero.addView(menuButton,mbp);
        root.addView(hero);
        ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true); scroll.setClipToPadding(false); LinearLayout content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(dp(14),dp(14),dp(14),dp(12));
        LinearLayout intro=new LinearLayout(this); intro.setOrientation(LinearLayout.VERTICAL); intro.setPadding(dp(16),dp(14),dp(16),dp(14)); intro.setBackground(strokeBg(dark?Color.rgb(17,31,44):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(218,228,239),18)); TextView introTitle=tv("Elige tus vehículos",17,text()); introTitle.setTypeface(null,Typeface.BOLD); intro.addView(introTitle,new LinearLayout.LayoutParams(-1,dp(26))); TextView hint=tv("Añade hasta 3 coches para ver sus características y compararlos.",13,sub()); hint.setPadding(0,dp(2),0,0); intro.addView(hint,new LinearLayout.LayoutParams(-1,dp(36))); LinearLayout.LayoutParams introLp = new LinearLayout.LayoutParams(-1, -2); introLp.topMargin = -dp(26); intro.setLayoutParams(introLp); content.addView(intro);
        HorizontalScrollView carsScroll=new HorizontalScrollView(this); carsScroll.setHorizontalScrollBarEnabled(false); carsScroll.setClipToPadding(false); carsScroll.setPadding(0,dp(12),0,dp(4)); carsRow=new LinearLayout(this); carsRow.setOrientation(LinearLayout.HORIZONTAL); carsRow.setGravity(Gravity.TOP); carsScroll.addView(carsRow,new HorizontalScrollView.LayoutParams(-2,-2)); content.addView(carsScroll,new LinearLayout.LayoutParams(-1,-2));
        TextView section=tv("Características",19,text()); section.setTypeface(null,Typeface.BOLD); section.setPadding(dp(2),dp(12),0,dp(2)); content.addView(section,new LinearLayout.LayoutParams(-1,dp(42))); TextView legend=tv("✦  Mejor valor",12,blue); legend.setGravity(Gravity.CENTER_VERTICAL); legend.setPadding(dp(4),0,0,dp(4)); if(selectedIds.size()>=2)content.addView(legend,new LinearLayout.LayoutParams(-1,dp(28)));
        table=new LinearLayout(this); table.setOrientation(LinearLayout.VERTICAL); table.setPadding(0,dp(2),0,0); HorizontalScrollView tableScroll=new HorizontalScrollView(this); tableScroll.setHorizontalScrollBarEnabled(false); tableScroll.addView(table,new HorizontalScrollView.LayoutParams(-2,-2)); content.addView(tableScroll,new LinearLayout.LayoutParams(-1,-2)); summary=new LinearLayout(this); summary.setOrientation(LinearLayout.VERTICAL); summary.setPadding(0,dp(18),0,dp(8)); content.addView(summary,new LinearLayout.LayoutParams(-1,-2));
        LinearLayout footer=new LinearLayout(this); footer.setOrientation(LinearLayout.VERTICAL); footer.setGravity(Gravity.CENTER); footer.setPadding(dp(14),0,dp(14),dp(4)); String appVersion="1.0.4"; try{appVersion=getPackageManager().getPackageInfo(getPackageName(),0).versionName;}catch(Exception ignored){} if(appVersion.startsWith("v")||appVersion.startsWith("V"))appVersion=appVersion.substring(1); TextView privacyLink=tv("Política de privacidad",13,dark?Color.rgb(105,175,255):blue); privacyLink.setGravity(Gravity.CENTER); privacyLink.setTypeface(null,Typeface.BOLD); privacyLink.setClickable(true); privacyLink.setFocusable(true); privacyLink.setContentDescription("Política de privacidad"); privacyLink.setOnClickListener(v->startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://mariskal19.github.io/EV-Calculator-PRO-Privacy/")))); footer.addView(privacyLink,new LinearLayout.LayoutParams(-1,dp(30))); TextView foot=tv("Powered by EV Calculator · v"+appVersion,12,sub()); foot.setGravity(Gravity.CENTER); footer.addView(foot,new LinearLayout.LayoutParams(-1,dp(24))); Space footerSpacer=new Space(this); content.addView(footerSpacer,new LinearLayout.LayoutParams(-1,0,1)); content.addView(footer,new LinearLayout.LayoutParams(-1,dp(62))); scroll.addView(content,new ScrollView.LayoutParams(-1,-1)); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1)); setContentView(root); getWindow().setStatusBarColor(dark?Color.rgb(7,19,28):Color.rgb(241,246,251)); getWindow().setNavigationBarColor(dark?Color.rgb(7,19,28):Color.rgb(241,246,251)); getWindow().getDecorView().setSystemUiVisibility(dark?0:View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private List<Vehicle> marketVehicles(){List<Vehicle> o=new ArrayList<>();for(Vehicle v:vehicles)if(v.market.equalsIgnoreCase(selectedMarket))o.add(v);return o;}
    private int tableWidth(){return dp(112+145*Math.max(1,selectedIds.size()));}

    private Vehicle find(String id){for(Vehicle v:vehicles)if(v.id.equals(id))return v;return null;}
    private Vehicle findByLogicalKey(String key){for(Vehicle v:vehicles)if(logicalKey(v).equals(key))return v;return null;}
    private void loadSelection(){
        selectedIds.clear();
        SharedPreferences p=getSharedPreferences(PREFS,MODE_PRIVATE);
        String logical=p.getString(KEY_SELECTED_LOGICAL,"");
        if(!logical.trim().isEmpty()) for(String key:logical.split("\\Q||\\E")){key=key.trim();Vehicle v=findByLogicalKey(key);if(v!=null&&!selectedIds.contains(v.id)&&selectedIds.size()<3)selectedIds.add(v.id);}
        if(selectedIds.isEmpty()){
            String ordered=p.getString(KEY_SELECTED_ORDERED,"");
            if(!ordered.trim().isEmpty())for(String id:ordered.split(",")){id=id.trim();Vehicle v=find(id);if(!id.isEmpty()&&v!=null&&!selectedIds.contains(id)&&selectedIds.size()<3)selectedIds.add(id);}
        }
        if(selectedIds.isEmpty()){
            Set<String>s=p.getStringSet(KEY_SELECTED,null);
            if(s!=null)for(String id:s){Vehicle v=find(id);if(v!=null&&!selectedIds.contains(id)&&selectedIds.size()<3)selectedIds.add(id);}
        }
        if(!selectedIds.isEmpty())saveSelection();
    }
    private void saveSelection(){
        SharedPreferences.Editor e=getSharedPreferences(PREFS,MODE_PRIVATE).edit();
        e.putString(KEY_SELECTED_ORDERED,joinSelection());
        e.putString(KEY_SELECTED_LOGICAL,joinLogicalSelection());
        e.putStringSet(KEY_SELECTED,new LinkedHashSet<>(selectedIds));
        e.apply();
    }
    private String joinSelection(){StringBuilder s=new StringBuilder();for(String id:selectedIds){if(s.length()>0)s.append(',');s.append(id);}return s.toString();}
    private String joinLogicalSelection(){StringBuilder s=new StringBuilder();for(String id:selectedIds){Vehicle v=find(id);if(v==null)continue;if(s.length()>0)s.append("||");s.append(logicalKey(v));}return s.toString();}

    private void rebuild(){if(carsRow==null||table==null||summary==null)return;carsRow.removeAllViews();table.removeAllViews();summary.removeAllViews();for(String id:selectedIds){Vehicle v=find(id);if(v!=null){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(145),-2);lp.setMargins(dp(3),0,dp(3),0);carsRow.addView(carCard(v),lp);}}if(selectedIds.size()<3){LinearLayout empty=new LinearLayout(this);empty.setOrientation(LinearLayout.VERTICAL);empty.setGravity(Gravity.CENTER);empty.setPadding(dp(8),dp(10),dp(8),dp(10));empty.setBackground(strokeBg(dark?Color.rgb(14,26,38):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(220,229,240),16));TextView plus=tv("＋",28,blue);plus.setGravity(Gravity.CENTER);empty.addView(plus,new LinearLayout.LayoutParams(-1,dp(34)));TextView n=tv("Añadir coche",12,sub());n.setGravity(Gravity.CENTER);empty.addView(n,new LinearLayout.LayoutParams(-1,dp(24)));empty.setOnClickListener(v->showSearch());LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(145),dp(150));lp.setMargins(dp(3),0,dp(3),0);carsRow.addView(empty,lp);}if(selectedIds.size()>=1){addSection("Batería y autonomía");addRow("Batería","battery",false);addRow("Tipo batería","type",false);addRow("Autonomía WLTP","range",true);addRow("Consumo","cons",true);addSection("Prestaciones");addRow("Potencia","power",true);addRow("Tracción","drive",false);addRow("0–100 km/h","acc",false);addSection("Carga");addRow("Carga AC","ac",true);addRow("Carga DC","dc",true);addRow("10–80 %","charge",false);addSection("Practicidad");addRow("Maletero","trunk",true);addRow("Peso","weight",false);addSection("Precio");addRow("Precio","price",false);if(selectedIds.size()>=2)buildSummary();}else{TextView t=tv("Selecciona un coche para mostrar sus características.",14,sub());t.setGravity(Gravity.CENTER);t.setPadding(dp(10),dp(18),dp(10),dp(18));table.addView(t,new LinearLayout.LayoutParams(tableWidth(),-2));}}

    private void addSection(String title){TextView s=tv(title,14,blue);s.setTypeface(null,Typeface.BOLD);s.setGravity(Gravity.CENTER_VERTICAL);s.setPadding(dp(4),dp(12),dp(4),dp(6));table.addView(s,new LinearLayout.LayoutParams(tableWidth(),dp(40)));}
    private TextView chip(String label){TextView t=tv(label,10.5f,sub());t.setGravity(Gravity.CENTER);t.setIncludeFontPadding(false);t.setPadding(dp(6),dp(4),dp(6),dp(4));t.setBackground(strokeBg(dark?Color.rgb(13,28,41):Color.rgb(244,248,253),dark?Color.rgb(43,65,84):Color.rgb(222,231,240),10));return t;}
    private View carCard(Vehicle v){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setGravity(Gravity.CENTER_HORIZONTAL);c.setPadding(dp(10),dp(11),dp(10),dp(9));c.setBackground(strokeBg(dark?Color.rgb(18,32,45):Color.WHITE,dark?Color.rgb(49,72,91):Color.rgb(214,225,237),18));TextView make=tv(v.make.toUpperCase(Locale.ROOT),11,blue);make.setTypeface(null,Typeface.BOLD);make.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);make.setIncludeFontPadding(false);c.addView(make,new LinearLayout.LayoutParams(-1,dp(20)));TextView model=tv(v.model,17,text());model.setTypeface(null,Typeface.BOLD);model.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);model.setIncludeFontPadding(false);model.setMaxLines(2);c.addView(model,new LinearLayout.LayoutParams(-1,dp(43)));TextView ver=tv(v.version==null||v.version.trim().isEmpty()?"—":v.version.trim(),11,sub());ver.setGravity(Gravity.START|Gravity.TOP);ver.setIncludeFontPadding(false);ver.setMaxLines(3);ver.setEllipsize(android.text.TextUtils.TruncateAt.END);ver.setPadding(dp(2),dp(7),dp(2),0);c.addView(ver,new LinearLayout.LayoutParams(-1,dp(58)));TextView rem=tv("Quitar",11,Color.rgb(210,70,70));rem.setGravity(Gravity.CENTER);rem.setTypeface(null,Typeface.BOLD);rem.setIncludeFontPadding(false);rem.setPadding(0,dp(5),0,0);rem.setOnClickListener(x->remove(v.id));c.addView(rem,new LinearLayout.LayoutParams(-1,dp(27)));return c;}
    private void remove(String id){selectedIds.remove(id);saveSelection();rebuild();}

    private void showSearch(){
        final EditText input=new EditText(this);
        input.setSingleLine(true);
        input.setHint(LanguageManager.t(this,"Marca, modelo, año, batería o versión"));
        input.setTextColor(text()); input.setHintTextColor(sub()); input.setTextSize(15);
        input.setPadding(dp(14),0,dp(14),0);
        input.setBackground(strokeBg(dark?Color.rgb(20,35,49):Color.rgb(247,250,254),dark?Color.rgb(59,84,106):Color.rgb(211,223,236),16));
        final ListView list=new ListView(this);
        list.setDivider(null); list.setVerticalScrollBarEnabled(true); list.setPadding(0,dp(2),0,0); list.setClipToPadding(false);
        final TextView header=tv("Todos los vehículos",13,blue);
        header.setTypeface(null,Typeface.BOLD); header.setPadding(dp(18),dp(16),dp(18),dp(7)); header.setBackgroundColor(Color.TRANSPARENT);
        list.addHeaderView(header,null,false);
        final List<Vehicle> results=new ArrayList<>();
        final BaseAdapter adapter=new BaseAdapter(){
            @Override public int getCount(){return results.size();}
            @Override public Object getItem(int position){return results.get(position);}
            @Override public long getItemId(int position){return position;}
            @Override public View getView(int position,View convertView,android.view.ViewGroup parent){
                TextView item=convertView instanceof TextView?(TextView)convertView:new TextView(CompararCochesActivity.this);
                item.setGravity(Gravity.CENTER_VERTICAL|Gravity.START); item.setPadding(dp(16),dp(6),dp(42),dp(6)); item.setLineSpacing(0,1.05f);
                Vehicle v=results.get(position);
                SpannableString styled=new SpannableString(searchLabel(v)); int nl=styled.toString().indexOf('\n');
                if(nl>0){styled.setSpan(new StyleSpan(Typeface.BOLD),0,nl,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);if(nl+1<styled.length())styled.setSpan(new RelativeSizeSpan(0.86f),nl+1,styled.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);}
                item.setText(styled); item.setTextSize(14); item.setTextColor(text());
                item.setBackground(strokeBg(dark?Color.rgb(18,31,44):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(225,233,242),14));
                item.setOnClickListener(x->{Object tag=input.getTag();if(tag instanceof AlertDialog)((AlertDialog)tag).dismiss();selectedIds.add(v.id);SharedPreferences prefs=getSharedPreferences(PREFS,MODE_PRIVATE);prefs.edit().putInt(KEY_SEARCH_COUNT_PREFIX+v.id,prefs.getInt(KEY_SEARCH_COUNT_PREFIX+v.id,0)+1).apply();saveSelection();rebuild();});
                return item;
            }
        };
        list.setAdapter(adapter);
        LinearLayout marketRow=new LinearLayout(this); marketRow.setOrientation(LinearLayout.HORIZONTAL); marketRow.setGravity(Gravity.CENTER_VERTICAL); marketRow.setPadding(dp(18),dp(12),dp(18),dp(6));
        TextView marketTitle=tv("Mercado",12,sub()); marketTitle.setTypeface(null,Typeface.BOLD); marketTitle.setGravity(Gravity.CENTER_VERTICAL|Gravity.START); marketRow.addView(marketTitle,new LinearLayout.LayoutParams(0,dp(38),1));
        final Spinner searchMarketSpinner=new Spinner(this); List<String> ms=markets(); List<String> labels=new ArrayList<>(); for(String m:ms)labels.add(marketLabel(m));
        ArrayAdapter<String> marketAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,labels){
            @Override public View getView(int p,View c,android.view.ViewGroup parent){TextView v=(TextView)super.getView(p,c,parent);v.setTextColor(text());v.setTextSize(14);v.setGravity(Gravity.CENTER_VERTICAL|Gravity.END);return v;}
            @Override public View getDropDownView(int p,View c,android.view.ViewGroup parent){TextView v=(TextView)super.getDropDownView(p,c,parent);v.setTextColor(text());v.setTextSize(15);v.setGravity(Gravity.CENTER_VERTICAL|Gravity.START);v.setPadding(dp(14),dp(10),dp(14),dp(10));v.setBackgroundColor(dark?Color.rgb(18,30,42):Color.WHITE);return v;}
        };
        searchMarketSpinner.setAdapter(marketAdapter); searchMarketSpinner.setBackground(strokeBg(dark?Color.rgb(20,35,49):Color.WHITE,dark?Color.rgb(59,84,106):Color.rgb(211,223,236),14)); searchMarketSpinner.setPadding(dp(10),0,dp(8),0);
        int marketIndex=0; for(int i=0;i<ms.size();i++)if(ms.get(i).equalsIgnoreCase(selectedMarket)){marketIndex=i;break;} searchMarketSpinner.setSelection(marketIndex);
        Runnable refreshResults=()->{String q=input.getText()==null?"":input.getText().toString().trim().toLowerCase(Locale.ROOT);List<Vehicle> filtered=orderedSearchVehicles(q);results.clear();results.addAll(filtered);header.setVisibility(q.isEmpty()?View.VISIBLE:View.GONE);adapter.notifyDataSetChanged();};
        searchMarketSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onItemSelected(android.widget.AdapterView<?>p,View v,int pos,long id){if(pos>=0&&pos<ms.size()){selectedMarket=ms.get(pos);getSharedPreferences(PREFS,MODE_PRIVATE).edit().putString(KEY_MARKET,selectedMarket).apply();input.post(refreshResults);}}public void onNothingSelected(android.widget.AdapterView<?>p){}});
        marketRow.addView(searchMarketSpinner,new LinearLayout.LayoutParams(dp(180),dp(38)));
        LinearLayout body=new LinearLayout(this); body.setOrientation(LinearLayout.VERTICAL); body.setPadding(dp(12),dp(12),dp(12),dp(8)); body.addView(input,new LinearLayout.LayoutParams(-1,dp(50))); body.addView(marketRow,new LinearLayout.LayoutParams(-1,dp(56))); body.addView(list,new LinearLayout.LayoutParams(-1,0,1));
        AlertDialog d=new AlertDialog.Builder(this).setView(body).create(); input.setTag(d);
        input.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int b,int c){input.post(refreshResults);}public void afterTextChanged(Editable e){}});
        d.setOnShowListener(x->{d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE|WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);d.getWindow().setBackgroundDrawable(bg(dark?Color.rgb(10,21,31):Color.WHITE,20));input.requestFocus();input.post(()->{InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);if(imm!=null)imm.showSoftInput(input,InputMethodManager.SHOW_IMPLICIT);input.post(refreshResults);});});
        d.show(); d.getWindow().setBackgroundDrawable(bg(dark?Color.rgb(10,21,31):Color.WHITE,20)); input.requestFocus();
    }

    private int trimRank(Vehicle v){String k=v.version.toLowerCase(Locale.ROOT);if(k.contains("standard")||k.contains("base")||k.contains("comfort"))return 0;if(k.contains("long range")||k.contains("extended"))return 1;if(k.contains("premium")||k.contains("performance")||k.contains("max"))return 2;return 3;}
    private String searchLabel(Vehicle v){String first=v.make+" "+v.model;StringBuilder second=new StringBuilder();if(v.year>0)second.append(v.year);String ver=v.version.trim().replaceAll("(?i)(?<![0-9])\\d+(?:[.,]\\d+)?\\s*kwh\\b","").replaceAll("(?i)(?<![0-9])\\d+(?:[.,]\\d+)?\\s*kw\\b","").replaceAll("\\s+"," ").trim();if(!ver.isEmpty()){if(second.length()>0)second.append(" · ");second.append(ver);}if(v.batteryKwh>0){if(second.length()>0)second.append(" · ");second.append(fmt(v.batteryKwh)).append(" kWh");}return first+"\n"+second;}
    private List<Vehicle> orderedSearchVehicles(String q){List<Vehicle>all=new ArrayList<>();for(Vehicle v:marketVehicles()){if(selectedIds.contains(v.id))continue;String hay=(v.make+" "+v.model+" "+v.year+" "+v.batteryKwh+" "+v.batteryType+" "+v.drivetrain+" "+v.version).toLowerCase(Locale.ROOT);if(!q.isEmpty()&&!hay.contains(q))continue;all.add(v);}Collections.sort(all,(a,b)->{int c=a.market.compareToIgnoreCase(b.market);if(c!=0)return c;c=a.make.compareToIgnoreCase(b.make);if(c!=0)return c;c=a.model.compareToIgnoreCase(b.model);if(c!=0)return c;c=Integer.compare(a.year,b.year);if(c!=0)return c;c=Integer.compare(trimRank(a),trimRank(b));if(c!=0)return c;c=Double.compare(a.batteryKwh,b.batteryKwh);if(c!=0)return c;c=Double.compare(a.powerKw,b.powerKw);if(c!=0)return c;c=Double.compare(a.wltpKm,b.wltpKm);if(c!=0)return c;c=Double.compare(a.price>0?a.price:Double.MAX_VALUE,b.price>0?b.price:Double.MAX_VALUE);if(c!=0)return c;return a.version.compareToIgnoreCase(b.version);});return all;}
    private void renderSearchResults(EditText input,LinearLayout list){list.removeAllViews();String q=input.getText()==null?"":input.getText().toString().trim().toLowerCase(Locale.ROOT);List<Vehicle>all=orderedSearchVehicles(q);if(q.isEmpty()){TextView header=tv("Todos los vehículos",13,blue);header.setTypeface(null,Typeface.BOLD);header.setPadding(dp(18),dp(16),dp(18),dp(7));list.addView(header,new LinearLayout.LayoutParams(-1,dp(38)));}for(Vehicle v:all)addSearchItem(v,list,input);if(!q.isEmpty()&&all.isEmpty()){TextView none=tv("No se encontraron vehículos",14,sub());none.setGravity(Gravity.CENTER);none.setPadding(dp(12),dp(20),dp(12),dp(20));list.addView(none,new LinearLayout.LayoutParams(-1,dp(60)));}}
    private void addSearchItem(Vehicle v,LinearLayout list,EditText input){TextView item=tv(searchLabel(v),14,text());item.setGravity(Gravity.CENTER_VERTICAL|Gravity.START);item.setPadding(dp(16),dp(6),dp(42),dp(6));item.setLineSpacing(0,1.05f);SpannableString styled=new SpannableString(item.getText());int nl=styled.toString().indexOf('\n');if(nl>0){styled.setSpan(new StyleSpan(Typeface.BOLD),0,nl,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);if(nl+1<styled.length())styled.setSpan(new RelativeSizeSpan(0.86f),nl+1,styled.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);}item.setText(styled);item.setBackground(strokeBg(dark?Color.rgb(18,31,44):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(225,233,242),14));item.setOnClickListener(x->{Object tag=input.getTag();if(tag instanceof AlertDialog)((AlertDialog)tag).dismiss();selectedIds.add(v.id);SharedPreferences prefs=getSharedPreferences(PREFS,MODE_PRIVATE);prefs.edit().putInt(KEY_SEARCH_COUNT_PREFIX+v.id,prefs.getInt(KEY_SEARCH_COUNT_PREFIX+v.id,0)+1).apply();saveSelection();rebuild();});LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(60));lp.setMargins(dp(14),dp(3),dp(14),dp(3));list.addView(item,lp);}
    private void addRow(String label,String key,boolean numeric){LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.HORIZONTAL);r.setGravity(Gravity.CENTER_VERTICAL);r.setBackgroundColor(table.getChildCount()%2==0?(dark?Color.rgb(12,24,35):Color.WHITE):rowAlt());TextView l=tv(label,13,sub());l.setGravity(Gravity.CENTER_VERTICAL|Gravity.START);l.setIncludeFontPadding(false);l.setPadding(dp(6),0,dp(6),0);r.addView(l,new LinearLayout.LayoutParams(dp(112),dp(52)));List<Vehicle>chosen=new ArrayList<>();for(String id:selectedIds){Vehicle v=find(id);if(v!=null)chosen.add(v);}double best=Double.NaN;if(numeric&&chosen.size()>=2){boolean higher=!key.equals("cons")&&!key.equals("charge")&&!key.equals("price")&&!key.equals("weight");for(Vehicle v:chosen){double n=numeric(v,key);if(Double.isNaN(n))continue;if(Double.isNaN(best)||(higher?n>best:n<best))best=n;}}for(Vehicle v:chosen){String value=value(v,key);TextView cell=tv(value,13,text());cell.setGravity(Gravity.CENTER);cell.setIncludeFontPadding(false);cell.setBackgroundColor(rowAlt());double n=numeric(v,key);if(selectedIds.size()>=2&&!Double.isNaN(best)&&!Double.isNaN(n)&&Math.abs(n-best)<0.0001)cell.setTextColor(blue);r.addView(cell,new LinearLayout.LayoutParams(dp(145),dp(52)));}table.addView(r,new LinearLayout.LayoutParams(tableWidth(),dp(52)));}
    private String value(Vehicle v,String key){if("battery".equals(key))return v.batteryKwh>0?fmt(v.batteryKwh)+" kWh":"—";if("type".equals(key))return empty(v.batteryType);if("range".equals(key))return v.wltpKm>0?String.format(Locale.US,"%.0f km",v.wltpKm):"—";if("cons".equals(key))return v.consumption>0?fmt(v.consumption)+" kWh/100 km":"—";if("power".equals(key))return v.powerKw>0?Math.round(v.powerKw*1.35962)+" CV ("+String.format(Locale.US,"%.0f kW",v.powerKw)+")":"—";if("drive".equals(key))return empty(v.drivetrain);if("acc".equals(key))return v.acc>0?fmt(v.acc)+" s":"—";if("ac".equals(key))return v.acKw>0?fmt(v.acKw)+" kW":"—";if("dc".equals(key))return v.dcKw>0?fmt(v.dcKw)+" kW":"—";if("charge".equals(key))return v.chargeMin>0?String.format(Locale.US,"%.0f min",v.chargeMin):"—";if("trunk".equals(key))return v.trunk>0?String.format(Locale.US,"%.0f L",v.trunk):"—";if("weight".equals(key))return v.weight>0?String.format(Locale.US,"%.0f kg",v.weight):"—";if("price".equals(key))return formatPrice(v.price);return "—";}
    private double numeric(Vehicle v,String key){if("battery".equals(key))return v.batteryKwh;if("range".equals(key))return v.wltpKm;if("cons".equals(key))return v.consumption;if("power".equals(key))return v.powerKw;if("acc".equals(key))return v.acc;if("ac".equals(key))return v.acKw;if("dc".equals(key))return v.dcKw;if("charge".equals(key))return v.chargeMin;if("trunk".equals(key))return v.trunk;if("weight".equals(key))return v.weight;if("price".equals(key))return v.price;return Double.NaN;}
    private void buildSummary(){LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(dp(16),dp(14),dp(16),dp(14));card.setBackground(strokeBg(dark?Color.rgb(17,31,44):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(218,228,239),18));TextView h=tv("Resumen de la comparativa",18,text());h.setTypeface(null,Typeface.BOLD);card.addView(h,new LinearLayout.LayoutParams(-1,dp(30)));TextView intro=tv("Resultado rápido de los vehículos seleccionados",13,sub());card.addView(intro,new LinearLayout.LayoutParams(-1,dp(28)));addSummaryWinner(card,"Autonomía","range",true);addSummaryWinner(card,"Consumo","cons",false);addSummaryWinner(card,"Potencia","power",true);addSummaryWinner(card,"Carga DC","dc",true);addSummaryWinner(card,"Precio","price",false);TextView selected=tv("Vehículos comparados",14,blue);selected.setTypeface(null,Typeface.BOLD);selected.setIncludeFontPadding(true);selected.setPadding(0,dp(4),0,dp(4));card.addView(selected,new LinearLayout.LayoutParams(-1,dp(36)));for(String id:selectedIds){Vehicle v=find(id);if(v==null)continue;TextView s=tv("•  "+v.make+" "+v.model+" · "+v.version+"  ·  "+marketLabel(v.market),13,text());s.setPadding(0,dp(3),0,dp(3));card.addView(s,new LinearLayout.LayoutParams(-1,-2));}summary.addView(card,new LinearLayout.LayoutParams(-1,-2));LinearLayout commentCard=new LinearLayout(this);commentCard.setOrientation(LinearLayout.VERTICAL);commentCard.setPadding(dp(16),dp(14),dp(16),dp(14));commentCard.setBackground(strokeBg(dark?Color.rgb(17,31,44):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(218,228,239),18));TextView commentTitle=tv("Comentario",16,blue);commentTitle.setTypeface(null,Typeface.BOLD);commentTitle.setPadding(0,0,0,dp(6));commentCard.addView(commentTitle,new LinearLayout.LayoutParams(-1,dp(30)));TextView comment=tv(summaryComment(),13,sub());comment.setLineSpacing(0,1.2f);commentCard.addView(comment,new LinearLayout.LayoutParams(-1,-2));LinearLayout.LayoutParams commentLp=new LinearLayout.LayoutParams(-1,-2);commentLp.setMargins(0,dp(10),0,0);summary.addView(commentCard,commentLp);}
    private void addSummaryWinner(LinearLayout parent,String label,String key,boolean higherBetter){List<Vehicle>chosen=new ArrayList<>();for(String id:selectedIds){Vehicle v=find(id);if(v!=null)chosen.add(v);}Vehicle bestV=null;double best=Double.NaN;for(Vehicle v:chosen){double n=numeric(v,key);if(Double.isNaN(n))continue;if(Double.isNaN(best)||(higherBetter?n>best:n<best)){best=n;bestV=v;}}if(bestV==null)return;String val=value(bestV,key);TextView row=tv(label+"  ·  "+bestV.make+" "+bestV.model+"  →  "+val,13,text());row.setPadding(0,dp(4),0,dp(4));parent.addView(row,new LinearLayout.LayoutParams(-1,dp(30)));}
    private String summaryComment(){List<Vehicle>chosen=new ArrayList<>();for(String id:selectedIds){Vehicle v=find(id);if(v!=null)chosen.add(v);}if(chosen.size()<2)return"";Vehicle bestRange=bestVehicle("range",true),bestCons=bestVehicle("cons",false),bestPrice=bestVehicle("price",false);StringBuilder out=new StringBuilder();out.append("En conjunto, ");if(bestRange!=null)out.append(bestRange.make).append(" ").append(bestRange.model).append(" destaca por autonomía");if(bestCons!=null&&bestCons!=bestRange)out.append(", mientras que ").append(bestCons.make).append(" ").append(bestCons.model).append(" ofrece el menor consumo");if(bestPrice!=null&&bestPrice!=bestRange&&bestPrice!=bestCons)out.append(" y ").append(bestPrice.make).append(" ").append(bestPrice.model).append(" es la opción más económica");out.append(". La elección final dependerá de si priorizas autonomía, eficiencia, prestaciones, velocidad de carga o precio.");return out.toString();}
    private Vehicle bestVehicle(String key,boolean higherBetter){Vehicle bestV=null;double best=Double.NaN;for(String id:selectedIds){Vehicle v=find(id);if(v==null)continue;double n=numeric(v,key);if(Double.isNaN(n))continue;if(Double.isNaN(best)||(higherBetter?n>best:n<best)){best=n;bestV=v;}}return bestV;}
    private String empty(String s){return s==null||s.trim().isEmpty()?"—":s;}
    private String fmt(double n){return String.format(Locale.US,"%.1f",n).replace('.',',');}
    private String formatPrice(double p){if(p<=0)return"—";String currency=getSharedPreferences(PREFS,MODE_PRIVATE).getString(KEY_CURRENCY,"EUR");return String.format(Locale.US,"%,.0f %s",p,currency).replace(',','.');}

    static class Vehicle {
        String id,make,model,version,batteryType,drivetrain,market; int year;
        double price,batteryKwh,usableBatteryKwh,wltpKm,consumption,powerKw,acKw,dcKw,chargeMin,acc,trunk,weight;
        Vehicle(JSONObject o){make=o.optString("make",o.optString("brand",""));model=o.optString("model","");version=o.optString("version",o.optString("trim",""));batteryType=o.optString("batteryType","");drivetrain=o.optString("drivetrain","");market=o.optString("market",o.optString("mercado","ES")).toUpperCase(Locale.ROOT);year=o.optInt("year",o.optInt("modelYear",0));price=o.optDouble("price",0);batteryKwh=o.optDouble("batteryKwh",o.optDouble("battery_capacity_kwh",0));usableBatteryKwh=o.optDouble("usableBatteryKwh",0);wltpKm=o.optDouble("wltpKm",o.optDouble("rangeKm",0));consumption=o.optDouble("consumption",0);if(consumption<=0)consumption=o.optDouble("consumptionKwh100",0);powerKw=o.optDouble("powerKw",0);acKw=o.optDouble("acKw",0);dcKw=o.optDouble("dcKw",0);chargeMin=o.optDouble("chargeMin",0);acc=o.optDouble("acc",o.optDouble("acceleration",0));trunk=o.optDouble("trunk",o.optDouble("trunkLiters",0));weight=o.optDouble("weight",0);id=o.optString("id","").trim();if(id.isEmpty()){String key=(make+"|"+model+"|"+market+"|"+year+"|"+String.format(Locale.US,"%.1f",batteryKwh)+"|"+version).trim().toLowerCase(Locale.ROOT);id="catalog-"+Integer.toHexString(key.hashCode());}}
    }
}