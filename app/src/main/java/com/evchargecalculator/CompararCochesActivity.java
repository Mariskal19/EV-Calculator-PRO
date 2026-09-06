package com.evchargecalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class CompararCochesActivity extends Activity {
    private static final String PREFS="ev_charge_calculator";
    private static final String KEY_SELECTED="compare_vehicle_ids";
    private static final String KEY_SELECTED_ORDERED="compare_vehicle_ids_ordered";
    private static final String KEY_CURRENCY="app_currency";
    private static final String KEY_MARKET="compare_market";
    private final int blue=Color.rgb(46,107,255), white=Color.rgb(22,42,63), secondary=Color.rgb(90,111,137);
    private boolean dark; private LinearLayout carsRow,table,summary; private Spinner marketSpinner;
    private final List<Vehicle> vehicles=new ArrayList<>(); private final List<String> selectedIds=new ArrayList<>(); private String selectedMarket="ES";
    @Override protected void onCreate(Bundle b){super.onCreate(b);try{SharedPreferences p=getSharedPreferences(PREFS,MODE_PRIVATE);dark=p.contains("dark_theme")?p.getBoolean("dark_theme",false):(getResources().getConfiguration().uiMode&0x30)==0x20;CurrencyRateManager.refreshIfNeeded(this);loadVehicles();selectedMarket=p.getString(KEY_MARKET,"ES");if(!hasMarket(selectedMarket))selectedMarket=defaultMarket();build();loadSelection();rebuild();}catch(Throwable t){TextView e=tv("Error al abrir Comparar coches\n\n"+t.getClass().getSimpleName(),16,Color.WHITE);e.setGravity(Gravity.CENTER);e.setPadding(dp(24),dp(24),dp(24),dp(24));e.setBackgroundColor(Color.rgb(8,34,58));setContentView(e);}}
    @Override protected void onResume(){super.onResume();CurrencyRateManager.refreshIfNeeded(this);SharedPreferences p=getSharedPreferences(PREFS,MODE_PRIVATE);boolean newDark=p.contains("dark_theme")?p.getBoolean("dark_theme",false):(getResources().getConfiguration().uiMode&0x30)==0x20;if(newDark!=dark){dark=newDark;build();loadSelection();rebuild();}else if(table!=null)rebuild();}
    private int dp(int n){return(int)(n*getResources().getDisplayMetrics().density+.5f);} private int text(){return dark?Color.rgb(245,248,255):white;} private int sub(){return dark?Color.rgb(170,183,204):secondary;} private int rowAlt(){return dark?Color.rgb(14,25,36):Color.rgb(248,251,255);} private int bestBg(){return dark?Color.rgb(13,36,58):Color.rgb(235,243,255);} private GradientDrawable bg(int c,float r){GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(dp((int)r));return g;} private GradientDrawable strokeBg(int fill,int stroke,float r){GradientDrawable g=bg(fill,r);g.setStroke(dp(1),stroke);return g;} private TextView tv(String s,float size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);return t;}
    private void loadVehicles(){vehicles.clear();loadAssetVehicles("vehicles.json",true);loadAssetVehicles("vehicle_variants.json",false);loadAssetVehicles("vehicle_market_additions.json",false);normalizeVehicleList();if(vehicles.isEmpty())throw new IllegalStateException("vehicles array missing");}
    private void loadAssetVehicles(String asset,boolean required){try(InputStream in=getAssets().open(asset);BufferedReader r=new BufferedReader(new InputStreamReader(in))){StringBuilder sb=new StringBuilder();String line;while((line=r.readLine())!=null)sb.append(line);JSONArray a=new JSONObject(sb.toString()).optJSONArray("vehicles");if(a==null)throw new IllegalStateException(asset+": vehicles array missing");for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o!=null)addOrMergeVehicle(new Vehicle(o));}}catch(Exception e){if(required)throw new IllegalStateException("No se ha podido cargar "+asset,e);}}
    private String logicalKey(Vehicle v){return(v.make+"|"+v.model+"|"+v.market+"|"+v.year+"|"+v.version).trim().toLowerCase(Locale.ROOT);}
    private void addOrMergeVehicle(Vehicle incoming){String key=logicalKey(incoming);for(Vehicle existing:vehicles){if(!logicalKey(existing).equals(key))continue;if(existing.price<=0)existing.price=incoming.price;if(existing.batteryKwh<=0)existing.batteryKwh=incoming.batteryKwh;if(existing.usableBatteryKwh<=0)existing.usableBatteryKwh=incoming.usableBatteryKwh;if(existing.batteryType.isEmpty())existing.batteryType=incoming.batteryType;if(existing.wltpKm<=0)existing.wltpKm=incoming.wltpKm;if(existing.consumption<=0)existing.consumption=incoming.consumption;if(existing.powerKw<=0)existing.powerKw=incoming.powerKw;if(existing.drivetrain.isEmpty())existing.drivetrain=incoming.drivetrain;if(existing.acKw<=0)existing.acKw=incoming.acKw;if(existing.dcKw<=0)existing.dcKw=incoming.dcKw;if(existing.chargeMin<=0)existing.chargeMin=incoming.chargeMin;if(existing.acc<=0)existing.acc=incoming.acc;if(existing.trunk<=0)existing.trunk=incoming.trunk;if(existing.weight<=0)existing.weight=incoming.weight;return;}vehicles.add(incoming);}
    private void normalizeVehicleList(){Iterator<Vehicle> it=vehicles.iterator();while(it.hasNext()){Vehicle v=it.next();if(v.version==null||!v.version.contains("/"))continue;boolean allPresent=true;for(String part:v.version.split("/")){boolean found=false;String wanted=part.trim();for(Vehicle other:vehicles)if(other!=v&&other.make.equalsIgnoreCase(v.make)&&other.model.equalsIgnoreCase(v.model)&&other.market.equalsIgnoreCase(v.market)&&other.year==v.year&&other.version.trim().equalsIgnoreCase(wanted)){found=true;break;}if(!found){allPresent=false;break;}}if(allPresent)it.remove();}Collections.sort(vehicles,(a,b)->{int c=a.make.compareToIgnoreCase(b.make);if(c!=0)return c;c=a.model.compareToIgnoreCase(b.model);if(c!=0)return c;c=Integer.compare(b.year,a.year);if(c!=0)return c;return a.version.compareToIgnoreCase(b.version);});}
    private String defaultMarket(){for(Vehicle v:vehicles)if(v.market.equalsIgnoreCase("ES"))return"ES";return vehicles.get(0).market;} private boolean hasMarket(String m){for(Vehicle v:vehicles)if(v.market.equalsIgnoreCase(m))return true;return false;}
    private List<String> markets(){LinkedHashSet<String>s=new LinkedHashSet<>();for(Vehicle v:vehicles)if(v.market!=null&&!v.market.trim().isEmpty())s.add(v.market.toUpperCase(Locale.ROOT));List<String>o=new ArrayList<>(s);Collections.sort(o,(a,b)->marketName(a).compareToIgnoreCase(marketName(b)));return o;}
    private String marketName(String c){if(c==null||c.trim().isEmpty())return"";String code=c.equalsIgnoreCase("UK")?"GB":c.toUpperCase(Locale.ROOT);if(code.matches("[A-Z]{2}")){Locale displayLocale=Locale.forLanguageTag(LanguageManager.getEffectiveLanguage(this));String name=new Locale("",code).getDisplayCountry(displayLocale);if(name!=null&&!name.trim().isEmpty()&&!name.equalsIgnoreCase(code))return name;}return code;}
    private String marketFlag(String c){if("ES".equalsIgnoreCase(c))return"🇪🇸";if("FR".equalsIgnoreCase(c))return"🇫🇷";if("DE".equalsIgnoreCase(c))return"🇩🇪";if("IT".equalsIgnoreCase(c))return"🇮🇹";if("PT".equalsIgnoreCase(c))return"🇵🇹";if("GB".equalsIgnoreCase(c)||"UK".equalsIgnoreCase(c))return"🇬🇧";if(c!=null&&c.matches("[A-Za-z]{2}")){int a=Character.toUpperCase(c.charAt(0))- 'A'+127462;int b=Character.toUpperCase(c.charAt(1))- 'A'+127462;return new String(Character.toChars(a))+new String(Character.toChars(b));}return"🌐";}
    private String marketLabel(String c){return marketFlag(c)+"  "+marketName(c);}
    private void build(){
    LinearLayout root=new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    int statusBarHeight=0;
    int statusBarId=getResources().getIdentifier("status_bar_height","dimen","android");
    if(statusBarId>0)statusBarHeight=getResources().getDimensionPixelSize(statusBarId);
    root.setPadding(0,statusBarHeight,0,0);
    root.setBackgroundColor(dark?Color.rgb(7,19,28):Color.rgb(241,246,251));
    FrameLayout hero=new FrameLayout(this);
    hero.setLayoutParams(new LinearLayout.LayoutParams(-1,dp(156)));
    ImageView heroImage=new ImageView(this);
    heroImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
    heroImage.setImageResource(R.drawable.cabecera_tema_claro);
    hero.addView(heroImage,new FrameLayout.LayoutParams(-1,-1));
    View shade=new View(this);
    shade.setBackgroundColor(Color.argb(dark?145:90,0,18,32));
    hero.addView(shade,new FrameLayout.LayoutParams(-1,-1));
    TextView back=tv("‹",40,Color.WHITE);
    back.setGravity(Gravity.CENTER);
    back.setTypeface(null,Typeface.BOLD);
    back.setShadowLayer(8,0,2,Color.BLACK);
    back.setOnClickListener(v->finish());
    FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(dp(52),dp(58),Gravity.START|Gravity.TOP);
    bp.setMargins(dp(8),dp(10),0,0);
    hero.addView(back,bp);
    TextView menuButton=tv("⋮",30,Color.WHITE);
    menuButton.setGravity(Gravity.CENTER);
    menuButton.setIncludeFontPadding(false);
    menuButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    menuButton.setShadowLayer(8,0,2,Color.BLACK);
    menuButton.setBackgroundColor(Color.TRANSPARENT);
    menuButton.setContentDescription(LanguageManager.t(this,"Menú"));
    menuButton.setOnClickListener(v->AppMenuHelper.show(this,menuButton,new AppMenuHelper.Listener(){
        public boolean isDark(){return dark;}
        public void setDark(boolean value){if(dark!=value){dark=value;getSharedPreferences(PREFS,MODE_PRIVATE).edit().putBoolean("dark_theme",dark).apply();build();loadSelection();rebuild();}}
    }));
    FrameLayout.LayoutParams mbp=new FrameLayout.LayoutParams(dp(44),dp(52),Gravity.END|Gravity.TOP);
    mbp.setMargins(0,dp(10),dp(8),0);
    hero.addView(menuButton,mbp);
    TextView title=tv("Comparar coches",25,Color.WHITE);
    title.setTypeface(null,Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setShadowLayer(8,0,2,Color.BLACK);
    FrameLayout.LayoutParams tp=new FrameLayout.LayoutParams(-1,dp(58),Gravity.CENTER);
    tp.setMargins(dp(50),dp(48),dp(50),0);
    hero.addView(title,tp);
    TextView subtitle=tv("Compara hasta 3 vehículos",14,Color.WHITE);
    subtitle.setGravity(Gravity.CENTER);
    subtitle.setShadowLayer(6,0,2,Color.BLACK);
    FrameLayout.LayoutParams sp=new FrameLayout.LayoutParams(-1,dp(34),Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
    sp.setMargins(dp(24),0,dp(24),dp(12));
    hero.addView(subtitle,sp);
    root.addView(hero);
    ScrollView scroll=new ScrollView(this);
    scroll.setFillViewport(true);
    scroll.setClipToPadding(false);
    LinearLayout content=new LinearLayout(this);
    content.setOrientation(LinearLayout.VERTICAL);
    content.setPadding(dp(14),dp(14),dp(14),dp(28));
    LinearLayout intro=new LinearLayout(this);
    intro.setOrientation(LinearLayout.VERTICAL);
    intro.setPadding(dp(16),dp(14),dp(16),dp(14));
    intro.setBackground(strokeBg(dark?Color.rgb(17,31,44):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(218,228,239),18));
    TextView introTitle=tv("Elige tus vehículos",17,text());
    introTitle.setTypeface(null,Typeface.BOLD);
    intro.addView(introTitle,new LinearLayout.LayoutParams(-1,dp(26)));
    TextView hint=tv("Añade 2 o 3 coches para ver sus diferencias de un vistazo.",13,sub());
    hint.setPadding(0,dp(2),0,0);
    intro.addView(hint,new LinearLayout.LayoutParams(-1,dp(36)));
    content.addView(intro,new LinearLayout.LayoutParams(-1,-2));
    HorizontalScrollView carsScroll=new HorizontalScrollView(this);
    carsScroll.setHorizontalScrollBarEnabled(false);
    carsScroll.setClipToPadding(false);
    carsScroll.setPadding(0,dp(12),0,dp(4));
    carsRow=new LinearLayout(this);
    carsRow.setOrientation(LinearLayout.HORIZONTAL);
    carsRow.setGravity(Gravity.TOP);
    carsScroll.addView(carsRow,new HorizontalScrollView.LayoutParams(-2,-2));
    content.addView(carsScroll,new LinearLayout.LayoutParams(-1,-2));
    TextView section=tv("Comparativa",19,text());
    section.setTypeface(null,Typeface.BOLD);
    section.setPadding(dp(2),dp(12),0,dp(2));
    content.addView(section,new LinearLayout.LayoutParams(-1,dp(42)));
    TextView legend=tv("✦  Mejor valor",12,blue);
    legend.setGravity(Gravity.CENTER_VERTICAL);
    legend.setPadding(dp(4),0,0,dp(4));
    content.addView(legend,new LinearLayout.LayoutParams(-1,dp(28)));
    table=new LinearLayout(this);
    table.setOrientation(LinearLayout.VERTICAL);
    table.setPadding(0,dp(2),0,0);
    HorizontalScrollView tableScroll=new HorizontalScrollView(this);
    tableScroll.setHorizontalScrollBarEnabled(false);
    tableScroll.addView(table,new HorizontalScrollView.LayoutParams(-2,-2));
    content.addView(tableScroll,new LinearLayout.LayoutParams(-1,-2));
    summary=new LinearLayout(this);
    summary.setOrientation(LinearLayout.VERTICAL);
    summary.setPadding(0,dp(18),0,dp(8));
    content.addView(summary,new LinearLayout.LayoutParams(-1,-2));
    scroll.addView(content,new ScrollView.LayoutParams(-1,-2));
    root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
    setContentView(root);
    getWindow().setStatusBarColor(dark?Color.rgb(7,19,28):Color.rgb(241,246,251));
    getWindow().setNavigationBarColor(dark?Color.rgb(7,19,28):Color.rgb(241,246,251));
    getWindow().getDecorView().setSystemUiVisibility(dark?0:View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
}
    private List<Vehicle> marketVehicles(){List<Vehicle>o=new ArrayList<>();for(Vehicle v:vehicles)if(v.market.equalsIgnoreCase(selectedMarket))o.add(v);return o;} private int tableWidth(){return dp(112+145*Math.max(2,selectedIds.size()));}
    private void loadSelection(){selectedIds.clear();SharedPreferences p=getSharedPreferences(PREFS,MODE_PRIVATE);String ordered=p.getString(KEY_SELECTED_ORDERED,"");if(!ordered.trim().isEmpty())for(String id:ordered.split(",")){id=id.trim();Vehicle v=find(id);if(!id.isEmpty()&&v!=null&&!selectedIds.contains(id)&&selectedIds.size()<3)selectedIds.add(id);}if(selectedIds.isEmpty()){Set<String>s=p.getStringSet(KEY_SELECTED,null);if(s!=null)for(String id:s){Vehicle v=find(id);if(v!=null&&!selectedIds.contains(id)&&selectedIds.size()<3)selectedIds.add(id);}}}
    private Vehicle find(String id){for(Vehicle v:vehicles)if(v.id.equals(id))return v;return null;} private void saveSelection(){SharedPreferences.Editor e=getSharedPreferences(PREFS,MODE_PRIVATE).edit();e.putString(KEY_SELECTED_ORDERED,joinSelection());e.putStringSet(KEY_SELECTED,new LinkedHashSet<>(selectedIds));e.apply();} private String joinSelection(){StringBuilder s=new StringBuilder();for(String id:selectedIds){if(s.length()>0)s.append(',');s.append(id);}return s.toString();}
    private void rebuild(){if(carsRow==null||table==null||summary==null)return;carsRow.removeAllViews();table.removeAllViews();summary.removeAllViews();for(String id:selectedIds){Vehicle v=find(id);if(v!=null){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(145),-2);lp.setMargins(dp(3),0,dp(3),0);carsRow.addView(carCard(v),lp);}}if(selectedIds.size()<3){LinearLayout empty=new LinearLayout(this);empty.setOrientation(LinearLayout.VERTICAL);empty.setGravity(Gravity.CENTER);empty.setPadding(dp(8),dp(10),dp(8),dp(10));empty.setBackground(strokeBg(dark?Color.rgb(14,26,38):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(220,229,240),16));TextView plus=tv("＋",28,blue);plus.setGravity(Gravity.CENTER);empty.addView(plus,new LinearLayout.LayoutParams(-1,dp(34)));TextView n=tv("Añadir coche",12,sub());n.setGravity(Gravity.CENTER);empty.addView(n,new LinearLayout.LayoutParams(-1,dp(24)));empty.setOnClickListener(v->showSearch());LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(145),dp(150));lp.setMargins(dp(3),0,dp(3),0);carsRow.addView(empty,lp);}if(selectedIds.size()>=2){addSection("Batería y autonomía");addRow("Batería","battery",false);addRow("Tipo batería","type",false);addRow("Autonomía WLTP","range",true);addRow("Consumo","cons",true);addSection("Prestaciones");addRow("Potencia","power",true);addRow("Tracción","drive",false);addRow("0–100 km/h","acc",true);addSection("Carga");addRow("Carga AC","ac",true);addRow("Carga DC","dc",true);addRow("10–80 %","charge",true);addSection("Practicidad");addRow("Maletero","trunk",true);addRow("Peso","weight",true);addSection("Precio");addRow("Precio","price",true);buildSummary();}else{TextView t=tv("Selecciona al menos 2 coches para mostrar la comparativa.",14,sub());t.setGravity(Gravity.CENTER);t.setPadding(dp(10),dp(18),dp(10),dp(18));table.addView(t,new LinearLayout.LayoutParams(tableWidth(),-2));}}
    private void addSection(String title){TextView s=tv(title,14,blue);s.setTypeface(null,Typeface.BOLD);s.setGravity(Gravity.CENTER_VERTICAL);s.setPadding(dp(4),dp(12),dp(4),dp(6));table.addView(s,new LinearLayout.LayoutParams(tableWidth(),dp(40)));}
    private android.view.View carCard(Vehicle v){
    LinearLayout c=new LinearLayout(this);
    c.setOrientation(LinearLayout.VERTICAL);
    c.setGravity(Gravity.CENTER_HORIZONTAL);
    c.setPadding(dp(9),dp(9),dp(9),dp(8));
    c.setBackground(strokeBg(dark?Color.rgb(18,32,45):Color.WHITE,dark?Color.rgb(49,72,91):Color.rgb(214,225,237),18));
    TextView photo=tv("🚘",34,blue);
    photo.setGravity(Gravity.CENTER);
    photo.setBackground(bg(dark?Color.rgb(12,29,43):Color.rgb(239,245,252),14));
    c.addView(photo,new LinearLayout.LayoutParams(-1,dp(68)));
    TextView make=tv(v.make,12,blue);
    make.setTypeface(null,Typeface.BOLD);
    make.setGravity(Gravity.CENTER);
    make.setPadding(0,dp(8),0,0);
    c.addView(make,new LinearLayout.LayoutParams(-1,dp(28)));
    TextView model=tv(v.model,17,text());
    model.setTypeface(null,Typeface.BOLD);
    model.setGravity(Gravity.CENTER);
    c.addView(model,new LinearLayout.LayoutParams(-1,dp(27)));
    TextView market=tv(marketLabel(v.market),11,sub());
    market.setGravity(Gravity.CENTER);
    c.addView(market,new LinearLayout.LayoutParams(-1,dp(25)));
    TextView ver=tv(v.version,11,sub());
    ver.setGravity(Gravity.CENTER);
    ver.setMaxLines(2);
    c.addView(ver,new LinearLayout.LayoutParams(-1,dp(34)));
    TextView year=tv(v.year>0?String.valueOf(v.year):"",11,sub());
    year.setGravity(Gravity.CENTER);
    c.addView(year,new LinearLayout.LayoutParams(-1,dp(21)));
    TextView rem=tv("✕  Quitar",12,Color.rgb(210,70,70));
    rem.setGravity(Gravity.CENTER);
    rem.setTypeface(null,Typeface.BOLD);
    rem.setPadding(0,dp(5),0,0);
    rem.setOnClickListener(x->remove(v.id));
    c.addView(rem,new LinearLayout.LayoutParams(-1,dp(31)));
    return c;
}
    private void remove(String id){selectedIds.remove(id);saveSelection();rebuild();}
    private void showSearch(){
        final EditText input=new EditText(this);input.setSingleLine(true);input.setHint("Marca, modelo, año o versión");
        final LinearLayout list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);
        LinearLayout marketRow=new LinearLayout(this);marketRow.setOrientation(LinearLayout.HORIZONTAL);marketRow.setGravity(Gravity.CENTER_VERTICAL);marketRow.setPadding(dp(18),dp(8),dp(18),dp(2));
        TextView marketTitle=tv("Mercado",14,text());marketTitle.setTypeface(null,Typeface.BOLD);marketRow.addView(marketTitle,new LinearLayout.LayoutParams(0,dp(48),1));
        final Spinner searchMarketSpinner=new Spinner(this);List<String> ms=markets();List<String> labels=new ArrayList<>();for(String m:ms)labels.add(marketLabel(m));
        ArrayAdapter<String> marketAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,labels){
            @Override public View getView(int p,View c,android.view.ViewGroup parent){TextView v=(TextView)super.getView(p,c,parent);v.setTextColor(text());v.setTextSize(14);v.setGravity(Gravity.CENTER_VERTICAL|Gravity.END);return v;}
            @Override public View getDropDownView(int p,View c,android.view.ViewGroup parent){TextView v=(TextView)super.getDropDownView(p,c,parent);v.setTextColor(text());v.setTextSize(15);v.setGravity(Gravity.CENTER_VERTICAL|Gravity.START);v.setPadding(dp(14),dp(10),dp(14),dp(10));v.setBackgroundColor(dark?Color.rgb(18,30,42):Color.WHITE);return v;}
        };
        searchMarketSpinner.setAdapter(marketAdapter);int marketIndex=0;for(int i=0;i<ms.size();i++)if(ms.get(i).equalsIgnoreCase(selectedMarket)){marketIndex=i;break;}searchMarketSpinner.setSelection(marketIndex,false);
        searchMarketSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onNothingSelected(android.widget.AdapterView<?> p){}public void onItemSelected(android.widget.AdapterView<?> p,android.view.View v,int pos,long id){if(pos>=ms.size()||ms.get(pos).equalsIgnoreCase(selectedMarket))return;selectedMarket=ms.get(pos);getSharedPreferences(PREFS,MODE_PRIVATE).edit().putString(KEY_MARKET,selectedMarket).apply();saveSelection();rebuild();renderSearchResults(input,list);}});
        marketRow.addView(searchMarketSpinner,new LinearLayout.LayoutParams(dp(190),dp(48)));
        LinearLayout box=new LinearLayout(this);box.setPadding(dp(18),dp(4),dp(18),dp(2));box.addView(input,new LinearLayout.LayoutParams(-1,dp(52)));
        ScrollView scroll=new ScrollView(this);scroll.addView(list);LinearLayout wrap=new LinearLayout(this);wrap.setOrientation(LinearLayout.VERTICAL);wrap.addView(marketRow);wrap.addView(box);wrap.addView(scroll,new LinearLayout.LayoutParams(-1,dp(430)));
        AlertDialog d=new AlertDialog.Builder(this).setTitle("Añadir coche").setView(wrap).create();
        input.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int b,int c){renderSearchResults(input,list);}public void afterTextChanged(Editable e){}});
        d.setOnShowListener(x->{renderSearchResults(input,list);input.requestFocus();d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);});d.show();
    }
    private void renderSearchResults(EditText input,LinearLayout list){list.removeAllViews();String q=input.getText().toString().trim().toLowerCase(Locale.ROOT);int count=0;for(Vehicle v:marketVehicles()){if(selectedIds.contains(v.id))continue;String hay=(v.make+" "+v.model+" "+v.year+" "+v.version).toLowerCase(Locale.ROOT);if(!q.isEmpty()&&!hay.contains(q))continue;TextView item=tv(v.make+" "+v.model+" · "+(v.year>0?v.year+" · ":"")+v.version,15,text());item.setPadding(dp(18),dp(14),dp(18),dp(14));item.setOnClickListener(x->{if(selectedIds.size()<3){selectedIds.add(v.id);saveSelection();rebuild();renderSearchResults(input,list);}});list.addView(item,new LinearLayout.LayoutParams(-1,-2));if(++count>=80)break;}if(count==0){TextView empty=tv("No hay coincidencias en "+marketLabel(selectedMarket)+".",14,sub());empty.setPadding(dp(18),dp(20),dp(18),dp(20));list.addView(empty);}}
    private String value(Vehicle v,String key){if("battery".equals(key))return fmt(v.batteryKwh)+" kWh";if("type".equals(key))return empty(v.batteryType);if("range".equals(key))return v.wltpKm>0?v.wltpKm+" km":"—";if("cons".equals(key))return v.consumption>0?fmt(v.consumption)+" kWh/100 km":"—";if("power".equals(key))return v.powerKw>0?fmt(v.powerKw)+" kW":"—";if("drive".equals(key))return empty(v.drivetrain);if("acc".equals(key))return v.acc>0?fmt(v.acc)+" s":"—";if("ac".equals(key))return v.acKw>0?fmt(v.acKw)+" kW":"—";if("dc".equals(key))return v.dcKw>0?fmt(v.dcKw)+" kW":"—";if("charge".equals(key))return v.chargeMin>0?v.chargeMin+" min":"—";if("trunk".equals(key))return v.trunk>0?v.trunk+" L":"—";if("weight".equals(key))return v.weight>0?v.weight+" kg":"—";if("price".equals(key))return formatPrice(v.price);return"—";}
    private void addRow(String label,String key,boolean higherBetter){LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.HORIZONTAL);r.setGravity(Gravity.CENTER_VERTICAL);r.setBackgroundColor(rowAlt());TextView l=tv(label,13,text());l.setPadding(dp(5),dp(10),dp(5),dp(10));r.addView(l,new LinearLayout.LayoutParams(dp(112),dp(52)));List<Vehicle> chosen=new ArrayList<>();for(String id:selectedIds){Vehicle v=find(id);if(v!=null)chosen.add(v);}double best=Double.NaN;for(Vehicle v:chosen){double n=numeric(v,key);if(Double.isNaN(n))continue;if(Double.isNaN(best)||(higherBetter?n>best:n<best))best=n;}for(Vehicle v:chosen){TextView cell=tv(value(v,key),12,text());cell.setGravity(Gravity.CENTER);cell.setPadding(dp(4),0,dp(4),0);double n=numeric(v,key);if(!Double.isNaN(best)&&!Double.isNaN(n)&&Math.abs(n-best)<0.0001)cell.setTextColor(blue);r.addView(cell,new LinearLayout.LayoutParams(dp(145),dp(52)));}table.addView(r,new LinearLayout.LayoutParams(tableWidth(),dp(52)));}
    private double numeric(Vehicle v,String key){if("battery".equals(key))return v.batteryKwh;if("range".equals(key))return v.wltpKm;if("cons".equals(key))return v.consumption;if("power".equals(key))return v.powerKw;if("acc".equals(key))return v.acc;if("ac".equals(key))return v.acKw;if("dc".equals(key))return v.dcKw;if("charge".equals(key))return v.chargeMin;if("trunk".equals(key))return v.trunk;if("weight".equals(key))return v.weight;if("price".equals(key))return v.price;return Double.NaN;}
    private void buildSummary(){
        LinearLayout card=new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16),dp(14),dp(16),dp(14));
        card.setBackground(strokeBg(dark?Color.rgb(17,31,44):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(218,228,239),18));
        TextView h=tv("Resumen de la comparativa",18,text());
        h.setTypeface(null,Typeface.BOLD);
        card.addView(h,new LinearLayout.LayoutParams(-1,dp(30)));
        TextView intro=tv("Resultado rápido de los vehículos seleccionados",13,sub());
        card.addView(intro,new LinearLayout.LayoutParams(-1,dp(28)));
        addSummaryWinner(card,"Autonomía", "range", true);
        addSummaryWinner(card,"Consumo", "cons", false);
        addSummaryWinner(card,"Potencia", "power", true);
        addSummaryWinner(card,"Carga DC", "dc", true);
        addSummaryWinner(card,"Precio", "price", false);
        TextView selected=tv("\nVehículos comparados",14,blue);
        selected.setTypeface(null,Typeface.BOLD);
        card.addView(selected,new LinearLayout.LayoutParams(-1,dp(28)));
        for(String id:selectedIds){Vehicle v=find(id);if(v==null)continue;TextView s=tv("•  "+v.make+" "+v.model+" · "+v.version+"  ·  "+marketLabel(v.market),13,text());s.setPadding(0,dp(3),0,dp(3));card.addView(s,new LinearLayout.LayoutParams(-1,-2));}
        summary.addView(card,new LinearLayout.LayoutParams(-1,-2));
    }
    private void addSummaryWinner(LinearLayout parent,String label,String key,boolean higherBetter){
        List<Vehicle> chosen=new ArrayList<>();
        for(String id:selectedIds){Vehicle v=find(id);if(v!=null)chosen.add(v);}
        Vehicle bestV=null;double best=Double.NaN;
        for(Vehicle v:chosen){double n=numeric(v,key);if(Double.isNaN(n))continue;if(Double.isNaN(best)||(higherBetter?n>best:n<best)){best=n;bestV=v;}}
        if(bestV==null)return;
        String val=value(bestV,key);
        TextView row=tv(label+"  ·  "+bestV.make+" "+bestV.model+"  →  "+val,13,text());
        row.setPadding(0,dp(4),0,dp(4));
        parent.addView(row,new LinearLayout.LayoutParams(-1,dp(30)));
    }
    private String empty(String s){return s==null||s.trim().isEmpty()?"—":s;} private String fmt(double n){return String.format(Locale.US,"%.1f",n).replace('.',',');} private String formatPrice(double p){if(p<=0)return"—";String currency=getSharedPreferences(PREFS,MODE_PRIVATE).getString(KEY_CURRENCY,"EUR");return String.format(Locale.US,"%,.0f %s",p,currency).replace(',','.');}
    static class Vehicle{String id,make,model,version,batteryType,drivetrain,market;int year;double price,batteryKwh,usableBatteryKwh,wltpKm,consumption,powerKw,acKw,dcKw,chargeMin,acc,trunk,weight;Vehicle(JSONObject o){id=o.optString("id",UUID.randomUUID().toString());make=o.optString("make",o.optString("brand",""));model=o.optString("model","");version=o.optString("version",o.optString("trim",""));batteryType=o.optString("batteryType","");drivetrain=o.optString("drivetrain","");market=o.optString("market",o.optString("mercado","ES")).toUpperCase(Locale.ROOT);year=o.optInt("year",o.optInt("modelYear",0));price=o.optDouble("price",0);batteryKwh=o.optDouble("batteryKwh",o.optDouble("battery_capacity_kwh",0));usableBatteryKwh=o.optDouble("usableBatteryKwh",0);wltpKm=o.optDouble("wltpKm",o.optDouble("rangeKm",0));consumption=o.optDouble("consumption",0);powerKw=o.optDouble("powerKw",0);acKw=o.optDouble("acKw",0);dcKw=o.optDouble("dcKw",0);chargeMin=o.optDouble("chargeMin",0);acc=o.optDouble("acc",o.optDouble("acceleration",0));trunk=o.optDouble("trunk",o.optDouble("trunkLiters",0));weight=o.optDouble("weight",0);}}
}
