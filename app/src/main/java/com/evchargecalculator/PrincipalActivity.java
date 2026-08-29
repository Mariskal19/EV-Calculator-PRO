package com.evchargecalculator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class PrincipalActivity extends Activity {
    private boolean dark;
    private final int blue=Color.rgb(46,107,255), white=Color.rgb(22,42,63), secondary=Color.rgb(90,111,137), cardLight=Color.argb(245,255,255,255), cardDark=Color.argb(220,21,31,42), lightBg=Color.rgb(244,248,255), darkBg=Color.rgb(7,19,28);
    private static final String PRIVACY_URL="https://mariskal19.github.io/EV-Calculator-PRO-Privacy/";
    private static final String PREFS="ev_charge_calculator", KEY_DARK_THEME="dark_theme";
    @Override protected void onCreate(Bundle b){super.onCreate(b);SharedPreferences p=getSharedPreferences(PREFS,MODE_PRIVATE);dark=p.contains(KEY_DARK_THEME)?p.getBoolean(KEY_DARK_THEME,false):(getResources().getConfiguration().uiMode&Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES;build();}
    @Override protected void onResume(){super.onResume();SharedPreferences p=getSharedPreferences(PREFS,MODE_PRIVATE);if(p.contains(KEY_DARK_THEME)){boolean d=p.getBoolean(KEY_DARK_THEME,false);if(d!=dark){dark=d;build();}}}
    private void build(){
        FrameLayout frame=new FrameLayout(this);PremiumBackgroundView bgv=new PremiumBackgroundView(this);bgv.setDark(dark);bgv.setShowVehicle(false);frame.addView(bgv,new FrameLayout.LayoutParams(-1,-1));
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(0,dp(36),0,dp(16));scroll.addView(root);frame.addView(scroll,new FrameLayout.LayoutParams(-1,-1));setContentView(frame);
        FrameLayout hero=new FrameLayout(this);hero.setLayoutParams(new LinearLayout.LayoutParams(-1,dp(260)));ImageView header=new ImageView(this);header.setImageResource(R.drawable.cabecera_tema_claro);header.setScaleType(ImageView.ScaleType.CENTER_CROP);header.setTranslationY(-dp(10));hero.addView(header,new FrameLayout.LayoutParams(-1,-1));View fade=new View(this);fade.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{Color.argb(200,0,0,0),Color.argb(80,0,0,0),Color.argb(20,0,0,0),Color.argb(0,0,0,0)}));hero.addView(fade,new FrameLayout.LayoutParams(-1,dp(170),Gravity.TOP));TextView title=tv("EV Calculator PRO",22,Color.WHITE);title.setTypeface(null,1);title.setGravity(Gravity.CENTER);title.setIncludeFontPadding(false);title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);title.setShadowLayer(dp(4),0,dp(2),Color.argb(90,0,0,0));FrameLayout.LayoutParams tp=new FrameLayout.LayoutParams(-1,dp(48));tp.leftMargin=dp(40);tp.rightMargin=dp(40);tp.topMargin=dp(12);hero.addView(title,tp);
        TextView menuButton=tv("⋮",25,Color.WHITE);menuButton.setGravity(Gravity.CENTER);menuButton.setIncludeFontPadding(false);menuButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);menuButton.setContentDescription("Más opciones");menuButton.setShadowLayer(dp(4),0,dp(2),Color.argb(90,0,0,0));menuButton.setBackground(bg(Color.argb(140,12,22,33),12));menuButton.setOnClickListener(v->AppMenuHelper.show(this,v,new AppMenuHelper.Listener(){public boolean isDark(){return dark;}public void setDark(boolean d){dark=d;getSharedPreferences(PREFS,MODE_PRIVATE).edit().putBoolean(KEY_DARK_THEME,d).apply();build();}}));FrameLayout.LayoutParams mp=new FrameLayout.LayoutParams(dp(40),dp(40),Gravity.TOP|Gravity.END);mp.rightMargin=dp(14);mp.topMargin=dp(16);hero.addView(menuButton,mp);root.addView(hero);
        LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(dp(18),dp(18),dp(18),dp(18));card.setBackground(bg(dark?cardDark:cardLight,22));LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.setMargins(dp(12),-dp(26),dp(12),0);card.setLayoutParams(cp);TextView heading=tv("Herramientas",18,textColor());heading.setTypeface(null,1);card.addView(heading);TextView description=tv("Calcula y planifica la carga de tu vehículo eléctrico.",14,subColor());description.setPadding(0,dp(10),0,dp(14));card.addView(description);TextView calculator=tv("⚡  EV Charge Calculator",17,Color.WHITE);calculator.setGravity(Gravity.CENTER_VERTICAL);calculator.setTypeface(null,1);calculator.setPadding(dp(18),0,dp(18),0);calculator.setBackground(bg(blue,18));calculator.setOnClickListener(v->startActivity(new Intent(this,PersistentMainActivity.class)));card.addView(calculator,new LinearLayout.LayoutParams(-1,dp(62)));root.addView(card);
        TextView privacy=tv("Política de privacidad",13,dark?Color.rgb(105,175,255):blue);privacy.setGravity(Gravity.CENTER);privacy.setTypeface(null,1);privacy.setPadding(0,dp(4),0,dp(4));privacy.setOnClickListener(v->startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(PRIVACY_URL))));root.addView(privacy,new LinearLayout.LayoutParams(-1,dp(34)));String ver="1.0.2";try{ver=getPackageManager().getPackageInfo(getPackageName(),0).versionName;}catch(Exception ignored){}if(ver.startsWith("v")||ver.startsWith("V"))ver=ver.substring(1);TextView foot=tv("Powered by EV Calculator · v"+ver,12,subColor());foot.setGravity(Gravity.CENTER);root.addView(foot,new LinearLayout.LayoutParams(-1,dp(28)));getWindow().setStatusBarColor(dark?darkBg:lightBg);getWindow().setNavigationBarColor(dark?darkBg:lightBg);getWindow().getDecorView().setSystemUiVisibility(dark?0:View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
    private TextView tv(String s,int sp,int c){TextView t=new TextView(this);t.setText(s);t.setTextSize(sp);t.setTextColor(c);return t;}private int textColor(){return dark?Color.rgb(245,248,255):white;}private int subColor(){return dark?Color.rgb(170,183,204):secondary;}private GradientDrawable bg(int c,float r){GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(r);return g;}private int dp(int n){return(int)(n*getResources().getDisplayMetrics().density+.5f);}
}
