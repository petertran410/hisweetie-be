package com.enterprise.backend.model.request;

import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.enums.EmploymentType;
import com.enterprise.backend.model.enums.WorkMode;
import com.enterprise.backend.model.error.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.enterprise.backend.util.Utils.DATE_FORMATTER;

@Getter
@Setter
public class SearchJobPostRequest extends SearchRequest {
    private String title;
    private String location;
    private WorkMode workMode;
    private EmploymentType employmentType;
    private LocalDate applicationDeadline;

    public void setApplicationDeadline(String applicationDeadline) {
        if (StringUtils.isEmpty(applicationDeadline)) return;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        try {
            this.applicationDeadline = LocalDate.parse(applicationDeadline, formatter);
        } catch (Exception e) {
            throw new EnterpriseBackendException(ErrorCode.INVALID_DATE, e.getMessage());
        }
    }
}
