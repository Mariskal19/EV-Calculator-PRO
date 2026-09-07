package com.evchargecalculator;

import android.content.Context;
import android.graphics.*;
import android.view.View;

public class PremiumBackgroundView extends View {
  private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Path path = new Path();
  private boolean dark = true;
  private boolean showVehicle = true;

  public PremiumBackgroundView(Context c) {
    super(c);
    setLayerType(View.LAYER_TYPE_SOFTWARE, null);
  }

  public void setDark(boolean d) {
    dark = d;
    invalidate();
  }

  public void setShowVehicle(boolean show) {
    showVehicle = show;
    invalidate();
  }

  @Override
  protected void onDraw(Canvas c) {
    float w = getWidth(), h = getHeight();
    p.setStyle(Paint.Style.FILL);
    int a = dark ? Color.rgb(7, 19, 28) : Color.rgb(244, 248, 255),
        b = dark ? Color.rgb(13, 29, 44) : Color.rgb(223, 233, 246);
    p.setShader(new LinearGradient(0, 0, w, h, a, b, Shader.TileMode.CLAMP));
    c.drawRect(0, 0, w, h, p);
    p.setShader(null);
    p.setColor(dark ? Color.argb(60, 78, 161, 255) : Color.argb(34, 46, 107, 255));
    p.setMaskFilter(new BlurMaskFilter(95, BlurMaskFilter.Blur.NORMAL));
    c.drawCircle(w * .75f, h * .12f, 170, p);
    p.setColor(dark ? Color.argb(42, 39, 196, 164) : Color.argb(28, 40, 198, 164));
    c.drawCircle(w * .18f, h * .36f, 140, p);
    p.setMaskFilter(null);
    if (!showVehicle) return;
    p.setColor(dark ? Color.argb(28, 120, 195, 245) : Color.argb(16, 46, 107, 255));
    c.drawRect(0, h * .72f, w, h * .725f, p);
    float y = h * .67f, x = w * .55f, cw = w * .42f;
    p.setColor(dark ? Color.argb(32, 125, 177, 214) : Color.argb(30, 120, 160, 210));
    path.reset();
    path.moveTo(x - cw * .5f, y);
    path.lineTo(x - cw * .38f, y - cw * .18f);
    path.quadTo(x - cw * .24f, y - cw * .30f, x - cw * .02f, y - cw * .30f);
    path.lineTo(x + cw * .20f, y - cw * .28f);
    path.quadTo(x + cw * .38f, y - cw * .23f, x + cw * .5f, y);
    path.close();
    c.drawPath(path, p);
    p.setStyle(Paint.Style.STROKE);
    p.setStrokeWidth(3);
    p.setColor(dark ? Color.argb(66, 140, 180, 230) : Color.argb(44, 120, 160, 210));
    c.drawPath(path, p);
    p.setStyle(Paint.Style.FILL);
    p.setColor(dark ? Color.argb(60, 82, 156, 255) : Color.argb(36, 46, 107, 255));
    c.drawCircle(x - cw * .30f, y + 2, 27, p);
    c.drawCircle(x + cw * .30f, y + 2, 27, p);
    p.setColor(dark ? Color.argb(70, 46, 107, 255) : Color.argb(24, 46, 107, 255));
    c.drawRoundRect(w * .08f, h * .54f, w * .13f, h * .72f, 10, 10, p);
    p.setStyle(Paint.Style.STROKE);
    p.setStrokeWidth(5);
    p.setColor(dark ? Color.argb(52, 78, 161, 255) : Color.argb(28, 46, 107, 255));
    path.reset();
    path.moveTo(w * .13f, h * .62f);
    path.cubicTo(w * .22f, h * .52f, w * .31f, h * .60f, w * .34f, h * .67f);
    c.drawPath(path, p);
    p.setStyle(Paint.Style.FILL);
  }
}
