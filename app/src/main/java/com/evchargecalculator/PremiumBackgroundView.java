package com.evchargecalculator;

import android.content.Context;
import android.graphics.*;
import android.view.View;

/**
 * Decorative background for the calculator.  The original V1.0.5 palette is
 * deliberately preserved; this view only adds the illustrated EV scene from
 * the new visual concept (blue car, charging posts, battery and energy icons).
 */
public class PremiumBackgroundView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private boolean dark = false;

    private final int cobalt = Color.rgb(27, 139, 220);
    private final int green = Color.rgb(50, 190, 155);

    public PremiumBackgroundView(Context c) {
        super(c);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setDark(boolean d) { dark = d; invalidate(); }

    @Override protected void onDraw(Canvas c) {
        super.onDraw(c);
        float w = getWidth(), h = getHeight();

        // Clean cobalt/light-blue base inspired by the supplied light-theme mockup.
        int top = dark ? Color.rgb(7, 15, 25) : Color.rgb(239, 247, 253);
        int bottom = dark ? Color.rgb(11, 27, 43) : Color.rgb(215, 232, 244);
        p.setStyle(Paint.Style.FILL);
        p.setShader(new LinearGradient(0, 0, w, h, top, bottom, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, w, h, p);
        p.setShader(null);

        // Soft atmospheric light.
        p.setMaskFilter(new BlurMaskFilter(dp(70), BlurMaskFilter.Blur.NORMAL));
        p.setColor(dark ? Color.argb(42, 27, 139, 220) : Color.argb(45, 27, 139, 220));
        c.drawCircle(w * .78f, h * .11f, dp(115), p);
        p.setColor(dark ? Color.argb(28, 50, 190, 155) : Color.argb(28, 50, 190, 155));
        c.drawCircle(w * .13f, h * .31f, dp(95), p);
        p.setMaskFilter(null);

        // Very subtle skyline / charging infrastructure in the upper half.
        drawSkyline(c, w, h);
        drawChargingPost(c, w * .86f, h * .27f, .95f);
        drawChargingPost(c, w * .12f, h * .39f, .62f);

        // Energy symbols floating around the scene.
        drawLightning(c, w * .77f, h * .19f, dp(18), dark ? Color.argb(120, 54, 184, 255) : Color.argb(115, 27, 139, 220));
        drawPlug(c, w * .19f, h * .20f, dp(15));
        drawBattery(c, w * .86f, h * .48f, dp(30));

        // Main blue EV illustration, kept behind the cards.
        drawCar(c, w * .50f, h * .61f, Math.min(w, dp(430)), dark);

        // Ground shadow and subtle road/charging lines.
        p.setColor(dark ? Color.argb(38, 170, 205, 230) : Color.argb(34, 60, 100, 130));
        c.drawRect(0, h * .665f, w, h * .668f, p);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(dp(2));
        p.setColor(dark ? Color.argb(55, 27, 139, 220) : Color.argb(48, 27, 139, 220));
        path.reset();
        path.moveTo(w * .04f, h * .78f);
        path.cubicTo(w * .27f, h * .69f, w * .62f, h * .73f, w * .96f, h * .67f);
        c.drawPath(path, p);
        p.setStyle(Paint.Style.FILL);
    }

    private void drawSkyline(Canvas c, float w, float h) {
        int col = dark ? Color.argb(24, 125, 190, 230) : Color.argb(30, 92, 145, 180);
        p.setColor(col);
        float base = h * .39f;
        float[] xs = {.56f,.61f,.65f,.69f,.73f,.77f,.81f,.85f,.89f,.93f};
        float[] hs = {.12f,.17f,.10f,.21f,.14f,.25f,.16f,.20f,.11f,.18f};
        for (int i=0;i<xs.length;i++) {
            float x=w*xs[i], bw=w*.045f, top=h*(.39f-hs[i]);
            c.drawRoundRect(x-bw/2, top, x+bw/2, base, dp(3), dp(3), p);
            p.setColor(dark ? Color.argb(25, 170, 215, 240) : Color.argb(24, 27, 139, 220));
            for (int r=0;r<3;r++) c.drawRect(x-bw*.22f, top+dp(7)+r*dp(9), x+bw*.22f, top+dp(9)+r*dp(9), p);
            p.setColor(col);
        }
    }

    private void drawChargingPost(Canvas c, float x, float y, float scale) {
        float s=scale;
        int body = dark ? Color.argb(105, 25, 48, 70) : Color.argb(105, 244, 249, 253);
        int edge = dark ? Color.argb(90, 92, 150, 190) : Color.argb(90, 85, 125, 155);
        p.setStyle(Paint.Style.FILL); p.setColor(body);
        c.drawRoundRect(x-dp(14)*s,y-dp(43)*s,x+dp(14)*s,y+dp(43)*s,dp(8)*s,dp(8)*s,p);
        p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(dp(2)*s); p.setColor(edge);
        c.drawRoundRect(x-dp(14)*s,y-dp(43)*s,x+dp(14)*s,y+dp(43)*s,dp(8)*s,dp(8)*s,p);
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.argb(150,27,139,220));
        c.drawRoundRect(x-dp(7)*s,y-dp(32)*s,x+dp(7)*s,y-dp(6)*s,dp(4)*s,dp(4)*s,p);
        drawLightning(c,x,y-dp(19)*s,dp(8)*s,Color.argb(210,255,255,255));
        p.setColor(Color.argb(110,27,139,220)); c.drawCircle(x,y+dp(28)*s,dp(3)*s,p);
    }

    private void drawCar(Canvas c, float cx, float cy, float width, boolean darkMode) {
        float s = width / dp(300);
        float x=cx, y=cy;
        // Shadow
        p.setColor(darkMode ? Color.argb(55,0,0,0) : Color.argb(45,55,80,100));
        p.setMaskFilter(new BlurMaskFilter(dp(10), BlurMaskFilter.Blur.NORMAL));
        c.drawOval(x-width*.36f,y+dp(22)*s,x+width*.36f,y+dp(38)*s,p); p.setMaskFilter(null);

        // Body
        p.setStyle(Paint.Style.FILL);
        p.setColor(cobalt);
        path.reset();
        path.moveTo(x-width*.43f,y+dp(12)*s);
        path.quadTo(x-width*.46f,y-dp(5)*s,x-width*.34f,y-dp(10)*s);
        path.lineTo(x-width*.22f,y-dp(48)*s);
        path.quadTo(x-width*.15f,y-dp(70)*s,x+width*.08f,y-dp(72)*s);
        path.lineTo(x+width*.25f,y-dp(62)*s);
        path.quadTo(x+width*.36f,y-dp(52)*s,x+width*.41f,y-dp(17)*s);
        path.quadTo(x+width*.48f,y-dp(8)*s,x+width*.43f,y+dp(12)*s);
        path.close(); c.drawPath(path,p);

        // Glass
        p.setColor(darkMode ? Color.rgb(34,67,94) : Color.rgb(69,125,164));
        path.reset();
        path.moveTo(x-width*.18f,y-dp(47)*s);
        path.quadTo(x-width*.11f,y-dp(63)*s,x+width*.05f,y-dp(64)*s);
        path.lineTo(x+width*.20f,y-dp(56)*s);
        path.quadTo(x+width*.27f,y-dp(49)*s,x+width*.30f,y-dp(35)*s);
        path.lineTo(x-width*.17f,y-dp(35)*s); path.close(); c.drawPath(path,p);
        p.setColor(Color.argb(55,255,255,255));
        path.reset(); path.moveTo(x-width*.16f,y-dp(58)*s); path.lineTo(x-width*.04f,y-dp(58)*s); path.lineTo(x-width*.16f,y-dp(40)*s); path.close(); c.drawPath(path,p);

        // Details
        p.setColor(Color.argb(180,255,255,255)); c.drawRoundRect(x-width*.32f,y-dp(2)*s,x-width*.16f,y+dp(2)*s,dp(2),dp(2),p);
        p.setColor(Color.argb(220,245,252,255)); c.drawCircle(x-width*.30f,y+dp(13)*s,dp(12)*s,p); c.drawCircle(x+width*.30f,y+dp(13)*s,dp(12)*s,p);
        p.setColor(Color.rgb(36,50,65)); c.drawCircle(x-width*.30f,y+dp(13)*s,dp(6)*s,p); c.drawCircle(x+width*.30f,y+dp(13)*s,dp(6)*s,p);
        p.setColor(Color.argb(220,255,255,255)); c.drawRoundRect(x+width*.31f,y-dp(1)*s,x+width*.40f,y+dp(5)*s,dp(2),dp(2),p);
        p.setColor(Color.argb(190,50,190,155)); c.drawRoundRect(x-width*.10f,y+dp(4)*s,x+width*.12f,y+dp(8)*s,dp(2),dp(2),p);
    }

    private void drawBattery(Canvas c,float x,float y,float size){
        float r=size*.20f;
        p.setStyle(Paint.Style.FILL);p.setColor(Color.argb(dark?90:75,255,255,255));c.drawRoundRect(x-size*.45f,y-size*.62f,x+size*.45f,y+size*.62f,r,r,p);
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(dp(2));p.setColor(Color.argb(105,65,105,135));c.drawRoundRect(x-size*.45f,y-size*.62f,x+size*.45f,y+size*.62f,r,r,p);
        p.setStyle(Paint.Style.FILL);p.setColor(Color.argb(125,27,139,220));c.drawRoundRect(x-size*.29f,y-size*.42f,x+size*.29f,y+size*.40f,r,r,p);
        drawLightning(c,x,y, size*.27f,Color.WHITE);
        p.setColor(Color.argb(120,65,105,135));c.drawRoundRect(x-size*.15f,y-size*.74f,x+size*.15f,y-size*.60f,dp(2),dp(2),p);
    }

    private void drawLightning(Canvas c,float x,float y,float size,int color){
        p.setStyle(Paint.Style.FILL);p.setColor(color);path.reset();path.moveTo(x-size*.20f,y-size*.62f);path.lineTo(x+size*.08f,y-size*.08f);path.lineTo(x-size*.04f,y-size*.08f);path.lineTo(x+size*.22f,y+size*.62f);path.lineTo(x-size*.18f,y+size*.02f);path.lineTo(x-size*.01f,y+size*.02f);path.close();c.drawPath(path,p);
    }

    private void drawPlug(Canvas c,float x,float y,float size){
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(dp(2.4f));p.setStrokeCap(Paint.Cap.ROUND);p.setColor(Color.argb(dark?110:90,27,139,220));
        c.drawLine(x-size*.35f,y-size*.75f,x-size*.35f,y-size*.25f,p);c.drawLine(x+size*.35f,y-size*.75f,x+size*.35f,y-size*.25f,p);c.drawArc(x-size*.55f,y-size*.35f,x+size*.55f,y+size*.65f,0,180,false,p);c.drawLine(x,y+size*.65f,x,y+size*1.15f,p);p.setStrokeCap(Paint.Cap.BUTT);p.setStyle(Paint.Style.FILL);
    }

    private float dp(float v){return v*getResources().getDisplayMetrics().density;}
}
