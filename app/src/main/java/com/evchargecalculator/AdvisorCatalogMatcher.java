package com.evchargecalculator;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Deterministic local matcher for advisor criteria.
 *
 * Callers adapt the already-loaded protected/validated catalog Vehicle objects to Candidate.
 * This class never loads data, performs I/O, refreshes a catalog, or changes the classic search.
 */
public final class AdvisorCatalogMatcher {
    private AdvisorCatalogMatcher() {}

    /** Immutable view of the fields needed by advisor filtering/ranking. Unknown numeric values <= 0 stay unknown. */
    public static final class Candidate {
        public final String logicalKey, make, model, bodyStyle, drivetrain;
        public final double price, rangeKm, trunkLiters, batteryKwh, powerKw, consumption, lengthM;

        public Candidate(String logicalKey, String make, String model, String bodyStyle, String drivetrain,
                         double price, double rangeKm, double trunkLiters, double batteryKwh,
                         double powerKw, double consumption) {
            this(logicalKey, make, model, bodyStyle, drivetrain, price, rangeKm, trunkLiters, batteryKwh, powerKw, consumption, 0);
        }

        public Candidate(String logicalKey, String make, String model, String bodyStyle, String drivetrain,
                         double price, double rangeKm, double trunkLiters, double batteryKwh,
                         double powerKw, double consumption, double lengthM) {
            this.logicalKey = safe(logicalKey);
            this.make = safe(make);
            this.model = safe(model);
            this.bodyStyle = safe(bodyStyle);
            this.drivetrain = safe(drivetrain);
            this.price = known(price);
            this.rangeKm = known(rangeKm);
            this.trunkLiters = known(trunkLiters);
            this.batteryKwh = known(batteryKwh);
            this.powerKw = known(powerKw);
            this.consumption = known(consumption);
            this.lengthM = known(lengthM);
        }
    }

    public static final class Result {
        public final Candidate candidate;
        public final int catalogOrder;
        public final double preferenceScore;
        private Result(Candidate candidate, int catalogOrder, double preferenceScore) {
            this.candidate = candidate;
            this.catalogOrder = catalogOrder;
            this.preferenceScore = preferenceScore;
        }
    }

    /**
     * Applies all hard constraints with AND semantics. Missing values never satisfy an active
     * numeric constraint. Soft preferences only rank qualifying candidates; they do not filter.
     */
    public static List<Result> search(List<Candidate> candidates, AdvisorCriteriaEngine.Criteria criteria) {
        if (candidates == null || criteria == null || !criteria.hasHardFilters() && !hasPreference(criteria)) {
            return Collections.emptyList();
        }
        List<Result> results = new ArrayList<>();
        for (int i = 0; i < candidates.size(); i++) {
            Candidate c = candidates.get(i);
            if (c == null || !matches(c, criteria)) continue;
            results.add(new Result(c, i, score(c, criteria)));
        }
        Collections.sort(results, new Comparator<Result>() {
            @Override public int compare(Result a, Result b) {
                double targetDistanceA = 0, targetDistanceB = 0;
                if (criteria.maxPrice != null && a.candidate.price > 0 && b.candidate.price > 0) {
                    targetDistanceA += Math.abs(criteria.maxPrice - a.candidate.price) / criteria.maxPrice;
                    targetDistanceB += Math.abs(criteria.maxPrice - b.candidate.price) / criteria.maxPrice;
                }
                if (criteria.targetTrunkLiters != null && a.candidate.trunkLiters > 0 && b.candidate.trunkLiters > 0) {
                    targetDistanceA += Math.abs(criteria.targetTrunkLiters - a.candidate.trunkLiters) / criteria.targetTrunkLiters;
                    targetDistanceB += Math.abs(criteria.targetTrunkLiters - b.candidate.trunkLiters) / criteria.targetTrunkLiters;
                }
                int byDistance = Double.compare(targetDistanceA, targetDistanceB);
                if (byDistance != 0) return byDistance;
                int byScore = Double.compare(b.preferenceScore, a.preferenceScore);
                return byScore != 0 ? byScore : Integer.compare(a.catalogOrder, b.catalogOrder);
            }
        });
        return results;
    }

