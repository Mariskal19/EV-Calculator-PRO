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
                + " catalog rows, " + matches.size() + " price/range matches, " + rangeMatches.size()
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
