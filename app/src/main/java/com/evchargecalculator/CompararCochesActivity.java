package com.evchargecalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class CompararCochesActivity extends Activity {
    private static final String PREFS="ev_charge_calculator", KEY_SELECTED="compare_vehicle_ids", KEY_SELECTED_ORDERED="compare_vehicle_ids_ordered", KEY_CURRENCY="app_currency";
    private final int blue=Color.rgb(46,107,255), white=Color.rgb(22,42,63), secondary=Color.rgb(90,111,137);
    private boolean dark; private LinearLayout carsRow,table,summary;
    private final List<Vehicle> vehicles=new ArrayList<>(); private final List<String> selectedIds=new ArrayList<>();

    @Override protected void onCreate(Bundle b){super.onCreate(b);try{
        SharedPreferences p=getSharedPreferences(PREFS,MODE_PRIVATE);
        dark=p.contains("dark_theme")?p.getBoolean("dark_theme",false):(getResources().getConfiguration().uiMode&0x30)==0x20;
        CurrencyRateManager.refreshIfNeeded(this);
        build(); try{loadVehicles();loadSelection();rebuild();}catch(Throwable t){showDataError(t);}
    }catch(Throwable t){TextView e=tv("Error al abrir Comparar coches\n\n"+t.getClass().getSimpleName(),16,Color.WHITE);e.setGravity(Gravity.CENTER);e.setPadding(dp(24),dp(24),dp(24),dp(24));e.setBackgroundColor(Color.rgb(8,34,58));setContentView(e);}}

    @Override protected void onResume(){super.onResume();CurrencyRateManager.refreshIfNeeded(this);if(table!=null)rebuild();}
    private int dp(int n){return(int)(n*getResources().getDisplayMetrics().density+.5f);}
    private int text(){return dark?Color.rgb(245,248,255):white;}
    private int sub(){return dark?Color.rgb(170,183,204):secondary;}
    private int card(){return dark?Color.rgb(18,29,41):Color.WHITE;}
    private int rowAlt(){return dark?Color.rgb(14,25,36):Color.rgb(248,251,255);}
    private int bestBg(){return dark?Color.rgb(13,36,58):Color.rgb(235,243,255);}
    private GradientDrawable bg(int c,float r){GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(dp((int)r));return g;}
    private GradientDrawable strokeBg(int fill,int stroke,float r){GradientDrawable g=bg(fill,r);g.setStroke(dp(1),stroke);return g;}
    private TextView tv(String s,float size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);return t;}

    private void loadVehicles(){
        vehicles.clear();
        loadAssetVehicles("vehicles.json", true);
        loadAssetVehicles("vehicle_variants.json", false);
        if(vehicles.isEmpty())throw new IllegalStateException("vehicles array missing");
    }
    private void loadAssetVehicles(String asset, boolean required){
        try(InputStream in=getAssets().open(asset);BufferedReader r=new BufferedReader(new InputStreamReader(in))){
            StringBuilder sb=new StringBuilder();String line;while((line=r.readLine())!=null)sb.append(line);
            JSONArray a=new JSONObject(sb.toString()).optJSONArray("vehicles");
            if(a==null)throw new IllegalStateException(asset+": vehicles array missing");
            for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o!=null)vehicles.add(new Vehicle(o));}
        }catch(Exception e){if(required)throw new IllegalStateException("No se ha podido cargar "+asset,e);}
    }
    private void loadSelection(){selectedIds.clear();SharedPreferences p=getSharedPreferences(PREFS,MODE_PRIVATE);
        String ordered=p.getString(KEY_SELECTED_ORDERED,"");
        if(!ordered.trim().isEmpty()){
            for(String id:ordered.split(",")){id=id.trim();if(!id.isEmpty()&&find(id)!=null&&!selectedIds.contains(id)&&selectedIds.size()<3)selectedIds.add(id);}
        }
        if(selectedIds.isEmpty()){
            Set<String> s=p.getStringSet(KEY_SELECTED,null);if(s!=null)for(String id:s)if(id!=null&&find(id)!=null&&!selectedIds.contains(id)&&selectedIds.size()<3)selectedIds.add(id);
        }
    }
    private Vehicle find(String id){for(Vehicle v:vehicles)if(v.id.equals(id))return v;return null;}

    private void build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(12),dp(20),dp(12),0);root.setBackgroundColor(dark?Color.rgb(7,19,28):Color.rgb(244,248,255));
        FrameLayout header=new FrameLayout(this);header.setLayoutParams(new LinearLayout.LayoutParams(-1,dp(58)));
        TextView back=tv("←",30,dark?Color.WHITE:white);back.setGravity(Gravity.CENTER);back.setOnClickListener(v->finish());header.addView(back,new FrameLayout.LayoutParams(dp(44),dp(50),Gravity.START));
        TextView title=tv("⚖  Comparar coches",22,text());title.setTypeface(null,Typeface.BOLD);title.setGravity(Gravity.CENTER);header.addView(title,new FrameLayout.LayoutParams(-1,dp(50),Gravity.CENTER));root.addView(header);

        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setClipToPadding(false);
        LinearLayout content=new LinearLayout(this);content.setOrientation(LinearLayout.VERTICAL);content.setPadding(0,0,0,dp(24));
        TextView hint=tv("Elige 2 o 3 coches para compararlos",14,sub());hint.setGravity(Gravity.CENTER);content.addView(hint,new LinearLayout.LayoutParams(-1,dp(30)));
        HorizontalScrollView carsScroll=new HorizontalScrollView(this);carsScroll.setHorizontalScrollBarEnabled(false);carsRow=new LinearLayout(this);carsRow.setOrientation(LinearLayout.HORIZONTAL);carsRow.setGravity(Gravity.TOP);carsScroll.addView(carsRow,new HorizontalScrollView.LayoutParams(-2,-2));content.addView(carsScroll,new LinearLayout.LayoutParams(-1,-2));
        TextView section=tv("Comparativa",18,text());section.setTypeface(null,Typeface.BOLD);section.setPadding(0,dp(14),0,dp(2));content.addView(section);
        TextView legend=tv("✦ Mejor valor",12,blue);legend.setGravity(Gravity.CENTER_VERTICAL);legend.setPadding(dp(2),0,0,dp(6));content.addView(legend,new LinearLayout.LayoutParams(-1,dp(28)));
        table=new LinearLayout(this);table.setOrientation(LinearLayout.VERTICAL);
        HorizontalScrollView tableScroll=new HorizontalScrollView(this);tableScroll.setHorizontalScrollBarEnabled(false);tableScroll.setClipToPadding(false);tableScroll.addView(table,new HorizontalScrollView.LayoutParams(-2,-2));content.addView(tableScroll,new LinearLayout.LayoutParams(-1,-2));
        summary=new LinearLayout(this);summary.setOrientation(LinearLayout.VERTICAL);summary.setPadding(0,dp(18),0,dp(8));content.addView(summary,new LinearLayout.LayoutParams(-1,-2));
        scroll.addView(content,new ScrollView.LayoutParams(-1,-2));root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);rebuild();
    }

    private int tableWidth(){return dp(112+145*Math.max(2,selectedIds.size()));}
    private void rebuild(){carsRow.removeAllViews();table.removeAllViews();summary.removeAllViews();
        for(String id:selectedIds){Vehicle v=find(id);if(v!=null){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(145),-2);lp.setMargins(dp(3),0,dp(3),0);carsRow.addView(carCard(v),lp);}}
        if(selectedIds.size()<3){LinearLayout empty=new LinearLayout(this);empty.setOrientation(LinearLayout.VERTICAL);empty.setGravity(Gravity.CENTER);empty.setPadding(dp(8),dp(10),dp(8),dp(10));empty.setBackground(strokeBg(dark?Color.rgb(14,26,38):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(220,229,240),16));TextView plus=tv("＋",28,blue);plus.setGravity(Gravity.CENTER);empty.addView(plus,new LinearLayout.LayoutParams(-1,dp(34)));TextView n=tv("Añadir coche",12,sub());n.setGravity(Gravity.CENTER);empty.addView(n,new LinearLayout.LayoutParams(-1,dp(24)));empty.setOnClickListener(v->showSearch());LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(145),dp(150));lp.setMargins(dp(3),0,dp(3),0);carsRow.addView(empty,lp);}
        if(selectedIds.size()>=2){
            addSection("Batería y autonomía");
            addRow("Batería", "battery", false); addRow("Tipo batería", "type", false); addRow("Autonomía WLTP", "range", true); addRow("Consumo", "cons", true);
            addSection("Prestaciones");
            addRow("Potencia", "power", true); addRow("Tracción", "drive", false); addRow("0–100 km/h", "acc", true);
            addSection("Carga");
            addRow("Carga AC", "ac", true); addRow("Carga DC", "dc", true); addRow("10–80 %", "charge", true);
            addSection("Practicidad");
            addRow("Maletero", "trunk", true); addRow("Peso", "weight", true);
            addSection("Precio");
            addRow("Precio", "price", true);
            buildSummary();
        }else{TextView t=tv("Selecciona al menos 2 coches para mostrar la comparativa.",14,sub());t.setGravity(Gravity.CENTER);t.setPadding(dp(10),dp(18),dp(10),dp(18));table.addView(t,new LinearLayout.LayoutParams(tableWidth(),-2));}}

    private void addSection(String title){TextView s=tv(title,14,blue);s.setTypeface(null,Typeface.BOLD);s.setGravity(Gravity.CENTER_VERTICAL);s.setPadding(dp(4),dp(12),dp(4),dp(6));table.addView(s,new LinearLayout.LayoutParams(tableWidth(),dp(40)));}

    private View carCard(Vehicle v){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setGravity(Gravity.CENTER_HORIZONTAL);c.setPadding(dp(8),dp(8),dp(8),dp(8));c.setBackground(strokeBg(dark?Color.rgb(21,31,42):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(220,229,240),16));
        TextView photo=tv("🚘",34,blue);photo.setGravity(Gravity.CENTER);photo.setBackground(bg(dark?Color.rgb(13,28,41):Color.rgb(239,245,252),12));c.addView(photo,new LinearLayout.LayoutParams(-1,dp(62)));
        TextView make=tv(v.make,12,blue);make.setTypeface(null,Typeface.BOLD);make.setGravity(Gravity.CENTER);make.setPadding(0,dp(8),0,0);c.addView(make,new LinearLayout.LayoutParams(-1,dp(28)));
        TextView model=tv(v.model,16,text());model.setTypeface(null,Typeface.BOLD);model.setGravity(Gravity.CENTER);c.addView(model,new LinearLayout.LayoutParams(-1,dp(25)));
        TextView ver=tv(v.version,11,sub());ver.setGravity(Gravity.CENTER);ver.setMaxLines(2);c.addView(ver,new LinearLayout.LayoutParams(-1,dp(34)));
        TextView year=tv(v.year>0?String.valueOf(v.year):"",11,sub());year.setGravity(Gravity.CENTER);c.addView(year,new LinearLayout.LayoutParams(-1,dp(22)));
        TextView rem=tv("✕  Quitar",12,Color.rgb(210,70,70));rem.setGravity(Gravity.CENTER);rem.setTypeface(null,Typeface.BOLD);rem.setPadding(0,dp(6),0,0);rem.setOnClickListener(x->remove(v.id));c.addView(rem,new LinearLayout.LayoutParams(-1,dp(34)));return c;}
    private void remove(String id){selectedIds.remove(id);saveSelection();rebuild();}
    private void saveSelection(){SharedPreferences.Editor e=getSharedPreferences(PREFS,MODE_PRIVATE).edit();e.putString(KEY_SELECTED_ORDERED,joinSelection());e.putStringSet(KEY_SELECTED,new LinkedHashSet<>(selectedIds));e.apply();}
    private String joinSelection(){StringBuilder s=new StringBuilder();for(String id:selectedIds){if(s.length()>0)s.append(',');s.append(id);}return s.toString();}

    private void addRow(String label,String key,boolean highlightBest){
        LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(8),dp(6),dp(8),dp(6));row.setBackground(bg(rowAlt(),10));
        TextView l=tv(label,13,text());l.setTypeface(null,Typeface.BOLD);l.setGravity(Gravity.CENTER_VERTICAL);row.addView(l,new LinearLayout.LayoutParams(dp(112),dp(42)));
        double best=highlightBest?bestNumeric(key):0;
        for(String id:selectedIds){Vehicle v=find(id);TextView val=tv(value(v,key),13,text());val.setGravity(Gravity.CENTER);val.setTypeface(null,Typeface.BOLD);LinearLayout.LayoutParams vp=new LinearLayout.LayoutParams(dp(145),dp(42));if(highlightBest&&isBest(v,key,best)){val.setTextColor(blue);val.setBackground(bg(bestBg(),8));val.setPadding(dp(3),0,dp(3),0);val.setText("✦ "+value(v,key));}row.addView(val,vp);}
        table.addView(row,new LinearLayout.LayoutParams(tableWidth(),-2));Space s=new Space(this);table.addView(s,new LinearLayout.LayoutParams(1,dp(4)));
    }

    private double numeric(Vehicle v,String key){if(v==null)return -1;if(key.equals("range"))return v.wltpKm;if(key.equals("cons"))return v.consumption;if(key.equals("power"))return v.powerKw;if(key.equals("ac"))return v.acKw;if(key.equals("dc"))return v.dcKw;if(key.equals("charge"))return v.chargeMin;if(key.equals("acc"))return v.acc;if(key.equals("trunk"))return v.trunk;if(key.equals("weight"))return v.weight;if(key.equals("price"))return v.price;return -1;}
    private double bestNumeric(String key){double best=-1;boolean lower=key.equals("cons")||key.equals("charge")||key.equals("acc")||key.equals("weight")||key.equals("price");for(String id:selectedIds){double n=numeric(find(id),key);if(n<0)continue;if(best<0|| (lower?n<best:n>best))best=n;}return best;}
    private boolean isBest(Vehicle v,String key,double best){double n=numeric(v,key);return n>=0&&best>=0&&Math.abs(n-best)<0.001;}

    private String value(Vehicle v,String k){if(v==null)return"—";if(k.equals("battery")){if(v.batteryKwh<=0)return"—";String gross=num(v.batteryKwh)+" kWh";if(v.usableBatteryKwh>0)gross+="\n("+num(v.usableBatteryKwh)+" útil)";return gross;}if(k.equals("type"))return empty(v.batteryType);if(k.equals("range"))return v.wltpKm>0?v.wltpKm+" km":"—";if(k.equals("cons"))return v.consumption>0?num(v.consumption)+" kWh/100":"—";if(k.equals("power"))return v.powerKw>0?num(v.powerKw)+" kW":"—";if(k.equals("drive"))return empty(v.drivetrain);if(k.equals("ac"))return v.acKw>0?num(v.acKw)+" kW":"—";if(k.equals("dc"))return v.dcKw>0?num(v.dcKw)+" kW":"—";if(k.equals("charge"))return v.chargeMin>0?v.chargeMin+" min":"—";if(k.equals("acc"))return v.acc>0?num(v.acc)+" s":"—";if(k.equals("trunk"))return v.trunk>0?v.trunk+" L":"—";if(k.equals("weight"))return v.weight>0?v.weight+" kg":"—";if(k.equals("price"))return price(v);return"—";}
    private String price(Vehicle v){if(v==null||v.price<=0)return"—";SharedPreferences p=getSharedPreferences(PREFS,MODE_PRIVATE);String currency=p.getString(KEY_CURRENCY,"EUR");double converted=CurrencyRateManager.convertFromEur(this,v.price,currency);return CurrencyNumberFormatter.format(converted,0,currency)+" "+currencySymbol(currency);}
    private String currencySymbol(String currency){if("USD".equals(currency))return"$";if("GBP".equals(currency))return"£";if("CHF".equals(currency))return"CHF";if("CAD".equals(currency))return"CA$";if("AUD".equals(currency))return"A$";return"€";}
    private String empty(String s){return s==null||s.trim().isEmpty()?"—":s;}
    private String num(double n){if(n==0)return"—";return String.format(Locale.US,"%.1f",n).replace('.',',');}

    private void buildSummary(){
        TextView title=tv("🏆  Resumen de la comparativa",18,text());title.setTypeface(null,Typeface.BOLD);title.setPadding(0,0,0,dp(10));summary.addView(title,new LinearLayout.LayoutParams(-1,dp(38)));
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(dp(14),dp(12),dp(14),dp(12));box.setBackground(strokeBg(dark?Color.rgb(18,34,48):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(220,229,240),16));
        int[] wins=new int[selectedIds.size()];String[] categories={"range","cons","power","ac","dc","acc","trunk","weight","price"};
        for(String key:categories){double best=bestNumeric(key);for(int i=0;i<selectedIds.size();i++){Vehicle v=find(selectedIds.get(i));if(isBest(v,key,best))wins[i]++;}}
        int winner=-1,max=-1;for(int i=0;i<wins.length;i++){if(wins[i]>max){max=wins[i];winner=i;}else if(wins[i]==max&&max>0)winner=-2;}
        if(winner>=0){Vehicle v=find(selectedIds.get(winner));TextView w=tv("🏆 Mejor equilibrio: "+shortName(v),16,blue);w.setTypeface(null,Typeface.BOLD);box.addView(w,new LinearLayout.LayoutParams(-1,dp(30)));}
        else {TextView w=tv("🏆 Comparativa muy equilibrada",16,blue);w.setTypeface(null,Typeface.BOLD);box.addView(w,new LinearLayout.LayoutParams(-1,dp(30)));}
        addCategoryWinner(box,"🔋 Autonomía y eficiencia",new String[]{"range","cons"},new boolean[]{false,false});
        addCategoryWinner(box,"⚡ Carga",new String[]{"ac","dc","charge"},new boolean[]{false,false,true});
        addCategoryWinner(box,"🚀 Prestaciones",new String[]{"power","acc"},new boolean[]{false,true});
        addCategoryWinner(box,"📦 Practicidad",new String[]{"trunk","weight"},new boolean[]{false,true});
        addCategoryWinner(box,"💰 Precio",new String[]{"price"},new boolean[]{true});
        String conclusion=summaryConclusion(winner,wins);
        TextView c=tv("Conclusión\n"+conclusion,13,text());c.setPadding(0,dp(10),0,0);box.addView(c,new LinearLayout.LayoutParams(-1,-2));
        summary.addView(box,new LinearLayout.LayoutParams(-1,-2));
    }

    private void addCategoryWinner(LinearLayout box,String label,String[] keys,boolean[] lower){
        int[] score=new int[selectedIds.size()];for(int k=0;k<keys.length;k++){String key=keys[k];double best=bestNumeric(key);for(int i=0;i<selectedIds.size();i++){Vehicle v=find(selectedIds.get(i));if(isBest(v,key,best))score[i]++;}}
        int max=0;for(int n:score)if(n>max)max=n;if(max==0)return;StringBuilder names=new StringBuilder();for(int i=0;i<score.length;i++)if(score[i]==max){if(names.length()>0)names.append(" / ");names.append(shortName(find(selectedIds.get(i))));}
        TextView row=tv(label+": "+names,13,text());row.setPadding(0,dp(4),0,0);box.addView(row,new LinearLayout.LayoutParams(-1,dp(28)));
    }

    private String summaryConclusion(int winner,int[] wins){
        if(winner==-2)return"No hay un ganador único: los coches están muy igualados y la elección depende de qué características valores más.";
        if(winner<0)return"No hay suficientes datos comparables para establecer un ganador general.";
        Vehicle v=find(selectedIds.get(winner));StringBuilder s=new StringBuilder(shortName(v)+" destaca por acumular más mejores resultados en las características comparables. ");
        if(wins[winner]>=4)s.append("Es la opción más completa de esta comparativa.");else s.append("Aun así, revisa las categorías que más peso tengan para tu uso.");return s.toString();
    }

    private String shortName(Vehicle v){if(v==null)return"—";String n=(v.make+" "+v.model).trim();return n.length()>34?n.substring(0,34)+"…":n;}

    private void showDataError(Throwable t){if(table==null)return;carsRow.removeAllViews();TextView m=tv("No se han podido cargar los datos de los coches.\n\n"+t.getClass().getSimpleName(),14,sub());m.setGravity(Gravity.CENTER);m.setPadding(dp(10),dp(18),dp(10),dp(18));table.removeAllViews();summary.removeAllViews();table.addView(m,new LinearLayout.LayoutParams(tableWidth(),-2));}

    private void showSearch(){if(selectedIds.size()>=3)return;final EditText input=new EditText(this);input.setHint("Buscar por marca, modelo o versión");input.setSingleLine(true);input.setTextColor(text());input.setHintTextColor(sub());final LinearLayout list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.addView(list,new ScrollView.LayoutParams(-1,-2));LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(dp(16),dp(8),dp(16),dp(8));box.addView(input,new LinearLayout.LayoutParams(-1,dp(52)));box.addView(scroll,new LinearLayout.LayoutParams(-1,dp(330)));AlertDialog dialog=new AlertDialog.Builder(this).setTitle("Añadir coche").setView(box).setNegativeButton("Cancelar",null).create();Runnable refresh=()->{list.removeAllViews();String q=input.getText().toString().trim().toLowerCase(Locale.ROOT);int count=0;for(Vehicle v:vehicles){if(selectedIds.contains(v.id))continue;String hay=(v.make+" "+v.model+" "+v.version).toLowerCase(Locale.ROOT);if(q.isEmpty()||hay.contains(q)){LinearLayout item=new LinearLayout(this);item.setOrientation(LinearLayout.VERTICAL);item.setGravity(Gravity.CENTER_VERTICAL);item.setPadding(dp(10),dp(8),dp(10),dp(8));item.setBackground(bg(dark?Color.rgb(21,31,42):Color.WHITE,10));TextView ti=tv(v.make+" "+v.model,15,text());ti.setTypeface(null,Typeface.BOLD);item.addView(ti);TextView de=tv(v.version+"  ·  "+v.year,12,sub());item.addView(de);item.setOnClickListener(x->{if(selectedIds.size()<3){selectedIds.add(v.id);saveSelection();dialog.dismiss();rebuild();}});list.addView(item,new LinearLayout.LayoutParams(-1,dp(58)));Space gap=new Space(this);list.addView(gap,new LinearLayout.LayoutParams(1,dp(4)));if(++count>=15)break;}}if(count==0){TextView none=tv(q.isEmpty()?"No hay coches disponibles":"No se encontraron coches",14,sub());none.setGravity(Gravity.CENTER);list.addView(none,new LinearLayout.LayoutParams(-1,dp(60)));}};input.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int b,int c){refresh.run();}public void afterTextChanged(Editable e){}});dialog.setOnShowListener(x->{refresh.run();input.requestFocus();dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);});dialog.show();}

    private static class Vehicle{String id,make,model,version,batteryType,drivetrain;int year,wltpKm,chargeMin,trunk,weight;double price,batteryKwh,usableBatteryKwh,consumption,powerKw,acKw,dcKw,acc;Vehicle(JSONObject o){id=o.optString("id");make=o.optString("make");model=o.optString("model");version=o.optString("version");year=o.optInt("year");price=o.optDouble("price",0);batteryKwh=o.optDouble("batteryKwh",0);usableBatteryKwh=o.optDouble("usableBatteryKwh",0);batteryType=o.optString("batteryType","");wltpKm=o.optInt("wltpKm",0);consumption=o.optDouble("consumptionKwh100",0);powerKw=o.optDouble("powerKw",0);drivetrain=o.optString("drivetrain","");acKw=o.optDouble("acKw",0);dcKw=o.optDouble("dcKw",0);chargeMin=o.optInt("charge10to80Min",0);acc=o.optDouble("acceleration0to100Sec",0);trunk=o.optInt("trunkLiters",0);weight=o.optInt("weightKg",0);}}
}