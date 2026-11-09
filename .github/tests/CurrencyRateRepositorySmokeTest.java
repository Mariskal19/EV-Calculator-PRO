package com.evchargecalculator;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/** Dependency-free regression tests for ECB CSV parsing, cache safety and currency conversion. */
public final class CurrencyRateRepositorySmokeTest {
    private static int checks;

    public static void main(String[] args) {
        testAllSupportedCurrencies();
        testReorderedAndQuotedCsv();
        testInvalidObservations();
        testFailedDownloadPreservesCacheAndCanRecover();
        testIncompleteRefreshNeverReplacesCache();
        testLastUpdateSemantics();
        testConversions();
        testMissingQuotesFailExplicitly();
        testCatalogPriceUsesSelectedCurrency();
        testCatalogPriceInEurKeepsSourceAmount();
        testCatalogPriceFallsBackToEurWithoutValidRate();
        testCatalogPriceDoesNotAccumulateConversion();
        testCatalogPriceRejectsInvalidAmounts();
        System.out.println("Currency-rate tests passed: " + checks + " checks.");
    }

    private static void testAllSupportedCurrencies() {
        String[] codes = {"USD", "GBP", "CHF", "CAD", "AUD"};
        double[] values = {1.16, 0.87, 0.93, 1.61, 1.78};
        for (int i = 0; i < codes.length; i++) {
            String csv = "KEY,OBS_VALUE,TIME_PERIOD\nseries," + values[i] + ",2026-11-06\n";
            check(close(CurrencyRateRepository.parseCsvRate(csv), values[i]),
                    "parses " + codes[i] + " observation");
        }
    }

    private static void testReorderedAndQuotedCsv() {
        String csv = "\uFEFF\"TIME_PERIOD\",\"NOTE\",\"OBS_VALUE\",\"UNIT\"\r\n"
                + "\"2026-11-06\",\"contains, comma and \"\"quote\"\"\",\"1.2345\",\"USD\"\r\n";
        check(close(CurrencyRateRepository.parseCsvRate(csv), 1.2345),
                "finds OBS_VALUE by name when columns are reordered and fields are quoted");
        String latest = "OBS_VALUE,TIME_PERIOD\n1.10,2026-11-05\n1.20,2026-11-06\n";
        check(close(CurrencyRateRepository.parseCsvRate(latest), 1.20),
                "uses the final observation in the CSV");
        expectFailure(() -> CurrencyRateRepository.parseCsvRate("TIME_PERIOD,VALUE\n2026-11-06,1.2\n"),
                "missing OBS_VALUE header is rejected");
        expectFailure(() -> CurrencyRateRepository.parseCsvRate("OBS_VALUE\n\"unterminated"),
                "unterminated quoted field is rejected");
        expectFailure(() -> CurrencyRateRepository.parseCsvRate("OBS_VALUE\n\n"),
                "empty observations are rejected");
        expectFailure(() -> CurrencyRateRepository.parseCsvRate("OBS_VALUE,TIME_PERIOD\n1.2,2026-11-05\n,2026-11-06\n"),
                "a blank latest observation cannot silently reuse an older quote");
    }

    private static void testInvalidObservations() {
        String[] invalid = {"", "0", "-1", "not-a-number", "NaN", "Infinity", "-Infinity", "1e309"};
        for (String value : invalid) {
            expectFailure(() -> CurrencyRateRepository.parseCsvRate("OBS_VALUE\n\"" + value + "\"\n"),
                    "rejects invalid observation: " + value);
        }
    }

