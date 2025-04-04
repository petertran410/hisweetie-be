package com.enterprise.backend.service;

import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.entity.Authority;
import com.enterprise.backend.model.entity.User;
import com.enterprise.backend.model.error.ErrorCode;
import com.enterprise.backend.model.request.BanUserRequest;
import com.enterprise.backend.model.request.SetAuthorityRequest;
import com.enterprise.backend.model.response.UserResponse;
import com.enterprise.backend.service.repository.AuthorityRepository;
import com.enterprise.backend.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    @Value("${sync-service.ban-user.url}")
    private String banUserUrl;
    @Value("${sync-service.authority-user.url}")
    private String authorityUserUrl;
    @Value("${security.secret-key}")
    private String secretKey;

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);

    public Page<UserResponse> getByType(int pageIndex, int pageSize, String type, String keyword) {
        Pageable paging = PageRequest.of(pageIndex - 1, pageSize, Sort.by(Sort.Direction.DESC, "created_date"));
        Page<User> userPage;

        if ("BANNED".equals(type)) {
            userPage = userRepository.getAllByActive(false, keyword, paging);
        } else if ("USER".equals(type)) {
            userPage = userRepository.getAllByRole(keyword, paging);
        } else if (type != null) {
            userPage = userRepository.getAllByRole(type, keyword, paging);
        } else {
            userPage = userRepository.findByKeyWord(keyword, paging);
        }

        return userPage.map(UserResponse::from);
    }

    public List<UserResponse> searchByEmailOrPhone(String keyword) {
        List<User> users = userRepository.searchByEmailOrPhone(keyword);
        return users.stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    public void addAuthorityWithUserId(String userId, Authority.Role role, Boolean sync) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EnterpriseBackendException(ErrorCode.USER_NOT_FOUND));

        authorityRepository.findByUserAndRole(user, role).ifPresent(authority -> {
            throw new EnterpriseBackendException(ErrorCode.ROLE_ALREADY_EXIST);
        });

        if (role == Authority.Role.ROLE_USER) {
            deleteAdminWithUserId(userId);
        }

        if (role == Authority.Role.ROLE_ADMIN) {
            deleteUserWithUserId(userId);
        }

        Authority authority = new Authority();
        authority.setRole(role);
        authority.setUser(user);
        authorityRepository.save(authority);

        if (sync) {
            executorService.schedule(() -> syncAuthorityToAnotherService(userId, role), 3, TimeUnit.SECONDS);
        }
    }

    public void syncAuthorityToAnotherService(String userId, Authority.Role role) {
        var request = new SetAuthorityRequest();
        request.setUsername(userId);
        request.setRole(role.name());
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Force-Signature", secretKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<SetAuthorityRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Object> response = restTemplate.exchange(authorityUserUrl, HttpMethod.POST, entity, Object.class);

            // Kiểm tra mã phản hồi (status code)
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Sync authority to another service successfully");
            } else {
                log.error("Failed to sync authority. Status code: {} - response: {}", response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            // Log chi tiết thông tin lỗi
            log.error("Error when syncing authority to another service. Error: {}", e.getMessage(), e);
        }
    }

    public void deleteAdminWithUserId(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EnterpriseBackendException(ErrorCode.USER_NOT_FOUND));

        Authority authority = authorityRepository.findByUserAndRole(user, Authority.Role.ROLE_ADMIN).orElseThrow(() ->
                new EnterpriseBackendException(ErrorCode.ROLE_NOT_FOUND));

        authorityRepository.delete(authority);
    }

    public void deleteUserWithUserId(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EnterpriseBackendException(ErrorCode.USER_NOT_FOUND));

        Authority authority = authorityRepository.findByUserAndRole(user, Authority.Role.ROLE_USER).orElseThrow(() ->
                new EnterpriseBackendException(ErrorCode.ROLE_NOT_FOUND));

        authorityRepository.delete(authority);
    }

    public UserResponse banUser(String userId, Boolean sync) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EnterpriseBackendException(ErrorCode.USER_NOT_FOUND));

        user.setActive(false);

        var result = userRepository.save(user);
        if (sync) {
            executorService.schedule(() -> syncBanUserToAnotherService(userId), 1, TimeUnit.SECONDS);
        }
        return UserResponse.from(result);
    }

    public void syncBanUserToAnotherService(String userId) {
        var request = new BanUserRequest();
        request.setUsername(userId);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Force-Signature", secretKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<BanUserRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Object> response = restTemplate.exchange(banUserUrl, HttpMethod.POST, entity, Object.class);

            // Kiểm tra mã phản hồi (status code)
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Sync ban user to another service successfully");
            } else {
                log.error("Failed to sync ban user. Status code: {} - response: {}", response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            // Log chi tiết thông tin lỗi
            log.error("Error when syncing ban user to another service. Error: {}", e.getMessage(), e);
        }
    }
}
