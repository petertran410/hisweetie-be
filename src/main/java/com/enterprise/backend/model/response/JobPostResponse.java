package com.enterprise.backend.model.response;

import com.enterprise.backend.model.SalaryRange;
import com.enterprise.backend.model.WorkingHour;
import com.enterprise.backend.model.enums.EmploymentType;
import com.enterprise.backend.model.enums.WorkMode;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

import static com.enterprise.backend.util.Utils.DATE_FORMATTER;

@Data
public class JobPostResponse {
    private Long id;
    private String title;
    private int vacancies;
    private String location;
    private List<WorkingHour> workingHours;
    private SalaryRange salaryRanges;
    private EmploymentType employmentType;
    private WorkMode workMode;

    @JsonFormat(pattern = DATE_FORMATTER)
    private LocalDate applicationDeadline;

    private String jobDescription;
    private String createdDate;
    private String updatedDate;
}
