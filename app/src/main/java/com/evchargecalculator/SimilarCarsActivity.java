package com.evchargecalculator;

import android.content.Intent;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.*;

public class SimilarCarsActivity extends BaseNavigationActivity {
    @Override protected int getBottomNavigationIndex(){ return 2; }

    private static final String PREFS="ev_charge_calculator";
    private boolean dark;
    private final List<Vehicle> vehicles=new ArrayList<>();
    private EditText search;
    private Spinner marketSpinner, yearSpinner, driveSpinner, batterySpinner;
    private LinearLayout results, referenceResults;
    private TextView selectedTitle, resultsTitle;
    private Vehicle reference;

    // Search optimization index.
    private final IdentityHashMap<Vehicle, SearchIndex> searchIndexCache = new IdentityHashMap<>();
    private static class SearchIndex {
        final String make, model, version, all, makeModel, marketLabel;
        SearchIndex(String make, String model, String version, String all, String makeModel, String marketLabel) {
            this.make=make; this.model=model; this.version=version; this.all=all; this.makeModel=makeModel; this.marketLabel=marketLabel;
        }
    }
    private SearchIndex searchIndex(Vehicle v) {
        SearchIndex cached=searchIndexCache.get(v);
        if(cached!=null)return cached;
        SearchIndex created=new SearchIndex(norm(v.make),norm(v.model),norm(v.version),
            norm(v.make+" "+v.model+" "+v.year+" "+v.batteryKwh+" "+v.batteryType+" "+v.drivetrain+" "+v.version),
            norm(v.make+" "+v.model),market(v.market));
        searchIndexCache.put(v,created);
        return created;
    }

