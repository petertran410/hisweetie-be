package com.enterprise.backend.util.validator.workinghour;

import com.enterprise.backend.model.WorkingHour;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WorkingHourValidator implements ConstraintValidator<ValidWorkingHour, WorkingHour> {

    @Override
    public boolean isValid(WorkingHour workingHour, ConstraintValidatorContext context) {
        if (workingHour.getStart() == null || workingHour.getEnd() == null) {
            return false; // Đã có @NotNull xử lý riêng
        }
        return workingHour.getStart().isBefore(workingHour.getEnd());
    }
}
