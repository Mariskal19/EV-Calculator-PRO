package com.evchargecalculator;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.Gravity;
import android.widget.*;
import android.text.InputType;
import java.util.Locale;

public class MainActivity extends Activity {
    final int BLUE=Color.rgb(38,130,255), CYAN=Color.rgb(75,210,255), BG=Color.rgb(8,12,18), CARD=Color.rgb(18,25,35), CARD2=Color.rgb(25,34,47), TEXT=Color.WHITE, MUTED=Color.rgb(153,166,185), GREEN=Color.rgb(76,220,155), RED=Color.rgb(255,100,100);
    LinearLayout page; SeekBar batteryBar; RangeView chargeRange; TextView batteryValue, currentValue, targetValue, xguardValue, durationValue, startValue, warningValue, summaryValue; EditText departure, xguardPercentField; Switch xguard; double capacity=80, power=3.45, xguardPercent=5.0;
    int d(float v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    GradientDrawable round(int c,int r){GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(d(r));return g;}
    TextView tv(String s,float z,int c,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(z);t.setTextColor(c);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}
    LinearLayout card(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);l.setPadding(d(18),d(16),d(18),d(16));l.setBackground(round(CARD,22));return l;}
    TextView label(String s){TextView t=tv(s,11,MUTED,true);t.setLetterSpacing(.12f);return t;}
    String dec(double n,int digits){return String.format(Locale.US,"%."+digits+"f",n).replace('.',',');}
    double xguardRate(){return Math.max(0.0,xguardPercent)/24.0;}

    @Override public void onCreate(Bundle b){super.onCreate(b); getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);
        ScrollView scroll=new ScrollView(this); page=new LinearLayout(this);page.setOrientation(LinearLayout.VERTICAL);page.setPadding(d(20),d(18),d(20),d(30));page.setBackgroundColor(BG);scroll.addView(page);setContentView(scroll);
        LinearLayout header=new LinearLayout(this);header.setGravity(Gravity.CENTER_VERTICAL); TextView bolt=tv("⚡",28,Color.WHITE,true);bolt.setGravity(Gravity.CENTER);bolt.setBackground(round(BLUE,18));header.addView(bolt,new LinearLayout.LayoutParams(d(52),d(52))); LinearLayout ht=new LinearLayout(this);ht.setPadding(d(14),0,0,0);ht.addView(tv("EV CHARGE",24,TEXT,true));ht.addView(tv("PREMIUM CALCULATOR",10,CYAN,true));header.addView(ht);page.addView(header); TextView intro=tv("Tu carga, calculada de forma inteligente.",14,MUTED,false);intro.setPadding(d(66),d(4),0,d(22));page.addView(intro);

        LinearLayout cap=card();cap.addView(label("CAPACIDAD DE BATERÍA"));batteryValue=tv("80 kWh",30,TEXT,true);batteryValue.setPadding(0,d(5),0,d(5));cap.addView(batteryValue);batteryBar=new SeekBar(this);batteryBar.setMax(100);batteryBar.setProgress(50);cap.addView(batteryBar,new LinearLayout.LayoutParams(-1,d(42)));TextView capHint=tv("Ajusta la capacidad de tu vehículo",12,MUTED,false);cap.addView(capHint);page.addView(cap);space(14);

        LinearLayout charge=card();charge.addView(label("NIVEL DE CARGA"));LinearLayout vals=new LinearLayout(this);vals.setGravity(Gravity.CENTER_VERTICAL);currentValue=tv("30 %",28,TEXT,true);targetValue=tv("80 %",28,TEXT,true);vals.addView(currentValue,new LinearLayout.LayoutParams(0,d(52),1));TextView arrow=tv("→",22,MUTED,true);arrow.setGravity(Gravity.CENTER);vals.addView(arrow,new LinearLayout.LayoutParams(d(42),d(52)));targetValue.setGravity(Gravity.RIGHT);vals.addView(targetValue,new LinearLayout.LayoutParams(0,d(52),1));charge.addView(vals);chargeRange=new RangeView();charge.addView(chargeRange,new LinearLayout.LayoutParams(-1,d(58)));TextView rangeHint=tv("Desliza cualquiera de los dos puntos",12,MUTED,false);charge.addView(rangeHint);page.addView(charge);space(14);

