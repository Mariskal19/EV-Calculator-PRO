package com.evchargecalculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/** Integration smoke test over a TSV projection generated from the protected JSON catalog. */
public final class AdvisorCatalogIntegrationSmokeTest {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) throw new IllegalArgumentException("Expected catalog TSV path");
        List<AdvisorCatalogMatcher.Candidate> catalog = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(args[0]))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] f = line.split("\\t", -1);
                if (f.length != 11) throw new AssertionError("Unexpected catalog row shape: " + f.length);
                catalog.add(new AdvisorCatalogMatcher.Candidate(f[0], f[1], f[2], f[3], f[4],
                        number(f[5]), number(f[6]), number(f[7]), number(f[8]), number(f[9]), number(f[10])));
            }
        }
        check(catalog.size() > 600, "loads a substantial real protected catalog");
        check(catalog.stream().anyMatch(c -> c.price > 0), "catalog has known prices");
        check(catalog.stream().anyMatch(c -> c.rangeKm > 0), "catalog has known WLTP ranges");

        AdvisorCriteriaEngine.Criteria q = new AdvisorCriteriaEngine.Criteria();
        q.maxPrice = 40000d;
        q.minRangeKm = 400d;
        List<AdvisorCatalogMatcher.Result> matches = AdvisorCatalogMatcher.search(catalog, q);
        check(!matches.isEmpty(), "real catalog produces matches for price/range constraints");
        for (AdvisorCatalogMatcher.Result result : matches) {
            check(result.candidate.price >= 28000d && result.candidate.price <= 46000d,
                    "every real-catalog approximate-target result stays within the widened budget band");
            check(result.candidate.rangeKm >= 400d,
                    "every real-catalog result satisfies known range floor");
        }

        AdvisorCriteriaEngine engine = new AdvisorCriteriaEngine();
        AdvisorCriteriaEngine.ParseResult sedanQuery = engine.interpret(
                "Búscame una berlina de 40 mil", AdvisorCriteriaEngine.Language.ES);
        check(!sedanQuery.needsClarification(), "parses real-catalog colloquial sedan target query");
        List<AdvisorCatalogMatcher.Result> sedanTargetMatches =
                AdvisorCatalogMatcher.search(catalog, sedanQuery.criteria);
        System.out.println("Real catalog results for 'berlina de 40 mil': " + sedanTargetMatches.size());

        // Mirror the app-only body-style correction and display de-duplication:
        // Kia EV4 Hatchback is mislabeled as sedan in the protected catalog, and
        // only one version per normalized make/model should be shown to the user.
        List<AdvisorCatalogMatcher.Result> visibleSedans = new ArrayList<>();
        java.util.Set<String> visibleModelKeys = new java.util.HashSet<>();
        for (AdvisorCatalogMatcher.Result match : sedanTargetMatches) {
            AdvisorCatalogMatcher.Candidate c = match.candidate;
            if ("Kia".equalsIgnoreCase(c.make) && "EV4 Hatchback".equalsIgnoreCase(c.model)) continue;
            String key = (c.make + " " + c.model).trim().toLowerCase(java.util.Locale.ROOT)
                    .replaceAll("\\s+", " ");
            if (visibleModelKeys.add(key)) visibleSedans.add(match);
        }
        check(visibleSedans.size() == 7,
                "real sedan query yields seven user-visible unique models after app body-style correction");
        String[] expectedModelOrder = {
                "Tesla Model 3", "Peugeot E-408", "Kia EV4 Fastback",
                "Mazda Mazda6e", "XPeng P7+", "Hyundai IONIQ 6", "BYD Seal"
        };
        for (int i = 0; i < expectedModelOrder.length; i++) {
            AdvisorCatalogMatcher.Candidate actual = visibleSedans.get(i).candidate;
            check(expectedModelOrder[i].equals(actual.make + " " + actual.model),
                    "visible sedan results are deduplicated and ordered by proximity to 40,000 euros at position " + (i + 1));
        }

        // Exercise the app's multi-criterion example against the full protected catalog.
        AdvisorCriteriaEngine.ParseResult suvQuery = new AdvisorCriteriaEngine().interpret(
                "SUV hasta 35.000 €, más de 450 km y maletero de al menos 450 litros",
                AdvisorCriteriaEngine.Language.ES);
        check(!suvQuery.needsClarification() && "SUV".equals(suvQuery.criteria.bodyStyle),
                "parses the full Spanish SUV advisor example");
        List<AdvisorCatalogMatcher.Result> suvMatches = AdvisorCatalogMatcher.search(catalog, suvQuery.criteria);
        check(!suvMatches.isEmpty(), "real catalog returns candidates for the combined SUV query");
        for (AdvisorCatalogMatcher.Result match : suvMatches) {
            AdvisorCatalogMatcher.Candidate c = match.candidate;
            check("suv".equalsIgnoreCase(c.bodyStyle) || "crossover".equalsIgnoreCase(c.bodyStyle),
                    "combined query never mixes sedans into SUV results");
            check(c.price > 0 && c.price <= 36750,
                    "combined query respects explicit 35,000 euro ceiling with the documented 5% tolerance");
            check(c.rangeKm >= 450, "combined query enforces the 450 km minimum");
            check(c.trunkLiters >= 450, "combined query enforces the 450 litre minimum");
        }
        System.out.println("Real catalog combined SUV query results: " + suvMatches.size());

        q = new AdvisorCriteriaEngine.Criteria();
        q.minRangeKm = 400d;
        List<AdvisorCatalogMatcher.Result> rangeMatches = AdvisorCatalogMatcher.search(catalog, q);
        for (AdvisorCatalogMatcher.Result result : rangeMatches) {
            check(result.candidate.rangeKm >= 400d, "unknown range never passes a hard range filter");
        }

        q = new AdvisorCriteriaEngine.Criteria();
        q.preferLowConsumption = true;
        check(AdvisorCatalogMatcher.search(catalog, q).size() == catalog.size(),
                "preference alone ranks rather than filters real catalog candidates");
        System.out.println("Advisor catalog integration smoke tests passed: " + catalog.size()
                + " catalog rows, " + matches.size() + " price/range matches, " + sedanTargetMatches.size() + " real sedan target matches, " + rangeMatches.size()
                + " range matches.");
    }

    private static double number(String s) {
        try { double value = Double.parseDouble(s); return Double.isFinite(value) && value > 0 ? value : 0; }
        catch (Exception ignored) { return 0; }
    }
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
