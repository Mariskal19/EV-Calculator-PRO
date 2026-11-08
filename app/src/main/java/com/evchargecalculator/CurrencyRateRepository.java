package com.evchargecalculator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Pure-Java ECB rate parsing, atomic cache replacement and currency conversion logic. */
final class CurrencyRateRepository {
    static final List<String> CURRENCIES =
            Collections.unmodifiableList(Arrays.asList("USD", "GBP", "CHF", "CAD", "AUD"));

    interface Cache {
        Map<String, Double> readRates();
        String lastSuccessfulUpdate();
        void saveRates(Map<String, Double> rates, String updateDate);
    }

    interface CsvSource {
        String download(String currency) throws IOException;
    }

    private CurrencyRateRepository() {}

    /** Only a complete, valid cache has a meaningful successful-update date. */
    static String lastUpdate(Cache cache) {
        return isComplete(cache.readRates()) ? cache.lastSuccessfulUpdate() : "";
    }

    /** A failed attempt does not update lastUpdate, so the next call can retry immediately. */
    static boolean refreshIfNeeded(Cache cache, CsvSource source, String today) {
        if (today.equals(lastUpdate(cache))) return false;
        return refresh(cache, source, today);
    }

    /**
     * Downloads and validates the complete five-currency set before changing the cache.
     * Any HTTP, parsing or validation failure leaves old rates and update date untouched.
     */
    static boolean refresh(Cache cache, CsvSource source, String updateDate) {
        Map<String, Double> candidate = new LinkedHashMap<>();
        try {
            for (String currency : CURRENCIES) {
                double value = parseCsvRate(source.download(currency));
                requireValidRate(value, currency);
                candidate.put(currency, value);
            }
            requireCompleteRates(candidate);
            cache.saveRates(Collections.unmodifiableMap(candidate), updateDate);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    static boolean isComplete(Map<String, Double> rates) {
        if (rates == null) return false;
        for (String currency : CURRENCIES) {
            Double value = rates.get(currency);
            if (value == null || !isValidRate(value)) return false;
        }
        return true;
    }

    static double rate(Map<String, Double> rates, String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code is required");
        }
        String code = currency.trim().toUpperCase(java.util.Locale.ROOT);
        if ("EUR".equals(code)) return 1.0d;
        if (!CURRENCIES.contains(code)) {
            throw new IllegalArgumentException("Unsupported currency: " + code);
        }
        Double value = rates == null ? null : rates.get(code);
        if (value == null || !isValidRate(value)) {
            throw new IllegalStateException("No valid exchange rate available for " + code);
        }
        return value;
    }

    static double convertFromEur(Map<String, Double> rates, double eurAmount, String currency) {
        return convert(rates, eurAmount, "EUR", currency);
    }

    /** Converts using EUR as the common base: amount / sourceRate * targetRate. */
    static double convert(Map<String, Double> rates, double amount, String from, String to) {
        if (!Double.isFinite(amount)) {
            throw new IllegalArgumentException("Amount must be finite");
        }
        double converted = amount / rate(rates, from) * rate(rates, to);
        if (!Double.isFinite(converted)) {
            throw new ArithmeticException("Converted amount is not finite");
        }
        return converted;
    }

    /** Parses the latest non-empty OBS_VALUE field from an ECB CSV response. */
    static double parseCsvRate(String csv) {
        List<List<String>> records = parseCsv(csv);
        if (records.isEmpty()) throw new IllegalArgumentException("CSV is empty");

        List<String> header = records.get(0);
        int observationColumn = -1;
        for (int i = 0; i < header.size(); i++) {
            String name = header.get(i);
            if (i == 0 && name.startsWith("\uFEFF")) name = name.substring(1);
            if ("OBS_VALUE".equals(name.trim())) {
                if (observationColumn >= 0) {
                    throw new IllegalArgumentException("Duplicate OBS_VALUE column");
                }
                observationColumn = i;
            }
        }
        if (observationColumn < 0) {
            throw new IllegalArgumentException("CSV has no OBS_VALUE column");
        }

        String latest = null;
        for (int rowIndex = 1; rowIndex < records.size(); rowIndex++) {
            List<String> row = records.get(rowIndex);
            if (row.size() == 1 && row.get(0).trim().isEmpty()) continue;
            if (row.size() <= observationColumn) {
                throw new IllegalArgumentException("CSV data row is missing OBS_VALUE");
            }
            String value = row.get(observationColumn).trim();
            if (!value.isEmpty()) latest = value;
        }
        if (latest == null) throw new IllegalArgumentException("CSV contains no observation value");

        final double parsed;
        try {
            parsed = Double.parseDouble(latest);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("OBS_VALUE is not numeric", ex);
        }
        if (!isValidRate(parsed)) {
            throw new IllegalArgumentException("OBS_VALUE must be positive and finite");
        }
        return parsed;
    }

    private static List<List<String>> parseCsv(String csv) {
        if (csv == null) throw new IllegalArgumentException("CSV is null");
        if (csv.startsWith("\uFEFF")) csv = csv.substring(1);
        List<List<String>> records = new ArrayList<>();
        List<String> record = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        boolean quoteClosed = false;

        for (int i = 0; i < csv.length(); i++) {
            char ch = csv.charAt(i);
            if (quoted) {
                if (ch == '"') {
                    if (i + 1 < csv.length() && csv.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        quoted = false;
                        quoteClosed = true;
                    }
                } else {
                    field.append(ch);
                }
                continue;
            }

            if (ch == ',') {
                record.add(field.toString());
                field.setLength(0);
                quoteClosed = false;
            } else if (ch == '\n' || ch == '\r') {
                record.add(field.toString());
                field.setLength(0);
                records.add(record);
                record = new ArrayList<>();
                quoteClosed = false;
                if (ch == '\r' && i + 1 < csv.length() && csv.charAt(i + 1) == '\n') i++;
            } else if (ch == '"') {
                if (field.toString().trim().isEmpty() && !quoteClosed) {
                    field.setLength(0);
                    quoted = true;
                } else {
                    throw new IllegalArgumentException("Unexpected quote in CSV field");
                }
            } else {
                if (quoteClosed && !Character.isWhitespace(ch)) {
                    throw new IllegalArgumentException("Unexpected text after quoted CSV field");
                }
                if (!quoteClosed) field.append(ch);
            }
        }

        if (quoted) throw new IllegalArgumentException("Unterminated quoted CSV field");
        if (field.length() > 0 || !record.isEmpty() || records.isEmpty()) {
            record.add(field.toString());
            records.add(record);
        }
        return records;
    }

    private static void requireCompleteRates(Map<String, Double> rates) {
        if (!isComplete(rates)) {
            throw new IllegalArgumentException("Exchange-rate set is incomplete or invalid");
        }
    }

    private static void requireValidRate(double value, String currency) {
        if (!isValidRate(value)) {
            throw new IllegalArgumentException("Invalid exchange rate for " + currency);
        }
    }

    private static boolean isValidRate(double value) {
        return Double.isFinite(value) && value > 0.0d;
    }
}
