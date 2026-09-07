package com.evchargecalculator;

import android.content.Context;
import android.graphics.*;
import android.view.View;

public class EVIconView extends View {
  public enum Type {
    BATTERY,
    CHARGER,
    CAR,
    CLOCK,
    LIGHTNING
  }

  private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Type type;
  private boolean dark = false;
  private int accent = Color.rgb(0, 116, 255);
  private int accent2 = Color.rgb(0, 196, 184);

  public EVIconView(Context c, Type t) {
    super(c);
    type = t;
    setLayerType(View.LAYER_TYPE_SOFTWARE, null);
  }

  public void setDark(boolean d) {
    dark = d;
    invalidate();
  }

  private float d(float v) {
    return v * getResources().getDisplayMetrics().density;
  }

  @Override
  protected void onDraw(Canvas c) {
    super.onDraw(c);
    float w = getWidth(), h = getHeight(), cx = w / 2f, cy = h / 2f;
    int fg = dark ? Color.rgb(235, 244, 255) : Color.rgb(22, 43, 70);
    int soft = dark ? Color.rgb(110, 139, 170) : Color.rgb(91, 119, 150);
    p.setStyle(Paint.Style.STROKE);
    p.setStrokeWidth(d(2.2f));
    p.setStrokeCap(Paint.Cap.ROUND);
    p.setStrokeJoin(Paint.Join.ROUND);
    if (type == Type.BATTERY) {
      p.setColor(accent);
      c.drawRoundRect(d(6), d(8), w - d(10), h - d(8), d(7), d(7), p);
      p.setStyle(Paint.Style.FILL);
      c.drawRoundRect(w - d(9), cy - d(5), w - d(4), cy + d(5), d(2), d(2), p);
      p.setColor(accent2);
      Path q = new Path();
      q.moveTo(cx + d(2), d(12));
      q.lineTo(cx - d(5), cy + d(1));
      q.lineTo(cx + d(1), cy + d(1));
      q.lineTo(cx - d(2), h - d(11));
      q.lineTo(cx + d(9), cy - d(3));
      q.lineTo(cx + d(3), cy - d(3));
      q.close();
      c.drawPath(q, p);
    } else if (type == Type.CHARGER) {
      p.setColor(accent);
      c.drawRoundRect(d(8), d(5), w - d(8), h - d(5), d(7), d(7), p);
      p.setColor(soft);
      c.drawLine(d(12), d(13), w - d(12), d(13), p);
      p.setColor(accent2);
      p.setStyle(Paint.Style.FILL);
      c.drawCircle(d(15), d(10), d(1.5f), p);
      c.drawCircle(d(20), d(10), d(1.5f), p);
      p.setStyle(Paint.Style.STROKE);
      p.setColor(fg);
      Path q = new Path();
      q.moveTo(cx + d(2), d(18));
      q.lineTo(cx - d(4), cy + d(1));
      q.lineTo(cx + d(1), cy + d(1));
      q.lineTo(cx - d(1), h - d(15));
      q.lineTo(cx + d(8), cy - d(4));
      q.lineTo(cx + d(2), cy - d(4));
      q.close();
      c.drawPath(q, p);
      c.drawArc(w - d(5), h - d(19), w + d(7), h - d(2), -80, 145, false, p);
    } else if (type == Type.CAR) {
      p.setColor(accent);
      Path q = new Path();
      q.moveTo(d(6), cy + d(6));
      q.lineTo(d(10), cy - d(4));
      q.quadTo(d(12), cy - d(8), d(18), cy - d(8));
      q.lineTo(w - d(17), cy - d(8));
      q.quadTo(w - d(12), cy - d(8), w - d(10), cy - d(3));
      q.lineTo(w - d(6), cy + d(6));
      q.lineTo(w - d(6), h - d(10));
      q.lineTo(d(6), h - d(10));
      q.close();
      c.drawPath(q, p);
      p.setColor(dark ? Color.rgb(10, 22, 35) : Color.WHITE);
      p.setStyle(Paint.Style.FILL);
      Path win = new Path();
      win.moveTo(d(16), cy - d(5));
      win.lineTo(w / 2 - d(2), cy - d(5));
      win.lineTo(w / 2 + d(2), cy - d(5));
      win.lineTo(w - d(15), cy - d(5));
      win.lineTo(w - d(19), cy + d(1));
      win.lineTo(d(19), cy + d(1));
      win.close();
      c.drawPath(win, p);
      p.setColor(accent2);
      c.drawCircle(d(15), h - d(9), d(3), p);
      c.drawCircle(w - d(15), h - d(9), d(3), p);
    } else if (type == Type.CLOCK) {
      p.setColor(accent);
      c.drawCircle(cx, cy, d(10), p);
      p.setColor(accent2);
      c.drawLine(cx, cy, cx, cy - d(6), p);
      c.drawLine(cx, cy, cx + d(5), cy + d(3), p);
    } else {
      p.setStyle(Paint.Style.FILL);
      p.setColor(accent2);
      Path q = new Path();
      q.moveTo(cx + d(3), d(3));
      q.lineTo(cx - d(7), cy + d(2));
      q.lineTo(cx - d(1), cy + d(2));
      q.lineTo(cx - d(5), h - d(3));
      q.lineTo(cx + d(8), cy - d(2));
      q.lineTo(cx + d(2), cy - d(2));
      q.close();
      c.drawPath(q, p);
    }
  }
}
