package com.enterprise.backend.model.request;

import lombok.Data;

@Data
public class SyncUserRequest {
    private String id;
    private String password;
    private String fullName;
    private String avaUrl;
    private String email;
    private String phone;
    private String address;
    private boolean isActive;
}
