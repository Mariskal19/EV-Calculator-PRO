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
    private final int green=Color.rgb(70,205,180);
    private int activeThumb = -1;
    public BatteryRangeView(Context c){super(c); setLayerType(View.LAYER_TYPE_SOFTWARE,null); setClickable(true);}
    public void setValues(int c,int t){ current=Math.max(0,Math.min(99,c)); target=Math.max(current+1,Math.min(100,t)); invalidate(); }
    public int getCurrent(){return current;} public int getTarget(){return target;}
    public void setListener(Listener l){listener=l;}
    private float xFor(int v){return dp(12)+(getWidth()-dp(24))*v/100f;}
    private float dp(float v){return v*getResources().getDisplayMetrics().density;}
    private int valueFor(float x){return Math.max(0,Math.min(100,Math.round((x-dp(12))*100f/(getWidth()-dp(24)))));}
    @Override protected void onDraw(Canvas c){
        super.onDraw(c); float y=getHeight()/2f, left=xFor(0), right=xFor(100);
        p.setStrokeWidth(dp(5)); p.setStrokeCap(Paint.Cap.ROUND);
        // Base track: the area outside the selected charging range remains neutral.
        p.setColor(Color.rgb(220,230,240)); c.drawLine(left,y,right,y,p);
        // Only the interval between the current level and the target is highlighted.
        p.setColor(green); c.drawLine(xFor(current),y,xFor(target),y,p);
        p.setShadowLayer(dp(4),0,dp(2),0x55000000);
        p.setColor(blue); c.drawCircle(xFor(current),y,dp(10),p);
        p.setColor(green); c.drawCircle(xFor(target),y,dp(10),p);
        p.clearShadowLayer();
    }
    @Override public boolean onTouchEvent(MotionEvent e){
        float x=e.getX();
        if(e.getAction()==MotionEvent.ACTION_DOWN){
            float dc=Math.abs(x-xFor(current)), dt=Math.abs(x-xFor(target));
            activeThumb = dc <= dt ? 0 : 1;
            getParent().requestDisallowInterceptTouchEvent(true); updateThumb(x); return true;
        }
        if(e.getAction()==MotionEvent.ACTION_MOVE){ updateThumb(x); return true; }
        if(e.getAction()==MotionEvent.ACTION_UP || e.getAction()==MotionEvent.ACTION_CANCEL){ updateThumb(x); activeThumb=-1; getParent().requestDisallowInterceptTouchEvent(false); performClick(); return true; }
        return true;
    }
    private void updateThumb(float x){
        int v=valueFor(x);
        if(activeThumb==0) current=Math.max(0,Math.min(v,target-1));
        else if(activeThumb==1) target=Math.min(100,Math.max(v,current+1));
        invalidate(); if(listener!=null) listener.onChanged(current,target);
    }
    @Override public boolean performClick(){super.performClick();return true;}
}
