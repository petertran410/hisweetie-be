package com.enterprise.backend.model;

import com.enterprise.backend.util.validator.salaryrange.ValidSalaryRange;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ValidSalaryRange
public class SalaryRange {
    @NotNull(message = "Mức lương tối thiểu không được để trống.")
    @Min(value = 0, message = "Mức lương tối thiểu phải lớn hơn hoặc bằng 0.")
    private Long min;

    @NotNull(message = "Mức lương tối đa không được để trống.")
    @Min(value = 0, message = "Mức lương tối đa phải lớn hơn hoặc bằng 0.")
    private Long max;
}

