package com.enterprise.backend.model.response;

import com.enterprise.backend.model.enums.ApplicationStatus;
import lombok.Data;

@Data
public class ApplicationResponse {
    private ApplicationStatus status;
    private String note;
    private String createdDate;
    private String updatedDate;
    private ApplicantResponse applicant;
    private JobPostResponse jobPost;
}
