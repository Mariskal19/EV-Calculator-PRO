package com.evchargecalculator;

/** Dependency-free smoke tests for the local criteria interpreter. */
public final class AdvisorCriteriaEngineSmokeTest {
    private static int checks;

    public static void main(String[] args) {
        AdvisorCriteriaEngine engine = new AdvisorCriteriaEngine();

        AdvisorCriteriaEngine.ParseResult r = engine.interpret(
                "Busco un SUV hasta 35.000 €", AdvisorCriteriaEngine.Language.ES);
        check("SUV".equals(r.criteria.bodyStyle), "recognizes SUV body style");
        check(r.criteria.maxPrice != null && Math.abs(r.criteria.maxPrice - 35000d) < 0.01,
                "parses Spanish price ceiling");

        r = engine.interpret("al menos 450 litros", AdvisorCriteriaEngine.Language.ES);
        check(r.criteria.minTrunkLiters != null && Math.abs(r.criteria.minTrunkLiters - 450d) < 0.01,
                "parses minimum trunk volume");

        engine.reset();
        r = engine.interpret("menos de 35.000 € y más de 450 km", AdvisorCriteriaEngine.Language.ES);
        check(r.criteria.maxPrice != null && Math.abs(r.criteria.maxPrice - 35000d) < 0.01,
                "parses price cap in multi-criteria query");
        check(r.criteria.minRangeKm != null && Math.abs(r.criteria.minRangeKm - 450d) < 0.01,
                "parses range floor after another criterion");

        r = engine.interpret("más de 400 km", AdvisorCriteriaEngine.Language.ES);
        check(r.criteria.maxPrice != null && Math.abs(r.criteria.maxPrice - 35000d) < 0.01,
                "follow-up retains previous price filter");
        check(r.criteria.minRangeKm != null && Math.abs(r.criteria.minRangeKm - 400d) < 0.01,
                "follow-up updates range filter");

        r = engine.interpret("450", AdvisorCriteriaEngine.Language.ES);
        check(r.needsClarification(), "does not guess a bare number");

        r = engine.interpret("quitar presupuesto", AdvisorCriteriaEngine.Language.ES);
        check(r.criteria.maxPrice == null && r.criteria.minPrice == null,
                "removes only the requested price filter");
        check(r.criteria.minRangeKm != null && Math.abs(r.criteria.minRangeKm - 400d) < 0.01,
                "removing price retains range filter");

        r = engine.interpret("at least 450 km", AdvisorCriteriaEngine.Language.EN);
        check(r.criteria.minRangeKm != null && Math.abs(r.criteria.minRangeKm - 450d) < 0.01,
                "supports English range pattern");

        System.out.println("AdvisorCriteriaEngine smoke tests passed: " + checks);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
        checks++;
    }
}
