package com.enterprise.backend.controller;

import com.enterprise.backend.model.entity.Authority;
import com.enterprise.backend.model.request.BanUserRequest;
import com.enterprise.backend.model.request.SetAuthorityRequest;
import com.enterprise.backend.model.request.SyncUserRequest;
import com.enterprise.backend.model.response.UserResponse;
import com.enterprise.backend.service.AdminService;
import com.enterprise.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalController {
    private final UserService userService;
    private final AdminService adminService;

    @PostMapping("/sync-user")
    public void syncUser(@RequestBody SyncUserRequest request) {
        userService.syncUser(request);
    }

    @PostMapping("/authority")
    public ResponseEntity<List<UserResponse>> setAuthority(@RequestBody SetAuthorityRequest request) {
        Authority.Role role = Authority.Role.valueOf(request.getRole());
        adminService.addAuthorityWithUserId(request.getUsername(), role, false);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/ban")
    public ResponseEntity<UserResponse> banUser(@RequestBody BanUserRequest request) {
        return ResponseEntity.ok(adminService.banUser(request.getUsername(), false));
    }
}
