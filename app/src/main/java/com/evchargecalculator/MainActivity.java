package com.evchargecalculator;

import android.app.*;import android.os.*;import android.graphics.Color;import android.graphics.Typeface;import android.graphics.drawable.GradientDrawable;import android.view.*;import android.widget.*;import android.text.InputType;import java.util.Locale;

public class MainActivity extends Activity {
 LinearLayout root; SeekBar fromBar,toBar; TextView fromVal,toVal,energy,time,cost,rangeText; EditText battery,power,price;
 int blue=Color.rgb(36,107,254), dark=Color.rgb(24,29,38), muted=Color.rgb(103,111,124);
 int dp(int x){return (int)(x*getResources().getDisplayMetrics().density+.5f);}
 TextView tv(String s,float size,int c,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(c);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}
 GradientDrawable bg(int color,int radius){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(radius));return g;}
 LinearLayout box(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);l.setPadding(dp(16),dp(14),dp(16),dp(14));l.setBackground(bg(Color.WHITE,18));return l;}
 EditText field(String value){EditText e=new EditText(this);e.setText(value);e.setTextSize(16);e.setTextColor(dark);e.setSingleLine();e.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);e.setPadding(dp(12),0,dp(12),0);e.setBackground(bg(Color.rgb(247,248,250),12));e.setLayoutParams(new LinearLayout.LayoutParams(-1,dp(50)));return e;}
 TextView unitLabel(String s){TextView t=tv(s,13,muted,false);t.setPadding(0,dp(6),0,dp(6));return t;}
 void addGap(LinearLayout l,int h){Space s=new Space(this);l.addView(s,new LinearLayout.LayoutParams(1,dp(h)));}
 @Override public void onCreate(Bundle b){super.onCreate(b); build(); calc();}
 void build(){
  ScrollView scroll=new ScrollView(this);root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(20),dp(18),dp(20),dp(28));root.setBackgroundColor(Color.rgb(245,247,250));scroll.addView(root);setContentView(scroll);
  TextView title=tv("EV Charge Calculator",28,dark,true);root.addView(title);TextView sub=tv("Calcula tu carga de un vistazo",15,muted,false);sub.setPadding(0,dp(3),0,dp(18));root.addView(sub);
  LinearLayout card=box();root.addView(card);card.addView(tv("¿Cuánto quieres cargar?",18,dark,true));
  LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);fromVal=tv("30%",26,blue,true);toVal=tv("80%",26,blue,true);TextView arrow=tv("  →  ",22,muted,true);row.addView(fromVal,new LinearLayout.LayoutParams(0,dp(50),1));row.addView(arrow);row.addView(toVal,new LinearLayout.LayoutParams(0,dp(50),1));card.addView(row);
  TextView f=tv("Inicio",12,muted,false);f.setGravity(Gravity.CENTER);f.setLayoutParams(new LinearLayout.LayoutParams(0,dp(18),1));
  fromBar=new SeekBar(this);fromBar.setMax(100);fromBar.setProgress(30);toBar=new SeekBar(this);toBar.setMax(100);toBar.setProgress(80);card.addView(fromBar);card.addView(toBar);TextView hint=tv("Mueve las barras para ajustar los porcentajes",12,muted,false);hint.setPadding(0,dp(4),0,0);card.addView(hint);
  addGap(root,14);LinearLayout settings=box();root.addView(settings);settings.addView(tv("Datos de carga",18,dark,true));
  settings.addView(unitLabel("Capacidad de batería (kWh)"));battery=field("80");settings.addView(battery);settings.addView(unitLabel("Potencia de carga (kW)"));power=field("3.45");settings.addView(power);settings.addView(unitLabel("Precio de electricidad (€/kWh)"));price=field("0.15");settings.addView(price);
  addGap(root,14);root.addView(tv("Resultado",20,dark,true));addGap(root,8);
  LinearLayout r1=new LinearLayout(this);r1.setOrientation(LinearLayout.HORIZONTAL);energy=metric("ENERGÍA","0.0 kWh");time=metric("TIEMPO","0 h 00 min");r1.addView(energy,new LinearLayout.LayoutParams(0,dp(105),1));r1.addView(time,new LinearLayout.LayoutParams(0,dp(105),1));root.addView(r1);addGap(root,10);cost=metric("COSTE ESTIMADO","0.00 €");root.addView(cost);addGap(root,10);rangeText=tv("",14,muted,false);rangeText.setGravity(Gravity.CENTER);rangeText.setPadding(dp(10),dp(10),dp(10),dp(10));root.addView(rangeText);
  View.OnFocusChangeListener l=(v,has)->{if(!has)calc();};battery.setOnFocusChangeListener(l);power.setOnFocusChangeListener(l);price.setOnFocusChangeListener(l);
  fromBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar s,int p,boolean u){if(p>=toBar.getProgress())toBar.setProgress(Math.min(100,p+1));calc();}public void onStartTrackingTouch(SeekBar s){}public void onStopTrackingTouch(SeekBar s){}});
  toBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar s,int p,boolean u){if(p<=fromBar.getProgress())fromBar.setProgress(Math.max(0,p-1));calc();}public void onStartTrackingTouch(SeekBar s){}public void onStopTrackingTouch(SeekBar s){}});
 }
 TextView metric(String label,String value){LinearLayout l=box();l.setGravity(Gravity.CENTER_VERTICAL);TextView a=tv(label,11,muted,true);TextView v=tv(value,23,dark,true);v.setPadding(0,dp(5),0,0);l.addView(a);l.addView(v);return v;}
 double num(EditText e,double d){try{return Double.parseDouble(e.getText().toString().replace(',','.'));}catch(Exception x){return d;}}
 void calc(){if(fromBar==null)return;int a=fromBar.getProgress(),z=toBar.getProgress();fromVal.setText(a+"%");toVal.setText(z+"%");double cap=num(battery,80),kw=num(power,3.45),p=num(price,.15);double kwh=cap*(z-a)/100.0;double hours=kw>0?kwh/kw:0;int h=(int)hours;int m=(int)Math.round((hours-h)*60);if(m==60){h++;m=0;}double c=kwh*p;energy.setText(String.format(Locale.getDefault(),"%.1f kWh",kwh));time.setText(String.format(Locale.getDefault(),"%d h %02d min",h,m));cost.setText(String.format(Locale.getDefault(),"%.2f €",c));rangeText.setText(String.format(Locale.getDefault(),"Del %d%% al %d%%  ·  %.1f kWh  ·  %.2f kW",a,z,kwh,kw));}
}
