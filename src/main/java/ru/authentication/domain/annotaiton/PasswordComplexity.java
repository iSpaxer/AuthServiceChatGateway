package ru.authentication.domain.annotaiton;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.authentication.domain.annotaiton.constraint.PasswordComplexityValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordComplexityValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordComplexity {
    String message() default "Password does not meet complexity requirements";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
