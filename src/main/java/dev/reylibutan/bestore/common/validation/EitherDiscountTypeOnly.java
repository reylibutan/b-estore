package dev.reylibutan.bestore.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EitherDiscountTypeOnlyValidator.class)
public @interface EitherDiscountTypeOnly {

  String message() default "{validation.product_discount.either_discount_type_only}";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
