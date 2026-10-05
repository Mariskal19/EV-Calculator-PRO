package com.evchargecalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.*;

public class SimilarCarsActivity extends BaseNavigationActivity {
    @Override protected int getBottomNavigationIndex(){ return 2; }

    private static final String PREFS="ev_charge_calculator";
    private boolean dark;
    private final List<Vehicle> vehicles=new ArrayList<>();
    private EditText search;
    private Spinner marketSpinner, yearSpinner, driveSpinner, batterySpinner;
    private LinearLayout results;
    private TextView selectedTitle, resultsTitle;
    private Vehicle reference;

    private int blue=Color.rgb(46,107,255);
    private int text(){return dark?Color.rgb(245,248,255):Color.rgb(22,42,63);}
    private int sub(){return dark?Color.rgb(170,183,204):Color.rgb(90,111,137);}
    private int dp(int n){return(int)(n*getResources().getDisplayMetrics().density+.5f);}
    private TextView tv(String s,float z,int c){TextView t=new TextView(this);t.setText(LanguageManager.t(this,s));t.setTextSize(z);t.setTextColor(c);return t;}
    private GradientDrawable bg(int c,float r){GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(dp((int)r));return g;}
    private GradientDrawable strokeBg(int fill,int stroke,float r){GradientDrawable g=bg(fill,r);g.setStroke(dp(1),stroke);return g;}

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        LanguageManager.applyStored(this);
        dark=isDarkTheme();
        loadVehicles();
        build();
        EdgeToEdgeHelper.apply(this,dark);
    }

    private void build(){
        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(dark?Color.rgb(7,19,28):Color.rgb(241,246,251));

        ScrollView scroll=new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setVerticalScrollBarEnabled(false);
        LinearLayout c=new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(dp(14),dp(18),dp(14),dp(18));

        TextView back=tv("‹  Volver a comparar coches",14,blue);
        back.setTypeface(null,Typeface.BOLD);
        back.setPadding(dp(4),0,0,dp(10));
        back.setOnClickListener(v->finish());
        c.addView(back,new LinearLayout.LayoutParams(-1,dp(36)));

        TextView title=tv("Buscar coches similares",24,text());
        title.setTypeface(null,Typeface.BOLD);
        c.addView(title,new LinearLayout.LayoutParams(-1,dp(38)));

        TextView intro=tv("Busca un coche de referencia y te mostraremos las 5 opciones más similares del catálogo.",14,sub());
        intro.setLineSpacing(0,1.15f);
        c.addView(intro,new LinearLayout.LayoutParams(-1,dp(48)));

        search=new EditText(this);
        search.setSingleLine(true);
        search.setHint(LanguageManager.t(this,"🔎 Marca o modelo"));
        search.setTextColor(text());
        search.setHintTextColor(sub());
        search.setTextSize(15);
        search.setPadding(dp(14),0,dp(14),0);
        search.setBackground(strokeBg(dark?Color.rgb(17,31,44):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(218,228,239),14));
        c.addView(search,new LinearLayout.LayoutParams(-1,dp(52)));

        LinearLayout filters=new LinearLayout(this);
        filters.setOrientation(LinearLayout.VERTICAL);
        filters.setPadding(dp(10),dp(10),dp(10),dp(10));
        filters.setBackground(strokeBg(dark?Color.rgb(17,31,44):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(218,228,239),16));
        LinearLayout row1=new LinearLayout(this); row1.setOrientation(LinearLayout.HORIZONTAL);
        marketSpinner=spinner(); yearSpinner=spinner();
        addSpinner(row1,marketSpinner,0.5f); addSpinner(row1,yearSpinner,0.5f);
        filters.addView(row1,new LinearLayout.LayoutParams(-1,dp(50)));
        LinearLayout row2=new LinearLayout(this); row2.setOrientation(LinearLayout.HORIZONTAL);
        driveSpinner=spinner(); batterySpinner=spinner();
        addSpinner(row2,driveSpinner,0.5f); addSpinner(row2,batterySpinner,0.5f);
        filters.addView(row2,new LinearLayout.LayoutParams(-1,dp(50)));
        c.addView(filters,marginLp(-1,-2,0,dp(10),0,dp(10)));

        selectedTitle=tv("1. Elige el coche de referencia",17,text());
        selectedTitle.setTypeface(null,Typeface.BOLD);
        c.addView(selectedTitle,new LinearLayout.LayoutParams(-1,dp(34)));

        LinearLayout referenceResults=new LinearLayout(this);
        referenceResults.setOrientation(LinearLayout.VERTICAL);
        referenceResults.setPadding(0,0,0,dp(8));
        c.addView(referenceResults,new LinearLayout.LayoutParams(-1,-2));
        results=referenceResults;

        resultsTitle=tv("2. 5 coches similares",17,text());
        resultsTitle.setTypeface(null,Typeface.BOLD);
        resultsTitle.setVisibility(View.GONE);
        c.addView(resultsTitle,new LinearLayout.LayoutParams(-1,dp(36)));

        TextView note=tv("La similitud combina batería, autonomía, potencia, consumo, precio, tamaño, maletero, carga y prestaciones. Los datos protegidos no se sobrescriben con los externos.",12,sub());
        note.setLineSpacing(0,1.15f);
        note.setPadding(dp(4),dp(8),dp(4),dp(12));
        c.addView(note,new LinearLayout.LayoutParams(-1,-2));

        scroll.addView(c);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);

        populateFilters();
        TextWatcher w=new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){} public void onTextChanged(CharSequence s,int st,int b,int c){showReferenceCandidates();} public void afterTextChanged(Editable e){}};
        search.addTextChangedListener(w);
        AdapterView.OnItemSelectedListener listener=new AdapterView.OnItemSelectedListener(){public void onItemSelected(AdapterView<?> p,View v,int a,long b){showReferenceCandidates();} public void onNothingSelected(AdapterView<?> p){}};
        marketSpinner.setOnItemSelectedListener(listener); yearSpinner.setOnItemSelectedListener(listener); driveSpinner.setOnItemSelectedListener(listener); batterySpinner.setOnItemSelectedListener(listener);
        showReferenceCandidates();
    }

    private LinearLayout.LayoutParams marginLp(int w,int h,int l,int t,int r,int b){
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(w,h);p.setMargins(l,t,r,b);return p;
    }
    private Spinner spinner(){Spinner s=new Spinner(this);s.setBackground(strokeBg(dark?Color.rgb(17,31,44):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(218,228,239),10));return s;}
    private void addSpinner(LinearLayout row,Spinner s,float weight){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-1,weight);p.setMargins(dp(3),dp(3),dp(3),dp(3));row.addView(s,p);}

    private void populateFilters(){
        List<String> markets=new ArrayList<>(); markets.add("Todos los mercados");
        TreeSet<String> ms=new TreeSet<>(); for(Vehicle v:vehicles)if(!v.market.isEmpty())ms.add(v.market);
        markets.addAll(ms); setAdapter(marketSpinner,markets);

        List<String> years=new ArrayList<>(Arrays.asList("Todos los años","2026","2025","2024")); setAdapter(yearSpinner,years);

        List<String> drives=new ArrayList<>(Arrays.asList("Cualquier tracción","FWD","RWD","AWD")); setAdapter(driveSpinner,drives);
        List<String> bats=new ArrayList<>(Arrays.asList("Cualquier batería","≤ 50 kWh","50–70 kWh","70–90 kWh","> 90 kWh")); setAdapter(batterySpinner,bats);
    }
    private void setAdapter(Spinner s,List<String> data){ArrayAdapter<String>a=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,data){@Override public View getView(int p,android.view.View v,android.view.ViewGroup g){TextView t=(TextView)super.getView(p,v,g);t.setTextColor(text());t.setTextSize(12);t.setPadding(dp(8),0,dp(4),0);return t;}@Override public View getDropDownView(int p,android.view.View v,android.view.ViewGroup g){TextView t=(TextView)super.getDropDownView(p,v,g);t.setTextColor(Color.DKGRAY);t.setTextSize(13);return t;}};a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);s.setAdapter(a);}

    private void showReferenceCandidates(){
        if(results==null)return;
        reference=null; resultsTitle.setVisibility(View.GONE);
        results.removeAllViews();
        String q=norm(search.getText().toString());
        int count=0;
        for(Vehicle v:vehicles){
            if(!passesFilters(v))continue;
            if(!q.isEmpty()&&!norm(v.make+" "+v.model+" "+v.version).contains(q))continue;
            addReferenceCard(v);
            if(++count>=8)break;
        }
        if(count==0){
            TextView empty=tv("No se han encontrado coches con esos criterios.",13,sub());empty.setPadding(dp(10),dp(12),dp(10),dp(12));results.addView(empty);
        }
    }

    private void addReferenceCard(Vehicle v){
        LinearLayout card=card();
        TextView name=tv(v.make+" "+v.model,16,text());name.setTypeface(null,Typeface.BOLD);
        card.addView(name,new LinearLayout.LayoutParams(-1,dp(27)));
        TextView info=tv(v.version+"  ·  "+v.year+"  ·  "+market(v.market),12,sub());
        card.addView(info,new LinearLayout.LayoutParams(-1,dp(24)));
        TextView specs=tv(specLine(v),12,sub());card.addView(specs,new LinearLayout.LayoutParams(-1,dp(25)));
        card.setOnClickListener(x->{reference=v;showSimilar();});
        results.addView(card,marginLp(-1,-2,0,0,0,dp(8)));
    }

    private void showSimilar(){
        results.removeAllViews();
        selectedTitle.setText(LanguageManager.t(this,"1. Coche de referencia: "+reference.make+" "+reference.model));
        resultsTitle.setVisibility(View.VISIBLE);
        TextView ref=tv(reference.version+"  ·  "+reference.year+"  ·  "+specLine(reference),12,sub());
        ref.setPadding(dp(4),0,dp(4),dp(10));results.addView(ref);

        List<Scored> scored=new ArrayList<>();
        for(Vehicle v:vehicles){
            if(v==reference)continue;
            if(!passesFilters(v))continue;
            double score=similarity(reference,v);
            if(Double.isFinite(score))scored.add(new Scored(v,score));
        }
        Collections.sort(scored,(a,b)->Double.compare(a.score,b.score));
        int n=Math.min(5,scored.size());
        for(int i=0;i<n;i++)addSimilarCard(scored.get(i),i+1);
        if(n==0)results.addView(tv("No hay suficientes opciones similares con estos filtros.",13,sub()));
    }

    private void addSimilarCard(Scored s,int rank){
        Vehicle v=s.v; LinearLayout card=card();
        TextView rankTv=tv("#"+rank+"  "+v.make+" "+v.model,16,text());rankTv.setTypeface(null,Typeface.BOLD);
        card.addView(rankTv,new LinearLayout.LayoutParams(-1,dp(28)));
        TextView ver=tv(v.version+"  ·  "+v.year+"  ·  "+market(v.market),12,sub());card.addView(ver,new LinearLayout.LayoutParams(-1,dp(24)));
        TextView specs=tv(specLine(v),12,sub());card.addView(specs,new LinearLayout.LayoutParams(-1,dp(25)));
        TextView match=tv("Similitud  "+Math.round(Math.max(0,Math.min(100,100-s.score*100)))+" %",12,blue);match.setTypeface(null,Typeface.BOLD);card.addView(match,new LinearLayout.LayoutParams(-1,dp(25)));
        card.setOnClickListener(x->openDetail(v));
        results.addView(card,marginLp(-1,-2,0,0,0,dp(8)));
    }

    private LinearLayout card(){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(dp(14),dp(10),dp(14),dp(10));c.setBackground(strokeBg(dark?Color.rgb(17,31,44):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(218,228,239),14));return c;}

    private double similarity(Vehicle a,Vehicle b){
        double sum=0,weight=0;
        double d;
        d=rel(a.batteryKwh,b.batteryKwh,80); if(d>=0){sum+=d*.15;weight+=.15;}
        d=rel(a.wltpKm,b.wltpKm,400); if(d>=0){sum+=d*.15;weight+=.15;}
        d=rel(a.powerKw,b.powerKw,250); if(d>=0){sum+=d*.15;weight+=.15;}
        d=rel(a.consumption,b.consumption,10); if(d>=0){sum+=d*.10;weight+=.10;}
        d=rel(a.price,b.price,40000); if(d>=0){sum+=d*.10;weight+=.10;}
        d=rel(a.trunk,b.trunk,500); if(d>=0){sum+=d*.08;weight+=.08;}
        d=dimensionDistance(a,b); if(d>=0){sum+=d*.12;weight+=.12;}
        d=rel(a.acc,b.acc,5); if(d>=0){sum+=d*.05;weight+=.05;}
        d=rel(a.dcKw,b.dcKw,150); if(d>=0){sum+=d*.05;weight+=.05;}
        if(!a.drivetrain.isEmpty()&&!b.drivetrain.isEmpty()){sum+=(a.drivetrain.equalsIgnoreCase(b.drivetrain)?0:.35)*.05;weight+=.05;}
        return weight>0?sum/weight:Double.POSITIVE_INFINITY;
    }
    private double rel(double a,double b,double scale){if(a<=0||b<=0)return -1;return Math.min(1,Math.abs(a-b)/scale);}
    private double dimensionDistance(Vehicle a,Vehicle b){
        double sum=0;int n=0;
        if(a.lengthMm>0&&b.lengthMm>0){sum+=Math.min(1,Math.abs(a.lengthMm-b.lengthMm)/1000);n++;}
        if(a.widthMm>0&&b.widthMm>0){sum+=Math.min(1,Math.abs(a.widthMm-b.widthMm)/500);n++;}
        if(a.heightMm>0&&b.heightMm>0){sum+=Math.min(1,Math.abs(a.heightMm-b.heightMm)/500);n++;}
        return n==0?-1:sum/n;
    }

    private boolean passesFilters(Vehicle v){
        String m=String.valueOf(marketSpinner.getSelectedItem()); if(m!=null&&!m.equals("Todos los mercados")&&!v.market.equalsIgnoreCase(m))return false;
        String y=String.valueOf(yearSpinner.getSelectedItem()); if(y!=null&&!y.startsWith("Todos")&&v.year!=Integer.parseInt(y))return false;
        String d=String.valueOf(driveSpinner.getSelectedItem()); if(d!=null&&!d.startsWith("Cualquier")&&!v.drivetrain.toUpperCase(Locale.ROOT).contains(d))return false;
        String b=String.valueOf(batterySpinner.getSelectedItem()); double k=v.batteryKwh;
        if(b!=null&&!b.startsWith("Cualquier")&&k>0){if(b.startsWith("≤")&&k>50)return false;if(b.startsWith("50")&&(k<50||k>70))return false;if(b.startsWith("70")&&(k<70||k>90))return false;if(b.startsWith(">")&&k<=90)return false;}
        return true;
    }

    private void loadVehicles(){
        try(InputStream in=getAssets().open("catalog_es_2024_2026.json");BufferedReader r=new BufferedReader(new InputStreamReader(in))){
            StringBuilder sb=new StringBuilder();String line;while((line=r.readLine())!=null)sb.append(line);
            JSONArray a=new JSONObject(sb.toString()).optJSONArray("vehicles");if(a==null)throw new IllegalStateException("vehicles missing");
            for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o!=null)vehicles.add(new Vehicle(o,false));}
        }catch(Exception e){throw new IllegalStateException("No se ha podido cargar el catálogo",e);}
        Set<String> keys=new HashSet<>();for(Vehicle v:vehicles)keys.add(logicalKey(v));
        JSONArray remote=RemoteCatalogManager.loadCached(this);
        addRemote(remote,keys);
        RemoteCatalogManager.refreshIfDue(this,additions->{if(additions==null)return;int before=vehicles.size();addRemote(additions,keys);if(vehicles.size()!=before)showReferenceCandidates();});
        Collections.sort(vehicles,(a,b)->{int c=a.make.compareToIgnoreCase(b.make);if(c!=0)return c;return a.model.compareToIgnoreCase(b.model);});
    }
    private void addRemote(JSONArray a,Set<String> keys){
        if(a==null)return;
        for(int i=0;i<a.length();i++)try{
            JSONObject o=a.optJSONObject(i);if(o==null)continue;Vehicle v=new Vehicle(o,true);
            if(v.year<2024||v.year>2026||v.make.isEmpty()||v.model.isEmpty()||v.version.isEmpty()||v.batteryKwh<=0||v.wltpKm<=0||v.powerKw<=0||v.consumption<=0)continue;
            if(v.consumption<8||v.consumption>35||v.wltpKm>1000||v.usableBatteryKwh>v.batteryKwh+.5)continue;
            if(keys.add(logicalKey(v)))vehicles.add(v);
        }catch(Exception ignored){}
    }

    private String logicalKey(Vehicle v){return(v.make+"|"+v.model+"|"+v.market+"|"+v.year+"|"+String.format(Locale.US,"%.1f",v.batteryKwh)+"|"+v.version).toLowerCase(Locale.ROOT).trim();}
    private String norm(String s){return java.text.Normalizer.normalize(s==null?"":s,java.text.Normalizer.Form.NFD).replaceAll("\\p{M}","").toLowerCase(Locale.ROOT).trim();}
    private String market(String s){return s==null||s.isEmpty()?"🌐":s;}
    private String specLine(Vehicle v){String bat=v.batteryKwh>0?fmt(v.batteryKwh)+" kWh":"—";String range=v.wltpKm>0?fmt(v.wltpKm)+" km":"—";String p=v.powerKw>0?fmt(v.powerKw)+" kW":"—";return bat+"  ·  "+range+"  ·  "+p;}
    private String fmt(double n){NumberFormat f=NumberFormat.getNumberInstance(Locale.forLanguageTag(LanguageManager.getEffectiveLanguage(this)));f.setMaximumFractionDigits(1);return f.format(n);}

    private void openDetail(Vehicle v){
        Intent i=new Intent(this,CarDetailActivity.class);
        i.putExtra("vehicle_id",v.id);
        i.putExtra("logical_key",logicalKey(v));
        i.putExtra("make",v.make);i.putExtra("model",v.model);i.putExtra("version",v.version);i.putExtra("market",v.market);i.putExtra("year",String.valueOf(v.year));
        i.putExtra("price",v.price);i.putExtra("batteryKwh",v.batteryKwh);i.putExtra("usableBatteryKwh",v.usableBatteryKwh);i.putExtra("batteryType",v.batteryType);i.putExtra("wltpKm",v.wltpKm);i.putExtra("consumption",v.consumption);i.putExtra("powerKw",v.powerKw);i.putExtra("drivetrain",v.drivetrain);i.putExtra("acKw",v.acKw);i.putExtra("dcKw",v.dcKw);i.putExtra("chargeMin",v.chargeMin);i.putExtra("acc",v.acc);i.putExtra("trunk",v.trunk);i.putExtra("weight",v.weight);i.putExtra("lengthMm",v.lengthMm);i.putExtra("widthMm",v.widthMm);i.putExtra("heightMm",v.heightMm);i.putExtra("remoteSource",v.remoteSource);
        startActivity(i);
    }

    static class Scored{Vehicle v;double score;Scored(Vehicle v,double s){this.v=v;score=s;}}
    static class Vehicle{
        String id,make,model,version,batteryType,drivetrain,market;int year;boolean remoteSource;
        double price,batteryKwh,usableBatteryKwh,wltpKm,consumption,powerKw,acKw,dcKw,chargeMin,acc,trunk,weight,lengthMm,widthMm,heightMm;
        Vehicle(JSONObject o,boolean remote){
            remoteSource=remote;make=o.optString("make",o.optString("brand",""));model=o.optString("model","");version=o.optString("version",o.optString("trim",""));
            batteryType=o.optString("batteryChemistry",o.optString("batteryType",""));drivetrain=o.optString("drivetrain",o.optString("drive",""));market=o.optString("market","ES").toUpperCase(Locale.ROOT);
            year=o.optInt("year",o.optInt("modelYear",0));price=o.optDouble("price",0);batteryKwh=o.optDouble("batteryKwh",o.optDouble("battery_capacity_kwh",0));usableBatteryKwh=o.optDouble("usableBatteryKwh",0);
            wltpKm=o.optDouble("wltpKm",o.optDouble("rangeKm",0));consumption=o.optDouble("consumption",o.optDouble("consumptionKwh100",0));powerKw=o.optDouble("powerKw",o.optDouble("power_kW",0));
            acKw=o.optDouble("acKw",o.optDouble("acChargeKw",0));dcKw=o.optDouble("dcKw",o.optDouble("dcChargeKw",0));chargeMin=o.optDouble("charge10to80Min",o.optDouble("chargeMin",0));
            acc=o.optDouble("acceleration0to100Sec",o.optDouble("acc",0));trunk=o.optDouble("trunkLiters",o.optDouble("trunk",0));weight=o.optDouble("weightKg",o.optDouble("weight",0));
            lengthMm=o.optDouble("lengthMm",o.optDouble("length",0));widthMm=o.optDouble("widthMm",o.optDouble("width",0));heightMm=o.optDouble("heightMm",o.optDouble("height",0));
            id=o.optString("id","");if(id.isEmpty())id="catalog-"+Integer.toHexString(logicalHash());
        }
        private int logicalHash(){return(make+"|"+model+"|"+market+"|"+year+"|"+batteryKwh+"|"+version).toLowerCase(Locale.ROOT).hashCode();}
    }
}
