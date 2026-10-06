package com.evchargecalculator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.LinkedHashSet;

public class CarDetailActivity extends BaseNavigationActivity {
 @Override protected int getBottomNavigationIndex(){return 2;}
 private boolean dark;
 private int blue=Color.rgb(46,107,255);
 private int text(){return dark?Color.rgb(245,248,255):Color.rgb(22,42,63);}
 private int sub(){return dark?Color.rgb(170,183,204):Color.rgb(90,111,137);}
 private int dp(int n){return(int)(n*getResources().getDisplayMetrics().density+.5f);}
 private TextView tv(String s,float z,int c){TextView t=new TextView(this);t.setText(LanguageManager.t(this,s));t.setTextSize(z);t.setTextColor(c);return t;}
 private String empty(String s){return s==null||s.trim().isEmpty()?"—":s.trim();}
 private NumberFormat nf(int max){NumberFormat f=NumberFormat.getNumberInstance(Locale.forLanguageTag(LanguageManager.getEffectiveLanguage(this)));f.setMaximumFractionDigits(max);return f;}
 private String num(double n){return nf(1).format(n);} private String num2(double n){return nf(2).format(n);}
 private String integer(double n){return NumberFormat.getIntegerInstance(Locale.forLanguageTag(LanguageManager.getEffectiveLanguage(this))).format(Math.round(n));}
 private String price(double n){if(n<=0)return"—";return integer(n)+" "+getSharedPreferences("ev_charge_calculator",0).getString("app_currency","EUR");}
 @Override protected void onCreate(Bundle b){super.onCreate(b);LanguageManager.applyStored(this);dark=isDarkTheme();build();EdgeToEdgeHelper.apply(this,dark);}
 private void build(){
  Intent i=getIntent(); LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(dark?Color.rgb(7,19,28):Color.rgb(241,246,251));
  ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setVerticalScrollBarEnabled(false);LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(dp(14),dp(18),dp(14),dp(18));
  TextView back=tv("‹  Volver a coches",14,blue);back.setTypeface(null,Typeface.BOLD);back.setPadding(dp(4),0,0,dp(10));back.setOnClickListener(v->finish());c.addView(back,new LinearLayout.LayoutParams(-1,dp(36)));
  TextView title=tv(i.getStringExtra("make")+" "+i.getStringExtra("model"),24,text());title.setTypeface(null,Typeface.BOLD);c.addView(title,new LinearLayout.LayoutParams(-1,dp(38)));
  TextView ver=tv(empty(i.getStringExtra("version")),14,sub());ver.setPadding(0,dp(2),0,dp(8));c.addView(ver,new LinearLayout.LayoutParams(-1,-2));
  c.addView(tv(i.getStringExtra("year")+" · "+i.getStringExtra("market"),12,sub()),new LinearLayout.LayoutParams(-1,dp(28)));
  Button add=new Button(this);add.setText(LanguageManager.t(this,"Añadir a comparativa"));add.setTextColor(Color.WHITE);add.setBackgroundColor(blue);add.setOnClickListener(v->addToComparison());c.addView(add,new LinearLayout.LayoutParams(-1,dp(48)));
  addSection(c,"Batería y autonomía");addRow(c,"Batería",battery(i));addRow(c,"Tipo batería",empty(i.getStringExtra("batteryType")));addRow(c,"Autonomía WLTP",i.getDoubleExtra("wltpKm",0)>0?integer(i.getDoubleExtra("wltpKm",0))+" km":"—");addRow(c,"Consumo",i.getDoubleExtra("consumption",0)>0?num(i.getDoubleExtra("consumption",0))+" kWh/100 km":"—");
  addSection(c,"Prestaciones");addRow(c,"Potencia",i.getDoubleExtra("powerKw",0)>0?integer(i.getDoubleExtra("powerKw",0)*1.35962)+" CV ("+integer(i.getDoubleExtra("powerKw",0))+" kW)":"—");addRow(c,"Tracción",empty(i.getStringExtra("drivetrain")));addRow(c,"0–100 km/h",i.getDoubleExtra("acc",0)>0?num(i.getDoubleExtra("acc",0))+" s":"—");
  addSection(c,"Carga");addRow(c,"Carga AC",kw(i,"acKw"));addRow(c,"Carga DC",kw(i,"dcKw"));addRow(c,"10–80 %",i.getDoubleExtra("chargeMin",0)>0?integer(i.getDoubleExtra("chargeMin",0))+" min":"—");
  addSection(c,"Practicidad");addRow(c,"Maletero",lit(i,"trunk"," L"));addRow(c,"Peso",lit(i,"weight"," kg"));
  addSection(c,"Dimensiones");addRow(c,"Largo",dim(i,"lengthMm"));addRow(c,"Ancho",dim(i,"widthMm"));addRow(c,"Alto",dim(i,"heightMm"));
  addSection(c,"Precio");addRow(c,"Precio",price(i.getDoubleExtra("price",0)));
  c.addView(tv(i.getBooleanExtra("remoteSource",false)?"Datos añadidos desde catálogo externo validado.":"Datos del catálogo protegido.",12,sub()),new LinearLayout.LayoutParams(-1,dp(44)));
  scroll.addView(c);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);
 }
 private String battery(Intent i){double g=i.getDoubleExtra("batteryKwh",0),u=i.getDoubleExtra("usableBatteryKwh",0);if(g<=0&&u<=0)return"—";return g>0&&u>0&&Math.abs(g-u)>.05?num(g)+" kWh\n"+num(u)+" kWh utilizable":num(g>0?g:u)+" kWh";}
 private String kw(Intent i,String k){double n=i.getDoubleExtra(k,0);return n>0?num(n)+" kW":"—";} private String lit(Intent i,String k,String suffix){double n=i.getDoubleExtra(k,0);return n>0?integer(n)+suffix:"—";} private String dim(Intent i,String k){double n=i.getDoubleExtra(k,0);return n>0?num2(n/1000)+" m":"—";}
 private void addSection(LinearLayout p,String s){TextView t=tv(s,16,blue);t.setTypeface(null,Typeface.BOLD);t.setPadding(dp(4),dp(14),dp(4),dp(6));p.addView(t,new LinearLayout.LayoutParams(-1,dp(38)));}
 private void addRow(LinearLayout p,String l,String v){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.setPadding(dp(8),0,dp(8),0);r.setBackgroundColor(dark?Color.rgb(14,25,36):Color.WHITE);TextView a=tv(l,13,sub()),b=tv(v,13,text());b.setGravity(Gravity.END|Gravity.CENTER_VERTICAL);r.addView(a,new LinearLayout.LayoutParams(0,dp(44),1));r.addView(b,new LinearLayout.LayoutParams(dp(170),dp(44)));p.addView(r,new LinearLayout.LayoutParams(-1,dp(45)));}
 private void addToComparison(){String id=getIntent().getStringExtra("vehicle_id");if(id==null)return;android.content.SharedPreferences p=getSharedPreferences("ev_charge_calculator",0);String logical=getIntent().getStringExtra("logical_key");boolean similarFlow=getIntent().getBooleanExtra("similar_flow",false);java.util.ArrayList<String> ids=new java.util.ArrayList<>();java.util.ArrayList<String> keys=new java.util.ArrayList<>();if(similarFlow){String refId=getIntent().getStringExtra("reference_vehicle_id");String refKey=getIntent().getStringExtra("reference_logical_key");if(refId!=null&&!refId.trim().isEmpty())ids.add(refId.trim());if(id!=null&&!ids.contains(id.trim()))ids.add(id.trim());if(refKey!=null&&!refKey.trim().isEmpty())keys.add(refKey.trim());if(logical!=null&&!logical.trim().isEmpty()&&!keys.contains(logical.trim()))keys.add(logical.trim());}else{String csv=p.getString("compare_vehicle_ids_ordered","");if(!csv.isEmpty())for(String x:csv.split(","))if(!x.trim().isEmpty())ids.add(x.trim());if(!ids.contains(id)&&ids.size()<3)ids.add(id);String oldKeys=p.getString("compare_vehicle_logical_ordered","");if(!oldKeys.isEmpty())for(String x:oldKeys.split("\\Q||\\E"))if(!x.trim().isEmpty())keys.add(x.trim());if(logical!=null&&!logical.isEmpty()&&!keys.contains(logical)&&keys.size()<3)keys.add(logical);}p.edit().putString("compare_vehicle_ids_ordered",android.text.TextUtils.join(",",ids)).putStringSet("compare_vehicle_ids",new LinkedHashSet<>(ids)).putString("compare_vehicle_logical_ordered",android.text.TextUtils.join("||",keys)).apply();Intent back=new Intent(this,CompararCochesActivity.class);back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);startActivity(back);finish();}
}