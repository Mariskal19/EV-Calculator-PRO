package com.evchargecalculator;

import java.util.Arrays;
import java.util.List;

/** Regression tests for direct AI-advisor catalog lookup by make, model and trim. */
public final class AdvisorSimilarityIntentSmokeTest {
    private static int checks;

    public static void main(String[] args) {
        List<String> names = Arrays.asList("Tesla Model 3", "Tesla Model Y", "XPeng G6", "BMW i4");

        check(!AdvisorSimilarityIntent.shouldRoute("Tesla", names),
                "a make-only query does not enter the similar-car route");
        check(AdvisorSimilarityIntent.shouldRoute("un coche parecido al Tesla Model 3", names),
                "an explicit similarity request keeps priority over direct catalog matches");
        check(AdvisorSimilarityIntent.isDirectModelLookup("Tesla", names),
                "a make-only query is a direct catalog lookup");
        check(AdvisorSimilarityIntent.isDirectModelLookup("Model 3 Performance", names),
                "a model plus trim query is recognized from its model identity");
        check(AdvisorSimilarityIntent.isDirectModelLookup("Búscame Tesla Model 3 Performance", names),
                "a natural-language make/model/trim query is recognized");
        check(!AdvisorSimilarityIntent.isDirectModelLookup("Performance", names),
                "a trim word without make/model identity is not a direct lookup");
        check(!AdvisorSimilarityIntent.isDirectModelLookup("Model", names),
                "the generic word model alone is not a useful direct lookup");
        check(!AdvisorSimilarityIntent.isDirectModelLookup("un coche parecido al Tesla Model 3", names),
                "similarity intent keeps precedence over direct catalog lookup");

        check(AdvisorSimilarityIntent.matchesDirectCatalogEntry(
                        "Tesla", "Tesla", "Model 3", "Performance 461 kW AWD 79 kWh"),
                "make-only query matches Tesla catalog entries");
        check(AdvisorSimilarityIntent.matchesDirectCatalogEntry(
                        "Model 3 Performance", "Tesla", "Model 3", "Performance 461 kW AWD 79 kWh"),
                "model and trim tokens match across model and version fields");
        check(!AdvisorSimilarityIntent.matchesDirectCatalogEntry(
                        "Model 3 Performance", "Tesla", "Model Y", "Performance 461 kW AWD 75 kWh"),
                "Model 3 Performance does not match Model Y Performance");
        check(!AdvisorSimilarityIntent.matchesDirectCatalogEntry(
                        "Model 3 Performance", "Tesla", "Model 3", "Gran autonomía 224 kW RWD 79 kWh"),
                "Model 3 Performance excludes non-Performance trims");
        check(AdvisorSimilarityIntent.matchesDirectCatalogEntry(
                        "Tesla Model 3 Performance", "Tesla", "Model 3", "Performance 461 kW AWD 79 kWh"),
                "full make/model/trim query matches the corresponding catalog entry");
        check(!AdvisorSimilarityIntent.matchesDirectCatalogEntry(
                        "Model 3", "Tesla", "Model 30", "Performance"),
                "numeric model tokens are matched as whole tokens");

        System.out.println("Advisor direct catalog lookup tests passed: " + checks + " checks.");
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
}
