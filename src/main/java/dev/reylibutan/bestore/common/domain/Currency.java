package dev.reylibutan.bestore.common.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = CurrencyDeserializer.class)
public enum Currency {
  HKD, USD, UNSUPPORTED;

  public static Currency getDefault() {
    return HKD;
  }

  public static Currency getValue(String str) {
    try {
      return Currency.valueOf(str);
    } catch (IllegalArgumentException e) {
      return UNSUPPORTED;
    }
  }

  public static boolean isSupported(Currency currency) {
    return currency != null && !UNSUPPORTED.equals(currency);
  }
}
