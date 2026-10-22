package com.evchargecalculator;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Local, deterministic first-pass interpreter for the conversational vehicle search.
 *
 * This class deliberately has no Android, network, storage, or catalog dependency.
 * It only updates in-memory criteria. Matching those criteria to Vehicle objects is
 * a separate step and must treat absent/non-positive numeric values as unknown.
 *
 * Supported patterns are intentionally bounded; unrecognized text is not guessed.
 */
public final class AdvisorCriteriaEngine {

    public enum Language { ES, EN, FR, DE, IT, PT }

    public enum Field {
        PRICE, RANGE, TRUNK, BATTERY, POWER, CONSUMPTION, BODY, DRIVETRAIN, MAKE_MODEL
    }

    public static final class Criteria {
        public Double minPrice;
        public Double maxPrice;
        public Double minRangeKm;
        public Double maxRangeKm;
        public Double minTrunkLiters;
        public Double maxTrunkLiters;
        public Double minBatteryKwh;
        public Double maxBatteryKwh;
        public Double minPowerKw;
        public Double maxPowerKw;
        public Double maxConsumption;
        public String bodyStyle;
        public String drivetrain;
        public String makeModel;
        public boolean preferLowConsumption;
        public boolean preferHighRange;
        public boolean preferLargeTrunk;

        public Criteria copy() {
            Criteria c = new Criteria();
            c.minPrice=minPrice; c.maxPrice=maxPrice;
            c.minRangeKm=minRangeKm; c.maxRangeKm=maxRangeKm;
            c.minTrunkLiters=minTrunkLiters; c.maxTrunkLiters=maxTrunkLiters;
            c.minBatteryKwh=minBatteryKwh; c.maxBatteryKwh=maxBatteryKwh;
            c.minPowerKw=minPowerKw; c.maxPowerKw=maxPowerKw;
            c.maxConsumption=maxConsumption;
            c.bodyStyle=bodyStyle; c.drivetrain=drivetrain; c.makeModel=makeModel;
            c.preferLowConsumption=preferLowConsumption;
            c.preferHighRange=preferHighRange; c.preferLargeTrunk=preferLargeTrunk;
            return c;
        }

        public void clear(Field field) {
            switch (field) {
                case PRICE: minPrice=null; maxPrice=null; break;
                case RANGE: minRangeKm=null; maxRangeKm=null; break;
                case TRUNK: minTrunkLiters=null; maxTrunkLiters=null; break;
                case BATTERY: minBatteryKwh=null; maxBatteryKwh=null; break;
                case POWER: minPowerKw=null; maxPowerKw=null; break;
                case CONSUMPTION: maxConsumption=null; preferLowConsumption=false; break;
                case BODY: bodyStyle=null; break;
                case DRIVETRAIN: drivetrain=null; break;
                case MAKE_MODEL: makeModel=null; break;
            }
        }

        public boolean hasHardFilters() {
            return minPrice!=null || maxPrice!=null || minRangeKm!=null || maxRangeKm!=null
                    || minTrunkLiters!=null || maxTrunkLiters!=null
                    || minBatteryKwh!=null || maxBatteryKwh!=null
                    || minPowerKw!=null || maxPowerKw!=null || maxConsumption!=null
                    || bodyStyle!=null || drivetrain!=null || makeModel!=null;
        }
    }

    public static final class ParseResult {
        public final Criteria criteria;
        public final boolean changed;
        public final String clarificationKey;
        public final String recognizedSummaryKey;

        private ParseResult(Criteria criteria, boolean changed, String clarificationKey,
                            String recognizedSummaryKey) {
            this.criteria=criteria;
            this.changed=changed;
            this.clarificationKey=clarificationKey;
            this.recognizedSummaryKey=recognizedSummaryKey;
        }
        public boolean needsClarification() { return clarificationKey != null; }
    }

    private static final Pattern NUMBER = Pattern.compile("(\\d[\\d\\s.,]*)");
    private static final Pattern PRICE = Pattern.compile(
            "(?:€|eur|euros?|euros?|\\$|usd|£|gbp)");
    private static final Pattern RANGE_UNIT = Pattern.compile("(?:km|kilomet(?:er|re)s?)");
    private static final Pattern TRUNK_UNIT = Pattern.compile("(?:l|lit(?:er|re)s?)");
    private static final Pattern BATTERY_UNIT = Pattern.compile("(?:kwh|kw\\s*h)");
    private static final Pattern POWER_UNIT = Pattern.compile("(?:kw|kilowatts?)");
    private static final Pattern CONSUMPTION_UNIT = Pattern.compile("(?:kwh\\s*/\\s*100\\s*km|kwh\\s*/\\s*100km)");

