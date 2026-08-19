package com.evchargecalculator;

import android.app.*;
import android.os.*;
import android.graphics.Color;
import android.content.*;
import android.content.res.Configuration;
import android.content.res.ColorStateList;
import android.text.*;
import android.view.*;
import android.widget.*;
import android.graphics.drawable.GradientDrawable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class MainActivity extends Activity {
    ScrollView scroll; LinearLayout root;
    EditText battery,startSoc,targetSoc,power,price,startTime,departure,xguard;
    SeekBar batS,startS,targetS,powS,priceS; Switch xSwitch;
    TextView timeR,energyR,costR,statusR;
    boolean busy;
    int blue, white, secondary, cardColor, fieldColor, trackColor, backgroundText;
    boolean dark;
    static final String PREFS="evcharge_prefs", THEME="theme";
    // 0 = automatic, 1 = light, 2 = dark
    int themeMode;

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        themeMode=getSharedPreferences(PREFS,0).getInt(THEME,0);
        applyThemeColors();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        build();
        loadValues();
        calculate();
    }

    void applyThemeColors(){
        boolean systemDark=(getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES;
        dark=themeMode==2 || (themeMode==0 && systemDark);
        if(dark){
            blue=Color.rgb(54,184,255); white=Color.rgb(245,248,255); secondary=Color.rgb(170,183,204);
            cardColor=Color.argb(225,17,25,39); fieldColor=Color.rgb(21,34,51); trackColor=Color.rgb(52,68,90); backgroundText=Color.rgb(102,118,141);
            getWindow().setStatusBarColor(Color.rgb(7,11,18)); getWindow().setNavigationBarColor(Color.rgb(7,11,18));
            getWindow().getDecorView().setSystemUiVisibility(0);
        }else{
            blue=Color.rgb(18,130,205); white=Color.rgb(25,34,48); secondary=Color.rgb(82,101,124);
            cardColor=Color.argb(235,255,255,255); fieldColor=Color.rgb(244,248,252); trackColor=Color.rgb(190,204,219); backgroundText=Color.rgb(100,116,138);
            getWindow().setStatusBarColor(Color.rgb(235,242,248)); getWindow().setNavigationBarColor(Color.rgb(235,242,248));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    TextView tv(String s,int sp,int c){ TextView t=new TextView(this); t.setText(s); t.setTextSize(sp); t.setTextColor(c); return t; }

    GradientDrawable bg(int color,float r,int stroke){
        GradientDrawable g=new GradientDrawable(); g.setColor(color); g.setCornerRadius(r);
        if(stroke>0) g.setStroke(1,dark?Color.rgb(38,59,85):Color.rgb(210,222,234)); return g;
    }

    EditText edit(String val){
        EditText e=new EditText(this); e.setText(val); e.setTextColor(white); e.setHintTextColor(secondary); e.setTextSize(18);
        e.setSingleLine(); e.setGravity(Gravity.CENTER); e.setBackground(bg(fieldColor,12,1)); e.setPadding(10,0,10,0);
        e.setSelectAllOnFocus(true);
        e.setOnFocusChangeListener((v,f)->{ if(f)new Handler().postDelayed(()->scroll.smoothScrollTo(0,Math.max(0,v.getBottom()-scroll.getHeight()+80)),180);});
        return e;
    }

    LinearLayout card(){
        LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); l.setPadding(18,16,18,16);
        l.setBackground(bg(cardColor,22,1)); return l;
    }

    SeekBar seek(int max,int progress){
        SeekBar s=new SeekBar(this); s.setMax(max); s.setProgress(progress);
        s.setProgressTintList(ColorStateList.valueOf(blue)); s.setThumbTintList(ColorStateList.valueOf(blue));
        s.setBackgroundTintList(ColorStateList.valueOf(trackColor)); s.setPadding(4,0,4,0); return s;
    }

    void row(LinearLayout p,String label,EditText e,String unit){
        LinearLayout r=new LinearLayout(this); r.setGravity(Gravity.CENTER_VERTICAL);
        TextView l=tv(label,14,secondary); r.addView(l,new LinearLayout.LayoutParams(0,48,1));
        r.addView(e,new LinearLayout.LayoutParams(dp(88),48));
        TextView u=tv(unit,13,secondary); u.setGravity(Gravity.CENTER); r.addView(u,new LinearLayout.LayoutParams(dp(58),48)); p.addView(r);
    }

    int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}

    void build(){
        FrameLayout frame=new FrameLayout(this);
        PremiumBackgroundView premium=new PremiumBackgroundView(this); premium.setDark(dark);
        frame.addView(premium,new FrameLayout.LayoutParams(-1,-1));

        scroll=new ScrollView(this); scroll.setFillViewport(true);
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(18),dp(14),dp(18),dp(36));
        scroll.addView(root); frame.addView(scroll,new FrameLayout.LayoutParams(-1,-1)); setContentView(frame);

        LinearLayout head=new LinearLayout(this); head.setGravity(Gravity.CENTER_VERTICAL);
        TextView icon=tv("⚡",26,white); head.addView(icon,new LinearLayout.LayoutParams(dp(44),dp(50)));
        LinearLayout titleBox=new LinearLayout(this); titleBox.setOrientation(LinearLayout.VERTICAL);
        TextView title=tv("EV Charge Calculator",22,white); title.setTypeface(null,1); titleBox.addView(title);
        TextView sub=tv("Carga inteligente para tu vehículo eléctrico",11,secondary); titleBox.addView(sub);
        head.addView(titleBox,new LinearLayout.LayoutParams(0,dp(50),1));
        TextView theme=tv(themeMode==0?"AUTO":themeMode==1?"☀ CLARO":"☾ OSCURO",11,blue);
        theme.setGravity(Gravity.CENTER); theme.setTypeface(null,1); theme.setPadding(dp(10),0,dp(10),0);
        theme.setBackground(bg(dark?Color.argb(50,54,184,255):Color.argb(30,18,130,205),16,1));
        theme.setOnClickListener(v->showThemeDialog());
        head.addView(theme,new LinearLayout.LayoutParams(dp(88),dp(40)));
        root.addView(head);

        LinearLayout c1=card(); TextView h1=tv("Batería",18,white); h1.setTypeface(null,1); c1.addView(h1);
        battery=edit("80,0"); row(c1,"Capacidad",battery,"kWh"); batS=seek(200,160); c1.addView(batS,new LinearLayout.LayoutParams(-1,dp(42)));
        c1.addView(tv("Estado de carga inicial",14,secondary));
        LinearLayout sr=new LinearLayout(this); sr.setGravity(Gravity.CENTER_VERTICAL); startS=seek(100,30);
        sr.addView(startS,new LinearLayout.LayoutParams(0,dp(42),1)); startSoc=edit("30");
        sr.addView(startSoc,new LinearLayout.LayoutParams(dp(70),dp(42))); sr.addView(tv("%",13,secondary),new LinearLayout.LayoutParams(dp(28),dp(42))); c1.addView(sr);
        c1.addView(tv("Objetivo de carga",14,secondary)); LinearLayout tr=new LinearLayout(this); tr.setGravity(Gravity.CENTER_VERTICAL); targetS=seek(100,80);
        tr.addView(targetS,new LinearLayout.LayoutParams(0,dp(42),1)); targetSoc=edit("80"); tr.addView(targetSoc,new LinearLayout.LayoutParams(dp(70),dp(42)));
        tr.addView(tv("%",13,secondary),new LinearLayout.LayoutParams(dp(28),dp(42))); c1.addView(tr); root.addView(c1); space();

        LinearLayout c2=card(); TextView h2=tv("Carga",18,white); h2.setTypeface(null,1); c2.addView(h2);
        power=edit("3,45"); row(c2,"Potencia",power,"kW"); powS=seek(400,69); c2.addView(powS,new LinearLayout.LayoutParams(-1,dp(42)));
        price=edit("0,15"); row(c2,"Precio energía",price,"€/kWh"); priceS=seek(100,15); c2.addView(priceS,new LinearLayout.LayoutParams(-1,dp(42))); root.addView(c2); space();

        LinearLayout c3=card(); TextView h3=tv("Planificación",18,white); h3.setTypeface(null,1); c3.addView(h3);
        startTime=edit("21:00"); startTime.setInputType(2); row(c3,"Inicio de carga",startTime,"");
        departure=edit("07:00"); departure.setInputType(2); row(c3,"Salida",departure,"");
        LinearLayout xr=new LinearLayout(this); xr.setGravity(Gravity.CENTER_VERTICAL); TextView xl=tv("XGuard (consumo / 24 h)",14,secondary);
        xr.addView(xl,new LinearLayout.LayoutParams(0,48,1)); xSwitch=new Switch(this); xSwitch.setChecked(true); xr.addView(xSwitch);
        xguard=edit("5,0"); xr.addView(xguard,new LinearLayout.LayoutParams(dp(70),dp(42))); xr.addView(tv("%",13,secondary),new LinearLayout.LayoutParams(dp(28),dp(42))); c3.addView(xr); root.addView(c3); space();

        LinearLayout c4=card(); TextView h4=tv("Resultado",18,white); h4.setTypeface(null,1); c4.addView(h4);
        timeR=tv("00 h 00 min",30,white); timeR.setTypeface(null,1); c4.addView(timeR);
        energyR=tv("0,0 kWh",17,blue); c4.addView(energyR); costR=tv("0,00 €",16,secondary); c4.addView(costR);
        statusR=tv("",15,white); statusR.setTypeface(null,1); c4.addView(statusR); root.addView(c4);
        TextView foot=tv("Cálculo automático · admite coma o punto decimal",12,backgroundText); foot.setGravity(Gravity.CENTER); root.addView(foot);
        setup();
    }

    void showThemeDialog(){
        final String[] items={"Automático (según el teléfono)","Claro","Oscuro"};
        new AlertDialog.Builder(this).setTitle("Tema de la aplicación").setSingleChoiceItems(items,themeMode,(d,w)->{
            themeMode=w; getSharedPreferences(PREFS,0).edit().putInt(THEME,themeMode).apply(); saveValues(); d.dismiss(); recreate();
        }).setNegativeButton("Cancelar",null).show();
    }

    void saveValues(){
        getSharedPreferences(PREFS,0).edit()
            .putString("battery",battery.getText().toString()).putString("startSoc",startSoc.getText().toString())
            .putString("targetSoc",targetSoc.getText().toString()).putString("power",power.getText().toString())
            .putString("price",price.getText().toString()).putString("startTime",startTime.getText().toString())
            .putString("departure",departure.getText().toString()).putString("xguard",xguard.getText().toString())
            .putBoolean("xguardEnabled",xSwitch.isChecked()).apply();
    }

    void loadValues(){
        SharedPreferences p=getSharedPreferences(PREFS,0);
        setText(battery,p.getString("battery","80,0")); setText(startSoc,p.getString("startSoc","30"));
        setText(targetSoc,p.getString("targetSoc","80")); setText(power,p.getString("power","3,45"));
        setText(price,p.getString("price","0,15")); setText(startTime,p.getString("startTime","21:00"));
        setText(departure,p.getString("departure","07:00")); setText(xguard,p.getString("xguard","5,0"));
        xSwitch.setChecked(p.getBoolean("xguardEnabled",true)); xguard.setEnabled(xSwitch.isChecked());
        syncBat(); syncSoc(startSoc,startS); syncSoc(targetSoc,targetS); syncPow(); syncPrice();
    }

    void space(){Space s=new Space(this); root.addView(s,new LinearLayout.LayoutParams(1,dp(12)));}

    void setup(){
        batS.setOnSeekBarChangeListener(slider((v)->setText(battery,fmt(v/2.0,1))));
        startS.setOnSeekBarChangeListener(slider((v)->setText(startSoc,""+v)));
        targetS.setOnSeekBarChangeListener(slider((v)->setText(targetSoc,""+v)));
        powS.setOnSeekBarChangeListener(slider((v)->setText(power,fmt(v/20.0,2))));
        priceS.setOnSeekBarChangeListener(slider((v)->setText(price,fmt(v/100.0,2))));
        battery.addTextChangedListener(watch(()->syncBat())); startSoc.addTextChangedListener(watch(()->syncSoc(startSoc,startS)));
        targetSoc.addTextChangedListener(watch(()->syncSoc(targetSoc,targetS))); power.addTextChangedListener(watch(()->syncPow()));
        price.addTextChangedListener(watch(()->syncPrice())); xguard.addTextChangedListener(watch(()->calculate()));
        startTime.addTextChangedListener(watch(()->calculate())); departure.addTextChangedListener(watch(()->calculate()));
        xSwitch.setOnCheckedChangeListener((b,c)->{xguard.setEnabled(c);calculate();});
    }

    SeekBar.OnSeekBarChangeListener slider(java.util.function.IntConsumer f){
        return new SeekBar.OnSeekBarChangeListener(){
            public void onProgressChanged(SeekBar s,int p,boolean from){if(from&&!busy){busy=true;f.accept(p);busy=false;calculate();}}
            public void onStartTrackingTouch(SeekBar s){} public void onStopTrackingTouch(SeekBar s){}
        };
    }

    TextWatcher watch(Runnable r){return new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int c,int d){} public void onTextChanged(CharSequence s,int a,int b,int c){if(!busy)r.run();} public void afterTextChanged(Editable e){}};}
    void setText(EditText e,String s){e.setText(s);e.setSelection(e.length());}
    void syncBat(){if(busy)return;double v=num(battery);if(v>=20&&v<=120){busy=true;batS.setProgress((int)Math.round(v*2));busy=false;}calculate();}
    void syncSoc(EditText e,SeekBar s){if(busy)return;int v=(int)Math.round(num(e));if(v>=0&&v<=100){busy=true;s.setProgress(v);busy=false;}calculate();}
    void syncPow(){if(busy)return;double v=num(power);if(v>=0&&v<=20){busy=true;powS.setProgress((int)Math.round(v*20));busy=false;}calculate();}
    void syncPrice(){if(busy)return;double v=num(price);if(v>=0&&v<=1){busy=true;priceS.setProgress((int)Math.round(v*100));busy=false;}calculate();}
    double num(EditText e){try{return Double.parseDouble(e.getText().toString().replace(',','.'));}catch(Exception x){return 0;}}
    String fmt(double v,int d){DecimalFormat f=new DecimalFormat("0."+"0".repeat(d),DecimalFormatSymbols.getInstance(Locale.US));return f.format(v).replace('.',',');}
    int minutes(String s){try{String[] a=s.trim().split(":");return Integer.parseInt(a[0])*60+Integer.parseInt(a[1]);}catch(Exception e){return -1;}}

    void calculate(){
        if(battery==null)return;
        double cap=num(battery), st=num(startSoc), tar=num(targetSoc), kw=num(power), eur=num(price), x=num(xguard);
        if(cap<=0||kw<=0||tar<=st){timeR.setText("00 h 00 min");energyR.setText("0,0 kWh");costR.setText("0,00 €");
            statusR.setText(tar<=st?"El objetivo debe ser mayor que el nivel inicial.":""); statusR.setTextColor(Color.rgb(255,107,122)); return;}
        int sm=minutes(startTime.getText().toString()), dm=minutes(departure.getText().toString());
        if(sm<0||dm<0){statusR.setText("Introduce las horas en formato HH:mm.");statusR.setTextColor(Color.rgb(255,107,122));return;}
        int avail=dm-sm;if(avail<=0)avail+=1440;
        double base=cap*(tar-st)/100.0;
        double extra=xSwitch.isChecked()?cap*x/100.0*(avail/1440.0):0;
        double energy=base+extra; double mins=energy/kw*60.0;
        timeR.setText(String.format(Locale.US,"%02d h %02d min",(int)(mins/60),(int)Math.round(mins%60)));
        energyR.setText(fmt(energy,1)+" kWh"); costR.setText(fmt(energy*eur,2)+" €");
        if(mins<=avail+0.5){statusR.setText("✓ Hay tiempo suficiente para la carga.");statusR.setTextColor(Color.rgb(83,224,185));}
        else{statusR.setText("⚠ No hay tiempo suficiente para alcanzar el objetivo antes de la salida.");statusR.setTextColor(Color.rgb(255,107,122));}
    }
}
