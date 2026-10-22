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
        r = engine.interpret("quiero uno de 450 de maletero", AdvisorCriteriaEngine.Language.ES);
        check(r.criteria.targetTrunkLiters != null && Math.abs(r.criteria.targetTrunkLiters - 450d) < 0.01,
                "interprets an unqualified trunk value as an approximate target, not a minimum");

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

        r = engine.interpret("berlina máximo 40000", AdvisorCriteriaEngine.Language.ES);
        check(r.criteria.maxPrice != null && Math.abs(r.criteria.maxPrice - 40000d) < 0.01,
                "parses a price ceiling without currency when maximum operator is explicit");

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
        check(matches.size() == 2 && "b".equals(matches.get(0).candidate.logicalKey),
                "allows a small budget stretch and prioritizes the price nearest the budget");

        catalog.add(new AdvisorCatalogMatcher.Candidate("e", "Marca", "Muy barato", "suv", "RWD",
                20000, 500, 500, 70, 150, 16.0));
        filters = new AdvisorCriteriaEngine.Criteria();
        filters.maxPrice = 40000d;
        matches = AdvisorCatalogMatcher.search(catalog, filters);
        check(matches.stream().noneMatch(m -> "e".equals(m.candidate.logicalKey)),
                "maximum budget excludes vehicles far below the intended price area");

        // Upper-bound tolerance policy: price +5%; other numeric maxima +3%; minima exact.
        java.util.List<AdvisorCatalogMatcher.Candidate> toleranceCatalog = new java.util.ArrayList<>();
        toleranceCatalog.add(new AdvisorCatalogMatcher.Candidate("base", "Marca", "Base", "suv", "RWD", 30000, 450, 450, 80, 200, 20));
        toleranceCatalog.add(new AdvisorCatalogMatcher.Candidate("priceLow", "Marca", "Precio bajo", "suv", "RWD", 20000, 450, 450, 80, 200, 20));
        toleranceCatalog.add(new AdvisorCatalogMatcher.Candidate("price31", "Marca", "Precio 31000", "suv", "RWD", 31000, 450, 450, 80, 200, 20));
        toleranceCatalog.add(new AdvisorCatalogMatcher.Candidate("price32", "Marca", "Precio 32000", "suv", "RWD", 32000, 450, 450, 80, 200, 20));
        toleranceCatalog.add(new AdvisorCatalogMatcher.Candidate("range449", "Marca", "Autonomia 449", "suv", "RWD", 30000, 449, 450, 80, 200, 20));
        toleranceCatalog.add(new AdvisorCatalogMatcher.Candidate("range463", "Marca", "Autonomia 463", "suv", "RWD", 30000, 463, 450, 80, 200, 20));
        toleranceCatalog.add(new AdvisorCatalogMatcher.Candidate("range464", "Marca", "Autonomia 464", "suv", "RWD", 30000, 464, 450, 80, 200, 20));
        toleranceCatalog.add(new AdvisorCatalogMatcher.Candidate("trunk463", "Marca", "Maletero 463", "suv", "RWD", 30000, 450, 463, 80, 200, 20));
        toleranceCatalog.add(new AdvisorCatalogMatcher.Candidate("trunk464", "Marca", "Maletero 464", "suv", "RWD", 30000, 450, 464, 80, 200, 20));
        toleranceCatalog.add(new AdvisorCatalogMatcher.Candidate("battery82", "Marca", "Bateria 82", "suv", "RWD", 30000, 450, 450, 82, 200, 20));
        toleranceCatalog.add(new AdvisorCatalogMatcher.Candidate("battery83", "Marca", "Bateria 83", "suv", "RWD", 30000, 450, 450, 83, 200, 20));
        toleranceCatalog.add(new AdvisorCatalogMatcher.Candidate("power206", "Marca", "Potencia 206", "suv", "RWD", 30000, 450, 450, 80, 206, 20));
        toleranceCatalog.add(new AdvisorCatalogMatcher.Candidate("power207", "Marca", "Potencia 207", "suv", "RWD", 30000, 450, 450, 80, 207, 20));
        toleranceCatalog.add(new AdvisorCatalogMatcher.Candidate("consumption20_5", "Marca", "Consumo 20.5", "suv", "RWD", 30000, 450, 450, 80, 200, 20.5));
        toleranceCatalog.add(new AdvisorCatalogMatcher.Candidate("consumption21", "Marca", "Consumo 21", "suv", "RWD", 30000, 450, 450, 80, 200, 21));

        filters = new AdvisorCriteriaEngine.Criteria();
        filters.maxPrice = 30000d;
        matches = AdvisorCatalogMatcher.search(toleranceCatalog, filters);
        check(matches.stream().anyMatch(m -> "price31".equals(m.candidate.logicalKey))
                        && matches.stream().noneMatch(m -> "price32".equals(m.candidate.logicalKey)),
                "price maximum permits up to 5% over the requested amount");

        engine.reset();
        r = engine.interpret("Busco un SUV hasta 30.000 €", AdvisorCriteriaEngine.Language.ES);
        check(r.criteria.maxPriceIsCeiling, "explicit 'hasta' price operator is treated as a ceiling");
        matches = AdvisorCatalogMatcher.search(toleranceCatalog, r.criteria);
        check(matches.stream().anyMatch(m -> "priceLow".equals(m.candidate.logicalKey))
                        && matches.stream().anyMatch(m -> "price31".equals(m.candidate.logicalKey))
                        && matches.stream().noneMatch(m -> "price32".equals(m.candidate.logicalKey)),
                "explicit price ceiling accepts cheaper cars and allows up to 5% over budget");

        filters = new AdvisorCriteriaEngine.Criteria();
        filters.maxRangeKm = 450d;
        matches = AdvisorCatalogMatcher.search(toleranceCatalog, filters);
        check(matches.stream().anyMatch(m -> "range463".equals(m.candidate.logicalKey))
                        && matches.stream().noneMatch(m -> "range464".equals(m.candidate.logicalKey)),
                "range maximum permits 3% tolerance, not 4%");

        filters = new AdvisorCriteriaEngine.Criteria();
        filters.maxTrunkLiters = 450d;
        matches = AdvisorCatalogMatcher.search(toleranceCatalog, filters);
        check(matches.stream().anyMatch(m -> "trunk463".equals(m.candidate.logicalKey))
                        && matches.stream().noneMatch(m -> "trunk464".equals(m.candidate.logicalKey)),
                "trunk maximum permits 3% tolerance, not 4%");

        filters = new AdvisorCriteriaEngine.Criteria();
        filters.maxBatteryKwh = 80d;
        matches = AdvisorCatalogMatcher.search(toleranceCatalog, filters);
        check(matches.stream().anyMatch(m -> "battery82".equals(m.candidate.logicalKey))
                        && matches.stream().noneMatch(m -> "battery83".equals(m.candidate.logicalKey)),
                "battery maximum permits 3% tolerance, not 4%");

        filters = new AdvisorCriteriaEngine.Criteria();
        filters.maxPowerKw = 200d;
        matches = AdvisorCatalogMatcher.search(toleranceCatalog, filters);
        check(matches.stream().anyMatch(m -> "power206".equals(m.candidate.logicalKey))
                        && matches.stream().noneMatch(m -> "power207".equals(m.candidate.logicalKey)),
                "power maximum permits 3% tolerance, not 4%");

        filters = new AdvisorCriteriaEngine.Criteria();
        filters.maxConsumption = 20d;
        matches = AdvisorCatalogMatcher.search(toleranceCatalog, filters);
        check(matches.stream().anyMatch(m -> "consumption20_5".equals(m.candidate.logicalKey))
                        && matches.stream().noneMatch(m -> "consumption21".equals(m.candidate.logicalKey)),
                "consumption maximum permits 3% tolerance, not 4%");

        filters = new AdvisorCriteriaEngine.Criteria();
        filters.minRangeKm = 450d;
        matches = AdvisorCatalogMatcher.search(toleranceCatalog, filters);
        check(matches.stream().anyMatch(m -> "base".equals(m.candidate.logicalKey))
                        && matches.stream().noneMatch(m -> "range449".equals(m.candidate.logicalKey)),
                "minimum range remains exact and lower bounds are not relaxed");

        filters = new AdvisorCriteriaEngine.Criteria();
        filters.minRangeKm = 450d;
        matches = AdvisorCatalogMatcher.search(catalog, filters);
        check(matches.size() == 4, "range filter excludes only candidate with unknown range");

        filters = new AdvisorCriteriaEngine.Criteria();
        filters.targetTrunkLiters = 450d;
        matches = AdvisorCatalogMatcher.search(catalog, filters);
        check(matches.size() == 4, "trunk target returns nearby known trunk sizes rather than requiring 450 L minimum");
        check("a".equals(matches.get(0).candidate.logicalKey), "trunk target ranks closest boot volume first");

        filters = new AdvisorCriteriaEngine.Criteria();
        filters.preferLowConsumption = true;
        matches = AdvisorCatalogMatcher.search(catalog, filters);
        check(matches.size() == 5, "soft preference ranks without filtering candidates");
        check("c".equals(matches.get(0).candidate.logicalKey),
                "low-consumption preference ranks lower known consumption first");

        filters = new AdvisorCriteriaEngine.Criteria();
        matches = AdvisorCatalogMatcher.search(catalog, filters);
        check(matches.isEmpty(), "empty criteria does not trigger a catalog search");

        engine.reset();
        r = engine.interpret("hasta 35.000 € y 450 km", AdvisorCriteriaEngine.Language.ES);
        check(r.needsClarification(), "does not borrow a price operator for a second criterion");
        check(r.criteria.maxPrice == null && r.criteria.minRangeKm == null && r.criteria.maxRangeKm == null,
                "ambiguous multi-criterion query does not partially mutate state");

        System.out.println("Advisor engine and catalog matcher smoke tests passed: " + checks);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
        checks++;
    }
}
