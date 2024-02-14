package dev.reylibutan.bestore.common.validation;

import dev.reylibutan.bestore.productdiscount.ProductDiscountDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EitherDiscountTypeOnlyValidator implements ConstraintValidator<EitherDiscountTypeOnly, ProductDiscountDto> {

  @Override
  public boolean isValid(ProductDiscountDto productDiscountDto, ConstraintValidatorContext context) {
    return (productDiscountDto.getFixedDiscount() != null && productDiscountDto.getPercentageDiscount() == null)
        || (productDiscountDto.getFixedDiscount() == null && productDiscountDto.getPercentageDiscount() != null);
  }
}
