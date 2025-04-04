package com.enterprise.backend.model.entity;

import com.enterprise.backend.model.SalaryRange;
import com.enterprise.backend.model.WorkingHour;
import com.enterprise.backend.model.enums.EmploymentType;
import com.enterprise.backend.model.enums.WorkMode;
import com.enterprise.backend.service.converter.ObjectConverter;
import com.enterprise.backend.service.converter.SalaryRangeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class JobPost extends Auditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private int vacancies;

    private String location;

    @Lob
    @Convert(converter = ObjectConverter.class)
    private List<WorkingHour> workingHours;

    @Lob
    @Convert(converter = SalaryRangeConverter.class)
    private SalaryRange salaryRanges;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    private WorkMode workMode;

    private LocalDate applicationDeadline;

    @Lob
    private String jobDescription;
}