        LinearLayout gx=card();LinearLayout gxHead=new LinearLayout(this);gxHead.setGravity(Gravity.CENTER_VERTICAL);gxHead.addView(label("XGUARD"),new LinearLayout.LayoutParams(0,-2,1));xguard=new Switch(this);xguard.setText("Activado");xguard.setTextColor(TEXT);xguard.setTextSize(13);gxHead.addView(xguard);gx.addView(gxHead);
        LinearLayout xr=new LinearLayout(this);xr.setGravity(Gravity.CENTER_VERTICAL);xr.setPadding(0,d(8),0,0);xr.addView(tv("Consumo cada 24 h",13,MUTED,false),new LinearLayout.LayoutParams(0,d(50),1));xguardPercentField=numberField("5,0");xguardPercentField.setHint("%");xguardPercentField.setTextSize(18);LinearLayout.LayoutParams xp=new LinearLayout.LayoutParams(d(105),d(50));xp.setMargins(d(10),0,0,0);xr.addView(xguardPercentField,xp);gx.addView(xr);
        xguardValue=tv("Desactivado",13,MUTED,false);xguardValue.setPadding(0,d(8),0,0);gx.addView(xguardValue);page.addView(gx);space(14);

        LinearLayout dur=card();dur.addView(label("TIEMPO DE CARGA NECESARIO"));durationValue=tv("—",32,TEXT,true);durationValue.setPadding(0,d(6),0,d(2));dur.addView(durationValue);dur.addView(tv("Calculado automáticamente según batería y potencia.",12,MUTED,false));page.addView(dur);space(14);

        LinearLayout out=card();out.addView(label("HORA DE SALIDA"));departure=new EditText(this);departure.setText("07:00");departure.setTextColor(TEXT);departure.setTextSize(28);departure.setTypeface(Typeface.DEFAULT,Typeface.BOLD);departure.setSingleLine(true);departure.setInputType(InputType.TYPE_CLASS_DATETIME|InputType.TYPE_DATETIME_VARIATION_TIME);departure.setPadding(0,d(4),0,d(4));departure.setBackground(round(CARD2,16));LinearLayout.LayoutParams dp=new LinearLayout.LayoutParams(-1,d(58));dp.setMargins(0,d(8),0,0);out.addView(departure,dp);TextView outHint=tv("Toca para elegir la hora",12,MUTED,false);out.addView(outHint);page.addView(out);space(14);

        LinearLayout powerCard=card();powerCard.addView(label("POTENCIA DE CARGA"));EditText p=numberField("3,45 kW");powerCard.addView(p);page.addView(powerCard);space(16);

        LinearLayout result=card();result.setBackground(round(Color.rgb(13,30,48),24));result.addView(label("RESULTADO"));TextView main=tv("INICIAR CARGA",12,CYAN,true);main.setPadding(0,d(10),0,0);result.addView(main);startValue=tv("—",38,TEXT,true);startValue.setPadding(0,d(2),0,d(4));result.addView(startValue);summaryValue=tv("",13,MUTED,false);result.addView(summaryValue);warningValue=tv("",13,RED,true);warningValue.setPadding(0,d(10),0,0);result.addView(warningValue);page.addView(result);

        batteryBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar s,int p,boolean f){capacity=40+(p/100.0)*80;capacity=Math.round(capacity);batteryValue.setText(dec(capacity,0)+" kWh");calculate();}public void onStartTrackingTouch(SeekBar s){}public void onStopTrackingTouch(SeekBar s){}});
        chargeRange.invalidate();xguard.setOnCheckedChangeListener((v,c)->calculate()); xguardPercentField.setOnFocusChangeListener((v,h)->{if(!h){xguardPercent=parsePercent(xguardPercentField.getText().toString());calculate();}}); xguardPercentField.setOnEditorActionListener((v,a,e)->{xguardPercent=parsePercent(xguardPercentField.getText().toString());calculate();return false;}); departure.setOnClickListener(v->showTimePicker()); p.setOnFocusChangeListener((v,h)->{if(!h){power=parsePower(p.getText().toString());calculate();}}); p.setOnEditorActionListener((v,a,e)->{power=parsePower(p.getText().toString());calculate();return false;}); calculate();
    }
    EditText numberField(String s){EditText e=new EditText(this);e.setText(s);e.setTextColor(TEXT);e.setTextSize(18);e.setSingleLine(true);e.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);e.setPadding(d(14),0,d(14),0);e.setBackground(round(CARD2,16));e.setSelectAllOnFocus(false);e.setLayoutParams(new LinearLayout.LayoutParams(-1,d(52)));return e;}
    double parsePower(String s){try{return Double.parseDouble(s.replace("kW","").trim().replace(',', '.'));}catch(Exception e){return 3.45;}}
    double parsePercent(String s){try{return Math.max(0,Math.min(100,Double.parseDouble(s.replace("%","").trim().replace(',', '.'))));}catch(Exception e){return 5.0;}}
    void showTimePicker(){String[] a=departure.getText().toString().split(":");int h=7,m=0;try{h=Integer.parseInt(a[0]);m=Integer.parseInt(a[1]);}catch(Exception ignored){}new TimePickerDialog(this,(v,hh,mm)->{departure.setText(String.format(Locale.US,"%02d:%02d",hh,mm));calculate();},h,m,true).show();}
    int[] parseTime(){String[] a=departure.getText().toString().trim().split(":");try{return new int[]{Integer.parseInt(a[0]),Integer.parseInt(a[1])};}catch(Exception e){return new int[]{7,0};}}
    void calculate(){int cur=chargeRange.current,target=chargeRange.target;if(target<=cur)target=Math.min(100,cur+1);double kw=power>0?power:3.45;double delta=Math.max(0,target-cur);double baseHours=(capacity*delta/100.0)/kw;double xh=0;int[] tm=parseTime();int departureMin=tm[0]*60+tm[1];double totalHours=baseHours; if(xguard.isChecked()){
            // Minimal-start solution: XGuard acts during the charging window and after it until departure.
            // We solve the total window iteratively, then start at departure minus that window.
            double total=baseHours;
            for(int i=0;i<20;i++){double loss=xguardRate()*total;double required=delta+loss;double charging=(capacity*required/100.0)/kw;total=charging;}
            totalHours=total;
            xh=xguardRate()*totalHours;
        }
        int totalMin=(int)Math.ceil(totalHours*60);int dh=totalMin/60,dm=totalMin%60;durationValue.setText(dh+" h "+String.format(Locale.US,"%02d",dm)+" min");
        int startMin=departureMin-totalMin;boolean crosses=false;if(startMin<0){startMin+=1440;crosses=true;}String start=String.format(Locale.US,"%02d:%02d",startMin/60,startMin%60);startValue.setText(start);
        xguardValue.setText(xguard.isChecked()?"Activado · consumo estimado "+dec(xh,1)+" % de batería":"Desactivado");
        warningValue.setText("");summaryValue.setText("Para llegar al "+target+" % a las "+String.format(Locale.US,"%02d:%02d",tm[0],tm[1])+" necesitas cargar durante "+dh+" h "+String.format(Locale.US,"%02d",dm)+" min.");
        // If required time is over 24 h, or current target is impossible within the same day window, make it explicit.
        if(totalMin>1440){warningValue.setText("⚠ No es posible alcanzar el objetivo en las próximas 24 horas con esta potencia.");}
        if(xguard.isChecked())summaryValue.setText(summaryValue.getText()+" XGuard: −"+dec(xh,1)+" % hasta la salida.");
    }
    void space(int n){Space s=new Space(this);page.addView(s,new LinearLayout.LayoutParams(1,d(n)));}

    class RangeView extends View {
        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);int current=30,target=80;boolean dragCurrent=false,dragTarget=false;float left,right,y;
        RangeView(){super(MainActivity.this);p.setStrokeCap(Paint.Cap.ROUND);setFocusable(true);}
        protected void onDraw(Canvas c){super.onDraw(c);left=d(10);right=getWidth()-d(10);y=getHeight()/2f;p.setStrokeWidth(d(7));p.setColor(Color.rgb(45,58,76));c.drawLine(left,y,right,y,p);float cx=left+(right-left)*current/100f,tx=left+(right-left)*target/100f;p.setColor(BLUE);c.drawLine(cx,y,tx,y,p);p.setColor(Color.WHITE);c.drawCircle(cx,y,d(11),p);c.drawCircle(tx,y,d(11),p);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(d(2));p.setColor(BLUE);c.drawCircle(cx,y,d(11),p);c.drawCircle(tx,y,d(11),p);p.setStyle(Paint.Style.FILL);}
        public boolean onTouchEvent(MotionEvent e){float x=e.getX();if(e.getAction()==MotionEvent.ACTION_DOWN){float cx=left+(right-left)*current/100f,tx=left+(right-left)*target/100f;dragCurrent=Math.abs(x-cx)<Math.abs(x-tx);dragTarget=!dragCurrent;return true;}if(e.getAction()==MotionEvent.ACTION_MOVE||e.getAction()==MotionEvent.ACTION_UP){int v=Math.max(0,Math.min(100,Math.round((x-left)/(right-left)*100)));if(dragCurrent)current=Math.min(v,target-1);else target=Math.max(v,current+1);invalidate();currentValue.setText(current+" %");targetValue.setText(target+" %");calculate();return true;}return true;}
    }
}
