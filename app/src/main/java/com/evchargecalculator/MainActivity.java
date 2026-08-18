package com.evchargecalculator;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends Activity {

    LinearLayout page;

    SeekBar fromBar;
    SeekBar toBar;

    TextView fromValue;
    TextView toValue;
    TextView chargePct;

    TextView energy;
    TextView time;
    TextView cost;
    TextView summary;

    EditText battery;
    EditText power;
    EditText price;

    final int blue = Color.rgb(79, 99, 232);
    final int dark = Color.rgb(27, 35, 52);
    final int muted = Color.rgb(105, 112, 128);
    final int light = Color.rgb(247, 248, 252);
    final int white = Color.WHITE;

    int d(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    TextView text(String value, float size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);

        if (bold) {
            t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }

        return t;
    }

    GradientDrawable round(int color, int radius) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(d(radius));
        return g;
    }

    EditText field(String value) {
        EditText e = new EditText(this);

        e.setText(value);
        e.setTextSize(17);
        e.setTextColor(dark);
        e.setSingleLine(true);

        e.setInputType(
                InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        e.setPadding(d(14), 0, d(14), 0);
        e.setBackground(round(Color.rgb(248, 249, 252), 14));

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, d(52));

        p.setMargins(0, d(5), 0, d(12));

        e.setLayoutParams(p);

        return e;
    }

    TextView label(String value) {
        TextView t = text(value, 12, muted, true);
        t.setPadding(0, d(8), 0, 0);
        return t;
    }

    TextView card(String title, String value) {

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER_VERTICAL);

        box.setPadding(d(12), d(10), d(12), d(10));
        box.setBackground(round(Color.rgb(248, 249, 252), 16));

        TextView titleView =
                text(title, 10, muted, true);

        TextView valueView =
                text(value, 16, dark, true);

        box.addView(titleView);
        box.addView(valueView);

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(0, d(82), 1);

        p.setMargins(d(4), 0, d(4), 0);

        box.setLayoutParams(p);

        valueView.setTag("value");

        return boxToTextView(box, title, value);
    }

    TextView boxToTextView(
            LinearLayout box,
            String title,
            String value) {

        TextView result = text(
                title + "\n" + value,
                14,
                dark,
                true
        );

        result.setGravity(Gravity.CENTER_VERTICAL);
        result.setPadding(
                d(14),
                d(8),
                d(10),
                d(8)
        );

        result.setBackground(
                round(Color.rgb(248, 249, 252), 16)
        );

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(0, d(82), 1);

        p.setMargins(d(4), 0, d(4), 0);

        result.setLayoutParams(p);

        return result;
    }

    LinearLayout panel(int color, int radius) {

        LinearLayout box = new LinearLayout(this);

        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(
                d(18),
                d(16),
                d(18),
                d(16)
        );

        box.setBackground(round(color, radius));

        return box;
    }

    void spacer(int height) {

        SpaceView space = new SpaceView(this);

        page.addView(
                space,
                new LinearLayout.LayoutParams(
                        1,
                        d(height)
                )
        );
    }

    void spacerIn(LinearLayout layout, int height) {

        SpaceView space = new SpaceView(this);

        layout.addView(
                space,
                new LinearLayout.LayoutParams(
                        1,
                        d(height)
                )
        );
    }

    static class SpaceView extends View {
        public SpaceView(android.content.Context context) {
            super(context);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);

        scroll.setBackgroundColor(light);

        page = new LinearLayout(this);

        page.setOrientation(LinearLayout.VERTICAL);

        page.setPadding(
                d(20),
                d(24),
                d(20),
                d(30)
        );

        scroll.addView(page);

        setContentView(scroll);

        createInterface();
    }

    void createInterface() {

        // CABECERA

        LinearLayout header =
                new LinearLayout(this);

        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView icon =
                text("⚡", 30, white, true);

        icon.setGravity(Gravity.CENTER);

        icon.setBackground(
                round(blue, 16)
        );

        header.addView(
                icon,
                new LinearLayout.LayoutParams(
                        d(54),
                        d(54)
                )
        );

        LinearLayout headerText =
                new LinearLayout(this);

        headerText.setOrientation(
                LinearLayout.VERTICAL
        );

        headerText.setPadding(
                d(14),
                0,
                0,
                0
        );

        headerText.addView(
                text(
                        "EV Charge",
                        25,
                        dark,
                        true
                )
        );

        headerText.addView(
                text(
                        "CALCULATOR",
                        12,
                        blue,
                        true
                )
        );

        header.addView(headerText);

        page.addView(header);

        TextView intro =
                text(
                        "Calcula tu carga de forma rápida y automática",
                        14,
                        muted,
                        false
                );

        intro.setPadding(
                d(68),
                d(3),
                0,
                d(18)
        );

        page.addView(intro);

        // TARJETA PRINCIPAL

        LinearLayout hero =
                panel(dark, 24);

        LinearLayout heroHeader =
                new LinearLayout(this);

        heroHeader.setGravity(
                Gravity.CENTER_VERTICAL
        );

        TextView heroTitle =
                text(
                        "CARGA DEL VEHÍCULO",
                        12,
                        Color.rgb(173, 184, 207),
                        true
                );

        heroHeader.addView(
                heroTitle,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                )
        );

        heroHeader.addView(
                text(
                        "● EN CASA",
                        11,
                        Color.rgb(100, 220, 170),
                        true
                )
        );

        hero.addView(heroHeader);

        LinearLayout values =
                new LinearLayout(this);

        values.setGravity(
                Gravity.CENTER_VERTICAL
        );

        fromValue =
                text(
                        "30%",
                        32,
                        white,
                        true
                );

        toValue =
                text(
                        "80%",
                        32,
                        white,
                        true
                );

        fromValue.setGravity(Gravity.CENTER);
        toValue.setGravity(Gravity.CENTER);

        values.addView(
                fromValue,
                new LinearLayout.LayoutParams(
                        0,
                        d(58),
                        1
                )
        );

        TextView arrow =
                text(
                        "→",
                        25,
                        Color.rgb(150, 164, 190),
                        true
                );

        arrow.setGravity(Gravity.CENTER);

        values.addView(
                arrow,
                new LinearLayout.LayoutParams(
                        d(42),
                        d(58)
                )
        );

        values.addView(
                toValue,
                new LinearLayout.LayoutParams(
                        0,
                        d(58),
                        1
                )
        );

        hero.addView(values);

        LinearLayout batteryBar =
                new LinearLayout(this);

        batteryBar.setGravity(Gravity.CENTER);

        batteryBar.setPadding(
                d(4),
                0,
                d(4),
                0
        );

        batteryBar.setBackground(
                round(
                        Color.rgb(44, 57, 82),
                        50
                )
        );

        chargePct =
                text(
                        "50% de batería",
                        12,
                        white,
                        true
                );

        chargePct.setGravity(
                Gravity.CENTER
        );

        batteryBar.addView(
                chargePct,
                new LinearLayout.LayoutParams(
                        -1,
                        d(30)
                )
        );

        hero.addView(batteryBar);

        page.addView(hero);

        spacer(16);

        // RANGO

        page.addView(
                text(
                        "RANGO DE CARGA",
                        12,
                        muted,
                        true
                )
        );

        fromBar =
                new SeekBar(this);

        fromBar.setMax(99);
        fromBar.setProgress(29);

        page.addView(
                fromBar,
                new LinearLayout.LayoutParams(
                        -1,
                        d(42)
                )
        );

        toBar =
                new SeekBar(this);

        toBar.setMax(100);
        toBar.setProgress(80);

        page.addView(
                toBar,
                new LinearLayout.LayoutParams(
                        -1,
                        d(42)
                )
        );

        spacer(8);

        // RESULTADO

        LinearLayout result =
                panel(white, 22);

        result.addView(
                text(
                        "Resultado",
                        20,
                        dark,
                        true
                )
        );

        TextView resultSub =
                text(
                        "Se actualiza al instante",
                        12,
                        muted,
                        false
                );

        resultSub.setPadding(
                0,
                d(2),
                0,
                d(12)
        );

        result.addView(resultSub);

        LinearLayout cards =
                new LinearLayout(this);

        cards.setOrientation(
                LinearLayout.HORIZONTAL
        );

        energy =
                boxToTextView(
                        new LinearLayout(this),
                        "ENERGÍA",
                        "— kWh"
                );

        time =
                boxToTextView(
                        new LinearLayout(this),
                        "TIEMPO",
                        "—"
                );

        cost =
                boxToTextView(
                        new LinearLayout(this),
                        "COSTE",
                        "— €"
                );

        cards.addView(energy);
        cards.addView(time);
        cards.addView(cost);

        result.addView(cards);

        summary =
                text(
                        "",
                        13,
                        muted,
                        false
                );

        summary.setPadding(
                d(2),
                d(14),
                d(2),
                0
        );

        result.addView(summary);

        page.addView(result);

        spacer(16);

        // PARAMETROS

        LinearLayout settings =
                panel(white, 22);

        settings.addView(
                text(
                        "Parámetros",
                        20,
                        dark,
                        true
                )
        );

        settings.addView(
                text(
                        "Puedes modificar estos valores cuando quieras",
                        12,
                        muted,
                        false
                )
        );

        spacerIn(settings, 8);

        settings.addView(
                label(
                        "CAPACIDAD DE BATERÍA · kWh"
                )
        );

        battery =
                field("80");

        settings.addView(battery);

        settings.addView(
                label(
                        "POTENCIA DE CARGA · kW"
                )
        );

        power =
                field("3,45");

        settings.addView(power);

        settings.addView(
                label(
                        "PRECIO ELECTRICIDAD · €/kWh"
                )
        );

        price =
                field("0,15");

        settings.addView(price);

        page.addView(settings);

        // EVENTOS

        SeekBar.OnSeekBarChangeListener listener =
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser
                    ) {
                        calculate();
                    }

                    @Override
                    public void onStartTrackingTouch(
                            SeekBar seekBar
                    ) {
                    }

                    @Override
                    public void onStopTrackingTouch(
                            SeekBar seekBar
                    ) {
                    }
                };

        fromBar.setOnSeekBarChangeListener(listener);
        toBar.setOnSeekBarChangeListener(listener);

        View.OnFocusChangeListener focusListener =
                (view, hasFocus) -> {

                    if (!hasFocus) {
                        calculate();
                    }
                };

        battery.setOnFocusChangeListener(
                focusListener
        );

        power.setOnFocusChangeListener(
                focusListener
        );

        price.setOnFocusChangeListener(
                focusListener
        );

        calculate();
    }

    double number(
            EditText editText,
            double defaultValue
    ) {

        try {

            String value =
                    editText
                            .getText()
                            .toString()
                            .trim()
                            .replace(",", ".");

            return Double.parseDouble(value);

        } catch (Exception e) {

            return defaultValue;
        }
    }

    String decimal(double value, int decimals) {

        return String.format(
                Locale.GERMANY,
                "%." + decimals + "f",
                value
        );
    }

    void calculate() {

        int from =
                fromBar.getProgress() + 1;

        int to =
                toBar.getProgress();

        if (to <= from) {

            to = Math.min(
                    100,
                    from + 1
            );

            toBar.setProgress(to);
        }

        fromValue.setText(
                from + "%"
        );

        toValue.setText(
                to + "%"
        );

        int percentage =
                to - from;

        chargePct.setText(
                percentage + "% de batería"
        );

        double capacity =
                number(battery, 80);

        double chargingPower =
                number(power, 3.45);

        double electricityPrice =
                number(price, 0.15);

        double kwh =
                Math.max(
                        0,
                        capacity *
                        percentage /
                        100.0
                );

        double hours =
                chargingPower > 0
                        ? kwh / chargingPower
                        : 0;

        int hour =
                (int) hours;

        int minutes =
                (int) Math.round(
                        (hours - hour) * 60
                );

        if (minutes == 60) {
            hour++;
            minutes = 0;
        }

        double totalCost =
                kwh * electricityPrice;

        energy.setText(
                "ENERGÍA\n" +
                decimal(kwh, 1) +
                " kWh"
        );

        time.setText(
                "TIEMPO\n" +
                hour +
                " h " +
                String.format(
                        Locale.getDefault(),
                        "%02d",
                        minutes
                ) +
                " min"
        );

        cost.setText(
                "COSTE\n" +
                decimal(totalCost, 2) +
                " €"
        );

        summary.setText(
                "De " +
                from +
                "% a " +
                to +
                "% necesitas " +
                decimal(kwh, 1) +
                " kWh · " +
                decimal(chargingPower, 2) +
                " kW · " +
                decimal(totalCost, 2) +
                " €"
        );
    }
}
