package com.enterprise.backend.model.response;

import lombok.Data;

@Data
public class ApplicantResponse {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String resumeUrl;
}
