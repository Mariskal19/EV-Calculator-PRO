package com.evchargecalculator;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

/** Temporary diagnostic version used to isolate the Comparar coches runtime crash. */
public class CompararCochesActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView view = new TextView(this);
        view.setText("Comparar coches\n\nPrueba de apertura correcta");
        view.setTextSize(22);
        view.setTextColor(Color.WHITE);
        view.setGravity(Gravity.CENTER);
        view.setBackgroundColor(Color.rgb(8,34,58));
        setContentView(view);
    }
}
