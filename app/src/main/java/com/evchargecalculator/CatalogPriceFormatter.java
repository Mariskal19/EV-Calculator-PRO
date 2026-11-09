package com.evchargecalculator;

import java.util.Locale;
import java.util.Map;

/** Converts catalog prices from their immutable EUR source for display only. */
public final class CatalogPriceFormatter {
    private CatalogPriceFormatter() {}

    static String format(double priceEur, String currency, Map<String, Double> cachedRates) {
        if (!Double.isFinite(priceEur) || priceEur <= 0.0d) return "—";
        String code = currency == null ? "EUR" : currency.trim().toUpperCase(Locale.ROOT);
        if (code.isEmpty() || "EUR".equals(code)) return formatEur(priceEur);
        try {
            double converted = CurrencyRateRepository.convertFromEur(cachedRates, priceEur, code);
            return CurrencyNumberFormatter.format(converted, 2, code) + " " + symbol(code);
        } catch (IllegalArgumentException | IllegalStateException | ArithmeticException ex) {
            return formatEur(priceEur);
        }
    }

    private static String formatEur(double priceEur) {
        return CurrencyNumberFormatter.format(priceEur, 2, "EUR") + " €";
    }

    private static String symbol(String currency) {
        switch (currency) {
            case "USD": return "$";
            case "GBP": return "£";
            case "CHF": return "CHF";
            case "CAD": return "CA$";
            case "AUD": return "A$";
            default: throw new IllegalArgumentException("Unsupported currency: " + currency);
        }
    }
}