    private Criteria criteria = new Criteria();

    public Criteria snapshot() { return criteria.copy(); }

    /** Resets state explicitly; callers should also discard the engine when leaving the screen. */
    public void reset() { criteria = new Criteria(); }

    public ParseResult interpret(String userText, Language language) {
        String original = userText == null ? "" : userText.trim();
        if (original.isEmpty()) return result(false, "empty_query", null);

        String s = normalize(original);
        boolean changed = false;
        String clarification = null;
        String lang = language == null ? "es" : language.name().toLowerCase(Locale.ROOT);

        // Explicit removal clears only the named criterion.
        Field removal = removalField(s, lang);
        if (removal != null) {
            criteria.clear(removal);
            return result(true, null, "criterion_removed");
        }

        NumberMatch price = findNumeric(s, lang, Field.PRICE);
        if (price != null) {
            if (price.ambiguous) clarification = "clarify_price";
            else {
                if (price.lowerBound) criteria.minPrice=price.value;
                else criteria.maxPrice=price.value;
                changed=true;
            }
        }

        NumberMatch range = findNumeric(s, lang, Field.RANGE);
        if (range != null) {
            if (range.ambiguous) clarification = "clarify_range";
            else { if (range.lowerBound) criteria.minRangeKm=range.value; else criteria.maxRangeKm=range.value; changed=true; }
        }

        NumberMatch trunk = findNumeric(s, lang, Field.TRUNK);
        if (trunk != null) {
            if (trunk.ambiguous) clarification = "clarify_trunk";
            else { if (trunk.lowerBound) criteria.minTrunkLiters=trunk.value; else criteria.maxTrunkLiters=trunk.value; changed=true; }
        }

        NumberMatch battery = findNumeric(s, lang, Field.BATTERY);
        if (battery != null) {
            if (battery.ambiguous) clarification = "clarify_battery";
            else { if (battery.lowerBound) criteria.minBatteryKwh=battery.value; else criteria.maxBatteryKwh=battery.value; changed=true; }
        }

        NumberMatch power = findNumeric(s, lang, Field.POWER);
        if (power != null) {
            if (power.ambiguous) clarification = "clarify_power";
            else { if (power.lowerBound) criteria.minPowerKw=power.value; else criteria.maxPowerKw=power.value; changed=true; }
        }

        NumberMatch consumption = findNumeric(s, lang, Field.CONSUMPTION);
        if (consumption != null) {
            if (consumption.ambiguous) clarification = "clarify_consumption";
            else { criteria.maxConsumption=consumption.value; changed=true; }
        }

        String body = detectBody(s, lang);
        if (body != null) { criteria.bodyStyle=body; changed=true; }

        String drive = detectDrivetrain(s, lang);
        if (drive != null) { criteria.drivetrain=drive; changed=true; }

        if (hasLowConsumptionPreference(s, lang)) { criteria.preferLowConsumption=true; changed=true; }
        if (hasHighRangePreference(s, lang)) { criteria.preferHighRange=true; changed=true; }
        if (hasLargeTrunkPreference(s, lang)) { criteria.preferLargeTrunk=true; changed=true; }

        // The first version does not guess arbitrary model names. A query that names a
        // make/model without a supported criterion is returned as unsupported upstream.
        if (!changed && clarification == null) clarification = "unsupported_or_ambiguous";
        return result(changed, clarification, changed ? "criteria_updated" : null);
    }

    private ParseResult result(boolean changed, String clarification, String summary) {
        return new ParseResult(criteria.copy(), changed, clarification, summary);
    }

    private static final class NumberMatch {
        final double value;
        final boolean lowerBound;
        final boolean ambiguous;
        NumberMatch(double value, boolean lowerBound, boolean ambiguous) {
            this.value=value; this.lowerBound=lowerBound; this.ambiguous=ambiguous;
        }
    }

