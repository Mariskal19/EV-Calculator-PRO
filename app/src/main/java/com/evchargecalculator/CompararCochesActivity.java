package com.evchargecalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/** Comparador de coches. Carga los datos despues de construir la pantalla para evitar cierres durante el arranque. */
public class CompararCochesActivity extends Activity {
    private static final String PREFS="ev_charge_calculator";
    private static final String KEY_SELECTED="compare_vehicle_ids";
    private final int blue=Color.rgb(46,107,255), white=Color.rgb(22,42,63), secondary=Color.rgb(90,111,137);
    private boolean dark; private LinearLayout carsRow, table;
    private final List<Vehicle> vehicles=new ArrayList<>(); private final List<String> selectedIds=new ArrayList<>();

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        try {
            SharedPreferences p=getSharedPreferences(PREFS,MODE_PRIVATE);
            dark=p.contains("dark_theme")?p.getBoolean("dark_theme",false):(getResources().getConfiguration().uiMode&0x30)==0x20;
            build();
            try {
                loadVehicles();
                loadSelection();
                rebuild();
            } catch(Throwable t) {
                android.util.Log.e("CompararCoches","Error cargando datos",t);
                showDataError(t);
            }
        } catch(Throwable t) {
            android.util.Log.e("CompararCoches","Crash in screen creation",t);
            TextView error=new TextView(this);
            error.setPadding(dp(24),dp(24),dp(24),dp(24));
            error.setText("Error al abrir Comparar coches\n\n"+t.getClass().getSimpleName()+"\n"+String.valueOf(t.getMessage()));
            error.setTextSize(16); error.setTextColor(Color.WHITE); error.setGravity(Gravity.CENTER);
            error.setBackgroundColor(Color.rgb(8,34,58)); setContentView(error);
        }
    }

    private int dp(int n){return(int)(n*getResources().getDisplayMetrics().density+.5f);}
    private int text(){return dark?Color.rgb(245,248,255):white;}
    private int sub(){return dark?Color.rgb(170,183,204):secondary;}
    private GradientDrawable bg(int color,float r){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp((int)r));return g;}
    private TextView tv(String s,float size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);return t;}

    private void loadVehicles(){
        vehicles.clear();
        try(InputStream in=getAssets().open("vehicles.json"); BufferedReader r=new BufferedReader(new InputStreamReader(in))){
            StringBuilder sb=new StringBuilder(); String line; while((line=r.readLine())!=null)sb.append(line);
            JSONObject root=new JSONObject(sb.toString()); JSONArray arr=root.optJSONArray("vehicles");
            if(arr==null)throw new IllegalStateException("vehicles array missing");
            for(int i=0;i<arr.length();i++){JSONObject o=arr.optJSONObject(i);if(o!=null)vehicles.add(new Vehicle(o));}
            if(vehicles.isEmpty())throw new IllegalStateException("vehicles array empty");
        }catch(Exception e){throw new IllegalStateException("No se ha podido cargar vehicles.json",e);}
    }

    private void loadSelection(){
        selectedIds.clear();
        SharedPreferences p=getSharedPreferences(PREFS,MODE_PRIVATE);
        Set<String> set=p.getStringSet(KEY_SELECTED,null);
        if(set!=null)for(String id:set)if(id!=null&&find(id)!=null&&selectedIds.size()<3)selectedIds.add(id);
    }
    private Vehicle find(String id){for(Vehicle v:vehicles)if(v.id.equals(id))return v;return null;}

    private void build(){
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(12),dp(20),dp(12),dp(16));
        root.setBackgroundColor(dark?Color.rgb(7,19,28):Color.rgb(244,248,255));
        FrameLayout header=new FrameLayout(this); header.setLayoutParams(new LinearLayout.LayoutParams(-1,dp(58)));
        TextView back=tv("←",30,dark?Color.WHITE:white);back.setGravity(Gravity.CENTER);back.setOnClickListener(v->finish());
        header.addView(back,new FrameLayout.LayoutParams(dp(44),dp(50),Gravity.START));
        TextView title=tv("⚖  Comparar coches",22,text());title.setTypeface(null,Typeface.BOLD);title.setGravity(Gravity.CENTER);header.addView(title,new FrameLayout.LayoutParams(-1,dp(50),Gravity.CENTER));
        root.addView(header);
        TextView hint=tv("Elige 2 o 3 coches para compararlos",14,sub());hint.setGravity(Gravity.CENTER);root.addView(hint,new LinearLayout.LayoutParams(-1,dp(30)));
        carsRow=new LinearLayout(this);carsRow.setOrientation(LinearLayout.HORIZONTAL);carsRow.setGravity(Gravity.CENTER);root.addView(carsRow,new LinearLayout.LayoutParams(-1,-2));
        Button add=new Button(this);add.setText("＋ Añadir coche");add.setOnClickListener(v->showSearch());root.addView(add,new LinearLayout.LayoutParams(-1,dp(52)));
        TextView section=tv("Comparativa",18,text());section.setTypeface(null,Typeface.BOLD);section.setPadding(0,dp(12),0,dp(8));root.addView(section);
        table=new LinearLayout(this);table.setOrientation(LinearLayout.VERTICAL);root.addView(table,new LinearLayout.LayoutParams(-1,-2));
        setContentView(root); rebuild();
    }

    private void rebuild(){
        carsRow.removeAllViews(); table.removeAllViews();
        for(String id:selectedIds){Vehicle v=find(id);if(v!=null)carsRow.addView(carCard(v),new LinearLayout.LayoutParams(0,-2,1));}
        if(selectedIds.size()<3){TextView note=tv("Pulsa «Añadir coche» para incorporar otro modelo.",13,sub());note.setGravity(Gravity.CENTER);carsRow.addView(note,new LinearLayout.LayoutParams(0,dp(70),1));}
        if(selectedIds.size()>=2){String[][] rows={{"Batería","battery"},{"Tipo batería","type"},{"Autonomía WLTP","range"},{"Consumo","cons"},{"Potencia","power"},{"Tracción","drive"},{"Carga AC","ac"},{"Carga DC","dc"},{"10–80 %","charge"},{"0–100 km/h","acc"},{"Maletero","trunk"},{"Peso","weight"},{"Precio","price"}};for(String[] row:rows)addRow(row[0],row[1]);}
        else {TextView t=tv("Selecciona al menos 2 coches para mostrar la comparativa.",14,sub());t.setGravity(Gravity.CENTER);t.setPadding(dp(10),dp(18),dp(10),dp(18));table.addView(t);}
    }

    private void showDataError(Throwable t){
        if(table==null)return;
        carsRow.removeAllViews();
        TextView msg=tv("No se han podido cargar los datos de los coches.\n\n"+t.getClass().getSimpleName(),14,sub());
        msg.setGravity(Gravity.CENTER); msg.setPadding(dp(10),dp(18),dp(10),dp(18)); table.removeAllViews(); table.addView(msg);
    }

    private TextView carCard(Vehicle v){TextView t=tv("🚘\n"+v.make+" "+v.model+"\n"+v.version+" · "+v.year+"\n✕ Quitar",14,text());t.setGravity(Gravity.CENTER);t.setPadding(dp(4),dp(8),dp(4),dp(8));t.setBackground(bg(dark?Color.rgb(21,31,42):Color.WHITE,16));t.setOnClickListener(x->remove(v.id));return t;}
    private void remove(String id){selectedIds.remove(id);saveSelection();rebuild();}
    private void saveSelection(){getSharedPreferences(PREFS,MODE_PRIVATE).edit().putStringSet(KEY_SELECTED,new HashSet<>(selectedIds)).apply();}

    private void addRow(String label,String key){
        LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);row.setPadding(dp(8),dp(7),dp(8),dp(7));row.setBackground(bg(dark?Color.rgb(18,29,41):Color.WHITE,10));
        TextView l=tv(label,13,text());l.setTypeface(null,Typeface.BOLD);row.addView(l,new LinearLayout.LayoutParams(dp(105),-2));
        for(String id:selectedIds){Vehicle v=find(id);TextView val=tv(value(v,key),13,text());val.setGravity(Gravity.CENTER);row.addView(val,new LinearLayout.LayoutParams(0,-2,1));}
        table.addView(row,new LinearLayout.LayoutParams(-1,-2));
        Space s=new Space(this);table.addView(s,new LinearLayout.LayoutParams(1,dp(3)));
    }
    private String value(Vehicle v,String k){if(v==null)return "—";if("battery".equals(k))return num(v.batteryKwh)+" kWh";if("type".equals(k))return empty(v.batteryType);if("range".equals(k))return v.wltpKm>0?v.wltpKm+" km":"—";if("cons".equals(k))return num(v.consumption)+" kWh/100";if("power".equals(k))return num(v.powerKw)+" kW";if("drive".equals(k))return empty(v.drivetrain);if("ac".equals(k))return num(v.acKw)+" kW";if("dc".equals(k))return num(v.dcKw)+" kW";if("charge".equals(k))return v.chargeMin>0?v.chargeMin+" min":"—";if("acc".equals(k))return num(v.acc)+" s";if("trunk".equals(k))return v.trunk>0?v.trunk+" L":"—";if("weight".equals(k))return v.weight>0?v.weight+" kg":"—";if("price".equals(k))return v.price>0?num(v.price)+" €":"—";return "—";}
    private String empty(String s){return s==null||s.trim().isEmpty()?"—":s;}
    private String num(double n){if(n==0)return "—";return String.format(Locale.US,"%.1f",n).replace('.',',');}

    private void showSearch(){
        if(selectedIds.size()>=3)return;
        final EditText input=new EditText(this);input.setHint("Marca o modelo");
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(dp(16),dp(8),dp(16),dp(8));box.addView(input);
        LinearLayout list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);box.addView(list);
        AlertDialog dialog=new AlertDialog.Builder(this).setTitle("Añadir coche").setView(box).setNegativeButton("Cancelar",null).create();
        Runnable refresh=()->{list.removeAllViews();String q=input.getText().toString().trim().toLowerCase(Locale.ROOT);int count=0;for(Vehicle v:vehicles){if(selectedIds.contains(v.id))continue;String hay=(v.make+" "+v.model+" "+v.version).toLowerCase(Locale.ROOT);if(q.isEmpty()||hay.contains(q)){TextView item=tv(v.make+" "+v.model+" — "+v.version+" ("+v.year+")",15,text());item.setPadding(dp(8),dp(12),dp(8),dp(12));item.setOnClickListener(x->{selectedIds.add(v.id);saveSelection();dialog.dismiss();rebuild();});list.addView(item);if(++count>=12)break;}};if(count==0){TextView none=tv("No se encontraron modelos",14,sub());none.setPadding(dp(8),dp(12),dp(8),dp(12));list.addView(none);}};
        input.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int b,int c){refresh.run();}public void afterTextChanged(android.text.Editable e){}});dialog.setOnShowListener(x->refresh.run());dialog.show();
    }

    private static class Vehicle{
        String id,make,model,version,batteryType,drivetrain;int year,wltpKm,chargeMin,trunk,weight;double price,batteryKwh,consumption,powerKw,acKw,dcKw,acc;
        Vehicle(JSONObject o){id=o.optString("id");make=o.optString("make");model=o.optString("model");version=o.optString("version");year=o.optInt("year");price=o.optDouble("price",0);batteryKwh=o.optDouble("batteryKwh",0);batteryType=o.optString("batteryType","");wltpKm=o.optInt("wltpKm",0);consumption=o.optDouble("consumptionKwh100",0);powerKw=o.optDouble("powerKw",0);drivetrain=o.optString("drivetrain","");acKw=o.optDouble("acKw",0);dcKw=o.optDouble("dcKw",0);chargeMin=o.optInt("charge10to80Min",0);acc=o.optDouble("acceleration0to100Sec",0);trunk=o.optInt("trunkLiters",0);weight=o.optInt("weightKg",0);}
    }
}
