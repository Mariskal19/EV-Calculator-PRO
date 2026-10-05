package com.evchargecalculator;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CochesActivity extends BaseNavigationActivity {
    @Override protected int getBottomNavigationIndex() { return 2; }

    private boolean dark;
    private int dp(int n) { return (int)(n * getResources().getDisplayMetrics().density + 0.5f); }
    private int text() { return dark ? Color.rgb(245,248,255) : Color.rgb(22,42,63); }
    private int sub() { return dark ? Color.rgb(170,183,204) : Color.rgb(90,111,137); }
    private int blue = Color.rgb(46,107,255);

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        LanguageManager.applyStored(this);
        dark = isDarkTheme();
        build();
        EdgeToEdgeHelper.apply(this, dark);
    }

    private GradientDrawable cardBg() {
        GradientDrawable g = new GradientDrawable();
        g.setColor(dark ? Color.rgb(17,31,44) : Color.WHITE);
        g.setCornerRadius(dp(20));
        g.setStroke(dp(1), dark ? Color.rgb(43,64,82) : Color.rgb(218,228,239));
        return g;
    }

    private TextView tv(String s, float size, int color) {
        TextView t = new TextView(this);
        t.setText(LanguageManager.t(this, s));
        t.setTextSize(size);
        t.setTextColor(color);
        return t;
    }

    private void build() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(28), dp(18), dp(78));
        root.setBackgroundColor(dark ? Color.rgb(7,19,28) : Color.rgb(241,246,251));

        TextView title = tv("Coches", 28, text());
        title.setTypeface(null, Typeface.BOLD);
        root.addView(title, new LinearLayout.LayoutParams(-1, dp(42)));

        TextView intro = tv("Elige qué quieres hacer con los vehículos eléctricos.", 14, sub());
        intro.setLineSpacing(0, 1.15f);
        root.addView(intro, new LinearLayout.LayoutParams(-1, dp(48)));

        LinearLayout compare = optionCard(
            "🚗",
            "Comparar coches",
            "Compara hasta 3 vehículos y consulta sus características lado a lado.",
            v -> startActivity(new Intent(this, CompararCochesActivity.class))
        );
        root.addView(compare, margins(0, dp(12), 0, 0));

        LinearLayout similar = optionCard(
            "🔎",
            "Buscar coches similares",
            "Elige un coche de referencia y descubre las 5 alternativas más similares.",
            v -> startActivity(new Intent(this, SimilarCarsActivity.class))
        );
        root.addView(similar, margins(0, dp(12), 0, 0));

        TextView note = tv("Ambas funciones utilizan el catálogo protegido y las incorporaciones externas validadas.", 12, sub());
        note.setPadding(dp(4), dp(18), dp(4), 0);
        root.addView(note, new LinearLayout.LayoutParams(-1, -2));

        setContentView(root);
    }

    private LinearLayout optionCard(String icon, String title, String description, android.view.View.OnClickListener click) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(16), dp(18), dp(16));
        card.setBackground(cardBg());
        card.setClickable(true);
        card.setFocusable(true);
        card.setOnClickListener(click);

        TextView iconView = tv(icon, 30, blue);
        iconView.setGravity(Gravity.CENTER_VERTICAL);
        card.addView(iconView, new LinearLayout.LayoutParams(-1, dp(40)));

        TextView h = tv(title, 19, text());
        h.setTypeface(null, Typeface.BOLD);
        h.setPadding(0, dp(4), 0, dp(4));
        card.addView(h, new LinearLayout.LayoutParams(-1, dp(34)));

        TextView d = tv(description, 13, sub());
        d.setLineSpacing(0, 1.15f);
        card.addView(d, new LinearLayout.LayoutParams(-1, dp(44)));

        TextView action = tv("Abrir  ›", 13, blue);
        action.setTypeface(null, Typeface.BOLD);
        action.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        card.addView(action, new LinearLayout.LayoutParams(-1, dp(28)));

        return card;
    }

    private LinearLayout.LayoutParams margins(int l, int t, int r, int b) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(l,t,r,b);
        return p;
    }
}
