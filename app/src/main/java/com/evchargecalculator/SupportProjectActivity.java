package com.evchargecalculator;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class SupportProjectActivity extends BaseNavigationActivity {
  /*
   * Stripe Payment Link:
   * Paste the final Stripe Payment Link here when it is created.
   * The link should be configured in Stripe to let the donor choose the amount.
   */
  private static final String STRIPE_PAYMENT_LINK = "";

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

  private Button stripeButton() {
    Button button = new Button(this);
    button.setText("☕  " + tr(
        "Apoyar con Stripe",
        "Support with Stripe",
        "Soutenir avec Stripe",
        "Mit Stripe unterstützen",
        "Supporta con Stripe",
        "Apoiar com Stripe"));
    button.setTextSize(16);
    button.setTextColor(Color.WHITE);
    button.setAllCaps(false);
    button.setTypeface(null, 1);
    button.setGravity(Gravity.CENTER);
    button.setBackground(bg(Color.rgb(99, 91, 255), 16));
    button.setPadding(dp(12), 0, dp(12), 0);
    button.setOnClickListener(v -> openStripe());
    return button;
  }

  private void openStripe() {
    if (STRIPE_PAYMENT_LINK.isEmpty()) {
      Toast.makeText(this, tr(
          "El enlace de Stripe todavía no está configurado.",
          "The Stripe link has not been configured yet.",
          "Le lien Stripe n'est pas encore configuré.",
          "Der Stripe-Link ist noch nicht konfiguriert.",
          "Il link Stripe non è ancora configurato.",
          "O link Stripe ainda não foi configurado."), Toast.LENGTH_SHORT).show();
      return;
    }

    try {
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(STRIPE_PAYMENT_LINK));
      startActivity(intent);
    } catch (Exception ignored) {
      Toast.makeText(this, tr(
          "No se ha podido abrir el enlace de pago.",
          "The payment link could not be opened.",
          "Impossible d'ouvrir le lien de paiement.",
          "Der Zahlungslink konnte nicht geöffnet werden.",
          "Impossibile aprire il link di pagamento.",
          "Não foi possível abrir o link de pagamento."), Toast.LENGTH_SHORT).show();
    }
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
            "Puedes apoyar su mantenimiento y desarrollo con una aportación voluntaria de la cantidad que tú elijas.",
            "You can support its maintenance and development with a voluntary contribution of any amount you choose.",
            "Vous pouvez soutenir sa maintenance et son développement avec une contribution volontaire du montant de votre choix.",
            "Du kannst ihre Pflege und Weiterentwicklung mit einem freiwilligen Beitrag in der von dir gewählten Höhe unterstützen.",
            "Puoi sostenere la manutenzione e lo sviluppo con un contributo volontario dell'importo che preferisci.",
            "Pode apoiar a manutenção e evolução com um contributo voluntário do valor que escolher."),
        15, sub());
    body.setPadding(0, dp(9), 0, dp(18));
    card.addView(body);

    LinearLayout paymentBox = new LinearLayout(this);
    paymentBox.setOrientation(LinearLayout.VERTICAL);
    paymentBox.setGravity(Gravity.CENTER);
    paymentBox.setPadding(dp(14), dp(14), dp(14), dp(14));
    paymentBox.setBackground(bg(softColor(), 18));

    TextView paymentTitle = label(
        tr("Cantidad libre",
            "Choose your amount",
            "Montant libre",
            "Freier Betrag",
            "Importo libero",
            "Valor livre"),
        15, text());
    paymentTitle.setTypeface(null, 1);
    paymentTitle.setGravity(Gravity.CENTER);
    paymentBox.addView(paymentTitle);

    TextView paymentBody = label(
        tr(
            "Tú decides cuánto aportar. El pago se realizará de forma segura en Stripe.",
            "You decide how much to contribute. Payment will be securely handled by Stripe.",
            "Vous choisissez le montant. Le paiement sera effectué en toute sécurité avec Stripe.",
            "Du entscheidest, wie viel du beitragen möchtest. Die Zahlung wird sicher über Stripe abgewickelt.",
            "Decidi tu quanto contribuire. Il pagamento sarà gestito in modo sicuro da Stripe.",
            "Você decide quanto contribuir. O pagamento será processado de forma segura pelo Stripe."),
        13, sub());
    paymentBody.setGravity(Gravity.CENTER);
    paymentBody.setPadding(0, dp(7), 0, dp(13));
    paymentBox.addView(paymentBody);

    Button stripe = stripeButton();
    paymentBox.addView(stripe, new LinearLayout.LayoutParams(-1, dp(52)));

    TextView methods = label(
        tr(
            "Tarjeta · Google Pay · otros métodos disponibles",
            "Card · Google Pay · other available methods",
            "Carte · Google Pay · autres moyens disponibles",
            "Karte · Google Pay · weitere verfügbare Zahlungsmethoden",
            "Carta · Google Pay · altri metodi disponibili",
            "Cartão · Google Pay · outros métodos disponíveis"),
        12, sub());
    methods.setGravity(Gravity.CENTER);
    methods.setPadding(0, dp(9), 0, 0);
    paymentBox.addView(methods);

    card.addView(paymentBox);

    TextView status = label(
        tr(
            "El enlace de pago se abrirá fuera de la app para completar la aportación.",
            "The payment link will open outside the app to complete your contribution.",
            "Le lien de paiement s'ouvrira en dehors de l'application pour terminer votre contribution.",
            "Der Zahlungslink wird außerhalb der App geöffnet, um deinen Beitrag abzuschließen.",
            "Il link di pagamento si aprirà fuori dall'app per completare il contributo.",
            "O link de pagamento será aberto fora da aplicação para concluir o seu contributo."),
        12, sub());
    status.setGravity(Gravity.CENTER);
    status.setPadding(dp(8), dp(13), dp(8), dp(3));
    card.addView(status);

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
