package com.evchargecalculator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/** Formats numeric values using the separators associated with the selected currency. */
public final class CurrencyNumberFormatter {
  private CurrencyNumberFormatter() {}

  public static String format(double value, int decimals, String currency) {
    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    if ("CHF".equals(currency)) {
      symbols.setGroupingSeparator('\'');
      symbols.setDecimalSeparator('.');
    } else if ("EUR".equals(currency)) {
      symbols.setGroupingSeparator('.');
      symbols.setDecimalSeparator(',');
    } else {
      symbols.setGroupingSeparator(',');
      symbols.setDecimalSeparator('.');
    }
    StringBuilder pattern = new StringBuilder("#,##0");
    if (decimals > 0) {
      pattern.append('.');
      for (int i = 0; i < decimals; i++) pattern.append('0');
    }
    DecimalFormat format = new DecimalFormat(pattern.toString(), symbols);
    format.setGroupingUsed(true);
    return format.format(value);
  }
}
