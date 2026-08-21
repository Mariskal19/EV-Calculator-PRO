package com.evchargecalculator;

import android.app.*;import android.os.*;import android.graphics.Rect;import android.graphics.Color;import android.content.*;import android.content.res.ColorStateList;import android.text.*;import android.text.method.*;import android.text.method.DigitsKeyListener;import android.view.*;import android.view.inputmethod.InputMethodManager;import android.widget.*;import android.graphics.drawable.GradientDrawable;import java.text.DecimalFormat;import java.text.DecimalFormatSymbols;import java.util.*;

public class MainActivity extends Activity {
 ScrollView scroll; LinearLayout root; PremiumBackgroundView background; EditText battery,power,price,departure,xguard; SeekBar batS; BatteryRangeView range; Switch xSwitch; TextView timeR,energyR,costR,statusR,statusTimeR,statusIcon,themeButton,rangeSummary,chargeAmount,lossInfo; LinearLayout statusBox; boolean busy,dark=false; SharedPreferences prefs;
 int blue=Color.rgb(27,139,220), green=Color.rgb(50,190,155), white=Color.rgb(25,38,58), secondary=Color.rgb(82,104,130), cardLight=Color.argb(235,255,255,255), cardDark=Color.argb(218,17,25,39);
 @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); dark=(getResources().getConfiguration().uiMode & 0x30)==0x20; prefs=getSharedPreferences("ev_charge_calculator",MODE_PRIVATE); build(); loadPreferences(); applyTheme(); calculate();}
 @Override protected void onPause(){super.onPause();savePreferences();}
 void savePreferences(){if(battery==null||range==null)return; prefs.edit().putString("battery",battery.getText().toString()).putString("power",power.getText().toString()).putString("price",price.getText().toString()).putString("departure",departure.getText().toString()).putString("xguard",xguard.getText().toString()).putInt("current",range.getCurrent()).putInt("target",range.getTarget()).putBoolean("xguard_enabled",xSwitch.isChecked()).apply();}
 void loadPreferences(){if(prefs==null)return; busy=true; battery.setText(prefs.getString("battery",battery.getText().toString())); power.setText(prefs.getString("power",power.getText().toString())); price.setText(prefs.getString("price",price.getText().toString())); departure.setText(prefs.getString("departure",departure.getText().toString())); xguard.setText(prefs.getString("xguard",xguard.getText().toString())); range.setValues(prefs.getInt("current",range.getCurrent()),prefs.getInt("target",range.getTarget())); xSwitch.setChecked(prefs.getBoolean("xguard_enabled",xSwitch.isChecked())); xguard.setEnabled(xSwitch.isChecked()); busy=false;}
 int text(){return dark?Color.rgb(245,248,255):white;} int sub(){return dark?Color.rgb(170,183,204):secondary;}
 TextView tv(String s,int sp,int c){TextView t=new TextView(this);t.setText(s);t.setTextSize(sp);t.setTextColor(c);return t;}
 GradientDrawable bg(int color,float r,int stroke){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(r);if(stroke>0)g.setStroke(1,dark?Color.rgb(38,59,85):Color.rgb(215,225,235));return g;}
 EditText edit(String val){EditText e=new EditText(this);e.setText(val);e.setTextColor(text());e.setTextSize(16);e.setSingleLine();e.setGravity(Gravity.CENTER);e.setBackground(bg(dark?Color.rgb(21,34,51):Color.rgb(246,249,253),12,1));e.setPadding(dp(8),0,dp(8),0);e.setSelectAllOnFocus(true);e.setInputType(android.text.InputType.TYPE_CLASS_NUMBER|android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);e.setKeyListener(DigitsKeyListener.getInstance("0123456789,."));e.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_DONE);e.setOnEditorActionListener((v,action,event)->{if(action==android.view.inputmethod.EditorInfo.IME_ACTION_DONE || (event!=null&&event.getKeyCode()==android.view.KeyEvent.KEYCODE_ENTER)){v.clearFocus();((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(),0);return true;}return false;});e.setOnFocusChangeListener((v,f)->{if(f){Handler h=new Handler(Looper.getMainLooper());Runnable keepVisible=()->{v.requestRectangleOnScreen(new Rect(0,0,v.getWidth(),v.getHeight()),true);int[] loc=new int[2];v.getLocationOnScreen(loc);int[] sloc=new int[2];scroll.getLocationOnScreen(sloc);int bottom=loc[1]+v.getHeight();int visibleBottom=sloc[1]+scroll.getHeight();int delta=bottom-(visibleBottom-dp(32));if(delta>0)scroll.smoothScrollBy(0,delta);};h.postDelayed(keepVisible,180);h.postDelayed(keepVisible,450);h.postDelayed(keepVisible,750);}});return e;}
 LinearLayout card(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);l.setPadding(dp(18),dp(18),dp(18),dp(18));l.setBackground(bg(dark?cardDark:cardLight,22,1));return l;}
 SeekBar seek(int max,int progress){SeekBar s=new SeekBar(this);s.setMax(max);s.setProgress(progress);s.setProgressTintList(ColorStateList.valueOf(blue));s.setThumbTintList(ColorStateList.valueOf(blue));s.setBackgroundTintList(ColorStateList.valueOf(dark?Color.rgb(52,68,90):Color.rgb(220,230,240)));s.setPadding(dp(20),0,dp(20),0);return s;}
 void row(LinearLayout p,String label,EditText e,String unit){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);TextView l=tv(label,14,sub());r.addView(l,new LinearLayout.LayoutParams(0,54,1));r.addView(e,new LinearLayout.LayoutParams(dp(88),54));View u=unit.equals("◷")?clock():tv(unit,13,sub()); if(u instanceof TextView){((TextView)u).setGravity(Gravity.CENTER);((TextView)u).setIncludeFontPadding(false);((TextView)u).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);} LinearLayout.LayoutParams clockLp=new LinearLayout.LayoutParams(dp(58),54);clockLp.gravity=Gravity.CENTER_VERTICAL;r.addView(u,clockLp);p.addView(r);}
 int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
 ImageView clock(){ImageView c=new ImageView(this);c.setImageResource(com.evchargecalculator.R.drawable.ic_clock);c.setScaleType(ImageView.ScaleType.CENTER);c.setContentDescription("Hora");c.setPadding(dp(14),dp(14),dp(14),dp(14));c.setColorFilter(sub(),android.graphics.PorterDuff.Mode.SRC_IN);return c;}
 void build(){
  FrameLayout frame=new FrameLayout(this);background=new PremiumBackgroundView(this);frame.addView(background,new FrameLayout.LayoutParams(-1,-1));
  scroll=new ScrollView(this);scroll.setFillViewport(true);root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(18),dp(36),dp(18),dp(68));scroll.addView(root);frame.addView(scroll,new FrameLayout.LayoutParams(-1,-1));setContentView(frame);
  FrameLayout head=new FrameLayout(this);head.setMinimumHeight(dp(68));
  TextView title=tv("EV Charge Calculator",22,text());title.setTypeface(null,1);title.setGravity(Gravity.CENTER);FrameLayout.LayoutParams tp=new FrameLayout.LayoutParams(-1,dp(68));tp.gravity=Gravity.CENTER;tp.leftMargin=dp(44);tp.rightMargin=dp(44);head.addView(title,tp);
  themeButton=tv(dark?"☀":"☾",20,text());themeButton.setGravity(Gravity.CENTER);themeButton.setContentDescription("Cambiar tema");themeButton.setBackground(bg(Color.TRANSPARENT,12,0));themeButton.setOnClickListener(v->toggleTheme());FrameLayout.LayoutParams ip=new FrameLayout.LayoutParams(dp(40),dp(40),Gravity.END|Gravity.CENTER_VERTICAL);head.addView(themeButton,ip);root.addView(head);space(10);
  LinearLayout c1=card();TextView h1=tv("Batería",18,text());h1.setTypeface(null,1);c1.addView(h1);spaceIn(c1,12);battery=edit("80,0");row(c1,"Capacidad",battery,"kWh");batS=seek(300,160);c1.addView(batS,new LinearLayout.LayoutParams(-1,dp(42)));
  rangeSummary=tv("Cargar la batería desde 30% al 80%",15,text());rangeSummary.setGravity(Gravity.CENTER);rangeSummary.setTypeface(null,1);c1.addView(rangeSummary);range=new BatteryRangeView(this);c1.addView(range,new LinearLayout.LayoutParams(-1,dp(62)));
  chargeAmount=tv("Se cargará 50% - 40,0 kWh",14,sub());chargeAmount.setGravity(Gravity.CENTER);c1.addView(chargeAmount);root.addView(c1);space(20);
  LinearLayout c2=card();TextView h2=tv("Carga",18,text());h2.setTypeface(null,1);c2.addView(h2);spaceIn(c2,12);power=edit("3,45");row(c2,"Potencia",power,"kW");spaceIn(c2,8);price=edit("0,15");row(c2,"Precio energía",price,"€/kWh");spaceIn(c2,8);
  LinearLayout xr=new LinearLayout(this);xr.setGravity(Gravity.CENTER_VERTICAL);xr.addView(tv("Centinela / XGuard (consumo / 24 h)",14,sub()),new LinearLayout.LayoutParams(0,54,1));xSwitch=new Switch(this);xSwitch.setChecked(true);xr.addView(xSwitch);xguard=edit("5,0");xguard.setInputType(android.text.InputType.TYPE_CLASS_NUMBER|android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);xguard.setKeyListener(DigitsKeyListener.getInstance("0123456789,."));xguard.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_DONE);xr.addView(xguard,new LinearLayout.LayoutParams(dp(88),54));TextView xp=tv("%",13,sub());xp.setGravity(Gravity.CENTER);xr.addView(xp,new LinearLayout.LayoutParams(dp(58),54));c2.addView(xr);root.addView(c2);space(14);
  LinearLayout c3=card();
  FrameLayout timeContainer=new FrameLayout(this);
  LinearLayout timeHeader=new LinearLayout(this);timeHeader.setGravity(Gravity.CENTER_VERTICAL);
  TextView h3=tv("Tiempo de Carga",18,text());h3.setTypeface(null,1);h3.setGravity(Gravity.CENTER_VERTICAL|Gravity.START);h3.setIncludeFontPadding(false);
  timeHeader.addView(h3,new LinearLayout.LayoutParams(-1,dp(46)));
  timeContainer.addView(timeHeader,new FrameLayout.LayoutParams(-1,dp(46)));
  lossInfo=tv("ⓘ",18,sub());lossInfo.setGravity(Gravity.CENTER);lossInfo.setIncludeFontPadding(false);lossInfo.setContentDescription("Información sobre pérdidas de carga");
  lossInfo.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Pérdidas de carga").setMessage("El cálculo incluye aproximadamente un 10% de pérdidas durante la carga, debidas principalmente a la conversión de energía, calor y otros consumos propios del proceso.").setPositiveButton("Aceptar",null).show());
  FrameLayout.LayoutParams infoLp=new FrameLayout.LayoutParams(dp(30),dp(30),Gravity.TOP|Gravity.END);infoLp.setMargins(0,dp(8),-dp(4),0);
  timeContainer.addView(lossInfo,infoLp);
  timeR=tv("00 h 00 min",28,text());timeR.setTypeface(null,1);timeR.setGravity(Gravity.CENTER);timeR.setSingleLine(true);timeR.setIncludeFontPadding(false);
  timeContainer.addView(timeR,new FrameLayout.LayoutParams(-1,dp(42),Gravity.TOP));
  ((FrameLayout.LayoutParams)timeR.getLayoutParams()).topMargin=dp(32);
  
  c3.addView(timeContainer,new LinearLayout.LayoutParams(-1,dp(92)));
  spaceIn(c3,0);
  LinearLayout costRow=new LinearLayout(this);costRow.setGravity(Gravity.CENTER_VERTICAL);
  LinearLayout.LayoutParams costRowLp=new LinearLayout.LayoutParams(-1,dp(26));costRowLp.topMargin=-dp(6);costRowLp.bottomMargin=dp(1);
  TextView costLabel=tv("Coste de carga",13,sub());costLabel.setGravity(Gravity.CENTER_VERTICAL|Gravity.START);costLabel.setIncludeFontPadding(false);costRow.addView(costLabel,new LinearLayout.LayoutParams(0,dp(26),1));
  costR=tv("0,00 € (0,0 kWh)",15,sub());costR.setGravity(Gravity.CENTER_VERTICAL|Gravity.START);costR.setSingleLine(true);costR.setIncludeFontPadding(false);LinearLayout.LayoutParams costValueLp=new LinearLayout.LayoutParams(0,dp(26),1);costValueLp.setMargins(-dp(14),0,dp(4),0);costRow.addView(costR,costValueLp);c3.addView(costRow,costRowLp);
  energyR=tv("",1,sub());energyR.setVisibility(View.GONE);c3.addView(energyR);root.addView(c3);space(14);
  LinearLayout c4=card();LinearLayout departureHeader=new LinearLayout(this);departureHeader.setGravity(Gravity.CENTER_VERTICAL);TextView h4=tv("Hora de Salida",18,text());h4.setTypeface(null,1);h4.setIncludeFontPadding(false);h4.setGravity(Gravity.CENTER_VERTICAL);departureHeader.addView(h4,new LinearLayout.LayoutParams(0,dp(52),1));departure=edit("07:00");departure.setTypeface(null,1);departure.setTextSize(17);departure.setInputType(android.text.InputType.TYPE_CLASS_DATETIME|android.text.InputType.TYPE_DATETIME_VARIATION_TIME);departure.setKeyListener(null);departure.setCursorVisible(false);departure.setShowSoftInputOnFocus(false);departure.setSelectAllOnFocus(false);departure.setFocusable(false);departure.setClickable(true);departure.setOnClickListener(v->{hideKeyboard(v);pickTime(departure);});departure.setGravity(Gravity.CENTER);departureHeader.addView(departure,new LinearLayout.LayoutParams(dp(104),dp(52)));c4.addView(departureHeader);
  statusBox=new LinearLayout(this);statusBox.setOrientation(LinearLayout.HORIZONTAL);statusBox.setGravity(Gravity.CENTER_VERTICAL);statusBox.setPadding(dp(10),dp(14),dp(10),dp(14));statusBox.setBackground(bg(Color.rgb(27,91,180),20,0));
  statusIcon=tv("🕓",28,Color.WHITE);statusIcon.setGravity(Gravity.CENTER);statusIcon.setIncludeFontPadding(false);statusBox.addView(statusIcon,new LinearLayout.LayoutParams(dp(42),dp(48)));
  statusR=tv("Hora Inicio Recomendada",15,Color.WHITE);statusR.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);statusR.setTypeface(null,1);statusR.setIncludeFontPadding(false);statusBox.addView(statusR,new LinearLayout.LayoutParams(0,dp(48),1));
  statusTimeR=tv("18:00",30,Color.WHITE);statusTimeR.setGravity(Gravity.CENTER);statusTimeR.setTypeface(null,1);statusTimeR.setIncludeFontPadding(false);statusBox.addView(statusTimeR,new LinearLayout.LayoutParams(dp(90),dp(48)));
  spaceIn(c4,10);c4.addView(statusBox,new LinearLayout.LayoutParams(-1,dp(66)));root.addView(c4);space(10);String appVersion="1.0.23";try{appVersion=getPackageManager().getPackageInfo(getPackageName(),0).versionName;}catch(Exception ignored){}TextView foot=tv("Powered by EV Charge Calculator · v"+appVersion,12,sub());foot.setGravity(Gravity.CENTER);root.addView(foot);space(8);setup();
 }
 void space(int n){Space s=new Space(this);root.addView(s,new LinearLayout.LayoutParams(1,dp(n)));} void spaceIn(LinearLayout p,int n){Space s=new Space(this);p.addView(s,new LinearLayout.LayoutParams(1,dp(n)));}
 void applyTheme(){background.setDark(dark);getWindow().setStatusBarColor(dark?Color.rgb(7,11,18):Color.rgb(242,247,252));getWindow().setNavigationBarColor(dark?Color.rgb(7,11,18):Color.rgb(242,247,252));getWindow().getDecorView().setSystemUiVisibility(dark?0:View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);themeButton.setText(dark?"☀":"☾");themeButton.setTextColor(text());themeButton.setBackground(bg(Color.TRANSPARENT,12,0));}
 void toggleTheme(){dark=!dark;rebuildTheme();}
 void rebuildTheme(){String b=battery.getText().toString(),pw=power.getText().toString(),pr=price.getText().toString(),dep=departure.getText().toString(),xg=xguard.getText().toString();int rc=range.getCurrent(),rt=range.getTarget();boolean xs=xSwitch.isChecked();build();battery.setText(b);power.setText(pw);price.setText(pr);departure.setText(dep);xguard.setText(xg);range.setValues(rc,rt);xSwitch.setChecked(xs);xguard.setEnabled(xs);applyTheme();calculate();}
 void ensureVisibleAboveKeyboard(View v){if(scroll==null||v==null)return;v.requestRectangleOnScreen(new Rect(0,0,v.getWidth(),v.getHeight()),true);int[] loc=new int[2];int[] sloc=new int[2];v.getLocationOnScreen(loc);scroll.getLocationOnScreen(sloc);int bottom=loc[1]+v.getHeight();int visibleBottom=sloc[1]+scroll.getHeight();int delta=bottom-(visibleBottom-dp(24));if(delta>0)scroll.smoothScrollBy(0,delta);}
 void hideKeyboard(View v){((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(),0);}
 void pickTime(EditText e){String[] a=e.getText().toString().split(":");int h=7,m=0;try{h=Integer.parseInt(a[0]);m=Integer.parseInt(a[1]);}catch(Exception ignored){}new TimePickerDialog(this,(v,hh,mm)->{setText(e,String.format(Locale.US,"%02d:%02d",hh,mm));calculate();},h,m,true).show();}
 void setup(){batS.setOnSeekBarChangeListener(slider(v->setText(battery,fmt(v/2.0,1))));range.setListener((c,t)->{calculate();savePreferences();});battery.addTextChangedListener(watch(()->{syncBat();savePreferences();}));power.addTextChangedListener(watch(()->{calculate();savePreferences();}));price.addTextChangedListener(watch(()->{calculate();savePreferences();}));xguard.addTextChangedListener(watch(()->{calculate();savePreferences();}));departure.addTextChangedListener(watch(()->{calculate();savePreferences();}));xSwitch.setOnCheckedChangeListener((b,c)->{xguard.setEnabled(c);calculate();savePreferences();});}
 SeekBar.OnSeekBarChangeListener slider(java.util.function.IntConsumer f){return new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar s,int p,boolean from){if(from&&!busy){busy=true;f.accept(p);busy=false;calculate();}}public void onStartTrackingTouch(SeekBar s){}public void onStopTrackingTouch(SeekBar s){}};}
 TextWatcher watch(Runnable r){return new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int c,int d){}public void onTextChanged(CharSequence s,int a,int b,int c){if(!busy)r.run();}public void afterTextChanged(Editable e){}};}
 void setText(EditText e,String s){e.setText(s);e.setSelection(e.length());}
 void syncBat(){if(busy)return;double v=num(battery);if(v>=0&&v<=150){busy=true;batS.setProgress((int)Math.round(v*2));busy=false;}calculate();}
 double num(EditText e){try{return Double.parseDouble(e.getText().toString().replace(',','.'));}catch(Exception x){return 0;}}
 String fmt(double v,int d){DecimalFormat f=new DecimalFormat("0."+"0".repeat(d),DecimalFormatSymbols.getInstance(Locale.US));return f.format(v).replace('.',',');}
 int minutes(String s){try{String[] a=s.trim().split(":");return Integer.parseInt(a[0])*60+Integer.parseInt(a[1]);}catch(Exception e){return -1;}}
 void calculate(){if(battery==null)return;double cap=num(battery),st=range.getCurrent(),tar=range.getTarget(),kw=num(power),eur=num(price),x=num(xguard);double diff=tar-st;rangeSummary.setText("Cargar la batería desde "+((int)st)+"% al "+((int)tar)+"%");chargeAmount.setText("Se cargará "+((int)diff)+"% - "+fmt(cap*diff/100.0,1)+" kWh");if(cap<=0||kw<=0||tar<=st){timeR.setText("00 h 00 min");energyR.setText("0,0 kWh");costR.setText("0,00 € (0,0 kWh)");statusR.setText("");statusTimeR.setText("");return;}int dm=minutes(departure.getText().toString());if(dm<0){statusR.setText("⚠  Hora de salida no válida");statusTimeR.setText("");statusR.setTextColor(Color.WHITE);return;}double base=cap*diff/100.0;double gridEnergyForBattery=base/0.90;double baseMin=gridEnergyForBattery/kw*60.0;double factor=xSwitch.isChecked()?cap*x/(100.0*24.0*kw):0;double mins=baseMin/(1.0-factor);if(factor>=1.0){timeR.setText("No disponible");statusR.setText("No es posible alcanzar el objetivo");statusTimeR.setText("");statusR.setTextColor(Color.WHITE);return;}double extra=xSwitch.isChecked()?cap*x/100.0*(mins/1440.0):0;double energy=gridEnergyForBattery+extra;timeR.setText(String.format(Locale.US,"%02d h %02d min",(int)(mins/60),(int)Math.round(mins%60)));energyR.setText(fmt(energy,1)+" kWh");costR.setText(fmt(energy*eur,2)+" € ("+fmt(energy,1)+" kWh)");Calendar now=Calendar.getInstance();int nowMin=now.get(Calendar.HOUR_OF_DAY)*60+now.get(Calendar.MINUTE);int available=dm-nowMin;if(available<=0)available+=1440;int start=(int)Math.round(dm-mins);start=((start%1440)+1440)%1440;int needed=(int)Math.ceil(mins);boolean onTime=needed<=available;int margin=Math.max(0,available-needed);String tm=String.format(Locale.US,"%02d:%02d",start/60,start%60); if(onTime){
   statusBoxLayout(true,false);
   statusIcon.setText("🕓");statusIcon.setTextSize(30);statusR.setText("Hora de inicio\nRecomendada");statusR.setSingleLine(false);statusR.setMaxLines(2);statusR.setGravity(Gravity.CENTER);statusTimeR.setText(tm);statusTimeR.setTextSize(30);
   statusR.setGravity(Gravity.CENTER);statusTimeR.setGravity(Gravity.CENTER);statusIcon.setVisibility(View.VISIBLE);statusTimeR.setVisibility(View.VISIBLE);
  }else{
   int deficit=needed-available; boolean moreThan24=deficit>1440 || mins>1440; statusBoxLayout(false,moreThan24);
   statusIcon.setText("⚠️");statusIcon.setTextSize(28);
   if(moreThan24){ int fh=deficit/60, fm=deficit%60; statusR.setText("No llegas a tiempo"); statusTimeR.setText("Faltan "+fh+" h "+fm+" min"); statusR.setTextSize(15); statusTimeR.setTextSize(19); }
   else { int fh=deficit/60, fm=deficit%60; statusR.setText("No llegas a tiempo"); statusTimeR.setText("Faltan "+fh+" h "+fm+" min"); statusR.setTextSize(15); statusTimeR.setTextSize(19); }
   statusR.setGravity(Gravity.CENTER);statusTimeR.setGravity(Gravity.CENTER);statusIcon.setVisibility(View.VISIBLE);
  }}
 void detach(View v){
  if(v==null)return;
  ViewParent p=v.getParent();
  if(p instanceof ViewGroup)((ViewGroup)p).removeView(v);
 }
 void statusBoxLayout(boolean ok, boolean moreThan24){
  if(statusBox==null)return;
  statusBox.setOrientation(LinearLayout.HORIZONTAL); statusBox.setGravity(Gravity.CENTER_VERTICAL);
  statusBox.setPadding(dp(10),dp(10),dp(10),dp(10));
  statusBox.setBackground(bg(ok?Color.rgb(27,91,180):Color.rgb(190,63,73),22,0));
  statusBox.removeAllViews(); detach(statusIcon); detach(statusR); detach(statusTimeR);
  statusIcon.setVisibility(View.VISIBLE); statusIcon.setGravity(Gravity.CENTER);
  if(ok){
    statusR.setSingleLine(false); statusR.setMaxLines(2); statusR.setEllipsize(null);
    statusBox.addView(statusIcon,new LinearLayout.LayoutParams(dp(48),dp(56)));
    LinearLayout.LayoutParams titleLp=new LinearLayout.LayoutParams(0,dp(56),1);
    statusBox.addView(statusR,titleLp);
    statusBox.addView(statusTimeR,new LinearLayout.LayoutParams(dp(100),dp(56)));
    statusBox.getLayoutParams().height=dp(78);
  } else if(moreThan24){
    statusBox.addView(statusIcon,new LinearLayout.LayoutParams(dp(48),dp(56)));
    LinearLayout.LayoutParams oneLp=new LinearLayout.LayoutParams(0,dp(56),1);
    statusR.setSingleLine(true); statusR.setGravity(Gravity.CENTER); statusBox.addView(statusR,oneLp);
    statusTimeR.setVisibility(View.GONE); statusBox.getLayoutParams().height=dp(78);
  } else {
    statusBox.addView(statusIcon,new LinearLayout.LayoutParams(dp(48),dp(64)));
    LinearLayout textCol=new LinearLayout(this); textCol.setOrientation(LinearLayout.VERTICAL); textCol.setGravity(Gravity.CENTER);
    statusR.setSingleLine(true); statusR.setGravity(Gravity.CENTER); textCol.addView(statusR,new LinearLayout.LayoutParams(-1,dp(28)));
    statusTimeR.setGravity(Gravity.CENTER); textCol.addView(statusTimeR,new LinearLayout.LayoutParams(-1,dp(34)));
    statusBox.addView(textCol,new LinearLayout.LayoutParams(0,dp(64),1)); statusBox.getLayoutParams().height=dp(84);
  }
  statusBox.requestLayout();
 }
}