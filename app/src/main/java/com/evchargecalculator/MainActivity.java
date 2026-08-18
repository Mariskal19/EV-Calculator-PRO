package com.evchargecalculator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends Activity {

    private LinearLayout page;

    private SeekBar fromBar;
    private SeekBar toBar;

    private TextView fromValue;
    private TextView toValue;
    private TextView batteryPercent;

    private TextView energyValue;
    private TextView timeValue;
    private TextView costValue;
    private TextView summary;

    private EditText batteryInput;
    private EditText powerInput;
    private EditText priceInput;

    private final int BACKGROUND = Color.rgb(241, 244, 249);
    private final int DARK = Color.rgb(25, 34, 51);
    private final int DARK_2 = Color.rgb(35, 47, 69);
    private final int BLUE = Color.rgb(72, 94, 235);
    private final int BLUE_LIGHT = Color.rgb(225, 231, 255);
    private final int TEXT = Color.rgb(32, 40, 56);
    private final int MUTED = Color.rgb(105, 114, 132);
    private final int WHITE = Color.WHITE;
    private final int GREEN = Color.rgb(88, 207, 151);

    private final Locale SPANISH = new Locale("es", "ES");

    private int dp(int value) {
        return (int) (
                value * getResources()
                        .getDisplayMetrics()
                        .density + 0.5f
        );
    }

    private TextView tv(
            String text,
            float size,
            int color,
            boolean bold
    ) {
        TextView view = new TextView(this);

        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);

        if (bold) {
            view.setTypeface(
                    Typeface.create(
                            Typeface.DEFAULT,
                            Typeface.BOLD
                    )
            );
        }

        return view;
    }

    private GradientDrawable background(
            int color,
            int radius
    ) {
        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));

        return drawable;
    }

    private LinearLayout panel(
            int color,
            int radius
    ) {
        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                dp(18),
                dp(18),
                dp(18),
                dp(18)
        );

        layout.setBackground(
                background(color, radius)
        );

        layout.setElevation(dp(3));

        return layout;
    }

    private void addSpace(
            LinearLayout layout,
            int height
    ) {
        View space = new View(this);

        layout.addView(
                space,
                new LinearLayout.LayoutParams(
                        1,
                        dp(height)
                )
        );
    }

    private TextView sectionTitle(
            String text
    ) {
        TextView view =
                tv(
                        text,
                        11,
                        MUTED,
                        true
                );

        view.setLetterSpacing(0.08f);

        view.setPadding(
                dp(2),
                dp(2),
                dp(2),
                dp(6)
        );

        return view;
    }

    private EditText input(
            String value
    ) {
        EditText editText =
                new EditText(this);

        editText.setText(value);
        editText.setTextSize(17);
        editText.setTextColor(TEXT);
        editText.setSingleLine(true);

        editText.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        editText.setPadding(
                dp(14),
                0,
                dp(14),
                0
        );

        editText.setBackground(
                background(
                        Color.rgb(247, 249, 252),
                        14
                )
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(52)
                );

        params.setMargins(
                0,
                dp(4),
                0,
                dp(10)
        );

        editText.setLayoutParams(params);

        return editText;
    }

    private TextView resultCard(
            String title,
            String initial
    ) {
        TextView view =
                tv(
                        title + "\n" + initial,
                        13,
                        TEXT,
                        true
                );

        view.setGravity(
                Gravity.CENTER_VERTICAL
        );

        view.setPadding(
                dp(14),
                dp(10),
                dp(10),
                dp(10)
        );

        view.setBackground(
                background(
                        Color.rgb(247, 249, 252),
                        16
                )
        );

        view.setElevation(dp(1));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0,
                        dp(84),
                        1
                );

        params.setMargins(
                dp(3),
                0,
                dp(3),
                0
        );

        view.setLayoutParams(params);

        return view;
    }

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

        buildInterface();
    }

    private void buildInterface() {

        ScrollView scroll =
                new ScrollView(this);

        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BACKGROUND);

        page =
                new LinearLayout(this);

        page.setOrientation(
                LinearLayout.VERTICAL
        );

        page.setPadding(
                dp(18),
                dp(24),
                dp(18),
                dp(30)
        );

        scroll.addView(page);

        setContentView(scroll);

        // --------------------------------
        // CABECERA
        // --------------------------------

        LinearLayout header =
                new LinearLayout(this);

        header.setGravity(
                Gravity.CENTER_VERTICAL
        );

        TextView icon =
                tv(
                        "⚡",
                        27,
                        WHITE,
                        true
                );

        icon.setGravity(Gravity.CENTER);

        icon.setBackground(
                background(BLUE, 17)
        );

        header.addView(
                icon,
                new LinearLayout.LayoutParams(
                        dp(54),
                        dp(54)
                )
        );

        LinearLayout titleContainer =
                new LinearLayout(this);

        titleContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        titleContainer.setPadding(
                dp(14),
                0,
                0,
                0
        );

        titleContainer.addView(
                tv(
                        "EV Charge",
                        25,
                        TEXT,
                        true
                )
        );

        titleContainer.addView(
                tv(
                        "CALCULATOR",
                        12,
                        BLUE,
                        true
                )
        );

        header.addView(titleContainer);

        page.addView(header);

        TextView subtitle =
                tv(
                        "Calcula tu carga de forma rápida y automática",
                        14,
                        MUTED,
                        false
                );

        subtitle.setPadding(
                dp(68),
                dp(4),
                0,
                dp(20)
        );

        page.addView(subtitle);

        // --------------------------------
        // TARJETA PRINCIPAL
        // --------------------------------

        LinearLayout hero =
                panel(DARK, 26);

        LinearLayout heroHeader =
                new LinearLayout(this);

        heroHeader.setGravity(
                Gravity.CENTER_VERTICAL
        );

        TextView heroTitle =
                tv(
                        "CARGA DEL VEHÍCULO",
                        11,
                        Color.rgb(177, 187, 207),
                        true
                );

        heroTitle.setLetterSpacing(0.08f);

        heroHeader.addView(
                heroTitle,
                new LinearLayout.LayoutParams(
                        0,
                        dp(30),
                        1
                )
        );

        TextView home =
                tv(
                        "●  EN CASA",
                        11,
                        GREEN,
                        true
                );

        heroHeader.addView(home);

        hero.addView(heroHeader);

        TextView charging =
                tv(
                        "Carga seleccionada",
                        13,
                        Color.rgb(158, 169, 192),
                        false
                );

        charging.setPadding(
                0,
                dp(2),
                0,
                dp(4)
        );

        hero.addView(charging);

        // PORCENTAJES

        LinearLayout values =
                new LinearLayout(this);

        values.setGravity(
                Gravity.CENTER_VERTICAL
        );

        fromValue =
                tv(
                        "30%",
                        36,
                        WHITE,
                        true
                );

        toValue =
                tv(
                        "80%",
                        36,
                        WHITE,
                        true
                );

        fromValue.setGravity(
                Gravity.CENTER
        );

        toValue.setGravity(
                Gravity.CENTER
        );

        values.addView(
                fromValue,
                new LinearLayout.LayoutParams(
                        0,
                        dp(64),
                        1
                )
        );

        TextView arrow =
                tv(
                        "→",
                        27,
                        Color.rgb(143, 157, 183),
                        true
                );

        arrow.setGravity(Gravity.CENTER);

        values.addView(
                arrow,
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(64)
                )
        );

        values.addView(
                toValue,
                new LinearLayout.LayoutParams(
                        0,
                        dp(64),
                        1
                )
        );

        hero.addView(values);

        // INDICADOR DE BATERÍA

        LinearLayout batteryContainer =
                new LinearLayout(this);

        batteryContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        batteryContainer.setPadding(
                dp(4),
                dp(4),
                dp(4),
                dp(4)
        );

        batteryContainer.setBackground(
                background(DARK_2, 20)
        );

        batteryPercent =
                tv(
                        "50% de batería",
                        12,
                        WHITE,
                        true
                );

        batteryPercent.setGravity(
                Gravity.CENTER
        );

        batteryContainer.addView(
                batteryPercent,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(30)
                )
        );

        hero.addView(batteryContainer);

        page.addView(hero);

        addSpace(page, 18);

        // --------------------------------
        // RANGO
        // --------------------------------

        page.addView(
                sectionTitle(
                        "RANGO DE CARGA"
                )
        );

        LinearLayout fromRow =
                new LinearLayout(this);

        fromRow.setGravity(
                Gravity.CENTER_VERTICAL
        );

        TextView fromLabel =
                tv(
                        "Desde",
                        14,
                        TEXT,
                        true
                );

        fromRow.addView(
                fromLabel,
                new LinearLayout.LayoutParams(
                        0,
                        dp(35),
                        1
                )
        );

        TextView fromInfo =
                tv(
                        "30%",
                        14,
                        BLUE,
                        true
                );

        fromInfo.setGravity(
                Gravity.CENTER
        );

        fromRow.addView(
                fromInfo,
                new LinearLayout.LayoutParams(
                        dp(55),
                        dp(35)
                )
        );

        page.addView(fromRow);

        fromBar =
                new SeekBar(this);

        fromBar.setMax(99);
        fromBar.setProgress(29);

        page.addView(
                fromBar,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(38)
                )
        );

        LinearLayout toRow =
                new LinearLayout(this);

        toRow.setGravity(
                Gravity.CENTER_VERTICAL
        );

        TextView toLabel =
                tv(
                        "Hasta",
                        14,
                        TEXT,
                        true
                );

        toRow.addView(
                toLabel,
                new LinearLayout.LayoutParams(
                        0,
                        dp(35),
                        1
                )
        );

        TextView toInfo =
                tv(
                        "80%",
                        14,
                        BLUE,
                        true
                );

        toInfo.setGravity(
                Gravity.CENTER
        );

        toRow.addView(
                toInfo,
                new LinearLayout.LayoutParams(
                        dp(55),
                        dp(35)
                )
        );

        page.addView(toRow);

        toBar =
                new SeekBar(this);

        toBar.setMax(100);
        toBar.setProgress(80);

        page.addView(
                toBar,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(38)
                )
        );

        addSpace(page, 12);

        // --------------------------------
        // RESULTADOS
        // --------------------------------

        LinearLayout result =
                panel(WHITE, 24);

        LinearLayout resultHeader =
                new LinearLayout(this);

        resultHeader.setGravity(
                Gravity.CENTER_VERTICAL
        );

        resultHeader.addView(
                tv(
                        "Resultado",
                        21,
                        TEXT,
                        true
                ),
                new LinearLayout.LayoutParams(
                        0,
                        dp(34),
                        1
                )
        );

        resultHeader.addView(
                tv(
                        "AUTOMÁTICO",
                        10,
                        BLUE,
                        true
                )
        );

        result.addView(resultHeader);

        result.addView(
                tv(
                        "Se actualiza al instante",
                        12,
                        MUTED,
                        false
                )
        );

        addSpace(result, 12);

        LinearLayout cards =
                new LinearLayout(this);

        cards.setOrientation(
                LinearLayout.HORIZONTAL
        );

        energyValue =
                resultCard(
                        "ENERGÍA",
                        "— kWh"
                );

        timeValue =
                resultCard(
                        "TIEMPO",
                        "—"
                );

        costValue =
                resultCard(
                        "COSTE",
                        "— €"
                );

        cards.addView(energyValue);
        cards.addView(timeValue);
        cards.addView(costValue);

        result.addView(cards);

        summary =
                tv(
                        "",
                        13,
                        MUTED,
                        false
                );

        summary.setPadding(
                dp(4),
                dp(14),
                dp(4),
                0
        );

        result.addView(summary);

        page.addView(result);

        addSpace(page, 16);

        // --------------------------------
        // PARÁMETROS
        // --------------------------------

        LinearLayout settings =
                panel(WHITE, 24);

        settings.addView(
                tv(
                        "Parámetros",
                        20,
                        TEXT,
                        true
                )
        );

        settings.addView(
                tv(
                        "Personaliza los datos de tu vehículo y tarifa",
                        12,
                        MUTED,
                        false
                )
        );

        addSpace(settings, 10);

        settings.addView(
                sectionTitle(
                        "CAPACIDAD DE BATERÍA · kWh"
                )
        );

        batteryInput =
                input("80");

        settings.addView(batteryInput);

        settings.addView(
                sectionTitle(
                        "POTENCIA DE CARGA · kW"
                )
        );

        powerInput =
                input("3,45");

        settings.addView(powerInput);

        settings.addView(
                sectionTitle(
                        "PRECIO ELECTRICIDAD · €/kWh"
                )
        );

        priceInput =
                input("0,15");

        settings.addView(priceInput);

        page.addView(settings);

        // --------------------------------
        // EVENTOS
        // --------------------------------

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

        fromBar.setOnSeekBarChangeListener(
                listener
        );

        toBar.setOnSeekBarChangeListener(
                listener
        );

        View.OnFocusChangeListener focus =
                new View.OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(
                            View view,
                            boolean hasFocus
                    ) {

                        if (!hasFocus) {
                            calculate();
                        }
                    }
                };

        batteryInput.setOnFocusChangeListener(
                focus
        );

        powerInput.setOnFocusChangeListener(
                focus
        );

        priceInput.setOnFocusChangeListener(
                focus
        );

        calculate();
    }

    // --------------------------------
    // LECTURA DE NÚMEROS
    // --------------------------------

    private double number(
            EditText input,
            double defaultValue
    ) {

        try {

            String value =
                    input.getText()
                            .toString()
                            .trim()
                            .replace(",", ".");

            return Double.parseDouble(value);

        } catch (Exception e) {

            return defaultValue;
        }
    }

    // --------------------------------
    // FORMATO ESPAÑOL
    // --------------------------------

    private String decimal(
            double value,
            int decimals
    ) {

        return String.format(
                SPANISH,
                "%." + decimals + "f",
                value
        );
    }

    // --------------------------------
    // CÁLCULO
    // --------------------------------

    private void calculate() {

        if (fromBar == null ||
                toBar == null) {
            return;
        }

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

        double battery =
                number(
                        batteryInput,
                        80.0
                );

        double power =
                number(
                        powerInput,
                        3.45
                );

        double price =
                number(
                        priceInput,
                        0.15
                );

        int percentage =
                to - from;

        double requiredKwh =
                Math.max(
                        0,
                        battery *
                                percentage /
                                100.0
                );

        double hours =
                power > 0
                        ? requiredKwh / power
                        : 0;

        int wholeHours =
                (int) hours;

        int minutes =
                (int) Math.round(
                        (hours - wholeHours)
                                * 60
                );

        if (minutes >= 60) {
            wholeHours++;
            minutes = 0;
        }

        double totalCost =
                requiredKwh * price;

        batteryPercent.setText(
                percentage +
                        "% de batería"
        );

        energyValue.setText(
                "ENERGÍA\n" +
                        decimal(
                                requiredKwh,
                                1
                        ) +
                        " kWh"
        );

        timeValue.setText(
                "TIEMPO\n" +
                        wholeHours +
                        " h " +
                        String.format(
                                SPANISH,
                                "%02d",
                                minutes
                        ) +
                        " min"
        );

        costValue.setText(
                "COSTE\n" +
                        decimal(
                                totalCost,
                                2
                        ) +
                        " €"
        );

        summary.setText(
                "De " +
                        from +
                        "% a " +
                        to +
                        "% · " +
                        decimal(
                                requiredKwh,
                                1
                        ) +
                        " kWh · " +
                        decimal(
                                power,
                                2
                        ) +
                        " kW · " +
                        decimal(
                                totalCost,
                                2
                        ) +
                        " €"
        );
    }
}
