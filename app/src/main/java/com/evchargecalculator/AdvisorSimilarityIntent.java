package com.evchargecalculator;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Pure-Java intent gate for catalog lookups and similarity requests. */
public final class AdvisorSimilarityIntent {
    private AdvisorSimilarityIntent() {}

    public static boolean shouldRoute(String query, List<String> catalogVehicleNames) {
        String normalized = normalize(query);
        if (normalized.isEmpty()) return false;

        boolean explicitSimilarity = containsAny(normalized,
                "parecido", "parecida", "similares", "similar", "semejante", "similar to",
                "like the", "alternativa a", "alternativas a", "rival de", "competidor de",
                " como ", "como ", "look like", "something like");
        if (explicitSimilarity) return true;

        boolean genericCarRequest = containsAny(normalized,
                "quiero un coche", "quiero coche", "buscame un coche", "buscame coche",
                "busca un coche", "busco un coche", "encuentrame un coche",
                "recomiendame un coche", "quiero un auto", "buscame un auto",
                "i want a car", "find me a car", "looking for a car");
        if (!genericCarRequest || catalogVehicleNames == null || catalogVehicleNames.isEmpty()) {
            return false;
        }

        String compactQuery = compact(normalized);
        for (String catalogName : catalogVehicleNames) {
            String compactName = compact(normalize(catalogName));
            if (compactName.length() >= 4 && compactQuery.contains(compactName)) return true;
        }
        return false;
    }

    /**
     * Detects a direct catalog lookup from a make/model identity. A query may also
     * contain trim words (stored separately in the catalog's version field), but
     * a trim word alone is not enough to turn a generic request into a direct lookup.
     */
    public static boolean isDirectModelLookup(String query, List<String> catalogVehicleNames) {
        String normalized = normalize(query);
        if (normalized.isEmpty() || catalogVehicleNames == null || catalogVehicleNames.isEmpty()) return false;

        boolean similarityIntent = containsAny(normalized,
                "parecido", "parecida", "similares", "similar", "semejante",
                "alternativa", "alternativas", "rival", "competidor", "competidores",
                "como el ", "como la ", "como un ", "similar to", "like the",
                "look like", "something like");
        if (similarityIntent) return false;

        List<String> queryTokens = meaningfulTokens(normalized);
        if (queryTokens.isEmpty()) return false;

        // The request must identify at least one make/model token. This permits a
        // make-only search (e.g. "Tesla") without treating "Performance" as a model.
        for (String catalogName : catalogVehicleNames) {
            if (catalogName == null) continue;
            String[] parts = catalogName.trim().split("\\s+", 2);
            String identity = parts.length == 2 ? parts[0] + " " + parts[1] : catalogName;
            List<String> identityTokens = meaningfulTokens(identity);
            for (String token : queryTokens) {
                if (identityTokens.contains(token)) return true;
            }
        }
        return false;
    }

    /**
     * Tests all meaningful query words against a single catalog vehicle, including
     * its version/trim field. Matching whole tokens supports "Model 3 Performance"
     * without allowing "Model 3" to match an unrelated "Model 30".
     */
    public static boolean matchesDirectCatalogEntry(String query, String make, String model, String version) {
        List<String> queryTokens = meaningfulTokens(query);
        if (queryTokens.isEmpty()) return false;
        List<String> vehicleTokens = meaningfulTokens(
                safe(make) + " " + safe(model) + " " + safe(version));
        return vehicleTokens.containsAll(queryTokens);
    }

    private static List<String> meaningfulTokens(String text) {
        String[] parts = normalize(text).split("\\s+");
        List<String> tokens = new ArrayList<>();
        for (String token : parts) {
            if (token.isEmpty() || isFiller(token)) continue;
            tokens.add(token);
        }
        return tokens;
    }

    private static boolean isFiller(String token) {
        switch (token) {
            case "buscame": case "busca": case "busco": case "encuentrame":
            case "muestrame": case "muestra": case "mostrar": case "quiero":
            case "ver": case "ensename": case "show": case "find": case "look":
            case "up": case "display": case "me": case "model": case "el": case "la": case "los":
            case "las": case "un": case "una": case "unos": case "unas":
            case "de": case "del": case "por": case "favor": case "coche":
            case "coches": case "auto": case "autos": case "vehiculo": case "vehiculos":
                return true;
            default:
                return false;
        }
    }

    private static boolean containsAny(String text, String... patterns) {
        for (String pattern : patterns) if (text.contains(pattern)) return true;
        return false;
    }

    private static String compact(String text) {
        return text.replaceAll("[^a-z0-9]", "");
    }

    private static String safe(String value) { return value == null ? "" : value.trim(); }

    private static String normalize(String text) {
        if (text == null) return "";
        String lower = text.toLowerCase(Locale.ROOT).trim();
        return Normalizer.normalize(lower, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replaceAll("[^\\p{L}\\p{N}]+", " ").trim();
    }
}