    private static boolean matches(Candidate c, AdvisorCriteriaEngine.Criteria q) {
        if (!withinPrice(c.price, q.minPrice, q.maxPrice, q.maxPriceIsCeiling)) return false;
        if (!within(c.rangeKm, q.minRangeKm, q.maxRangeKm, 1.03)) return false;
        if (!within(c.trunkLiters, q.minTrunkLiters, q.maxTrunkLiters, 1.03)) return false;
        // An unqualified trunk value is a target, not a hard minimum: retain nearby known values.
        if (q.targetTrunkLiters != null && (c.trunkLiters <= 0
                || c.trunkLiters < q.targetTrunkLiters * 0.75
                || c.trunkLiters > q.targetTrunkLiters * 1.25)) return false;
        if (!within(c.batteryKwh, q.minBatteryKwh, q.maxBatteryKwh, 1.03)) return false;
        if (!within(c.powerKw, q.minPowerKw, q.maxPowerKw, 1.03)) return false;
        // Explicit consumption ceilings are exact: "under 15 kWh/100 km" must not include 15.1.
        if (q.maxConsumption != null && (c.consumption <= 0 || c.consumption > q.maxConsumption)) return false;
        // Exact upper bound: 4.72 m must fail a maximum of 4.7 m.
        if (q.maxLengthM != null && (c.lengthM <= 0 || (q.maxLengthExclusive ? c.lengthM >= q.maxLengthM : c.lengthM > q.maxLengthM))) return false;
        if (q.bodyStyle != null && !bodyMatches(q.bodyStyle, c.bodyStyle)) return false;
        if (q.drivetrain != null && !driveMatches(q.drivetrain, c.drivetrain)) return false;
        if (q.makeModel != null && !normalize(c.make + " " + c.model).contains(normalize(q.makeModel))) return false;
        return true;
    }

    /**
     * An approximate target price (e.g. "berlina de 40 mil") searches a symmetric
     * inclusive 85%-115% band (±15%); explicit upper limits (e.g. "menos de",
     * "hasta" or "máximo 40.000 €") are strict and never exceed the requested amount.
     */
    private static boolean withinPrice(double value, Double min, Double max, boolean explicitCeiling) {
        if (min == null && max == null) return true;
        if (value <= 0) return false;
        if (min == null && max != null) return explicitCeiling ? value <= max : value >= max * 0.85 && value <= max * 1.15;
        return (min == null || value >= min) && (max == null || value <= max * (explicitCeiling ? 1.0 : 1.05));
    }

    private static boolean within(double value, Double min, Double max, double upperTolerance) {
        if (min == null && max == null) return true;
        if (value <= 0) return false;
        // Lower bounds remain exact; only upper bounds get tolerance.
        return (min == null || value >= min) && (max == null || value <= max * upperTolerance);
    }

    private static boolean bodyMatches(String wanted, String actual) {
        String w = normalize(wanted), a = normalize(actual);
        if (w.isEmpty() || a.isEmpty()) return false;
        if (w.equals(a)) return true;
        return (isSuv(w) && isSuv(a))
                 || (isOneOf(w, "sedan", "berlina", "saloon")
                    && isOneOf(a, "sedan", "berlina", "saloon", "fastback", "liftback", "notchback"))
                || (isOneOf(w, "estate", "wagon", "familiar") && isOneOf(a, "estate", "wagon", "familiar"))
                || (isOneOf(w, "hatchback", "compacto", "utilitario") && isOneOf(a, "hatchback", "compacto", "utilitario"));
    }

    private static boolean driveMatches(String wanted, String actual) {
        String w = normalize(wanted), a = normalize(actual);
        if (w.isEmpty() || a.isEmpty()) return false;
        String wn = driveCode(w), an = driveCode(a);
        return !wn.isEmpty() && wn.equals(an);
    }

    private static String driveCode(String s) {
        if (isOneOf(s, "awd", "4x4", "all wheel drive", "traccion total", "traction integrale", "allrad")) return "AWD";
        if (isOneOf(s, "rwd", "rear wheel drive", "propulsion trasera", "traccion trasera", "hinterrad")) return "RWD";
        if (isOneOf(s, "fwd", "front wheel drive", "traccion delantera", "vorderrad")) return "FWD";
        return "";
    }

    private static boolean isSuv(String s) { return isOneOf(s, "suv", "crossover"); }
    private static boolean isOneOf(String s, String... values) {
        for (String value : values) if (s.equals(normalize(value))) return true;
        return false;
    }

    private static boolean hasPreference(AdvisorCriteriaEngine.Criteria c) {
        return c.preferLowConsumption || c.preferHighRange || c.preferLargeTrunk;
    }

    private static double score(Candidate c, AdvisorCriteriaEngine.Criteria q) {
        double total = 0, weight = 0;
        if (q.preferLowConsumption && c.consumption > 0) {
            total += 1.0 / c.consumption; weight += 1;
        }
        if (q.preferHighRange && c.rangeKm > 0) {
            total += c.rangeKm / 1000.0; weight += 1;
        }
        if (q.preferLargeTrunk && c.trunkLiters > 0) {
            total += c.trunkLiters / 1000.0; weight += 1;
        }
        return weight == 0 ? 0 : total / weight;
    }

    private static double known(double value) { return Double.isFinite(value) && value > 0 ? value : 0; }
    private static String safe(String value) { return value == null ? "" : value.trim(); }
    private static String normalize(String value) {
        return Normalizer.normalize(safe(value).toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").replaceAll("[^\\p{L}\\p{N}]+", " ").trim();
    }
}
