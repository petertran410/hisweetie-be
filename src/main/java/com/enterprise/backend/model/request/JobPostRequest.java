package com.enterprise.backend.model.request;

import com.enterprise.backend.model.SalaryRange;
import com.enterprise.backend.model.WorkingHour;
import com.enterprise.backend.model.enums.EmploymentType;
import com.enterprise.backend.model.enums.WorkMode;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class JobPostRequest {
    @NotEmpty(message = "Job title is required")
    private String title;

    @NotNull(message = "Vacancies is required")
    private int vacancies;

    @NotEmpty(message = "Location is required")
    private String location;

    @Valid
    private List<WorkingHour> workingHours;

    @Valid
    private SalaryRange salaryRanges;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    @NotNull(message = "Work mode is required")
    private WorkMode workMode;

    @NotNull(message = "Application deadline is required")
    private LocalDate applicationDeadline;

    @NotEmpty(message = "Job description is required")
    private String jobDescription;
}
