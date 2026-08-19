package com.evchargecalculator;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

public class PremiumBackgroundView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private boolean dark = true;
    public PremiumBackgroundView(Context c, AttributeSet a) { super(c,a); setLayerType(View.LAYER_TYPE_SOFTWARE,null); }
    public PremiumBackgroundView(Context c) { this(c,null); }
    public void setDark(boolean d){ dark=d; invalidate(); }

    @Override protected void onDraw(Canvas c) {
        super.onDraw(c);
        float w=getWidth(), h=getHeight();
        p.setStyle(Paint.Style.FILL);
        int bg1=dark?Color.rgb(5,8,14):Color.rgb(235,242,248);
        int bg2=dark?Color.rgb(6,18,28):Color.rgb(249,252,255);
        LinearGradient grad=new LinearGradient(0,0,w,h,bg1,bg2,Shader.TileMode.CLAMP); p.setShader(grad); c.drawRect(0,0,w,h,p); p.setShader(null);

        if(dark){
            p.setColor(Color.argb(42,54,184,255)); p.setMaskFilter(new BlurMaskFilter(90,BlurMaskFilter.Blur.NORMAL)); c.drawCircle(w*.72f,h*.12f,150,p);
            p.setColor(Color.argb(30,77,225,193)); c.drawCircle(w*.18f,h*.35f,130,p);
        } else {
            p.setColor(Color.argb(34,18,130,205)); p.setMaskFilter(new BlurMaskFilter(90,BlurMaskFilter.Blur.NORMAL)); c.drawCircle(w*.72f,h*.12f,150,p);
            p.setColor(Color.argb(24,77,190,170)); c.drawCircle(w*.18f,h*.35f,130,p);
        }
        p.setMaskFilter(null);

        p.setColor(dark?Color.argb(32,180,205,230):Color.argb(45,80,105,130)); c.drawRect(0,h*.72f,w,h*.725f,p);

        float y=h*.67f, x=w*.55f, cw=w*.42f;
        p.setColor(dark?Color.argb(40,145,180,215):Color.argb(35,90,120,150));
        path.reset(); path.moveTo(x-cw*.5f,y); path.lineTo(x-cw*.38f,y-cw*.18f); path.quadTo(x-cw*.24f,y-cw*.30f,x-cw*.02f,y-cw*.30f);
        path.lineTo(x+cw*.20f,y-cw*.28f); path.quadTo(x+cw*.38f,y-cw*.23f,x+cw*.5f,y); path.close(); c.drawPath(path,p);
        p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(3); p.setColor(dark?Color.argb(70,180,215,240):Color.argb(70,70,100,130)); c.drawPath(path,p); p.setStyle(Paint.Style.FILL);
        p.setColor(dark?Color.argb(65,54,184,255):Color.argb(50,18,130,205)); c.drawCircle(x-cw*.30f,y+2,27,p); c.drawCircle(x+cw*.30f,y+2,27,p);
        p.setColor(dark?Color.argb(75,54,184,255):Color.argb(55,18,130,205)); c.drawRoundRect(w*.08f,h*.54f,w*.13f,h*.72f,10,10,p);
        p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(5); p.setColor(dark?Color.argb(55,54,184,255):Color.argb(50,18,130,205));
        path.reset(); path.moveTo(w*.13f,h*.62f); path.cubicTo(w*.22f,h*.52f,w*.31f,h*.60f,w*.34f,h*.67f); c.drawPath(path,p); p.setStyle(Paint.Style.FILL);
    }
}
