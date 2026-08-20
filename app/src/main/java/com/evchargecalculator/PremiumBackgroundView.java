package com.evchargecalculator;
import android.content.Context;import android.graphics.*;import android.view.View;
public class PremiumBackgroundView extends View {
 private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG); private final Path path=new Path(); private boolean dark=true;
 public PremiumBackgroundView(Context c){super(c);setLayerType(View.LAYER_TYPE_SOFTWARE,null);} public void setDark(boolean d){dark=d;invalidate();}
 @Override protected void onDraw(Canvas c){float w=getWidth(),h=getHeight();
  int top=dark?Color.rgb(4,10,19):Color.rgb(242,248,253), bottom=dark?Color.rgb(6,20,32):Color.rgb(225,239,249);
  p.setStyle(Paint.Style.FILL);p.setShader(new LinearGradient(0,0,w,h,top,bottom,Shader.TileMode.CLAMP));c.drawRect(0,0,w,h,p);p.setShader(null);
  p.setColor(dark?Color.argb(55,0,116,255):Color.argb(28,0,116,255));p.setMaskFilter(new BlurMaskFilter(105,BlurMaskFilter.Blur.NORMAL));c.drawCircle(w*.78f,h*.10f,dark?145:125,p);
  p.setColor(dark?Color.argb(34,0,196,184):Color.argb(25,0,170,160));c.drawCircle(w*.12f,h*.38f,125,p);p.setMaskFilter(null);
  // subtle electric grid
  p.setColor(dark?Color.argb(22,130,170,210):Color.argb(22,75,110,145));p.setStrokeWidth(1);
  for(float y=h*.73f;y<h;y+=d(34)) c.drawLine(0,y,w,y,p); for(float x=0;x<w;x+=d(42)) c.drawLine(x,h*.73f,x,h,p);
  // ghost EV silhouette
  float y=h*.70f,x=w*.56f,cw=w*.44f;p.setColor(dark?Color.argb(36,120,160,200):Color.argb(28,70,105,135));
  path.reset();path.moveTo(x-cw*.50f,y);path.lineTo(x-cw*.37f,y-cw*.18f);path.quadTo(x-cw*.25f,y-cw*.29f,x-cw*.02f,y-cw*.29f);path.lineTo(x+cw*.19f,y-cw*.27f);path.quadTo(x+cw*.39f,y-cw*.21f,x+cw*.50f,y);path.lineTo(x+cw*.45f,y);path.lineTo(x+cw*.45f,y+cw*.06f);path.lineTo(x-cw*.45f,y+cw*.06f);path.close();c.drawPath(path,p);
  p.setColor(dark?Color.argb(55,0,116,255):Color.argb(34,0,116,255));c.drawCircle(x-cw*.30f,y+cw*.05f,25,p);c.drawCircle(x+cw*.30f,y+cw*.05f,25,p);
 }
 private float d(float v){return v*getResources().getDisplayMetrics().density;}
}
