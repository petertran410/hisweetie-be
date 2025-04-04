package com.enterprise.backend.util.validator.salaryrange;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SalaryRangeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSalaryRange {
    String message() default "Mức lương tối thiểu phải nhỏ hơn hoặc bằng mức lương tối đa.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
