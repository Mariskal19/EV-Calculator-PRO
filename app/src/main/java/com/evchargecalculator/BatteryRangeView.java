package com.evchargecalculator;

import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;

public class BatteryRangeView extends View {
    public interface Listener { void onChanged(int current, int target); }
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int current=30, target=80;
    private Listener listener;
    private final int blue=Color.rgb(27,139,220);
    public BatteryRangeView(Context c){super(c); setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
    public void setValues(int c,int t){current=Math.max(0,Math.min(100,c));target=Math.max(0,Math.min(100,t));invalidate();}
    public int getCurrent(){return current;} public int getTarget(){return target;}
    public void setListener(Listener l){listener=l;}
    private float xFor(int v){return dp(8)+(getWidth()-dp(16))*v/100f;}
    private float dp(float v){return v*getResources().getDisplayMetrics().density;}
    private int valueFor(float x){return Math.max(0,Math.min(100,Math.round((x-dp(8))*100f/(getWidth()-dp(16)))));}
    @Override protected void onDraw(Canvas c){super.onDraw(c); float y=getHeight()/2f; float left=dp(8), right=getWidth()-dp(8);
        p.setStrokeWidth(dp(4)); p.setStrokeCap(Paint.Cap.ROUND); p.setColor(Color.rgb(220,230,240)); c.drawLine(left,y,right,y,p);
        p.setColor(blue); c.drawLine(xFor(0),y,xFor(target),y,p);
        p.setColor(Color.rgb(70,205,180)); c.drawLine(xFor(current),y,xFor(target),y,p);
        p.setShadowLayer(dp(4),0,dp(2),0x55000000); p.setColor(blue); c.drawCircle(xFor(current),y,dp(9),p); p.setColor(Color.rgb(70,205,180)); c.drawCircle(xFor(target),y,dp(9),p); p.clearShadowLayer(); p.setTextSize(dp(12)); p.setTypeface(Typeface.DEFAULT_BOLD); p.setTextAlign(Paint.Align.CENTER); p.setColor(blue); c.drawText(current+" %",xFor(current),y-dp(18),p); p.setColor(Color.rgb(70,205,180)); c.drawText(target+" %",xFor(target),y-dp(18),p);
    }
    @Override public boolean onTouchEvent(MotionEvent e){
        if(e.getAction()==MotionEvent.ACTION_DOWN||e.getAction()==MotionEvent.ACTION_MOVE||e.getAction()==MotionEvent.ACTION_UP){
            int v=valueFor(e.getX());
            if(Math.abs(v-current)<=Math.abs(v-target)){
                current=Math.min(v,target-1);
                if(current<0) current=0;
            } else {
                target=Math.max(v,current+1);
                if(target>100) target=100;
            }
            invalidate();
            if(listener!=null)listener.onChanged(current,target);
            return true;
        }
        return true;
    }
}
