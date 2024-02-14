package dev.reylibutan.bestore.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotNullOrZeroValidator implements ConstraintValidator<NotNullOrZero, Number> {

  @Override
  public boolean isValid(Number number, ConstraintValidatorContext context) {
    return number != null && number.longValue() != 0;
  }
}
