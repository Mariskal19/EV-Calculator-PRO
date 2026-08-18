package com.evchargecalculator;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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
    private SeekBar fromBar, toBar;
    private TextView fromValue, toValue, energy, duration, cost, summary;
    private EditText battery, power, price;
    private float d;

    private int dp(int n) { return (int)(n * d + 0.5f); }

    private TextView text(String s, float size, int color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(s); v.setTextSize(size); v.setTextColor(color);
        if (bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return v;
    }

    private GradientDrawable rounded(int color, int radius) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color); g.setCornerRadius(dp(radius));
        return g;
    }

    private EditText field(String value) {
        EditText e = new EditText(this);
        e.setText(value); e.setTextSize(16); e.setSingleLine(true);
        e.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        e.setPadding(dp(14), 0, dp(14), 0);
        e.setBackground(rounded(Color.WHITE, 14));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, dp(52));
        p.setMargins(0, dp(5), 0, dp(12)); e.setLayoutParams(p);
        return e;
    }

    private void label(LinearLayout box, String s) {
        TextView v = text(s, 12, Color.rgb(90,96,110), true);
        box.addView(v);
    }

    private TextView resultCard(String title) {
        TextView v = text(title + "\n—", 14, Color.rgb(45,50,62), false);
        v.setGravity(Gravity.CENTER_VERTICAL); v.setPadding(dp(14), dp(8), dp(10), dp(8));
        v.setBackground(rounded(Color.WHITE, 18));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(82), 1);
        p.setMargins(dp(4), 0, dp(4), 0); v.setLayoutParams(p); return v;
    }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        d = getResources().getDisplayMetrics().density;

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(247,248,252));

        ScrollView scroll = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(20), dp(22), dp(20), dp(30));
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);

        content.addView(text("EV Charge Calculator", 28, Color.rgb(24,28,38), true));
        TextView subtitle = text("Calcula tu carga de forma rápida y automática", 14, Color.rgb(103,109,122), false);
        subtitle.setPadding(0, dp(4), 0, dp(18)); content.addView(subtitle);

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL); hero.setPadding(dp(18),dp(17),dp(18),dp(18));
        hero.setBackground(rounded(Color.rgb(79,99,232),22));
        hero.addView(text("Carga del vehículo",20,Color.WHITE,true));
        TextView hs = text("Selecciona el porcentaje inicial y final",13,Color.rgb(225,229,255),false);
        hs.setPadding(0,dp(4),0,dp(12)); hero.addView(hs);
        LinearLayout values = new LinearLayout(this); values.setGravity(Gravity.CENTER_VERTICAL);
        fromValue=text("30%",25,Color.WHITE,true); toValue=text("80%",25,Color.WHITE,true); toValue.setGravity(Gravity.RIGHT);
        values.addView(fromValue,new LinearLayout.LayoutParams(0,-2,1));
        TextView arrow=text("→",24,Color.WHITE,true); arrow.setGravity(Gravity.CENTER);
        values.addView(arrow,new LinearLayout.LayoutParams(dp(42),-2));
        values.addView(toValue,new LinearLayout.LayoutParams(0,-2,1)); hero.addView(values);
        content.addView(hero);

        label(content,"PORCENTAJE INICIAL");
        fromBar=new SeekBar(this); fromBar.setMax(99); fromBar.setProgress(29); content.addView(fromBar,new LinearLayout.LayoutParams(-1,dp(42)));
        label(content,"PORCENTAJE FINAL");
        toBar=new SeekBar(this); toBar.setMax(100); toBar.setProgress(80); content.addView(toBar,new LinearLayout.LayoutParams(-1,dp(42)));

        label(content,"CAPACIDAD DE BATERÍA (kWh)"); battery=field("80"); content.addView(battery);
        label(content,"POTENCIA DE CARGA (kW)"); power=field("3.45"); content.addView(power);
        label(content,"PRECIO ELECTRICIDAD (€/kWh)"); price=field("0.15"); content.addView(price);

        TextView resultTitle=text("Resultado",21,Color.rgb(24,28,38),true);
        resultTitle.setPadding(0,dp(8),0,dp(10)); content.addView(resultTitle);
        LinearLayout cards=new LinearLayout(this); cards.setOrientation(LinearLayout.HORIZONTAL);
        energy=resultCard("Energía necesaria"); duration=resultCard("Tiempo estimado"); cost=resultCard("Coste");
        cards.addView(energy); cards.addView(duration); cards.addView(cost); content.addView(cards);
        summary=text("",14,Color.rgb(75,81,94),false); summary.setPadding(dp(4),dp(14),dp(4),0); content.addView(summary);

        SeekBar.OnSeekBarChangeListener listener=new SeekBar.OnSeekBarChangeListener(){
            public void onProgressChanged(SeekBar s,int p,boolean user){ calculate(); }
            public void onStartTrackingTouch(SeekBar s){}
            public void onStopTrackingTouch(SeekBar s){}
        };
        fromBar.setOnSeekBarChangeListener(listener); toBar.setOnSeekBarChangeListener(listener);
        View.OnFocusChangeListener focus=(v,has)->{if(!has)calculate();};
        battery.setOnFocusChangeListener(focus); power.setOnFocusChangeListener(focus); price.setOnFocusChangeListener(focus);
        calculate();
    }

    private double number(EditText e,double fallback){
        try { return Double.parseDouble(e.getText().toString().trim().replace(',','.')); }
        catch(Exception ex){ return fallback; }
    }

    private void calculate(){
        if(fromBar==null || toBar==null) return;
        int from=fromBar.getProgress()+1;
        int to=toBar.getProgress();
        if(to<=from){ to=Math.min(100,from+1); if(toBar.getProgress()!=to) toBar.setProgress(to); }
        fromValue.setText(from+"%"); toValue.setText(to+"%");
        double cap=Math.max(0,number(battery,80));
        double kw=Math.max(0,number(power,3.45));
        double eur=Math.max(0,number(price,0.15));
        double kwh=cap*(to-from)/100.0;
        double hours=kw>0?kwh/kw:0;
        int h=(int)hours; int m=(int)Math.round((hours-h)*60);
        if(m>=60){h++;m=0;}
        double total=kwh*eur;
        energy.setText(String.format(Locale.getDefault(),"Energía necesaria\n%.1f kWh",kwh));
        duration.setText(String.format(Locale.getDefault(),"Tiempo estimado\n%d h %02d min",h,m));
        cost.setText(String.format(Locale.getDefault(),"Coste\n%.2f €",total));
        summary.setText(String.format(Locale.getDefault(),"De %d%% a %d%% necesitas %.1f kWh · a %.2f kW tardarás %d h %02d min · coste %.2f €.",from,to,kwh,kw,h,m,total));
    }
}
