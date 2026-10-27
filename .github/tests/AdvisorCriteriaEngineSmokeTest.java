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

        r = engine.interpret("Búscame un coche de unos 40000 euros", AdvisorCriteriaEngine.Language.ES);
        check(!r.needsClarification() && r.criteria.maxPrice != null
                        && Math.abs(r.criteria.maxPrice - 40000d) < 0.01,
                "parses approximate price wording as a target budget");
        check(!r.criteria.maxPriceIsCeiling,
                "approximate price target is not misclassified as a strict ceiling");

        r = engine.interpret("Búscame una berlina de 40 mil", AdvisorCriteriaEngine.Language.ES);
        check(!r.needsClarification() && "SEDAN".equals(r.criteria.bodyStyle),
                "parses sedan body style with colloquial price wording without currency");
        check(r.criteria.maxPrice != null && Math.abs(r.criteria.maxPrice - 40000d) < 0.01
                        && !r.criteria.maxPriceIsCeiling,
                "parses '40 mil' without currency as an approximate 40,000 euro target");

        engine.reset();
        r = engine.interpret("Búscame una berlina de 62800", AdvisorCriteriaEngine.Language.ES);
        check(!r.needsClarification() && "SEDAN".equals(r.criteria.bodyStyle),
                "parses sedan body style with a five-digit implicit price and no currency");
        check(r.criteria.maxPrice != null && Math.abs(r.criteria.maxPrice - 62800d) < 0.01
                        && !r.criteria.maxPriceIsCeiling,
                "parses berlina de 62800 as an approximate 62,800 euro target, not a hard ceiling");

        r = engine.interpret("Búscame una berlina de 40 mil euros", AdvisorCriteriaEngine.Language.ES);
        check(!r.needsClarification() && "SEDAN".equals(r.criteria.bodyStyle),
                "parses sedan body style with colloquial price wording");
        check(r.criteria.maxPrice != null && Math.abs(r.criteria.maxPrice - 40000d) < 0.01
                        && !r.criteria.maxPriceIsCeiling,
                "parses '40 mil euros' as an approximate price target, not a hard ceiling");

        java.util.List<AdvisorCatalogMatcher.Candidate> priceTargetCandidates = new java.util.ArrayList<>();
        priceTargetCandidates.add(new AdvisorCatalogMatcher.Candidate("sedan40", "Marca", "Berlina 40k",
                "sedan", "RWD", 39900, 450, 450, 70, 150, 16));
        priceTargetCandidates.add(new AdvisorCatalogMatcher.Candidate("sedan30", "Marca", "Berlina 30k",
                "sedan", "RWD", 30000, 450, 450, 70, 150, 16));
        priceTargetCandidates.add(new AdvisorCatalogMatcher.Candidate("sedan34", "Marca", "Berlina 34k",
                "sedan", "RWD", 34000, 450, 450, 70, 150, 16));
        priceTargetCandidates.add(new AdvisorCatalogMatcher.Candidate("sedan46", "Marca", "Berlina 46k",
                "sedan", "RWD", 46000, 450, 450, 70, 150, 16));
        priceTargetCandidates.add(new AdvisorCatalogMatcher.Candidate("sedan33", "Marca", "Berlina 33k",
                "sedan", "RWD", 33000, 450, 450, 70, 150, 16));
        priceTargetCandidates.add(new AdvisorCatalogMatcher.Candidate("sedan47", "Marca", "Berlina 47k",
                "sedan", "RWD", 47000, 450, 450, 70, 150, 16));
        priceTargetCandidates.add(new AdvisorCatalogMatcher.Candidate("sedan45", "Marca", "Berlina 45k",
                "sedan", "RWD", 45000, 450, 450, 70, 150, 16));
        priceTargetCandidates.add(new AdvisorCatalogMatcher.Candidate("sedan66", "Marca", "Berlina 66k",
                "sedan", "RWD", 66000, 450, 450, 70, 150, 16));
        java.util.List<AdvisorCatalogMatcher.Result> priceTargetMatches =
                AdvisorCatalogMatcher.search(priceTargetCandidates, r.criteria);
        check(priceTargetMatches.size() == 4,
                "approximate 40,000 euro target returns options between 85% and 115% of target");
        check(priceTargetMatches.stream().noneMatch(match -> "sedan66".equals(match.candidate.logicalKey)
                        || "sedan30".equals(match.candidate.logicalKey)
                        || "sedan33".equals(match.candidate.logicalKey)
                        || "sedan47".equals(match.candidate.logicalKey)),
                "approximate 40,000 euro sedan query excludes prices outside the 34,000–46,000 euro band");
        check(priceTargetMatches.stream().anyMatch(match -> "sedan34".equals(match.candidate.logicalKey))
                        && priceTargetMatches.stream().anyMatch(match -> "sedan46".equals(match.candidate.logicalKey)),
                "approximate 40,000 euro sedan query includes both ±15% band boundaries");

        r = engine.interpret("450", AdvisorCriteriaEngine.Language.ES);
        check(r.needsClarification(), "does not guess a bare number");

        r = engine.interpret("menos de 35.000 € y más de 400 km", AdvisorCriteriaEngine.Language.ES);
        check(r.criteria.maxPrice != null && Math.abs(r.criteria.maxPrice - 35000d) < 0.01
                        && r.criteria.minRangeKm != null && Math.abs(r.criteria.minRangeKm - 400d) < 0.01,
                "sets price and range filters before removing price");

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
        check(matches.size() == 1 && "b".equals(matches.get(0).candidate.logicalKey),
                "approximate budget excludes vehicles more than 15% below target and keeps the closest qualifying price");

        catalog.add(new AdvisorCatalogMatcher.Candidate("e", "Marca", "Muy barato", "suv", "RWD",
                20000, 500, 500, 70, 150, 16.0));
        filters = new AdvisorCriteriaEngine.Criteria();
        filters.maxPrice = 40000d;
        matches = AdvisorCatalogMatcher.search(catalog, filters);
        check(matches.stream().noneMatch(m -> "e".equals(m.candidate.logicalKey)),
                "maximum budget excludes vehicles far below the intended price area");

        // Explicit price ceilings allow +5%; approximate price targets use ±15%; other maxima +3%.
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
        filters.maxPriceIsCeiling = true;
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

        java.util.List<String> similarityCatalogNames = java.util.Arrays.asList(
                "XPeng G6", "Tesla Model Y", "Zeekr 7GT");
        check(AdvisorSimilarityIntent.isDirectModelLookup("Búscame el XPeng G6", similarityCatalogNames),
                "direct lookup displays the exact requested catalog model");
        check(AdvisorSimilarityIntent.isDirectModelLookup("Muéstrame el Tesla Model Y", similarityCatalogNames),
                "direct lookup works globally for another make/model");
        check(AdvisorSimilarityIntent.isDirectModelLookup("Model Y", similarityCatalogNames),
                "bare Tesla Model Y name resolves to the exact catalog model");
        check(AdvisorSimilarityIntent.isDirectModelLookup("XPeng G6", similarityCatalogNames),
                "bare XPeng G6 name resolves to the exact catalog model");
        check(AdvisorSimilarityIntent.isDirectModelLookup("Zeekr 7GT", similarityCatalogNames),
                "bare Zeekr 7GT name resolves to the exact catalog model");
        check(!AdvisorSimilarityIntent.isDirectModelLookup("Muéstrame un coche parecido al XPeng G6", similarityCatalogNames),
                "similarity wording takes precedence over direct lookup");
        check(!AdvisorSimilarityIntent.isDirectModelLookup("Búscame un SUV de 47 mil euros", similarityCatalogNames),
                "generic criteria requests are not treated as exact model lookup");
        check(AdvisorSimilarityIntent.shouldRoute("Quiero un coche el XPeng G6", similarityCatalogNames),
                "routes a generic car request naming a known model to similar-car search");
        check(AdvisorSimilarityIntent.shouldRoute("Quiero un coche parecido al XPeng G6", similarityCatalogNames),
                "keeps explicit similar-car phrasing working");
        check(!AdvisorSimilarityIntent.shouldRoute("Quiero un coche", similarityCatalogNames),
                "does not route a generic request without a named reference model");
        check(!AdvisorSimilarityIntent.shouldRoute("Quiero un SUV de 47 mil euros", similarityCatalogNames),
                "does not route a budget/body-style query as a similar-car request");

        engine.reset();
        r = engine.interpret("Quiero un coche similar al XPeng G6 de precio", AdvisorCriteriaEngine.Language.ES);
        check(r.needsClarification() && "clarify_reference_price_relation".equals(r.clarificationKey),
                "asks how price should relate to XPeng G6 when the request ends at 'de precio'");
        check(r.criteria.maxPrice == null && r.criteria.minPrice == null,
                "incomplete reference-price request does not invent a numeric budget");

        engine.reset();
        r = engine.interpret("Quiero un coche similar al XPeng G6 de precio...", AdvisorCriteriaEngine.Language.ES);
        check(r.needsClarification() && "clarify_reference_price_relation".equals(r.clarificationKey),
                "also asks for clarification when the unfinished price phrase ends with an ellipsis");


        // SUV + approximate price must exclude the BYD Dolphin Surf, which is a hatchback.
        engine.reset();
        r = engine.interpret("Quiero un SUV de unos 40000", AdvisorCriteriaEngine.Language.ES);
        check(!r.needsClarification() && "SUV".equals(r.criteria.bodyStyle),
                "parses SUV as a required body style in an approximate-price query");
        check(r.criteria.maxPrice != null && Math.abs(r.criteria.maxPrice - 40000d) < 0.01
                        && !r.criteria.maxPriceIsCeiling,
                "parses approximate SUV budget as a target around 40,000 euros");
        java.util.List<AdvisorCatalogMatcher.Candidate> suvCandidates = new java.util.ArrayList<>();
        suvCandidates.add(new AdvisorCatalogMatcher.Candidate("dolphin-surf", "BYD", "Dolphin Surf",
                "hatchback", "FWD", 26490, 322, 308, 43.2, 115, 16.0));
        suvCandidates.add(new AdvisorCatalogMatcher.Candidate("atto-3", "BYD", "Atto 3",
                "suv", "FWD", 39990, 420, 490, 60, 150, 16.0));
        suvCandidates.add(new AdvisorCatalogMatcher.Candidate("fiat-grande-panda", "Fiat", "Grande Panda",
                "crossover", "FWD", 24412, 320, 361, 44, 83, 14.0));
        suvCandidates.add(new AdvisorCatalogMatcher.Candidate("suv-over-budget", "Marca", "SUV 47000",
                "suv", "FWD", 47000, 420, 490, 60, 150, 16.0));
        java.util.List<AdvisorCatalogMatcher.Result> suvMatches =
                AdvisorCatalogMatcher.search(suvCandidates, r.criteria);
        check(suvMatches.size() == 1 && "atto-3".equals(suvMatches.get(0).candidate.logicalKey),
                "SUV search excludes hatchbacks, cheaper crossovers such as Grande Panda, and SUVs outside the approximate budget");

        // Body-style compatibility: berlina includes fastback/liftback, not a conventional hatchback.
        AdvisorCriteriaEngine.Criteria sedanFilter = new AdvisorCriteriaEngine.Criteria();
        sedanFilter.bodyStyle = "SEDAN";
        java.util.List<AdvisorCatalogMatcher.Candidate> bodyCandidates = new java.util.ArrayList<>();
        bodyCandidates.add(new AdvisorCatalogMatcher.Candidate("model3", "Tesla", "Model 3",
                "sedan", "RWD", 39990, 513, 594, 60, 208, 14.4));
        bodyCandidates.add(new AdvisorCatalogMatcher.Candidate("ev4-fastback", "Kia", "EV4 Fastback",
                "sedan", "FWD", 37953, 440, 490, 58.3, 150, 14.9));
        bodyCandidates.add(new AdvisorCatalogMatcher.Candidate("e408", "Peugeot", "E-408",
                "liftback", "FWD", 41290, 453, 536, 58.2, 157, 15.2));
        bodyCandidates.add(new AdvisorCatalogMatcher.Candidate("ev4-hatchback", "Kia", "EV4 Hatchback",
                "hatchback", "FWD", 37951, 440, 435, 58.3, 150, 14.9));
        java.util.List<AdvisorCatalogMatcher.Result> sedanMatches =
                AdvisorCatalogMatcher.search(bodyCandidates, sedanFilter);
        check(sedanMatches.size() == 3, "berlina search includes sedan, fastback and liftback, not hatchback");
        check(sedanMatches.stream().anyMatch(m -> "e408".equals(m.candidate.logicalKey)),
                "berlina search includes Peugeot E-408 liftback");
        check(sedanMatches.stream().noneMatch(m -> "ev4-hatchback".equals(m.candidate.logicalKey)),
                "berlina search excludes Kia EV4 Hatchback");

        // Negative length follow-up: keep prior SUV/budget filters and exclude lengths > 4.7 m.
        engine.reset();
        r = engine.interpret("Búscame un SUV de hasta 45.000 €", AdvisorCriteriaEngine.Language.ES);
        r = engine.interpret("Y además, sin coches de más de 4,7 metros", AdvisorCriteriaEngine.Language.ES);
        check(!r.needsClarification() && r.criteria.maxLengthM != null
                        && Math.abs(r.criteria.maxLengthM - 4.7d) < 0.001,
                "parses negative maximum-length follow-up as an exact 4.7 m ceiling");
        check(r.criteria.maxPrice != null && Math.abs(r.criteria.maxPrice - 45000d) < 0.01
                        && "SUV".equals(r.criteria.bodyStyle),
                "negative length follow-up preserves previous SUV and budget criteria");

        java.util.List<AdvisorCatalogMatcher.Candidate> lengthCandidates = new java.util.ArrayList<>();
        lengthCandidates.add(new AdvisorCatalogMatcher.Candidate("short", "Marca", "Coche 4.70", "suv", "RWD",
                40000, 500, 600, 80, 200, 18, 4.70));
        lengthCandidates.add(new AdvisorCatalogMatcher.Candidate("long", "Skywell", "BE11", "suv", "RWD",
                40000, 500, 600, 80, 200, 18, 4.72));
        lengthCandidates.add(new AdvisorCatalogMatcher.Candidate("unknown", "Marca", "Longitud desconocida", "suv", "RWD",
                40000, 500, 600, 80, 200, 18, 0));
        java.util.List<AdvisorCatalogMatcher.Result> lengthMatches =
                AdvisorCatalogMatcher.search(lengthCandidates, r.criteria);
        check(lengthMatches.size() == 1 && "short".equals(lengthMatches.get(0).candidate.logicalKey),
                "4.70 m is included while 4.72 m Skywell BE11 and unknown-length vehicles are excluded");


        // Regression for the user's device report: Škoda Peaq is 4.658 m and must
        // not survive a cumulative maximum-length follow-up of 4.5 m.
        engine.reset();
        engine.interpret("Búscame un SUV de hasta 45.000 €", AdvisorCriteriaEngine.Language.ES);
        r = engine.interpret("Y además, sin coches de más de 4,5 metros", AdvisorCriteriaEngine.Language.ES);
        check(!r.needsClarification() && r.criteria.maxLengthM != null
                        && Math.abs(r.criteria.maxLengthM - 4.5d) < 0.001,
                "parses 4,5 m negative length follow-up as an exact ceiling");
        java.util.List<AdvisorCatalogMatcher.Candidate> peaqCandidates = new java.util.ArrayList<>();
        peaqCandidates.add(new AdvisorCatalogMatcher.Candidate("peaq", "Škoda", "Peaq", "suv", "RWD",
                45000, 440, 935, 63, 150, 14.3, 4.658));
        peaqCandidates.add(new AdvisorCatalogMatcher.Candidate("exact45", "Marca", "Coche 4.50", "suv", "RWD",
                40000, 440, 600, 63, 150, 14.3, 4.5));
        peaqCandidates.add(new AdvisorCatalogMatcher.Candidate("short45", "Marca", "Coche 4.40", "suv", "RWD",
                40000, 440, 600, 63, 150, 14.3, 4.4));
        java.util.List<AdvisorCatalogMatcher.Result> peaqMatches =
                AdvisorCatalogMatcher.search(peaqCandidates, r.criteria);
        check(peaqMatches.size() == 2
                        && peaqMatches.stream().noneMatch(m -> "peaq".equals(m.candidate.logicalKey)),
                "4.658 m Škoda Peaq is excluded by a 4.5 m maximum while exact-boundary and shorter cars remain");

        // All constraints entered in a single message: the last, strictest length
        // ceiling (4.5 m) must win over the earlier 4.7 m ceiling.
        engine.reset();
        r = engine.interpret("Búscame un SUV de hasta 45.000 € Y además, sin coches de más de 4,7 metros Y además, sin coches de más de 4,5 metros",
                AdvisorCriteriaEngine.Language.ES);
        check(!r.needsClarification() && r.criteria.maxLengthM != null
                        && Math.abs(r.criteria.maxLengthM - 4.5d) < 0.001,
                "combined query uses the strictest of repeated length constraints");
        check(r.criteria.maxPrice != null && Math.abs(r.criteria.maxPrice - 45000d) < 0.01
                        && "SUV".equals(r.criteria.bodyStyle),
                "combined repeated length constraints preserve SUV and budget filters");
        java.util.List<AdvisorCatalogMatcher.Result> combinedMatches =
                AdvisorCatalogMatcher.search(peaqCandidates, r.criteria);
        check(combinedMatches.size() == 2
                        && combinedMatches.stream().noneMatch(m -> "peaq".equals(m.candidate.logicalKey)),
                "combined query excludes 4.658 m Peaq under the final 4.5 m ceiling");

        // Regression for a fresh search after an earlier restrictive query.
        // The Activity must reset the engine before interpreting a new full request.
        engine.reset();
        r = engine.interpret("Búscame un SUV de hasta 45.000 €", AdvisorCriteriaEngine.Language.ES);
        r = engine.interpret("Y además, sin coches de más de 4,5 metros", AdvisorCriteriaEngine.Language.ES);
        check(r.criteria.maxLengthM != null && Math.abs(r.criteria.maxLengthM - 4.5d) < 0.001,
                "setup prior restrictive query for new-search reset regression");
        engine.reset(); // mirrors SimilarCarsActivity.runAdvisorSearch() for a complete new request
        r = engine.interpret("Búscame un SUV de hasta 20.000 €", AdvisorCriteriaEngine.Language.ES);
        check(!r.needsClarification() && r.criteria.maxPrice != null
                        && Math.abs(r.criteria.maxPrice - 20000d) < 0.01
                        && "SUV".equals(r.criteria.bodyStyle),
                "fresh SUV search parses its own body style and 20,000 euro ceiling");
        check(r.criteria.maxLengthM == null,
                "fresh SUV search does not inherit the previous 4.5 m length ceiling");

        System.out.println("Advisor engine and catalog matcher smoke tests passed: " + checks);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
        checks++;
    }
}
