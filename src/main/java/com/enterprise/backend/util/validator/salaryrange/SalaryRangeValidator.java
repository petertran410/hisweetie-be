package com.enterprise.backend.util.validator.salaryrange;

import com.enterprise.backend.model.SalaryRange;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SalaryRangeValidator implements ConstraintValidator<ValidSalaryRange, SalaryRange> {

    @Override
    public boolean isValid(SalaryRange salaryRange, ConstraintValidatorContext context) {
        if (salaryRange == null) {
            return true; // Không bắt buộc kiểm tra nếu object null
        }
        if (salaryRange.getMin() == null || salaryRange.getMax() == null) {
            return false; // Không hợp lệ nếu thiếu dữ liệu
        }
        return salaryRange.getMin() <= salaryRange.getMax();
    }
}
