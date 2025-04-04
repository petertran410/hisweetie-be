package com.enterprise.backend.model.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class ApplicantRequest {
    @NotEmpty(message = "Name is required")
    private String name;

    @Pattern(regexp = "^[\\w.-]+@[\\w-]+(\\.[\\w-]{2,4}){1,4}$", message = "invalid email!")
    @NotEmpty(message = "Email is required")
    private String email;

    @Pattern(regexp = "^[0-9+\\-]{9,15}$", message = "invalid phone number!")
    @NotEmpty(message = "Phone number is required")
    private String phoneNumber;

    @NotEmpty(message = "Resume URL is required")
    private String resumeUrl;

    @NotNull(message = "Job ID is required")
    private Long jobId;
}