    private static void testFailedDownloadPreservesCacheAndCanRecover() {
        FakeCache cache = completeCache(1.0);
        cache.date = "2026-11-05";
        Map<String, Double> before = new LinkedHashMap<>(cache.rates);
        CurrencyRateRepository.CsvSource failing = currency -> {
            if ("CHF".equals(currency)) throw new IOException("simulated HTTP 503");
            return csv(1.2);
        };
        check(!CurrencyRateRepository.refreshIfNeeded(cache, failing, "2026-11-06"),
                "HTTP failure reports unsuccessful refresh");
        check(before.equals(cache.rates), "HTTP failure preserves every old quote");
        check("2026-11-05".equals(cache.date), "HTTP failure preserves last successful date");
        check("2026-11-05".equals(CurrencyRateRepository.lastUpdate(cache)),
                "lastUpdate continues to report the previous successful update");

        CurrencyRateRepository.CsvSource recovered = currency -> csv(rateFor(currency));
        check(CurrencyRateRepository.refreshIfNeeded(cache, recovered, "2026-11-06"),
                "a later call retries and recovers on the same day");
        check("2026-11-06".equals(cache.date), "successful retry updates last successful date");
        check(close(cache.rates.get("USD"), 1.16), "successful retry replaces old quote set");
        check(CurrencyRateRepository.isComplete(cache.rates), "successful retry stores all five currencies");
        check(!CurrencyRateRepository.refreshIfNeeded(cache, currency -> {
            throw new IOException("must not be called for today's successful cache");
        }, "2026-11-06"), "same-day successful cache avoids redundant download");
    }

    private static void testIncompleteRefreshNeverReplacesCache() {
        FakeCache cache = completeCache(1.0);
        cache.date = "2026-11-04";
        Map<String, Double> before = new LinkedHashMap<>(cache.rates);
        CurrencyRateRepository.CsvSource partial = currency -> {
            if ("AUD".equals(currency)) return "OBS_VALUE\n\n";
            return csv(1.3);
        };
        check(!CurrencyRateRepository.refresh(cache, partial, "2026-11-06"),
                "an update missing one currency is rejected");
        check(before.equals(cache.rates), "partial update cannot replace a valid old cache");
        check("2026-11-04".equals(cache.date), "partial update cannot advance lastUpdate");
        check(CurrencyRateRepository.isComplete(cache.rates), "old complete cache remains valid");
    }

    private static void testLastUpdateSemantics() {
        FakeCache cache = completeCache(1.1);
        cache.date = "2026-11-05";
        check("2026-11-05".equals(CurrencyRateRepository.lastUpdate(cache)),
                "lastUpdate exposes the last complete successful cache date");
        cache.rates.remove("CAD");
        check("".equals(CurrencyRateRepository.lastUpdate(cache)),
                "incomplete cache has no valid lastUpdate date");
    }

    private static void testConversions() {
        Map<String, Double> rates = completeCache(1.0).rates;
        rates.put("USD", 1.20);
        rates.put("GBP", 0.80);
        rates.put("CHF", 0.90);
        check(close(CurrencyRateRepository.rate(rates, "EUR"), 1.0), "EUR reference rate is exactly one");
        check(close(CurrencyRateRepository.convertFromEur(rates, 100.0, "USD"), 120.0),
                "EUR to USD conversion");
        check(close(CurrencyRateRepository.convert(rates, 100.0, "USD", "GBP"), 66.6666666667),
                "USD to GBP conversion uses EUR as common base");
        check(close(CurrencyRateRepository.convert(rates, 80.0, "GBP", "CHF"), 90.0),
                "GBP to CHF conversion");
    }

    private static void testMissingQuotesFailExplicitly() {
        Map<String, Double> incomplete = new LinkedHashMap<>();
        incomplete.put("USD", 1.2);
        expectFailure(() -> CurrencyRateRepository.rate(incomplete, "GBP"),
                "missing quote does not silently become one");
        expectFailure(() -> CurrencyRateRepository.convertFromEur(incomplete, 10.0, "GBP"),
                "conversion without quote fails explicitly");
        expectFailure(() -> CurrencyRateRepository.rate(incomplete, "XYZ"),
                "unsupported currency fails explicitly");
        expectFailure(() -> CurrencyRateRepository.convertFromEur(incomplete, Double.NaN, "USD"),
                "non-finite amount is rejected");
    }

    private static void testCatalogPriceUsesSelectedCurrency() {
        Map<String, Double> rates = catalogRates();
        check("120 $".equals(CatalogPriceFormatter.format(100.0, "USD", rates)),
                "catalog price is converted from EUR and formatted in selected USD");
        check("87 £".equals(CatalogPriceFormatter.format(100.0, "GBP", rates)),
                "catalog price uses the selected GBP rate and symbol");
        check("93 CHF".equals(CatalogPriceFormatter.format(100.0, "CHF", rates)),
                "catalog price uses the selected CHF rate and symbol");
        check("161 CA$".equals(CatalogPriceFormatter.format(100.0, "CAD", rates)),
                "catalog price uses the selected CAD rate and symbol");
        check("178 A$".equals(CatalogPriceFormatter.format(100.0, "AUD", rates)),
                "catalog price uses the selected AUD rate and symbol");
    }

