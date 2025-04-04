package com.enterprise.backend.controller;

import com.enterprise.backend.auth.AuthoritiesConstants;
import com.enterprise.backend.model.request.ApplicantRequest;
import com.enterprise.backend.model.request.ApplicationRequest;
import com.enterprise.backend.model.request.JobPostRequest;
import com.enterprise.backend.model.request.SearchApplicationRequest;
import com.enterprise.backend.model.request.SearchJobPostRequest;
import com.enterprise.backend.model.response.ApplicationResponse;
import com.enterprise.backend.model.response.JobPostResponse;
import com.enterprise.backend.service.ApplicationService;
import com.enterprise.backend.service.JobPostService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Profile("dieptra")
@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobPostController {
    private final JobPostService jobPostService;
    private final ApplicationService applicationService;

    @PostMapping("/admin")
    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.ROLE_SUPER_ADMIN})
    @ApiOperation(value = "Admin create job", authorizations = {@Authorization(value = "Bearer")})
    public void createJobPost(@Valid @RequestBody JobPostRequest jobPostRequest) {
        jobPostService.createJobPost(jobPostRequest);
    }

    @PutMapping("/admin/{id}")
    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.ROLE_SUPER_ADMIN})
    @ApiOperation(value = "Admin update job", authorizations = {@Authorization(value = "Bearer")})
    public void updateJobPost(@PathVariable Long id, @Valid @RequestBody JobPostRequest jobPostRequest) {
        jobPostService.updateJobPost(jobPostRequest, id);
    }

    @GetMapping("/client/{id}")
    @ApiOperation(value = "Hiển thị thông tin chi tiết job")
    public JobPostResponse getJobPostById(@PathVariable Long id) {
        return jobPostService.getJobPostById(id);
    }

    @GetMapping("/client/search")
    @ApiOperation(value = "Tìm kiếm job theo tên job, địa điểm, hình thức làm việc, loại hình làm việc, hạn nộp hồ sơ")
    public Page<JobPostResponse> searchJobPosts(@ModelAttribute @Valid SearchJobPostRequest searchJobPostRequest) {
        return jobPostService.search(searchJobPostRequest);
    }

    @PostMapping("/client/apply/{jobId}")
    @ApiOperation(value = "Client ứng tuyển vào job")
    public void applyJob(@PathVariable Long jobId, @Valid @RequestBody ApplicantRequest applicantRequest) {
        applicantRequest.setJobId(jobId);
        applicationService.applyCV(applicantRequest);
    }

    @PostMapping("/admin/change-status/{applicationId}")
    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.ROLE_SUPER_ADMIN})
    @ApiOperation(value = "Admin duyệt hoặc từ chối hồ sơ ứng tuyển")
    public void approveOrRejectApplication(@PathVariable Long applicationId, @Valid @RequestBody ApplicationRequest request) {
        applicationService.changeStatusCVProfile(applicationId, request);
    }

    @GetMapping("/admin/apply/{applicationId}")
    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.ROLE_SUPER_ADMIN})
    @ApiOperation(value = "Admin xem thông tin chi tiết hồ sơ ứng tuyển")
    public ApplicationResponse getApplicationById(@PathVariable Long applicationId) {
        return applicationService.getApplicationById(applicationId);
    }

    @GetMapping("/admin/apply/search")
    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.ROLE_SUPER_ADMIN})
    @ApiOperation(value = "Admin tìm kiếm hồ sơ ứng tuyển")
    public Page<ApplicationResponse> searchApplications(@ModelAttribute @Valid SearchApplicationRequest searchApplicationRequest) {
        return applicationService.search(searchApplicationRequest);
    }
}