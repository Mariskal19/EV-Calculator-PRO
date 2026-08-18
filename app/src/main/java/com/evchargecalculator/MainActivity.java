package com.evchargecalculator;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends Activity {
    private final int BG = Color.rgb(10,18,32);
    private final int CARD = Color.rgb(20,31,50);
    private final int CARD2 = Color.rgb(25,38,60);
    private final int BLUE = Color.rgb(91,111,245);
    private final int BLUE2 = Color.rgb(124,140,255);
    private final int GREEN = Color.rgb(100,222,168);
    private final int WHITE = Color.WHITE;
    private final int MUTED = Color.rgb(164,177,202);
    private final int SOFT = Color.rgb(219,226,240);
    private final Locale ES = new Locale("es", "ES");

    private LinearLayout page;
    private SeekBar fromBar, toBar;
    private TextView fromValue, toValue, batteryPercent;
    private TextView energyValue, timeValue, costValue, summary;
    private EditText batteryInput, powerInput, priceInput;

    private int dp(int n) { return (int)(n * getResources().getDisplayMetrics().density + .5f); }

    private TextView tv(String s, float size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private GradientDrawable bg(int color, int radius) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color); g.setCornerRadius(dp(radius));
        return g;
    }

    private GradientDrawable gradient(int top, int bottom, int radius) {
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{top,bottom});
        g.setCornerRadius(dp(radius)); return g;
    }

    private LinearLayout card(int color, int radius) {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(dp(18),dp(18),dp(18),dp(18));
        l.setBackground(bg(color,radius));
        l.setElevation(dp(5));
        return l;
    }

    private void space(LinearLayout l, int h) { l.addView(new View(this), new LinearLayout.LayoutParams(1,dp(h))); }

    private TextView section(String s) {
        TextView t = tv(s,11,MUTED,true); t.setLetterSpacing(.09f); t.setPadding(dp(2),0,0,dp(7)); return t;
    }

    private EditText input(String value) {
        EditText e = new EditText(this);
        e.setText(value); e.setTextSize(17); e.setTextColor(WHITE); e.setHintTextColor(MUTED);
        e.setSingleLine(true); e.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        e.setPadding(dp(14),0,dp(14),0); e.setBackground(bg(CARD2,14));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1,dp(52)); p.setMargins(0,dp(4),0,dp(12)); e.setLayoutParams(p); return e;
    }

    private TextView result(String title, String value, boolean accent) {
        TextView t = tv(title + "\n" + value, 13, accent ? WHITE : SOFT, true);
        t.setGravity(Gravity.CENTER_VERTICAL); t.setPadding(dp(13),dp(10),dp(8),dp(10));
        t.setBackground(bg(accent ? BLUE : CARD2, 17));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0,dp(86),1); p.setMargins(dp(3),0,dp(3),0); t.setLayoutParams(p); return t;
    }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(BG); getWindow().setNavigationBarColor(BG);
        build();
    }

    private void build() {
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true); scroll.setBackgroundColor(BG);
        page = new LinearLayout(this); page.setOrientation(LinearLayout.VERTICAL); page.setPadding(dp(18),dp(22),dp(18),dp(32)); scroll.addView(page); setContentView(scroll);

        LinearLayout header = new LinearLayout(this); header.setGravity(Gravity.CENTER_VERTICAL);
        TextView icon = tv("⚡",28,WHITE,true); icon.setGravity(Gravity.CENTER); icon.setBackground(gradient(BLUE,Color.rgb(69,82,196),18));
        header.addView(icon,new LinearLayout.LayoutParams(dp(54),dp(54)));
        LinearLayout ht = new LinearLayout(this); ht.setOrientation(LinearLayout.VERTICAL); ht.setPadding(dp(14),0,0,0);
        ht.addView(tv("EV Charge",25,WHITE,true)); ht.addView(tv("CALCULATOR",11,BLUE2,true)); header.addView(ht);
        page.addView(header);
        TextView sub = tv("Tu carga eléctrica, calculada al instante",14,MUTED,false); sub.setPadding(dp(68),dp(4),0,dp(20)); page.addView(sub);

        LinearLayout hero = card(CARD,28);
        LinearLayout h1 = new LinearLayout(this); h1.setGravity(Gravity.CENTER_VERTICAL);
        h1.addView(tv("CARGA DEL VEHÍCULO",11,MUTED,true),new LinearLayout.LayoutParams(0,dp(28),1)); h1.addView(tv("●  EN CASA",11,GREEN,true)); hero.addView(h1);
        TextView hint = tv("Selecciona cuánto quieres cargar",13,MUTED,false); hero.addView(hint);
        LinearLayout vals = new LinearLayout(this); vals.setGravity(Gravity.CENTER_VERTICAL);
        fromValue=tv("30%",38,WHITE,true); toValue=tv("80%",38,WHITE,true); fromValue.setGravity(Gravity.CENTER); toValue.setGravity(Gravity.CENTER);
        vals.addView(fromValue,new LinearLayout.LayoutParams(0,dp(70),1)); TextView arrow=tv("→",28,BLUE2,true); arrow.setGravity(Gravity.CENTER); vals.addView(arrow,new LinearLayout.LayoutParams(dp(44),dp(70))); vals.addView(toValue,new LinearLayout.LayoutParams(0,dp(70),1)); hero.addView(vals);

        LinearLayout battery = new LinearLayout(this); battery.setOrientation(LinearLayout.VERTICAL); battery.setPadding(dp(5),dp(5),dp(5),dp(5)); battery.setBackground(bg(Color.rgb(30,44,70),18));
        batteryPercent=tv("50% de batería",12,WHITE,true); batteryPercent.setGravity(Gravity.CENTER); battery.addView(batteryPercent,new LinearLayout.LayoutParams(-1,dp(28))); hero.addView(battery);
        page.addView(hero); space(page,18);

        LinearLayout range = card(CARD,22);
        range.addView(section("RANGO DE CARGA"));
        LinearLayout r1=new LinearLayout(this); r1.setGravity(Gravity.CENTER_VERTICAL); r1.addView(tv("Desde",14,SOFT,true),new LinearLayout.LayoutParams(0,dp(28),1)); TextView f=tv("30%",14,BLUE2,true); r1.addView(f); range.addView(r1);
        fromBar=new SeekBar(this); fromBar.setMax(99); fromBar.setProgress(29); styleSeek(fromBar); range.addView(fromBar,new LinearLayout.LayoutParams(-1,dp(42)));
        LinearLayout r2=new LinearLayout(this); r2.setGravity(Gravity.CENTER_VERTICAL); r2.addView(tv("Hasta",14,SOFT,true),new LinearLayout.LayoutParams(0,dp(28),1)); TextView t=tv("80%",14,BLUE2,true); r2.addView(t); range.addView(r2);
        toBar=new SeekBar(this); toBar.setMax(100); toBar.setProgress(80); styleSeek(toBar); range.addView(toBar,new LinearLayout.LayoutParams(-1,dp(42)));
        page.addView(range); space(page,16);

        LinearLayout result = card(CARD,22); LinearLayout rh=new LinearLayout(this); rh.setGravity(Gravity.CENTER_VERTICAL); rh.addView(tv("Resultado",21,WHITE,true),new LinearLayout.LayoutParams(0,dp(32),1)); rh.addView(tv("● AUTOMÁTICO",10,GREEN,true)); result.addView(rh);
        result.addView(tv("Se actualiza al instante al mover las barras",12,MUTED,false)); space(result,12);
        LinearLayout cards=new LinearLayout(this); cards.setOrientation(LinearLayout.HORIZONTAL);
        energyValue=result("ENERGÍA","— kWh",false); timeValue=result("TIEMPO","—",false); costValue=result("COSTE","— €",true); cards.addView(energyValue); cards.addView(timeValue); cards.addView(costValue); result.addView(cards);
        summary=tv("",12,MUTED,false); summary.setPadding(dp(4),dp(14),dp(4),0); result.addView(summary); page.addView(result); space(page,16);

        LinearLayout settings=card(CARD,22); settings.addView(tv("Parámetros",20,WHITE,true)); settings.addView(tv("Personaliza los datos de tu vehículo y tarifa",12,MUTED,false)); space(settings,10);
        settings.addView(section("CAPACIDAD DE BATERÍA · kWh")); batteryInput=input("80"); settings.addView(batteryInput);
        settings.addView(section("POTENCIA DE CARGA · kW")); powerInput=input("3,45"); settings.addView(powerInput);
        settings.addView(section("PRECIO ELECTRICIDAD · €/kWh")); priceInput=input("0,15"); settings.addView(priceInput); page.addView(settings);

        SeekBar.OnSeekBarChangeListener l=new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar s,int p,boolean u){calculate();} public void onStartTrackingTouch(SeekBar s){} public void onStopTrackingTouch(SeekBar s){}};
        fromBar.setOnSeekBarChangeListener(l); toBar.setOnSeekBarChangeListener(l);
        View.OnFocusChangeListener fl=(v,h)->{if(!h)calculate();}; batteryInput.setOnFocusChangeListener(fl); powerInput.setOnFocusChangeListener(fl); priceInput.setOnFocusChangeListener(fl);
        calculate();
    }

    private void styleSeek(SeekBar s) { s.setProgressTintList(ColorStateList.valueOf(BLUE)); s.setThumbTintList(ColorStateList.valueOf(WHITE)); s.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(51,66,94))); }

    private double num(EditText e,double def){try{return Double.parseDouble(e.getText().toString().trim().replace(',','.'));}catch(Exception x){return def;}}
    private String dec(double n,int d){return String.format(ES,"%."+d+"f",n);}

    private void calculate(){
        if(fromBar==null)return;
        int f=fromBar.getProgress()+1; int t=toBar.getProgress(); if(t<=f){t=Math.min(100,f+1);toBar.setProgress(t);}
        fromValue.setText(f+"%"); toValue.setText(t+"%");
        double cap=num(batteryInput,80), kw=num(powerInput,3.45), eur=num(priceInput,.15); int pct=t-f;
        double kwh=Math.max(0,cap*pct/100.0), hrs=kw>0?kwh/kw:0; int h=(int)hrs; int m=(int)Math.round((hrs-h)*60); if(m>=60){h++;m=0;} double c=kwh*eur;
        batteryPercent.setText(pct+"% de batería");
        energyValue.setText("ENERGÍA\n"+dec(kwh,1)+" kWh"); timeValue.setText("TIEMPO\n"+h+" h "+String.format(ES,"%02d",m)+" min"); costValue.setText("COSTE\n"+dec(c,2)+" €");
        summary.setText("De "+f+"% a "+t+"%  ·  "+dec(kwh,1)+" kWh  ·  "+dec(kw,2)+" kW  ·  "+dec(c,2)+" €");
    }
}
