package com.evchargecalculator;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

public class SupportProjectActivity extends BaseNavigationActivity {
  @Override protected int getBottomNavigationIndex() { return 3; }

  private int dp(int n) {
    return (int) (n * getResources().getDisplayMetrics().density + .5f);
  }

  private boolean dark() {
    return getSharedPreferences("ev_charge_calculator", MODE_PRIVATE)
        .getBoolean("dark_theme", false);
  }

  private int text() { return dark() ? Color.rgb(245,248,255) : Color.rgb(22,42,63); }
  private int sub() { return dark() ? Color.rgb(170,183,204) : Color.rgb(90,111,137); }

  private GradientDrawable bg(int color, int radius) {
    GradientDrawable g = new GradientDrawable();
    g.setColor(color);
    g.setCornerRadius(dp(radius));
    return g;
  }

  private String tr(String es, String en, String fr, String de, String it, String pt) {
    String l = LanguageManager.getSelectedLanguage(this);
    if ("es".equals(l)) return es;
    if ("fr".equals(l)) return fr;
    if ("de".equals(l)) return de;
    if ("it".equals(l)) return it;
    if ("pt".equals(l)) return pt;
    return en;
  }

  @Override protected void onCreate(Bundle state) {
    super.onCreate(state);

    FrameLayout frame = new FrameLayout(this);
    PremiumBackgroundView background = new PremiumBackgroundView(this);
    background.setDark(dark());
    background.setShowVehicle(false);
    frame.addView(background, new FrameLayout.LayoutParams(-1,-1));

    ScrollView scroll = new ScrollView(this);
    scroll.setFillViewport(true);
    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(12),dp(12),dp(12),dp(84));
    scroll.addView(root);
    frame.addView(scroll,new FrameLayout.LayoutParams(-1,-1));
    setContentView(frame);

    TextView title = new TextView(this);
    title.setText("☕  " + tr("Apoyar el proyecto","Support the project","Soutenir le projet",
        "Projekt unterstützen","Supporta il progetto","Apoiar o projeto"));
    title.setTextSize(22);
    title.setTextColor(text());
    title.setTypeface(null,1);
    title.setGravity(Gravity.CENTER);
    root.addView(title,new LinearLayout.LayoutParams(-1,dp(58)));

    LinearLayout card = new LinearLayout(this);
    card.setOrientation(LinearLayout.VERTICAL);
    card.setPadding(dp(20),dp(22),dp(20),dp(22));
    card.setBackground(bg(dark()?Color.argb(225,21,31,42):Color.WHITE,22));

    TextView heading = new TextView(this);
    heading.setText(tr("Gracias por apoyar EV Calculator PRO",
        "Thank you for supporting EV Calculator PRO","Merci de soutenir EV Calculator PRO",
        "Danke, dass du EV Calculator PRO unterstützt","Grazie per supportare EV Calculator PRO",
        "Obrigado por apoiar o EV Calculator PRO"));
    heading.setTextSize(19); heading.setTextColor(text()); heading.setTypeface(null,1);
    card.addView(heading);

    TextView body = new TextView(this);
    body.setText(tr(
        "La app es gratuita. Si te resulta útil, puedes hacer una pequeña aportación para ayudar a mantenerla y seguir mejorándola.",
        "The app is free. If you find it useful, you can make a small contribution to help maintain and improve it.",
        "L'application est gratuite. Si elle vous est utile, vous pouvez faire une petite contribution pour aider à la maintenir et à l'améliorer.",
        "Die App ist kostenlos. Wenn sie dir hilft, kannst du mit einem kleinen Beitrag ihre Pflege und Weiterentwicklung unterstützen.",
        "L'app è gratuita. Se ti è utile, puoi contribuire con una piccola somma per aiutarne la manutenzione e lo sviluppo.",
        "A aplicação é gratuita. Se for útil para si, pode contribuir com um pequeno valor para ajudar na manutenção e evolução."));
    body.setTextSize(15); body.setTextColor(sub());
    body.setPadding(0,dp(10),0,dp(18));
    card.addView(body);

    TextView amounts = new TextView(this);
    amounts.setText("☕   1 €     ·     ☕☕   3 €     ·     ☕☕☕   5 €");
    amounts.setTextSize(17); amounts.setTextColor(text());
    amounts.setGravity(Gravity.CENTER);
    amounts.setPadding(0,dp(12),0,dp(12));
    amounts.setBackground(bg(dark()?Color.rgb(21,34,51):Color.rgb(246,249,253),16));
    card.addView(amounts,new LinearLayout.LayoutParams(-1,dp(58)));

    TextView info = new TextView(this);
    info.setText(tr(
        "Estamos preparando el sistema de aportaciones. Cuando esté disponible, podrás elegir libremente la cantidad que quieras aportar.",
        "We are preparing the support system. When available, you will be able to freely choose the amount you want to contribute.",
        "Nous préparons le système de soutien. Lorsqu'il sera disponible, vous pourrez choisir librement le montant de votre contribution.",
        "Wir bereiten das Unterstützungssystem vor. Sobald es verfügbar ist, kannst du den gewünschten Betrag frei wählen.",
        "Stiamo preparando il sistema di supporto. Quando sarà disponibile, potrai scegliere liberamente l'importo da contribuire.",
        "Estamos a preparar o sistema de apoio. Quando estiver disponível, poderá escolher livremente o valor da contribuição."));
    info.setTextSize(13); info.setTextColor(sub()); info.setGravity(Gravity.CENTER);
    info.setPadding(dp(8),dp(16),dp(8),dp(4));
    card.addView(info);

    root.addView(card,new LinearLayout.LayoutParams(-1,-2));

    TextView note = new TextView(this);
    note.setText(tr("Sin suscripciones ni funciones bloqueadas.",
        "No subscriptions and no locked features.","Aucun abonnement ni fonctionnalité bloquée.",
        "Keine Abonnements und keine gesperrten Funktionen.","Nessun abbonamento né funzione bloccata.",
        "Sem subscrições nem funcionalidades bloqueadas."));
    note.setTextSize(12); note.setTextColor(sub()); note.setGravity(Gravity.CENTER);
    note.setPadding(0,dp(14),0,0);
    root.addView(note,new LinearLayout.LayoutParams(-1,dp(42)));
  }
}
