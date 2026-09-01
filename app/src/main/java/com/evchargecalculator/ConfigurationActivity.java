package com.evchargecalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.Locale;

/** Application preferences: language, currency and theme. */
public class ConfigurationActivity extends Activity {
    private static final String PREFS="ev_charge_calculator";
    private static final String KEY_DARK_THEME="dark_theme";
    private static final String KEY_THEME_MODE="theme_mode";
    private static final String KEY_CURRENCY="app_currency";
    private static final String PRIVACY_URL="https://mariskal19.github.io/EV-Calculator-PRO-Privacy/";
    private SharedPreferences prefs;
    private boolean dark;
    private TextView languageValue, currencyValue, themeValue;

    @Override public void onCreate(Bundle b){super.onCreate(b);prefs=getSharedPreferences(PREFS,MODE_PRIVATE);dark=prefs.contains(KEY_DARK_THEME)?prefs.getBoolean(KEY_DARK_THEME,false):(getResources().getConfiguration().uiMode&0x30)==0x20;build();}

    private int dp(int n){return(int)(n*getResources().getDisplayMetrics().density+.5f);}
    private int text(){return dark?Color.rgb(245,248,255):Color.rgb(22,42,63);}
    private int sub(){return dark?Color.rgb(170,183,204):Color.rgb(90,111,137);}
    private GradientDrawable bg(int color,float r,int stroke){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp((int)r));if(stroke>0)g.setStroke(dp(1),dark?Color.rgb(38,59,85):Color.rgb(217,228,241));return g;}
    private TextView tv(String s,float size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);return t;}
    private String lang(){return LanguageManager.getSelectedLanguage(this);}
    private String tr(String es,String en,String fr,String de,String it,String pt){String l=lang();if("es".equals(l))return es;if("fr".equals(l))return fr;if("de".equals(l))return de;if("it".equals(l))return it;if("pt".equals(l))return pt;return en;}

    private void build(){
        FrameLayout frame=new FrameLayout(this);
        PremiumBackgroundView background=new PremiumBackgroundView(this);background.setDark(dark);background.setShowVehicle(false);frame.addView(background,new FrameLayout.LayoutParams(-1,-1));
        ScrollView scroll=new ScrollView(this);LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(0,dp(36),0,dp(16));scroll.addView(root);frame.addView(scroll,new FrameLayout.LayoutParams(-1,-1));setContentView(frame);

        FrameLayout hero=new FrameLayout(this);hero.setLayoutParams(new LinearLayout.LayoutParams(-1,dp(220)));
        View glow=new View(this);GradientDrawable gd=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{dark?Color.rgb(8,34,58):Color.rgb(231,244,255),dark?Color.rgb(15,69,110):Color.rgb(195,229,255),dark?Color.rgb(5,25,42):Color.rgb(242,250,255)});glow.setBackground(gd);hero.addView(glow,new FrameLayout.LayoutParams(-1,-1));
        int headerColor=dark?Color.WHITE:Color.rgb(22,42,63);
        TextView back=tv("←",30,headerColor);back.setGravity(Gravity.CENTER);back.setIncludeFontPadding(false);back.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);back.setShadowLayer(dp(4),0,dp(2),dark?Color.argb(90,0,0,0):Color.argb(55,0,0,0));back.setContentDescription(tr("Volver a EV Calculator PRO Principal","Back to EV Calculator PRO Home","Retour à l'accueil EV Calculator PRO","Zur EV Calculator PRO Startseite","Torna alla schermata principale di EV Calculator PRO","Voltar à página inicial do EV Calculator PRO"));back.setOnClickListener(v->finish());FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(dp(40),dp(40),Gravity.TOP|Gravity.START);bp.leftMargin=dp(14);bp.topMargin=dp(12);hero.addView(back,bp);
        TextView title=tv("⚙  "+tr("Configuración","Settings","Configuration","Einstellungen","Impostazioni","Definições"),22,headerColor);title.setTypeface(null,1);title.setGravity(Gravity.CENTER);title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);title.setShadowLayer(dp(4),0,dp(2),dark?Color.argb(90,0,0,0):Color.argb(55,0,0,0));FrameLayout.LayoutParams tp=new FrameLayout.LayoutParams(-1,dp(48),Gravity.TOP|Gravity.CENTER_HORIZONTAL);tp.leftMargin=dp(48);tp.rightMargin=dp(48);tp.topMargin=dp(12);hero.addView(title,tp);
        root.addView(hero);

        LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(dp(18),dp(18),dp(18),dp(18));card.setBackground(bg(dark?Color.argb(220,21,31,42):Color.argb(245,255,255,255),22,1));LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.setMargins(dp(12),-dp(4),dp(12),dp(14));card.setLayoutParams(cp);
        TextView section=tv(tr("Preferencias de la aplicación","Application preferences","Préférences de l'application","App-Einstellungen","Preferenze dell'applicazione","Preferências da aplicação"),18,text());section.setTypeface(null,1);card.addView(section);space(card,12);
        languageValue=addRow(card,"🌐",tr("Idioma","Language","Langue","Sprache","Lingua","Idioma"),LanguageManager.displayName(lang()),v->chooseLanguage());
        currencyValue=addRow(card,"💰",tr("Moneda","Currency","Devise","Währung","Valuta","Moeda"),currencyName(prefs.getString(KEY_CURRENCY,"EUR")),v->chooseCurrency());
        themeValue=addRow(card,"🎨",tr("Tema","Theme","Thème","Design","Tema","Tema"),themeName(),v->chooseTheme());
        root.addView(card);
        Space bottom=new Space(this);root.addView(bottom,new LinearLayout.LayoutParams(1,0,1));
        TextView privacyLink=tv(tr("Política de privacidad","Privacy Policy","Politique de confidentialité","Datenschutzerklärung","Informativa sulla privacy","Política de privacidade"),13,dark?Color.rgb(105,175,255):Color.rgb(46,107,255));privacyLink.setGravity(Gravity.CENTER);privacyLink.setTypeface(null,1);privacyLink.setPadding(0,dp(4),0,dp(4));privacyLink.setClickable(true);privacyLink.setFocusable(true);privacyLink.setContentDescription(LanguageManager.t(this,"Política de privacidad"));privacyLink.setOnClickListener(v->{Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(PRIVACY_URL));startActivity(intent);});root.addView(privacyLink,new LinearLayout.LayoutParams(-1,dp(34)));
        TextView foot=tv("Powered by EV Calculator · v"+version(),12,sub());foot.setGravity(Gravity.CENTER);root.addView(foot,new LinearLayout.LayoutParams(-1,dp(28)));
        getWindow().setStatusBarColor(dark?Color.rgb(7,19,28):Color.rgb(244,248,255));getWindow().setNavigationBarColor(dark?Color.rgb(7,19,28):Color.rgb(244,248,255));getWindow().getDecorView().setSystemUiVisibility(dark?0:View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
    private void space(LinearLayout p,int h){Space s=new Space(this);p.addView(s,new LinearLayout.LayoutParams(1,dp(h)));}
    private TextView addRow(LinearLayout parent,String icon,String label,String value,View.OnClickListener click){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.setPadding(dp(6),dp(8),dp(4),dp(8));r.setBackground(bg(dark?Color.rgb(21,34,51):Color.rgb(246,249,253),16,1));TextView i=tv(icon,22,text());i.setGravity(Gravity.CENTER);r.addView(i,new LinearLayout.LayoutParams(dp(44),dp(62)));LinearLayout texts=new LinearLayout(this);texts.setOrientation(LinearLayout.VERTICAL);texts.setGravity(Gravity.CENTER_VERTICAL);TextView l=tv(label,15,text());l.setTypeface(null,1);texts.addView(l);TextView v=tv(value,13,sub());v.setTag("value");texts.addView(v);r.addView(texts,new LinearLayout.LayoutParams(0,dp(62),1));TextView arrow=tv("›",30,sub());arrow.setGravity(Gravity.CENTER);r.addView(arrow,new LinearLayout.LayoutParams(dp(36),dp(62)));r.setOnClickListener(click);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(78));lp.setMargins(0,0,0,dp(10));parent.addView(r,lp);return v;}
    private void chooseLanguage(){String[] codes={"en","es","fr","de","it","pt"};String[] names={"🇬🇧  English","🇪🇸  Español","🇫🇷  Français","🇩🇪  Deutsch","🇮🇹  Italiano","🇵🇹  Português"};int checked=0;String cur=lang();for(int i=0;i<codes.length;i++)if(codes[i].equals(cur))checked=i;new AlertDialog.Builder(this).setTitle(tr("Idioma","Language","Langue","Sprache","Lingua","Idioma")).setSingleChoiceItems(names,checked,(d,w)->{LanguageManager.setLanguage(this,codes[w]);d.dismiss();recreate();}).show();}
    private void chooseCurrency(){String[] codes={"EUR","USD","GBP","CHF","CAD","AUD"};String[] names={"€  EUR — Euro","$  USD — Dólar estadounidense","£  GBP — Libra esterlina","CHF — Franco suizo","CA$  CAD — Dólar canadiense","A$  AUD — Dólar australiano"};String cur=prefs.getString(KEY_CURRENCY,"EUR");int checked=0;for(int i=0;i<codes.length;i++)if(codes[i].equals(cur))checked=i;new AlertDialog.Builder(this).setTitle(tr("Moneda","Currency","Devise","Währung","Valuta","Moeda")).setSingleChoiceItems(names,checked,(d,w)->{prefs.edit().putString(KEY_CURRENCY,codes[w]).apply();d.dismiss();currencyValue.setText(currencyName(codes[w]));}).show();}
    private void chooseTheme(){String[] values={"system","light","dark"};String[] names={tr("Automático","Automatic","Automatique","Automatisch","Automatico","Automático"),tr("Claro","Light","Clair","Hell","Chiaro","Claro"),tr("Oscuro","Dark","Sombre","Dunkel","Scuro","Escuro")};String cur=prefs.getString(KEY_THEME_MODE,"system");int checked=0;for(int i=0;i<values.length;i++)if(values[i].equals(cur))checked=i;new AlertDialog.Builder(this).setTitle(tr("Tema","Theme","Thème","Design","Tema","Tema")).setSingleChoiceItems(names,checked,(d,w)->{setTheme(values[w]);d.dismiss();}).show();}
    private void setTheme(String mode){if("system".equals(mode)){prefs.edit().putString(KEY_THEME_MODE,"system").remove(KEY_DARK_THEME).apply();dark=(getResources().getConfiguration().uiMode&0x30)==0x20;}else{dark="dark".equals(mode);prefs.edit().putString(KEY_THEME_MODE,mode).putBoolean(KEY_DARK_THEME,dark).apply();}recreate();}
    private String themeName(){String m=prefs.getString(KEY_THEME_MODE,prefs.contains(KEY_DARK_THEME)?(dark?"dark":"light"):"system");return "dark".equals(m)?tr("Oscuro","Dark","Sombre","Dunkel","Scuro","Escuro"):"light".equals(m)?tr("Claro","Light","Clair","Hell","Chiaro","Claro"):tr("Automático","Automatic","Automatique","Automatisch","Automatico","Automático");}
    private String currencyName(String c){if("USD".equals(c))return "$  USD";if("GBP".equals(c))return "£  GBP";if("CHF".equals(c))return "CHF";if("CAD".equals(c))return "CA$  CAD";if("AUD".equals(c))return "A$  AUD";return "€  EUR";}
    private String version(){try{return getPackageManager().getPackageInfo(getPackageName(),0).versionName;}catch(Exception e){return "1.0.3.1";}}
}
