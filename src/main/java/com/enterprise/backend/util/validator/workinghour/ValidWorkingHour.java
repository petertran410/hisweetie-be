package com.enterprise.backend.util.validator.workinghour;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = WorkingHourValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidWorkingHour {
    String message() default "Thời gian bắt đầu phải trước thời gian kết thúc.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
