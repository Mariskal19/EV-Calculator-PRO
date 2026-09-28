package com.evchargecalculator;

import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;

public class BatteryRangeView extends View {
  public interface Listener {
    void onChanged(int current, int target);
  }

  private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
  private int current = 30, target = 80;
  private Listener listener;
  private boolean dark = false;
  private final int blueLight = Color.rgb(41, 179, 255);
  private final int blueDark = Color.rgb(16, 144, 230);
  private final int trackLight = Color.rgb(220, 235, 247);
  private final int trackDark = Color.rgb(42, 58, 75);
  private int activeThumb = -1;
  private float thumbScale = 1f;

  public BatteryRangeView(Context c) {
    super(c);
    setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    setClickable(true);
  }

  public void setDark(boolean d) {
    dark = d;
    invalidate();
  }

  public void setValues(int c, int t) {
    current = Math.max(0, Math.min(99, c));
    target = Math.max(current + 1, Math.min(100, t));
    invalidate();
  }

  public int getCurrent() {
    return current;
  }

  public int getTarget() {
    return target;
  }

  public void setListener(Listener l) {
    listener = l;
  }

  public void setListener(Runnable r) {
    listener = (current, target) -> r.run();
  }

  private float xFor(int v) {
    return dp(12) + (getWidth() - dp(24)) * v / 100f;
  }

  private float dp(float v) {
    return v * getResources().getDisplayMetrics().density;
  }

  private int valueFor(float x) {
    return Math.max(0, Math.min(100, Math.round((x - dp(12)) * 100f / (getWidth() - dp(24)))));
  }

  @Override
  protected void onDraw(Canvas c) {
    super.onDraw(c);
    float y = getHeight() / 2f, left = xFor(0), right = xFor(100);
    int track = dark ? trackDark : trackLight;

    // Thinner rounded progress-bar style, matching the second blue reference bar.
    p.setStrokeWidth(dp(5));
    p.setStrokeCap(Paint.Cap.ROUND);
    p.setColor(track);
    c.drawLine(left, y, right, y, p);

    float activeLeft = xFor(current);
    float activeRight = xFor(target);

    // Active section: blue, with the same light-to-dark direction used by the app.
    p.setShader(new LinearGradient(activeLeft, y, activeRight, y, blueDark, blueLight, Shader.TileMode.CLAMP));
    c.drawLine(activeLeft, y, activeRight, y, p);
    p.setShader(null);

    // Small round handles with the same subtle depth/effect as the capacity slider thumb.
    p.setShadowLayer(dp(2), 0, dp(1), Color.argb(90, 0, 0, 0));
    p.setColor(blueDark);
    c.drawCircle(activeLeft, y, dp(7) * (activeThumb == 0 ? thumbScale : 1f), p);
    p.setColor(blueLight);
    c.drawCircle(activeRight, y, dp(7) * (activeThumb == 1 ? thumbScale : 1f), p);
    p.clearShadowLayer();
  }

  @Override
  public boolean onTouchEvent(MotionEvent e) {
    float x = e.getX();
    if (e.getAction() == MotionEvent.ACTION_DOWN) {
      float dc = Math.abs(x - xFor(current)), dt = Math.abs(x - xFor(target));
      activeThumb = dc <= dt ? 0 : 1;
      animateThumb(true);
      getParent().requestDisallowInterceptTouchEvent(true);
      updateThumb(x);
      return true;
    }
    if (e.getAction() == MotionEvent.ACTION_MOVE) {
      updateThumb(x);
      return true;
    }
    if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_CANCEL) {
      updateThumb(x);
      animateThumb(false);
      activeThumb = -1;
      getParent().requestDisallowInterceptTouchEvent(false);
      performClick();
      return true;
    }
    return true;
  }

  private void animateThumb(boolean enlarge) {
    final float from = thumbScale;
    final float to = enlarge ? 1.28f : 1f;
    long start = System.currentTimeMillis();
    Runnable anim = new Runnable() {
      @Override public void run() {
        float t = Math.min(1f, (System.currentTimeMillis() - start) / 140f);
        float eased = 1f - (1f - t) * (1f - t);
        thumbScale = from + (to - from) * eased;
        invalidate();
        if (t < 1f) postOnAnimation(this);
      }
    };
    post(anim);
  }

  private void updateThumb(float x) {
    int v = valueFor(x);
    if (activeThumb == 0) current = Math.max(0, Math.min(v, target - 1));
    else if (activeThumb == 1) target = Math.min(100, Math.max(v, current + 1));
    invalidate();
    if (listener != null) listener.onChanged(current, target);
  }

  @Override
  public boolean performClick() {
    super.performClick();
    return true;
  }
}
