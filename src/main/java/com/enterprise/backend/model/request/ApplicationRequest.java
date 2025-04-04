package com.enterprise.backend.model.request;

import com.enterprise.backend.model.enums.ApplicationStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ApplicationRequest {
    @NotNull(message = "Status is required")
    private ApplicationStatus status;

    private String note;
}