    private static NumberMatch findNumeric(String s, String lang, Field field) {
        String unitPattern;
        switch (field) {
            case PRICE: unitPattern="(?:€|eur|euros?|\\$|usd|£|gbp)"; break;
            case RANGE: unitPattern="(?:km|kilomet(?:er|re)s?)"; break;
            case TRUNK: unitPattern="(?:l|lit(?:er|re)s?)"; break;
            case BATTERY: unitPattern="(?:kwh|kw\\s*h)"; break;
            case POWER: unitPattern="(?:kw|kilowatts?)"; break;
            case CONSUMPTION: unitPattern="(?:kwh\\s*/\\s*100\\s*km|kwh\\s*/\\s*100km)"; break;
            default: return null;
        }

        Matcher unit = Pattern.compile("(" + NUMBER.pattern() + ")\\s*" + unitPattern).matcher(s);
        if (!unit.find()) {
            if (field != Field.PRICE || !containsPriceTerm(s, lang)) return null;
            // A price term with a number but no currency is still understandable in context.
            Matcher generic = NUMBER.matcher(s);
            if (!generic.find()) return new NumberMatch(0, false, true);
            unit = generic;
        }

        String raw = unit.group(1);
        if (raw == null) raw = unit.group();
        Double value = parseNumber(raw);
        if (value == null || value <= 0) return new NumberMatch(0, false, true);

        int start = unit.start();
        String prefix = s.substring(Math.max(0, start - 36), start);
        boolean lower = hasLowerOperator(prefix, lang);
        boolean upper = hasUpperOperator(prefix, lang);
        if (lower && upper) return new NumberMatch(value, false, true);
        // Bare numeric constraints are not inferred; ask the user what they mean.
        if (!lower && !upper) return new NumberMatch(value, false, true);
        if (field == Field.CONSUMPTION) {
            if (lower) return new NumberMatch(value, false, true);
            return new NumberMatch(value, false, false);
        }
        return new NumberMatch(value, lower, false);
    }

    private static Double parseNumber(String raw) {
        if (raw == null) return null;
        String n = raw.replaceAll("\\s", "");
        int comma=n.lastIndexOf(','), dot=n.lastIndexOf('.');
        if (comma>=0 && dot>=0) {
            if (comma>dot) n=n.replace(".", "").replace(',', '.');
            else n=n.replace(",", "");
        } else if (comma>=0) {
            int decimals=n.length()-comma-1;
            n = decimals==3 ? n.replace(",", "") : n.replace(',', '.');
        } else if (dot>=0 && n.length()-dot-1==3) n=n.replace(".", "");
        try { return Double.parseDouble(n); } catch (NumberFormatException ex) { return null; }
    }

    private static boolean hasLowerOperator(String prefix, String lang) {
        String p = prefix;
        switch (lang) {
            case "en": return has(p,"at least","more than","over","minimum","from");
            case "fr": return has(p,"au moins","plus de","minimum","superieur a");
            case "de": return has(p,"mindestens","mehr als","uber","ab");
            case "it": return has(p,"almeno","piu di","oltre","minimo");
            case "pt": return has(p,"pelo menos","mais de","acima de","minimo");
            default: return has(p,"al menos","más de","mas de","por encima de","mínimo","minimo","desde");
        }
    }

    private static boolean hasUpperOperator(String prefix, String lang) {
        switch (lang) {
            case "en": return has(prefix,"up to","at most","no more than","less than","under","maximum","below");
            case "fr": return has(prefix,"jusqu a","au maximum","maximum","moins de","pas plus de");
            case "de": return has(prefix,"bis zu","höchstens","maximal","weniger als","unter");
            case "it": return has(prefix,"fino a","al massimo","massimo","meno di","non oltre");
            case "pt": return has(prefix,"ate","até","no maximo","máximo","menos de","no mais de");
            default: return has(prefix,"hasta","como mucho","máximo","maximo","menos de","por debajo de","no más de","no mas de");
        }
    }

    private static boolean containsPriceTerm(String s, String lang) {
        switch (lang) {
            case "en": return has(s,"price","budget","cost");
            case "fr": return has(s,"prix","budget","cout");
            case "de": return has(s,"preis","budget","kosten");
            case "it": return has(s,"prezzo","budget","costo");
            case "pt": return has(s,"preco","orçamento","orcamento","custo");
            default: return has(s,"precio","presupuesto","cuesta");
        }
    }

