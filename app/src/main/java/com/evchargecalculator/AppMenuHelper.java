package com.evchargecalculator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.PopupMenu;

/** Common top-right app menu used on every app screen. */
public final class AppMenuHelper {
    private static final String PLAY_STORE_URL="https://play.google.com/store/apps/details?id=com.evcalculatorpro";
    public interface Listener { boolean isDark(); void setDark(boolean dark); }
    private AppMenuHelper() {}
    public static void show(Activity activity, View anchor, Listener listener) {
        PopupMenu popup=new PopupMenu(activity,anchor);
        popup.getMenu().add(0,10,1,"🌐  "+LanguageManager.t(activity,"Idioma"));
        String themeKey=listener.isDark()?"Cambiar a tema claro":"Cambiar a tema oscuro";
        popup.getMenu().add(0,1,2,(listener.isDark()?"☀  ":"☾  ")+LanguageManager.t(activity,themeKey));
        popup.getMenu().add(0,2,3,"↗  "+LanguageManager.t(activity,"Compartir app"));
        popup.getMenu().add(0,3,4,"★  "+LanguageManager.t(activity,"Calificar app"));
        popup.setOnMenuItemClickListener(item->{
            if(item.getItemId()==10){LanguageManager.showSelector(activity);return true;}
            if(item.getItemId()==1){listener.setDark(!listener.isDark());return true;}
            if(item.getItemId()==2){Intent share=new Intent(Intent.ACTION_SEND);share.setType("text/plain");share.putExtra(Intent.EXTRA_TEXT,LanguageManager.t(activity,"Descarga EV Calculator PRO en Google Play: ")+PLAY_STORE_URL);activity.startActivity(Intent.createChooser(share,LanguageManager.t(activity,"Compartir app")));return true;}
            if(item.getItemId()==3){activity.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(PLAY_STORE_URL)));return true;}
            return false;
        });popup.show();
    }
}