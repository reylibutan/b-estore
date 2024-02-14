package dev.reylibutan.bestore.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotNullOrZeroValidator.class)
public @interface NotNullOrZero {

  String message() default "{validation.common.not_null_or_zero}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