    private static String detectBody(String s, String lang) {
        if (has(s,"suv","crossover")) return "SUV";
        if (has(s,"berlina","sedan","saloon")) return "SEDAN";
        if (has(s,"familiar","estate","station wagon","break")) return "ESTATE";
        if (has(s,"compacto","compact","hatchback","utilitario","city car")) return "HATCHBACK";
        if (has(s,"monovolumen","minivan","mpv")) return "MPV";
        if (has(s,"coupé","coupe")) return "COUPE";
        if (has(s,"cabrio","cabriolet","convertible","descapotable")) return "CONVERTIBLE";
        return null;
    }

    private static String detectDrivetrain(String s, String lang) {
        if (has(s,"awd","4x4","all wheel drive","four wheel drive","tracción total","traccion total","integrale","allrad","traction intégrale")) return "AWD";
        if (has(s,"rwd","rear wheel drive","propulsion trasera","tracción trasera","traccion trasera","hinterrad","trazione posteriore","tração traseira")) return "RWD";
        if (has(s,"fwd","front wheel drive","tracción delantera","traccion delantera","vorderrad","trazione anteriore","tração dianteira")) return "FWD";
        return null;
    }

    private static boolean hasLowConsumptionPreference(String s, String lang) {
        switch (lang) {
            case "en": return has(s,"efficient","low consumption","uses little","economical");
            case "fr": return has(s,"consomme peu","faible consommation","sobre","economique");
            case "de": return has(s,"sparsam","geringer verbrauch","wenig verbrauch","effizient");
            case "it": return has(s,"consuma poco","basso consumo","efficiente","parsimonioso");
            case "pt": return has(s,"consuma pouco","baixo consumo","economico","eficiente");
            default: return has(s,"consuma poco","poco consumo","bajo consumo","que gaste poco","eficiente");
        }
    }

    private static boolean hasHighRangePreference(String s, String lang) {
        switch (lang) {
            case "en": return has(s,"long range","best range","more range","maximum range");
            case "fr": return has(s,"grande autonomie","meilleure autonomie","plus d autonomie");
            case "de": return has(s,"hohe reichweite","maximale reichweite","mehr reichweite");
            case "it": return has(s,"grande autonomia","massima autonomia","piu autonomia");
            case "pt": return has(s,"grande autonomia","maior autonomia","mais autonomia");
            default: return has(s,"mucha autonomía","mucha autonomia","mayor autonomía","mayor autonomia","más autonomía","mas autonomia");
        }
    }

    private static boolean hasLargeTrunkPreference(String s, String lang) {
        switch (lang) {
            case "en": return has(s,"large trunk","big boot","more boot space","spacious boot");
            case "fr": return has(s,"grand coffre","coffre spacieux","plus de coffre");
            case "de": return has(s,"großer kofferraum","grosser kofferraum","viel kofferraum");
            case "it": return has(s,"bagagliaio grande","piu bagagliaio");
            case "pt": return has(s,"porta malas grande","bagageira grande","mais bagageira");
            default: return has(s,"maletero grande","mucho maletero","mayor maletero","más maletero","mas maletero");
        }
    }

    private static Field removalField(String s, String lang) {
        boolean remove = has(s,"quitar","elimina","eliminar","remove","clear","enlever","supprimer","entfernen","loschen","togli","rimuovi","remover","retirar");
        if (!remove) return null;
        if (has(s,"precio","presupuesto","price","budget","prix","preis","prezzo","preco")) return Field.PRICE;
        if (has(s,"autonom","range","reichweite")) return Field.RANGE;
        if (has(s,"maletero","cofre","trunk","boot","kofferraum","bagagliaio","porta malas","bagageira")) return Field.TRUNK;
        if (has(s,"bateria","batterie","battery","batteria")) return Field.BATTERY;
        if (has(s,"potencia","power","puissance","leistung","potenza")) return Field.POWER;
        if (has(s,"consumo","consumption","verbrauch","consommation")) return Field.CONSUMPTION;
        if (has(s,"carroceria","carrocería","body","karosserie","carrosserie")) return Field.BODY;
        if (has(s,"traccion","tracción","drivetrain","traction","antrieb","trazione")) return Field.DRIVETRAIN;
        return null;
    }

    private static String normalize(String value) {
        String n=Normalizer.normalize(value.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").replace('€','€');
        return n.replaceAll("[^\\p{L}\\p{N}€$£.,/\\s-]", " ").replaceAll("\\s+", " ").trim();
    }

    private static boolean has(String text, String... terms) {
        for (String term : terms) if (text.contains(normalize(term))) return true;
        return false;
    }
}
