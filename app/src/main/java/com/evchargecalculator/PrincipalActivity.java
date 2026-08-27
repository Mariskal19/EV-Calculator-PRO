package com.evchargecalculator;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class PrincipalActivity extends Activity {
    private boolean dark;
    private TextView themeButton;
    private final int blue = Color.rgb(46, 107, 255);
    private final int white = Color.rgb(22, 42, 63);
    private final int secondary = Color.rgb(90, 111, 137);
    private final int cardLight = Color.argb(245, 255, 255, 255);
    private final int cardDark = Color.argb(220, 21, 31, 42);
    private final int darkBg = Color.rgb(7, 19, 28);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dark = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        build();
    }

    private void build() {
        FrameLayout frame = new FrameLayout(this);
        PremiumBackgroundView background = new PremiumBackgroundView(this);
        frame.addView(background, new FrameLayout.LayoutParams(-1, -1));

        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(0, dp(36), 0, dp(68));
        scroll.addView(root);
        frame.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
        setContentView(frame);

        FrameLayout hero = new FrameLayout(this);
        hero.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(260)));

        ImageView header = new ImageView(this);
        header.setImageResource(R.drawable.cabecera_tema_claro);
        header.setScaleType(ImageView.ScaleType.CENTER_CROP);
        header.setTranslationY(-dp(10));
        hero.addView(header, new FrameLayout.LayoutParams(-1, -1));

        View fade = new View(this);
        fade.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.argb(200, 0, 0, 0), Color.argb(80, 0, 0, 0), Color.argb(20, 0, 0, 0), Color.argb(0, 0, 0, 0)}));
        hero.addView(fade, new FrameLayout.LayoutParams(-1, dp(170), Gravity.TOP));

        TextView title = tv("EV Calculator PRO", 22, Color.WHITE);
        title.setTypeface(null, 1);
        title.setGravity(Gravity.CENTER);
        title.setShadowLayer(dp(4), 0, dp(2), Color.argb(90, 0, 0, 0));
        FrameLayout.LayoutParams tp = new FrameLayout.LayoutParams(-1, dp(48));
        tp.leftMargin = dp(40); tp.rightMargin = dp(40); tp.topMargin = dp(12);
        hero.addView(title, tp);

        themeButton = tv(dark ? "☀" : "☾", 20, Color.WHITE);
        themeButton.setGravity(Gravity.CENTER);
        themeButton.setContentDescription("Cambiar tema");
        themeButton.setBackground(bg(Color.argb(140, 12, 22, 33), 12));
        themeButton.setOnClickListener(v -> toggleTheme());
        FrameLayout.LayoutParams ip = new FrameLayout.LayoutParams(dp(40), dp(40), Gravity.TOP | Gravity.END);
        ip.rightMargin = dp(14); ip.topMargin = dp(12);
        hero.addView(themeButton, ip);
        root.addView(hero);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));
        card.setBackground(bg(dark ? cardDark : cardLight, 22));
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2);
        cp.setMargins(dp(12), -dp(26), dp(12), 0);
        card.setLayoutParams(cp);

        TextView heading = tv("Herramientas", 18, textColor());
        heading.setTypeface(null, 1);
        card.addView(heading);
        TextView description = tv("Calcula y planifica la carga de tu vehículo eléctrico.", 14, subColor());
        description.setPadding(0, dp(10), 0, dp(14));
        card.addView(description);

        TextView calculator = tv("⚡  EV Charge Calculator", 17, Color.WHITE);
        calculator.setGravity(Gravity.CENTER_VERTICAL);
        calculator.setTypeface(null, 1);
        calculator.setPadding(dp(18), 0, dp(18), 0);
        calculator.setBackground(bg(blue, 18));
        calculator.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        card.addView(calculator, new LinearLayout.LayoutParams(-1, dp(62)));
        root.addView(card);

        TextView privacy = tv("Política de privacidad", 13, subColor());
        privacy.setGravity(Gravity.CENTER);
        privacy.setPadding(0, dp(20), 0, 0);
        privacy.setOnClickListener(v -> startActivity(new Intent(this, PrivacyMainActivity.class)));
        root.addView(privacy, new LinearLayout.LayoutParams(-1, dp(48)));
    }

    private void toggleTheme() {
        dark = !dark;
        getWindow().setNavigationBarColor(dark ? darkBg : Color.WHITE);
        build();
    }

    private TextView tv(String s, int sp, int color) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextSize(sp); t.setTextColor(color);
        return t;
    }

    private int textColor() { return dark ? Color.rgb(245, 248, 255) : white; }
    private int subColor() { return dark ? Color.rgb(170, 183, 204) : secondary; }
    private GradientDrawable bg(int color, float radius) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color); g.setCornerRadius(radius);
        return g;
    }
    private int dp(int n) { return (int)(n * getResources().getDisplayMetrics().density + .5f); }
}
