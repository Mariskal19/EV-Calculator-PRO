package com.evchargecalculator;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.Locale;

public class MainActivity extends Activity {
    LinearLayout root, content; SeekBar fromBar, toBar; TextView fromValue, toValue, energy, duration, cost, summary; EditText battery, power, price;
    int dp(int x){ return (int)(x*getResources().getDisplayMetrics().density+.5f); }
    TextView tv(String s,float size,int color,boolean bold){ TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD); return t; }
    GradientDrawable bg(int color,int radius){ GradientDrawable g=new GradientDrawable(); g.setColor(color); g.setCornerRadius(dp(radius)); return g; }
    EditText field(String value){ EditText e=new EditText(this); e.setText(value); e.setTextSize(16); e.setSingleLine(true); e.setInputType(2|8192); e.setPadding(dp(14),0,dp(14),0); e.setBackground(bg(Color.WHITE,14)); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(52)); p.setMargins(0,dp(5),0,dp(12)); e.setLayoutParams(p); return e; }
    void addTitle(String s){ content.addView(tv(s,13,Color.rgb(92,98,112),true)); }
    TextView card(String title){ TextView t=tv(title+"\n—",14,Color.rgb(45,50,62),false); t.setGravity(Gravity.CENTER_VERTICAL); t.setPadding(dp(14),dp(8),dp(10),dp(8)); t.setBackground(bg(Color.WHITE,18)); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(82),1); p.setMargins(dp(4),0,dp(4),0); t.setLayoutParams(p); return t; }
    @Override public void onCreate(Bundle b){ super.onCreate(b);
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(Color.rgb(247,248,252));
        ScrollView sv=new ScrollView(this); content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(dp(20),dp(20),dp(20),dp(28)); sv.addView(content); root.addView(sv,new LinearLayout.LayoutParams(-1,0,1)); setContentView(root);
        TextView title=tv("EV Charge Calculator",28,Color.rgb(24,28,38),true); content.addView(title); TextView sub=tv("Calcula tu carga de forma rápida y automática",14,Color.rgb(103,109,122),false); sub.setPadding(0,dp(4),0,dp(20)); content.addView(sub);
        LinearLayout hero=new LinearLayout(this); hero.setOrientation(LinearLayout.VERTICAL); hero.setPadding(dp(18),dp(16),dp(18),dp(16)); hero.setBackground(bg(Color.rgb(79,99,232),22)); TextView h=tv("Carga del vehículo",20,Color.WHITE,true); hero.addView(h); TextView hs=tv("Selecciona el porcentaje inicial y final",13,Color.rgb(225,229,255),false); hs.setPadding(0,dp(3),0,dp(12)); hero.addView(hs);
        LinearLayout row=new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL); fromValue=tv("30%",25,Color.WHITE,true); toValue=tv("80%",25,Color.WHITE,true); row.addView(fromValue,new LinearLayout.LayoutParams(0,-2,1)); TextView arrow=tv("→",24,Color.WHITE,true); arrow.setGravity(Gravity.CENTER); row.addView(arrow,new LinearLayout.LayoutParams(dp(42),-2)); toValue.setGravity(Gravity.RIGHT); row.addView(toValue,new LinearLayout.LayoutParams(0,-2,1)); hero.addView(row); content.addView(hero);
        addTitle("PORCENTAJE INICIAL"); fromBar=new SeekBar(this); fromBar.setMax(99); fromBar.setProgress(29); content.addView(fromBar,new LinearLayout.LayoutParams(-1,dp(42)));
        addTitle("PORCENTAJE FINAL"); toBar=new SeekBar(this); toBar.setMax(100); toBar.setProgress(80); content.addView(toBar,new LinearLayout.LayoutParams(-1,dp(42)));
        addTitle("CAPACIDAD DE BATERÍA (kWh)"); battery=field("80"); content.addView(battery);
        addTitle("POTENCIA DE CARGA (kW)"); power=field("3.45"); content.addView(power);
        addTitle("PRECIO ELECTRICIDAD (€/kWh)"); price=field("0.15"); content.addView(price);
        TextView rt=tv("Resultado",21,Color.rgb(24,28,38),true); rt.setPadding(0,dp(8),0,dp(10)); content.addView(rt);
        LinearLayout cards=new LinearLayout(this); cards.setOrientation(LinearLayout.HORIZONTAL); energy=card("Energía necesaria"); duration=card("Tiempo estimado"); cost=card("Coste"); cards.addView(energy); cards.addView(duration); cards.addView(cost); content.addView(cards);
        summary=tv("",14,Color.rgb(75,81,94),false); summary.setPadding(dp(4),dp(14),dp(4),0); content.addView(summary);
        SeekBar.OnSeekBarChangeListener l=new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar s,int p,boolean f){calculate();} public void onStartTrackingTouch(SeekBar s){} public void onStopTrackingTouch(SeekBar s){}}; fromBar.setOnSeekBarChangeListener(l); toBar.setOnSeekBarChangeListener(l);
        View.OnFocusChangeListener fl=(v,has)->{if(!has)calculate();}; battery.setOnFocusChangeListener(fl); power.setOnFocusChangeListener(fl); price.setOnFocusChangeListener(fl); calculate();
    }
    double num(EditText e,double d){try{return Double.parseDouble(e.getText().toString().replace(',','.'));}catch(Exception x){return d;}}
    void calculate(){ int f=fromBar.getProgress()+1; int t=toBar.getProgress(); if(t<=f){t=Math.min(100,f+1);toBar.setProgress(t);} fromValue.setText(f+"%");toValue.setText(t+"%"); double cap=num(battery,80), kw=num(power,3.45), eur=num(price,.15); double kwh=Math.max(0,cap*(t-f)/100.0); double hrs=kw>0?kwh/kw:0; int hh=(int)hrs, mm=(int)Math.round((hrs-hh)*60); if(mm==60){hh++;mm=0;} double c=kwh*eur; energy.setText(String.format(Locale.getDefault(),"Energía necesaria\n%.1f kWh",kwh)); duration.setText(String.format(Locale.getDefault(),"Tiempo estimado\n%d h %02d min",hh,mm)); cost.setText(String.format(Locale.getDefault(),"Coste\n%.2f €",c)); summary.setText(String.format(Locale.getDefault(),"De %d%% a %d%%: %.1f kWh · %.2f kW · %.2f €",f,t,kwh,kw,c)); }
}
