package com.evchargecalculator;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class AppMenuApplication extends Application {
    private static final String PREFS="ev_charge_calculator", KEY_DARK_THEME="dark_theme";
    private static final int MENU_ID=0x7ECAFE;
    @Override public void onCreate(){super.onCreate();LanguageManager.applyStored(this);registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks(){
        @Override public void onActivityCreated(Activity activity,Bundle state){activity.getWindow().getDecorView().post(()->{LanguageManager.applyStored(activity);protectSystemBars(activity);if(activity instanceof MainActivity){installMenu(activity);bindChargeMenu(activity);}LanguageManager.translateViews(activity);});}
        @Override public void onActivityStarted(Activity activity){}
        @Override public void onActivityResumed(Activity activity){activity.getWindow().getDecorView().post(()->{protectSystemBars(activity);LanguageManager.translateViews(activity);});}
        @Override public void onActivityPaused(Activity activity){}
        @Override public void onActivityStopped(Activity activity){}
        @Override public void onActivitySaveInstanceState(Activity activity,Bundle outState){}
        @Override public void onActivityDestroyed(Activity activity){}
    });}
    private void protectSystemBars(Activity activity){
        if(Build.VERSION.SDK_INT<35)return;
        ViewGroup content=activity.findViewById(android.R.id.content);
        if(content==null)return;
        protectScrollViews(content);
    }
    private void protectScrollViews(View view){
        if(view instanceof ScrollView){
            ScrollView scroll=(ScrollView)view;
            if(scroll.getTag()==null){
                int left=scroll.getPaddingLeft(),top=scroll.getPaddingTop(),right=scroll.getPaddingRight(),bottom=scroll.getPaddingBottom();
                scroll.setTag(new int[]{left,top,right,bottom});
                scroll.setOnApplyWindowInsetsListener((v,insets)->{
                    WindowInsets.Type.InsetsTypeMask ignored=WindowInsets.Type.systemBars();
                    android.graphics.Insets bars=insets.getInsets(WindowInsets.Type.systemBars());
                    int[] base=(int[])v.getTag();
                    v.setPadding(base[0],base[1]+bars.top,base[2],base[3]+bars.bottom);
                    return insets;
                });
                scroll.requestApplyInsets();
            }
        }
        if(view instanceof ViewGroup){
            ViewGroup group=(ViewGroup)view;
            for(int i=0;i<group.getChildCount();i++)protectScrollViews(group.getChildAt(i));
        }
    }
    private void installMenu(Activity activity){
        ViewGroup content=activity.findViewById(android.R.id.content);if(content==null)return;
        if(content.findViewById(MENU_ID)!=null)return;
        removeLegacyMenus(content);
        TextView menu=new TextView(activity);menu.setId(MENU_ID);menu.setText("⋮");menu.setTextSize(30);menu.setTextColor(Color.WHITE);menu.setGravity(Gravity.CENTER);menu.setIncludeFontPadding(false);menu.setContentDescription(LanguageManager.t(activity,"Menú de la aplicación"));menu.setShadowLayer(dp(activity,4),0,dp(activity,2),Color.argb(90,0,0,0));menu.setBackgroundColor(Color.TRANSPARENT);menu.setOnClickListener(v->showMenu(activity,menu));
        FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(dp(activity,40),dp(activity,40),Gravity.TOP|Gravity.END);lp.rightMargin=dp(activity,14);lp.topMargin=dp(activity,12);
        content.addView(menu,lp);
    }
    private void bindChargeMenu(Activity activity){
        ViewGroup content=activity.findViewById(android.R.id.content);if(content==null)return;
        TextView button=findRightBackButton(content);
        View global=content.findViewById(MENU_ID);
        if(button==null)return;
        if(global!=null)global.setVisibility(View.GONE);
        button.setText("⋮");button.setTextSize(30);button.setContentDescription(LanguageManager.t(activity,"Menú de la aplicación"));button.setOnClickListener(v->showMenu(activity,button));
    }
    /**
     * Charge has two arrow buttons in its header. The right-hand arrow is the
     * menu anchor. We deliberately identify it by its position rather than by
     * contentDescription, because contentDescription is translated and can
     * therefore change after the language selector recreates the Activity.
     */
    private TextView findRightBackButton(ViewGroup parent){
        TextView result=null;
        int bestX=Integer.MIN_VALUE;
        for(int i=0;i<parent.getChildCount();i++){
            View child=parent.getChildAt(i);
            if(child instanceof TextView){
                TextView t=(TextView)child;
                CharSequence text=t.getText();
                if("←".contentEquals(text)){
                    int[] loc=new int[2];t.getLocationOnScreen(loc);
                    if(loc[0]>bestX){bestX=loc[0];result=t;}
                }
            }
            if(child instanceof ViewGroup){
                TextView found=findRightBackButton((ViewGroup)child);
                if(found!=null){
                    int[] loc=new int[2];found.getLocationOnScreen(loc);
                    if(loc[0]>bestX){bestX=loc[0];result=found;}
                }
            }
        }
        return result;
    }
    private void removeLegacyMenus(ViewGroup parent){for(int i=parent.getChildCount()-1;i>=0;i--){View child=parent.getChildAt(i);CharSequence d=child.getContentDescription();if(d!=null&&(LanguageManager.t((Context)parent.getContext(),"Menú de la aplicación").contentEquals(d)||"Cambiar tema".contentEquals(d)||"Tema claro".contentEquals(d)||"Tema oscuro".contentEquals(d)||"Más opciones".contentEquals(d))){parent.removeViewAt(i);}else if(child instanceof ViewGroup)removeLegacyMenus((ViewGroup)child);}}
    private void showMenu(Activity activity,View anchor){SharedPreferences prefs=activity.getSharedPreferences(PREFS,Context.MODE_PRIVATE);AppMenuHelper.show(activity,anchor,new AppMenuHelper.Listener(){@Override public boolean isDark(){return prefs.getBoolean(KEY_DARK_THEME,false);}@Override public void setDark(boolean value){prefs.edit().putBoolean(KEY_DARK_THEME,value).apply();activity.recreate();}});}
    private static int dp(Activity a,int n){return(int)(n*a.getResources().getDisplayMetrics().density+.5f);}
}