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

        // Catalog matching: AND filters, unknown numeric fields, and soft ranking.
        java.util.List<AdvisorCatalogMatcher.Candidate> catalog = new java.util.ArrayList<>();
        catalog.add(new AdvisorCatalogMatcher.Candidate("a", "Marca", "Conocido", "suv", "RWD",
                32000, 480, 500, 70, 150, 16.0));
        catalog.add(new AdvisorCatalogMatcher.Candidate("b", "Marca", "Precio alto", "suv", "RWD",
                42000, 500, 600, 75, 180, 17.0));
        catalog.add(new AdvisorCatalogMatcher.Candidate("c", "Marca", "Autonomia desconocida", "suv", "RWD",
                30000, 0, 520, 70, 150, 15.0));
        catalog.add(new AdvisorCatalogMatcher.Candidate("d", "Marca", "Carroceria distinta", "sedan", "RWD",
                30000, 490, 520, 70, 150, 15.0));

        AdvisorCriteriaEngine.Criteria filters = new AdvisorCriteriaEngine.Criteria();
        filters.maxPrice = 40000d;
        filters.minRangeKm = 450d;
        filters.bodyStyle = "SUV";
        java.util.List<AdvisorCatalogMatcher.Result> matches = AdvisorCatalogMatcher.search(catalog, filters);
        check(matches.size() == 1 && "a".equals(matches.get(0).candidate.logicalKey),
                "combines hard filters and excludes unknown range / incompatible body");

        filters = new AdvisorCriteriaEngine.Criteria();
        filters.minRangeKm = 450d;
        matches = AdvisorCatalogMatcher.search(catalog, filters);
        check(matches.size() == 3, "range filter excludes only candidate with unknown range");

        filters = new AdvisorCriteriaEngine.Criteria();
        filters.preferLowConsumption = true;
        matches = AdvisorCatalogMatcher.search(catalog, filters);
        check(matches.size() == 4, "soft preference ranks without filtering candidates");
        check("c".equals(matches.get(0).candidate.logicalKey),
                "low-consumption preference ranks lower known consumption first");

        filters = new AdvisorCriteriaEngine.Criteria();
        matches = AdvisorCatalogMatcher.search(catalog, filters);
        check(matches.isEmpty(), "empty criteria does not trigger a catalog search");

        System.out.println("Advisor engine and catalog matcher smoke tests passed: " + checks);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
        checks++;
    }
}
