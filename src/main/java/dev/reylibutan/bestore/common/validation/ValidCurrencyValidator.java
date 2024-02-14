package dev.reylibutan.bestore.common.validation;

import dev.reylibutan.bestore.common.domain.Currency;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCurrencyValidator implements ConstraintValidator<ValidCurrency, Currency> {

  @Override
  public boolean isValid(Currency currency, ConstraintValidatorContext context) {
    // let's allow null currencies for now and decide a default for them
    return currency == null || Currency.isSupported(currency);
  }
}
