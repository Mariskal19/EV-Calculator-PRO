package com.evchargecalculator;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

public class PremiumBackgroundView extends View {
    private boolean darkMode = true;
    public void setDarkMode(boolean dark) { darkMode = dark; invalidate(); }
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    public PremiumBackgroundView(Context c, AttributeSet a) { super(c,a); setLayerType(View.LAYER_TYPE_SOFTWARE,null); }
    public PremiumBackgroundView(Context c) { this(c,null); }
    @Override protected void onDraw(Canvas c) {
        super.onDraw(c);
        float w=getWidth(), h=getHeight();
        p.setStyle(Paint.Style.FILL);
        LinearGradient bg=new LinearGradient(0,0,w,h,darkMode?Color.rgb(5,8,14):Color.rgb(241,246,251),darkMode?Color.rgb(6,18,28):Color.rgb(218,231,242),Shader.TileMode.CLAMP); p.setShader(bg); c.drawRect(0,0,w,h,p); p.setShader(null);
        p.setColor(Color.argb(darkMode?42:28,54,184,255)); p.setMaskFilter(new BlurMaskFilter(90,BlurMaskFilter.Blur.NORMAL)); c.drawCircle(w*.72f,h*.12f,150,p);
        p.setColor(Color.argb(darkMode?30:22,77,225,193)); c.drawCircle(w*.18f,h*.35f,130,p); p.setMaskFilter(null);
        // subtle road / floor
        p.setColor(Color.argb(darkMode?32:55,120,145,170)); c.drawRect(0,h*.72f,w,h*.725f,p);
        // premium EV silhouette
        float y=h*.67f, x=w*.55f, cw=w*.42f;
        p.setColor(Color.argb(darkMode?40:28,70,105,135));
        path.reset(); path.moveTo(x-cw*.5f,y); path.lineTo(x-cw*.38f,y-cw*.18f); path.quadTo(x-cw*.24f,y-cw*.30f,x-cw*.02f,y-cw*.30f); path.lineTo(x+cw*.20f,y-cw*.28f); path.quadTo(x+cw*.38f,y-cw*.23f,x+cw*.5f,y); path.close(); c.drawPath(path,p);
        p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(3); p.setColor(Color.argb(70,180,215,240)); c.drawPath(path,p); p.setStyle(Paint.Style.FILL);
        p.setColor(Color.argb(65,54,184,255)); c.drawCircle(x-cw*.30f,y+2,27,p); c.drawCircle(x+cw*.30f,y+2,27,p);
        // charger and cable glow
        p.setColor(Color.argb(75,54,184,255)); c.drawRoundRect(w*.08f,h*.54f,w*.13f,h*.72f,10,10,p);
        p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(5); p.setColor(Color.argb(55,54,184,255)); path.reset(); path.moveTo(w*.13f,h*.62f); path.cubicTo(w*.22f,h*.52f,w*.31f,h*.60f,w*.34f,h*.67f); c.drawPath(path,p); p.setStyle(Paint.Style.FILL);
    }
}
