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