    private int blue=Color.rgb(46,107,255);
    private int text(){return dark?Color.rgb(245,248,255):Color.rgb(22,42,63);}
    private int sub(){return dark?Color.rgb(170,183,204):Color.rgb(90,111,137);}
    private int dp(int n){return(int)(n*getResources().getDisplayMetrics().density+.5f);}
    private TextView tv(String s,float z,int c){TextView t=new TextView(this);t.setText(LanguageManager.t(this,s));t.setTextSize(z);t.setTextColor(c);return t;}
    private GradientDrawable bg(int c,float r){GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(dp((int)r));return g;}
    private GradientDrawable strokeBg(int fill,int stroke,float r){GradientDrawable g=bg(fill,r);g.setStroke(dp(1),stroke);return g;}

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        LanguageManager.applyStored(this);
        dark=isDarkTheme();
        loadVehicles();
        build();
        EdgeToEdgeHelper.apply(this,dark);
    }

    private void build(){
        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(dark?Color.rgb(7,19,28):Color.rgb(241,246,251));

        ScrollView scroll=new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);

        LinearLayout content=new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(14),0,dp(14),dp(18));

        LinearLayout hero=new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dp(18),dp(16),dp(18),dp(16));
        GradientDrawable heroBg=new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[]{dark?Color.rgb(12,42,66):Color.rgb(20,123,207),dark?Color.rgb(7,28,44):Color.rgb(46,107,255)}
        );
        heroBg.setCornerRadius(dp(22));
        hero.setBackground(heroBg);

        TextView icon=tv("🔎",34,Color.WHITE);
        hero.addView(icon,new LinearLayout.LayoutParams(-1,dp(38)));

        TextView title=tv("Buscar coches similares",25,Color.WHITE);
        title.setTypeface(null,Typeface.BOLD);
        hero.addView(title,new LinearLayout.LayoutParams(-1,dp(36)));

        TextView intro=tv("Elige un coche de referencia y descubre las 10 alternativas más similares del catálogo.",14,Color.WHITE);
        intro.setAlpha(0.94f);
        intro.setLineSpacing(0,1.15f);
        hero.addView(intro,new LinearLayout.LayoutParams(-1,dp(40)));

        content.addView(hero,marginLp(-1,-2,0,dp(10),0,0));

        LinearLayout searchCard=card();
        searchCard.setPadding(dp(14),dp(14),dp(14),dp(14));

        TextView searchLabel=tv("Busca tu coche de referencia",16,text());
        searchLabel.setTypeface(null,Typeface.BOLD);
        searchCard.addView(searchLabel,new LinearLayout.LayoutParams(-1,dp(30)));

        search=new EditText(this);
        search.setVisibility(View.GONE);

        TextView picker=tv("🚗  Seleccionar coche del catálogo  ›",15,text());
        picker.setTypeface(null,Typeface.BOLD);
        picker.setGravity(Gravity.CENTER_VERTICAL|Gravity.START);
        picker.setPadding(dp(14),0,dp(14),0);
        picker.setBackground(strokeBg(dark?Color.rgb(10,24,36):Color.rgb(248,251,255),dark?Color.rgb(43,64,82):Color.rgb(205,219,233),14));
        picker.setOnClickListener(v->showVehiclePicker(picker));
        searchCard.addView(picker,new LinearLayout.LayoutParams(-1,dp(52)));
        content.addView(searchCard,marginLp(-1,-2,0,0,0,dp(10)));


        referenceResults=card();
        referenceResults.setPadding(dp(14),dp(10),dp(14),dp(10));
        selectedTitle=tv("Coche de referencia",17,text());
        selectedTitle.setTypeface(null,Typeface.BOLD);
        referenceResults.addView(selectedTitle,new LinearLayout.LayoutParams(-1,dp(34)));
        referenceResults.setVisibility(View.GONE);
        content.addView(referenceResults,marginLp(-1,-2,0,0,0,dp(10)));

        results=card();
        results.setPadding(dp(14),dp(10),dp(14),dp(10));
        resultsTitle=tv("Coches similares",17,text());
        resultsTitle.setTypeface(null,Typeface.BOLD);
        resultsTitle.setVisibility(View.GONE);
        results.addView(resultsTitle,new LinearLayout.LayoutParams(-1,dp(34)));

        content.addView(results,marginLp(-1,-2,0,0,0,dp(10)));

        TextView note=tv("Primero identifica competidores reales y después los ordena por similitud de características.",12,sub());
        note.setLineSpacing(0,1.15f);
        note.setPadding(dp(4),dp(10),dp(4),dp(12));
        content.addView(note,new LinearLayout.LayoutParams(-1,-2));

        scroll.addView(content);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
    }

    private LinearLayout.LayoutParams marginLp(int w,int h,int l,int t,int r,int b){
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(w,h);p.setMargins(l,t,r,b);return p;
    }
    private Spinner spinner(){Spinner s=new Spinner(this);s.setBackground(strokeBg(dark?Color.rgb(17,31,44):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(218,228,239),10));return s;}
    private void addSpinner(LinearLayout row,Spinner s,float weight){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-1,weight);p.setMargins(dp(3),dp(3),dp(3),dp(3));row.addView(s,p);}

    private void populateFilters(){
        List<String> markets=new ArrayList<>(); markets.add("Todos los mercados");
        TreeSet<String> ms=new TreeSet<>(); for(Vehicle v:vehicles)if(!v.market.isEmpty())ms.add(v.market);
        markets.addAll(ms); setAdapter(marketSpinner,markets);

        List<String> years=new ArrayList<>(Arrays.asList("Todos los años","2026","2025","2024")); setAdapter(yearSpinner,years);

        List<String> drives=new ArrayList<>(Arrays.asList("Cualquier tracción","FWD","RWD","AWD")); setAdapter(driveSpinner,drives);
        List<String> bats=new ArrayList<>(Arrays.asList("Cualquier batería","≤ 50 kWh","50–70 kWh","70–90 kWh","> 90 kWh")); setAdapter(batterySpinner,bats);
    }
    private void setAdapter(Spinner s,List<String> data){ArrayAdapter<String>a=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,data){@Override public View getView(int p,android.view.View v,android.view.ViewGroup g){TextView t=(TextView)super.getView(p,v,g);t.setText(LanguageManager.t(SimilarCarsActivity.this,t.getText().toString()));t.setTextColor(text());t.setTextSize(12);t.setPadding(dp(8),0,dp(4),0);return t;}@Override public View getDropDownView(int p,android.view.View v,android.view.ViewGroup g){TextView t=(TextView)super.getDropDownView(p,v,g);t.setText(LanguageManager.t(SimilarCarsActivity.this,t.getText().toString()));t.setTextColor(Color.DKGRAY);t.setTextSize(13);return t;}};a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);s.setAdapter(a);}

    private void showVehiclePicker(TextView picker){
        final EditText input=new EditText(this);
        input.setSingleLine(true);
        input.setHint(LanguageManager.t(this,"Marca, modelo, año, batería o versión"));
        input.setTextColor(text()); input.setHintTextColor(sub()); input.setTextSize(15);
        input.setPadding(dp(14),0,dp(14),0);
        input.setBackground(strokeBg(dark?Color.rgb(20,35,49):Color.rgb(247,250,254),dark?Color.rgb(59,84,106):Color.rgb(211,223,236),16));

        final ListView list=new ListView(this);
        list.setDivider(null); list.setVerticalScrollBarEnabled(false); list.setPadding(0,dp(2),0,0); list.setClipToPadding(false);
        final TextView header=tv("Todos los vehículos",13,blue);
        header.setTypeface(null,Typeface.BOLD); header.setPadding(dp(18),dp(16),dp(18),dp(7));
        list.addHeaderView(header,null,false);

        final List<Vehicle> found=new ArrayList<>();
        final BaseAdapter adapter=new BaseAdapter(){
            @Override public int getCount(){return found.size();}
            @Override public Object getItem(int position){return found.get(position);}
            @Override public long getItemId(int position){return position;}
            @Override public View getView(int position,View convertView,android.view.ViewGroup parent){
                TextView item=convertView instanceof TextView?(TextView)convertView:new TextView(SimilarCarsActivity.this);
                item.setLayoutParams(new android.widget.AbsListView.LayoutParams(-1,dp(60)));
                item.setGravity(Gravity.CENTER_VERTICAL|Gravity.START);
                item.setPadding(dp(16),dp(6),dp(42),dp(6));
                item.setLineSpacing(0,1.05f);
                Vehicle v=found.get(position);
                android.text.SpannableString styled=new android.text.SpannableString(pickerLabel(v));
                int nl=styled.toString().indexOf('\n');
                if(nl>0){
                    styled.setSpan(new android.text.style.StyleSpan(Typeface.BOLD),0,nl,android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if(nl+1<styled.length())styled.setSpan(new android.text.style.RelativeSizeSpan(0.86f),nl+1,styled.length(),android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                item.setText(styled); item.setTextSize(14); item.setTextColor(text());
                item.setBackground(strokeBg(dark?Color.rgb(18,31,44):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(225,233,242),14));
                return item;
            }
        };
        list.setAdapter(adapter);

        LinearLayout marketRow=new LinearLayout(this);
        marketRow.setOrientation(LinearLayout.HORIZONTAL);
        marketRow.setGravity(Gravity.CENTER_VERTICAL);
        marketRow.setPadding(dp(18),dp(12),dp(18),dp(6));
        TextView marketTitle=tv("Mercado",12,sub());
        marketTitle.setTypeface(null,Typeface.BOLD);
        marketRow.addView(marketTitle,new LinearLayout.LayoutParams(0,dp(38),1));

        final Spinner searchMarketSpinner=new Spinner(this);
        List<String> ms=new ArrayList<>();
        TreeSet<String> marketSet=new TreeSet<>();
        for(Vehicle v:vehicles)if(v.market!=null&&!v.market.isEmpty())marketSet.add(v.market);
        ms.addAll(marketSet);
        List<String> labels=new ArrayList<>();
        for(String m:ms)labels.add(market(m));
        ArrayAdapter<String> marketAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,labels){
            @Override public View getView(int p,View c,android.view.ViewGroup parent){
                TextView v=(TextView)super.getView(p,c,parent);v.setTextColor(text());v.setTextSize(14);v.setGravity(Gravity.CENTER_VERTICAL|Gravity.END);return v;
            }
            @Override public View getDropDownView(int p,View c,android.view.ViewGroup parent){
                TextView v=(TextView)super.getDropDownView(p,c,parent);v.setTextColor(text());v.setTextSize(15);
                v.setGravity(Gravity.CENTER_VERTICAL|Gravity.START);v.setPadding(dp(14),dp(10),dp(14),dp(10));
                v.setBackgroundColor(dark?Color.rgb(18,30,42):Color.WHITE);return v;
            }
        };
        searchMarketSpinner.setAdapter(marketAdapter);
        searchMarketSpinner.setBackground(strokeBg(dark?Color.rgb(20,35,49):Color.WHITE,dark?Color.rgb(59,84,106):Color.rgb(211,223,236),14));
        searchMarketSpinner.setPadding(dp(10),0,dp(8),0);

        final String defaultMarket=ms.contains("ES")?"ES":(ms.isEmpty()?"":ms.get(0));
        int marketIndex=ms.indexOf(defaultMarket);
        if(marketIndex>=0)searchMarketSpinner.setSelection(marketIndex);

        final android.os.Handler searchHandler=new android.os.Handler(android.os.Looper.getMainLooper());
        final Runnable refresh=()->{
            String q=input.getText()==null?"":input.getText().toString().trim();
            String selectedMarket=searchMarketSpinner.getSelectedItem()==null?"":String.valueOf(searchMarketSpinner.getSelectedItem());
            found.clear();
            String nq=norm(q);
            String[] tokens=nq.isEmpty()?new String[0]:nq.split("\\s+");
            final IdentityHashMap<Vehicle,Integer> scores=new IdentityHashMap<>();
            for(Vehicle v:vehicles){
                SearchIndex idx=searchIndex(v);
                if(!selectedMarket.isEmpty()&&!idx.marketLabel.equalsIgnoreCase(selectedMarket))continue;
                boolean matches=true;
                for(String token:tokens){
                    if(!token.isEmpty()&&!idx.all.contains(token)){matches=false;break;}
                }
                if(!matches)continue;
                found.add(v);
                scores.put(v,pickerSearchScore(idx,nq,tokens));
            }
            Collections.sort(found,(a,b)->{
                int c=Integer.compare(scores.get(b),scores.get(a));
                if(c!=0)return c;
                c=Integer.compare(b.year,a.year);
                if(c!=0)return c;
                c=a.make.compareToIgnoreCase(b.make);
                if(c!=0)return c;
                c=a.model.compareToIgnoreCase(b.model);
                if(c!=0)return c;
                c=Integer.compare(trimRank(a),trimRank(b));
                if(c!=0)return c;
                c=Double.compare(a.batteryKwh,b.batteryKwh);
                if(c!=0)return c;
                return a.version.compareToIgnoreCase(b.version);
            });
            header.setVisibility(q.isEmpty()?View.VISIBLE:View.GONE);
            adapter.notifyDataSetChanged();
        };

        list.setOnItemClickListener((parent,view,position,id)->{
            int resultPosition=position-list.getHeaderViewsCount();
            if(resultPosition<0||resultPosition>=found.size())return;
            Vehicle v=found.get(resultPosition);
            Object tag=input.getTag();
            if(tag instanceof AlertDialog)((AlertDialog)tag).dismiss();
            reference=v;
            picker.setText("✓  "+v.make+" "+v.model+" · "+v.version);
            showSimilar();
        });

        searchMarketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> p,View v,int a,long b){searchHandler.removeCallbacks(refresh);searchHandler.post(refresh);}
            public void onNothingSelected(AdapterView<?> p){}
        });

        LinearLayout body=new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(12),dp(12),dp(12),dp(8));
        body.addView(input,new LinearLayout.LayoutParams(-1,dp(50)));
        body.addView(marketRow,new LinearLayout.LayoutParams(-1,dp(56)));
        marketRow.addView(searchMarketSpinner,new LinearLayout.LayoutParams(dp(180),dp(38)));
        body.addView(list,new LinearLayout.LayoutParams(-1,0,1));

        AlertDialog d=new AlertDialog.Builder(this).setView(body).create();
        input.setTag(d);
        input.addTextChangedListener(new TextWatcher(){
            public void beforeTextChanged(CharSequence s,int st,int c,int a){}
            public void onTextChanged(CharSequence s,int st,int b,int c){searchHandler.removeCallbacks(refresh);searchHandler.postDelayed(refresh,70);}
            public void afterTextChanged(Editable e){}
        });
        d.setOnShowListener(x->{
            if(d.getWindow()==null)return;
            d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            d.getWindow().setBackgroundDrawable(bg(dark?Color.rgb(10,21,31):Color.WHITE,20));
            input.requestFocus();
            input.post(()->{
                InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                if(imm!=null)imm.showSoftInput(input,InputMethodManager.SHOW_IMPLICIT);
                searchHandler.removeCallbacks(refresh);searchHandler.post(refresh);
            });
        });
        d.show();
        Window searchWindow=d.getWindow();
        if(searchWindow!=null){
            searchWindow.setBackgroundDrawable(bg(dark?Color.rgb(10,21,31):Color.WHITE,20));
            searchWindow.setLayout(-1,-1);
            searchWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        input.requestFocus();
    }

    private int trimRank(Vehicle v){String k=v.version==null?"":v.version.toLowerCase(Locale.ROOT);if(k.contains("standard")||k.contains("base")||k.contains("comfort"))return 0;if(k.contains("long range")||k.contains("extended"))return 1;if(k.contains("premium")||k.contains("performance")||k.contains("max"))return 2;return 3;}

    private int pickerSearchScore(SearchIndex idx,String q,String[] tokens){
        if(q.isEmpty())return 0;
        int score=0;
        for(String token:tokens){
            if(token.isEmpty())continue;
            if(idx.make.equals(token))score+=120;
            else if(idx.make.startsWith(token))score+=70;
            else if(idx.make.contains(token))score+=45;
            if(idx.model.equals(token))score+=110;
            else if(idx.model.startsWith(token))score+=65;
            else if(idx.model.contains(token))score+=40;
            if(idx.version.contains(token))score+=20;
            if(idx.all.contains(token))score+=10;
        }
        if(idx.makeModel.equals(q))score+=180;
        else if(idx.makeModel.startsWith(q))score+=100;
        return score;
    }

    private String pickerLabel(Vehicle v){
        String first=v.make+" "+v.model;
        StringBuilder second=new StringBuilder();
        if(v.year>0)second.append(v.year);
        String ver=v.version==null?"":v.version.trim().replaceAll("(?i)(?<![0-9])\\d+(?:[.,]\\d+)?\\s*kwh\\b","").replaceAll("(?i)(?<![0-9])\\d+(?:[.,]\\d+)?\\s*kw\\b","").replaceAll("\\s+"," ").trim();
        if(!ver.isEmpty()){if(second.length()>0)second.append(" · ");second.append(ver);}
        if(v.batteryKwh>0){if(second.length()>0)second.append(" · ");second.append(fmt(v.batteryKwh)).append(" kWh");}
        return first+"\n"+second;
    }

    private void showReferenceCandidates(){
        // Ya no se utiliza una búsqueda libre en pantalla: el coche de referencia
        // se selecciona exclusivamente desde el selector del catálogo.
        if(results==null||referenceResults==null)return;
        if(reference==null){
            referenceResults.removeAllViews();
            referenceResults.setVisibility(View.VISIBLE);
            selectedTitle.setText(LanguageManager.t(this,"Coche de referencia"));
            referenceResults.addView(selectedTitle,new LinearLayout.LayoutParams(-1,dp(34)));
            results.removeAllViews();
            results.addView(resultsTitle,new LinearLayout.LayoutParams(-1,dp(34)));
            resultsTitle.setVisibility(View.GONE);
        }
    }

    private void addReferenceCard(Vehicle v){
        // Conservado para compatibilidad con el flujo anterior; la selección actual
        // se realiza mediante el selector del catálogo.
        if(v!=null){reference=v;showSimilar();}
    }

    private void showSimilar(){
        results.removeAllViews();
        if(reference==null||vehicles==null||vehicles.isEmpty())return;

        final int COMPETITOR_POOL=100;
        final int FINAL_TOP=10;
        int referenceYear=reference.year;
        int minYear=referenceYear>0?referenceYear-5:0;

        List<Scored> competitors=buildCompetitionCandidates(reference,COMPETITOR_POOL,referenceYear,minYear);

        // Una marca no se representa por el primer coche que aparece en
        // competencia pura. Primero dejamos entrar un pool amplio por competencia
        // y, dentro de cada marca, elegimos el modelo que mejor equilibra
        // competencia (60%) y características (40%).
        Map<String,Scored> bestByMake=new LinkedHashMap<>();
        for(Scored s:competitors){
            String make=s.v.make==null?"":s.v.make.trim().toLowerCase(Locale.ROOT);
            if(make.isEmpty())continue;

            Scored current=bestByMake.get(make);
            if(current==null||compareFinalScore(s,current)<0){
                bestByMake.put(make,s);
            }
        }

        List<Scored> uniqueBrands=new ArrayList<>(bestByMake.values());
        Collections.sort(uniqueBrands,(a,b)->compareFinalScore(a,b));

        int n=Math.min(FINAL_TOP,uniqueBrands.size());
        for(int i=0;i<n;i++)addSimilarCard(uniqueBrands.get(i),i+1);
        if(n==0)results.addView(tv("No hay suficientes opciones similares con estos filtros.",13,sub()));
    }

    /** FASE 1: competencia pura. La similitud técnica no decide quién entra. */
    private List<Scored> buildCompetitionCandidates(Vehicle reference,int topPool,int referenceYear,int minYear){
        List<Scored> all=new ArrayList<>();
        for(Vehicle v:vehicles){
            if(v==reference)continue;
            if(reference.make!=null&&v.make!=null&&reference.make.trim().equalsIgnoreCase(v.make.trim()))continue;
            if(referenceYear>0&&(v.year>referenceYear||v.year<minYear))continue;

            double competition=competitionDistance(reference,v);
            double technical=similarity(reference,v);

            if(Double.isFinite(competition)&&Double.isFinite(technical)){
                all.add(new Scored(v,competition,technical));
            }
        }

        Collections.sort(all,(x,y)->{
            int c=Double.compare(x.competitionScore,y.competitionScore);
            if(c!=0)return c;
            c=Double.compare(x.technicalScore,y.technicalScore);
            if(c!=0)return c;
            c=Integer.compare(Math.abs(x.v.year-referenceYear),Math.abs(y.v.year-referenceYear));
            if(c!=0)return c;
            return Integer.compare(y.v.year,x.v.year);
        });
        return new ArrayList<>(all.subList(0,Math.min(topPool,all.size())));
    }

    private int distinctBrandCount(List<Scored> scored){
        Set<String> brands=new HashSet<>();
        for(Scored s:scored){
            if(s==null||s.v==null)continue;
            String make=s.v.make==null?"":s.v.make.trim().toLowerCase(Locale.ROOT);
            if(!make.isEmpty())brands.add(make);
        }
        return brands.size();
    }

    private boolean sameBodyStyle(Vehicle a,Vehicle b){
        if(a==null||b==null)return false;
        String ba=normalizeBodyStyle(a.bodyStyle);
        String bb=normalizeBodyStyle(b.bodyStyle);
        if(ba.isEmpty()||bb.isEmpty())return false;
        return ba.equals(bb)||(isSuvLike(ba)&&isSuvLike(bb));
    }

    private boolean sameSegmentCompatible(Vehicle a,Vehicle b){
        String sa=a.segment==null?"":a.segment.trim().toUpperCase(Locale.ROOT);
        String sb=b.segment==null?"":b.segment.trim().toUpperCase(Locale.ROOT);
        if(sa.isEmpty()||sb.isEmpty())return false;
        return sa.equals(sb)||(isSuvLike(a.bodyStyle)&&isSuvLike(b.bodyStyle));
    }

    private boolean samePhysicalClass(Vehicle a,Vehicle b,double lengthTolerance,double widthTolerance,double heightTolerance){
        if(a==null||b==null||a.lengthMm<=0||b.lengthMm<=0||
           a.widthMm<=0||b.widthMm<=0||a.heightMm<=0||b.heightMm<=0)return false;
        double lengthDiff=Math.abs(a.lengthMm-b.lengthMm)/Math.max(a.lengthMm,b.lengthMm);
        double widthDiff=Math.abs(a.widthMm-b.widthMm)/Math.max(a.widthMm,b.widthMm);
        double heightDiff=Math.abs(a.heightMm-b.heightMm)/Math.max(a.heightMm,b.heightMm);
        return lengthDiff<=lengthTolerance/100.0
            &&widthDiff<=widthTolerance/100.0
            &&heightDiff<=heightTolerance/100.0;
    }

    private boolean priceWithin(Vehicle a,Vehicle b,double maxPricePercent){
        if(a==null||b==null)return false;
        if(a.price<=0||b.price<=0)return true;
        double referencePrice=Math.max(a.price,b.price);
        double priceRatio=Math.abs(a.price-b.price)/referencePrice;
        return priceRatio<=maxPricePercent/100.0;
    }

    private int compareFinalScore(Scored a,Scored b){
        double scoreA=a.competitionScore*.60+a.technicalScore*.40;
        double scoreB=b.competitionScore*.60+b.technicalScore*.40;
        int cmp=Double.compare(scoreA,scoreB);
        if(cmp!=0)return cmp;
        cmp=Double.compare(a.competitionScore,b.competitionScore);
        if(cmp!=0)return cmp;
        cmp=Double.compare(a.technicalScore,b.technicalScore);
        if(cmp!=0)return cmp;
        return Integer.compare(b.v.year,a.v.year);
    }

    private void addSimilarCard(Scored s,int rank){
        Vehicle v=s.v;
        // El Top 10 ya está seleccionado por competencia.
        // El orden y el porcentaje mostrado usan 60% competencia + 40% características.
        double finalScore=s.competitionScore*.60+s.technicalScore*.40;
        int similarityScore=(int)Math.round(Math.max(0,Math.min(100,100-finalScore*100)));

        LinearLayout card=card();
        card.setPadding(dp(16),dp(9),dp(16),dp(9));

        LinearLayout top=new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);

        TextView badge=tv("#"+rank,12,Color.WHITE);
        badge.setTypeface(null,Typeface.BOLD);
        badge.setGravity(Gravity.CENTER);
        badge.setBackground(bg(blue,12));
        top.addView(badge,new LinearLayout.LayoutParams(dp(42),dp(28)));

        TextView name=tv(v.make+" "+v.model,17,text());
        name.setTypeface(null,Typeface.BOLD);
        name.setPadding(dp(10),0,dp(6),0);
        name.setGravity(Gravity.CENTER_VERTICAL|Gravity.START);
        name.setTranslationY(-dp(4));
        name.setMaxLines(2);
        name.setEllipsize(android.text.TextUtils.TruncateAt.END);
        top.addView(name,new LinearLayout.LayoutParams(0,dp(42),1));

        TextView score=tv(similarityScore+" %",13,blue);
        score.setTypeface(null,Typeface.BOLD);
        score.setGravity(Gravity.CENTER);
        score.setBackground(strokeBg(dark?Color.rgb(13,36,58):Color.rgb(235,243,255),blue,10));
        top.addView(score,new LinearLayout.LayoutParams(dp(64),dp(30)));

        card.addView(top,new LinearLayout.LayoutParams(-1,dp(42)));

        TextView ver=tv(cleanVersion(v.version)+"  ·  "+v.year,12,sub());
        ver.setSingleLine(true);
        ver.setEllipsize(android.text.TextUtils.TruncateAt.END);
        card.addView(ver,new LinearLayout.LayoutParams(-1,dp(21)));

        TextView specs=tv(specLine(v),12,sub());
        specs.setSingleLine(true);
        specs.setEllipsize(android.text.TextUtils.TruncateAt.END);
        specs.setPadding(0,dp(2),0,dp(4));
        card.addView(specs,new LinearLayout.LayoutParams(-1,dp(23)));

        TextView action=tv("Ver detalles  ›",13,blue);
        action.setTypeface(null,Typeface.BOLD);
        action.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
        action.setOnClickListener(x->openDetail(v));
        card.addView(action,new LinearLayout.LayoutParams(-1,dp(24)));

        card.setOnClickListener(x->addSimilarToComparison(v));
        results.addView(card,marginLp(-1,-2,0,0,0,dp(6)));
    }

    private LinearLayout card(){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(dp(14),dp(10),dp(14),dp(10));c.setBackground(strokeBg(dark?Color.rgb(17,31,44):Color.WHITE,dark?Color.rgb(43,64,82):Color.rgb(218,228,239),14));return c;}

    /**
     * Calcula una distancia de similitud normalizada entre 0 (idénticos) y 1
     * (muy diferentes). La puntuación visible se obtiene como 100 - distancia*100.
     *
     * El algoritmo usa tolerancias relativas en lugar de escalas absolutas.
     * Así, por ejemplo, 10 kWh de diferencia no pesan igual en una batería de
     * 40 kWh que en una de 100 kWh. Los campos ausentes se excluyen y el peso
     * restante se renormaliza, evitando penalizar a un coche por datos que no existen.
     */
    /**
     * Distancia competitiva normalizada: 0 = rival directo, 1 = poco competitivo.
     *
     * Se basa únicamente en variables que definen si dos coches se disputan
     * realmente el mismo comprador:
     * - carrocería: 25%
     * - tamaño físico: 35%
     * - precio: 25%
     * - segmento: 15%
     *
     * SUV y crossover son equivalentes. En SUV/crossover, una diferencia de
     * segmento del catálogo no elimina al rival, pero sí aporta una pequeña
     * penalización. No se usa la marca ni su notoriedad.
     */
    /**
     * Distancia competitiva normalizada: 0 = rival directo, 1 = poco competitivo.
     *
     * La competencia pesa más que la similitud técnica. Además de carrocería,
     * tamaño, precio y segmento, incorporamos el posicionamiento de prestaciones.
     *
     * Importante: el bloque de prestaciones es DIRECCIONAL:
     * - si el candidato rinde peor que el referente, se penaliza;
     * - si rinde igual o mejor, no se penaliza por esa variable.
     *
     * Esto evita el error de considerar al Model Y menos competidor del G6
     * simplemente por tener más potencia, mejor aceleración o mayor autonomía.
     */
    private double competitionDistance(Vehicle a,Vehicle b){
        if(a==null||b==null)return Double.POSITIVE_INFINITY;
        double sum=0,weight=0,d;

        d=commercialPriceDistance(a.price,b.price); if(d>=0){sum+=d*.35;weight+=.35;}
        d=physicalCompetitionDistance(a,b); if(d>=0){sum+=d*.30;weight+=.30;}
        d=bodyCompetitionDistance(a.bodyStyle,b.bodyStyle); if(d>=0){sum+=d*.20;weight+=.20;}
        d=segmentCompetitionDistance(a.segment,b.segment,a.bodyStyle,b.bodyStyle); if(d>=0){sum+=d*.15;weight+=.15;}

        return weight>0?Math.min(1,sum/weight):Double.POSITIVE_INFINITY;
    }

    private double commercialPriceDistance(double a,double b){
        if(a<=0||b<=0)return -1;
        return softDistance(Math.abs(a-b)/Math.max(a,b),.22);
    }

    private double physicalCompetitionDistance(Vehicle a,Vehicle b){
        double sum=0,weight=0,d;
        d=softRelativeDistance(a.lengthMm,b.lengthMm,.08); if(d>=0){sum+=d*.50;weight+=.50;}
        d=softRelativeDistance(a.widthMm,b.widthMm,.05); if(d>=0){sum+=d*.25;weight+=.25;}
        d=softRelativeDistance(a.heightMm,b.heightMm,.07); if(d>=0){sum+=d*.25;weight+=.25;}
        return weight>0?Math.min(1,sum/weight):-1;
    }

    private double softRelativeDistance(double a,double b,double scale){
        if(a<=0||b<=0||scale<=0)return -1;
        return softDistance(Math.abs(a-b)/Math.max(a,b),scale);
    }

    private double bodyCompetitionDistance(String a,String b){
        String x=normalizeBodyStyle(a),y=normalizeBodyStyle(b);
        if(x.isEmpty()||y.isEmpty())return -1;
        if(x.equals(y))return 0;
        if(isSuvLike(x)&&isSuvLike(y))return .08;
        return 1.0;
    }

    private double segmentCompetitionDistance(String a,String b,String bodyA,String bodyB){
        String x=a==null?"":a.trim().toUpperCase(Locale.ROOT);
        String y=b==null?"":b.trim().toUpperCase(Locale.ROOT);
        if(x.isEmpty()||y.isEmpty())return -1;
        if(x.equals(y))return 0;

        // Algunos registros del catálogo usan códigos de segmento que no son
        // comparables entre sí (por ejemplo C frente a J). En SUV/crossover no
        // tratamos un código desconocido como una distancia enorme: el tamaño
        // físico ya se mide por separado y es una señal mucho más fiable del
        // espacio de mercado que pretende representar el coche.
        if(isSuvLike(bodyA)&&isSuvLike(bodyB)){
            int dx=segmentRank(x),dy=segmentRank(y);
            if(dx>=0&&dy>=0){
                int gap=Math.abs(dx-dy);
                if(gap==1)return .18;
                if(gap==2)return .35;
                return .50;
            }
            if(dx<0||dy<0)return -1;
        }
        return .70;
    }

    private int segmentRank(String s){
        String x=s==null?"":s.trim().toUpperCase(Locale.ROOT);
        if(x.equals("A"))return 0;
        if(x.equals("B"))return 1;
        if(x.equals("C"))return 2;
        if(x.equals("D"))return 3;
        if(x.equals("E"))return 4;
        if(x.equals("F"))return 5;
        return -1;
    }

    private double softDistance(double ratio,double scale){
        if(ratio<0||scale<=0)return -1;
        return 1.0-Math.exp(-Math.pow(ratio/scale,2));
    }

    /**
     * Posicionamiento de prestaciones dentro de la competencia.
     *
     * Se penaliza únicamente cuando el candidato queda por debajo del
     * referente. Un rival superior en una variable no recibe castigo.
     *
     * Potencia 20%, 0-100 20%, WLTP 25%, consumo 15%, carga 10-80 20%. Total = 100%.
     */
    private double performanceCompetitionDistance(Vehicle reference,Vehicle candidate){
        double sum=0,weight=0,d;

        d=directionalWorseDistance(reference.powerKw,candidate.powerKw,.30,false);
        if(d>=0){sum+=d*.20;weight+=.20;}

        d=directionalWorseDistance(reference.acc,candidate.acc,.25,true);
        if(d>=0){sum+=d*.20;weight+=.20;}

        d=directionalWorseDistance(reference.wltpKm,candidate.wltpKm,.25,false);
        if(d>=0){sum+=d*.25;weight+=.25;}

        d=directionalWorseDistance(reference.consumption,candidate.consumption,.25,true);
        if(d>=0){sum+=d*.15;weight+=.15;}

        d=directionalWorseDistance(reference.chargeMin,candidate.chargeMin,.50,true);
        if(d>=0){sum+=d*.20;weight+=.20;}

        return weight>0?Math.min(1,sum/weight):-1;
    }

    /**
     * Distancia direccional: solo mide cuánto peor es el candidato.
     *
     * lowerIsBetter=true para variables donde un valor menor es mejor
     * (0-100, consumo y tiempo de carga).
     * lowerIsBetter=false para variables donde un valor mayor es mejor
     * (potencia y autonomía).
     */
    private double directionalWorseDistance(double reference,double candidate,double tolerance,boolean lowerIsBetter){
        if(reference<=0||candidate<=0||tolerance<=0)return -1;

        double worseRatio;
        if(lowerIsBetter){
            worseRatio=Math.max(0,candidate-reference)/reference;
        }else{
            worseRatio=Math.max(0,reference-candidate)/reference;
        }

        return Math.tanh(worseRatio/tolerance);
    }

    private double similarity(Vehicle a,Vehicle b){
        double sum=0,weight=0,d;
        d=relativeDistance(a.wltpKm,b.wltpKm,.25); if(d>=0){sum+=d*.15;weight+=.15;}
        d=relativeDistance(a.batteryKwh,b.batteryKwh,.25); if(d>=0){sum+=d*.10;weight+=.10;}
        d=relativeDistance(a.consumption,b.consumption,.25); if(d>=0){sum+=d*.10;weight+=.10;}
        d=relativeDistance(a.powerKw,b.powerKw,.30); if(d>=0){sum+=d*.10;weight+=.10;}
        d=relativeDistance(a.trunk,b.trunk,.35); if(d>=0){sum+=d*.10;weight+=.10;}
        d=dimensionDistance(a,b); if(d>=0){sum+=d*.15;weight+=.15;}
        d=relativeDistance(a.acc,b.acc,.25); if(d>=0){sum+=d*.08;weight+=.08;}
        d=relativeDistance(a.dcKw,b.dcKw,.50); if(d>=0){sum+=d*.07;weight+=.07;}
        d=relativeDistance(a.chargeMin,b.chargeMin,.50); if(d>=0){sum+=d*.10;weight+=.10;}
        d=relativeDistance(a.price,b.price,.25); if(d>=0){sum+=d*.05;weight+=.05;}
        return weight>0?Math.min(1,sum/weight):Double.POSITIVE_INFINITY;
    }

    /**
     * Convierte una diferencia relativa en una distancia suave 0..1.
     * La tolerancia indica aproximadamente qué diferencia debe considerarse
     * una similitud media; diferencias mayores se saturan progresivamente.
     */
    /**
     * Pequeño refuerzo específico para la carga rápida DC.
     * Máximo 0,02 puntos de distancia técnica, equivalente a un máximo
     * aproximado de 0,56 puntos porcentuales en el ranking final (28% técnico).
     * La ventaja se basa en la cercanía de la potencia DC del candidato a la
     * del referente, de forma que un rival con mejor DC que otro pueda quedar
     * ligeramente favorecido sin alterar el peso principal de competencia.
     */
    /**
     * Bonificación del segundo filtro: premia a un competidor cuando ofrece
     * una ventaja objetiva sobre el coche de referencia.
     *
     * Solo se premia lo que es mejor para el comprador:
     * autonomía, carga rápida, potencia, aceleración, consumo, maletero y precio.
     * Las dimensiones no se consideran "mejores" por sí mismas.
     *
     * Máximo: 0,10 de distancia técnica = hasta 10 puntos porcentuales.
     * Así una ventaja puede desempatar o mejorar posiciones, pero nunca convierte
     * en competidor a un coche que no haya pasado la Fase 1.
     */
    private double referenceAdvantageBonus(Vehicle reference,Vehicle candidate){
        if(reference==null||candidate==null)return 0;

        double sum=0,weight=0,d;

        d=directionalAdvantage(reference.wltpKm,candidate.wltpKm,.25,false);
        if(d>=0){sum+=d*.25;weight+=.25;}

        d=directionalAdvantage(reference.dcKw,candidate.dcKw,.50,false);
        if(d>=0){sum+=d*.15;weight+=.15;}

        d=directionalAdvantage(reference.chargeMin,candidate.chargeMin,.50,true);
        if(d>=0){sum+=d*.15;weight+=.15;}

        d=directionalAdvantage(reference.powerKw,candidate.powerKw,.30,false);
        if(d>=0){sum+=d*.10;weight+=.10;}

        d=directionalAdvantage(reference.acc,candidate.acc,.25,true);
        if(d>=0){sum+=d*.10;weight+=.10;}

        d=directionalAdvantage(reference.consumption,candidate.consumption,.25,true);
        if(d>=0){sum+=d*.10;weight+=.10;}

        d=directionalAdvantage(reference.trunk,candidate.trunk,.35,false);
        if(d>=0){sum+=d*.05;weight+=.05;}

        d=directionalAdvantage(reference.price,candidate.price,.20,true);
        if(d>=0){sum+=d*.10;weight+=.10;}

        return weight>0?0.10*Math.min(1,sum/weight):0;
    }

    /**
     * Ventaja direccional normalizada: 0 si el candidato no es mejor,
     * y un valor creciente si sí lo es.
     */
    private double directionalAdvantage(double reference,double candidate,double tolerance,boolean lowerIsBetter){
        if(reference<=0||candidate<=0||tolerance<=0)return -1;
        double advantage;
        if(lowerIsBetter){
            advantage=Math.max(0,reference-candidate)/reference;
        }else{
            advantage=Math.max(0,candidate-reference)/reference;
        }
        return Math.tanh(advantage/tolerance);
    }

    private double dcChargingSimilarityBonus(Vehicle reference,Vehicle candidate){
        if(reference==null||candidate==null||reference.dcKw<=0||candidate.dcKw<=0)return 0;
        double max=Math.max(reference.dcKw,candidate.dcKw);
        if(max<=0)return 0;
        double difference=Math.abs(reference.dcKw-candidate.dcKw)/max;
        double closeness=Math.max(0,1-difference);
        return 0.02*closeness;
    }

    private double relativeDistance(double a,double b,double tolerance){
        if(a<=0||b<=0||tolerance<=0)return -1;
        double reference=Math.max(a,b);
        double ratio=Math.abs(a-b)/reference;

        // Distancia simétrica y continua:
        // - diferencias pequeñas penalizan poco;
        // - 27, 35 y 40 min siguen produciendo valores distintos;
        // - la función no se satura bruscamente en 1 para diferencias finitas.
        //
        // Usamos una curva hiperbólica (tanh) para conservar información
        // también cuando la diferencia supera la tolerancia.
        double normalized=ratio/tolerance;
        return Math.tanh(normalized);
    }

    /**
     * SUV y crossover se consideran equivalentes para la similitud:
     * son carrocerías muy próximas y, en este contexto, rivales directos.
     */
    private double bodyStyleDistance(Vehicle a,Vehicle b){
        String x=normalizeBodyStyle(a.bodyStyle), y=normalizeBodyStyle(b.bodyStyle);
        if(x.isEmpty()||y.isEmpty())return -1;
        if(x.equals(y))return 0;
        if((x.equals("suv")&&y.equals("crossover"))||(x.equals("crossover")&&y.equals("suv")))return 0;
        return 1;
    }

    private String normalizeBodyStyle(String s){
        String x=s==null?"":s.trim().toLowerCase(Locale.ROOT);
        return x.equals("suv")||x.equals("crossover")?x:x;
    }

    /**
     * El segmento aporta contexto, pero con poco peso. Para SUV/crossover
     * permitimos diferencias de letra del catálogo Gaia sin convertirlas
     * en una penalización fuerte (p.ej. G6 C frente a Model Y J).
     */
    private double segmentDistance(Vehicle a,Vehicle b){
        String x=a.segment==null?"":a.segment.trim().toUpperCase(Locale.ROOT);
        String y=b.segment==null?"":b.segment.trim().toUpperCase(Locale.ROOT);
        if(x.isEmpty()||y.isEmpty())return -1;
        if(x.equals(y))return 0;
        if(isSuvLike(a.bodyStyle)&&isSuvLike(b.bodyStyle))return .20;
        return 1;
    }

    private boolean isSuvLike(String s){
        String x=normalizeBodyStyle(s);
        return x.equals("suv")||x.equals("crossover");
    }

    private double dimensionDistance(Vehicle a,Vehicle b){
        double sum=0,weight=0,d;
        d=relativeDistance(a.lengthMm,b.lengthMm,.08); if(d>=0){sum+=d*.50;weight+=.50;}
        d=relativeDistance(a.widthMm,b.widthMm,.05); if(d>=0){sum+=d*.20;weight+=.20;}
        d=relativeDistance(a.heightMm,b.heightMm,.05); if(d>=0){sum+=d*.30;weight+=.30;}
        return weight>0?Math.min(1,sum/weight):-1;
    }

    /**
     * Determina si dos vehículos pertenecen a una zona económica competitiva.
     *
     * El precio no es un criterio adicional con peso: actúa como filtro previo.
     * Se permite una diferencia máxima del 20% porque el precio por sí solo no
     * debe decidir la similitud técnica. La combinación con carrocería y clase
     * física ya se comprueba en sameVehicleClass().
     *
     * Ejemplos para un G6 de 47.083 €:
     * - Model Y de 50.990 € -> 8,3% -> entra.
     * - ID.4 de 46.900 € -> 0,4% -> entra.
     * - ID.5 de 52.790 € -> 12,1% -> entra.
     * - Un SUV equivalente de 60.000 € -> 27,4% -> queda fuera.
     */
    private boolean inCompetitiveZone(Vehicle a,Vehicle b){
        return inCompetitiveZone(a,b,15.0);
    }

    private boolean inCompetitiveZone(Vehicle a,Vehicle b,double maxPricePercent){
        if(a==null||b==null)return false;

        // Zona competitiva estricta: no basta con parecerse técnicamente.
        // Debe pertenecer al mismo espacio comercial y físico que el referente.
        String ba=normalizeBodyStyle(a.bodyStyle);
        String bb=normalizeBodyStyle(b.bodyStyle);
        if(ba.isEmpty()||bb.isEmpty())return false;
        if(!ba.equals(bb)&&!(isSuvLike(ba)&&isSuvLike(bb)))return false;

        // El tamaño es una condición de entrada: evitamos que un SUV claramente
        // más pequeño o más grande entre solo por tener especificaciones parecidas.
        if(!samePhysicalClass(a,b))return false;

        // Con carrocería y tamaño compatibles, permitimos el salto de código de
        // segmento que existe en algunos catálogos (p.ej. C/D/J para SUV).
        String sa=a.segment==null?"":a.segment.trim().toUpperCase(Locale.ROOT);
        String sb=b.segment==null?"":b.segment.trim().toUpperCase(Locale.ROOT);
        if(!sa.isEmpty()&&!sb.isEmpty()&&!sa.equals(sb)){
            if(!isSuvLike(ba)||!isSuvLike(bb))return false;
        }

        // Finalmente, precio: filtro económico configurable.
        // En el primer cribado es 15%; solo se amplía si faltan 8 marcas.
        if(a.price>0&&b.price>0){
            double reference=Math.max(a.price,b.price);
            double priceRatio=Math.abs(a.price-b.price)/reference;
            if(priceRatio>(maxPricePercent/100.0))return false;
        }

        return true;
    }

    /**
     * Compatibilidad histórica mantenida para cualquier llamada futura.
     * La lógica activa de candidatos usa los filtros escalonados de showSimilar().
     */
    private boolean sameVehicleClass(Vehicle a,Vehicle b){
        return sameBodyStyle(a,b)
            &&samePhysicalClass(a,b,8.0,5.0,8.0)
            &&sameSegmentCompatible(a,b);
    }

    private boolean samePhysicalClass(Vehicle a,Vehicle b){
        return samePhysicalClass(a,b,8.0,5.0,8.0);
    }

    private void loadVehicles(){
        try(InputStream in=getAssets().open("catalog_es_2024_2026.json");BufferedReader r=new BufferedReader(new InputStreamReader(in))){
            StringBuilder sb=new StringBuilder();String line;while((line=r.readLine())!=null)sb.append(line);
            JSONArray a=new JSONObject(sb.toString()).optJSONArray("vehicles");if(a==null)throw new IllegalStateException("vehicles missing");
            for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o!=null)vehicles.add(new Vehicle(o,false));}
        }catch(Exception e){throw new IllegalStateException("No se ha podido cargar el catálogo",e);}
        Set<String> keys=new HashSet<>();for(Vehicle v:vehicles)keys.add(logicalKey(v));
        JSONArray remote=RemoteCatalogManager.loadCached(this);
        addRemote(remote,keys);
        RemoteCatalogManager.refreshIfDue(this,additions->{if(additions==null)return;int before=vehicles.size();addRemote(additions,keys);if(vehicles.size()!=before)showReferenceCandidates();});
        Collections.sort(vehicles,(a,b)->{int c=a.make.compareToIgnoreCase(b.make);if(c!=0)return c;return a.model.compareToIgnoreCase(b.model);});
    }
    private void addRemote(JSONArray a,Set<String> keys){
        if(a==null)return;
        for(int i=0;i<a.length();i++)try{
            JSONObject o=a.optJSONObject(i);if(o==null)continue;Vehicle v=new Vehicle(o,true);
            if(v.year<2024||v.year>2026||v.make.isEmpty()||v.model.isEmpty()||v.version.isEmpty()||v.batteryKwh<=0||v.wltpKm<=0||v.powerKw<=0||v.consumption<=0)continue;
            if(v.consumption<8||v.consumption>35||v.wltpKm>1000||v.usableBatteryKwh>v.batteryKwh+.5)continue;
            if(keys.add(logicalKey(v)))vehicles.add(v);
        }catch(Exception ignored){}
    }

    private String effectiveBatteryKey(Vehicle v){
        if(v.batteryKwh>0)return String.format(Locale.US,"%.1f",v.batteryKwh);
        String s=v.version==null?"":v.version.trim();
        java.util.regex.Matcher m=java.util.regex.Pattern.compile("^(\\d+(?:\\.\\d+)?)\\s*kwh\\b",java.util.regex.Pattern.CASE_INSENSITIVE).matcher(s);
        return m.find()?m.group(1):"0";
    }
    private String normalizedVersion(Vehicle v){
        String ver=v.version==null?"":v.version.trim().toLowerCase(Locale.ROOT);
        ver=ver.replaceAll("^\\d+(?:\\.\\d+)?\\s*kwh\\s*","");
        ver=ver.replaceAll("\\s+"," ").trim();
        return ver;
    }
    private String logicalKey(Vehicle v){return(v.make+"|"+v.model+"|"+v.market+"|"+v.year+"|"+effectiveBatteryKey(v)+"|"+normalizedVersion(v)).trim().toLowerCase(Locale.ROOT);}
    private String norm(String s){return java.text.Normalizer.normalize(s==null?"":s,java.text.Normalizer.Form.NFD).replaceAll("\\p{M}","").toLowerCase(Locale.ROOT).trim();}
    private String market(String s){return s==null||s.isEmpty()?"🌐":s;}
    private String specLine(Vehicle v){String bat=v.batteryKwh>0?fmt(v.batteryKwh)+" kWh":"—";String range=v.wltpKm>0?fmt(v.wltpKm)+" km":"—";String p=v.powerKw>0?fmtCv(v.powerKw*1.35962)+" CV":"—";return bat+"  ·  "+range+"  ·  "+p;}
    private String cleanVersion(String version){String s=version==null?"":version.trim();s=s.replaceAll("(?i)\\b\\d+(?:[.,]\\d+)?\\s*kwh\\b","");s=s.replaceAll("(?i)\\b\\d+(?:[.,]\\d+)?\\s*kw\\b","");s=s.replaceAll("\\s{2,}"," ").replaceAll("\\s*[·-]\\s*$","").trim();return s;}
    private String fmt(double n){NumberFormat f=NumberFormat.getNumberInstance(Locale.forLanguageTag(LanguageManager.getEffectiveLanguage(this)));f.setMaximumFractionDigits(1);return f.format(n);} private String fmtCv(double n){NumberFormat f=NumberFormat.getNumberInstance(Locale.forLanguageTag(LanguageManager.getEffectiveLanguage(this)));f.setMaximumFractionDigits(0);f.setRoundingMode(java.math.RoundingMode.HALF_UP);return f.format(n);}

    private void addSimilarToComparison(Vehicle v){
        if(v==null||reference==null)return;
        SharedPreferences p=getSharedPreferences(PREFS,MODE_PRIVATE);
        ArrayList<String> ids=new ArrayList<>();
        ArrayList<String> keys=new ArrayList<>();
        if(reference.id!=null&&!reference.id.trim().isEmpty())ids.add(reference.id.trim());
        if(v.id!=null&&!v.id.trim().isEmpty()&&!ids.contains(v.id.trim()))ids.add(v.id.trim());
        String refKey=logicalKey(reference);
        String selectedKey=logicalKey(v);
        if(!refKey.isEmpty())keys.add(refKey);
        if(!selectedKey.isEmpty()&&!keys.contains(selectedKey))keys.add(selectedKey);
        p.edit().putBoolean("compare_selection_initialized",true)
            .putString("compare_vehicle_ids_ordered",android.text.TextUtils.join(",",ids))
            .putStringSet("compare_vehicle_ids",new LinkedHashSet<>(ids))
            .putString("compare_vehicle_logical_ordered",android.text.TextUtils.join("||",keys)).apply();
        Intent back=new Intent(this,CompararCochesActivity.class);
        back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(back);
    }

    private void openDetail(Vehicle v){
        Intent i=new Intent(this,CarDetailActivity.class);
        i.putExtra("vehicle_id",v.id);
        i.putExtra("logical_key",logicalKey(v));
        i.putExtra("similar_flow",true);
        i.putExtra("reference_vehicle_id",reference==null?null:reference.id);
        i.putExtra("reference_logical_key",reference==null?null:logicalKey(reference));
        i.putExtra("make",v.make);i.putExtra("model",v.model);i.putExtra("version",v.version);i.putExtra("market",v.market);i.putExtra("year",String.valueOf(v.year));
        i.putExtra("price",v.price);i.putExtra("batteryKwh",v.batteryKwh);i.putExtra("usableBatteryKwh",v.usableBatteryKwh);i.putExtra("batteryType",v.batteryType);i.putExtra("wltpKm",v.wltpKm);i.putExtra("consumption",v.consumption);i.putExtra("powerKw",v.powerKw);i.putExtra("drivetrain",v.drivetrain);i.putExtra("acKw",v.acKw);i.putExtra("dcKw",v.dcKw);i.putExtra("chargeMin",v.chargeMin);i.putExtra("acc",v.acc);i.putExtra("trunk",v.trunk);i.putExtra("weight",v.weight);i.putExtra("lengthMm",v.lengthMm);i.putExtra("widthMm",v.widthMm);i.putExtra("heightMm",v.heightMm);i.putExtra("remoteSource",v.remoteSource);
        startActivity(i);
    }

    static class Scored{Vehicle v;double competitionScore;double technicalScore;Scored(Vehicle v,double competitionScore,double technicalScore){this.v=v;this.competitionScore=competitionScore;this.technicalScore=technicalScore;}}
    static class Vehicle{
        String id,make,model,version,batteryType,drivetrain,market,bodyStyle,segment;int year;boolean remoteSource;
        double price,batteryKwh,usableBatteryKwh,wltpKm,consumption,powerKw,acKw,dcKw,chargeMin,acc,trunk,weight,lengthMm,widthMm,heightMm;
        Vehicle(JSONObject o,boolean remote){
            remoteSource=remote;make=o.optString("make",o.optString("brand",""));model=o.optString("model","");version=o.optString("version",o.optString("trim",""));
            batteryType=o.optString("batteryChemistry",o.optString("batteryType",""));drivetrain=o.optString("drivetrain",o.optString("drive",""));market=o.optString("market","ES").toUpperCase(Locale.ROOT);
            bodyStyle=o.optString("bodyStyle",o.optString("body_style","")).trim().toLowerCase(Locale.ROOT);
            segment=o.optString("segment","").trim().toUpperCase(Locale.ROOT);
            year=o.optInt("year",o.optInt("modelYear",0));price=o.optDouble("price",0);batteryKwh=o.optDouble("batteryKwh",o.optDouble("battery_capacity_kwh",0));usableBatteryKwh=o.optDouble("usableBatteryKwh",0);
            wltpKm=o.optDouble("wltpKm",o.optDouble("rangeKm",0));consumption=o.optDouble("consumption",o.optDouble("consumptionKwh100",0));powerKw=o.optDouble("powerKw",o.optDouble("power_kW",0));
            acKw=o.optDouble("acKw",o.optDouble("acChargeKw",0));dcKw=o.optDouble("dcKw",o.optDouble("dcChargeKw",0));chargeMin=o.optDouble("charge10to80Min",o.optDouble("chargeMin",0));
            acc=o.optDouble("acceleration0to100Sec",o.optDouble("acc",0));trunk=o.optDouble("trunkLiters",o.optDouble("trunk",0));weight=o.optDouble("weightKg",o.optDouble("weight",0));
            lengthMm=o.optDouble("lengthMm",o.optDouble("length",0));widthMm=o.optDouble("widthMm",o.optDouble("width",0));heightMm=o.optDouble("heightMm",o.optDouble("height",0));
            id=o.optString("id","");if(id.isEmpty())id="catalog-"+Integer.toHexString(logicalHash());
        }
        private int logicalHash(){return(make+"|"+model+"|"+market+"|"+year+"|"+batteryKwh+"|"+version).toLowerCase(Locale.ROOT).hashCode();}
    }
}