package com.evchargecalculator;

import java.text.Normalizer;
import java.util.Locale;

/** Classifies conversational advisor follow-ups separately from complete new searches. */
public final class AdvisorQueryClassifier {
    private AdvisorQueryClassifier() {}

    public static boolean isFollowUp(String query) {
        if (query == null) return false;
        String normalized = Normalizer.normalize(query, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT).trim();
        if (normalized.isEmpty()) return false;
        String[] prefixes = {
                "y ", "y ademas ", "tambien ", "tambien quiero ", "ademas ",
                "anade ", "agrega ", "quita ", "elimina ", "sin ",
                "mas de ", "menos de ", "al menos ", "como minimo ",
                "como maximo ", "por encima de ", "por debajo de ",
                "que tenga ", "con mas de ", "con menos de ",
                "con al menos ", "hasta "
        };
        for (String prefix : prefixes) {
            if (normalized.startsWith(prefix)) return true;
        }
        return false;
    }
}
