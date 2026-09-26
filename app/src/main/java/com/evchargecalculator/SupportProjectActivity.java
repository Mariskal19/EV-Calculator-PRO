package com.evchargecalculator;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
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

  private int text() {
    return dark() ? Color.rgb(245,248,255) : Color.rgb(22,42,63);
  }

  private int sub() {
    return dark() ? Color.rgb(170,183,204) : Color.rgb(90,111,137);
  }

  private int cardColor() {
    return dark() ? Color.argb(232,21,31,42) : Color.WHITE;
  }

  private int softColor() {
    return dark() ? Color.rgb(29,43,60) : Color.rgb(246,249,253);
  }

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

  private TextView label(String value, float size, int color) {
    TextView v = new TextView(this);
    v.setText(value);
    v.setTextSize(size);
    v.setTextColor(color);
    return v;
  }

  private TextView supportOption(String amount) {
    TextView option = label("☕  " + amount, 17, Color.WHITE);
    option.setGravity(Gravity.CENTER);
    option.setTypeface(null, 1);
    option.setBackground(bg(Color.rgb(214, 55, 72), 16));
    option.setPadding(dp(12), 0, dp(12), 0);
    option.setAlpha(0.92f);
    return option;
  }

  @Override protected void onCreate(Bundle state) {
    super.onCreate(state);

    FrameLayout frame = new FrameLayout(this);
    PremiumBackgroundView background = new PremiumBackgroundView(this);
    background.setDark(dark());
    background.setShowVehicle(false);
    frame.addView(background, new FrameLayout.LayoutParams(-1, -1));

    ScrollView scroll = new ScrollView(this);
    scroll.setFillViewport(true);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(12), dp(12), dp(12), dp(84));
    scroll.addView(root);
    frame.addView(scroll, new FrameLayout.LayoutParams(-1, -1));

    setContentView(frame);
    EdgeToEdgeHelper.apply(this, dark());

    TextView title = label(
        "☕  " + tr(
            "Apoyar EV Calculator PRO",
            "Support EV Calculator PRO",
            "Soutenir EV Calculator PRO",
            "EV Calculator PRO unterstützen",
            "Supporta EV Calculator PRO",
            "Apoiar o EV Calculator PRO"),
        22, text());
    title.setTypeface(null, 1);
    title.setGravity(Gravity.CENTER);
    root.addView(title, new LinearLayout.LayoutParams(-1, dp(58)));

    TextView identity = label(
        tr("Independiente · Sin publicidad",
            "Independent · Ad-free",
            "Indépendante · Sans publicité",
            "Unabhängig · Werbefrei",
            "Indipendente · Senza pubblicità",
            "Independente · Sem publicidade"),
        14, sub());
    identity.setGravity(Gravity.CENTER);
    identity.setPadding(0, 0, 0, dp(12));
    root.addView(identity, new LinearLayout.LayoutParams(-1, dp(34)));

    LinearLayout card = new LinearLayout(this);
    card.setOrientation(LinearLayout.VERTICAL);
    card.setPadding(dp(20), dp(22), dp(20), dp(20));
    card.setBackground(bg(cardColor(), 22));

    TextView heading = label(
        tr("¿Te resulta útil la app?",
            "Is the app useful to you?",
            "L'application vous est-elle utile ?",
            "Ist die App für dich nützlich?",
            "L'app ti è utile?",
            "A aplicação é útil para si?"),
        19, text());
    heading.setTypeface(null, 1);
    card.addView(heading);

    TextView body = label(
        tr(
            "Puedes apoyar su mantenimiento y desarrollo con una pequeña aportación voluntaria.",
            "You can support its maintenance and development with a small voluntary contribution.",
            "Vous pouvez soutenir sa maintenance et son développement avec une petite contribution volontaire.",
            "Du kannst ihre Pflege und Weiterentwicklung mit einem kleinen freiwilligen Beitrag unterstützen.",
            "Puoi sostenere la manutenzione e lo sviluppo con un piccolo contributo volontario.",
            "Pode apoiar a manutenção e evolução com uma pequena contribuição voluntária."),
        15, sub());
    body.setPadding(0, dp(9), 0, dp(18));
    card.addView(body);

    TextView optionsTitle = label(
        tr("Aportación orientativa",
            "Suggested contribution",
            "Contribution indicative",
            "Vorgeschlagener Beitrag",
            "Contributo indicativo",
            "Contribuição sugerida"),
        13, sub());
    optionsTitle.setGravity(Gravity.CENTER);
    optionsTitle.setPadding(0, 0, 0, dp(8));
    card.addView(optionsTitle);

    LinearLayout options = new LinearLayout(this);
    options.setOrientation(LinearLayout.HORIZONTAL);
    options.setGravity(Gravity.CENTER);
    options.setWeightSum(2);

    TextView one = supportOption("1 €");
    TextView three = supportOption("3 €");

    LinearLayout.LayoutParams optionParams =
        new LinearLayout.LayoutParams(0, dp(52), 1f);
    optionParams.setMargins(dp(4), 0, dp(4), 0);
    options.addView(one, optionParams);
    options.addView(three, optionParams);
    card.addView(options);

    TextView status = label(
        tr("Las aportaciones estarán disponibles próximamente.",
            "Support will be available soon.",
            "Le soutien sera bientôt disponible.",
            "Die Unterstützung wird bald verfügbar sein.",
            "Il supporto sarà disponibile prossimamente.",
            "O apoio estará disponível em breve."),
        13, sub());
    status.setGravity(Gravity.CENTER);
    status.setPadding(dp(8), dp(14), dp(8), dp(4));
    card.addView(status);

    TextView playNote = label(
        tr(
            "Cuando esté disponible, Google Play mostrará el importe y la moneda correspondientes a tu país.",
            "When available, Google Play will show the amount and currency for your country.",
            "Lorsqu'il sera disponible, Google Play affichera le montant et la devise correspondant à votre pays.",
            "Sobald verfügbar, zeigt Google Play den für dein Land geltenden Betrag und die Währung an.",
            "Quando sarà disponibile, Google Play mostrerà l'importo e la valuta del tuo paese.",
            "Quando estiver disponível, o Google Play mostrará o valor e a moeda correspondentes ao seu país."),
        12, sub());
    playNote.setGravity(Gravity.CENTER);
    playNote.setPadding(dp(8), dp(10), dp(8), 0);
    card.addView(playNote);

    root.addView(card, new LinearLayout.LayoutParams(-1, -2));

    TextView noFeatures = label(
        tr("Aportación voluntaria · Sin suscripciones ni funciones bloqueadas.",
            "Voluntary support · No subscriptions or locked features.",
            "Soutien volontaire · Aucun abonnement ni fonctionnalité bloquée.",
            "Freiwillige Unterstützung · Keine Abonnements oder gesperrten Funktionen.",
            "Supporto volontario · Nessun abbonamento o funzione bloccata.",
            "Apoio voluntário · Sem subscrições nem funcionalidades bloqueadas."),
        12, sub());
    noFeatures.setGravity(Gravity.CENTER);
    noFeatures.setPadding(dp(8), dp(14), dp(8), 0);
    root.addView(noFeatures, new LinearLayout.LayoutParams(-1, dp(48)));

    TextView thanks = label(
        "♥  " + tr(
            "Gracias por ayudar a que EV Calculator PRO siga creciendo.",
            "Thank you for helping EV Calculator PRO keep growing.",
            "Merci d'aider EV Calculator PRO à continuer d'évoluer.",
            "Danke, dass du EV Calculator PRO bei der Weiterentwicklung hilfst.",
            "Grazie per aiutare EV Calculator PRO a continuare a crescere.",
            "Obrigado por ajudar o EV Calculator PRO a continuar a evoluir."),
        13, sub());
    thanks.setGravity(Gravity.CENTER);
    thanks.setPadding(dp(8), dp(6), dp(8), dp(8));
    root.addView(thanks, new LinearLayout.LayoutParams(-1, dp(46)));
  }
}
