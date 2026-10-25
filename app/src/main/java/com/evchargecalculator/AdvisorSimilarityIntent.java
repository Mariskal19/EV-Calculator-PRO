package com.evchargecalculator;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

/** Pure-Java intent gate for natural-language requests to find alternatives to a named car. */
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
     * Detects a request to display a named catalog model itself, not alternatives.
     * This is catalog-driven and therefore applies to every make/model, not a
     * hard-coded brand. Similarity language always takes precedence.
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

        boolean directIntent = containsAny(normalized,
                "buscame", "busca ", "busco ", "encuentrame", "muestrame",
                "muestra ", "mostrar ", "quiero ver", "ensename", "ver el ",
                "ver la ", "show me", "find me", "look up", "display ");

        // A bare model name is also a direct lookup, but only when it resolves
        // uniquely in the catalog. This avoids requiring verbs such as "show me".
        if (!directIntent) {
            String bareQuery = normalized.trim();
            String matchedBareModel = null;
            for (String catalogName : catalogVehicleNames) {
                if (catalogName == null) continue;
                String normalizedName = normalize(catalogName).trim();
                if (bareQuery.equals(normalizedName)) return true;

                String[] parts = catalogName.trim().split("\\s+", 2);
                if (parts.length < 2) continue;
                String model = normalize(parts[1]).trim();
                if (!bareQuery.equals(model)) continue;
                String key = compact(normalize(catalogName));
                if (matchedBareModel == null) matchedBareModel = key;
                else if (!matchedBareModel.equals(key)) return false;
            }
            return matchedBareModel != null;
        }

        String compactQuery = compact(normalized);
        for (String catalogName : catalogVehicleNames) {
            String compactName = compact(normalize(catalogName));
            if (compactName.length() >= 4 && compactQuery.contains(compactName)) return true;
        }

        // Permit model-only requests (e.g. "Muéstrame el G6") only when the
        // model name resolves to one make/model in the catalog.
        String paddedQuery = " " + normalized + " ";
        String matchedModelKey = null;
        for (String catalogName : catalogVehicleNames) {
            if (catalogName == null) continue;
            String[] parts = catalogName.trim().split("\\s+", 2);
            if (parts.length < 2) continue;
            String model = normalize(parts[1]).trim();
            if (model.length() < 2 || !paddedQuery.contains(" " + model + " ")) continue;
            String key = compact(normalize(catalogName));
            if (matchedModelKey == null) matchedModelKey = key;
            else if (!matchedModelKey.equals(key)) return false;
        }
        return matchedModelKey != null;
    }

    private static boolean containsAny(String text, String... patterns) {
        for (String pattern : patterns) if (text.contains(pattern)) return true;
        return false;
    }

    private static String compact(String text) {
        return text.replaceAll("[^a-z0-9]", "");
    }

    private static String normalize(String text) {
        if (text == null) return "";
        String lower = text.toLowerCase(Locale.ROOT).trim();
        return Normalizer.normalize(lower, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
    }
}