    private static void testCatalogPriceInEurKeepsSourceAmount() {
        Map<String, Double> rates = catalogRates();
        check("100 €".equals(CatalogPriceFormatter.format(100.0, "EUR", rates)),
                "EUR selection preserves the catalog source price");
    }

    private static void testCatalogPriceFallsBackToEurWithoutValidRate() {
        check("100 €".equals(CatalogPriceFormatter.format(100.0, "USD", new LinkedHashMap<>())),
                "missing cached rate falls back to the original EUR amount");
        Map<String, Double> partial = new LinkedHashMap<>();
        partial.put("GBP", 0.87);
        check("100 €".equals(CatalogPriceFormatter.format(100.0, "USD", partial)),
                "missing selected-currency rate falls back to the original EUR amount");
        Map<String, Double> invalid = catalogRates();
        invalid.put("USD", Double.NaN);
        check("100 €".equals(CatalogPriceFormatter.format(100.0, "USD", invalid)),
                "invalid cached rate falls back to the original EUR amount");
    }

    private static void testCatalogPriceDoesNotAccumulateConversion() {
        Map<String, Double> rates = catalogRates();
        String firstDisplay = CatalogPriceFormatter.format(100.0, "USD", rates);
        String backInEur = CatalogPriceFormatter.format(100.0, "EUR", rates);
        String displayedAgain = CatalogPriceFormatter.format(100.0, "USD", rates);
        check("120 $".equals(firstDisplay) && "100 €".equals(backInEur)
                        && firstDisplay.equals(displayedAgain),
                "currency changes always format the unchanged EUR source amount");
    }

    private static void testCatalogPriceRejectsInvalidAmounts() {
        Map<String, Double> rates = catalogRates();
        check("—".equals(CatalogPriceFormatter.format(0.0, "USD", rates)),
                "zero catalog price is not presented as a valid price");
        check("—".equals(CatalogPriceFormatter.format(-1.0, "EUR", rates)),
                "negative catalog price is not presented as a valid price");
        check("—".equals(CatalogPriceFormatter.format(Double.NaN, "EUR", rates)),
                "non-finite catalog price is not presented as a valid price");
    }

    private static Map<String, Double> catalogRates() {
        Map<String, Double> rates = new LinkedHashMap<>();
        rates.put("USD", 1.20);
        rates.put("GBP", 0.87);
        rates.put("CHF", 0.93);
        rates.put("CAD", 1.61);
        rates.put("AUD", 1.78);
        return rates;
    }

    private static FakeCache completeCache(double value) {
        FakeCache cache = new FakeCache();
        for (String currency : CurrencyRateRepository.CURRENCIES) cache.rates.put(currency, value);
        return cache;
    }

    private static double rateFor(String currency) {
        if ("USD".equals(currency)) return 1.16;
        if ("GBP".equals(currency)) return 0.87;
        if ("CHF".equals(currency)) return 0.93;
        if ("CAD".equals(currency)) return 1.61;
        return 1.78;
    }

    private static String csv(double value) {
        return "\"TIME_PERIOD\",\"OBS_VALUE\",\"NOTE\"\n\"2026-11-06\",\"" + value
                + "\",\"ECB reference rate\"\n";
    }

    private static boolean close(double a, double b) {
        return Math.abs(a - b) < 0.000001;
    }

    private static void expectFailure(Runnable action, String message) {
        try {
            action.run();
            throw new AssertionError(message);
        } catch (IllegalArgumentException | IllegalStateException | ArithmeticException expected) {
            checks++;
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
        checks++;
    }

    private static final class FakeCache implements CurrencyRateRepository.Cache {
        final Map<String, Double> rates = new LinkedHashMap<>();
        String date = "";

        @Override public Map<String, Double> readRates() { return new LinkedHashMap<>(rates); }
        @Override public String lastSuccessfulUpdate() { return date; }
        @Override public void saveRates(Map<String, Double> values, String updateDate) {
            rates.clear();
            rates.putAll(values);
            date = updateDate;
        }
    }
}
