package com.evchargecalculator;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class LanguageManager {
  private static final String PREFS = "ev_charge_calculator", KEY_LANGUAGE = "app_language";
  private static final String KEY_LANGUAGE_USER_SET = "app_language_user_set";
  private static final String[] LANGS = {"en", "es", "fr", "de", "it", "pt"};
  private static final Map<String, String[]> TR = new HashMap<>();

  private LanguageManager() {}

  public static String getSelectedLanguage(Context c) {
    android.content.SharedPreferences prefs = c.getSharedPreferences(PREFS, 0);
    if (!prefs.getBoolean(KEY_LANGUAGE_USER_SET, false)) {
      String system = getSystemLanguage(c);
      prefs.edit().putString(KEY_LANGUAGE, system).apply();
      return system;
    }
    String s = prefs.getString(KEY_LANGUAGE, null);
    return isSupported(s) ? s : "en";
  }

  public static String getEffectiveLanguage(Context c) {
    return getSelectedLanguage(c);
  }

  public static void setLanguage(Context c, String l) {
    String s = isSupported(l) ? l : "en";
    c.getSharedPreferences(PREFS, 0)
        .edit()
        .putString(KEY_LANGUAGE, s)
        .putBoolean(KEY_LANGUAGE_USER_SET, true)
        .apply();
    apply(c, s);
  }

  public static void applyStored(Context c) {
    apply(c, getSelectedLanguage(c));
  }

  public static void apply(Context c, String l) {
    String s = isSupported(l) ? l : "en";
    Locale locale = Locale.forLanguageTag(s);
    Locale.setDefault(locale);
    Configuration cfg = new Configuration(c.getResources().getConfiguration());
    cfg.setLocale(locale);
    c.getResources().updateConfiguration(cfg, c.getResources().getDisplayMetrics());
  }

  private static String getSystemLanguage(Context c) {
    Locale systemLocale;
    Configuration systemConfig = android.content.res.Resources.getSystem().getConfiguration();
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      systemLocale = systemConfig.getLocales().isEmpty() ? null : systemConfig.getLocales().get(0);
    } else {
      systemLocale = systemConfig.locale;
    }
    String s = systemLocale == null ? null : systemLocale.getLanguage();
    return isSupported(s) ? s : "en";
  }

  public static boolean isSupported(String l) {
    if (l == null) return false;
    for (String s : LANGS) if (s.equals(l)) return true;
    return false;
  }

  public static String displayName(String l) {
    if ("en".equals(l)) return "🇬🇧  English";
    if ("es".equals(l)) return "🇪🇸  Español";
    if ("fr".equals(l)) return "🇫🇷  Français";
    if ("de".equals(l)) return "🇩🇪  Deutsch";
    if ("it".equals(l)) return "🇮🇹  Italiano";
    if ("pt".equals(l)) return "🇵🇹  Português";
    return l;
  }

  public static void showSelector(Activity a) {
    String[] c = LANGS,
        names =
            {
              displayName("en"),
              displayName("es"),
              displayName("fr"),
              displayName("de"),
              displayName("it"),
              displayName("pt")
            };
    int checked = -1;
    String cur = getSelectedLanguage(a);
    for (int i = 0; i < c.length; i++) if (c[i].equals(cur)) checked = i;
    new android.app.AlertDialog.Builder(a)
        .setTitle(t(a, "Idioma"))
        .setSingleChoiceItems(
            names,
            checked,
            (d, w) -> {
              setLanguage(a, c[w]);
              d.dismiss();
              if (a instanceof ElectricVsCombustionActivity) {
                ((ElectricVsCombustionActivity) a).refreshLanguage();
              } else if (a instanceof MainActivity) {
                ((MainActivity) a).rebuildTheme();
                a.getWindow()
                    .getDecorView()
                    .post(
                        () -> {
                          translateViews(a);
                          a.getWindow().getDecorView().invalidate();
                        });
              } else {
                translateViews(a);
                a.getWindow()
                    .getDecorView()
                    .post(
                        () -> {
                          translateViews(a);
                          a.getWindow().getDecorView().invalidate();
                        });
              }
            })
        .show();
  }

  static {
    add("Herramientas", "Tools", "Outils", "Werkzeuge", "Strumenti", "Ferramentas");
    add("Calcula y planifica la carga de tu vehículo eléctrico.", "Calculate and plan your electric vehicle charging.", "Calculez et planifiez la recharge de votre véhicule électrique.", "Berechne und plane das Laden deines Elektrofahrzeugs.", "Calcola e pianifica la ricarica del tuo veicolo elettrico.", "Calcule e planeie o carregamento do seu veículo elétrico.");
    add("Electric Vs\nCombustion Calculator", "Electric vs\nCombustion", "Eléctrico vs\nCombustión", "Électrique vs\nCombustion", "Elektro vs\nVerbrenner", "Elettrico vs\nCombustione", "Elétrico vs\nCombustão");
    add("Datos del viaje", "Trip details", "Données du trajet", "Reisedaten", "Dati del viaggio", "Dados da viagem");
    add("Distancia", "Distance", "Distance", "Entfernung", "Distanza", "Distância");
    add("Vehículo eléctrico", "Electric vehicle", "Véhicule électrique", "Elektrofahrzeug", "Veicolo elettrico", "Veículo elétrico");
    add("Consumo", "Consumption", "Consommation", "Verbrauch", "Consumo", "Consumo");
    add("Precio electricidad", "Electricity price", "Prix de l'électricité", "Strompreis", "Prezzo dell'elettricità", "Preço da eletricidade");
    add("Vehículo de combustión", "Combustion vehicle", "Véhicule à combustion", "Verbrennungsfahrzeug", "Veicolo a combustione", "Veículo a combustão");
    add("Precio combustible", "Fuel price", "Prix du carburant", "Kraftstoffpreis", "Prezzo del carburante", "Preço do combustível");
    add("Resultado del viaje", "Trip result", "Résultat du trajet", "Reiseergebnis", "Risultato del viaggio", "Resultado da viagem");
    add("Eléctrico", "Electric", "Électrique", "Elektrisch", "Elettrico", "Elétrico");
    add("Combustión", "Combustion", "Combustion", "Verbrennung", "Combustione", "Combustão");
    add("Ahorro con el eléctrico:", "Savings with the EV:", "Économie avec l'électrique :", "Ersparnis mit dem Elektrofahrzeug:", "Risparmio con l'elettrico:", "Poupança com o elétrico:");
    add("de ahorro", "savings", "d'économies", "Ersparnis", "di risparmio", "de poupança");
    add("Diferencia:", "Difference:", "Différence :", "Differenz:", "Differenza:", "Diferença:");
    add("El eléctrico cuesta", "The EV costs", "L'électrique coûte", "Das Elektrofahrzeug kostet", "L'elettrico costa", "O elétrico custa");
    add("más", "more", "de plus", "mehr", "in più", "mais");
    add("Política de privacidad", "Privacy policy", "Politique de confidentialité", "Datenschutzerklärung", "Informativa sulla privacy", "Política de privacidade");
    add("Volver a EV Calculator PRO Principal", "Back to EV Calculator PRO Home", "Retour à l'accueil EV Calculator PRO", "Zur EV Calculator PRO Startseite", "Torna alla schermata principale di EV Calculator PRO", "Voltar à página inicial do EV Calculator PRO");
    add("Menú de la aplicación", "App menu", "Menu de l'application", "App-Menü", "Menu dell'app", "Menu da aplicação");
    add("Idioma", "Language", "Langue", "Sprache", "Lingua", "Idioma");
    add("Compartir app", "Share app", "Partager l'application", "App teilen", "Condividi app", "Partilhar app");
    add("Calificar app", "Rate app", "Noter l'application", "App bewerten", "Valuta app", "Avaliar app");
    add("Cambiar a tema claro", "Switch to light theme", "Passer au thème clair", "Helles Design", "Tema chiaro", "Tema claro");
    add("Cambiar a tema oscuro", "Switch to dark theme", "Passer au thème sombre", "Dunkles Design", "Tema scuro", "Tema escuro");
    add("Hora", "Time", "Heure", "Uhrzeit", "Ora", "Hora");
    add("Hora de Salida", "Departure Time", "Heure de départ", "Abfahrtszeit", "Ora di partenza", "Hora de saída");
    add("EV Charge Calculator", "EV Charge Calculator", "Calculateur de recharge EV", "EV-Laderechner", "Calcolatore di ricarica EV", "Calculadora de carga EV");
    add("Descarga EV Calculator PRO en Google Play", "Download EV Calculator PRO on Google Play", "Télécharger EV Calculator PRO sur Google Play", "EV Calculator PRO bei Google Play herunterladen", "Scarica EV Calculator PRO su Google Play", "Descarregar EV Calculator PRO no Google Play");
    add("Batería", "Battery", "Batterie", "Batterie", "Batteria", "Bateria");
    add("Capacidad", "Capacity", "Capacité", "Kapazität", "Capacità", "Capacidade");
    add("Cargar la batería desde", "Charge the battery from", "Charger la batterie de", "Batterie laden von", "Carica la batteria dal", "Carregar a bateria de");
    add("al", "to", "à", "bis", "al", "a");
    add("Se cargará", "Will charge", "Charge prévue", "Wird geladen", "Verrà caricata", "Será carregada");
    add("Carga", "Charging", "Recharge", "Laden", "Ricarica", "Carregamento");
    add("Charge", "Charge", "Recharge", "Laden", "Ricarica", "Carregamento");
    add("Potencia", "Power", "Puissance", "Leistung", "Potenza", "Potência");
    add("Precio energía", "Energy price", "Prix de l'énergie", "Energiepreis", "Prezzo energia", "Preço da energia");
    add("Tiempo de Carga", "Charging Time", "Temps de charge", "Ladezeit", "Tempo di carregamento", "Tempo de carregamento");
    add("Coste de carga", "Charging cost", "Coût de recharge", "Ladekosten", "Costo di ricarica", "Custo de carregamento");
    add("Hora Inicio Recomendada", "Recommended Start Time", "Heure de début recommandée", "Empfohlene Startzeit", "Ora di inizio consigliata", "Hora de início recomendada");
    add("Pérdidas de carga", "Charging losses", "Pertes de recharge", "Ladeverluste", "Perdite di ricarica", "Perdas de carregamento");
    add("Información sobre pérdidas de carga", "Charging loss information", "Informations sur les pertes de recharge", "Informationen zu den Ladeverlusten", "Informazioni sulle perdite di ricarica", "Informações sobre perdas de carregamento");
    add("Aceptar", "OK", "OK", "OK", "OK", "OK");
    add("No llegas a tiempo", "You won't make it in time", "Vous n'arriverez pas à temps", "Du schaffst es nicht rechtzeitig", "Non arriverai in tempo", "Não chegará a tempo");
    add("Faltan", "Remaining", "Restantes", "Verbleibend", "Mancano", "Faltam");
    add("Hora de salida no válida", "Invalid departure time", "Heure de départ non valide", "Ungültige Abfahrtszeit", "Ora di partenza non valida", "Hora de saída inválida");
    add("Comparar coches", "Compare cars", "Comparer les voitures", "Autos vergleichen", "Confronta auto", "Comparar carros");
    add("Atrás", "Back", "Retour", "Zurück", "Indietro", "Voltar");
    add("Selecciona vehículo", "Select vehicle", "Sélectionnez un véhicule", "Fahrzeug auswählen", "Seleziona veicolo", "Selecionar veículo");
    add("Versión", "Version", "Version", "Version", "Versione", "Versão");
    add("Año", "Year", "Année", "Jahr", "Anno", "Ano");
    add("Tipo batería", "Battery type", "Type de batterie", "Batterietyp", "Tipo di batteria", "Tipo de bateria");
    add("Potencia máxima", "Max power", "Puissance maximale", "Maximale Leistung", "Potenza massima", "Potência máxima");
    add("Autonomía WLTP", "WLTP range", "Autonomie WLTP", "WLTP-Reichweite", "Autonomia WLTP", "Autonomia WLTP");
    add("Precio", "Price", "Prix", "Preis", "Prezzo", "Preço");
    add("No disponible", "Not available", "Non disponible", "Nicht verfügbar", "Non disponibile", "Não disponível");
    add("Elige tus vehículos", "Choose your vehicles", "Choisissez vos véhicules", "Wähle deine Fahrzeuge", "Scegli i tuoi veicoli", "Escolha os seus veículos");
    add("Añade hasta 3 coches para ver sus características y compararlos.", "Add up to 3 cars to see their features and compare them.", "Ajoutez jusqu'à 3 voitures pour voir leurs caractéristiques et les comparer.", "Füge bis zu 3 Fahrzeuge hinzu, um ihre Eigenschaften zu sehen und sie zu vergleichen.", "Aggiungi fino a 3 auto per vederne le caratteristiche e confrontarle.", "Adicione até 3 carros para ver as suas características e compará-los.");
    add("Características", "Features", "Caractéristiques", "Eigenschaften", "Caratteristiche", "Características");
    add("Mejor valor", "Best value", "Meilleure valeur", "Bester Wert", "Miglior valore", "Melhor valor");
    add("Todos los vehículos", "All vehicles", "Tous les véhicules", "Alle Fahrzeuge", "Tutti i veicoli", "Todos os veículos");
    add("No se encontraron vehículos", "No vehicles found", "Aucun véhicule trouvé", "Keine Fahrzeuge gefunden", "Nessun veicolo trovato", "Nenhum veículo encontrado");
    add("Resumen de la comparativa", "Comparison summary", "Résumé de la comparaison", "Vergleichszusammenfassung", "Riepilogo del confronto", "Resumo da comparação");
    add("Resultado rápido de los vehículos seleccionados", "Quick result for the selected vehicles", "Résultat rapide des véhicules sélectionnés", "Schneller Überblick über die ausgewählten Fahrzeuge", "Risultato rapido dei veicoli selezionati", "Resultado rápido dos veículos selecionados");
    add("Vehículos comparados", "Compared vehicles", "Véhicules comparés", "Verglichene Fahrzeuge", "Veicoli confrontati", "Veículos comparados");
    add("Comentario", "Comment", "Commentaire", "Kommentar", "Commento", "Comentário");
    add("Autonomía", "Range", "Autonomie", "Reichweite", "Autonomia", "Autonomia");
    add("Carga DC", "DC charging", "Recharge DC", "DC-Laden", "Ricarica DC", "Carregamento DC");
    add("Error al abrir Comparar coches", "Error opening Compare cars", "Erreur lors de l'ouverture de Comparer les voitures", "Fehler beim Öffnen von Autos vergleichen", "Errore nell'apertura di Confronta auto", "Erro ao abrir Comparar carros");
    add("Añadir coche", "Add car", "Ajouter une voiture", "Auto hinzufügen", "Aggiungi auto", "Adicionar carro");
    add("Batería y autonomía", "Battery & range", "Batterie et autonomie", "Batterie & Reichweite", "Batteria e autonomia", "Bateria e autonomia");
    add("Prestaciones", "Performance", "Performances", "Leistung", "Prestazioni", "Desempenho");
    add("Tracción", "Drivetrain", "Transmission", "Antrieb", "Trazione", "Tração");
    add("Carga AC", "AC charging", "Recharge AC", "AC-Laden", "Ricarica AC", "Carregamento AC");
    add("Practicidad", "Practicality", "Praticité", "Praktikabilität", "Praticità", "Praticidade");
    add("Maletero", "Boot space", "Coffre", "Kofferraum", "Bagagliaio", "Bagageira");
    add("Peso", "Weight", "Poids", "Gewicht", "Peso", "Peso");
    add("Selecciona un coche para mostrar sus características.", "Select a car to show its features.", "Sélectionnez une voiture pour afficher ses caractéristiques.", "Wähle ein Fahrzeug aus, um seine Eigenschaften anzuzeigen.", "Seleziona un'auto per visualizzarne le caratteristiche.", "Selecione um carro para ver as suas características.");
    add("Marca, modelo, año, batería o versión", "Make, model, year, battery or version", "Marque, modèle, année, batterie ou version", "Marke, Modell, Jahr, Batterie oder Version", "Marca, modello, anno, batteria o versione", "Marca, modelo, ano, bateria ou versão");
    add("Mercado", "Market", "Marché", "Markt", "Mercato", "Mercado");
    add("✕  Quitar", "✕  Remove", "✕  Supprimer", "✕  Entfernen", "✕  Rimuovi", "✕  Remover");
    add("Menú", "Menu", "Menu", "Menü", "Menu", "Menu");
    add("En conjunto, ", "Overall, ", "Dans l'ensemble, ", "Insgesamt, ", "Nel complesso, ", "No geral, ");
    add(" destaca por autonomía", " stands out for range", " se distingue par son autonomie", " überzeugt durch seine Reichweite", " si distingue per l'autonomia", " destaca pela autonomia");
    add(", mientras que ", ", while ", ", tandis que ", ", während ", ", mentre ", ", enquanto ");
    add(" ofrece el menor consumo", " offers the lowest consumption", " offre la consommation la plus faible", " bietet den niedrigsten Verbrauch", " offre il consumo più basso", " oferece o menor consumo");
    add(" es la opción más económica", " is the most affordable option", " est l'option la plus économique", " ist die günstigste Option", " è l'opzione più economica", " é a opção mais económica");
    add(". La elección final dependerá de si priorizas autonomía, eficiencia, prestaciones, velocidad de carga o precio.", ". The final choice depends on whether you prioritize range, efficiency, performance, charging speed or price.", ". Le choix final dépendra de votre priorité : autonomie, efficacité, performances, vitesse de recharge ou prix.", ". Die endgültige Wahl hängt davon ab, ob du Reichweite, Effizienz, Leistung, Ladegeschwindigkeit oder Preis priorisierst.", ". La scelta finale dipenderà dalla tua priorità: autonomia, efficienza, prestazioni, velocità di ricarica o prezzo.", ". A escolha final dependerá de priorizar autonomia, eficiência, desempenho, velocidade de carregamento ou preço.");
  }

  private static void add(String... v) {
    if (v.length >= 2) TR.put(v[0], v);
  }

  public static String t(Context c, String spanish) {
    if (spanish == null || spanish.isEmpty()) return spanish;
    String lang = getSelectedLanguage(c);
    String[] v = TR.get(spanish);
    if (v != null) return translatedValue(v, lang);
    String result = spanish;
    java.util.List<String> keys = new java.util.ArrayList<>(TR.keySet());
    java.util.Collections.sort(keys, (a, b) -> Integer.compare(b.length(), a.length()));
    for (String key : keys) {
      if (key == null || key.isEmpty() || !result.contains(key)) continue;
      String[] entry = TR.get(key);
      if (entry != null) result = result.replace(key, translatedValue(entry, lang));
    }
    return result;
  }

  private static String translatedValue(String[] v, String lang) {
    int i;
    if ("es".equals(lang)) i = 0;
    else if ("en".equals(lang)) i = 1;
    else if ("fr".equals(lang)) i = 2;
    else if ("de".equals(lang)) i = 3;
    else if ("it".equals(lang)) i = 4;
    else if ("pt".equals(lang)) i = 5;
    else i = 1;
    return (i < v.length && v[i] != null) ? v[i] : v[0];
  }

  public static void translateViews(Activity a) {
    translateViewTree(a, a.getWindow().getDecorView());
  }

  private static void translateViewTree(Context c, View v) {
    if (v instanceof TextView) {
      TextView t = (TextView) v;
      CharSequence text = t.getText();
      if (text != null) {
        String raw = text.toString();
        String translated = t(c, raw);
        if (!raw.equals(translated)) t.setText(translated);
      }
      CharSequence cd = t.getContentDescription();
      if (cd != null) {
        String rawCd = cd.toString();
        String translatedCd = t(c, rawCd);
        if (!rawCd.equals(translatedCd)) t.setContentDescription(translatedCd);
      }
    }
    if (v instanceof ViewGroup) {
      ViewGroup g = (ViewGroup) v;
      for (int i = 0; i < g.getChildCount(); i++) translateViewTree(c, g.getChildAt(i));
    }
  }
}
