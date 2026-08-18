package com.evchargecalculator;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends Activity {
 int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
 EditText battery,from,to,power,price; TextView energy,time,cost,summary;
 TextView label(String s){TextView v=new TextView(this);v.setText(s);v.setTextSize(13);v.setTextColor(Color.rgb(90,95,105));v.setPadding(0,dp(8),0,dp(4));return v;}
 EditText input(String s){EditText e=new EditText(this);e.setText(s);e.setTextSize(16);e.setSingleLine();e.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);e.setPadding(dp(14),0,dp(14),0);GradientDrawable g=new GradientDrawable();g.setColor(Color.WHITE);g.setCornerRadius(dp(12));g.setStroke(dp(1),Color.rgb(220,223,228));e.setBackground(g);e.setLayoutParams(new LinearLayout.LayoutParams(-1,dp(52)));return e;}
 TextView card(String title){TextView v=new TextView(this);v.setText(title+"\n—");v.setTextSize(14);v.setTextColor(Color.rgb(55,60,70));v.setGravity(Gravity.CENTER_VERTICAL);v.setPadding(dp(16),dp(10),dp(16),dp(10));GradientDrawable g=new GradientDrawable();g.setColor(Color.WHITE);g.setCornerRadius(dp(16));g.setStroke(dp(1),Color.rgb(232,234,238));v.setBackground(g);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(78),1);p.setMargins(dp(4),0,dp(4),0);v.setLayoutParams(p);return v;}
 double val(EditText e,double d){try{return Double.parseDouble(e.getText().toString().trim().replace(',','.'));}catch(Exception x){return d;}}
 void calc(){double c=val(battery,80),a=val(from,30),b=val(to,80),kw=val(power,3.45),p=val(price,.15);if(c<=0||kw<=0)return;double k=c*Math.max(0,b-a)/100.0,h=k/kw,co=k*p;int ih=(int)h,im=(int)Math.round((h-ih)*60);if(im==60){ih++;im=0;}energy.setText(String.format(Locale.getDefault(),"Energía\n%.1f kWh",k));time.setText(String.format(Locale.getDefault(),"Tiempo\n%d h %02d min",ih,im));cost.setText(String.format(Locale.getDefault(),"Coste\n%.2f €",co));summary.setText(String.format(Locale.getDefault(),"De %.0f%% a %.0f%% necesitas %.1f kWh. A %.2f kW tardarás %d h %02d min y costará %.2f €.",a,b,k,kw,ih,im,co));}
 @Override public void onCreate(Bundle b){super.onCreate(b);LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.VERTICAL);r.setPadding(dp(20),dp(22),dp(20),dp(20));r.setBackgroundColor(Color.rgb(247,248,250));TextView t=new TextView(this);t.setText("EV Charge Calculator");t.setTextSize(28);t.setTypeface(null,1);t.setTextColor(Color.rgb(25,29,35));r.addView(t);TextView s=new TextView(this);s.setText("Calcula automáticamente tiempo, energía y coste de carga");s.setTextSize(14);s.setTextColor(Color.rgb(100,105,115));s.setPadding(0,dp(4),0,dp(14));r.addView(s);LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);
 c.addView(label("Batería del vehículo (kWh)"));battery=input("80");c.addView(battery);c.addView(label("Porcentaje inicial (%)"));from=input("30");c.addView(from);c.addView(label("Porcentaje final (%)"));to=input("80");c.addView(to);c.addView(label("Potencia de carga (kW)"));power=input("3.45");c.addView(power);c.addView(label("Precio electricidad (€/kWh)"));price=input("0.15");c.addView(price);TextView h=new TextView(this);h.setText("Resultado");h.setTextSize(20);h.setTypeface(null,1);h.setTextColor(Color.rgb(25,29,35));h.setPadding(0,dp(22),0,dp(8));c.addView(h);LinearLayout cards=new LinearLayout(this);cards.setOrientation(LinearLayout.HORIZONTAL);energy=card("Energía");time=card("Tiempo");cost=card("Coste");cards.addView(energy);cards.addView(time);cards.addView(cost);c.addView(cards);summary=new TextView(this);summary.setTextSize(14);summary.setTextColor(Color.rgb(75,80,90));summary.setPadding(dp(4),dp(14),dp(4),dp(10));c.addView(summary);r.addView(c,new LinearLayout.LayoutParams(-1,0,1));View.OnFocusChangeListener l=(v,f)->{if(!f)calc();};battery.setOnFocusChangeListener(l);from.setOnFocusChangeListener(l);to.setOnFocusChangeListener(l);power.setOnFocusChangeListener(l);price.setOnFocusChangeListener(l);setContentView(r);calc();}
}
