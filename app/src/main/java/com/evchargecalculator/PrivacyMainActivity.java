package com.evchargecalculator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/** Adds the public privacy-policy link without changing the calculator UI layout. */
public class PrivacyMainActivity extends MainActivity {
    private static final String PRIVACY_URL = "https://mariskal19.github.io/EV-Calculator-PRO-Privacy/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachPrivacyLink(findViewById(android.R.id.content));
    }

    private void attachPrivacyLink(View view) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            CharSequence text = textView.getText();
            if (text != null && text.toString().startsWith("Powered by EV Calculator")) {
                textView.setText("Política de privacidad\n" + text);
                textView.setClickable(true);
                textView.setFocusable(true);
                textView.setContentDescription("Política de privacidad");
                textView.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_URL));
                    startActivity(intent);
                });
                return;
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                attachPrivacyLink(group.getChildAt(i));
            }
        }
    }
}
